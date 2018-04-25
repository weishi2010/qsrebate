package com.rebate.common.util;

import com.google.common.base.Joiner;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String grabBOSSUrl(String uri, String cookie) throws IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("referer", "http://media.jd.com");
        connection
                .setRequestProperty(
                        "Cookie",
                        "t=" + cookie + "; path=/; domain=.zhipin.com; HttpOnly");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
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

    public static String getJdUrl(String uri, String loginCookie) throws IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("referer", "http://media.jd.com");
        connection
                .setRequestProperty(
                        "Cookie",
                        "thor=" + loginCookie + "; path=/; domain=.jd.com; HttpOnly");
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

    public static String post(String url, String paramJson, String charset) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset=" + charset);
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

    /**
     * 过滤JD联盟参数
     *
     * @param url
     * @return
     */
    public static String filterJDUnionParam(String url) {
        List<String> params = new ArrayList<>();
        //获取重定向后的URL
        if (StringUtils.isNotBlank(url)) {
            String[] urlArray = url.split("\\?");
            String domainStr = urlArray[0];
            if (urlArray.length > 1) {
                String paramStr = urlArray[1];
                String[] paramArray = paramStr.split("\\&");
                for (String param : paramArray) {
                    if (!param.contains("utm")) {
                        params.add(param);
                    }
                }
            }
            if (params.size() > 0) {
                url = domainStr + "?" + Joiner.on("&").join(params);
            } else {
                url = domainStr;
            }

        }
        return url;
    }

    /**
     * 使用递归的方式进行JD多重跳转活动链接解析
     *
     * @param url
     * @return
     */
    public static String convertJDPromotionUrl(String url) {

        if (StringUtils.isNotBlank(url) && !url.contains("sale.jd.com") && !RegexUtils.isJDCouponUrl(url)) {
            String oriUrl = HttpClientUtil.getFinalURL(url);
            if (StringUtils.isBlank(oriUrl) || oriUrl.contains("union-click.jd.com")) {
                //如果获取不到原始URl则再解析HTML看是否还有跳转URL
                String html = HttpClientUtil.get(url);
                if (StringUtils.isNotBlank(html) && html.contains("hrl")) {
                    List<String> links = getJDPromotionLinks(html);
                    if (null != links && links.size() > 0) {
                        url = convertJDPromotionUrl(links.get(0));
                    }
                }
            } else if (oriUrl.contains("yiqifa")) {
                //如果获取不到原始URl则再解析HTML看是否还有跳转URL
                String html = HttpClientUtil.get(url);
                if (StringUtils.isNotBlank(html) && html.contains("funtz")) {
                    List<String> links = getYiqifaLinks(html);
                    if (null != links && links.size() > 0) {
                        url = convertJDPromotionUrl(links.get(0));
                    }
                }
            } else if (oriUrl.contains("qingsongfan")) {
                url = convertJDPromotionUrl(oriUrl);
            }else if(oriUrl.contains("/user/login.action")){
                //如果是京东m端登录则从returnUrl中获取登录跳转后URL作为链接返回
                try {
                    String[] array = oriUrl.split("returnurl=");
                    String returnUrl = array[1];

                    if(returnUrl.indexOf("&")>0){
                        returnUrl = returnUrl.substring(0,returnUrl.indexOf("&"));
                    }
                    url = URLDecoder.decode(returnUrl, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    LOG.error("[plogin.m.jd.com]check url is error!oriUrl:"+oriUrl,e);
                    url = oriUrl;
                }
            } else if (StringUtils.isNotBlank(oriUrl) && !oriUrl.contains("error2.aspx")) {
                url = oriUrl;
            }
        }
        url = filterJDUnionParam(url);
        return url;
    }

    /**
     * 从html中解析出活动链接
     *
     * @param html
     * @return
     */
    public static List<String> getJDPromotionLinks(String html) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern
                .compile("\\s*(?i)hrl\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            urls.add(matcher.group(0).replace("hrl=", "").replace("\'", "").trim());
        }
        return urls;
    }

    public static List<String> getYiqifaLinks(String html) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern
                .compile("\\s*(?i)u\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            urls.add(matcher.group(0).replace("u=", "").replace("\'", "").trim());
        }
        return urls;
    }

    public static String uploadImage(String uploadUrl,byte[] fileBytes,String fileName) {

        String result = null;
        try {
            URL url = new URL(uploadUrl);

            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false); // post方式不能使用缓存
            // 设置请求头信息
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");

            // 设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();

            con.setRequestProperty("Content-Type", "multipart/form-data; boundary="  + BOUNDARY);


            // 请求正文信息
            // 第一部分：
            StringBuilder sb = new StringBuilder();

            sb.append("--"); // 必须多两道线
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + fileBytes.length + "\";filename=\""
                    + fileName + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            byte[] head = sb.toString().getBytes("utf-8");
            // 获得输出流
            OutputStream out = new DataOutputStream(con.getOutputStream());

            // 输出表头

            out.write(head);

            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            out.write(fileBytes, 0, fileBytes.length);
            // 结尾部分
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = null;
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
            throw new RuntimeException("数据读取异常");

        } finally {
        }
        return result;
    }

    public static byte[] downloadImage(String imgUrl){
        byte[] imgByte =  new byte[1024];
        try{
            //new一个URL对象
            URL url = new URL(imgUrl);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=inStream.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            //把outStream里的数据写入内存
            imgByte = outStream.toByteArray();

        }catch (Exception e){
            LOG.error("downloadImage error!",e);
        }
        return imgByte;
    }
}
