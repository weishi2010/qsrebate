package com.rebate.controller;

import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.des.DESUtil;
import com.rebate.common.web.inteceptor.QsLoginInteceptor;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.OrderSummary;
import com.rebate.domain.UserInfo;
import com.rebate.domain.agent.AgentRelation;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.*;
import com.rebate.domain.vo.AgentRelationVo;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.order.RebateDetailService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxService;
import net.sf.json.JSONObject;
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
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        PaginatedArrayList<ExtractDetailVo> result = null;
        if (StringUtils.isNotBlank(extractDetailQuery.getOpenId())) {
            result = extractDetailService.findExtractDetailList(extractDetailQuery);
        }

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
        view.addObject("extractCommission", userInfoService.getUserExtractCommission(userInfo.getOpenId()));
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
//        RebateDetailQuery query = new RebateDetailQuery();
//        query.setOpenId(userInfo.getSubUnionId());
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -days);
//        query.setStartDate(cal.getTime());
//        PaginatedArrayList<RebateDetailVo> result = rebateDetailService.findRebateDetailList(query);
//
//        view.addObject("detailList", result);
//        view.addObject("totalItem", result.getTotalItem());
        view.addObject("userInfo", userInfo);
        view.addObject("days", days);

        return view;
    }

    @RequestMapping({"", "/", "/orders.json"})
    public ResponseEntity<?> orders(HttpServletRequest request, Integer page,Integer pageSize, Integer days,Integer tab) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfo userInfo = getUserInfo(request);

        if (null == page) {
            page = 1;//默认只查30天
        }

        if (null == pageSize) {
            pageSize = 10;//默认10条
        }

        if (null == tab) {
            tab = 0;//默认查询自己的订单
        }
        if (null == days) {
            days = 30;//默认只查30天
        }
        if (days > 180) {
            days = 180;//大于180天则只取180天
        }
        RebateDetailQuery query = new RebateDetailQuery();
        query.setSubUnionId(userInfo.getSubUnionId());
        query.setOpenId(userInfo.getOpenId());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        query.setStartDate(cal.getTime());
        query.setIndex(page);
        query.setPageSize(pageSize);
        PaginatedArrayList<RebateDetailVo> result = null;
        if(1==tab){
            //查询代理模式一下级代理订单列表
            result = rebateDetailService.findFirstAgentSonRebateDetailList(query);
        }else if(2==tab){
            //查询代理模式二粉丝订单列表
            result = rebateDetailService.findSecondAgentRecommendRebateDetailList(query);
        }else{
            //查询自己的订单明细
            result = rebateDetailService.findRebateDetailList(query);
        }

        LOG.error("page:{},size:{}", page, result.size());

        map.put("detailList", result);
        map.put("totalItem", result.getTotalItem());
        map.put("userInfo", userInfo);
        map.put("days", days);
        map.put("tab", tab);
        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }


    @RequestMapping({"", "/", "/income"})
    public ModelAndView income(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/income");
        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
        return view;
    }

    @RequestMapping({"", "/", "/mySecondAgents"})
    public ModelAndView mySecondAgents(HttpServletRequest request,String sui,Integer page) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/agent/secondAgents");
        if (null == page) {
            page = 1;
        }

        String subUnionId = "";
        if(StringUtils.isNotBlank(sui)){
            subUnionId = DESUtil.qsDecrypt(jDProperty.getEncryptKey(), sui, "UTF-8");
        }else{
            UserInfo userInfo = getUserInfo(request);
            subUnionId = userInfo.getSubUnionId();
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

    @RequestMapping({"", "/", "/addSecondAgent.json"})
    public ResponseEntity<?> addSecondAgent(HttpServletRequest request,String agentSubUnionId,Double commissionRatio) {
        Map<String, Object> map = new HashMap<String, Object>();
        UserInfo userInfo = getUserInfo(request);

        //只能更新当前登录用户自己的二级代理比例
        int code = userInfoService.addSecondAgent(userInfo.getSubUnionId(),agentSubUnionId,commissionRatio);
        if(code== EAgentResultCode.SUCCESS.getCode()){
            map.put("success", true);
        }else{
            map.put("code", code);
            map.put("success", false);
        }

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/updateSecondAgentCommissionRate.json"})
    public ResponseEntity<?> updateSecondAgentCommissionRate(HttpServletRequest request,Long id,Double commissionRatio) {
        UserInfo userInfo = getUserInfo(request);
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=commissionRatio && commissionRatio>0 && commissionRatio<=1){
            map.put("success", true);
            userInfoService.updateSecondAgentCommissionRate(userInfo.getSubUnionId(),id,commissionRatio);
        }else{
            map.put("success", false);
        }

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/registNextAgentQrCode.json"})
    public ResponseEntity<?> registNextAgentQrCode(HttpServletRequest request,Double commissionRatio) {
        UserInfo userInfo = getUserInfo(request);
        Map<String, Object> map = new HashMap<String, Object>();
        if(null!=commissionRatio && commissionRatio>0 && commissionRatio<=1){
            map.put("success", true);

            //生成二维码
            String sceneStr = userInfo.getOpenId()+"#"+EQrCodeType.REGIST_FIRST_AGENT_NEXT_AGENT_QR_CODE.getCode()+"#"+commissionRatio;
            String qrCodeUrl = wxService.getQrcodeUrl(sceneStr);
            //下载二维码
            String mediaId = wxService.getWxImageMediaId(qrCodeUrl);
            //发送图片消息
            wxService.sendImageMessage(userInfo.getOpenId(), mediaId);

            //发送文本消息
            String content = "代理推广二维码已生成!佣金比例:"+commissionRatio*100+"%";
            wxService.sendMessage(userInfo.getOpenId(),content);
        }else{
            map.put("success", false);
        }

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }

    @RequestMapping({"", "/", "/sonAgentStatistits"})
    public ModelAndView getSonAgentStatistits(HttpServletRequest request,Integer dayTab) {
        Random r = new Random(System.currentTimeMillis());
        ModelAndView view = new ModelAndView();
        String vm = VIEW_PREFIX + "/sonAgentStatistits";
        view.setViewName(vm);
        UserInfo userInfo = getUserInfo(request);

        if (null == userInfo || userInfo.getAgent() == EAgent.NOT_AGENT.getCode()) {
            view.setViewName(VIEW_PREFIX + "/permission");
            return view;
        }

        OrderSummary orderSummaryQuery = new OrderSummary();
        orderSummaryQuery.setSubUnionId(userInfo.getSubUnionId());
        orderSummaryQuery.setOpenId(userInfo.getOpenId());
        orderSummaryQuery.setPageSize(60);//取近30天记录
        PaginatedArrayList<OrderSummary>  list = null;
        if(userInfo.getAgent() == EAgent.FIRST_AGENT.getCode()){
            list =  rebateDetailService.getFirstAgentSonOrderSummary(userInfo.getSubUnionId());
        }else if(userInfo.getAgent() == EAgent.SECOND_AGENT.getCode()){
            list =  rebateDetailService.getSecondAgentRecommendUserOrderSummary(userInfo.getOpenId());
        }

        view.addObject("list", list);
        view.addObject("userInfo", userInfo);
        view.addObject("r", r.nextInt());
        return view;
    }

    @RequestMapping({"", "/", "/agentStatistits"})
    public ModelAndView agentStatistits(HttpServletRequest request,Integer dayTab) {
        Random r = new Random(System.currentTimeMillis());
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
        if(isAdmin){

            OrderSummaryQuery orderSummaryQuery2 = new OrderSummaryQuery();
            orderSummaryQuery2.setStartDate(startDate);
            orderSummaryQuery2.setEndDate(endDate);
            OrderSummary allOrderSummary = rebateDetailService.getAllOrderSummaryByDate(orderSummaryQuery2);
            if (null != allOrderSummary) {
                if(null!=allOrderSummary.getCommission()){
                    allOrderSummary.setCommission(new BigDecimal(allOrderSummary.getCommission()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }else{
                    allOrderSummary.setCommission(0.0);
                }
                allOrderSummary.setClickCount(shortUrlManager.getALLJDUnionUrlClick(queryDate));
                view.addObject("allOrderSummary", allOrderSummary);
            }
        }

        if (userInfo.getAgent() == EAgent.SECOND_AGENT.getCode()) {
            //按时间取粉丝数
            RecommendUserInfoQuery recommendUserInfoQuery = new RecommendUserInfoQuery();
            recommendUserInfoQuery.setRecommendAccount(userInfo.getOpenId());
            recommendUserInfoQuery.setStartDate(startDate);
            recommendUserInfoQuery.setEndDate(endDate);
            view.addObject("recommendUserCount", userInfoService.findRecommendUserCount(recommendUserInfoQuery));

            //取全部粉丝数
            RecommendUserInfoQuery recommendAllUserInfoQuery = new RecommendUserInfoQuery();
            recommendAllUserInfoQuery.setRecommendAccount(userInfo.getOpenId());
            view.addObject("allRecommendUserCount", userInfoService.findRecommendUserCount(recommendAllUserInfoQuery));

            RebateDetailQuery  rebateDetailQuery = new RebateDetailQuery();
            rebateDetailQuery.setOpenId(userInfo.getOpenId());
            rebateDetailQuery.setStartDate(startDate);
            rebateDetailQuery.setEndDate(endDate);
            OrderSummary orderSummary = rebateDetailService.getRecommendUserOrderSummaryByOpenId(rebateDetailQuery);
            if (null != orderSummary && orderSummary.getClickCount() == null) {
                orderSummary.setCommission(0.0);
            }
            view.addObject("recommendUserOrderSummary",orderSummary);
        }

        LOG.error("[agentStatistits]dayTab:"+dayTab+",size:"+list.size());
        view.addObject("dayTab", dayTab);
        view.addObject("adminFlag", isAdmin);
        view.addObject("todayClick",shortUrlManager.getJDUnionUrlClick(userInfo.getSubUnionId(),queryDate));
        view.addObject("todayOrderSummary", todayOrderSummary);
        view.addObject("list", list);
        view.addObject("userInfo", userInfo);
        view.addObject("r", r.nextInt());
        return view;
    }

    @RequestMapping({"", "/", "/logout"})
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {
        //清空登录cookie
        cookieUtils.deleteQsCookie(response, QsLoginInteceptor.USERINFO_COOKIE);
        cookieUtils.deleteQsCookie(response, QsLoginInteceptor.WX_ACCESSTOKEN_COOKIE);

        UserInfo userInfo = getUserInfo(request);


        if (null != userInfo) {
            //清空缓存
            userInfoService.cleanUserInfoCache(userInfo.getOpenId());
        }


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

        String sceneStr = openId+"#"+EQrCodeType.REGIST_FIRST_AGENT_QR_CODE.getCode();
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

        String sceneStr = openId+"#"+EQrCodeType.REGIST_SECOND_AGENT_QR_CODE.getCode();
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

        String sceneStr = openId+"#"+EQrCodeType.REGIST_SECOND_AGENT_REBATE_USER_QR_CODE.getCode();
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
