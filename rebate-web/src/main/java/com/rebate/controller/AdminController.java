package com.rebate.controller;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.dao.AdvertismentPositionDao;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.*;
import com.rebate.domain.en.*;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.ProductCouponQuery;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.domain.vo.ProductVo;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.activity.ActivityService;
import com.rebate.service.activity.AdvertismentPositionService;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.order.RebateDetailService;
import com.rebate.service.product.ProductService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import com.rebate.service.wx.WxMenuService;
import com.rebate.service.wx.WxService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
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
import java.util.*;

@Controller
@RequestMapping(AdminController.PREFIX)
public class AdminController extends BaseController {
    public static final String PREFIX = "/admin";
    public static final String VIEW_PREFIX = "/rebate";

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);


    private static final TypeReference<List<ProductCoupon>> productCouponTypeReference = new TypeReference<List<ProductCoupon>>() {
    };
    private static final TypeReference<List<Activity>> activityTypeReference = new TypeReference<List<Activity>>() {
    };

    @Autowired
    private ProductCouponDao productCouponDao;

    @Autowired
    private ProductDao productDao;

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;

    @Qualifier("activityService")
    @Autowired(required = true)
    private ActivityService activityService;

    @Qualifier("wxMenuService")
    @Autowired(required = true)
    private WxMenuService wxMenuService;

    @Qualifier("advertismentPositionService")
    @Autowired(required = true)
    private AdvertismentPositionService advertismentPositionService;

    @Qualifier("wxService")
    @Autowired(required = true)
    private WxService wxService;

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @RequestMapping({"", "/", "/importProducts.json"})
    public ResponseEntity<?> importProducts(HttpServletRequest request, String productIds) {
        //导入普通返利商品
        productService.importProducts(productIds);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/importCouponProduct.json"})
    public ResponseEntity<?> importCouponProduct(HttpServletRequest request, String paramJson) {

        List<ProductCoupon> couponMapList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = true;

        try {
            couponMapList = JsonUtil.fromJson(paramJson, productCouponTypeReference);
        } catch (Exception e) {
            LOG.error("paramJson error!", e);
            success = false;
            map.put("msg", "param error!");
        }


        //导入优惠券商品
        try {
            productService.importCouponProducts(couponMapList);
            map.put("success", success);
        } catch (Exception e) {
            map.put("success", false);
            LOG.error("json analysis error!json:" + paramJson, e);
        }
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/importActivity.json"})
    public ResponseEntity<?> importActivity(HttpServletRequest request, String paramJson) {

        List<Activity> activityListList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = true;

        try {
            activityListList = JsonUtil.fromJson(paramJson, activityTypeReference);
        } catch (Exception e) {
            LOG.error("paramJson error!", e);
            success = false;
            map.put("msg", "param error!");
        }


        //导入活动
        activityService.importActivity(activityListList);
        map.put("success", success);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/importMainAdPosition.json"})
    public ResponseEntity<?> importMainAdPosition(String link, String imgUrl, String title) {

        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = true;

        if (StringUtils.isNotBlank(link) && StringUtils.isNotBlank(imgUrl) && StringUtils.isNotBlank(title)) {
            AdvertismentPosition advertismentPosition = new AdvertismentPosition();
            advertismentPosition.setLink(link);
            advertismentPosition.setImgUrl(imgUrl);
            advertismentPosition.setTitle(title);
            advertismentPosition.setStatus(0);
            advertismentPosition.setSortWeight(0);
            advertismentPosition.setPosition(EAdPosition.MAIN.getCode());
            advertismentPositionService.addAdvertismentPosition(advertismentPosition);
        } else {
            success = false;
        }

        map.put("success", success);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/initWxMenu.json"})
    public ResponseEntity<?> initWxMenu() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("delete", wxMenuService.deleteMenu());
        map.put("create", wxMenuService.createMenu());
        map.put("get", wxMenuService.getMenu());
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
    @RequestMapping({"", "/", "/updateCouponPrice.json"})
    public ResponseEntity<?> updateCouponPrice() {
        int page=1;
        int pageSize=20;
        ProductCouponQuery productCouponQuery = new ProductCouponQuery();
        productCouponQuery.setPageSize(pageSize);
        productCouponQuery.setStartRow((page-1)*pageSize);
        List<ProductCoupon> list = productCouponDao.findProductCoupons(productCouponQuery);
        while(list.size()>0){
            for(ProductCoupon coupon:list){
                Product product = new Product();
                product.setProductId(coupon.getProductId());
                product.setCouponPrice(coupon.getCouponPrice());
                productDao.update(product);
            }
            LOG.error("[updateCouponPrice]page:"+page+",list:" + list.size());
            page++;
            productCouponQuery.setStartRow((page-1)*pageSize);
            list = productCouponDao.findProductCoupons(productCouponQuery);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/qrcode"})
    public ModelAndView qrcode(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/agent/qrcode");

        view.addObject("qrcodeUrl", wxService.getQrcodeUrl("agent"));
        return view;
    }

    @RequestMapping({"", "/", "/manager"})
    public ModelAndView manager(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/admin/index");
        return view;
    }

    @RequestMapping({"", "/", "/sysRecommendUser.json"})
    public ResponseEntity<?> sysRecommendUser() {

        userInfoService.sysRecommendUser();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getProducts.json"})
    public ResponseEntity<?> getProducts(HttpServletRequest request,Integer page, Integer pageSize, Integer couponType,String productName,Long productId) {
        Map<String, Object> map = new HashMap<String, Object>();
        ProductQuery query = new ProductQuery();
        if (StringUtils.isNotBlank(productName)) {
            query.setName(EncodeUtils.urlDecode(productName));
        }
        if(null!=productId){
            query.setProductId(productId);
        }
        if(null!=couponType){
            query.setCouponType(couponType);
        }
        if(null==page){
            page = 1;
        }
        if(null==pageSize){
            pageSize = 20;
        }

        query.setIndex(page);
        query.setPageSize(pageSize);
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,null);
        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        map.put("success",true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 批量置顶
     * @param productId
     * @return
     */
    @RequestMapping({"", "/", "/findProduct.json"})
    public ResponseEntity<?> findProduct(Long productId) {

        ProductVo product = productService.findProduct(productId);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("product", product);

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 批量置顶
     * @param productIds
     * @return
     */
    @RequestMapping({"", "/", "/batchResetTop.json"})
    public ResponseEntity<?> batchResetTop(String productIds) {

        Integer sortWeight = 10000;
        ProductQuery productQuery = new ProductQuery();
        productQuery.setSortWeight(sortWeight);
        productQuery.setProductIds(productIds);
        productService.batchResetProductSortWeight(productQuery);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 取消置顶
     * @param productIds
     * @return
     */
    @RequestMapping({"", "/", "/batchResetCancel.json"})
    public ResponseEntity<?> batchResetCancel(String productIds) {

        Integer sortWeight = 0;
        ProductQuery productQuery = new ProductQuery();
        productQuery.setSortWeight(sortWeight);
        productQuery.setProductIds(productIds);
        productService.batchResetProductSortWeight(productQuery);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 批量沉底
     * @param productIds
     * @return
     */
    @RequestMapping({"", "/", "/batchResetBottom.json"})
    public ResponseEntity<?> batchResetBottom(String productIds) {
        Integer sortWeight = -10000;

        ProductQuery productQuery = new ProductQuery();
        productQuery.setSortWeight(sortWeight);
        productQuery.setProductIds(productIds);
        productService.batchResetProductSortWeight(productQuery);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
}
