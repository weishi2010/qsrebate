import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.RegexUtils;
import com.rebate.common.util.des.DESUtil;
import com.rebate.domain.ProductCoupon;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;

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

    public static String get1(String oriUrl){
        return HttpClientUtil.getFinalURL(oriUrl);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(DESUtil.decrypt("QIUSNHYAGHSNEIJAMYEFHQLRIHFADCHE", "ZoonL5QSpRTcUbETYotw==".replace(" ",""), "UTF-8"));
//        String content = "[表情] 全站促销&神券导航一键直达，欢迎收藏转发。\n" +
//                "\uD83C\uDF96 超市满199-100：http://t.cn/Rl2QBKM\n" +
//                "\uD83C\uDF96 数码产品满千减百：http://t.cn/Rl28KhY\n" +
//                "\uD83C\uDF96 电脑满3000减1000：http://t.cn/Rl28uah\n" +
//                "\uD83C\uDF96 苹果8下单立减1111：http://t.cn/Rl2RvpP\n" +
//                "\uD83C\uDF96 自营家电五折秒杀：http://t.cn/Rl2RUwv\n" +
//                "\uD83C\uDF96 居家好物1.1元起：http://t.cn/Rl2RfIu\n" +
//                "\uD83C\uDF96 建材家具厨具11.1元起：http://t.cn/Rl2RCPi\n" +
//                "\uD83C\uDF96 天天1元秒生鲜http://t.cn/Rl2RHyM\n" +
//                "\uD83C\uDF96 服装箱包1折秒杀：http://t.cn/Rl2RBDK\n" +
//                "\uD83C\uDF96 运动服鞋五折秒：http://t.cn/Rl2EPiX\n" +
//                "\uD83C\uDF96 童装童鞋三件7折：http://t.cn/Rl2EUsV\n" +
//                "\uD83C\uDF96 图书每满100-50：http://t.cn/Rl2EMdh\n" +
//                "\uD83C\uDF96 母婴爆款低至五折：http://t.cn/Rl2EppK\n" +
//                "\uD83C\uDF96 中华老字号超值秒杀：http://t.cn/Rl2EQdR\n" +
//                "\uD83C\uDF96 宠物商品买三免一：http://t.cn/Rl2EdO6\n" +
//                "\uD83C\uDF96 医药保健超级秒：http://t.cn/Rl2nAc3\n" +
//                "\uD83C\uDF96 自营车品满199-100：http://t.cn/Rl2ntvE\n" +
//                "\uD83C\uDF96 全球购精品秒杀：http://t.cn/Rl2nXlk\n" +
//                "\uD83C\uDF96 智能商品五折限量秒：http://t.cn/Rl2nlAE\n" +
//                "[表情] 京东11.11权威发布！全站促销&神券导航一键直达，欢迎收藏转发。\n" +
//                "\uD83C\uDF96  大牌钟表限时秒：http://t.cn/Rl2mJd1\n" +
//                "\uD83C\uDF96 品牌特卖专区：http://t.cn/Rl2mjhm\n" +
//                "\uD83C\uDF96 快速找货直接搜索：http://t.cn/Rl2mrwL\n" +
//                "\uD83C\uDF96 京东秒杀频道实时秒杀：http://t.cn/Rl2u7CT\n" +
//                "\uD83C\uDF96 11.11折扣活动大全：http://t.cn/Rl2utbc\n" +
//                "\uD83C\uDF96 京东拼购，1元拼好货：http://t.cn/Rl2uOUw\n" +
//                "\uD83C\uDFC5 点我天天领神券：http://t.cn/Rl2uu2s\n" +
//                "\uD83C\uDFC5 礼品频道大促直达：http://t.cn/Rl23zaH\n" +
//                "\uD83C\uDFC5 京东实时热卖榜，选择困难症患者点这里：http://t.cn/Rl23Vv7\n" +
//                "\uD83C\uDFC5 满3000- 211全品券：http://t.cn/Rl21yUq\n" +
//                "\uD83C\uDFC5 新plus会员领服饰600-150神券（可下黄金）：http://t.cn/Rl216A1\n" +
//                "\uD83C\uDFC5  iPhone 800元神券：http://t.cn/Rl21Wvq\n" +
//                "\uD83C\uDFC5 美赞臣好运抽奖：http://t.cn/Rl213b3";
//        List<String> list = RegexUtils.getLinks(content);
//        for (String link : list) {
//            String clink = HttpClientUtil.convertJDPromotionUrl(link);
//            if (StringUtils.isNotBlank(clink)) {
//                content = content.replace(link, clink);
//            }
//        }
//        System.out.println(content);
    }

}
