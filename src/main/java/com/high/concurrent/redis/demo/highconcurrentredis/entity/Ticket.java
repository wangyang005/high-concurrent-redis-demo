package com.high.concurrent.redis.demo.highconcurrentredis.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* 请添加描述
* @author FGVTH
* @time 2019-03-31
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket implements Serializable {
    /**
    * 车票名
    */
    @ApiModelProperty(notes = "车票名")
    private String name;

    /**
    * 来源地
    */
    @ApiModelProperty(notes = "来源地")
    private String from;

    /**
    * 目的地
    */
    @ApiModelProperty(notes = "目的地")
    private String to;

    /**
    * 余票数量
    */
    @ApiModelProperty(notes = "余票数量")
    private Integer num;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", name=").append(name);
        sb.append(", from=").append(from);
        sb.append(", to=").append(to);
        sb.append(", num=").append(num);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}