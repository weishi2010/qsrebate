package com.rebate.controller;

import com.rebate.controller.base.BaseController;
import com.rebate.domain.AdvertismentPosition;
import com.rebate.domain.en.EActivityStatus;
import com.rebate.domain.en.EPromotionTab;
import com.rebate.domain.query.ActivityQuery;
import com.rebate.service.activity.ActivityService;
import com.rebate.service.activity.AdvertismentPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(ActivityController.PREFIX)
public class ActivityController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityController.class);
    public static final String PREFIX = "/activity";
    public static final String VIEW_PREFIX = "/rebate";


    @Qualifier("activityService")
    @Autowired(required = true)
    private ActivityService activityService;

    @Qualifier("advertismentPositionService")
    @Autowired(required = true)
    private AdvertismentPositionService advertismentPositionService;

    @RequestMapping({"", "/", "/activityIndex"})
    public ModelAndView activityIndex() {
        ModelAndView view = new ModelAndView(VIEW_PREFIX + "/activity/activityList");
        ActivityQuery activityQuery= new ActivityQuery();
        activityQuery.setStatusList(EActivityStatus.DEFAULT.getCode()+","+EActivityStatus.PASS.getCode());
        activityQuery.setPageSize(20);

        view.addObject("activityList",activityService.getActivityTopList(activityQuery));
        view.addObject("promotionTab", EPromotionTab.ACTIVITY.getTab());

        return view;
    }

    @RequestMapping({"", "/", "/activityList.json"})
    public ResponseEntity<?> activityList() {

        Map<String, Object> map = new HashMap<String, Object>();
        ActivityQuery activityQuery= new ActivityQuery();
        activityQuery.setStatusList(EActivityStatus.DEFAULT.getCode()+","+EActivityStatus.PASS.getCode());
        activityQuery.setPageSize(20);
        map.put("activityList",activityService.getActivityTopList(activityQuery));
        map.put("adPosition",advertismentPositionService.findMainAdvertismentPosition());

        return new ResponseEntity<Map<String, ?>>(map, HttpStatus.OK);
    }
}
