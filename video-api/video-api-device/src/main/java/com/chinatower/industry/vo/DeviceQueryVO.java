package com.chinatower.industry.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceQueryVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 1- 摄像机
     * 2- 物联网设备
     */
    @NotBlank(message = "设备类型不为null")
    private String deviceType;

    /**
     * 距离 单位：米
     */
    @NotNull(message = "距离不能为null")
    private Integer distance;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;

}
