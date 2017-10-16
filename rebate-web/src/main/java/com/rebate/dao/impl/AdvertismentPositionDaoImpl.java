package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.AdvertismentPositionDao;
import com.rebate.domain.AdvertismentPosition;

public class AdvertismentPositionDaoImpl  extends BaseDao implements AdvertismentPositionDao {
    @Override
    public void insert(AdvertismentPosition advertismentPosition) {
        insert("AdvertismentPosition.insert",advertismentPosition);
    }

    @Override
    public void update(AdvertismentPosition advertismentPosition) {
        insert("AdvertismentPosition.update",advertismentPosition);
    }

    @Override
    public AdvertismentPosition findAdPositionByPositionId(AdvertismentPosition advertismentPosition) {
        return (AdvertismentPosition)queryForObject("AdvertismentPosition.findAdPositionByPositionId",advertismentPosition);
    }
}
