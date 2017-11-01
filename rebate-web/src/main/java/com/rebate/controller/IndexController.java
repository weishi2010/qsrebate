package com.rebate.controller;

import com.rebate.controller.base.BaseController;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.RecommendCategory;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.order.RebateDetailService;
import com.rebate.service.product.ProductService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(IndexController.PREFIX)
public class IndexController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    public static final String PREFIX = "/rebate";

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;



    @RequestMapping({"", "/", "/index"})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView(PREFIX + "/index");
        String jdWlDomain = "https://m.jd.com";
        UserInfo userInfo = getUserInfo(request);
        String subUnionId = "";
        if (null != userInfo) {
            subUnionId = userInfo.getSubUnionId();
        }

        String promotionUrl = jdSdkManager.getLongPromotinUrl(jdWlDomain, subUnionId);
        if(StringUtils.isBlank(promotionUrl)){
            promotionUrl = jdWlDomain;
        }
        view.addObject("promotionUrl",promotionUrl);
        return view;
    }

    /**
     * 每日必买
     *
     * @param request
     * @param tab
     * @return
     */
    @RequestMapping({"", "/", "/promotion"})
    public ModelAndView promotion(HttpServletRequest request, Integer tab) {
        String vm = "/promotion/promotionList";
        if (null == tab) {
            tab = EPromotionTab.COUPON_PROMOTION.getTab();//默认为"独家优惠券"
        } else if (EPromotionTab.SECKILL.getTab() == tab) {
            vm = "/promotion/promotionList";
        } else if (EPromotionTab.ACTIVITY.getTab() == tab) {
            vm = "/coupon";
        } else if (EPromotionTab.JD.getTab() == tab) {
            vm = "/index";
        }

        ModelAndView view = new ModelAndView(PREFIX + vm);

        if (EPromotionTab.COUPON_PROMOTION.getTab() == tab || EPromotionTab.SECKILL.getTab() == tab) {

            //独家优惠券、9.9秒杀时查询分类列表
            RecommendCategory recommendCategory = new RecommendCategory();
            recommendCategory.setPageSize(10);
            view.addObject("topCategories", productService.findByRecommendCategories(recommendCategory));
            recommendCategory.setPageSize(50);
            view.addObject("allCategories", productService.findByRecommendCategories(recommendCategory));
        }
        view.addObject("promotionTab", tab);
        return view;
    }

    @RequestMapping({"", "/", "/getOrderInfoList"})
    public ResponseEntity<?> getOrderInfoList(@RequestParam(value = "id") final String id) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ok", id);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    //---------------------------------------------------------------
}
