package com.rebate.controller;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ApiProductVo;
import com.rebate.service.product.ProductService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping({"", "/", "/daxue/productList.json"})
    @ResponseBody
    public JSONPObject productList(HttpServletRequest request,String callback,String productName,Integer tab, Integer page,Integer pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();

        ProductQuery query = new ProductQuery();
        if(null==page){
            page = 1;
        }
        if(null==pageSize || pageSize>200){
            pageSize = 10;
        }

        if (StringUtils.isNotBlank(productName)) {
            query.setName(EncodeUtils.urlDecode(EncodeUtils.urlDecode(productName)));
        }

        query.setPageSize(pageSize);
        query.setIndex(page);

        if (EPromotionTab.SECKILL.getTab() == tab) {
            query.setLetPrice(10.0);
        }else{
            query.setGtPrice(10.0);
        }

        PaginatedArrayList<ApiProductVo> products = productService.findDaxueProductList(query);

        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        map.put("success", true);
        return  new JSONPObject(callback, map);
    }


    private boolean validateToken(String token,String subUnionId){
        try {
            if(token.equalsIgnoreCase(EncodeUtils.md5Encode(subUnionId))){
                    return true;
            }
        } catch (Exception e) {
           LOG.error("validateToken error!token:"+token+",subUnionId:"+subUnionId,e);
        }
        return false;
    }
}
