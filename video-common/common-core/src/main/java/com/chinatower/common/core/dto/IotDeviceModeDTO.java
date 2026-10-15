package com.chinatower.common.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "设备类型查询入参")
public class IotDeviceModeDTO extends BaseModel implements Serializable {
    /**
     * 物联厂家编码集合
     */
    private List<Long> iotModelIds;
    /**
     * 物联设备厂家编码
     */
    private String mfgrCode;
    /**
     * 设备厂家编码
     */
    private String deviceModelCode;
    /**
     * 设备类型
     */
    private String deviceType;
    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;
    //add 2022-06-15 by wu.zh 查询条件新增创建人、创建时间、修改人、修改时间
    /**
     * 创建开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTimeStart;
    /**
     * 创建结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTimeEnd;
    /**
     * 更新开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTimeStart;
    /**
     * 更新结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTimeEnd;

    /**
     * 排序方式
     * 排序字段
     * @return
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;
}
