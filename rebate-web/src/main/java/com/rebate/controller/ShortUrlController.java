package com.rebate.controller;

import com.rebate.controller.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ShortUrlController.PREFIX)
public class ShortUrlController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ShortUrlController.class);

    public static final String PREFIX = "/qsc";

    @RequestMapping({"", "/", "/jumpJdUnionUrl"})
    public String jumpJdUnionUrl( String d) {
        return "redirect:https://union-click.jd.com/jdc?d=" + d;
    }


}
