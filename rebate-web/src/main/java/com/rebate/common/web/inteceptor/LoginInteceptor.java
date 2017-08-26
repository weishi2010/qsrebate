//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rebate.common.web.inteceptor;

import com.rebate.common.util.RequestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LoginInteceptor extends HandlerInterceptorAdapter {
    public static final long THIRTY_DAYS = 2592000000L;
    public static final long CHECK_INTEVELS = 180000L;
    private static final Log log = LogFactory.getLog(LoginInteceptor.class);
    protected String authenticationKey;
    protected String cookieName;
    protected String loginUrl;
    protected Set<String> everNeedLoginPaths;
    protected Set<String> unLoginPaths;
    protected String appName;
    protected String ajaxModel;
    protected String charsetName = "gbk";
    protected String URIEncoding = "gbk";
    protected boolean needRedirect = true;
    protected boolean needParse = false;
    protected String domain = null;
    protected boolean expiredLog = true;
    protected boolean needRemote = true;
    protected boolean needVisit = true;
    protected boolean needAnnotation = false;
    protected int remoteCheckIntevalMs = 0;
    protected int crypt = 0;
    protected Set<String> flashUploadMethodName;
    protected String paramCookieName = "loginCookie";

    public LoginInteceptor() {
    }


    public boolean needLogin(String path) {
        if(!this.needRedirect) {
            return Boolean.FALSE.booleanValue();
        } else if(!StringUtils.isEmpty(path) && this.unLoginPaths != null && this.unLoginPaths.size() != 0) {
            String halfPath = "";
            int lastIndex = path.lastIndexOf(".");
            if(lastIndex > -1) {
                halfPath = path.substring(0, lastIndex);
            }

            Iterator i$;
            String perPath;
            if(this.everNeedLoginPaths != null && this.everNeedLoginPaths.size() > 0) {
                i$ = this.everNeedLoginPaths.iterator();

                while(i$.hasNext()) {
                    perPath = (String)i$.next();
                    if(halfPath.equals(perPath)) {
                        return Boolean.TRUE.booleanValue();
                    }
                }
            }

            i$ = this.unLoginPaths.iterator();

            do {
                if(!i$.hasNext()) {
                    return Boolean.TRUE.booleanValue();
                }

                perPath = (String)i$.next();
            } while(!path.startsWith(perPath));

            return Boolean.FALSE.booleanValue();
        } else {
            return Boolean.TRUE.booleanValue();
        }
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        boolean isAjaxReuest = false;
        if(this.ajaxModel == null) {
            if(request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
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
            log.error("--ajaxResponse error--", var12);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (Exception var11) {
                    log.error("--ajaxResponse close writer error--", var11);
                }
            }

        }

    }

    public void redirect2LoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(this.isAjaxRequest(request)) {
            this.ajaxResponse(response);
        } else {
            String currentUrl = RequestUtils.getCurrentUrl(request, this.URIEncoding);
            String encodeCurrentUrl = "";

            try {
                encodeCurrentUrl = RequestUtils.encode(currentUrl, this.charsetName);
            } catch (Exception var6) {
                log.error("RequestUtils.encodeCurrentUrl error!!" + currentUrl + ":" + this.charsetName, var6);
            }

            String log = this.loginUrl +"?redirectUrl="+ encodeCurrentUrl;
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.sendRedirect(log);
        }
    }

    public boolean isUnLoginPath(String path) {
        return !this.needLogin(path);
    }

    public void setUnLoginPaths(Set<String> unLoginPaths) {
        this.unLoginPaths = unLoginPaths;
    }

    public void setEverNeedLoginPaths(Set<String> everNeedLoginPaths) {
        this.everNeedLoginPaths = everNeedLoginPaths;
    }

    public void addUnLoginPath(String path) {
        if(this.unLoginPaths == null) {
            this.unLoginPaths = new HashSet();
        }

        this.unLoginPaths.add(path);
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public void setAuthenticationKeyLocation(String authenticationKeyLocation) {
        File authenticationKeyFile = new File(authenticationKeyLocation);
        if(authenticationKeyFile.exists()) {
            String code = null;

            try {
                code = FileUtils.readFileToString(authenticationKeyFile);
            } catch (IOException var5) {
                throw new RuntimeException("read authenticationKeyLocation content error!!!", var5);
            }

            if(StringUtils.isNotEmpty(code)) {
                this.authenticationKey = code;
            } else {
                throw new RuntimeException("authenticationKeyLocation content is empty!!!");
            }
        } else {
            throw new RuntimeException("authenticationKeyLocation is not exist!!!");
        }
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCharsetName() {
        return this.charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public boolean isNeedRedirect() {
        return this.needRedirect;
    }

    public void setNeedRedirect(boolean needRedirect) {
        this.needRedirect = needRedirect;
    }

    public void setNeedParse(boolean needParse) {
        this.needParse = needParse;
    }

    public void setNeedRemote(boolean needRemote) {
        this.needRemote = needRemote;
    }

    public String getDomain() {
        return StringUtils.isNotEmpty(this.domain)?this.domain:".360buy.com";
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isExpiredLog() {
        return this.expiredLog;
    }

    public void setExpiredLog(boolean expiredLog) {
        this.expiredLog = expiredLog;
    }

    public boolean isNeedAnnotation() {
        return this.needAnnotation;
    }

    public void setNeedAnnotation(boolean needAnnotation) {
        this.needAnnotation = needAnnotation;
    }

    public boolean isNeedVisit() {
        return this.needVisit;
    }

    public void setNeedVisit(boolean needVisit) {
        this.needVisit = needVisit;
    }

    protected boolean isOpenRemoteCheck() {
        this.printLog("开启远程session校验，默认模式");
        return true;
    }

    protected boolean isOpenLog() {
        return false;
    }

    protected void printLog(String logText) {
        if(this.isOpenLog()) {
            log.error(logText);
        }

    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public Set<String> getEverNeedLoginPaths() {
        return everNeedLoginPaths;
    }

    public Set<String> getUnLoginPaths() {
        return unLoginPaths;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAjaxModel() {
        return ajaxModel;
    }

    public void setAjaxModel(String ajaxModel) {
        this.ajaxModel = ajaxModel;
    }

    public String getURIEncoding() {
        return URIEncoding;
    }

    public void setURIEncoding(String URIEncoding) {
        this.URIEncoding = URIEncoding;
    }

    public boolean isNeedParse() {
        return needParse;
    }

    public boolean isNeedRemote() {
        return needRemote;
    }

    public int getRemoteCheckIntevalMs() {
        return remoteCheckIntevalMs;
    }

    public void setRemoteCheckIntevalMs(int remoteCheckIntevalMs) {
        this.remoteCheckIntevalMs = remoteCheckIntevalMs;
    }

    public int getCrypt() {
        return crypt;
    }

    public void setCrypt(int crypt) {
        this.crypt = crypt;
    }

    public Set<String> getFlashUploadMethodName() {
        return flashUploadMethodName;
    }

    public void setFlashUploadMethodName(Set<String> flashUploadMethodName) {
        this.flashUploadMethodName = flashUploadMethodName;
    }

    public String getParamCookieName() {
        return paramCookieName;
    }

    public void setParamCookieName(String paramCookieName) {
        this.paramCookieName = paramCookieName;
    }
}
