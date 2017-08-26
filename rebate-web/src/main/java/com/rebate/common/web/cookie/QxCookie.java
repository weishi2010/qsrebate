package com.rebate.common.web.cookie;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;

public class QxCookie {
//
        private CookieCipherTools cookieCipherTools;
        private String name;
        private String domain;
        private String path;
        private int expiry;
        private String key;
        private boolean encrypt;

        public QxCookie() {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDomain() {
            return this.domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getExpiry() {
            return this.expiry;
        }

        public void setExpiry(int expiry) {
            this.expiry = expiry;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isEncrypt() {
            return this.encrypt;
        }

        public void setEncrypt(boolean encrypt) {
            this.encrypt = encrypt;
        }

        public void setCookieCipherTools(CookieCipherTools cookieCipherTools) {
            this.cookieCipherTools = cookieCipherTools;
        }

        public Cookie newCookie(String value) {
            String newValue;
            if (!StringUtils.isEmpty(value)) {
                newValue = this.isEncrypt() ? this.cookieCipherTools.encrypt(value, this.getKey()) : value;
            } else {
                newValue = value;
            }

            Cookie cookie = new Cookie(this.name, newValue);
            if (!StringUtils.isBlank(this.domain)) {
                cookie.setDomain(this.domain);
            }

            if (!StringUtils.isBlank(this.path)) {
                cookie.setPath(this.path);
            }

            if (this.expiry > 0) {
                cookie.setMaxAge(this.expiry);
            }

            return cookie;
        }

        public String getValue(String value) {
            if (!StringUtils.isEmpty(value)) {
                return this.isEncrypt() ? this.cookieCipherTools.decrypt(value, this.getKey()) : value;
            } else {
                return value;
            }
        }
    }
