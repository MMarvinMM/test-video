package com.chinatower.event.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmSuppressConfigVO.java
 *
 * @author luo.zhenghong
 * @date 2022/6/9 9:42
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmSuppressConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer configId;

    /**
     * 设备编码
     */
    @NotNull(message = "设备编码不能为空")
    private String deviceCode;

    /**
     * 告警压制开始时间段
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date suppressBeginTime;

    /**
     * 告警压制结束时间段
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date suppressEndTime;

    /**
     * 告警压制持续时长
     */
    @NotNull(message = "告警压制时长不能为空")
    @Min(value = 1, message = "告警压制时长不合法!")
    private Integer suppressContinuousTime;

    /**
     * 告警压制时长单位
     */
    @NotNull(message = "告警压制时长单位不能为空")
    private String suppressIntervalType;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 删除标识  1删除 0正常
     */
    private String deleteFlag;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业应用版本
     */
    private String appVerCode;
}
