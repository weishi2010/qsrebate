package com.rebate.domain.wx.output;

import lombok.Getter;
import lombok.Setter;

public class TextMessage extends BaseOutputMessage{

    @Getter
    @Setter
    private String Content;
}
