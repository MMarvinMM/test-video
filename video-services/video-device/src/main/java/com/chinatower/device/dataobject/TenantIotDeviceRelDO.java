package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 租户物联设备关系表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-08
 */
@Getter
@Setter
@TableName("vm_tenant_iot_device_rel_t")
public class TenantIotDeviceRelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表id
     */
    @TableId(value = "ten_iot_dev_rel_id", type = IdType.AUTO)
    private Long tenIotDevRelId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 街道行政区域编码
     */
    private String townCode;

    /**
     * 街道行政区域名称
     */
    private String townName;

    /**
     * 设备类型
     */
    private Integer categoryCode;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 字典值：0正常1删除
     */
    private String deleteFlag;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;




}
