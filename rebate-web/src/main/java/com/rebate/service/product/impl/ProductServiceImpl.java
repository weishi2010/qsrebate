package com.rebate.service.product.impl;

import com.google.common.base.Joiner;
import com.rebate.common.data.seq.SequenceUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.common.util.rebate.RebateRuleUtil;
import com.rebate.common.web.page.PaginatedArrayList;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.ProductCouponDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.*;
import com.rebate.domain.en.*;
import com.rebate.domain.property.JDProperty;
import com.rebate.domain.query.ProductQuery;
import com.rebate.domain.vo.ApiProductVo;
import com.rebate.domain.vo.ProductVo;
import com.rebate.manager.jd.JdSdkManager;
import com.rebate.manager.shorturl.ShortUrlManager;
import com.rebate.service.product.ProductCouponService;
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

    @Qualifier("sequenceUtil")
    @Autowired(required = true)
    private SequenceUtil sequenceUtil;

    @Qualifier("productDao")
    @Autowired(required = true)
    private ProductDao productDao;

    @Qualifier("categoryDao")
    @Autowired(required = true)
    private CategoryDao categoryDao;

    @Qualifier("jdSdkManager")
    @Autowired(required = true)
    private JdSdkManager jdSdkManager;

    @Qualifier("productCouponDao")
    @Autowired(required = true)
    private ProductCouponDao productCouponDao;


    @Qualifier("productCouponService")
    @Autowired(required = true)
    private ProductCouponService productCouponService;


    @Qualifier("shortUrlManager")
    @Autowired(required = true)
    private ShortUrlManager shortUrlManager;

    @Qualifier("jDProperty")
    @Autowired(required = true)
    private JDProperty jDProperty;

    @Override
    public void update(Product product) {
        productDao.update(product);
    }

    @Override
    public void importProducts(String products) {
        if (StringUtils.isBlank(products)) {
            return;
        }

        if (StringUtils.isBlank(products)) {
            return;
        }

        List<Product> list = jdSdkManager.getMediaProducts(products);
        LOG.error("list:"+JsonUtil.toJson(list));

        for (Product product : list) {
            product.setCouponType(EProudctCouponType.GENERAL.getCode());
            product.setIsRebate(EProudctRebateType.REBATE.getCode());
            product.setFreePost(EProductFreePost.NOT_FREE_POST.getCode());
            //插入或更新商品
            if (null == productDao.findById(product)) {
                productDao.insert(product);
            } else {
                productDao.update(product);
            }
        }

    }

    @Override
    public void importCouponProducts(List<ProductCoupon> couponMapList,Integer couponType) {
        if (couponMapList.size() == 0) {
            return;
        }

        List<Long> skuList = new ArrayList<>();
        Map couponInfoMap = new HashMap();
        for (ProductCoupon productCoupon : couponMapList) {
            Long skuId = productCoupon.getProductId();
            skuList.add(skuId);
            couponInfoMap.put(skuId, productCoupon);
        }

        List<Product> list = jdSdkManager.getMediaProducts(Joiner.on(",").join(skuList));
        LOG.error("list:"+JsonUtil.toJson(list));
        for (Product product : list) {
            //获取导入的优惠券信息
            ProductCoupon couponInfo = (ProductCoupon) couponInfoMap.get(product.getProductId());

            //商品优惠券信息解析入库
            ProductCoupon productCoupon = productToCoupon(product);

            if (null != couponInfo) {
                if(null!=couponInfo.getSourcePlatform()){
                    //从导入的优惠券数据中获取来源
                    product.setSourcePlatform(couponInfo.getSourcePlatform());
                }else{
                    //没有则默认为JD联盟商品
                    product.setSourcePlatform(EProductSource.JD.getCode());
                }

                //如果传入的优惠券限额和面额不为空
                if (null != couponInfo.getDiscount() && null != couponInfo.getQuota()) {
                    productCoupon.setDiscount(couponInfo.getDiscount());
                    productCoupon.setQuota(couponInfo.getQuota());
                } else if (null != couponInfo.getOriginalPrice() && null != couponInfo.getCouponPrice()) {
                    productCoupon.setQuota(0.0);//没有面额值，填0
                    productCoupon.setDiscount(couponInfo.getOriginalPrice() - couponInfo.getCouponPrice());//原价减券后价来计算面额
                    productCoupon.setDiscount(new BigDecimal(productCoupon.getDiscount()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }

                productCoupon.setDiscount(new BigDecimal(productCoupon.getDiscount()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                productCoupon.setQuota(new BigDecimal(productCoupon.getQuota()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                if (null != couponInfo.getStartDate()) {
                    productCoupon.setStartDate(couponInfo.getStartDate());
                } else {
                    productCoupon.setStartDate(new Date());
                }
                if (null != couponInfo.getEndDate()) {
                    productCoupon.setEndDate(couponInfo.getEndDate());
                } else {
                    productCoupon.setEndDate(new Date());
                }


                productCoupon.setCouponLink(couponInfo.getCouponLink());
                //原价
                productCoupon.setOriginalPrice(product.getOriginalPrice());
                productCoupon.setOriginalPrice(new BigDecimal(product.getOriginalPrice()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                //券后价
                productCoupon.setCouponPrice(product.getOriginalPrice()-productCoupon.getDiscount());
                productCoupon.setCouponPrice(new BigDecimal(productCoupon.getCouponPrice()+"").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                if (null != couponInfo.getSortWeight()) {
                    product.setSortWeight(couponInfo.getSortWeight());
                }

                //是否包邮
                if(null!=couponInfo.getFreePost()){
                    product.setFreePost(couponInfo.getFreePost());
                }else {
                    product.setFreePost(EProductFreePost.NOT_FREE_POST.getCode());
                }


                product.setStatus(EProductStatus.PASS.getCode());
                product.setCouponType(couponType);
                product.setCouponPrice(productCoupon.getCouponPrice());
                product.setSortWeight(0);
                long extSortWeight = EProductSource.getExtSortWeight(product.getSourcePlatform());
                product.setExtSortWeight(extSortWeight);
                //计算优惠券商品返利规则
                product.setIsRebate(RebateRuleUtil.couponProductRebateRule(product.getCommissionWl()));

                //插入或更新商品
                if (null == productDao.findById(product)) {
                    productDao.insert(product);
                } else {
                    productDao.update(product);
                }
            }


            if (null == productCouponDao.findById(productCoupon)) {
                productCouponDao.insert(productCoupon);
            } else {
                productCouponDao.update(productCoupon);
            }

        }

    }

    @Override
    public List<RecommendCategory> findByRecommendCategories(RecommendCategory recommendCategory) {
        return categoryDao.findByRecommendCategories(recommendCategory);
    }

    @Override
    public PaginatedArrayList<ProductVo> findProductList(ProductQuery productQuery, UserInfo userInfo) {
        PaginatedArrayList<ProductVo> products = new PaginatedArrayList<ProductVo>(productQuery.getIndex(), productQuery.getPageSize());
        try {
            int totalItem = productDao.findProductsCount(productQuery);
            if (totalItem > 0) {
                products.setTotalItem(totalItem);
                productQuery.setStartRow(products.getStartRow());
                if (productQuery.getIndex() <= products.getTotalPage()) {
                    List<Product> list = productDao.findProducts(productQuery);
                    for (Product product : list) {
                        try {
                            ProductVo vo = new ProductVo(product);

                            //获取商品链接
                            vo.setImgUrl(product.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));

                            //查询优惠券信息
                            ProductCoupon productCouponQuery = new ProductCoupon();
                            productCouponQuery.setProductId(vo.getProductId());
                            ProductCoupon coupon = productCouponDao.findById(productCouponQuery);
                            vo.setProductCoupon(coupon);

                            vo.setUserCommission(jdSdkManager.getQSCommission(userInfo,product));//平台返还用户佣金

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
    public PaginatedArrayList<ApiProductVo> findDaxueProductList(ProductQuery productQuery) {
        PaginatedArrayList<ApiProductVo> products = new PaginatedArrayList<ApiProductVo>(productQuery.getIndex(), productQuery.getPageSize());
        try {
            int totalItem = productDao.findDaxueProductsCount(productQuery);
            if (totalItem > 0) {
                products.setTotalItem(totalItem);
                productQuery.setStartRow(products.getStartRow());
                if (productQuery.getIndex() <= products.getTotalPage()) {
                    List<DaxueProduct> list = productDao.findDaxueProducts(productQuery);
                    for (DaxueProduct daxueProduct : list) {
                        try {

                            //获取商品链接
                            daxueProduct.setImgUrl(daxueProduct.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));

                            //查询优惠券信息
                            ProductCoupon productCouponQuery = new ProductCoupon();
                            productCouponQuery.setProductId(daxueProduct.getProductId());
                            ProductCoupon coupon = productCouponDao.findById(productCouponQuery);

                            ApiProductVo apiProductVo =  new ApiProductVo(daxueProduct);
                            if (null != coupon) {
                                apiProductVo.setDiscount(coupon.getDiscount());
                            }else{
                                apiProductVo.setDiscount(0.0);
                            }

                            apiProductVo.setPromotionUrl(shortUrlManager.getQsShortPromotinUrl(apiProductVo.getPromotionUrl(),jDProperty.getApiSubUnionId()));
                            products.add(apiProductVo);

                        } catch (Exception e) {
                            LOG.error("findDaxueProductList error!product:{}", JsonUtil.toJson(daxueProduct), e);
                        }

                    }
                }

            }

        } catch (Exception e) {
            LOG.error("findDaxueProductList error!productQuery:" + JsonUtil.toJson(productQuery), e);
        }
        return products;
    }
    @Override
    public ProductVo findProduct(Long skuId) {
        ProductVo vo = null;
        try {
            Product productQuery = new Product();
            productQuery.setProductId(skuId);
            Product product = productDao.findById(productQuery);
            if (null != product) {

                vo = new ProductVo(product);
                //查询优惠券信息
                ProductCoupon productCouponQuery = new ProductCoupon();
                productCouponQuery.setProductId(vo.getProductId());
                ProductCoupon coupon = productCouponDao.findById(productCouponQuery);
                vo.setProductCoupon(coupon);
                //获取商品链接
                vo.setImgUrl(product.getImgUrl().replace(DEFAULT_IMG_SIZE, IMG_SIZE));
            }


        } catch (Exception e) {
            LOG.error("findProduct error!skuId:" + skuId, e);
        }
        return vo;
    }

    @Override
    public Product findProductBaseInfo(Long skuId) {
        Product productQuery = new Product();
        productQuery.setProductId(skuId);
        return productDao.findById(productQuery);
    }

    @Override
    public PaginatedArrayList<ApiProductVo> findCouponProducts(String subUnionId,int page,int pageSize) {
        PaginatedArrayList<ApiProductVo> list = new PaginatedArrayList<>();

        try {
            PaginatedArrayList<Long> productList  = productCouponService.getProductCouponList(page,pageSize);
            list.setTotalItem(productList.getTotalItem());

            for(Long productId:productList){
                ProductVo productVo = productCouponService.getProductVoCache(productId);
                if(null!=productVo){
                    ApiProductVo apiProductVo =  new ApiProductVo(productVo);
                    if (null != productVo.getProductCoupon()) {
                        apiProductVo.setDiscount(productVo.getProductCoupon().getDiscount());
                    }else{
                        apiProductVo.setDiscount(0.0);
                    }
                    list.add(apiProductVo);
                }
            }
        } catch (Exception e) {
            LOG.error("findCouponProducts error!subUnionId:" + subUnionId, e);
        }
        return list;
    }

    @Override
    public PaginatedArrayList<ApiProductVo> findSecSkillProducts(String subUnionId, int page, int pageSize) {
        PaginatedArrayList<ApiProductVo> list = new PaginatedArrayList<>();

        try {
            PaginatedArrayList<Long> productList  = productCouponService.findSecSkillProducts(page,pageSize);
            list.setTotalItem(productList.getTotalItem());

            for(Long productId:productList){
                ProductVo productVo = productCouponService.getProductVoCache(productId);
                if(null!=productVo){
                    ApiProductVo apiProductVo =  new ApiProductVo(productVo);
                    if (null != productVo.getProductCoupon()) {
                        apiProductVo.setDiscount(productVo.getProductCoupon().getDiscount());
                    }else{
                        apiProductVo.setDiscount(0.0);
                    }
                    list.add(apiProductVo);
                }
            }
        } catch (Exception e) {
            LOG.error("findCouponProducts error!subUnionId:" + subUnionId, e);
        }
        return list;
    }

    @Override
    public long batchResetProductSortWeight(ProductQuery productQuery) {
        return productDao.batchResetProductSortWeight( productQuery);
    }

    @Override
    public String getCouponPromotionUrl(Long skuId,String subUnionId) {
        String couponLink = "";
        //查询优惠券信息
        ProductCoupon productCouponQuery = new ProductCoupon();
        productCouponQuery.setProductId(skuId);
        ProductCoupon coupon = productCouponDao.findById(productCouponQuery);
        if (null != coupon) {
            couponLink = coupon.getCouponLink();
        }
        String url = jdSdkManager.getPromotionCouponCode(skuId, couponLink, subUnionId);
        if (StringUtils.isNotBlank(url)) {
            url = shortUrlManager.getQsShortPromotinUrl(url,subUnionId);
        } else {
            //清理掉活动过期的商品信息
            Product productUpdate = new Product();
            productUpdate.setProductId(skuId);
            productUpdate.setStatus(EProductStatus.DELETE.getCode());
            productDao.update(productUpdate);

            ProductCoupon productCouponUpdate = new ProductCoupon();
            productCouponUpdate.setProductId(skuId);
            productCouponUpdate.setStatus(EProductStatus.DELETE.getCode());
            productCouponDao.update(productCouponUpdate);

        }

        LOG.error("getPromotionCouponCode===============>skuId:" + skuId + ",couponLink:" + couponLink + ",url:" + url + ",subUnionId:" + subUnionId);

        return url;
    }

    @Override
    public void updateProductExtSortWeight(List<Product> products) {
        if (null != products) {
            for (Product product : products) {
                Product productUpdate = new Product();
                productUpdate.setProductId(product.getProductId());
                //基于之前排序值继续排序，格式：默认排序值+增量值
                long extSortWeight = EProductSource.getExtSortWeight(product.getSourcePlatform()) + sequenceUtil.get(ESequence.PRODUCT_EXT_SORTWEIGHT.getSequenceName()) + product.getExtSortWeight();
                productUpdate.setExtSortWeight(extSortWeight);
                productDao.update(productUpdate);
            }
        }
    }

//------------------------------------------------------------------------------

    /**
     * 商品信息转优惠券信息
     *
     * @param product
     * @return
     */
    private ProductCoupon productToCoupon(Product product) {
        ProductCoupon coupon = new ProductCoupon();
        coupon.setProductId(product.getProductId());
        coupon.setStartDate(product.getStartDate());
        coupon.setEndDate(product.getEndDate());
        coupon.setCouponTab(0);
        coupon.setCouponNote("");
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
