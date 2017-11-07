package com.rebate.controller;

import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.controller.base.BaseController;
import com.rebate.dao.ProductCouponDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EProductFreePost;
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.MessageTempManager;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.product.ProductService;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
@RequestMapping(ShareController.PREFIX)
public class ShareController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ShareController.class);
    public static final String PREFIX = "/share";
    public static final String VIEW_PREFIX = "/rebate";

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxConfig")
    @Autowired(required = true)
    private WxConfig wxConfig;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = true)
    private WxAccessTokenService wxAccessTokenService;

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("wxService")
    @Autowired(required = true)
    private WxService wxService;

    @Qualifier("messageTempManager")
    @Autowired(required = true)
    private MessageTempManager messageTempManager;

    @RequestMapping({"", "/", "/shareIndex"})
    public ModelAndView shareIndex(HttpServletRequest request, Long skuId) {
        String vm = "/share";
        UserInfo userInfo = getUserInfo(request);
        String subUnionId = "";
        if (null != userInfo) {
            subUnionId = userInfo.getSubUnionId();
        }

        ModelAndView view = new ModelAndView(VIEW_PREFIX+ vm);
        //查询商品
        ProductVo product = productService.findProduct(skuId);

        String promotionShortUrl = shortUrlManager.getQsShortPromotinUrl(jdSdkManager.getShortPromotinUrl(skuId, subUnionId),subUnionId);

        //转为微信短链接
        product.setPromotionShortUrl(wxService.getShortUrl(promotionShortUrl));

        //获取临时票据
        String jsapiTicket = wxAccessTokenService.getTicket();
        //生成时间戳
        String timeStamp = Sha1Util.getTimeStamp();
        //生成noncestr
        String noncestr = Sha1Util.getNonceStr();
        //生成签名
        String currentUrl = RequestUtils.getFullUrl(request);
        String str = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + noncestr + "&timestamp=" + timeStamp + "&url=" + currentUrl;
        LOG.error("[share]str:" + str);
        String signature = Sha1Util.getSha1(str);

        LOG.error("[share]signature:" + signature);
        view.addObject("product", product);
        view.addObject("wxConfig", wxConfig);
        view.addObject("timeStamp", timeStamp);
        view.addObject("signature", signature);
        view.addObject("noncestr", noncestr);


        return view;
    }

    @RequestMapping({"", "/", "/sendMessage.json"})
    public ResponseEntity<?> sendMessage(HttpServletRequest request, Long productId) {

        Map<String, Object> map = new HashMap<String, Object>();

        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        String subUnionId = "";
        if (null != userInfo) {
            openId = userInfo.getOpenId();
            subUnionId = userInfo.getSubUnionId();
        }


        ProductVo productVo = productService.findProduct(productId);
        String mediaUrl = "";
        if (null != productVo.getProductCoupon()) {
            String couponLink = productVo.getProductCoupon().getCouponLink();
            mediaUrl = jdSdkManager.getPromotionCouponCode(productId, couponLink, subUnionId);
        }else{
            mediaUrl = jdSdkManager.getShortPromotinUrl(productId,subUnionId);
        }
        String content = "";
        if(StringUtils.isNotBlank(mediaUrl)){
            mediaUrl = shortUrlManager.getWxShortPromotinUrl(mediaUrl,subUnionId);

            //推送消息
             content = messageTempManager.getAgentProductMessageTemp(productVo,mediaUrl);
        }else{
            content ="推广商品已经失效，请更换其他商品!";
        }

       String result =  wxService.sendMessage(openId,content);
        map.put("result", result);
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

}
