package com.rebate.controller;

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

    @RequestMapping({"", "/", "/sendMessage.json"})
    public ResponseEntity<?> sendMessage(String openIds,String content) {

        Map<String, Object> map = new HashMap<String, Object>();

        String[] array = openIds.split(",");
        List<String> openIdList = new ArrayList<>();
        for(String openId:array){
            openIdList.add(openId);
        }

        map.put("result", wxService.sendMessage(openIdList,content));
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
}
