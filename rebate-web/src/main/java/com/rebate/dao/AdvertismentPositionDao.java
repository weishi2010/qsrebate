package com.rebate.dao;

import com.rebate.domain.AdvertismentPosition;

public interface AdvertismentPositionDao {
    /**
     * 插入
     *
     * @param advertismentPosition
     */
    void insert(AdvertismentPosition advertismentPosition);

    /**
     * 更新
     *
     * @param advertismentPosition
     */
    void update(AdvertismentPosition advertismentPosition);

    /**
     * 根据标题和链接查询活动
     *
     * @param advertismentPosition
     * @return
     */
    AdvertismentPosition findAdPositionByPosition(AdvertismentPosition advertismentPosition);

}
