package com.rebate.controller;

import com.google.common.base.Joiner;
import com.rebate.common.util.*;
import com.rebate.common.util.wx.MessageUtil;
import com.rebate.controller.base.BaseController;
import com.rebate.dao.ProductDao;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Product;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.wx.InputMessage;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.output.*;
import com.rebate.manager.MessageTempManager;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.product.ProductService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxService;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(WsMessageController.PREFIX)
public class WsMessageController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(WsMessageController.class);
    public static final String PREFIX = "/wxmsg";

    @Qualifier("threadPoolTaskExecutor")
    @Autowired(required = true)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("messageTempManager")
    @Autowired(required = true)
    private MessageTempManager messageTempManager;

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxConfig")
    @Autowired(required = true)
    private WxConfig wxConfig;

    @Qualifier("wxService")
    @Autowired(required = true)
    private WxService wxService;

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;


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
        // 处理接收消息
        InputMessage inputMsg = getInputMessage(request);

        if (null != inputMsg) {
            LOG.error("accept[" + inputMsg.getFromUserName() + "],msgType:" + inputMsg.getMsgType());
            //查询用户信息获取子联盟ID
            UserInfo userInfo = userInfoService.getUserInfo(inputMsg.getFromUserName());
            String subUnionId = "";
            int agent = EAgent.GENERAL_REBATE_USER.getCode();
            if (null != userInfo) {
                subUnionId = userInfo.getSubUnionId();
                agent = userInfo.getAgent();
            }
            // 取得消息类型
            String msgType = inputMsg.getMsgType();
            // 根据消息类型获取对应的消息内容
            if (msgType.equals(EWxMsgType.TEXT.getValue())) {
                //文本事件
                textEvent(agent, inputMsg, subUnionId, response);

            } else if (msgType.equals(EWxMsgType.EVENT.getValue())) {
                String openId = inputMsg.getFromUserName();
                LOG.error("accept[" + openId + "],msgType:" + inputMsg.getMsgType() + ",event:" + inputMsg.getEvent());
                if (EWxEventType.SUBSCRIBE.getValue().equalsIgnoreCase(inputMsg.getEvent())) {
                    //订阅处理
                    subscribteEvent(openId, inputMsg, subUnionId, response);
                } else if (EWxEventType.CLICK.getValue().equalsIgnoreCase(inputMsg.getEvent())) {
                    //点击事件
                    clickEvent(openId, inputMsg, subUnionId, response);
                } else if (EWxEventType.SCAN.getValue().equalsIgnoreCase(inputMsg.getEvent())) {
                    //扫描处理
                    scanEvent(openId, inputMsg, subUnionId, response);
                }
            }
        }
    }

    /**
     * 文本事件
     *
     * @param agent
     * @param inputMsg
     * @param subUnionId
     * @param response
     */
    private void textEvent(int agent, InputMessage inputMsg, String subUnionId, HttpServletResponse response) {
        //默认为普通用户获取文本推送商品推荐xml

        //如果为代理用户则解析消息进行推广转链处理
        if (EAgent.FIRST_AGENT.getCode() == agent || EAgent.SECOND_AGENT.getCode() == agent) {
            agentMessageAnalyzer(response, inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), subUnionId);
        } else {
            rebateUserMessageAnalyzer(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), subUnionId);
        }

    }

    /**
     * 订阅事件
     *
     * @param openId
     * @param inputMsg
     * @param subUnionId
     * @param response
     */
    private void clickEvent(String openId, InputMessage inputMsg, String subUnionId, HttpServletResponse response) {
        //点击
        String eventXml = "";
        if (EWxEventCode.QS_WX_CLICK001.getValue().equalsIgnoreCase(inputMsg.getEventKey())) {
            eventXml = articlePushXml(inputMsg.getFromUserName(), inputMsg.getToUserName());
        }
        LOG.error("output wx eventXml:" + eventXml);
        try {
            response.getWriter().write(eventXml.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅事件
     *
     * @param openId
     * @param inputMsg
     * @param subUnionId
     * @param response
     */
    private void subscribteEvent(String openId, InputMessage inputMsg, String subUnionId, HttpServletResponse response) {
        //查询是否已存在此用户
        UserInfo userInfoQuery = new UserInfo();
        userInfoQuery.setOpenId(openId);
        UserInfo userInfo = userInfoDao.findLoginUserInfo(userInfoQuery);


        String eventKey = inputMsg.getEventKey();
        Integer agentType = null;
        String agentOpenId = null;
        if (StringUtils.isNotBlank(eventKey) && eventKey.contains(EWxEventCode.QRCODE_SUBSCRIBE.getValue())) {
            String paramJson = eventKey.replace(EWxEventCode.QRCODE_SUBSCRIBE.getValue(), "");//前缀去掉

            if (StringUtils.isNotBlank(paramJson)) {
                //如果扫码带数据则进行代理参数解析
                String[] paramArray = paramJson.split("#");
                agentOpenId = paramArray[0];
                agentType = Integer.parseInt(paramArray[1]);
            }
        }

        String eventXml = getPushXml(openId, agentOpenId, userInfo, agentType, inputMsg, subUnionId);

        try {
            response.getWriter().write(eventXml.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPushXml(String openId, String agentOpenId, UserInfo userInfo, Integer codeType, InputMessage inputMsg, String subUnionId) {
        String eventXml = "";
        if (null == userInfo) {
            if (null != codeType) {
                //代理用户扫码，带参数
                if (EQrCodeType.REGIST_FIRST_AGENT_QR_CODE.getCode() == codeType) {
                    //代理模式1-注册代理用户
                    userInfo = userInfoService.registUserInfo(openId, "", EAgent.FIRST_AGENT.getCode(), true);
                    //代理用户关注消息
                    eventXml = agentTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), userInfo.getSubUnionId());

                } else if (EQrCodeType.REGIST_SECOND_AGENT_QR_CODE.getCode() == codeType) {
                    //代理模式2-注册代理用户
                    userInfo = userInfoService.registUserInfo(openId, "", EAgent.SECOND_AGENT.getCode(), true);
                    //代理用户关注消息
                    eventXml = agentTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), userInfo.getSubUnionId());

                } else if (EQrCodeType.REGIST_SECOND_AGENT_REBATE_USER_QR_CODE.getCode() == codeType) {
                    //代理模式2-拉粉丝返利用户
                    userInfoService.registUserInfo(openId, agentOpenId, EAgent.GENERAL_REBATE_USER.getCode(), true);
                    eventXml = subscribeTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), subUnionId);

                }

            } else {
                //普通用户扫码，不带参数
                //注册普通返利用户
                userInfoService.registUserInfo(openId, "", EAgent.GENERAL_REBATE_USER.getCode(), true);

                //普通用户首次关注消息
                eventXml = subscribeTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), subUnionId);

            }

        } else {

            if (null != codeType) {
                //代理用户扫码，带参数
                if (EQrCodeType.REGIST_FIRST_AGENT_QR_CODE.getCode() == codeType) {
                    //代理模式1-注册代理用户
                    userInfoService.updateUserInfoAgent(openId, "", EAgent.FIRST_AGENT.getCode());

                    //代理用户关注消息
                    eventXml = agentTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), userInfo.getSubUnionId());

                } else if (EQrCodeType.REGIST_SECOND_AGENT_QR_CODE.getCode() == codeType) {
                    //代理模式2-注册代理用户
                    userInfoService.updateUserInfoAgent(openId, "", EAgent.SECOND_AGENT.getCode());
                    //代理用户关注消息
                    eventXml = agentTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), userInfo.getSubUnionId());
                } else if (EQrCodeType.REGIST_SECOND_AGENT_REBATE_USER_QR_CODE.getCode() == codeType) {
                    //代理模式2-拉粉丝返利用户
                    if (StringUtils.isBlank(userInfo.getRecommendAccount())) {
                        userInfoService.updateUserInfoAgent(openId, agentOpenId, EAgent.GENERAL_REBATE_USER.getCode());
                    }
                    eventXml = subscribeTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), subUnionId);

                }

            } else {
                //普通用户扫码，不带参数
                //普通用户首次关注消息
                eventXml = subscribeTextPushXml(inputMsg.getFromUserName(), inputMsg.getToUserName(), inputMsg.getMsgType(), inputMsg.getContent(), subUnionId);

            }
        }

        return eventXml;
    }

    /**
     * 扫描事件
     *
     * @param openId
     * @param inputMsg
     * @param subUnionId
     * @param response
     */
    private void scanEvent(String openId, InputMessage inputMsg, String subUnionId, HttpServletResponse response) {
        //查询是否已存在此用户
        UserInfo userInfoQuery = new UserInfo();
        userInfoQuery.setOpenId(openId);
        UserInfo userInfo = userInfoDao.findLoginUserInfo(userInfoQuery);

        String eventKey = inputMsg.getEventKey();
        Integer agentType = null;
        String agentOpenId = null;
        if (StringUtils.isNotBlank(eventKey)) {
            String paramJson = eventKey.replace(EWxEventCode.QRCODE_SUBSCRIBE.getValue(), "");//前缀去掉

            if (StringUtils.isNotBlank(paramJson)) {
                //如果扫码带数据则进行代理参数解析
                String[] paramArray = paramJson.split("#");
                if (paramArray.length > 0) {
                    agentType = Integer.parseInt(paramArray[paramArray.length - 1]);
                    agentOpenId = paramArray[paramArray.length - 2];
                }
            }
        }

        String eventXml = getPushXml(openId, agentOpenId, userInfo, agentType, inputMsg, subUnionId);


        LOG.error("output wx eventXml:" + eventXml);
        try {
            response.getWriter().write(eventXml.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关注后回复消息
     *
     * @param toUserName
     * @param fromUserName
     * @param msgType
     * @param content
     * @param subUnionId
     * @return
     */
    private String firstSubscribeTextPushXml(String toUserName, String fromUserName, String msgType, String content, String subUnionId) {

        //构造消息回复XML
        StringBuffer str = new StringBuffer();
        str.append("<xml>");
        str.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
        str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
        str.append("<CreateTime>" + new Date().getTime() + "</CreateTime>");
        str.append("<MsgType><![CDATA[text]]></MsgType>");
        str.append("<Content><![CDATA[欢迎加入我们,10元提现奖励稍候会发放到您的账户，满20元可提现！购物返钱，还有更多独家优惠券哦！ \n\n" +
                "1、输入京东商品编号 或 商品链接，立刻获取返钱链接！\n" +
                "2、打开【京东返钱-内购券】，每日更新独家的！超值的！内购优惠券！]]></Content>");
        str.append("</xml>");
        return str.toString();
    }

    /**
     * 关注后回复消息
     *
     * @param toUserName
     * @param fromUserName
     * @param msgType
     * @param content
     * @param subUnionId
     * @return
     */
    private String subscribeTextPushXml(String toUserName, String fromUserName, String msgType, String content, String subUnionId) {

        //构造消息回复XML
        StringBuffer str = new StringBuffer();
        str.append("<xml>");
        str.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
        str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
        str.append("<CreateTime>" + new Date().getTime() + "</CreateTime>");
        str.append("<MsgType><![CDATA[text]]></MsgType>");
        str.append("<Content><![CDATA[\uD83C\uDF89欢迎加入“轻松返钱”，购物能返钱！还有您的专属内购券哦！\n" +
                "\n" +
                "【返钱步骤】\n" +
                "   1、输入京东商品编号 或 商品链接，立刻获取返钱链接！ \n" +
                "   2、通过返钱链接下单，获得返钱！\n" +
                "\n" +
                "【 内购券 】\n" +
                "打开菜单栏，《内购券》每日爆出更新！独家！专享！超值！     [阴险][阴险]直接访问官网没有的哦！\n" +
                "\n" +
                " 【提现】\uD83C\uDE35 20元，就可以提现啦！\n" +
                "\uD83D\uDD25京东11.11优惠券抢购中，链接 https://w.url.cn/s/A6mete6\n" +
                "\n]]></Content>");
        str.append("</xml>");
        return str.toString();
    }

    private String agentTextPushXml(String toUserName, String fromUserName, String msgType, String content, String subUnionId) {

        //构造消息回复XML
        StringBuffer str = new StringBuffer();
        str.append("<xml>");
        str.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
        str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
        str.append("<CreateTime>" + new Date().getTime() + "</CreateTime>");
        str.append("<MsgType><![CDATA[text]]></MsgType>");
        str.append("<Content><![CDATA[\uD83C\uDF89欢迎加入“轻松返钱网”，一起轻松赚钱[红包]！\n" +
                "    尊敬的代理用户，您可以进行如下推广操作：\n" +
                "\n" +
                "  1、通过【内购券】和9.9元频道推广我们为您精选的优质优惠券商品！\n" +
                "  2、通过对话框，输入“外部复制回来的发单信息”转链后转发推广出去！\n" +
                "  3、通过输入框，输入“京东促销活动链接”进行转链，推广京东活动页面。\n" +
                " 如：https://sale.jd.com/m/act/1Kr7yhjnkxi.html?jd_pop=3daac57c-7f96-44a2-9e0a-f2c1eb12aee1&abt=1 和 https://pro.m.jd.com/mall/active/3ccrpMyatkBAmk7C9TQQNpuL1Zwu/index.html 等链接。]]></Content>");
        str.append("</xml>");
        return str.toString();
    }

    private String articlePushXml(String toUserName, String fromUserName) {

        //构造消息回复XML
        StringBuffer str = new StringBuffer();
        str.append("<xml>");
        str.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
        str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
        str.append("<CreateTime>" + new Date().getTime() + "</CreateTime>");
        str.append("<MsgType><![CDATA[text]]></MsgType>");
        str.append("<Content><![CDATA[\uD83D\uDCB0\uD83D\uDCB0【免费发放京东全品类优惠券】\n" +
                "\n" +
                "\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25京东全品类15元优惠券\n" +
                "https://w.url.cn/s/A6R1o5T\n" +
                "\n" +
                "\uD83D\uDD25\uD83D\uDD25京东全品类10元优惠券\n" +
                "https://w.url.cn/s/AiuOBlu\n" +
                "\n" +
                "\uD83D\uDD25京东全品类5元优惠券，数量有限速度抢https://w.url.cn/s/AaIXaV0\n]]></Content>");
        str.append("</xml>");
        return str.toString();
    }

    private String articlePushXmlOld(String toUserName, String fromUserName) {

        //对图文消息
        NewsMessage newmsg = new NewsMessage();
        newmsg.setToUserName(toUserName);
        newmsg.setFromUserName(fromUserName);
        newmsg.setCreateTime(new Date().getTime());
        newmsg.setMsgType(EWxMsgType.NEWS.getValue());

        Article article = new Article();
        article.setTitle("轻松返钱网，怎么能返钱（返利）？");  //图文消息标题
        article.setDescription("加入“轻松返钱网”，怎么轻松拿返钱？请往下看..."); //图文消息的描述
        article.setPicUrl("http://m.qingsongfan.com.cn/static/img/article_1.jpg"); //图文消息图片地址
        article.setUrl("https://mp.weixin.qq.com/s/CAIExEX3oxYyKtOEgq0SCg");  //图文url链接
        List<Article> list = new ArrayList<Article>();
        list.add(article);     //这里发送的是单图文，如果需要发送多图文则在这里list中加入多个Article即可！
        newmsg.setArticleCount(list.size());
        newmsg.setArticles(list);
        String xml = MessageUtil.newsMessageToXml(newmsg);

//        //构造消息回复XML
//        StringBuffer str = new StringBuffer();
//        str.append("<xml>");
//        str.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
//        str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
//        str.append("<CreateTime>" + new Date().getTime() + "</CreateTime>");
//        str.append("<MsgType><![CDATA[news]]></MsgType>");
//        str.append("<ArticleCount>1</ArticleCount>");
//        str.append("<Articles>" +
//                "<item>" +
//                "<Title><![CDATA[轻松返钱网，怎么能返钱（返利）？]]></Title> " +
//                "<Description><![CDATA[加入“轻松返钱网”，怎么轻松拿返钱？请往下看...]]></Description>" +
//                "<PicUrl><![CDATA[http://m.qingsongfan.com.cn/static/img/article_1.jpg]]></PicUrl>" +
//                "<Url><![CDATA[https://mp.weixin.qq.com/s/CAIExEX3oxYyKtOEgq0SCg]]></Url>" +
//                "</item>" +
//                "</Articles>");
//
//        str.append("</xml>");
//        return str.toString();
        return xml;
    }

    /**
     * 推荐商品消息回复
     *
     * @param toUserName
     * @param fromUserName
     * @param msgType
     * @param content
     * @param subUnionId
     * @return
     */
    private void rebateUserMessageAnalyzer(String toUserName, String fromUserName, String msgType, String content, String subUnionId) {
        StringBuffer recommendContent = new StringBuffer();
        String imgUrl = "";
        List<Long> skus = RegexUtils.getLongList(content);
        if (skus.size() > 0) {
            //消息中有SKU信息则按SKU进行搜索
            Long skuId = skus.get(0);

            //查询用户信息
            UserInfo userInfoQuery = new UserInfo();
            userInfoQuery.setSubUnionId(subUnionId);
            UserInfo userInfo = userInfoDao.findUserInfoBySubUnionId(userInfoQuery);

            //查询商品基础信息
            Product product = productService.findProductBaseInfo(skuId);
            boolean isRebate = false;
            Double commission = 0.0;

            String mediaUrl = "";
            if (null != product && EProudctCouponType.COUPON.getCode() == product.getCouponType()) {
                commission = jdSdkManager.getQSCommission(userInfo, product);
                //如果是内购券商品则进行内购券二全一转链
                ProductVo productVo = productService.findProduct(skuId);
                isRebate = false;
                imgUrl = productVo.getImgUrl();
                String couponLink = productVo.getProductCoupon().getCouponLink();
                mediaUrl = jdSdkManager.getPromotionCouponCode(skuId, couponLink, subUnionId);
            } else {
                //如果不是内购券商品则从联盟获取商品信息并获取图片
                List<Product> products = jdSdkManager.getMediaProducts(skuId.toString());

                if (null != products && products.size() > 0) {
                    Product mediaProduct = products.get(0);
                    imgUrl = mediaProduct.getImgUrl();
                    commission =jdSdkManager.getQSCommission(userInfo, mediaProduct);
                }
                //转链
                mediaUrl = jdSdkManager.getShortPromotinUrl(skuId, subUnionId);
                isRebate = true;
            }

            if(StringUtils.isNotBlank(mediaUrl)){
                String shortUrl = shortUrlManager.getWxShortPromotinUrl(mediaUrl, subUnionId);

                //获取返利用户消息模板
                recommendContent.append(messageTempManager.getRebateUserProductMessageTemp(isRebate, commission, shortUrl));
            }

        }

        if (StringUtils.isBlank(recommendContent.toString())) {
            recommendContent.append("很抱歉，暂时没有可返钱的商品!");
        }

        if (StringUtils.isNotBlank(imgUrl)) {
            String mediaId = wxService.getWxImageMediaId(imgUrl);
            //发送图片消息
            wxService.sendImageMessage(toUserName, mediaId);
        }
        //发送文本消息
        wxService.sendMessage(toUserName, recommendContent.toString());
//        return textPushXml(toUserName, fromUserName, msgType, recommendContent.toString(), subUnionId);
    }

    /**
     * 代理消息回复
     *
     * @param toUserName
     * @param fromUserName
     * @param msgType
     * @param content
     * @param subUnionId
     * @return
     */
    private void agentMessageAnalyzer(HttpServletResponse response, String toUserName, String fromUserName, String msgType, String content, String subUnionId) {
        String pushContent = "";
        String jdSaleDomains = jDProperty.getSaleDomains();
        //识别是否为活动推广转链接
        boolean isSaleConvert = false;
        if (StringUtils.isNotBlank(jdSaleDomains)) {
            String[] doaminsArray = jdSaleDomains.split(",");
            for (String domain : doaminsArray) {
                if (content.toLowerCase().contains(domain.toLowerCase())) {
                    isSaleConvert = true;
                }
            }
        }
        if (RegexUtils.checkURL(content) || StringUtils.isNumeric(content)) {
            //如果只是商品url或sku则进行商品转链接返回，通过api方式进行消息发送
            sendAgentProductMessage(response, toUserName, fromUserName, msgType, content, subUnionId);
        } else if (isSaleConvert) {
            //解析活动消息进行活动链接转推广链接，通过消息回置方式进行发送
            sendSalesMessage(content, toUserName, subUnionId);
        } else {
            //解析优惠券消息进行券二合一推广链接转换
            pushContent = couponMessageConvertJDMediaUrl(content, subUnionId);
            //发送文本消息
            wxService.sendMessage(toUserName, pushContent);
        }

    }

    /**
     * 文本消息回复
     *
     * @param toUserName
     * @param fromUserName
     * @param msgType
     * @param content
     * @param subUnionId
     * @return
     */
    private String textPushXml(String toUserName, String fromUserName, String msgType, String content, String subUnionId) {
        //构造消息回复XML
        String xml = "";
        TextMessage txtmsg = new TextMessage();
        txtmsg.setToUserName(toUserName);
        txtmsg.setFromUserName(fromUserName);
        txtmsg.setCreateTime(new Date().getTime());
        txtmsg.setMsgType(EWxMsgType.TEXT.getValue());
        txtmsg.setContent(content);
        xml = MessageUtil.textMessageToXml(txtmsg);

        return xml;
    }

    /**
     * 获取商品推荐给代理用户
     *
     * @param content
     * @param subUnionId
     * @return
     */
    private void sendAgentProductMessage(HttpServletResponse response, String toUserName, String fromUserName, String msgType, String content, String subUnionId) {
        StringBuffer recommendContent = new StringBuffer();
        String imgUrl = "";
        List<Long> skus = RegexUtils.getLongList(content);
        if (skus.size() > 0) {
            //消息中有SKU信息则按SKU进行搜索
            Long skuId = skus.get(0);
            List<Product> products = jdSdkManager.getMediaProducts(skuId.toString());

            if (null != products && products.size() > 0) {

                Product product = products.get(0);

                //查询用户信息
                UserInfo userInfoQuery = new UserInfo();
                userInfoQuery.setSubUnionId(subUnionId);
                UserInfo userInfo = userInfoDao.findUserInfoBySubUnionId(userInfoQuery);


                LOG.error("getAgentRecommendContent product:" + JsonUtil.toJson(product));
                String shortUrl = jdSdkManager.getShortPromotinUrl(product.getProductId(), subUnionId);
                shortUrl = shortUrlManager.getWxShortPromotinUrl(shortUrl, subUnionId);

                product.setUserCommission(jdSdkManager.getQSCommission(userInfo, product));
                //获取代理户消息模板
                recommendContent.append(messageTempManager.getAgentProductMessageTemp(product, shortUrl));

                imgUrl = product.getImgUrl();
            }
        } else {
            if (RegexUtils.checkURL(content)) {
                //转换为推广链接
                String jdMediaUrl = jdSdkManager.getSalesActivityPromotinUrl(content, subUnionId);
                jdMediaUrl = shortUrlManager.getWxShortPromotinUrl(jdMediaUrl, subUnionId);
                recommendContent.append(jdMediaUrl);
            }
        }

        if (StringUtils.isBlank(recommendContent.toString())) {
            recommendContent.append("很抱歉，暂时没有可推广的商品!");
        }

        if (StringUtils.isNotBlank(imgUrl)) {
            String mediaId = wxService.getWxImageMediaId(imgUrl);
            //发送图片消息
            wxService.sendImageMessage(toUserName, mediaId);
        }
        //发送文本消息
        wxService.sendMessage(toUserName, recommendContent.toString());
    }


    private void sendSalesMessage(String content, String toUserName, String subUnionId) {
        String result = "很抱歉，活动链接转换失败，请联系公众号管理员!";
        try {
            boolean hasSku = false;
            List<Long> skus = new ArrayList<>();
            //包括sku的则只转sku为推广链接
            List<Long> dataList = RegexUtils.getLongList(content);
            for (Long data : dataList) {
                //如果大于5位大数据则转为商品推广链接
                if (data.toString().length() > 5) {
                    skus.add(data);
                    hasSku = true;
                    String url = jdSdkManager.getShortPromotinUrl(data, subUnionId);
                    content = content.replace("" + data, url);
                }
            }

            //不包含sku的则按活动转链处理
            if (!hasSku) {
                List<String> list = RegexUtils.getLinks(content);
                List<LinkTask> tasks = new ArrayList<>();
                for (String link : list) {
                    tasks.add(new LinkTask(link, subUnionId));
                }

                //通过并发获取
                List<Future<Map>> futures = threadPoolTaskExecutor.getThreadPoolExecutor().invokeAll(tasks, 5000, TimeUnit.MILLISECONDS);

                for (int i = 0; i < futures.size(); i++) {
                    Map map = futures.get(i).get();
                    if (null != map) {
                        Iterator it = map.keySet().iterator();
                        while (it.hasNext()) {
                            String key = it.next().toString();
                            content = content.replace(key, map.get(key).toString());
                        }
                    }

                }

            } else {
                List<Product> products = jdSdkManager.getMediaProducts(Joiner.on(",").join(skus));

                for (Product product : products) {
                    if (StringUtils.isNotBlank(product.getImgUrl())) {
                        String mediaId = wxService.getWxImageMediaId(product.getImgUrl());
                        //发送图片消息
                        wxService.sendImageMessage(toUserName, mediaId);
                    }
                }
            }

            //发送文本消息
            wxService.sendMessage(toUserName, content);
        } catch (Exception e) {
            LOG.error("salesMessageConvertJDMediaUrl", e);
        }
    }

    private class LinkTask implements Callable<Map> {
        private String url;
        private String subUnionId;

        public LinkTask(String url, String subUnionId) {
            this.url = url;
            this.subUnionId = subUnionId;
        }

        @Override
        public Map call() throws Exception {
            //JD活动多重跳转解析
            String convertJDPromotionUrl = HttpClientUtil.convertJDPromotionUrl(url);
            //转换为推广链接
            String jdMediaUrl = jdSdkManager.getSalesActivityPromotinUrl(convertJDPromotionUrl, subUnionId);
            jdMediaUrl = shortUrlManager.getWxShortPromotinUrl(jdMediaUrl, subUnionId);

            Map map = new HashMap();
            map.put(url, jdMediaUrl);
            return map;
        }
    }

    /**
     * 优惠券消息解析并转链
     *
     * @param content
     * @return
     */
    private String couponMessageConvertJDMediaUrl(String content, String subUnionId) {
        String result = "很抱歉，优惠券链接转换失败，请联系公众号管理员!";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
            String line = "";
            StringBuffer sb = new StringBuffer();
            String couponLink = "";
            Long skuId = 0l;
            String linkMark = "#couponLink#";
            boolean stop = false;
            while ((line = br.readLine()) != null) {

                List<String> list = RegexUtils.getLinks(line);
                if (list.size() > 0) {
                    String url = list.get(0);
                    if (url.contains("coupon.m.jd.com") || url.contains("coupon.jd.com")) {
                        couponLink = url;
                        line = linkMark;//链接占位符
                    } else if (url.contains("item.jd.com")) {
                        List<Long> dataList = RegexUtils.getLongList(url);
                        if (null != dataList && dataList.size() > 0) {
                            skuId = dataList.get(0);
                            line = "";
                        }
                        //解析完商品链接后停止解析后边的内容
                        stop = true;
                    }
                }

                if (StringUtils.isNotBlank(line)) {
                    sb.append(line).append("\n");
                }
                //停止解析
                if (stop) {
                    break;
                }
            }

            if (StringUtils.isNotBlank(couponLink) && skuId > 0l) {
                sb.append("-----------\n");
                sb.append("京东商城  正品保证\n");

                String jdMediaUrl = jdSdkManager.getPromotionCouponCode(skuId, couponLink, subUnionId);
                jdMediaUrl = shortUrlManager.getQsShortPromotinUrl(jdMediaUrl, subUnionId);

                if (StringUtils.isNotBlank(jdMediaUrl)) {
                    result = sb.toString().replace(linkMark, jdMediaUrl);
                } else {
                    result = "优惠券链接已经失效!";
                }
                LOG.error("couponMessageConvertJDMediaUrl sb:{}jdMediaUrl:{}", sb.toString(), jdMediaUrl);

            } else {
                List<Long> list = RegexUtils.getLongList(content);
                for (Long data : list) {
                    //如果大于5位大数据则转为商品推广链接
                    if (data.toString().length() > 5) {
                        String url = jdSdkManager.getShortPromotinUrl(data, subUnionId);
                        content = content.replace("" + data, url);
                    }
                }
                result = content;
            }
            LOG.error("couponMessageConvertJDMediaUrl couponLink:{}skuId:{}", couponLink, skuId);

        } catch (Exception e) {
            LOG.error("couponMessageConvertJDMediaUrl", e);
        }
        return result;
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
            LOG.error("xmlMsg:" + xmlMsg);
            inputMessage = (InputMessage) xs.fromXML(xmlMsg.toString());

//            inputMessage = JsonUtil.fromJson(xmlMsg.toString(), InputMessage.class);
        } catch (Exception e) {
            LOG.error("getInputMessage error!", e);
        }
        return inputMessage;
    }

//    @RequestMapping({"", "/", "/acceptMulMessage.json"})
//    public void acceptMulMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // 处理接收消息
//        ServletInputStream in = request.getInputStream();
//        // 将POST流转换为XStream对象
//        XStream xs = SerializeXmlUtil.createXstream();
//        // 将指定节点下的xml节点数据映射为对象
//        xs.alias("xml", InputMessage.class);
//        // 将流转换为字符串
//        StringBuilder xmlMsg = new StringBuilder();
//        byte[] b = new byte[4096];
//        for (int n; (n = in.read(b)) != -1; ) {
//            xmlMsg.append(new String(b, 0, n, "UTF-8"));
//        }
//        // 将xml内容转换为InputMessage对象
//        InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
//
//        String servername = inputMsg.getToUserName();// 服务端
//        String custermname = inputMsg.getFromUserName();// 客户端
//        long createTime = inputMsg.getCreateTime();// 接收时间
//        Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间
//
//        // 取得消息类型
//        String msgType = inputMsg.getMsgType();
//        // 根据消息类型获取对应的消息内容
//        if (msgType.equals(EWxMsgType.TEXT.getValue())) {
//            // 文本消息
//            System.out.println("开发者微信号：" + inputMsg.getToUserName());
//            System.out.println("发送方帐号：" + inputMsg.getFromUserName());
//            System.out.println("消息创建时间：" + inputMsg.getCreateTime() + new Date(createTime * 1000l));
//            System.out.println("消息内容：" + inputMsg.getContent());
//            System.out.println("消息Id：" + inputMsg.getMsgId());
//
//            StringBuffer str = new StringBuffer();
//            str.append("<xml>");
//            str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
//            str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
//            str.append("<CreateTime>" + returnTime + "</CreateTime>");
//            str.append("<MsgType><![CDATA[" + msgType + "]]></MsgType>");
//            str.append("<Content><![CDATA[你说的是：" + inputMsg.getContent() + "，吗？]]></Content>");
//            str.append("</xml>");
//            System.out.println(str.toString());
//            response.getWriter().write(str.toString());
//        }
//        // 获取并返回多图片消息
//        if (msgType.equals(EWxMsgType.IMAGE.getValue())) {
//            System.out.println("获取多媒体信息");
//            System.out.println("多媒体文件id：" + inputMsg.getMediaId());
//            System.out.println("图片链接：" + inputMsg.getPicUrl());
//            System.out.println("消息id，64位整型：" + inputMsg.getMsgId());
//
//            OutputMessage outputMsg = new OutputMessage();
//            outputMsg.setFromUserName(servername);
//            outputMsg.setToUserName(custermname);
//            outputMsg.setCreateTime(returnTime);
//            outputMsg.setMsgType(msgType);
//            ImageMessage images = new ImageMessage();
//            images.setMediaId(inputMsg.getMediaId());
//            outputMsg.setImage(images);
//            System.out.println("xml转换：/n" + xs.toXML(outputMsg));
//            response.getWriter().write(xs.toXML(outputMsg));
//
//        }
//    }
}
