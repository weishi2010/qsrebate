package com.rebate.controller;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.JsonUtil;
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
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
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

    @Qualifier("rebateUrlUtil")
    @Autowired(required = true)
    private RebateUrlUtil rebateUrlUtil;

    @Qualifier("productCouponDao")
    @Autowired(required = true)
    private ProductCouponDao productCouponDao;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

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

        //独家优惠券、9.9秒杀时查询分类列表
        RecommendCategory recommendCategory = new RecommendCategory();
        recommendCategory.setPageSize(10);
        view.addObject("topCategories", productService.findByRecommendCategories(recommendCategory));
        recommendCategory.setPageSize(50);
        view.addObject("allCategories", productService.findByRecommendCategories(recommendCategory));
        view.addObject("promotionTab", EPromotionTab.SECKILL.getTab());
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

        Integer agent = EAgent.NOT_AGENT.getCode();
        if (null != userInfo) {
            agent = userInfo.getAgent();
        }

        //独家优惠券、9.9秒杀时查询分类列表
        RecommendCategory recommendCategory = new RecommendCategory();
        recommendCategory.setPageSize(10);
        view.addObject("topCategories", productService.findByRecommendCategories(recommendCategory));
        recommendCategory.setPageSize(50);
        view.addObject("allCategories", productService.findByRecommendCategories(recommendCategory));
        view.addObject("promotionTab", EPromotionTab.COUPON_PROMOTION.getTab());
        view.addObject("agent",agent);

        return view;
    }

    @RequestMapping({"", "/", "/products.json"})
    public ResponseEntity<?> products(HttpServletRequest request, Integer tab, Integer page, String secondCategoryList) {
        Map<String, Object> map = new HashMap<String, Object>();
        Double queryPrice = null;
        Integer couponType = null;
        if (null != tab && EPromotionTab.SECKILL.getTab() == tab) {
            couponType = EProudctCouponType.GENERAL.getCode();
        } else {
            tab = EPromotionTab.COUPON_PROMOTION.getTab();
            couponType = EProudctCouponType.COUPON.getCode();
        }


        if (EPromotionTab.SECKILL.getTab() == tab) {
            queryPrice = 9.9;
        }

        ProductQuery query = new ProductQuery();
        query.setIndex(page);
        query.setPageSize(10);
        query.setQueryPrice(queryPrice);
        query.setSecondCategoryList(secondCategoryList);
        query.setCouponType(couponType);
        PaginatedArrayList<ProductVo> products = productService.findProductList(query);
        LOG.error("tab:" + tab + ",page:{},size:{}", page, products.size());
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
        map.put("url", rebateUrlUtil.jdPromotionUrlToQsrebateShortUrl(url));
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

        String couponLink = "";
        //查询优惠券信息
        ProductCoupon productCouponQuery = new ProductCoupon();
        productCouponQuery.setProductId(skuId);
        ProductCoupon coupon = productCouponDao.findById(productCouponQuery);
        if (null != coupon) {
            couponLink = coupon.getCouponLink();
        }
        String url = jdSdkManager.getPromotionCouponCode(skuId, couponLink, subUnionId);
        if (StringUtils.isNotBlank(url)) {
            map.put("success", true);
            map.put("url", url);
        } else {
            //清理掉活动过期的商品信息
            Product productUpdate = new Product();
            productUpdate.setProductId(skuId);
            productUpdate.setStatus(EProductStatus.DELETE.getCode());
            productDao.update(productUpdate);

            ProductCoupon productCouponUpdate = new ProductCoupon();
            productCouponUpdate.setProductId(skuId);
            productCouponUpdate.setStatus(EProductStatus.DELETE.getCode());
            productCouponDao.update(productCouponUpdate);

            map.put("success", false);
        }
        LOG.error("getPromotionCouponCode===============>skuId:" + skuId + ",couponLink:" + couponLink + ",url:" + url + ",subUnionId:" + subUnionId);
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

        query.setIndex(page);
        query.setPageSize(10);
        PaginatedArrayList<ProductVo> products = productService.findProductList(query);
        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        map.put("promotionTab", EPromotionTab.COUPON_PROMOTION.getTab());
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

}
