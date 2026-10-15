package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 级联平台同步信息
 */
@Data
public class CasCadeSyncVO implements Serializable {
    /**
     * 租户id
     * @required true
     */
    private String tenantId;
    /**
     * 上级平台编码
     * @required true
     */
    private String platAsId;
    /**
     * 访问权限
     * @required true
     */
    private String[] permissions;
    /**
     * 设备编码
     * @required true
     */
    private String[] deviceCode;

    /**
     * 是否需要设备信息
     */
    private boolean needDevice = true;

    /**
     * 平台编码
     */
    private String platCode;

    /**
     * 客户编码列表
     */
    private List<String> tenantIdList;
}
