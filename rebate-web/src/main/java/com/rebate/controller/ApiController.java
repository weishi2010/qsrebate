package com.rebate.controller;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.ActivityQuery;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.service.activity.ActivityService;
import com.rebate.service.activity.AdvertismentPositionService;
import com.rebate.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(ApiController.PREFIX)
public class ApiController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);
    public static final String PREFIX = "/api";
    @Qualifier("productService")
    @Autowired(required = true)
    private ProductService productService;


    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @RequestMapping({"", "/", "/productList.json"})
    public ResponseEntity<?> productList(HttpServletRequest request, Integer page,Integer pageSize) {
        if(null==page){
            page = 1;
        }
        if(null==pageSize){
            pageSize = 10;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        Integer couponType = EProudctCouponType.COUPON.getCode();
        ProductQuery query = new ProductQuery();


        String subUnionId = jDProperty.getApiSubUnionId();
        int agent = EAgent.FIRST_AGENT.getCode();

        query.setIndex(page);
        query.setPageSize(pageSize);
        query.setCouponType(couponType);
        query.setStatus(EProductStatus.PASS.getCode());
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,agent,subUnionId);
        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

}
