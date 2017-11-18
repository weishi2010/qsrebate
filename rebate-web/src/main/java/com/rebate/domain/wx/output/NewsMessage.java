package com.rebate.domain.wx.output;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class NewsMessage extends BaseOutputMessage{

    @Getter
    @Setter
    private int ArticleCount;

    @Getter
    @Setter
    private List<Article> Articles;
}
