package com.rebate.service.activity;

import com.rebate.domain.AdvertismentPosition;

public interface AdvertismentPositionService {

    /**
     * 添加广告位置
     * @param advertismentPosition
     */
    void addAdvertismentPosition(AdvertismentPosition advertismentPosition);

    /**
     * 查询广告位置
     * @return
     */
    AdvertismentPosition findMainAdvertismentPosition();
}
