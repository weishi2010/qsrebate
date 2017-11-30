package com.rebate.test;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RegexUtils;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.Product;
import com.rebate.domain.UserInfo;
import com.rebate.manager.MessageTempManager;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.userinfo.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class MessageTest extends AbstractJUnit4SpringContextTests {
    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("messageTempManager")
    @Autowired(required = true)
    private MessageTempManager messageTempManager;

    @Qualifier("userInfoService")
    @Autowired(required = true)
    private UserInfoService userInfoService;

    @Qualifier("userInfoDao")
    @Autowired(required = true)
    private UserInfoDao userInfoDao;

    @Test
    public void salesMessageConvertJDMediaUrl() {
        String content ="免费发放京东全品类优惠券】\n" +
                "\n" +
                "?￥?东全品类15元优惠券\n" +
                "https://w.url.cn/s/A6R1o5T\n" +
                "\n" +
                "?￥京东全品类10元优惠券\n" +
                "https://w.url.cn/s/AiuOBlu\n" +
                "\n" +
                "?东全品类5元优惠券，数量有限速度抢https://w.url.cn/s/AaIXaV0";
        String subUnionId ="wxx";
        try {
            boolean hasSku = false;
            //包括sku的则只转sku为推广链接
            List<Long> dataList = RegexUtils.getLongList(content);
            for (Long data : dataList) {
                //如果大于5位大数据则转为商品推广链接
                if(data.toString().length()>5){
                    hasSku = true;
                    String url = jdSdkManager.getShortPromotinUrl(data,subUnionId);
                    content = content.replace(""+data,url);
                }
            }

            //不包含sku的则按活动转链处理
            if(!hasSku){
                List<String> list = RegexUtils.getLinks(content);
                for (String link : list) {
                    //JD活动多重跳转解析
                    String convertJDPromotionUrl = HttpClientUtil.convertJDPromotionUrl(link);
                    //转换为推广链接
                    String jdMediaUrl = jdSdkManager.getSalesActivityPromotinUrl(convertJDPromotionUrl, subUnionId);
                    jdMediaUrl = shortUrlManager.getWxShortPromotinUrl(jdMediaUrl, subUnionId);
                    content = content.replace(link, jdMediaUrl);
                }


            }

            System.out.println(content);
        } catch (Exception e) {
        }
    }

    @Test
    public void getAgentRecommendContent() {
        String content ="免费发放京东全品类优惠券】\n" +
                "\n" +
                "?￥?东全品类15元优惠券\n" +
                "https://w.url.cn/s/A6R1o5T\n" +
                "\n" +
                "?￥京东全品类10元优惠券\n" +
                "https://w.url.cn/s/AiuOBlu\n" +
                "\n" +
                "?东全品类5元优惠券，数量有限速度抢https://w.url.cn/s/AaIXaV0";
        String subUnionId ="wxx";
        StringBuffer recommendContent = new StringBuffer();

        List<Long> skus = RegexUtils.getLongList(content);
        if (skus.size() > 0) {
            //消息中有SKU信息则按SKU进行搜索
            Long skuId = skus.get(0);
            List<Product> products = jdSdkManager.getMediaProducts(skuId.toString());

            if (null != products && products.size() > 0) {

                Product product = products.get(0);

                //查询用户信息
                UserInfo userInfoQuery = new UserInfo();
                userInfoQuery.setSubUnionId(subUnionId);
                UserInfo userInfo = userInfoDao.findUserInfoBySubUnionId(userInfoQuery);


                String shortUrl = jdSdkManager.getShortPromotinUrl(product.getProductId(), subUnionId);
                shortUrl = shortUrlManager.getWxShortPromotinUrl(shortUrl, subUnionId);

                product.setUserCommission(jdSdkManager.getQSCommission(userInfo, product));
                //获取代理户消息模板
                recommendContent.append(messageTempManager.getAgentProductMessageTemp(product, shortUrl));
            }
        }else{
            if(RegexUtils.checkURL(content)){
                //转换为推广链接
                String jdMediaUrl = jdSdkManager.getSalesActivityPromotinUrl(content, subUnionId);
                jdMediaUrl = shortUrlManager.getWxShortPromotinUrl(jdMediaUrl, subUnionId);
                recommendContent.append(jdMediaUrl);
            }
        }

        if (StringUtils.isBlank(recommendContent.toString())) {
            recommendContent.append("很抱歉，暂时没有可推广的商品!");
        }
    }

    public static void main(String[] args) {
        String url1 ="http://union-click.jd.com/jdc?d=qakJIw";
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
