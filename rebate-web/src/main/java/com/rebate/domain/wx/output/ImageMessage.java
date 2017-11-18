package com.rebate.domain.wx.output;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ImageMessage extends BaseOutputMessage{

    @Getter
    @Setter
    private int FuncFlag=0;

    @Getter
    @Setter
    private Media Image;
}
