package com.rebate.test;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.cps.ServicePromotionGoodsInfoRequest;
import com.jd.open.api.sdk.response.cps.ServicePromotionGoodsInfoResponse;
import com.rebate.common.util.HttpClientUtil;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JDSdkTest {
    private static final String appKey = "58647A717120C93F2B509C79EE554B5C";
    private static final String appSecret = "0490015803c14051bf4d5763e7cb3a06 ";

    /**
     * {
     "access_token": "b721dff3-cdf3-4e69-9245-aaf4c2e2e2e4",
     "code": 0,
     "expires_in": 31535999,
     "refresh_token": "88c6a481-9f68-4df8-952c-c9769ef05c13",
     "time": "1503844477453",
     "token_type": "bearer",
     "uid": "2298807957",
     "user_nick": "yitong0524"
     }
     * @param accessToken
     * @throws JdException
     */
    public static  String  getOrderInfList(String accessToken) throws JdException {
        String apiUrl = "https://api.jd.com/routerjson";
        JdClient client=new DefaultJdClient(apiUrl,accessToken,appKey,appSecret);

        ServicePromotionGoodsInfoRequest request=new ServicePromotionGoodsInfoRequest();

        request.setSkuIds( "1031535957" );

        ServicePromotionGoodsInfoResponse response= client.execute(request);

        return response.getGetpromotioninfoResult();
    }

    public static String getAccessToken() throws Exception {
        String authorizeCode = "IkRHg4";

        String appUrl ="www.jingcuhui.com";
        String url ="https://oauth.jd.com/oauth/token?grant_type=authorization_code&client_id="+appKey
                +"&client_secret="+ appSecret
                +"&scope=read&redirect_uri=http://"+ appUrl
                +"&code="+ authorizeCode
                +"&state=1234";
        URL uri = new URL(url);
        HttpURLConnection conn =(HttpURLConnection) uri.openConnection();
        conn.setRequestProperty("Accept-Charset","utf-8");
        conn.setRequestMethod("POST");
        int responseCode = conn.getResponseCode();
        InputStream is =conn.getInputStream();
        String jsonStr = IOUtils.toString(is, "UTF-8");

        return jsonStr;
    }

    public static String getJDSDKCode(){
        String url = "https://oauth.jd.com/oauth/authorize?response_type=code&client_id=4FC0D2906C5B6832B43A6309FB51CD22&redirect_uri=https://m.xfanli.cn/&state=ws1";
        String code = HttpClientUtil.get(url);
        return code;
    }
    public static void main(String[] args) throws Exception {
//        String accessToken = getAccessToken();

        String accessToken = "b721dff3-cdf3-4e69-9245-aaf4c2e2e2e4";
        String json = getOrderInfList(accessToken);
        System.out.println("json:"+json);

    }
}
