
package com.rebate.common.web.inteceptor;

import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.RequestUtils;
import com.rebate.dao.UserInfoDao;
import com.rebate.domain.UserInfo;
import com.rebate.domain.wx.WxConfig;
import com.rebate.domain.wx.WxUserInfo;
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
import java.util.UUID;

/**
 * 登录拦截
 * WX授权登录步骤：
 * 1 第一步：用户同意授权，获取code
 * 2 第二步：通过code换取网页授权access_token
 * 3 第三步：刷新access_token（如果需要）
 * 4 第四步：拉取用户信息(需scope为 snsapi_userinfo)
 * 5 附：检验授权凭证（access_token）是否有效
 *
 * @author mengxianglei
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2015-01-25 mengxianglei 参考M端plus的登陆完成 
 * =========================== 维护日志 ===========================
 */
public class QsLoginInteceptor extends LoginInteceptor {

    private static final Logger LOG = LoggerFactory.getLogger(QsLoginInteceptor.class);

    /**
     * 登录后微信用户openId
     */
    private static String LOGIN_OPEN_ID = "wx_openId";

    /**
     * WX授权登录code
     */
    private static String WX_LOGIN_CODE_COOKIE_NAME = "WX_LOGIN_CODE";
    /**
     * WX用户openId 登录后写入cookie的名称
     */
    private static String QS_USER_OPENID_COOKIE_NAME = "QS_O_C";

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

    @Qualifier("userInfoDao")
    @Autowired(required = false)
    private UserInfoDao userInfoDao;

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
//        String path = request.getServletPath();

        String openId = cookieUtils.getQsCookieValue(request, QS_USER_OPENID_COOKIE_NAME);
        openId ="weishi2010";//TODO WX授权后删除此代码
        if (StringUtils.isBlank(openId)) {
            //获取WX登录code
            String loginCode = getWxLoginCode(request, response);
            if (StringUtils.isNotBlank(loginCode)) {
                //获取用户信息
                WxUserInfo wxUserInfo = wxAccessTokenService.getWxUserInfo(loginCode);
                if (null != wxUserInfo) {
                    //将openId写入到cookie
                    cookieUtils.setCookie(response, QS_USER_OPENID_COOKIE_NAME, wxUserInfo.getOpenid());
                }
            } else {
                //转跳到WX授权页，用户授权后回跳到当前应用链接并附带code参数
                redirect2WxAuthorizePage(request, response);
            }
        } else {
            cookieUtils.setCookie(response, QS_USER_OPENID_COOKIE_NAME,openId);
            UserInfo userInfo = new UserInfo();
            userInfo.setOpenId(openId);
            UserInfo existsUserInfo = userInfoDao.findLoginUserInfo(userInfo);
            if (null != existsUserInfo) {
                request.setAttribute(USERINFO, existsUserInfo);
            }
        }

        if (StringUtils.isNotBlank(openId)) {
            //设置到request中
            request.setAttribute(LOGIN_OPEN_ID, openId);

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
        String loginCode = cookieUtils.getQsCookieValue(request, WX_LOGIN_CODE_COOKIE_NAME);
        if (StringUtils.isBlank(loginCode)) {
            //COOKIE中没有则从URL中获取，授权跳转后则从URL参数中获取
            loginCode = request.getParameter("code");
            if (StringUtils.isNotBlank(loginCode)) {
                //设置到cookie中
                cookieUtils.setCookie(response, WX_LOGIN_CODE_COOKIE_NAME, loginCode);
            }
        }

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
            LOG.error("currentUrl:" + currentUrl);
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
