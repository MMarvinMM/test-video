package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("vm_radar_device_rel_t")
public class RadarDeviceRelDO implements Serializable {
    @TableId(value = "radar_device_rel_id", type = IdType.AUTO)
    private Long radarDeviceRelId;

    @TableField(value = "radar_device_code")
    private String radarDeviceCode;

    @TableField(value = "device_rel_code")
    private String deviceRelCode;

    @TableField(value = "tenant_id")
    private String tenantId;

    /**
     * 行业编码
     */
    @TableField(value = "industry_code")
    private String industryCode;

    /**
     * 行业版本编码
     */
    @TableField(value = "app_ver_code")
    private String appVerCode;
}
