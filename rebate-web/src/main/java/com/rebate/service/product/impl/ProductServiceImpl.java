package com.rebate.service.product.impl;

import com.rebate.common.util.JsonUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Product;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final static String IMG_SIZE = "s150x150_jfs";
    private final static String DEFAULT_IMG_SIZE = "jfs";

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Qualifier("categoryDao")
    @Autowired(required = true)
    private CategoryDao categoryDao;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Override
    public List<Category> findByActiveCategories(CategoryQuery qategoryQuery) {
        return categoryDao.findByActiveCategories(qategoryQuery);
    }

    @Override
    public PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery, String openId) {
        PaginatedArrayList<ProductVo> products = new PaginatedArrayList<ProductVo>(productQuery.getIndex(), productQuery.getPageSize());
        try {
            int totalItem = productDao.findProductsCount(productQuery);
            if (totalItem > 0) {
                products.setTotalItem(totalItem);
                productQuery.setStartRow(products.getStartRow());
                List<Product> list = productDao.findProducts(productQuery);
                for (Product product : list) {
                    try{
                        ProductVo vo = new ProductVo(product);

                        //获取商品链接
                        vo.setImgUrl(product.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));
                        //获取推广链接
                        vo.setPromotionUrl(jdSdkManager.getPromotinUrl(getJdItemUrl(product.getProductId()), openId));

                        //轻松返平台获取佣金
                        Double qsCommissionWl = getCommissionWl(vo.getCommissionRatioWl(),vo.getOriginalPrice());//移动端
                        Double qsCommissionPc = getCommissionPc(vo.getCommissionRatioPc(),vo.getOriginalPrice());//PC端

                        //按比例给用户返佣金
                        vo.setCommissionWl(getUserCommission(qsCommissionWl));
                        vo.setCommissionPc(qsCommissionPc);

                        //轻松返平台是否返佣判断，符合规则再给予用户佣金返利
                        vo.setRebate(isRebate(qsCommissionWl));
                        products.add(vo);
                    }catch (Exception e){
                        LOG.error("findProductList error!product:{}",JsonUtil.toJson(product),e);
                    }

                }
            }

        } catch (Exception e) {
            LOG.error("findProductList error!productQuery:" + JsonUtil.toJson(productQuery), e);
        }
        return products;
    }

    /**
     * 是否返还佣金规则判断
     * TODO 目前只简单判断，大于5元再返佣金
     *
     * @param commission
     * @return
     */
    private boolean isRebate(Double commission) {
        if (commission > 5) {
            return true;
        }
        return false;
    }

    /**
     * 获取返利佣金
     */
    public Double getUserCommission(Double commission) {
        Double rateForUser = 0.5;//TODO 广告佣金计算后再按此比例给用户返利
        return new BigDecimal(commission * rateForUser).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    /**
     * 获取移动返利佣金
     */
    public Double getCommissionWl(Double commissionRatioWl,Double originalPrice) {
        if (null != commissionRatioWl && null != originalPrice) {
            return new BigDecimal(commissionRatioWl * originalPrice ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.0;
    }

    /**
     * 获取PC返利佣金
     */
    public Double getCommissionPc(Double commissionRatioPc,Double originalPrice) {
        if (null != commissionRatioPc && null != originalPrice) {
            return new BigDecimal(commissionRatioPc * originalPrice ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.0;
    }


    /**
     * 获取京东商品链接
     *
     * @param skuId
     * @return
     */
    private String getJdItemUrl(Long skuId) {
        return "https://item.jd.com/" + skuId + ".html";
    }
}
