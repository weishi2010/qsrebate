package com.rebate.controller;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.RecommendCategory;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EAgent;
import com.rebate.domain.en.EProductStatus;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.en.EProudctCouponType;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.product.ProductService;
import net.sf.json.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(ProductController.PREFIX)
public class ProductController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    public static final String PREFIX = "/product";
    public static final String VIEW_PREFIX = "/rebate";

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("productCouponDao")
    @Autowired(required = true)
    private ProductCouponDao productCouponDao;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;
    /**
     * 京东首页
     *
     * @param request
     * @param tab
     * @return
     */
    @RequestMapping({"", "/", "/jdIndex"})
    public ModelAndView jdIndex(HttpServletRequest request, Integer tab) {
        String vm = "/index";

        ModelAndView view = new ModelAndView(VIEW_PREFIX + vm);
        view.addObject("promotionTab", EPromotionTab.JD.getTab());
        return view;
    }

    /**
     * 秒杀
     *
     * @param request
     * @param tab
     * @return
     */
    @RequestMapping({"", "/", "/seckill"})
    public ModelAndView promotion(HttpServletRequest request, Integer tab) {
        String vm = "/product/secSkillProductList";

        ModelAndView view = new ModelAndView(VIEW_PREFIX + vm);

        UserInfo userInfo = getUserInfo(request);

        Integer agent = EAgent.GENERAL_REBATE_USER.getCode();
        if (null != userInfo) {
            agent = userInfo.getAgent();
        }

        //独家优惠券、9.9秒杀时查询分类列表
        RecommendCategory recommendCategory = new RecommendCategory();
        recommendCategory.setPageSize(10);
        view.addObject("topCategories", productService.findByRecommendCategories(recommendCategory));
        recommendCategory.setPageSize(50);
        view.addObject("allCategories", productService.findByRecommendCategories(recommendCategory));
        view.addObject("promotionTab", EPromotionTab.SECKILL.getTab());
        view.addObject("agent",agent);

        return view;
    }

    /**
     * 专享优惠券商品
     *
     * @param request
     * @param tab
     * @return
     */
    @RequestMapping({"", "/", "/couponProducts"})
    public ModelAndView couponProducts(HttpServletRequest request, Integer tab) {
        String vm = "/product/couponProductList";

        ModelAndView view = new ModelAndView(VIEW_PREFIX + vm);

        UserInfo userInfo = getUserInfo(request);

        Integer agent = EAgent.GENERAL_REBATE_USER.getCode();
        String sui = "";
        if (null != userInfo) {
            agent = userInfo.getAgent();
            sui = DESUtil.qsEncrypt(jDProperty.getEncryptKey(), userInfo.getSubUnionId(), "UTF-8");
        }

        //独家优惠券、9.9秒杀时查询分类列表
        RecommendCategory recommendCategory = new RecommendCategory();
        recommendCategory.setPageSize(10);
        view.addObject("topCategories", productService.findByRecommendCategories(recommendCategory));
        recommendCategory.setPageSize(50);
        view.addObject("allCategories", productService.findByRecommendCategories(recommendCategory));
        view.addObject("promotionTab", EPromotionTab.COUPON_PROMOTION.getTab());
        view.addObject("agent",agent);
        view.addObject("sui",sui);//子联盟id加密后的串
        return view;
    }

    @RequestMapping({"", "/", "/products.json"})
    public ResponseEntity<?> products(HttpServletRequest request, Integer tab, Integer page, String secondCategoryList) {
        Map<String, Object> map = new HashMap<String, Object>();
        Integer couponType = null;
        if (null == tab ) {
            tab = EPromotionTab.COUPON_PROMOTION.getTab();
        }

        UserInfo userInfo = getUserInfo(request);

        couponType = EProudctCouponType.COUPON.getCode();
        ProductQuery query = new ProductQuery();

        if (EPromotionTab.SECKILL.getTab() == tab) {
            query.setLetPrice(10.0);
        }else{
            query.setGtPrice(10.0);
        }

        query.setIndex(page);
        query.setPageSize(10);
        query.setSecondCategoryList(secondCategoryList);
        query.setCouponType(couponType);
        query.setStatus(EProductStatus.PASS.getCode());
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,userInfo);
        if(products.size()>0){

            LOG.error("tab:" + tab + ",page:{},first:{}", page, JsonUtil.toJson(products.get(0)));
        }
        map.put("products", products);
        map.put("page", page);
        map.put("secondCategoryList", secondCategoryList);
        map.put("totalItem", products.getTotalItem());
        map.put("promotionTab", tab);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/jumpJdUnionUrl"})
    public String jumpJdUnionUrl(HttpServletRequest request, String d) {
        return "redirect:https://union-click.jd.com/jdc?d=" + d;
    }

    @RequestMapping({"", "/", "/jdShortUrl.json"})
    @ResponseBody
    public ResponseEntity<?> jdPromotionShortUrl(HttpServletRequest request, Long skuId) {
        Map<String, Object> map = new HashMap<String, Object>();

        UserInfo userInfo = getUserInfo(request);
        String subUnionId = "";
        if (null != userInfo) {
            subUnionId = userInfo.getSubUnionId();
        }

        String url = jdSdkManager.getShortPromotinUrl(skuId, subUnionId);
        map.put("url", shortUrlManager.getQsShortPromotinUrl(url,subUnionId));
        LOG.error("jdPromotionShortUrl===============>url:" + url + ",subUnionId:" + subUnionId);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getPromotionCouponCode.json"})
    @ResponseBody
    public ResponseEntity<?> getPromotionCouponCode(HttpServletRequest request, Long skuId) {
        Map<String, Object> map = new HashMap<String, Object>();

        UserInfo userInfo = getUserInfo(request);
        String subUnionId = "";
        if (null != userInfo) {
            subUnionId = userInfo.getSubUnionId();
        }

        String url = productService.getCouponPromotionUrl(skuId, subUnionId);
        if (StringUtils.isNotBlank(url)) {
            map.put("success", true);
            map.put("url", url);
        } else {
            map.put("success", false);
        }
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/searchProducts.json"})
    public ResponseEntity<?> searchProducts(HttpServletRequest request, Integer page, String params) {
        Map<String, Object> map = new HashMap<String, Object>();
        ProductQuery query = new ProductQuery();
        if (StringUtils.isNumeric(params)) {
            query.setProductId(Long.parseLong(params));
        } else {
            params = EncodeUtils.urlDecode(EncodeUtils.urlDecode(params));
            query.setName(params);
        }

        UserInfo userInfo = getUserInfo(request);

        query.setIndex(page);
        query.setPageSize(10);
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,userInfo);
        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        map.put("promotionTab", EPromotionTab.COUPON_PROMOTION.getTab());
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

}
