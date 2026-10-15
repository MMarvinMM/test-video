package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RadarDeviceRelDTO extends BaseDataScopeEntity implements Serializable {
    /**
     * 页码
     */
    protected Integer pageNum;
    /**
     * 每页展示数量
     */
    protected Integer pageSize;
    /**
     * 租户id或租户名称
     */
    private String tenantIdOrName;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 雷达设备编码
     */
    private String radarDeviceCode;

    /**
     * 设备编码集合
     */
    private List<String> deviceCodeList;

    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;

    /**
     * 行业编码
     */
    private String industryCode;




}
