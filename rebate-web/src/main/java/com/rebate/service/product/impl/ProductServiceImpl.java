package com.rebate.service.product.impl;

import com.google.common.base.Joiner;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.CouponUtil;
import com.rebate.common.util.rebate.RebateRuleUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import com.rebate.domain.Product;
import com.rebate.domain.ProductCoupon;
import com.rebate.domain.en.EProductSource;
import com.rebate.domain.en.EProudctCouponType;
import com.rebate.domain.en.EProudctRebateType;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.service.product.ProductService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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

    @Autowired
    private ProductCouponDao productCouponDao;

    @Override
    public void importProducts(String products) {
        if (StringUtils.isBlank(products)) {
            return;
        }

        if (StringUtils.isBlank(products)) {
            return;
        }

        List<Product> list = jdSdkManager.getMediaProducts(products);

        for (Product product : list) {
            product.setCouponType(EProudctCouponType.GENERAL.getCode());
            product.setIsRebate(EProudctRebateType.REBATE.getCode());

            //插入或更新商品
            if (null == productDao.findById(product)) {
                productDao.insert(product);
            } else {
                productDao.update(product);
            }
        }

    }

    @Override
    public void importCouponProducts(List<ProductCoupon> couponMapList) {
        if (couponMapList.size()==0) {
            return;
        }

        List<Long> skuList = new ArrayList<>();
        Map discountMap = new HashMap();
        Map quotaMap = new HashMap();
        for (ProductCoupon productCoupon : couponMapList) {
            Long skuId = productCoupon.getProductId();
            skuList.add(skuId);
            discountMap.put(skuId,productCoupon.getDiscount());
            quotaMap.put(skuId,productCoupon.getQuota());
        }

        List<Product> list = jdSdkManager.getMediaProducts(Joiner.on(",").join(skuList));

        for (Product product : list) {
            product.setCouponType(EProudctCouponType.COUPON.getCode());
            //计算优惠券商品返利规则
            product.setIsRebate(RebateRuleUtil.couponProductRebateRule(product.getCommissionWl()));

            //插入或更新商品
            if (null == productDao.findById(product)) {
                productDao.insert(product);
            } else {
                productDao.update(product);
            }

            //商品优惠券信息解析入库
            ProductCoupon productCoupon = productToCoupon(product);
            Double discount = (Double) discountMap.get(product.getProductId());
            Double quota = (Double) quotaMap.get(product.getProductId());
            if (null != discount) {
                productCoupon.setDiscount(discount);
            } else {
                productCoupon.setDiscount(0.0);
            }

            if (null != quota) {
                productCoupon.setQuota(quota);
            } else {
                productCoupon.setQuota(0.0);
            }

            if (null == productCouponDao.findById(productCoupon)) {
                productCouponDao.insert(productCoupon);
            }else{
                productCouponDao.update(productCoupon);
            }

        }

    }

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
                if(productQuery.getIndex()<=products.getTotalPage()){
                    List<Product> list = productDao.findProducts(productQuery);
                    for (Product product : list) {
                        try {
                            ProductVo vo = new ProductVo(product);

                            //获取商品链接
                            vo.setImgUrl(product.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));

                            //轻松返平台获取佣金
                            Double qsCommissionWl = getCommissionWl(vo.getCommissionRatioWl(), vo.getOriginalPrice());//移动端
                            Double qsCommissionPc = getCommissionPc(vo.getCommissionRatioPc(), vo.getOriginalPrice());//PC端

                            //按比例给用户返佣金
                            vo.setCommissionWl(getUserCommission(qsCommissionWl));
                            vo.setCommissionPc(qsCommissionPc);

                            //查询优惠券信息
                            ProductCoupon productCouponQuery =new ProductCoupon();
                            productCouponQuery.setProductId(vo.getProductId());
                            ProductCoupon coupon = productCouponDao.findById(productCouponQuery);
                            vo.setProductCoupon(coupon);
                            products.add(vo);
                        } catch (Exception e) {
                            LOG.error("findProductList error!product:{}", JsonUtil.toJson(product), e);
                        }

                    }
                }

            }

        } catch (Exception e) {
            LOG.error("findProductList error!productQuery:" + JsonUtil.toJson(productQuery), e);
        }
        return products;
    }

    @Override
    public ProductVo findProduct(Long skuId, String openId) {
        ProductVo vo = null;
        try {
            Product productQuery = new Product();
            productQuery.setProductId(skuId);
            Product product = productDao.findById(productQuery);
            vo = new ProductVo(product);

            //获取商品链接
            vo.setImgUrl(product.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));

            //轻松返平台获取佣金
            Double qsCommissionWl = getCommissionWl(vo.getCommissionRatioWl(), vo.getOriginalPrice());//移动端
            Double qsCommissionPc = getCommissionPc(vo.getCommissionRatioPc(), vo.getOriginalPrice());//PC端

            //按比例给用户返佣金
            vo.setCommissionWl(getUserCommission(qsCommissionWl));
            vo.setCommissionPc(qsCommissionPc);


        } catch (Exception e) {
            LOG.error("findProduct error!skuId:" + skuId, e);
        }
        return vo;
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
    public Double getCommissionWl(Double commissionRatioWl, Double originalPrice) {
        if (null != commissionRatioWl && null != originalPrice) {
            return new BigDecimal(commissionRatioWl * originalPrice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.0;
    }

    /**
     * 获取PC返利佣金
     */
    public Double getCommissionPc(Double commissionRatioPc, Double originalPrice) {
        if (null != commissionRatioPc && null != originalPrice) {
            return new BigDecimal(commissionRatioPc * originalPrice).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0.0;
    }

//------------------------------------------------------------------------------
    /**
     * 商品信息转优惠券信息
     * @param product
     * @return
     */
    private ProductCoupon productToCoupon(Product product){
        ProductCoupon coupon = new ProductCoupon();
        coupon.setProductId(product.getProductId());
        coupon.setStartDate(product.getStartDate());
        coupon.setEndDate(product.getEndDate());
        coupon.setCouponTab(0);
        coupon.setCouponNote("");
        coupon.setSourcePlatform(EProductSource.JD.getCode());
        coupon.setYn(0);
        coupon.setNum(0);
        coupon.setRemainNum(0);
        coupon.setCouponLink("");
        coupon.setCouponPrice(0.0);
        coupon.setStatus(0);
        coupon.setPlatform(0);
        return coupon;
    }

}
