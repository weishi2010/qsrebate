package com.rebate.common.util.rebate;

import com.rebate.common.util.GrabUtils;
import com.rebate.common.util.HttpClientUtil;
import com.rebate.domain.Product;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdMediaProductGrapUtil {
    private static final Log LOG = LogFactory.getLog(JdMediaProductGrapUtil.class);
    private final static String JD_MEDIA_URL = "https://media.jd.com/gotoadv/goods";
    private final static String PRODUCT_ROOT_XPATH = "/html/body/div[2]/form/div[2]/div/div/div/div[2]/ul/li";
    private final static String PRDUCT_NAME_XPATH = "div[1]/div[2]/a";
    private final static String PRDUCT_IMG_XPATH = "div[1]/div[1]/a/img";
    private final static String COMMISSION_RATE_XPATH = "div[1]/div[2]/div[2]/div[1]/em";
    private final static String COMMISSION_XPATH = "div[1]/div[2]/div[2]/div[3]/em";
    private final static String ORIGINAL_PRICE_XPATH = "div[1]/div[2]/div[1]/span[1]/span";


    public static List<Product> grabProducts(int page, int pageSize) {
        String url = JD_MEDIA_URL + "?pageSize=" + pageSize + "&pageIndex=" + page;
        HtmlCleaner cleaner = new HtmlCleaner();
        List<Product> list = new ArrayList<Product>();
        try {

            TagNode rootNode = cleaner.clean(HttpClientUtil.httpGet(url));
            CleanerProperties props = new CleanerProperties();
            org.w3c.dom.Document doc = new DomSerializer(props)
                    .createDOM(rootNode);
            Document dom4j = new DOMReader().read(doc);
            Element rootElement = dom4j.getRootElement();
            List<Element> elements = GrabUtils.evaluatesXPath2(rootElement, PRODUCT_ROOT_XPATH);
            for (Element element : elements) {
                Product product = new Product();
                //商品编号
                Attribute attr = element.attribute("skuid");
                Long skuId = Long.parseLong(attr.getValue());
                product.setProductId(skuId);
                //商品名称
                String productName = GrabUtils.getValueByEvaluateXPath2(element, PRDUCT_NAME_XPATH);
                product.setName(productName);

                //商品图片
                List<Element> imgElements = GrabUtils.evaluatesXPath2(element, PRDUCT_IMG_XPATH);
                product.setImgUrl(imgElements.get(0).attribute("src").getValue());

                //佣金比例
                String commissionRate = GrabUtils.getValueByEvaluateXPath2(element, COMMISSION_RATE_XPATH);
                product.setCommissionRatio(Double.parseDouble(commissionRate.replace("%", "")) / 100);
                product.setCommissionRatio(new BigDecimal(product.getCommissionRatio()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                //原价
                String originalPrice = GrabUtils.getValueByEvaluateXPath2(element, ORIGINAL_PRICE_XPATH);
                product.setOriginalPrice(Double.parseDouble(originalPrice.replace("￥", "").replace(",", "")));
                //佣金
                product.setCommission(product.getOriginalPrice() * product.getCommissionRatio());
                product.setCommission(new BigDecimal(product.getCommission()).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());

                list.add(product);
            }
        } catch (Exception e) {
            LOG.error("grabProducts error");
        }
        return list;
    }

    public static Product grapCategory(Product product) {
        HtmlCleaner cleaner = new HtmlCleaner();
        List<Product> list = new ArrayList<Product>();
        try {
            String url = "http://item.jd.com/" + product.getProductId() + ".html#";
            TagNode rootNode = cleaner.clean(HttpClientUtil.httpGet(url));
            CleanerProperties props = new CleanerProperties();
            org.w3c.dom.Document doc = new DomSerializer(props)
                    .createDOM(rootNode);
            Document dom4j = new DOMReader().read(doc);
            Element rootElement = dom4j.getRootElement();
            //商品名称
            TagNode tagNode = GrabUtils.evaluateXPath(rootNode, "body");

            String classStr = tagNode.getAttributeByName("class");
            String[] array = classStr.split(" ");
            for(String str : array){
                if(str.contains("cat-1")){
                    product.setFirstCategory(Integer.parseInt(str.replace("cat-1-","")));
                }else if(str.contains("cat-2")){
                    product.setSecondCategory(Integer.parseInt(str.replace("cat-2-","")));
                }else if(str.contains("cat-3")){
                    product.setThirdCategory(Integer.parseInt(str.replace("cat-3-","")));
                }
            }
        } catch (Exception e) {
            LOG.error("grabProducts error");
        }
        return product;
    }
}
