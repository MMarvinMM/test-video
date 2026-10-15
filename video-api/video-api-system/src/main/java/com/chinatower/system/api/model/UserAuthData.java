package com.chinatower.system.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 用户认证
 * @data 创建时间：2023/8/22 9:35
 */
@Data
public class UserAuthData {
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 是否租户管理员
     */
    private String isTenantAdmin;
    /**
     * 区域集合
     */
    private List<String> areaList;
    /**
     * 设备集合
     */
    private List<DeviceTypeAndCodeVO> deviceList;
    /**
     *卫星
     */
    private List<DeviceTypeAndCodeVO> satelliteList;
    /**
     * 部门
     */
    private List<String> deptList;

}
