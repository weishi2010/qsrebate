import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RegexUtils;
import com.rebate.common.util.des.DESUtil;
import com.rebate.domain.ProductCoupon;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getFinal(String oriUrl){
        String urlStr = oriUrl;
        try {
            URL url = new URL(oriUrl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.getResponseCode();
            urlStr=conn.getURL().toString();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlStr;
    }


    public static void main(String[] args) throws Exception {
        String url ="https://w.url.cn/s/AZeaQyw";
        System.out.println(HttpClientUtil.convertJDPromotionUrl(url));


        String content = "京东双11全部卖场的活动，全部卖场一键直达，卖场和商品下方都自带，优惠券的，记得爬楼看活动 ---------------------- 3分钟100亿 1.主会场，全品争锋，万千好货5折起！手机11.11元秒！ https://w.url.cn/s/ArUBHlD 2.家电会场，家电排行榜，价保30天！抢1000元神券！ https://w.url.cn/s/Af2VGYp 3.手机会场，潮流新phone爆，当燃京东！iPhone X现货抢！ https://w.url.cn/s/AmypdbX 4.什么值得买，京选会场！全场低至9.9元包邮！ https://w.url.cn/s/ARzilMB 5.数码会场，iPad Pro低至3688！ https://w.url.cn/s/AIE8ZwQ 6.居家生活，0点抢5折神券！跨店3件7折！自营199-100！https://w.url.cn/s/Aj3sSat 7.国际大牌品质购！满199减100！ https://w.url.cn/s/ASdWgGv 8.潮流服饰，5折神券嗨翻天，自营满199-100！ https://w.url.cn/s/AErXPrR 9.自营图书，满200-100！最高满300-180！ https://w.url.cn/s/Ak10JlZ --------------------- 【京东双十一狂欢11日领券汇总】 https://w.url.cn/s/ANfIMpC 京东双11领券大全各种免邮券，全品券速领数量有限 https://w.url.cn/s/AxR1JkI 超市全品188-100、699-200、499-150、299-90、99-30神券叠加满减 https://w.url.cn/s/AvR5OWJ 纸品清洁188-100神券叠加满减后神价 https://w.url.cn/s/ATEkmEU 食品饮料188-69、299-100 https://w.url.cn/s/Ae28gr9 母婴748-300、348-100、299-200神券 https://w.url.cn/s/AU5y9L9 全球购纸尿裤满900-300神券 https://w.url.cn/s/AgRdCUR 图书300-180 https://w.url.cn/s/AgN31gi 双11家电爆品排行榜 https://w.url.cn/s/AMToiIr 京东家电疯抢24小时 https://w.url.cn/s/A9Kv0zb 手机全品6000-1111、4980-700神券 https://w.url.cn/s/AmyOvBr 服饰五折神券可下黄金600-150服饰神券 https://w.url.cn/s/AZeaQyw 长按-发送给朋友分享给更多的人！";
        List<String> list = RegexUtils.getLinks(content);
        for (String link : list) {
            String clink = HttpClientUtil.convertJDPromotionUrl(link);
            if (StringUtils.isNotBlank(clink)) {
                System.out.println(link+"----"+clink);
            }
        }
        System.out.println(content);
    }

}
