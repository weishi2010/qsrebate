package com.rebate.common.util;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
                responseBody = new String(responseBody.getBytes("ISO-8859-1"), "UTF-8");
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

    public static String httpGet(String uri) throws IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection
                .setRequestProperty(
                        "Cookie",
                        "thor=3714D9560E7F32525C81BF1A4F8D6759BCE08EAC79692F8D813E3243C465F4D15487314AC5EBD64C65F07BBEFA498FFBDF5B3F33A353186C1DBB96F2E20D79A51F6FE170D56DAF0C3F177F7B057772684D3E6AE1CE2ACD354CB57FAFCC329F459384B3BC80676D2F7404211F37415EE7BB610F9B6A3AD08D034612879747964FF062967D62120DE71386711F24520003; path=/; domain=.jd.com; HttpOnly");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "gb2312");
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
}
