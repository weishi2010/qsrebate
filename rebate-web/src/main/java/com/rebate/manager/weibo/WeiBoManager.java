package com.rebate.manager.weibo;

import java.util.List;

public interface WeiBoManager {
    /**
     * 获取微短链接
     * @param urls
     * @return
     */
    List<String> getShortUrl(List<String> urls);
}
