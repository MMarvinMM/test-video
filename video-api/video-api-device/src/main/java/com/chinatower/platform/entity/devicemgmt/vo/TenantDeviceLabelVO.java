package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

/**
 * 租户设备标签实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/5 14:25
 */
@Data
public class TenantDeviceLabelVO extends BaseIndustryEntity {
    private static final long serialVersionUID = -3201609084635922393L;
    /**
     * 标签id
     */
    private Long labelId;
    /**
     * 标签关联编码
     */
    private Long labelRelId;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 标签名称
     */
    private String labelName;
    /**
     * 标签状态
     */
    private Integer labelStatus;
    /**
     * 标识
     */
    private Integer flag;
}
