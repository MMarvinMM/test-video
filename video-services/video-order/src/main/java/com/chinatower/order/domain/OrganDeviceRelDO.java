package com.chinatower.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 组织设备关系表
 * </p>
 *
 * @author chenmf
 * @since 2022-06-13
 */
@Getter
@Setter
@TableName("vm_organ_device_rel_t")
public class OrganDeviceRelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 组织id
     */
    private String deptId;

    /**
     * 设备编码
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

    /**
     * 设备类型: 0-摄像头,1-雷达,2-传感器
     */
    private int deviceType;

    private String industryCode;

    private String appVerCode;

}
