package com.rebate.test;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.RegexUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class MessageTest {

    public static void main(String[] args) {
        String url1 ="https://w.url.cn/s/AZeaQyw";
        System.out.println(HttpClientUtil.convertJDPromotionUrl(url1));

        String content = "京东捡漏领券专区\n" +
                "http://c7.gg/wXf8\n" +
                "\n" +
                "抽美赞 臣199-100\n" +
                "https://union-click.jd.com/jdc?d=KZV8Tr\n" +
                "\n" +
                "1元购凑单\n" +
                "http://t.cn/RCvKvNc\n" +
                "\n" +
                "京东捡漏领券专区\n" +
                "http://c7.gg/wXf8\n" +
                "\n" +
                "抽美赞 臣199-100\n" +
                "https://union-click.jd.com/jdc?d=KZV8Tr\n" +
                "\n" +
                "1元购凑单\n" +
                "http://t.cn/RCvKvNc";
        List<String> list = RegexUtils.getLinks(content);
        for(String url:list){

            String oriUrl = HttpClientUtil.convertJDPromotionUrl(url);
            System.out.println(oriUrl);
        }
    }
}
