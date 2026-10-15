package com.chinatower.platform.entity.device;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DelDeviceRelaDTO
 * @Deacription 批量删除设备组织关系DTO
 * @Author chenmf
 * @Date 2022/2/19 15:36
 * @Version 1.0
 **/
@Data
public class DelDeviceRelaDTO implements Serializable {

    /** 客户编码 */
    @NotNull(message = "客户编码不能为空")
    private String tenantId;

    /** 删除设备id列表 */
    private List<String> deviceList;

    /** 设备类型 */
    private int deviceType;
    /** 行业编号 */
    private String industryCode;
    /** 行业版本 */
    private String appVerCode;

    private List<String> industryCodeList;
}
