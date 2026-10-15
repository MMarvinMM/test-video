package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lc
 * @version 1.0
 * @date 2022/4/20 9:50
 */
@Data
@ToString
public class CheckpointDeviceInfoVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -2191999855013830554L;
    /**
     * 主键
     */
    private String cpId;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备状态
     */
    private String deviceStatus;
    /**
     * 通道编码
     */
    private String channelCode;
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 省份编码
     */
    private String provinceId;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 地市编码
     */
    private String cityId;
    /**
     * 地市名称
     */
    private String cityName;
    /**
     * 区县编码
     */
    private String countyId;
    /**
     * 区县名称
     */
    private String countyName;
    /**
     * 租户名称
     */
    private String tenantName;
    private List<String> ids;

    protected Integer pageNum;
    protected Integer pageSize;
    private String sort;
    private String sortName;

    private List<String> deviceCodeList;

    private String acceptNode;

    private List<String> areaCodeList;


}
