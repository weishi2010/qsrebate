package com.rebate.test;

import com.rebate.common.util.JsonUtil;
import com.rebate.dao.AdvertismentPositionDao;
import com.rebate.domain.AdvertismentPosition;
import com.rebate.domain.en.EAdPosition;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class AdvertismentDaoTest extends AbstractJUnit4SpringContextTests {

    private final static TypeReference<List<Map>> mapListTypeReference = new TypeReference<List<Map>>() {
    };

    @Qualifier("advertismentPositionDao")
    @Autowired(required = true)
    private AdvertismentPositionDao advertismentPositionDao;

    @Test
    public void testInsert() {

        AdvertismentPosition advertismentPosition = new AdvertismentPosition();
        advertismentPosition.setLink("http://m.qingsongfan.com");
        advertismentPosition.setImgUrl("http://img.qingsongfan.com");
        advertismentPosition.setTitle("活动测试");
        advertismentPosition.setStatus(0);
        advertismentPosition.setSortWeight(100);
        advertismentPosition.setPosition(EAdPosition.MAIN.getCode());
        System.out.println(JsonUtil.toJson(advertismentPosition));
        if (null == advertismentPositionDao.findAdPositionByPosition(advertismentPosition)) {
            advertismentPositionDao.insert(advertismentPosition);
        }

    }


}