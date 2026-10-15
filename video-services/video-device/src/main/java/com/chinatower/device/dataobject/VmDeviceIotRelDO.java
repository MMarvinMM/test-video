package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 摄像机物联设备关系表
 * @TableName vm_device_iot_rel_t
 */
@Data
@TableName("vm_device_iot_rel_t")
public class VmDeviceIotRelDO {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 摄像机设备编码
     */
    private String deviceCode;

    /**
     * 物联设备编码
     */
    private String iotDeviceCode;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    private Byte deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 版本编码
     */
    private String appVerCode;

    /**
     * 绑定标识 0-已绑定 1-未绑定
     */
    private Byte bindFlag;

    private static final long serialVersionUID = 1L;
}
