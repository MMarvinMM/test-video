package com.chinatower.sync.consumer.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UavMountModelVO implements Serializable {

    private Long id;

    private Long ygId;

    private String modelCode;

    private String modelName;

    private String manufacturerCode;

    private String manufacturerName;

    private String mountType;

    private String mountTypeName;

    private String supportFunction;

    private String supportFunctionName;

    private String creator;

    private String modifier;

    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
