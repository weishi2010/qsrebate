package com.rebate.service.activity.impl;

import com.rebate.dao.AdvertismentPositionDao;
import com.rebate.domain.AdvertismentPosition;
import com.rebate.domain.en.EAdPosition;
import com.rebate.service.activity.AdvertismentPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("advertismentPositionService")
public class AdvertismentPositionServiceImpl implements AdvertismentPositionService {

    @Qualifier("advertismentPositionDao")
    @Autowired(required = true)
    private AdvertismentPositionDao advertismentPositionDao;

    @Override
    public void addAdvertismentPosition(AdvertismentPosition advertismentPosition) {
        if (null == advertismentPositionDao.findAdPositionByPosition(advertismentPosition)) {
            advertismentPositionDao.insert(advertismentPosition);
        } else {
            advertismentPositionDao.update(advertismentPosition);
        }
    }

    @Override
    public AdvertismentPosition findMainAdvertismentPosition() {
        AdvertismentPosition advertismentPosition = new AdvertismentPosition();
        advertismentPosition.setPosition(EAdPosition.MAIN.getCode());
        return advertismentPositionDao.findAdPositionByPosition(advertismentPosition);
    }
}
