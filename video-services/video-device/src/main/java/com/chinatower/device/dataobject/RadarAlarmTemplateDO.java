package com.chinatower.device.dataobject;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * Copyright: Copyright(c) 2023 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: RadarAlarmTemplateVo.java
 *
 * @author li.shihao
 * @version v1.5.85
 * @date 2023/4/27
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
@TableName("vm_radar_alarm_template")
public class RadarAlarmTemplateDO implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 告警模板名称
     */
    private String templateName;

    /**
     * 告警类型编码
     */
    private String alarmTypeValue;

    /**
     * 告警模板状态 1-开启 0-关闭
     */
    private Integer templateStatus;

    /**
     *模板生效开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date validStartDate;

    /**
     *模板生效结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date validEndDate;

    /**
     *模板生效开始时间
     */
    private String validStartTime;

    /**
     *模板生效结束时间
     */
    private String validEndTime;

    /**
     * 模板属性信息
     */
    private String templateJson;

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("应用版本编码")
    private String appVerCode;

    @ApiModelProperty("行业编码")
    private String industryCode;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String modifier;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    @ApiModelProperty("删除标记")
    private Integer deleteFlag;

    /**
     * 同步状态 0-初始值 1-成功 2-失败
     */
    private Integer syncStatus;

    /**
     * 同步成功后--任务ID
     */
    private String taskId;
}
