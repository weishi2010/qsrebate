package com.rebate.controller;

import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.UserInfo;
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.product.ProductService;
import com.rebate.service.wx.WxAccessTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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

    @Qualifier("rebateUrlUtil")
    @Autowired(required = true)
    private RebateUrlUtil rebateUrlUtil;

    @RequestMapping({"", "/", "/shareIndex"})
    public ModelAndView shareIndex(HttpServletRequest request, Long skuId) {
        String vm = "/share";
        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        if (null != userInfo) {
            openId = userInfo.getOpenId();
        }

        ModelAndView view = new ModelAndView(VIEW_PREFIX+ vm);
        //查询商品
        ProductVo product = productService.findProduct(skuId, openId);

        product.setPromotionShortUrl(rebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(jdSdkManager.getShortPromotinUrl(skuId, openId)));


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
}
