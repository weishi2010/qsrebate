package com.rebate.domain.wx.output;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class BaseOutputMessage implements Serializable{

    @Getter
    @Setter
    private String ToUserName;


    @Getter
    @Setter
    private String FromUserName;

    @Getter
    @Setter
    private Long CreateTime;

    @Getter
    @Setter
    private String MsgType;

    @Getter
    @Setter
    private String Content;
}
