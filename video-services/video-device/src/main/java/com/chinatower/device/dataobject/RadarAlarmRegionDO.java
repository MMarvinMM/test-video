package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("vm_radar_alarm_region")
public class RadarAlarmRegionDO implements Serializable {

    /**
     *区域id-自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 告警区域名称
     */
    private String regionName;

    /**
     * 告警区域类型
     */
    private String regionType;

    /**
     * 地图标绘信息
     */
    private String regionJson;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 删除标识
     */
    private Integer deleteFlag;

    /**
     *创建人
     */
    private String creator;

    /**
     *创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     *更新者
     */
    private String modifier;

    /**
     *更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
}
