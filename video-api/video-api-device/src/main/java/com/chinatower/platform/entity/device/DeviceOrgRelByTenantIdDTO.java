package com.chinatower.platform.entity.device;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DeviceOrgRelByTenantIdDTO
 * @Deacription 租户对应设备组织关系
 * @Author chenmf
 * @Date 2022/6/1 13:53
 * @Version 1.0
 **/
@Data
public class DeviceOrgRelByTenantIdDTO implements Serializable {

    /**
     * 客户编码
     * @required true
     */
    @NotNull(message = "客户编码不能为空")
    private String tenantId;
}
