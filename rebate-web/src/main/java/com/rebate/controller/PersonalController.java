package com.rebate.controller;

import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.inteceptor.QsLoginInteceptor;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.OrderSummary;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EAgent;
import com.rebate.domain.en.EExtractCode;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.OrderSummaryQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.query.UserInfoQuery;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.order.RebateDetailService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxService;
import net.sf.json.JSONObject;
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
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(PersonalController.PREFIX)
public class PersonalController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(PersonalController.class);

    public static final String PREFIX = "/personal";

    public static final String VIEW_PREFIX = "/rebate";

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @Qualifier("rebateDetailService")
    @Autowired(required = true)
    private RebateDetailService rebateDetailService;

    @Qualifier("extractDetailService")
    @Autowired(required = true)
    private ExtractDetailService extractDetailService;

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Qualifier("cookieUtils")
    @Autowired(required = true)
    public CookieUtils cookieUtils;

    @Qualifier("wxService")
    @Autowired(required = true)
    private WxService wxService;


    @RequestMapping({"", "/", "/extract"})
    public ModelAndView extract(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/extract");
        UserInfo userInfo = getUserInfo(request);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/extractDetail"})
    public ModelAndView extractDetail(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/extractDetail");
        UserInfo userInfo = getUserInfo(request);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/getExtractDetails.json"})
    @ResponseBody
    public ResponseEntity<?> getExtractDetails(HttpServletRequest request, int year) {

        ExtractDetailQuery extractDetailQuery = initExtractQuery(request, year);

        //查询列表
        PaginatedArrayList<ExtractDetailVo> result = extractDetailService.findExtractDetailList(extractDetailQuery);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("detailList", result);
        map.put("totalItem", result.getTotalItem());

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);

    }


    @RequestMapping({"", "/", "/extractPrice.json"})
    @ResponseBody
    public ResponseEntity<?> extractPrice(HttpServletRequest request, String extractPhone, Double extractPrice) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfo userInfo = getUserInfo(request);

        EExtractCode code = EExtractCode.SUCCESS;
        if (extractPrice < 0 || extractPrice > 99999) {
            code = EExtractCode.PARAMS_ERROR;
        } else {
            ExtractDetail extractDetail = new ExtractDetail();
            extractDetail.setExtractPrice(extractPrice);
            extractDetail.setOpenId(userInfo.getOpenId());
            extractDetail.setStatus(EExtractStatus.UNCHECK.getCode());
            code = extractDetailService.extract(extractDetail);
        }

        map.put("code", code.getCode());
        map.put("msg", code.getName());

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/index"})
    public ModelAndView personal(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/personal");

        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));
        view.addObject("promotionTab", EPromotionTab.PERSONAL.getTab());

        return view;
    }

    @RequestMapping({"", "/", "/orderDetail"})
    public ModelAndView orderDetail(HttpServletRequest request, Integer days) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/orderDetail");
        UserInfo userInfo = getUserInfo(request);

        if (null == days) {
            days = 30;//默认只查30天
        }
        if (days > 180) {
            days = 180;//大于180天则只取180天
        }
        RebateDetailQuery query = new RebateDetailQuery();
        query.setOpenId(userInfo.getSubUnionId());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        query.setStartDate(cal.getTime());
        PaginatedArrayList<RebateDetailVo> result = rebateDetailService.findRebateDetailList(query);

        view.addObject("detailList", result);
        view.addObject("totalItem", result.getTotalItem());
        view.addObject("userInfo", userInfo);
        view.addObject("days", days);

        return view;
    }

    @RequestMapping({"", "/", "/orders.json"})
    public ResponseEntity<?> orders(HttpServletRequest request, Integer page, Integer days) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfo userInfo = getUserInfo(request);

        if (null == days) {
            days = 30;//默认只查30天
        }
        if (days > 180) {
            days = 180;//大于180天则只取180天
        }
        RebateDetailQuery query = new RebateDetailQuery();
        query.setSubUnionId(userInfo.getSubUnionId());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        query.setStartDate(cal.getTime());
        query.setIndex(page);
        PaginatedArrayList<RebateDetailVo> result = rebateDetailService.findRebateDetailList(query);
        LOG.error("page:{},size:{}", page, result.size());

        map.put("detailList", result);
        map.put("totalItem", result.getTotalItem());
        map.put("userInfo", userInfo);
        map.put("days", days);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/income"})
    public ModelAndView income(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/income");
        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
        return view;
    }

    @RequestMapping({"", "/", "/agentStatistits"})
    public ModelAndView agentStatistits(HttpServletRequest request,Integer dayTab) {
        ModelAndView view = new ModelAndView();
        String vm = VIEW_PREFIX + "/agentStatistits";
        view.setViewName(vm);
        UserInfo userInfo = getUserInfo(request);
        if (null == userInfo || userInfo.getAgent() == EAgent.NOT_AGENT.getCode()) {
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

        boolean isAdmin = jDProperty.isAdmin(userInfo.getSubUnionId());

        OrderSummary orderSummaryQuery = new OrderSummary();
        orderSummaryQuery.setSubUnionId(userInfo.getSubUnionId());
        orderSummaryQuery.setPageSize(30);//取近30
        PaginatedArrayList<OrderSummary>  list =  rebateDetailService.getOrderSummaryBySubUnionId(orderSummaryQuery);
        OrderSummary todayOrderSummary = null;
        SimpleDateFormat formatStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat formatEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

        for(OrderSummary orderSummary:list){
            try {
                if(orderSummary.getSubmitDate().equals(formatStart.parse(formatStart.format(queryDate)))){
                    todayOrderSummary = orderSummary;
                }
            } catch (ParseException e) {
                e.printStackTrace();
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
        if(isAdmin){

            OrderSummaryQuery orderSummaryQuery2 = new OrderSummaryQuery();
            orderSummaryQuery2.setStartDate(startDate);
            orderSummaryQuery2.setEndDate(endDate);
            OrderSummary allOrderSummary = rebateDetailService.getAllOrderSummaryByDate(orderSummaryQuery2);
            if (null != allOrderSummary) {
                allOrderSummary.setClickCount(shortUrlManager.getALLJDUnionUrlClick(queryDate));
                view.addObject("allOrderSummary", allOrderSummary);
            }
        }

        if (userInfo.getAgent() == EAgent.SECOND_AGENT.getCode()) {
            UserInfoQuery userInfoQuery = new UserInfoQuery();
            userInfoQuery.setOpenId(userInfo.getOpenId());
            userInfoQuery.setStartDate(startDate);
            userInfoQuery.setEndDate(endDate);
            view.addObject("recommendUserCount", userInfoService.findRecommendUserCount(userInfoQuery));
        }

        LOG.error("[agentStatistits]dayTab:"+dayTab+",size:"+list.size());
        view.addObject("dayTab", dayTab);
        view.addObject("adminFlag", isAdmin);
        view.addObject("todayClick",shortUrlManager.getJDUnionUrlClick(userInfo.getSubUnionId(),queryDate));
        view.addObject("todayOrderSummary", todayOrderSummary);
        view.addObject("list", list);
        view.addObject("userInfo", userInfo);
        return view;
    }

    @RequestMapping({"", "/", "/logout"})
    public ResponseEntity<?> logout(HttpServletResponse response) {
        cookieUtils.deleteQsCookie(response, QsLoginInteceptor.USERINFO_COOKIE);
        cookieUtils.deleteQsCookie(response, QsLoginInteceptor.WX_ACCESSTOKEN_COOKIE);

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("success", true);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/firstAgentCode"})
    public ModelAndView firstAgentCode(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/agent/qrcode");

        UserInfo userInfo = getUserInfo(request);
        String openId = null;
        if(null!=userInfo){
            openId = userInfo.getOpenId();
        }

        String sceneStr = openId+"#"+EAgent.FIRST_AGENT.getCode();
        view.addObject("qrcodeUrl", wxService.getQrcodeUrl(sceneStr));
        return view;
    }

    @RequestMapping({"", "/", "/secondAgentCode"})
    public ModelAndView secondAgentCode(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/agent/qrcode");

        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        if(null!=userInfo){
            openId = userInfo.getOpenId();
        }

        String sceneStr = openId+"#"+EAgent.SECOND_AGENT.getCode();
        view.addObject("qrcodeUrl", wxService.getQrcodeUrl(sceneStr));
        return view;
    }

    @RequestMapping({"", "/", "/recommendUserCode"})
    public ModelAndView recommendUserCode(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/agent/qrcode");

        UserInfo userInfo = getUserInfo(request);
        String openId = "";
        if(null!=userInfo){
            openId = userInfo.getOpenId();
        }

        String sceneStr = openId+"#"+EAgent.GENERAL_REBATE_USER.getCode();
        view.addObject("qrcodeUrl", wxService.getQrcodeUrl(sceneStr));
        return view;
    }
    //---------------------------------------------------------------

    /**
     * 初始化查询条件
     *
     * @param request
     * @param year
     * @return
     */
    private ExtractDetailQuery initExtractQuery(HttpServletRequest request, int year) {
        SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //查询用户信息
        UserInfo userInfo = getUserInfo(request);


        ExtractDetailQuery extractDetailQuery = new ExtractDetailQuery();
        extractDetailQuery.setOpenId(userInfo.getOpenId());

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        //设置查询开始时间
        extractDetailQuery.setBeginDate(calendar.getTime());

        //设计查询结束时间
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        try {
            extractDetailQuery.setEndDate(format.parse(endFormat.format(calendar.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return extractDetailQuery;
    }
}
