package com.chinatower.receive.vo.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_iot_alarm_config_t")
public class IotAlarmConfigDo implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 物联告警配置名称
     */
    private String iotAlarmConfigName;

    /**
     * 配置方式
     */
    private String configMethod;

    /**
     * 启用状态
     */
    private String configStatus;


    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新者")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String modifier;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
//    @TableField(fill = FieldFill.INSERT_UPDATE)
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date updateTime;

    @ApiModelProperty("删除标记")
    private Integer deleteFlag;

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("应用版本编码")
    private String appVerCode;

    @ApiModelProperty("行业编码")
    private String industryCode;

    /**物联告警类型*/
    @TableField(updateStrategy  = FieldStrategy.IGNORED)
    private String iotAlarmType;

    /**
     * 物联设备类型
     */
    @TableField(updateStrategy  = FieldStrategy.IGNORED)
    private String deviceType;

    /**告警开始时间*/
    @TableField(updateStrategy  = FieldStrategy.IGNORED)
    private String receiveStartTime;

    /**告警结束时间*/
    @TableField(updateStrategy  = FieldStrategy.IGNORED)
    private String receiveEndTime;

    /**告警时间段*/
    @TableField(updateStrategy  = FieldStrategy.IGNORED)
    private String receivePeriodTime;

    /**物联因子关系 0:且  1:或*/
    @TableField(updateStrategy  = FieldStrategy.IGNORED)
    private String relation;

    /**
     * 物联因子
     */
    @TableField(exist = false)
    private List<IotFactorVo> iotFactorVoList;

    /**
     * 物联设备
     */
    @TableField(exist = false)
    private List<IotAlarmDeviceDo> iotAlarmDeviceDoList;
}
