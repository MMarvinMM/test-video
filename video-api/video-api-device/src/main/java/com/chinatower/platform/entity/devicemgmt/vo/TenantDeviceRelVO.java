package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/6 15:52
 */
@Data
public class TenantDeviceRelVO extends BaseIndustryEntity {
    private static final long serialVersionUID = -164881473932160843L;

    private Long id;
    private String deviceCode;	//设备编码
    private String deviceName;	//设备名称
    private String channelCode;
    private String channelName;
    private String tenantId; //租户ID

    /**
     * 设备类型
     */
    private Integer deviceType;

    private List<String> channelCodeList;

}
