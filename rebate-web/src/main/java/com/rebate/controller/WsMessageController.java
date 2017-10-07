package com.rebate.controller;

import com.google.common.base.Joiner;
import com.rebate.common.util.SerializeXmlUtil;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EWxMsgType;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.wx.ImageMessage;
import com.rebate.domain.wx.InputMessage;
import com.rebate.domain.wx.OutputMessage;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.product.ProductService;
import com.rebate.service.userinfo.UserInfoService;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(WsMessageController.PREFIX)
public class WsMessageController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(WsMessageController.class);
    public static final String PREFIX = "/wxmsg";

    @Qualifier("productCouponDao")
    @Autowired(required = true)
    private ProductCouponDao productCouponDao;

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("rebateUrlUtil")
    @Autowired(required = true)
    private RebateUrlUtil rebateUrlUtil;

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxConfig")
    @Autowired(required = true)
    private WxConfig wxConfig;

    @RequestMapping({"", "/", "/acceptMessage.json"})
    public void acceptMessage(HttpServletRequest request, HttpServletResponse response, String echostr, String signature, String timestamp, String nonce) {

        //如果echostr为空说明为消息接收请求，直接进行消息接收处理；如果不为空为签名认证
        if (StringUtils.isBlank(echostr)) {
            accept(request, response);
        } else {
            checkSignature(request, response, echostr, signature, timestamp, nonce);
        }

    }


    /**
     * 签名检查
     *
     * @param request
     * @param response
     * @param echostr
     * @param signature
     * @param timestamp
     * @param nonce
     */
    private void checkSignature(HttpServletRequest request, HttpServletResponse response, String echostr, String signature, String timestamp, String nonce) {
        String token = wxConfig.getMessageCheckToken();
        List<String> list = new ArrayList<>();
        list.add(token);
        list.add(timestamp);
        list.add(nonce);
        Collections.sort(list);     //字典排序
        String str = Joiner.on("").join(list);

        String checkSignature = Sha1Util.getSha1(str);
        LOG.error("[accept]token:" + token + ",timestamp:" + timestamp + ",nonce:" + nonce + ",checkSignature:" + checkSignature + "&signature:" + signature + ",echostr:" + echostr);
        if (StringUtils.isNotBlank(signature) && signature.equalsIgnoreCase(checkSignature)) {
            try {
                response.getWriter().write(echostr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收公众号消息
     *
     * @param request
     * @param response
     */
    private void accept(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 处理接收消息
            InputMessage inputMsg = getInputMessage(request);

            LOG.error("accept[" + inputMsg.getFromUserName() + "] content:" + inputMsg.getContent());
            if (null != inputMsg) {
                // 取得消息类型
                String msgType = inputMsg.getMsgType();
                // 根据消息类型获取对应的消息内容
                if (msgType.equals(EWxMsgType.TEXT.getValue())) {
                    //查询用户信息获取子联盟ID
                    UserInfo userInfo = userInfoService.getUserInfo(inputMsg.getToUserName());
                    String subUnionId = "";
                    if (null != userInfo) {
                        subUnionId = userInfo.getSubUnionId();
                    }

                    //构造消息回复XML
                    StringBuffer str = new StringBuffer();
                    str.append("<xml>");
                    str.append("<ToUserName><![CDATA[" + inputMsg.getFromUserName() + "]]></ToUserName>");
                    str.append("<FromUserName><![CDATA[" + inputMsg.getToUserName() + "]]></FromUserName>");
                    str.append("<CreateTime>" + new Date().getTime() + "</CreateTime>");
                    str.append("<MsgType><![CDATA[" + msgType + "]]></MsgType>");

                    str.append("<Content><![CDATA[" + getRecommendContent(inputMsg.getContent(), subUnionId) + "]]></Content>");
                    str.append("</xml>");

                    LOG.error("output wx message:" + str);


                    response.getWriter().write(str.toString());

                }
            }

        } catch (IOException e) {
            LOG.error("accept wx message error!", e);
        }

    }

    /**
     * 获取商品推荐给用户
     *
     * @param content
     * @param subUnionId
     * @return
     */
    private String getRecommendContent(String content, String subUnionId) {
        StringBuffer recommendContent = new StringBuffer();
        ProductQuery query = new ProductQuery();
        query.setIndex(1);
        query.setPageSize(5);

        List<Long> skus = getSkuListFromMessage(content);
        if (skus.size() > 0) {
            //消息中有SKU信息则按SKU进行搜索
            Long skuId = skus.get(0);
            query.setProductId(skuId);
            List<Product> products =  jdSdkManager.getMediaProducts(skuId.toString());

            if (null != products && products.size()>0) {
                Product product = products.get(0);
                String shortUrl = rebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(jdSdkManager.getShortPromotinUrl(product.getProductId(), subUnionId));
                //商品名
                recommendContent.append("已成功转成把钱链接，从返利链接下单，才可以返钱哦！\n");
                //可返钱
                recommendContent.append("[Packet]可返钱：").append(product.getUserCommission()).append("元\n");
                //推广地址
                recommendContent.append("/:gift返钱链接：").append(shortUrl).append("\n");
            }
        }


        if (StringUtils.isBlank(recommendContent.toString())) {
            recommendContent.append("很抱歉，暂时没有可返钱的商品!");
        }
        return recommendContent.toString();
    }

    /**
     * 获取微信消息推送对象
     *
     * @param request
     * @return
     */
    private InputMessage getInputMessage(HttpServletRequest request) {
        InputMessage inputMessage = null;
        // 处理接收消息
        try {
            ServletInputStream in = request.getInputStream();
            // 将POST流转换为XStream对象
            XStream xs = SerializeXmlUtil.createXstream();
            // 将指定节点下的xml节点数据映射为对象
            xs.alias("xml", InputMessage.class);
            // 将流转换为字符串
            StringBuilder xmlMsg = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                xmlMsg.append(new String(b, 0, n, "UTF-8"));
            }
            inputMessage = (InputMessage) xs.fromXML(xmlMsg.toString());

//            inputMessage = JsonUtil.fromJson(xmlMsg.toString(), InputMessage.class);
        } catch (Exception e) {
            LOG.error("getInputMessage error!", e);
        }
        return inputMessage;
    }

    /**
     * 从消息中解析出SKU
     *
     * @param content
     * @return
     */
    private List<Long> getSkuListFromMessage(String content) {
        List<Long> skus = new ArrayList<>();

        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        String[] skuList = m.replaceAll(",").trim().split(",");
        for (String value : skuList) {
            if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
                skus.add(Long.parseLong(value));
            }
        }
        return skus;
    }

    @RequestMapping({"", "/", "/acceptMulMessage.json"})
    public void acceptMulMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 处理接收消息
        ServletInputStream in = request.getInputStream();
        // 将POST流转换为XStream对象
        XStream xs = SerializeXmlUtil.createXstream();
        // 将指定节点下的xml节点数据映射为对象
        xs.alias("xml", InputMessage.class);
        // 将流转换为字符串
        StringBuilder xmlMsg = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            xmlMsg.append(new String(b, 0, n, "UTF-8"));
        }
        // 将xml内容转换为InputMessage对象
        InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());

        String servername = inputMsg.getToUserName();// 服务端
        String custermname = inputMsg.getFromUserName();// 客户端
        long createTime = inputMsg.getCreateTime();// 接收时间
        Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间

        // 取得消息类型
        String msgType = inputMsg.getMsgType();
        // 根据消息类型获取对应的消息内容
        if (msgType.equals(EWxMsgType.TEXT.getValue())) {
            // 文本消息
            System.out.println("开发者微信号：" + inputMsg.getToUserName());
            System.out.println("发送方帐号：" + inputMsg.getFromUserName());
            System.out.println("消息创建时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));
            System.out.println("消息内容：" + inputMsg.getContent());
            System.out.println("消息Id：" + inputMsg.getMsgId());

            StringBuffer str = new StringBuffer();
            str.append("<xml>");
            str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
            str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
            str.append("<CreateTime>" + returnTime + "</CreateTime>");
            str.append("<MsgType><![CDATA[" + msgType + "]]></MsgType>");
            str.append("<Content><![CDATA[你说的是：" + inputMsg.getContent() + "，吗？]]></Content>");
            str.append("</xml>");
            System.out.println(str.toString());
            response.getWriter().write(str.toString());
        }
        // 获取并返回多图片消息
        if (msgType.equals(EWxMsgType.IMAGE.getValue())) {
            System.out.println("获取多媒体信息");
            System.out.println("多媒体文件id：" + inputMsg.getMediaId());
            System.out.println("图片链接：" + inputMsg.getPicUrl());
            System.out.println("消息id，64位整型：" + inputMsg.getMsgId());

            OutputMessage outputMsg = new OutputMessage();
            outputMsg.setFromUserName(servername);
            outputMsg.setToUserName(custermname);
            outputMsg.setCreateTime(returnTime);
            outputMsg.setMsgType(msgType);
            ImageMessage images = new ImageMessage();
            images.setMediaId(inputMsg.getMediaId());
            outputMsg.setImage(images);
            System.out.println("xml转换：/n" + xs.toXML(outputMsg));
            response.getWriter().write(xs.toXML(outputMsg));

        }
    }
}
