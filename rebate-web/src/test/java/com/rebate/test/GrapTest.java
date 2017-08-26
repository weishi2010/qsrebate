package com.rebate.test;

import com.rebate.common.util.GrabUtils;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.JdMediaProductGrapUtil;
import com.rebate.domain.Product;
import net.sf.json.JSON;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by weishi on 2017/7/15.
 */
public class GrapTest {

    public static void grabComments() {
        Long skuId = 10979457620l;
        String url = "https://club.jd.com/comment/skuProductPageComments.action?productId=" + skuId + "&score=0&sortType=5&page=0&pageSize=10&isShadowSku=0&fold=1";
        String json = HttpClientUtil.get(url);
        Map rootMap = JsonUtil.fromJson(json, Map.class);
        List<Map> list = (List) rootMap.get("comments");
        System.out.println("comments:" + list.size());
        for (Map map : list) {
            System.out.println("comment:" + JsonUtil.toJson(map));
        }
    }
    public static void grabProduct()   {
        Long skuId = 10979457620l;
        String url = "https://media.jd.com/gotoadv/goods?pageSize=50&pageIndex=1";
        String content = null;
        try {
            content = HttpClientUtil.httpGet(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("content:"+content);
    }
    public static void main(String[] args) throws Exception {
        List<Product> list = JdMediaProductGrapUtil.grabProducts(1, 10);
        System.out.println("list:"+ JsonUtil.toJson(list));
    }
}
