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
 * 物联设备联动关联表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Getter
@Setter
@TableName("vm_tenant_iot_conf_rel_t")
public class TenantIotConfRelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物联设备联动关联ID
     */
    @TableId(value = "conf_rel_id", type = IdType.AUTO)
    private Long confRelId;

    /**
     * 物联设备联动ID
     */
    private Long iotDeviceConfId;

    /**
     * 摄像机设备编码
     */
    private String deviceCode;

    /**
     * 摄像机通道编码
     */
    private String channelCode;

    /**
     * 预置位id
     */
    private Long presetId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    private String industryCode;

    private String appVerCode;

    /**
     * 联动摄像机类型1:指定摄像机2:最近的摄像机3:周边指定范围摄像机
     */
    private String linkConfType;


}
