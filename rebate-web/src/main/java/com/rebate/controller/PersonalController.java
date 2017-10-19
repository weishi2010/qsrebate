package com.rebate.controller;

import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.controller.base.BaseController;
import com.rebate.domain.ExtractDetail;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EExtractCode;
import com.rebate.domain.en.EExtractStatus;
import com.rebate.domain.query.ExtractDetailQuery;
import com.rebate.domain.query.RebateDetailQuery;
import com.rebate.domain.vo.ExtractDetailVo;
import com.rebate.domain.vo.RebateDetailVo;
import com.rebate.service.extract.ExtractDetailService;
import com.rebate.service.order.RebateDetailService;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    @Qualifier("wxService")
    @Autowired(required = true)
    private WxService wxService;

    @RequestMapping({"", "/", "/extract"})
    public ModelAndView extract(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/extract");
        UserInfo userInfo = getUserInfo(request);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/extractDetail"})
    public ModelAndView extractDetail(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/extractDetail");
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
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/personal");

        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
        view.addObject("commission", userInfoService.getUserCommission(userInfo.getOpenId()));

        return view;
    }

    @RequestMapping({"", "/", "/orderDetail"})
    public ModelAndView orderDetail(HttpServletRequest request, Integer days) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/orderDetail");
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
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/income");
        UserInfo userInfo = getUserInfo(request);

        view.addObject("userInfo", userInfo);
        return view;
    }


    @RequestMapping({"", "/", "/qrcode"})
    public ModelAndView qrcode(HttpServletRequest request) {
        ModelAndView view = new ModelAndView(VIEW_PREFIX+ "/agent/qrcode");
        UserInfo userInfo = getUserInfo(request);

        view.addObject("qrcodeUrl", wxService.getQrcodeUrl(userInfo.getOpenId()));
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
