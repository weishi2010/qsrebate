//package com.rebate.controller;
//
//import com.rebate.common.util.rebate.RebateUrlUtil;
//import com.rebate.controller.base.BaseController;
//import com.rebate.domain.UserInfo;
//import com.rebate.manager.jd.JdSdkManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//@RequestMapping(ShortUrlController.PREFIX)
//public class ShortUrlController extends BaseController {
//    private static final Logger LOG = LoggerFactory.getLogger(ShortUrlController.class);
//
//    public static final String PREFIX = "/qsc";
//    @Qualifier("jdSdkManager")
//    @Autowired(required = true)
//    private JdSdkManager jdSdkManager;
//
//    @RequestMapping({"", "/", "/index"})
//    public String index(HttpServletRequest request, String d) {
//        return "redirect:https://union-click.jd.com/jdc?d=" + d;
//    }
//
//    @RequestMapping({"", "/", "/jdShortUrl.json"})
//    @ResponseBody
//    public ResponseEntity<?> jdPromotionShortUrl(HttpServletRequest request, Long skuId) {
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        UserInfo userInfo = getUserInfo(request);
//        String subUnionId = "";
//        if (null != userInfo) {
//            subUnionId = userInfo.getSubUnionId();
//        }
//
//        String url = jdSdkManager.getShortPromotinUrl(skuId, subUnionId);
//        map.put("url", rebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(url));
//        LOG.error("jdPromotionShortUrl===============>url:"+url+",subUnionId:"+subUnionId);
//        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
//    }
//
//
//}
