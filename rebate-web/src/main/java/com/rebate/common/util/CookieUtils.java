package com.rebate.common.util;


import com.rebate.common.web.cookie.QxCookie;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CookieUtils {
    private static final Log log = LogFactory.getLog(CookieUtils.class);
    private Map<String, QxCookie> cookieMap;

    public CookieUtils() {
    }

    /**
     * 获取cookie值，解密后的cookie值
     * @param servletRequest
     * @param name
     * @return
     */
    public String getQsCookieValue(HttpServletRequest servletRequest, String name) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null && cookies.length > 0) {
            Cookie[] arr$ = cookies;
            int len$ = cookies.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Cookie cookie = arr$[i$];
                String cookieName = cookie.getName();
                if (cookieName.equals(name)) {
                    if (this.cookieMap != null && this.cookieMap.containsKey(name)) {
                        QxCookie qxCookie = (QxCookie)this.cookieMap.get(name);
                        return qxCookie.getValue(cookie.getValue());
                    }

                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 删除登录cookie，设置过期时间为0
     * @param servletResponse
     * @param name
     */
    public void deleteQsCookie(HttpServletResponse servletResponse, String name) {
        Cookie cookie;
        if (this.cookieMap != null && this.cookieMap.containsKey(name)) {
            QxCookie qxCookie = (QxCookie)this.cookieMap.get(name);
            cookie = qxCookie.newCookie((String)null);
        } else {
            cookie = new Cookie(name, (String)null);
        }

        cookie.setMaxAge(0);
        servletResponse.addCookie(cookie);
    }

    /**
     * 设置cookie
     * @param servletResponse
     * @param name
     * @param value
     */
    public void setCookie(HttpServletResponse servletResponse, String name, String value) {
        if (this.cookieMap != null && this.cookieMap.containsKey(name)) {
            QxCookie qxCookie = (QxCookie)this.cookieMap.get(name);
            Cookie cookie = qxCookie.newCookie(value);
            servletResponse.addCookie(cookie);
        } else {
            throw new RuntimeException("Cookie " + name + " is undefined!");
        }
    }

    /**
     * 设置cookieMap，配置中配置注入
     * @param axCookieList
     */
    public void setQxCookie(List<QxCookie> axCookieList) {
        if (axCookieList != null) {
            HashMap<String, QxCookie> qxCookieHashMap = new HashMap(axCookieList.size());
            Iterator i$ = axCookieList.iterator();

            while(i$.hasNext()) {
                QxCookie qxCookie = (QxCookie)i$.next();
                qxCookieHashMap.put(qxCookie.getName(), qxCookie);
            }

            this.cookieMap = qxCookieHashMap;
        }

    }

    //过期cookie失效
    public void invalidate(HttpServletRequest request, HttpServletResponse response) {
        if (this.cookieMap != null && this.cookieMap.size() > 0) {
            Iterator i$ = this.cookieMap.entrySet().iterator();

            while(i$.hasNext()) {
                Map.Entry<String, QxCookie> entry = (Map.Entry)i$.next();
                String key = (String)entry.getKey();
                QxCookie qxCookie = (QxCookie)entry.getValue();
                if (qxCookie.getExpiry() < 1 && StringUtils.isNotEmpty(this.getGlobalCookieValue(request, key))) {
                    this.deleteQsCookie(response, key);
                }
            }
        }

    }


    /**
     * 获取全局cookie
     * @param servletRequest
     * @param name
     * @return
     */
    public static String getGlobalCookieValue(HttpServletRequest servletRequest, String name) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null && cookies.length > 0) {
            Cookie[] arr$ = cookies;
            int len$ = cookies.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Cookie cookie = arr$[i$];
                String cookieName = cookie.getName();
                if (cookieName.equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 设置全局cookie
     * @param response
     * @param name
     * @param value
     * @param domain
     */
    public static void setGlobalCookie(HttpServletResponse response, String name, String value, String domain) {
        if (name != null) {
            Cookie cookie = new Cookie(name, value);
            if (domain != null && !"".equals(domain.trim())) {
                cookie.setDomain(domain);
            }

            cookie.setPath("/");
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
        }
    }
}
