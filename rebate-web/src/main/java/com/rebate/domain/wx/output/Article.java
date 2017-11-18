package com.rebate.domain.wx.output;

import lombok.Getter;
import lombok.Setter;

public class Article {

    /**
     * 图文消息的描述
     */
    @Getter
    @Setter
    private String Description;
    /**
     * 图文消息图片地址
     */
    @Getter
    @Setter
    private String  PicUrl;
    /**
     * 图文消息标题
     */
    @Getter
    @Setter
    private String Title;
    /**
     * 图文url链接
     */
    @Getter
    @Setter
    private String Url;
}
