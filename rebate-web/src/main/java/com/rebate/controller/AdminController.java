package com.rebate.controller;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EExtractCode;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.en.EProudctCouponType;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
@RequestMapping(AdminController.PREFIX)
public class AdminController extends BaseController {
    public static final String PREFIX = "/admin";
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);


    private static final TypeReference<List<ProductCoupon>> productCouponTypeReference = new TypeReference<List<ProductCoupon>>() {
    };

    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;


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

        try{
            couponMapList = JsonUtil.fromJson(paramJson,productCouponTypeReference);
        }catch (Exception e){
            LOG.error("paramJson error!",e);
            success = false;
            map.put("msg", "param error!");
        }


        //导入优惠券商品
        productService.importCouponProducts(couponMapList);
        map.put("success", success);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
}
