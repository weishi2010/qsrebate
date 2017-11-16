package com.rebate.controller;

import com.rebate.controller.base.BaseController;
import com.rebate.manager.shorturl.ShortUrlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ShortUrlController.PREFIX)
public class ShortUrlController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ShortUrlController.class);

    public static final String PREFIX = "/qsc";

    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @RequestMapping({"", "/", "/qsu"})
    public String jumpJdUnionUrl( String d,String sui) {

        LOG.error("jumpJdUnionUrl d;{},sui:{}",d,sui);
        //获取md5后的子联盟ID进行点击统计
        shortUrlManager.incrJDUnionUrlClick(sui);

        return "redirect:https://union-click.jd.com/jdc?d=" + d;
    }


}
