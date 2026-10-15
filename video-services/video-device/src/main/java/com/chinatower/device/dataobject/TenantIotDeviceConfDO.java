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
 * 物联设备联动配置表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Getter
@Setter
@TableName("vm_tenant_iot_device_conf_t")
public class TenantIotDeviceConfDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物联设备联动ID
     */
    @TableId(value = "iot_device_conf_id", type = IdType.AUTO)
    private Long iotDeviceConfId;

    /**
     * 物联设备编码
     */
    private String deviceCode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 触发条件,逗号分隔1:发生告警
     */
    private String triggerCond;

    /**
     * 联动动作,逗号分隔1:摄像机看向告警位置2:抓图3:抓视频
     */
    private String linkAction;

    /**
     * 联动摄像机类型1:指定摄像机2:最近的摄像机3:周边指定范围摄像机
     */
    private String linkConfType;

    /**
     * 1:转到预置位2:转到经纬度
     */
    private String linkConfSubType;

    /**
     * 联动摄像机距离
     */
    private String linkDistance;

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


}
