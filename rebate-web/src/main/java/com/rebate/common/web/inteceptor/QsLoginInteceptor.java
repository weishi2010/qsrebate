
package com.rebate.common.web.inteceptor;

import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RequestUtils;
import com.rebate.domain.UserInfo;
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
    private static String WX_ACCESSTOKEN_COOKIE = "WX_T_C";


    /**
     * 用户信息COOKIE
     */
    private static String USERINFO_COOKIE = "u_i_o";

    /**
     * 用户信息
     */
    private static String USERINFO = "userInfo";

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

        if (StringUtils.isNotBlank(userInfoCookieValue)) {
            LOG.error("===================>userInfoCookieValue:" + userInfoCookieValue);
            userInfo = JsonUtil.fromJson(userInfoCookieValue, UserInfo.class);
        } else {
            AuthorizationCodeInfo authorizationCodeInfo = null;
            String cookieValue = cookieUtils.getQsCookieValue(request, WX_ACCESSTOKEN_COOKIE);
            if (StringUtils.isNotBlank(cookieValue)) {
                authorizationCodeInfo = JsonUtil.fromJson(cookieValue, AuthorizationCodeInfo.class);
            } else {
                //获取WX登录code
                String loginCode = getWxLoginCode(request, response);
                if (StringUtils.isNotBlank(loginCode)) {
                    authorizationCodeInfo = wxAccessTokenService.getLoginAccessToken(loginCode);
                }
                if (null == authorizationCodeInfo) {
                    //转跳到WX授权页，用户授权后回跳到当前应用链接并附带code参数
                    redirect2WxAuthorizePage(request, response);
                    return false;
                } else {
                    cookieUtils.setCookie(response, WX_ACCESSTOKEN_COOKIE, JsonUtil.toJson(authorizationCodeInfo));
                }
            }
            LOG.error("===================>authorizationCodeInfo:" + JsonUtil.toJson(authorizationCodeInfo));

            userInfo = userInfoService.getUserInfo(authorizationCodeInfo.getOpenId());
            if (null == userInfo) {
                WxUserInfo wxUserInfo = wxAccessTokenService.getWxUserInfo(authorizationCodeInfo.getAccessToken(), authorizationCodeInfo.getOpenId());
                LOG.error("===================>wxUserInfo:" + JsonUtil.toJson(wxUserInfo));
                if (null != wxUserInfo) {
                    userInfo = new UserInfo();
                    userInfo.setPhone("");
                    userInfo.setNickName(wxUserInfo.getNickname());
                    userInfo.setOpenId(wxUserInfo.getOpenid());
                    userInfo.setStatus(0);
                    userInfo.setEmail("");
                    userInfo.setWxImage(wxUserInfo.getHeadimgurl());
                    userInfoService.registUserInfo(userInfo);
                }
            }
        }
        if (null != userInfo) {
            request.setAttribute(USERINFO, userInfo);
            cookieUtils.setCookie(response, USERINFO_COOKIE, JsonUtil.toJson(userInfo));
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
        LOG.error("wxReturnUrl:" + log);

        response.sendRedirect(log);
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
