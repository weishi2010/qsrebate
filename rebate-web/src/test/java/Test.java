import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RegexUtils;
import com.rebate.domain.ProductCoupon;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    private static final TypeReference<List<ProductCoupon>> productCouponTypeReference = new TypeReference<List<ProductCoupon>>() {
    };

    public static String get(String oriUrl) throws Exception {
       URL url = new URL(oriUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.215 Safari/535.1");
        connection.setRequestProperty("accept-language", "zh-CN");
        connection.setConnectTimeout(5 * 1000);//5秒的链接超时
        connection.setReadTimeout(5 * 1000);//设置从主机读取数据超时（单位：毫秒）
        connection.setInstanceFollowRedirects(false);

        System.out.println(connection.getURL().toString()+" ----------------------------url");
        int code = connection.getResponseCode();
        return connection.getURL().toString();
    }

    /**
     * 获取重定向地址
     * @param path
     * @return
     * @throws Exception
     */
    public static String getRedirectUrl(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(path)
                .openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);
        return conn.getHeaderField("Location");
    }

    public static String get1(String oriUrl){
        return HttpClientUtil.getFinalURL(oriUrl);
    }

    public static void main(String[] args) throws Exception {
        String oriUrl = "http://t.cn/Rl216A1";

        String url = HttpClientUtil.getFinalURL(oriUrl);
        System.out.println("url:"+url);
        String html = HttpClientUtil.get(url);

        List<String> links = RegexUtils.getLinks(html);
        for(String link:links){
            System.out.println(link.trim().replace("\'",""));
            if(StringUtils.isNotBlank(link) && link.contains("coupon")){
            }
        }

//        HttpClientDemo.getRedirectInfo();
    }
}
