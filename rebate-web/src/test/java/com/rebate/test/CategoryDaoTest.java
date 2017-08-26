package com.rebate.test;

import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CategoryDao;
import com.rebate.dao.CommentDao;
import com.rebate.domain.Category;
import com.rebate.domain.CategoryQuery;
import net.sf.json.JSON;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class CategoryDaoTest extends AbstractJUnit4SpringContextTests {

    private final static TypeReference<List<Map>> mapListTypeReference = new TypeReference<List<Map>>() {
    };

    @Qualifier("categoryDao")
    @Autowired(required = true)
    private CategoryDao categoryDao;

    @Test
    public void testInsert() {
        Category category = new Category();
        category.setFirstCategory(1);
        category.setFirstCategoryName("1");
        category.setSecondCategory(2);
        category.setSecondCategoryName("2");
        category.setThirdCategory(33);
        category.setThirdCategoryName("33");
        category.setSource(1);
        categoryDao.insert(category);
    }


    @Test
    public void findByActiveCategories() {
        CategoryQuery query = new CategoryQuery();
        query.setPageSize(100);
        List list = categoryDao.findByActiveCategories(query);
        System.out.println("list:" + JsonUtil.toJson(list));
    }

    @Test
    public void grabAllCategories() {
        String firstUrl = "http://mj.club.jd.com/java/common/getFirstCategoryList.action";
        String secondUrl = "http://mj.club.jd.com/java/common/getSecondCategoryList.action?firstCategory=";
        String thirdUrl = "http://mj.club.jd.com/java/common/getThirdCategoryList.action?secondCategory=";

        String json = null;
        try {
            json = HttpClientUtil.httpGet(firstUrl);
            List<Map> firstMapList = getCategoryList(json);
            for (Map firstMap : firstMapList) {
                String secondJson = HttpClientUtil.httpGet(secondUrl + firstMap.get("productSortId").toString());
                List<Map> secondMapList = getCategoryList(secondJson);
                for (Map secondMap : secondMapList) {
                    String thirdJson = HttpClientUtil.httpGet(thirdUrl + secondMap.get("productSortId").toString());
                    List<Map> thirdMapList = getCategoryList(thirdJson);
                    for (Map thirdMap : thirdMapList) {
                        String firstCategory = firstMap.get("productSortId").toString();
                        String firstCategoryName = firstMap.get("name").toString();
                        String secondCategory = secondMap.get("productSortId").toString();
                        String secondCategoryName = secondMap.get("name").toString();
                        String thirdCategory = thirdMap.get("productSortId").toString();
                        String thirdCategoryName = thirdMap.get("name").toString();

                        Category category = installCategory(firstCategory, firstCategoryName, secondCategory, secondCategoryName, thirdCategory, thirdCategoryName);
                        CategoryQuery categoryQuery = new CategoryQuery();
                        categoryQuery.setThirdCategory(Integer.parseInt(thirdCategory));
                        if (null == categoryDao.findByThirdCategory(categoryQuery)) {
                            categoryDao.insert(category);
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Category installCategory(String firstCategory, String firstCategoryName, String secondCategory, String secondCategoryName, String thirdCategory, String thirdCategoryName) {
        Category category = new Category();
        category.setFirstCategory(Integer.parseInt(firstCategory));
        category.setFirstCategoryName(firstCategoryName);
        category.setSecondCategory(Integer.parseInt(secondCategory));
        category.setSecondCategoryName(secondCategoryName);
        category.setThirdCategory(Integer.parseInt(thirdCategory));
        category.setThirdCategoryName(thirdCategoryName);
        category.setSource(1);
        return category;
    }

    public List<Map> getCategoryList(String json) {
        json = json.replace("null(", "").replace(");", "");
        Map rootMap = JsonUtil.fromJson(json, Map.class);
        List<Map> categoryList = (List<Map>) rootMap.get("categoryList");
        return categoryList;
    }


}