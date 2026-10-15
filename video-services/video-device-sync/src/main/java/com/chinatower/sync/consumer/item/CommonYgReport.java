package com.chinatower.sync.consumer.item;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommonYgReport implements Serializable {

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    @ApiModelProperty(value = "删除标志, 0：正常，1：删除")
    private String delFlag;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
