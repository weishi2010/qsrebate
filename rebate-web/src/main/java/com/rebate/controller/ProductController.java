package com.rebate.controller;

import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.en.EProudctCouponType;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.product.ProductService;
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

        ModelAndView view = new ModelAndView(VIEW_PREFIX+ vm);
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
        String vm = "/product/couponProductList";

        ModelAndView view = new ModelAndView(VIEW_PREFIX+ vm);

        //独家优惠券、9.9秒杀时查询分类列表
        CategoryQuery qategoryQuery = new CategoryQuery();
        qategoryQuery.setPageSize(10);
        view.addObject("topCategories", productService.findByActiveCategories(qategoryQuery));
        qategoryQuery.setPageSize(50);
        view.addObject("allCategories", productService.findByActiveCategories(qategoryQuery));
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

        ModelAndView view = new ModelAndView(VIEW_PREFIX+ vm);

        //独家优惠券、9.9秒杀时查询分类列表
        CategoryQuery qategoryQuery = new CategoryQuery();
        qategoryQuery.setPageSize(10);
        view.addObject("topCategories", productService.findByActiveCategories(qategoryQuery));
        qategoryQuery.setPageSize(50);
        view.addObject("allCategories", productService.findByActiveCategories(qategoryQuery));
        view.addObject("promotionTab", EPromotionTab.COUPON_PROMOTION.getTab());
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
        LOG.error("jdPromotionShortUrl===============>url:"+url+",subUnionId:"+subUnionId);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

}
