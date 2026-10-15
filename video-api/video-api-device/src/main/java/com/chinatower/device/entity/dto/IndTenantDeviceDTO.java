package com.chinatower.device.entity.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Name TenantDeviceDTO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 10 : 11
 */
@Data
public class IndTenantDeviceDTO extends CommonFeignEntity {

    @ApiModelProperty(value = "设备状态",required = false)
    private Integer deviceStatus;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    @ApiModelProperty(value = "设备类型")
    private Integer categoryCode;

}
