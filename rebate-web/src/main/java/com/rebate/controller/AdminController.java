package com.rebate.controller;

import com.rebate.common.util.EncodeUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RequestUtils;
import com.rebate.common.util.Sha1Util;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.util.rebate.RebateUrlUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.dao.AdvertismentPositionDao;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.*;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.*;
import com.rebate.domain.vo.*;
import com.rebate.domain.wx.WxConfig;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.activity.ActivityService;
import com.rebate.service.activity.AdvertismentPositionService;
import com.rebate.service.admin.AdminService;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(AdminController.PREFIX)
public class AdminController extends BaseController {
    public static final String PREFIX = "/admin";
    public static final String VIEW_PREFIX = "/rebate";

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
    private static final TypeReference<List<Product>> productReference = new TypeReference<List<Product>>() {
    };

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

    @Qualifier("adminService")
    @Autowired(required = true)
    private AdminService adminService;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Qualifier("rebateDetailService")
    @Autowired(required = true)
    private RebateDetailService rebateDetailService;

    @Qualifier("extractDetailService")
    @Autowired(required = true)
    private ExtractDetailService extractDetailService;


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

            //导入优惠券商品，
            productService.importCouponProducts(couponMapList,EProudctCouponType.COUPON.getCode());
            map.put("success", success);
        } catch (Exception e) {
            map.put("success", false);
            LOG.error("json analysis error!json:" + paramJson, e);
        }
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/importConvertCouponProduct.json"})
    public ResponseEntity<?> importConvertCouponProduct(HttpServletRequest request, String paramJson) {

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
            productService.importCouponProducts(couponMapList,EProudctCouponType.CONVERT_COUPON.getCode());
            map.put("success", success);
        } catch (Exception e) {
            map.put("success", false);
            LOG.error("json analysis error!json:" + paramJson, e);
        }
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/updateProductExtSortWeight.json"})
    public ResponseEntity<?> updateProductExtSortWeight(HttpServletRequest request, String paramJson) {

        List<Product> productList = null;
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = true;

        try {
            productList = JsonUtil.fromJson(paramJson, productReference);
        } catch (Exception e) {
            LOG.error("paramJson error!", e);
            success = false;
            map.put("msg", "param error!");
        }


        try {
            LOG.error("updateProductExtSortWeight=======>paramJson:paramJson"+paramJson);
            //更新商品扩展排序
            productService.updateProductExtSortWeight(productList);
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
    public ModelAndView manager(HttpServletRequest request,String sui) {
        ModelAndView view = new ModelAndView();
        String subUionId = DESUtil.qsDecrypt(jDProperty.getEncryptKey(),sui,"UTF-8");
        if("admin".equalsIgnoreCase(subUionId)){
            view.setViewName(VIEW_PREFIX+ "/admin/index");
        }else{
            view.setViewName(VIEW_PREFIX + "/permission");
        }
        return view;
    }

    @RequestMapping({"", "/", "/sysRecommendUser.json"})
    public ResponseEntity<?> sysRecommendUser() {

        userInfoService.sysRecommendUser();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/synWxUserInfo.json"})
    public ResponseEntity<?> synWxUserInfo(String openId) {

        userInfoService.synWxUserInfo(openId);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getProducts.json"})
    public ResponseEntity<?> getProducts(HttpServletRequest request,Integer page, Integer pageSize, Integer couponType,String productName,Long productId,Integer thirdCategory,Integer status) {
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
        query.setStatus(status);
        query.setThirdCategory(thirdCategory);
        PaginatedArrayList<ProductVo> products = productService.findProductList(query,null);
        map.put("products", products);
        map.put("page", page);
        map.put("totalItem", products.getTotalItem());
        map.put("success",true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getUserList.json"})
    public ResponseEntity<?> getUserList(HttpServletRequest request,Integer page, Integer pageSize, Integer agent,String nickname,String openId,String subUnionId,String sui,Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfoQuery query = new UserInfoQuery();
        if (StringUtils.isNotBlank(nickname)) {
            query.setNickName(EncodeUtils.urlDecode(nickname));
        }

        if (StringUtils.isNotBlank(sui)) {
            query.setSubUnionId(DESUtil.qsDecrypt(jDProperty.getEncryptKey(), sui, "UTF-8"));
        }

        if(null!=status){
            query.setStatus(status);
        }
        if(StringUtils.isNotBlank(subUnionId)){
            query.setSubUnionId(subUnionId);
        }

        if(StringUtils.isNotBlank(openId)){
            query.setOpenId(openId);
        }

        if(null==page){
            page = 1;
        }
        if(null==pageSize){
            pageSize = 20;
        }

        query.setAgent(agent);
        query.setIndex(page);
        query.setPageSize(pageSize);
        PaginatedArrayList<UserInfoVo> userList = userInfoService.getUserList(query);
        map.put("userList", userList);
        map.put("page", page);
        map.put("totalItem", userList.getTotalItem());
        map.put("success",true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/orders.json"})
    public ResponseEntity<?> orders(HttpServletRequest request,String subUnionId,String startDate,String endDate,Long orderId, Integer page,Integer pageSize,Integer status,Integer validCode) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (null == page || page<=0) {
            page = 1;//默认只查第一页
        }

        if (null == pageSize) {
            pageSize = 10;//默认10条
        }

        RebateDetailQuery query = new RebateDetailQuery();


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


        try {
            if (StringUtils.isNotBlank(startDate)) {
                query.setStartDate(format.parse(startDate));
            }
            if (StringUtils.isNotBlank(endDate)) {
                query.setEndDate(format.parse(endDate));
            }
        } catch (ParseException e) {
            LOG.error("date format error!");
        }

        if (StringUtils.isNotBlank(subUnionId)) {
            query.setSubUnionId(subUnionId);
        }
        query.setIndex(page);
        query.setPageSize(pageSize);
        query.setStatus(status);
        query.setOrderId(orderId);
        query.setValidCode(validCode);
        PaginatedArrayList<RebateDetailVo> result = rebateDetailService.findRebateDetailList(query);

        map.put("detailList", result);
        map.put("totalItem", result.getTotalItem());
        map.put("success",true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 查询提现收入
     * @param request
     * @param page
     * @param pageSize
     * @param openId
     * @param status
     * @return
     */
    @RequestMapping({"", "/", "/getExtractDetails.json"})
    public ResponseEntity<?> getExtractDetails(HttpServletRequest request,Integer page, Integer pageSize,String openId,Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        ExtractDetailQuery query = new ExtractDetailQuery();

        if(null!=status){
            query.setStatus(status);
        }

        if(StringUtils.isNotBlank(openId)){
            query.setOpenId(openId);
        }

        if(null==page||page<=0){
            page = 1;
        }
        if(null==pageSize){
            pageSize = 20;
        }

        query.setIndex(page);
        query.setPageSize(pageSize);
        //查询列表
        PaginatedArrayList<ExtractDetailVo> result =  extractDetailService.findExtractDetailList(query);

        map.put("extractList", result);
        map.put("totalItem", result.getTotalItem());
        map.put("success",true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 更新提现记录状态
     * @param openId
     * @return
     */
    @RequestMapping({"", "/", "/updateExtractDetail.json"})
    public ResponseEntity<?> updateExtractDetail(String openId,Long id) {
        Map<String, Object> map = new HashMap<String, Object>();

        if(StringUtils.isNotBlank(openId) && id!=null){
            ExtractDetail extractDetail = new ExtractDetail();
            extractDetail.setId(id);
            extractDetail.setOpenId(openId);
            extractDetail.setStatus(1);
            extractDetailService.updateExtractDetail(extractDetail);
            map.put("success", true);
        }else{
            map.put("success", false);
        }

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    /**
     * 添加白名单
     * @param subUnionId
     * @return
     */
    @RequestMapping({"", "/", "/addWhiteAgent.json"})
    public ResponseEntity<?> addWhiteAgent(String subUnionId) {
        Map<String, Object> map = new HashMap<String, Object>();

        if(StringUtils.isNotBlank(subUnionId)){
            userInfoService.addWhiteAgent(subUnionId);
            map.put("success", true);
        }else{
            map.put("success", false);
        }

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 取消白名单
     * @param subUnionId
     * @return
     */
    @RequestMapping({"", "/", "/cancelWhiteAgent.json"})
    public ResponseEntity<?> cancelWhiteAgent(String subUnionId) {
        Map<String, Object> map = new HashMap<String, Object>();

        if(StringUtils.isNotBlank(subUnionId)){
            userInfoService.cancelWhiteAgent(subUnionId);
            map.put("success", true);
        }else{
            map.put("success", false);
        }

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    /**
     * 查询商品
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

    /**
     * 商品更新
     * @param productId
     * @param sortWeight
     * @return
     */
    @RequestMapping({"", "/", "/updateProduct.json"})
    public ResponseEntity<?> updateProduct(Long productId,Integer sortWeight) {
        if(null!=productId){

            Product product = new Product();
            product.setProductId(productId);
            product.setSortWeight(sortWeight);
            productService.update(product);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getFirstCategory.json"})
    public ResponseEntity<?> getFirstCategory() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("firstCategoryList",adminService.getFirstCategory() );
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
    @RequestMapping({"", "/", "/getSecondCategory.json"})
    public ResponseEntity<?> getSecondCategory(Integer categoryId) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("secondCategoryList",adminService.getSecondCategory(categoryId) );
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/getThirdCategory.json"})
    public ResponseEntity<?> getThirdCategory(Integer categoryId) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("thirdCategoryList",adminService.getThirdCategory(categoryId) );
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/secondAgents"})
    public ModelAndView secondAgents(HttpServletRequest request,String sui,Integer page) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/agent/secondAgents");
        if (null == page) {
            page = 1;
        }

        String subUnionId = "";
        if(StringUtils.isNotBlank(sui)){
            subUnionId = DESUtil.qsDecrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        }

        AgentRelationQuery agentRelationQuery = new AgentRelationQuery();
        agentRelationQuery.setParentAgentSubUnionId(subUnionId);
        agentRelationQuery.setIndex(page);
        agentRelationQuery.setPageSize(10000);
        PaginatedArrayList<AgentRelationVo> agents = userInfoService.getAgentUserByParentId(agentRelationQuery);
        view.addObject("agents", agents);
        view.addObject("totalItem", agents.getTotalItem());
        return view;
    }

    @RequestMapping({"", "/", "/sonAgentStatistits"})
    public ModelAndView getSonAgentStatistits(HttpServletRequest request,String sui,Integer agent) {
        Random r = new Random(System.currentTimeMillis());
        ModelAndView view = new ModelAndView();
        String vm = VIEW_PREFIX + "/sonAgentStatistits";
        view.setViewName(vm);

        String subUnionId = DESUtil.qsDecrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        if (StringUtils.isBlank(subUnionId)) {
            view.setViewName(VIEW_PREFIX + "/permission");
            return view;
        }


        PaginatedArrayList<OrderSummary>  list = null;
        if(agent == EAgent.FIRST_AGENT.getCode()) {
            list = rebateDetailService.getFirstAgentSonOrderSummary(subUnionId);
        }
        view.addObject("list", list);
        view.addObject("r", r.nextInt());
        return view;
    }

    @RequestMapping({"", "/", "/agentStatistits"})
    public ModelAndView agentStatistits(HttpServletRequest request,String sui,Integer dayTab,Integer days) {
        ModelAndView view = new ModelAndView();
        String vm = VIEW_PREFIX + "/adminStatistits";
        view.setViewName(vm);

        String subUnionId = DESUtil.qsDecrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        if (StringUtils.isBlank(subUnionId)) {
            view.setViewName(VIEW_PREFIX + "/permission");
            return view;
        }

        Calendar calendar = Calendar.getInstance();
        Date queryDate = new Date();
        if (null != dayTab && 2 == dayTab) {
            calendar.add(Calendar.DATE, -1);
            queryDate = calendar.getTime();
        }else{
            dayTab = 1;
        }

        if (null == days || days <= 0) {
            days = 60;
        }


        OrderSummary orderSummaryQuery = new OrderSummary();
        orderSummaryQuery.setSubUnionId(subUnionId);
        orderSummaryQuery.setPageSize(days);//取近60
        PaginatedArrayList<OrderSummary>  list =  rebateDetailService.getOrderSummaryBySubUnionId(orderSummaryQuery);
        OrderSummary todayOrderSummary = null;
        SimpleDateFormat formatStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat formatEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

        for(OrderSummary orderSummary:list){
            if (formatStart.format(orderSummary.getSubmitDate()).equals(formatStart.format(queryDate))) {
                todayOrderSummary = orderSummary;
                break;
            }
        }

        if(null== todayOrderSummary){
            todayOrderSummary = new OrderSummary();
            todayOrderSummary.setCommission(0.0);
            todayOrderSummary.setOrderCount(0l);
        }

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = formatStart.parse(formatStart.format(queryDate));

            calendar.setTime(queryDate);
            calendar.add(Calendar.DATE, 1);
            endDate = formatEnd.parse(formatEnd.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        LOG.error("[agentStatistits]dayTab:"+dayTab+",size:"+list.size());
        view.addObject("dayTab", dayTab);
        view.addObject("adminFlag", false);
        view.addObject("todayClick",shortUrlManager.getJDUnionUrlClick(subUnionId,queryDate));
        view.addObject("todayOrderSummary", todayOrderSummary);
        view.addObject("list", list);
        view.addObject("sui",sui);
        view.addObject("subUnionId",subUnionId);

        return view;
    }
}
