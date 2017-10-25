
package com.rebate.common.web.inteceptor;

import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RequestUtils;
import com.rebate.domain.UserInfo;
import com.rebate.domain.en.EAgent;
import com.rebate.domain.en.ESequence;
import com.rebate.domain.en.ESubUnionIdPrefix;
import com.rebate.domain.wx.AuthorizationCodeInfo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
import com.rebate.service.userinfo.UserInfoService;
import com.rebate.service.wx.WxAccessTokenService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class QsLoginInteceptor extends LoginInteceptor {

    private static final Logger LOG = LoggerFactory.getLogger(QsLoginInteceptor.class);

    /**
     * 登录后微信用户openId
     */
    private static String LOGIN_OPEN_ID = "wx_openId";

    /**
     * WX授权accessToken Cookie
     */
    public static String WX_ACCESSTOKEN_COOKIE = "WX_T_C";


    /**
     * 用户信息COOKIE
     */
    public static String USERINFO_COOKIE = "qs_u_i_o";

    /**
     * 用户信息
     */
    public static String USERINFO = "userInfo";

    private CookieUtils cookieUtils;

    /**
     * 微信公众号接口配置
     */
    private WxConfig wxConfig;

    @Qualifier("wxAccessTokenService")
    @Autowired(required = false)
    private WxAccessTokenService wxAccessTokenService;

    @Qualifier("userInfoService")
    @Autowired(required = false)
    private UserInfoService userInfoService;

    private String loginUrl;

    private String charsetName = "gbk";

    private String uriEncoding = "gbk";

    private Set<String> needPinPaths;

    private String staticResourcePath;


    public void setStaticResourcePath(String staticResourcePath) {
        this.staticResourcePath = staticResourcePath;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        String path = request.getServletPath();
        if (!path.startsWith(staticResourcePath)) {

            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Cache-Control", "no-store");
            response.setDateHeader("Expires", 0);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        UserInfo userInfo = null;
        String userInfoCookieValue = cookieUtils.getQsCookieValue(request, USERINFO_COOKIE);

        LOG.error("===================>userInfoCookieValue:" + userInfoCookieValue);
        if (StringUtils.isNotBlank(userInfoCookieValue)) {
            userInfo = JsonUtil.fromJson(userInfoCookieValue, UserInfo.class);
        } else {
            //获取accessToken
            AuthorizationCodeInfo authorizationCodeInfo = null;
            String cookieValue = cookieUtils.getQsCookieValue(request, WX_ACCESSTOKEN_COOKIE);
            String openId = null;
            if (StringUtils.isNotBlank(cookieValue)) {
                authorizationCodeInfo = JsonUtil.fromJson(cookieValue, AuthorizationCodeInfo.class);
                openId = authorizationCodeInfo.getOpenId();
            } else {
                //获取WX登录code
                String loginCode = getWxLoginCode(request, response);
                if (StringUtils.isNotBlank(loginCode)) {
                    authorizationCodeInfo = wxAccessTokenService.getLoginAccessToken(loginCode);
                    if (null != authorizationCodeInfo) {
                        openId = authorizationCodeInfo.getOpenId();
                        cookieUtils.setCookie(response, WX_ACCESSTOKEN_COOKIE, JsonUtil.toJson(authorizationCodeInfo),300);
                    }
                }else{
                    //转跳到WX授权页，用户授权后回跳到当前应用链接并附带code参数
                    redirect2WxAuthorizePage(request, response);
                    return false;
                }
            }

            if (StringUtils.isNotBlank(openId)) {
                //查询用户信息
                userInfo = userInfoService.getUserInfo(authorizationCodeInfo.getOpenId());

                if (null != userInfo) {
                    //设置cookie
                    LOG.error("[set cookie]===================>userInfo:" + JsonUtil.toJson(userInfo));
                    cookieUtils.setCookie(response, USERINFO_COOKIE, JsonUtil.toJson(userInfo), 120);
                }else{
                    //用户未注册的，则跳转到公众号关注页，通过关注进行注册
                    redirect2WxSubscribePage(request, response);
                    return false;
                }

            }


        }

        LOG.error("===================>userInfo:" + JsonUtil.toJson(userInfo));

        if (null != userInfo) {
            request.setAttribute(USERINFO, userInfo);
        }

        return true;
    }


    /**
     * 获取WX登录code
     *
     * @param request
     * @return
     */
    private String getWxLoginCode(HttpServletRequest request, HttpServletResponse response) {
        //获取WX登录code
        String loginCode = request.getParameter("code");
        return loginCode;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 跳转到登录页
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void redirect2WxAuthorizePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String currentUrl = RequestUtils.getDomainUrl(request);
        String encodeCurrentUrl = "";
        try {
            encodeCurrentUrl = RequestUtils.encode(currentUrl, charsetName);

        } catch (Exception e) {
            LOG.error("RequestUtils.encodeCurrentUrl error!! {}:", new Object[]{currentUrl, charsetName}, e);
        }
        String log = getWxReturnUrl(encodeCurrentUrl);
        LOG.error("[redirect2WxAuthorizePage]####################>returnUrl:" + log);

        response.sendRedirect(log);
    }

    /**
     * 跳转到公众号关注页
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void redirect2WxSubscribePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Random random = new Random();
        int scene = random.nextInt(100000);
        String wxSubscribeUrl = "https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzIwNjkzOTkzNg==&scene="+scene+"#wechat_redirect";
        response.sendRedirect(wxSubscribeUrl);
    }

    /**
     * 获取返回URL
     *
     * @param encodeReturnUrl
     * @return
     */
    private String getWxReturnUrl(String encodeReturnUrl) {
        Integer randomNum = RandomUtils.nextInt(1000);
        return wxConfig.getAuthorizeUrl() + "?appid=" + wxConfig.getAppId() + "&redirect_uri=" + encodeReturnUrl + "&response_type=code&scope=snsapi_userinfo&state=" + randomNum + "#wechat_redirect";
    }

    public boolean needLogin(String path) {
        if (!this.needRedirect) {
            return Boolean.FALSE.booleanValue();
        } else if (!StringUtils.isEmpty(path) && this.unLoginPaths != null && this.unLoginPaths.size() != 0) {
            String halfPath = "";
            int lastIndex = path.lastIndexOf(".");
            if (lastIndex > -1) {
                halfPath = path.substring(0, lastIndex);
            }

            Iterator i$;
            String perPath;
            if (this.everNeedLoginPaths != null && this.everNeedLoginPaths.size() > 0) {
                i$ = this.everNeedLoginPaths.iterator();

                while (i$.hasNext()) {
                    perPath = (String) i$.next();
                    if (halfPath.equals(perPath)) {
                        return Boolean.TRUE.booleanValue();
                    }
                }
            }

            i$ = this.unLoginPaths.iterator();

            do {
                if (!i$.hasNext()) {
                    return Boolean.TRUE.booleanValue();
                }

                perPath = (String) i$.next();
            } while (!path.startsWith(perPath));

            return Boolean.FALSE.booleanValue();
        } else {
            return Boolean.TRUE.booleanValue();
        }
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        boolean isAjaxReuest = false;
        if (this.ajaxModel == null) {
            if (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                isAjaxReuest = true;
            }
        } else {
            isAjaxReuest = request.getHeader("X-Requested-With") != null;
        }

        return isAjaxReuest;
    }

    protected void ajaxResponse(HttpServletResponse response) {
        PrintWriter writer = null;

        try {
            writer = response.getWriter();
            writer.write("{\"error\":\"NotLogin\"}");
        } catch (Exception var12) {
            LOG.error("--ajaxResponse error--", var12);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception var11) {
                    LOG.error("--ajaxResponse close writer error--", var11);
                }
            }

        }

    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public void setUriEncoding(String uriEncoding) {
        this.uriEncoding = uriEncoding;
    }

    public void setCookieUtils(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }

    public WxConfig getWxConfig() {
        return wxConfig;
    }

    public void setWxConfig(WxConfig wxConfig) {
        this.wxConfig = wxConfig;
    }
}
