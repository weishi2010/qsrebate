package com.rebate.controller;

import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.*;
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

/**
 * Description:
 *
 * @author weishi
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2016-08-05 16:46  weishi 新建代码
 * =========================== 维护日志 ===========================
 */
@Controller
@RequestMapping(IndexController.PREFIX)
public class IndexController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    public static final String PREFIX = "/rebate";

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("rebateDetailService")
    @Autowired(required = true)
    private RebateDetailService rebateDetailService;

    @Qualifier("extractDetailService")
    @Autowired(required = true)
    private ExtractDetailService extractDetailService;

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


    /**
     * 用户信息COOKIE
     */
    private static String USERINFO_COOKIE = "u_i_o";

    @RequestMapping({"", "/", "/index"})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView(PREFIX + "/index");

        return view;
    }

    @RequestMapping({"", "/", "/personal/extract"})
    public ModelAndView extract(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(PREFIX + "/extract");
        UserInfo userInfo = getUserInfo(request);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/personal/extractDetail"})
    public ModelAndView extractDetail(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(PREFIX + "/extractDetail");
        UserInfo userInfo = getUserInfo(request);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/personal/getExtractDetails.json"})
    @ResponseBody
    public ResponseEntity<?> getExtractDetails(HttpServletRequest request, int year) {

        ExtractDetailQuery extractDetailQuery = initExtractQuery(request, year);

        //查询列表
        PaginatedArrayList<ExtractDetailVo> result = extractDetailService.findExtractDetailList(extractDetailQuery);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("detailList", result);
        map.put("totalItem", result.getTotalItem());

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);

    }


    @RequestMapping({"", "/", "/personal/extractPrice.json"})
    @ResponseBody
    public ResponseEntity<?> extractPrice(HttpServletRequest request, String extractPhone, Double extractPrice) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfo userInfo = getUserInfo(request);

        EExtractCode code = EExtractCode.SUCCESS;
        if (extractPrice < 0 || extractPrice > 99999) {
            code = EExtractCode.PARAMS_ERROR;
        } else {
            ExtractDetail extractDetail = new ExtractDetail();
            extractDetail.setExtractPrice(extractPrice);
            extractDetail.setOpenId(userInfo.getOpenId());
            extractDetail.setStatus(EExtractStatus.UNCHECK.getCode());
            code = extractDetailService.extract(extractDetail);
        }

        map.put("code", code.getCode());
        map.put("msg", code.getName());

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/personal/index"})
    public ModelAndView personal(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(PREFIX + "/personal");

        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/personal/orderDetail"})
    public ModelAndView orderDetail(HttpServletRequest request, Integer days) {
        ModelAndView view = new ModelAndView(PREFIX + "/orderDetail");
        UserInfo userInfo = getUserInfo(request);

        if (null == days) {
            days = 30;//默认只查30天
        }
        if (days > 180) {
            days = 180;//大于180天则只取180天
        }
        RebateDetailQuery query = new RebateDetailQuery();
        query.setOpenId(userInfo.getOpenId());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        query.setEndDate(cal.getTime());
        PaginatedArrayList<RebateDetailVo> result = rebateDetailService.findRebateDetailList(query);

        view.addObject("detailList", result);
        view.addObject("totalItem", result.getTotalItem());
        view.addObject("userInfo", userInfo);
        view.addObject("days", days);

        return view;
    }

    @RequestMapping({"", "/", "/personal/orders.json"})
    public ResponseEntity<?> orders(HttpServletRequest request, Integer days) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfo userInfo = getUserInfo(request);

        if (null == days) {
            days = 30;//默认只查30天
        }
        if (days > 180) {
            days = 180;//大于180天则只取180天
        }
        RebateDetailQuery query = new RebateDetailQuery();
        query.setSubUnionId(userInfo.getSubUnionId());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        query.setEndDate(cal.getTime());
        PaginatedArrayList<RebateDetailVo> result = rebateDetailService.findRebateDetailList(query);

        map.put("detailList", result);
        map.put("totalItem", result.getTotalItem());
        map.put("userInfo", userInfo);
        map.put("days", days);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/personal/income"})
    public ModelAndView income(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(PREFIX + "/income");
        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
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
        } else if (EPromotionTab.COUPON.getTab() == tab) {
            vm = "/coupon";
        } else if (EPromotionTab.JD.getTab() == tab) {
            vm = "/index";
        }

        ModelAndView view = new ModelAndView(PREFIX + vm);

        if (EPromotionTab.COUPON_PROMOTION.getTab() == tab || EPromotionTab.SECKILL.getTab() == tab) {

            //独家优惠券、9.9秒杀时查询分类列表
            CategoryQuery qategoryQuery = new CategoryQuery();
            qategoryQuery.setPageSize(10);
            view.addObject("topCategories", productService.findByActiveCategories(qategoryQuery));
            qategoryQuery.setPageSize(50);
            view.addObject("allCategories", productService.findByActiveCategories(qategoryQuery));
        }
        view.addObject("promotionTab", tab);
        return view;
    }

    @RequestMapping({"", "/", "/share"})
    public ModelAndView share(HttpServletRequest request, Long skuId) {
        String vm = "/share";
        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        if (null != userInfo) {
            openId = userInfo.getOpenId();
        }

        ModelAndView view = new ModelAndView(PREFIX + vm);
        //查询商品
        ProductVo product = productService.findProduct(skuId, openId);

        product.setPromotionShortUrl(RebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(jdSdkManager.getShortPromotinUrl(skuId, openId)));


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

    @RequestMapping({"", "/", "/products.json"})
    public ResponseEntity<?> products(HttpServletRequest request, Integer tab, Integer page, Integer thirdCategory) {
        Map<String, Object> map = new HashMap<String, Object>();
        Double queryPrice = null;
        Integer couponType = null;
        if (null == tab) {
            tab = EPromotionTab.COUPON_PROMOTION.getTab();
            couponType = EProudctCouponType.COUPON.getCode();
        }
        if (EPromotionTab.SECKILL.getTab() == tab) {
            queryPrice = 9.9;
        }

        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        if (null != userInfo) {
            openId = userInfo.getOpenId();
        }

        ProductQuery query = new ProductQuery();
        query.setIndex(page);
        query.setPageSize(10);
        query.setQueryPrice(queryPrice);
        query.setThirdCategory(thirdCategory);
        query.setCouponType(couponType);
        PaginatedArrayList<ProductVo> products = productService.findProductList(query, openId);
        LOG.error("page:{},size:{}", page, products.size());
        map.put("products", products);
        map.put("page", page);
        map.put("thirdCategory", thirdCategory);
        map.put("totalItem", products.getTotalItem());
        map.put("promotionTab", tab);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/getOrderInfoList"})
    public ResponseEntity<?> getOrderInfoList(@RequestParam(value = "id") final String id) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ok", id);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/importProducts.json"})
    public ResponseEntity<?> importProducts(HttpServletRequest request, String productIds) {
        //导入普通返利商品
        productService.importProducts(productIds, EProudctCouponType.GENERAL.getCode());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/importCouponProduct.json"})
    public ResponseEntity<?> importCouponProduct(HttpServletRequest request, String productIds) {
        //导入优惠券商品
        productService.importProducts(productIds, EProudctCouponType.COUPON.getCode());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
    //---------------------------------------------------------------

    /**
     * 初始化查询条件
     *
     * @param request
     * @param year
     * @return
     */
    private ExtractDetailQuery initExtractQuery(HttpServletRequest request, int year) {
        SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //查询用户信息
        UserInfo userInfo = getUserInfo(request);


        ExtractDetailQuery extractDetailQuery = new ExtractDetailQuery();
        extractDetailQuery.setOpenId(userInfo.getOpenId());

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        //设置查询开始时间
        extractDetailQuery.setBeginDate(calendar.getTime());

        //设计查询结束时间
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        try {
            extractDetailQuery.setEndDate(format.parse(endFormat.format(calendar.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return extractDetailQuery;
    }
}
