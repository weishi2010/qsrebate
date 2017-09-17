package com.rebate.controller;

import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EExtractCode;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.order.RebateDetailService;
import com.rebate.service.product.ProductService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(ShortUrlController.PREFIX)
public class ShortUrlController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ShortUrlController.class);

    public static final String PREFIX = "/qsc";
    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @RequestMapping({"", "/", "/index"})
    public String index(HttpServletRequest request, String d) {
        return "redirect:https://union-click.jd.com/jdc?d=" + d;
    }

    @RequestMapping({"", "/", "/jdShortUrl.json"})
    @ResponseBody
    public ResponseEntity<?> jdPromotionShortUrl(HttpServletRequest request, Long skuId) {
        Map<String, Object> map = new HashMap<String, Object>();

        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        if (null != userInfo) {
            openId = userInfo.getOpenId();
        }

        String url = jdSdkManager.getShortPromotinUrl(skuId, openId);
        map.put("url", RebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(url));

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


}
