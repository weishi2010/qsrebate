package com.rebate.test;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.cps.ServicePromotionGoodsInfoRequest;
import com.jd.open.api.sdk.response.cps.ServicePromotionGoodsInfoResponse;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.SerializeXmlUtil;
import com.rebate.domain.UserInfo;
import com.rebate.domain.wx.AuthorizationCodeInfo;
import com.rebate.domain.wx.InputMessage;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.wx.WxAccessTokenService;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSON;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.servlet.ServletInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class WXSdkTest extends AbstractJUnit4SpringContextTests {
    private static final Logger LOG = LoggerFactory.getLogger(WXSdkTest.class);

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
        if (null == authorizationCodeInfo) {
            System.out.println("authorizationCodeInfo:" + JsonUtil.toJson(authorizationCodeInfo));
        }
        System.out.println("authorizationCodeInfo:" + authorizationCodeInfo);
    }

    /**
     * {"openid":"oIAUmv8x60aC5B7FrxVy8Z9-imyY","nickname":"wsh","sex":1,"language":"zh_CN","city":"Chaoyang","province":"Beijing","country":"CN","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/vi_32\/DYAIOgq83eqpJdBmwaEgG5QfqPAyxvUBtEtPmeoIP9Zmmxkic8EJvKUXj9FjUAoMwnvb0KySL7NMPJXc6Kic4lOQ\/0","privilege":[]}
     */
    @Test
    public void getWXUserInfo() {
        String accessToken = "Uf0xvO9MHvROFIK78NGDXtL8XnOeXODAVV7XvF0YXnScsmzzGj47xl4LSSdAt2hCT6TfT8IhT1zWedONDBZ5dA";
        String openId = "oIAUmv8x60aC5B7FrxVy8Z9";
        WxUserInfo wxUserInfo = wxAccessTokenService.getWxUserInfo(accessToken, openId);
        System.out.println(JsonUtil.toJson(wxUserInfo));
    }

    @Test
    public void ticket() {
        String ticket = wxAccessTokenService.getTicket();
        System.out.println("ticket:" + ticket);
    }

    @Test
    public void testJson() {
        UserInfo userInfo = new UserInfo();
        userInfo.setWxImage("asdfsdf");
        System.out.println(JsonUtil.toJson(userInfo));

        String json = "{\"access_token\":\"rifBleWm-XwM7NlS9FM4uyBrFPiucHM3r1Ihs2UmbUEi8F-zxtWSO4vWDQR5hvqR992UxB-d0UmqLaABJ1qpSA\",\"expires_in\":7200,\"refresh_token\":\"o8lQMJnWlz2j5HrBMqzJz4mSu8ed_0gYBfncJ5A05h6R1LPoBmin-e7s31NzbNZvAl0f4N1H2lFs2eowSstWRw\",\"openid\":\"oIAUmv8x60aC5B7FrxVy8Z9-imyY\",\"scope\":\"snsapi_userinfo\"}";
        AuthorizationCodeInfo authorizationCodeInfo = JsonUtil.fromJson(json, AuthorizationCodeInfo.class);
        System.out.println(authorizationCodeInfo.getAccessToken());
    }

    public static void main(String[] args){
        InputMessage inputMessage = null;
        // 处理接收消息
        try {
            String xmlMsg =   "<xml>" +
                    "<ToUserName><![CDATA[gh_70856a038556]]></ToUserName>" +
                    "<FromUserName><![CDATA[oIAUmvxE2B8M53Rn5S7_5ctIQqzA]]></FromUserName>" +
                    "<CreateTime>1507381258</CreateTime>" +
                    "<MsgType><![CDATA[event]]></MsgType>" +
                    "<Event><![CDATA[unsubscribe]]></Event>" +
                    "<EventKey><![CDATA[]]></EventKey>" +
                    "</xml>";
            // 将POST流转换为XStream对象
            XStream xs = SerializeXmlUtil.createXstream();
            xs.alias("xml", InputMessage.class);
            inputMessage = (InputMessage) xs.fromXML(xmlMsg.toString());

            System.out.println("----"+inputMessage.getEvent());
//            inputMessage = JsonUtil.fromJson(xmlMsg.toString(), InputMessage.class);
        } catch (Exception e) {
            LOG.error("getInputMessage error!", e);
        }
    }
}
