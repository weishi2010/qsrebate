package com.rebate.common.util;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpClientUtil {
    private final static Logger LOG = Logger.getLogger(HttpClientUtil.class);

    private HttpClientUtil() {
    }

    private static final Logger logger = Logger.getLogger(HttpClientUtil.class);

    public static JSONObject getWithJsonObj(String url, Map<String, String> params) {
        return get(url, params, "utf-8");
    }

    /**
     * 发送GET请求
     *
     * @param url    getURL
     * @param params 请求参数
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        String responseBody = null;

        HttpClient httpCilent = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("Content-Type", "text/html; charset=GBK");

        try {
            int index = 0;
            if (params != null && params.size() > 0) {
                NameValuePair[] param = new NameValuePair[params.size()];
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    param[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }
                getMethod.setQueryString(param);
            }
            httpCilent.executeMethod(getMethod);
            if (getMethod.getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_OK) {
                responseBody = getMethod.getResponseBodyAsString();
            }
        } catch (Exception e) {
            LOG.error("get请求提交失败:" + url, e);
        } finally {
            getMethod.releaseConnection();
        }

        return responseBody;
    }


    /**
     * 发送GET请求
     *
     * @param url     getURL
     * @param params  请求参数
     * @param charset 编码
     * @return
     */
    public static JSONObject get(String url, Map<String, String> params, String charset) {
        LOG.info("params = " + params);
        JSONObject jsonResult = null;

        HttpClient httpCilent = new HttpClient();
        GetMethod getMethod = new GetMethod(url);

        try {
            int index = 0;
            if (params != null && params.size() > 0) {
                NameValuePair[] param = new NameValuePair[params.size()];
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    param[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }
                getMethod.setQueryString(param);
            }
            httpCilent.executeMethod(getMethod);
            if (getMethod.getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_OK) {
                String responseBody = getMethod.getResponseBodyAsString();
                jsonResult = JSONObject.fromObject(responseBody);
            }
        } catch (Exception e) {
            LOG.error("get请求提交失败:" + url, e);
        } finally {
            getMethod.releaseConnection();
        }

        LOG.info("jsonResult = " + jsonResult);
        return jsonResult;
    }

    public static String get(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        String result = null;
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(get);
            HttpEntity entity1 = response1.getEntity();
            result = EntityUtils.toString(entity1);
        } catch (Exception e) {
            logger.error(e.getMessage() + ",url:" + url + ",html=====" + result, e);
        } finally {
            try {
                if (response1 != null) {
                    response1.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public static String getJdUrl(String uri,String loginCookie) throws IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("referer", "http://media.jd.com");
        connection
                .setRequestProperty(
                        "Cookie",
                        "thor="+loginCookie+"; path=/; domain=.jd.com; HttpOnly");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "gb2312");
        out.write("DF4A0ED346195BD2A969439ABFA0D1016F257513A205B84A54868E573B84CAC859B77494C24DED7996FD9CABBAE1978EA58E16F2954481B746556E453BE16A2AF9CD3E5E3FCB46EC7BD5D3B9C9A6DB4B01F694EA08FD50BD757CC5FECF70C2903699A5E6D3BAC4D05A8B05601954A720F7ED32DDD0BA2051BD90514C7B233F4C8CEB95238D9C2084BD20FB929C7BEBEF");
        out.flush();
        out.close();
        String sCurrentLine;
        String sTotalString;
        sCurrentLine = "";
        sTotalString = "";
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
        while ((sCurrentLine = l_reader.readLine()) != null) {
            sTotalString += sCurrentLine;

        }

        return sTotalString;
    }

    public static String getJdUrl(String uri) throws IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("referer", "http://media.jd.com");
        connection
                .setRequestProperty(
                        "Cookie",
                        "thor=DF4A0ED346195BD2A969439ABFA0D1016F257513A205B84A54868E573B84CAC859B77494C24DED7996FD9CABBAE1978EA58E16F2954481B746556E453BE16A2AF9CD3E5E3FCB46EC7BD5D3B9C9A6DB4B01F694EA08FD50BD757CC5FECF70C2903699A5E6D3BAC4D05A8B05601954A720F7ED32DDD0BA2051BD90514C7B233F4C8CEB95238D9C2084BD20FB929C7BEBEF; path=/; domain=.jd.com; HttpOnly");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "gb2312");
        out.write("DF4A0ED346195BD2A969439ABFA0D1016F257513A205B84A54868E573B84CAC859B77494C24DED7996FD9CABBAE1978EA58E16F2954481B746556E453BE16A2AF9CD3E5E3FCB46EC7BD5D3B9C9A6DB4B01F694EA08FD50BD757CC5FECF70C2903699A5E6D3BAC4D05A8B05601954A720F7ED32DDD0BA2051BD90514C7B233F4C8CEB95238D9C2084BD20FB929C7BEBEF");
        out.flush();
        out.close();
        String sCurrentLine;
        String sTotalString;
        sCurrentLine = "";
        sTotalString = "";
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
        while ((sCurrentLine = l_reader.readLine()) != null) {
            sTotalString += sCurrentLine;

        }

        return sTotalString;
    }

    public static String post(String url, Map data) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        String result = null;
        try {
            StringEntity s = new StringEntity(JsonUtil.toJson(data));
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            CloseableHttpResponse res = httpclient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(res.getEntity());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static String post(String url, String paramJson,String charset) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset="+charset);
        String result = null;
        try {
            StringEntity s = new StringEntity(paramJson);
            s.setContentEncoding(charset);
            s.setContentType("application/json");
            post.setEntity(s);
            CloseableHttpResponse res = httpclient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(res.getEntity());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 获得最终的地址（包括301或者302等跳转后的地址）
     *
     * @param from 原始地址
     * @return 最终的地址
     */

    public static String getFinalURL(String from) {

        String to = "";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(from);
        HttpParams params = client.getParams();
        params.setParameter(AllClientPNames.HANDLE_REDIRECTS, false);
        try {
            HttpResponse response = client.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 301 || statusCode == 302) {
                Header[] hs = response.getHeaders("Location");
                for (Header h : hs) {
                    to = h.getValue();
                }
            }
        } catch (Exception e) {
            to = "";
        }
        return to;
    }
}
