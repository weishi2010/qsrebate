package com.rebate.test;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.cps.ServicePromotionGoodsInfoRequest;
import com.jd.open.api.sdk.response.cps.ServicePromotionGoodsInfoResponse;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.wx.AuthorizationCodeInfo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.wx.WxAccessTokenService;
import net.sf.json.JSON;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class WXSdkTest extends AbstractJUnit4SpringContextTests {
    /**
     * {"access_token":"T1zYOY10swUIMzqV5isSGxKj_aDx8hnMD7ZdXt7iuVPu66xmxzaoYBJXU5D_cJ5tT1bkYn_8vUSqzkOBPmz-GQ","expires_in":7200,"refresh_token":"7cnelCw6N9rxYpHS_AlnOtnmTpTLi-XJ0Oiuk6k34mcB_T06_lssfFvnRzYzv6JoVFG0dW86wrcZhyFBlIKyJw","openid":"oIAUmv8x60aC5B7FrxVy8Z9-imyY","scope":"snsapi_userinfo"}"
     *
     */
    /**
     * 微信公众号接口配置
     */
    @Qualifier("wxAccessTokenService")
    @Autowired(required = true)
    private WxAccessTokenService wxAccessTokenService;

    @Test
    public void getAccessTokenUrl() {
        String code = "021Og2pc1M3Jtr03i8oc1NUNoc1Og2p1";
        AuthorizationCodeInfo authorizationCodeInfo = wxAccessTokenService.getLoginAccessToken(code);
        if (null==authorizationCodeInfo) {
            System.out.println("authorizationCodeInfo:" + JsonUtil.toJson(authorizationCodeInfo));
        }
        System.out.println("authorizationCodeInfo:"+authorizationCodeInfo);
    }

    /**
     * {"openid":"oIAUmv8x60aC5B7FrxVy8Z9-imyY","nickname":"wsh","sex":1,"language":"zh_CN","city":"Chaoyang","province":"Beijing","country":"CN","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/vi_32\/DYAIOgq83eqpJdBmwaEgG5QfqPAyxvUBtEtPmeoIP9Zmmxkic8EJvKUXj9FjUAoMwnvb0KySL7NMPJXc6Kic4lOQ\/0","privilege":[]}
     */
    @Test
    public void getWXUserInfo(){
        String accessToken = "T1zYOY10swUIMzqV5isSGxKj_aDx8hnMD7ZdXt7iuVPu66xmxzaoYBJXU5D_cJ5tT1bkYn_8vUSqzkOBPmz-GQ";
        String openId ="oIAUmv8x60aC5B7FrxVy8Z9";
        WxUserInfo wxUserInfo = wxAccessTokenService.getWxUserInfo(accessToken,openId);
        System.out.println(wxUserInfo);
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", "wxa460b41c4d7421f0");
        params.put("secret", "f6855bc575fc656f0c77f8f46cf624da");
        params.put("code", "021Og2pc1M3Jtr03i8oc1NUNoc1Og2p1");
        params.put("grant_type", "authorization_code");

        String json = HttpClientUtil.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa460b41c4d7421f0&secret=f6855bc575fc656f0c77f8f46cf624da&code=021Og2pc1M3Jtr03i8oc1NUNoc1Og2p1&grant_type=authorization_code");

        System.out.println(json);
    }
}
