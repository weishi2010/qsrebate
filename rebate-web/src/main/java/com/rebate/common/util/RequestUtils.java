package com.rebate.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestUtils {
    private static final Log log = LogFactory.getLog(RequestUtils.class);

    public RequestUtils() {
    }

    public static String getDomainUrl(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    public static String getCurrentUrl(HttpServletRequest request, String URIEncoding) {
        String queryString = request.getQueryString();
        List parameterNamesByGet = parameterNamesByGet(queryString);
        int paramSizeByGet = parameterNamesByGet.size();
        int paramTotal = request.getParameterMap().size();
        if(paramTotal == 0 && paramSizeByGet == 0) {
            return request.getRequestURL().toString();
        } else if(paramTotal > 0 && paramTotal == paramSizeByGet || paramTotal == 0 && paramSizeByGet > 0) {
            return request.getRequestURL() + "?" + queryString;
        } else {
            StringBuffer url = request.getRequestURL();
            url.append("?");
            if(paramSizeByGet > 0) {
                url.append(queryString);
            }

            String afterConvert = covertToGet(parameterNamesByGet, request.getParameterMap(), URIEncoding);
            if(afterConvert.trim().length() > 0) {
                url.append("&").append(afterConvert);
            }

            return url.toString();
        }
    }

    private static String covertToGet(Collection<String> parameterNamesByGet, Map<String, String[]> parameterMap, String URIEncoding) {
        if(parameterMap.size() == 0) {
            return "";
        } else {
            StringBuffer postParams = new StringBuffer();
            Set parameterNameSet = parameterMap.keySet();
            Iterator it = parameterNameSet.iterator();

            while(it.hasNext()) {
                String key = (String)it.next();
                if(!parameterNamesByGet.contains(key)) {
                    postParams.append(key).append("=").append(encode(((String[])parameterMap.get(key))[0], URIEncoding)).append("&");
                }
            }

            return postParams.toString().substring(0, postParams.length() - 1);
        }
    }

    private static List<String> parameterNamesByGet(String queryString) {
        ArrayList parameterNames = new ArrayList();
        if(StringUtils.isNotEmpty(queryString)) {
            String[] params = queryString.split("&");

            for(int i = 0; i < params.length; ++i) {
                if(params[i].contains("=")) {
                    parameterNames.add(params[i].split("=")[0]);
                }
            }
        }

        return parameterNames;
    }

    public static String encode(String value, String charset) {
        if(value != null && value.length() != 0) {
            try {
                return URLEncoder.encode(value, charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException(var3.getMessage(), var3);
            }
        } else {
            return "";
        }
    }

    public static String decode(String value, String charset) {
        if(value != null && value.length() != 0) {
            try {
                return URLDecoder.decode(value, charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException(var3.getMessage(), var3);
            }
        } else {
            return "";
        }
    }

    public static void clearCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String path, String domain) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0) {
            Cookie[] arr$ = cookies;
            int len$ = cookies.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Cookie cookie = arr$[i$];
                if(cookie.getName().equals(cookieName)) {
                    cookie.setMaxAge(0);
                    cookie.setValue((String)null);
                    cookie.setPath(path);
                    cookie.setDomain(domain);
                    response.addCookie(cookie);
                    break;
                }
            }
        }

    }

    public static String getRemoteIp(HttpServletRequest request) {
        if(request == null) {
            return null;
        } else {
            String ip = request.getHeader("x-forwarded-for");
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            if(StringUtils.isNotEmpty(ip) && ip.contains(",")) {
                ip = ip.substring(0, ip.indexOf(","));
            }

            return ip != null?ip.split(":")[0]:null;
        }
    }

    public static Cookie setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, String path, String domain) {
        log.debug("CookieUtils.setCookie " + name + ":" + value);
        response.setHeader("P3P", "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        if(path != null) {
            cookie.setPath(path);
        }

        cookie.setDomain(domain);
        response.addCookie(cookie);
        return cookie;
    }

    public static String getRootDomain(HttpServletRequest request) {
        String hostStr = request.getServerName();
        String realDomain = null;
        String[] arrayDomain = null;
        if(null != hostStr) {
            hostStr = hostStr.toLowerCase();
            arrayDomain = hostStr.split("\\.");
            int arrayNum = arrayDomain.length - 1;
            if(arrayNum > 0) {
                String end = arrayDomain[arrayNum];
                String main = arrayDomain[arrayNum - 1];
                if(end.equals("cn")) {
                    if(!"com".equals(main) && !"net".equals(main) && !"edu".equals(main) && !"org".equals(main) && !"gov".equals(main)) {
                        realDomain = main + "." + end;
                    } else {
                        realDomain = arrayDomain[arrayNum - 2] + "." + main + "." + end;
                    }
                } else {
                    realDomain = main + "." + end;
                }
            }
        }

        return realDomain;
    }
}
