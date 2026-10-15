package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 组织设备关系实体
 * @author chmod
 * @version 1.0
 * @date 2021/12/6 17:02
 */
@Data
public class OrganDeviceRelVO  extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -1231137262653913548L;
    /** 唯一编码 */
    private String id;
    /** 客户编码 */
    private String tenantId;
    /** 组织编码 */
    private String deptId;
    /** 设备编码 */
    private String deviceCode;
    /** 逻辑删除 0正常1删除 */
    private String deleteFlag;
    /** 设备类型 */
    private int deviceType;
    /** 行业编号 */
    private String industryCode;
    /** 行业版本 */
    private String appVerCode;
    /** 组织名称 */
    private String deptName;
}
