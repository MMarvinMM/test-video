package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 级联平台绑定摄像机下发信息
 */
@Data
public class CasCadeBindPlatInfoVO implements Serializable {
    /**
     * 上级平台编码
     */
    private String platCode;
    /**
     * 访问权限列表
     */
    private List<String> permissionTypeList;
    /**
     * 设备树信息列表
     */
    private List<CasCadeBindDeviceInfoTreeVO> deviceInfoTreeList;

    /**
     * 级联状态 0-关闭 1-开启
     */
    private String cascadeStatus;

    /**
     * 客户编码列表
     */
    private List<String> tenantIdList;

    private String operator;

    private String operatorName;

    private Date modifyTime;
}
