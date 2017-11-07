package com.rebate.controller;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.RecommendCategory;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EAgent;
import com.rebate.domain.en.EProductStatus;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.en.EProudctCouponType;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ApiProductVo;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.product.ProductService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.util.JSONPObject;
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

/**
 * 推广
 */
@Controller
@RequestMapping(PromotionController.PREFIX)
public class PromotionController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(PromotionController.class);
    public static final String PREFIX = "/promotion";
    public static final String VIEW_PREFIX = "/rebate";

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("titleMap")
    @Autowired(required = true)
    private HashMap titleMap = new HashMap();

    @RequestMapping({"", "/", "/productList"})
    public ModelAndView productList(HttpServletRequest request,String sui,Integer tab) {
        String vm = "/product/thirdPartyProductList";

        if (null == tab ) {
            tab = EPromotionTab.COUPON_PROMOTION.getTab();
        }

        ModelAndView view = new ModelAndView(VIEW_PREFIX + vm);

        String subUnionId = DESUtil.decrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        if (null == subUnionId) {
            subUnionId = "";
        }

        //通过子联盟ID+tab获取标题
        String title = "京东内部优惠";
        if(titleMap.containsKey(subUnionId+"_"+tab)){
            title =  titleMap.get(subUnionId+"_"+tab).toString();

        }


        view.addObject("sui",sui);//子联盟id加密后的串
        RecommendCategory recommendCategory = new RecommendCategory();
        recommendCategory.setPageSize(10);
        view.addObject("topCategories", productService.findByRecommendCategories(recommendCategory));
        recommendCategory.setPageSize(50);
        view.addObject("allCategories", productService.findByRecommendCategories(recommendCategory));
        view.addObject("promotionTab", tab);
        view.addObject("promotionTitle", title);

        return view;
    }

    @RequestMapping({"", "/", "/jdShortUrl.json"})
    @ResponseBody
    public ResponseEntity<?> jdShortUrl(HttpServletRequest request,String sui, Long skuId) {
        Map<String, Object> map = new HashMap<String, Object>();

        String subUnionId = DESUtil.decrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        if(null==subUnionId){
            subUnionId ="";
        }

        String url = jdSdkManager.getShortPromotinUrl(skuId, subUnionId);
        map.put("url", shortUrlManager.getQsShortPromotinUrl(url,subUnionId));
        LOG.error("jdPromotionShortUrl===============>url:" + url + ",subUnionId:" + subUnionId);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getPromotionCouponCode.json"})
    @ResponseBody
    public ResponseEntity<?> getPromotionCouponCode(HttpServletRequest request,String sui, Long skuId) {
        Map<String, Object> map = new HashMap<String, Object>();
        String subUnionId = "";
        try{
            subUnionId = DESUtil.decrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        }catch (Exception e){
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

    @RequestMapping({"", "/", "/products.json"})
    public ResponseEntity<?> products(HttpServletRequest request, Integer tab, Integer page, String secondCategoryList) {
        Map<String, Object> map = new HashMap<String, Object>();
        Integer couponType = null;
        if (null == tab ) {
            tab = EPromotionTab.COUPON_PROMOTION.getTab();
        }

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
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,null);
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
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,null);
        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        map.put("promotionTab", EPromotionTab.COUPON_PROMOTION.getTab());
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

}
