package com.rebate.service.job.impl;

import com.rebate.common.util.HtmlCreatorUtil;
import com.rebate.service.job.TopicJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weishi on 2017/7/17.
 */
@Service("topicJob")
public class TopicJobImpl implements TopicJob {

    @Qualifier("htmlCreatorMap")
    @Autowired(required = true)
    private HashMap htmlCreatorMap;

    @Override
    public void createHtmlJob(){
            String templatePath = htmlCreatorMap.get("templatePath").toString();
            String templateName = htmlCreatorMap.get("templateName").toString();
            String htmlPath = htmlCreatorMap.get("htmlPath").toString();
            String html = htmlCreatorMap.get("html").toString();
            List<Map> mapList = new ArrayList<Map>();
            for(int i=0;i<10;i++){

                Map<String,Object> topicMap = new HashMap<String,Object>();
                topicMap.put("img","/uploads/allimg/s14976187838899.jpg");
                topicMap.put("imgDesc","图片描述"+i);
                topicMap.put("title","标题"+i);
                topicMap.put("clickCount",100);
                topicMap.put("publishTime", "2017-07-17");

                mapList.add(topicMap);
            }

            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("topicMapList",mapList);

            HtmlCreatorUtil.createHtml(templatePath, htmlPath, templateName, html, paramMap);
    }
}
