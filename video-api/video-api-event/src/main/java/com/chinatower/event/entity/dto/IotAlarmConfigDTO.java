package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@ApiModel(description = "物联告警配置查询入参")
public class IotAlarmConfigDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 物联告警配置名称
     */
    private String iotAlarmConfigName;

    /**
     * 配置状态
     */
    private String configStatus;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页展示条数
     */
    private Integer pageSize;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTimeBegin;

    /**
     * 创建结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTimeEnd;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTimeBegin;

    /**
     * 修改结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTimeEnd;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 应用版本
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;
}
