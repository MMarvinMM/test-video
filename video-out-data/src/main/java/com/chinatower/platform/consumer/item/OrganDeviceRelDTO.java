package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrganDeviceRelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;
    /** 设备类型 */
    private int deviceType;
    /** 行业版本 */
    private String appVerCode;

    /** 行业编号 */
    private String industryCode;

}
