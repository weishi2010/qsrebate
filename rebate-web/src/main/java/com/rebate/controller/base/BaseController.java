package com.rebate.controller.base;

import com.rebate.common.util.CookieUtils;
import com.rebate.common.util.JsonUtil;
import com.rebate.domain.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BaseController implements MessageSourceAware {
    private static final Log LOG = LogFactory.getLog(BaseController.class);

    /**
     * 登录后微信用户openId
     */
    private static String LOGIN_OPEN_ID = "wx_openId";
    /**
     * 用户信息
     */
    private static String USERINFO = "userInfo";

    /**
     * 用户信息COOKIE
     */
    private static String USERINFO_COOKIE = "qs_u_i_o";

    @Qualifier("cookieUtils")
    @Autowired(required = false)
    private CookieUtils cookieUtils;


    private MessageSource messageSource;

    public BaseController() {
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    protected String getMessage(String code, Object[] args, Locale locale) {
        return this.messageSource.getMessage(code, args, locale);
    }

    protected void addActionError(String text, ModelMap context) {
        List<String> actionMessages = (List) context.get("actionMessages");
        if (actionMessages == null) {
            actionMessages = new ArrayList();
            ((List) actionMessages).add(text);
        }

        context.put("actionMessages", actionMessages);
    }

    protected void addActionMessage(String text, ModelMap context) {
        List<String> actionErrors = (List) context.get("actionErrors");
        if (actionErrors == null) {
            actionErrors = new ArrayList();
            ((List) actionErrors).add(text);
        }

        context.put("actionErrors", actionErrors);
    }

    /**
     * 获取WX openId
     * @param request
     * @return
     */
    public String getWxOpenId(HttpServletRequest request){
        return (String)request.getAttribute(LOGIN_OPEN_ID);
    }

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    public UserInfo getUserInfo(HttpServletRequest request){
        UserInfo userInfo = (UserInfo)request.getAttribute(USERINFO);
        if(null!=userInfo){
            return userInfo;
        }

        String userInfoCookieValue = cookieUtils.getQsCookieValue(request, USERINFO_COOKIE);
        LOG.error("[BaseController]userInfoCookieValue:"+userInfoCookieValue);
        if (StringUtils.isNotBlank(userInfoCookieValue)) {
            userInfo = JsonUtil.fromJson(userInfoCookieValue, UserInfo.class);
        }
        return userInfo;
    }
}
