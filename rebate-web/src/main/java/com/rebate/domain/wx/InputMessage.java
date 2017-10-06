package com.rebate.domain.wx;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * POST的XML数据包转换为消息接受对象
 * <p/>
 * <p>
 * 由于POST的是XML数据包，所以不确定为哪种接受消息，<br/>
 * 所以直接将所有字段都进行转换，最后根据<tt>MsgType</tt>字段来判断取何种数据
 * </p>
 */
@XStreamAlias("xml")
public class InputMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @XStreamAlias("ToUserName")
    @JsonProperty("toUserName")
    @Getter
    @Setter
    private String ToUserName;


    @XStreamAlias("FromUserName")
    @JsonProperty("fromUserName")
    @Getter
    @Setter
    private String FromUserName;

    @XStreamAlias("CreateTime")
    @JsonProperty("createTime")
    @Getter
    @Setter
    private Long CreateTime;

    @XStreamAlias("MsgType")
    @JsonProperty("msgType")
    @Getter
    @Setter
    private String MsgType = "text";

    @XStreamAlias("MsgId")
    @JsonProperty("msgId")
    @Getter
    @Setter
    private Long MsgId;
    // 文本消息
    @XStreamAlias("Content")
    @JsonProperty("content")
    @Getter
    @Setter
    private String Content;

    // 图片消息  
    @XStreamAlias("PicUrl")
    @JsonProperty("picUrl")
    @Getter
    @Setter
    private String PicUrl;
    // 位置消息  
    @XStreamAlias("LocationX")
    @JsonProperty("locationX")
    @Getter
    @Setter
    private String LocationX;

    @XStreamAlias("LocationY")
    @JsonProperty("locationY")
    @Getter
    @Setter
    private String LocationY;

    @XStreamAlias("Scale")
    @JsonProperty("scale")
    @Getter
    @Setter
    private Long Scale;

    @XStreamAlias("Label")
    @JsonProperty("label")
    @Getter
    @Setter
    private String Label;

    // 链接消息  
    @XStreamAlias("Title")
    @JsonProperty("title")
    @Getter
    @Setter
    private String Title;

    @XStreamAlias("Description")
    @JsonProperty("description")
    @Getter
    @Setter
    private String Description;

    @XStreamAlias("Url")
    @JsonProperty("url")
    @Getter
    @Setter
    private String Url;

    // 语音信息  
    @XStreamAlias("MediaId")
    @JsonProperty("mediaId")
    @Getter
    @Setter
    private String MediaId;

    @XStreamAlias("Format")
    @JsonProperty("format")
    @Getter
    @Setter
    private String Format;

    @XStreamAlias("Recognition")
    @JsonProperty("recognition")
    @Getter
    @Setter
    private String Recognition;
    // 事件  
    @XStreamAlias("Event")
    @JsonProperty("event")
    @Getter
    @Setter
    private String event;

    @XStreamAlias("EventKey")
    @JsonProperty("eventKey")
    @Getter
    @Setter
    private String EventKey;

    @XStreamAlias("Ticket")
    @JsonProperty("ticket")
    @Getter
    @Setter
    private String Ticket;

}