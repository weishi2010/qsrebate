package com.rebate.test;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.cps.ServicePromotionGoodsInfoRequest;
import com.jd.open.api.sdk.response.cps.ServicePromotionGoodsInfoResponse;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.manager.weibo.WeiBoManager;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class WeiBoSdkTest extends AbstractJUnit4SpringContextTests {
    @Qualifier("weiBoManager")
    @Autowired(required = true)
    private WeiBoManager weiBoManager;

    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add("http://m.qingsongfan.com.cn");
        List urlList =  weiBoManager.getShortUrl(list);
        System.out.println(urlList);
    }
}
