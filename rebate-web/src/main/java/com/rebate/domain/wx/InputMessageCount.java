package com.rebate.domain.wx;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * POST的XML数据包转换为消息接受对象
 * <p/>
 * <p>
 * 由于POST的是XML数据包，所以不确定为哪种接受消息，<br/>
 * 所以直接将所有字段都进行转换，最后根据<tt>MsgType</tt>字段来判断取何种数据
 * </p>
 */
@XStreamAlias("xml")
public class InputMessageCount implements Serializable {

    @XStreamAlias("Count")
    @JsonProperty("count")
    @Getter
    @Setter
    private Integer Count;


    @XStreamAlias("ResultList")
    @JsonProperty("resultList")
    @Getter
    @Setter
    private String ResultList;

    @XStreamAlias("CheckState")
    @JsonProperty("checkState")
    @Getter
    @Setter
    private Integer CheckState;
}