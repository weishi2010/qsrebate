package com.rebate.manager.weibo.impl;

import com.google.common.base.Joiner;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.property.WeiBoProperty;
import com.rebate.manager.weibo.WeiBoManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("weiBoManager")
public class WeiBoManagerImpl implements WeiBoManager {
    private static Logger LOG = LoggerFactory.getLogger(WeiBoManagerImpl.class);

    @Qualifier("weiBoProperty")
    @Autowired(required = false)
    private WeiBoProperty weiBoProperty;

    @Override
    public List<String> getShortUrl(List<String> urls) {
        List<String> shortUrlList = new ArrayList<>();

        try {
            List<String> urlParamList = new ArrayList<>();
            urlParamList.add("access_token=" + weiBoProperty.getAccessToken());
            for (String url : urls) {
                urlParamList.add("url_long=" + URLEncoder.encode(url, "GBK"));
            }
            String params = Joiner.on("&").join(urlParamList);
            String json = HttpClientUtil.get(weiBoProperty.getShortUrlApiUrl() + "?" + params);
            Map resultMap = JsonUtil.fromJson(json, Map.class);
            if (resultMap.containsKey("urls")) {
                List<Map> mapList = (List<Map>) resultMap.get("urls");
                if (null != mapList) {
                    for (Map map : mapList) {
                        String shortUrl = map.get("url_short").toString();
                        if (StringUtils.isNotBlank(shortUrl)) {
                            shortUrlList.add(shortUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Weibo getShortUrl error!",e);
        }

        return shortUrlList;
    }
}
