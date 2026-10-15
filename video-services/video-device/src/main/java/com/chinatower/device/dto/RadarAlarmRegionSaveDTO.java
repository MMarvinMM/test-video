package com.chinatower.device.dto;

import com.chinatower.device.entity.vo.RadarAlarmRegionJson;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 雷达告警区域查询实体类
 */
@Data
public class RadarAlarmRegionSaveDTO implements Serializable {

    /**
     * 告警区域ID
     */
    private Long id;

    /**
     * 告警区域名称
     */
    private String regionName;

    /**
     * 告警区域类型
     */
    private Integer regionType;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 告警区域属性信息
     */
    private RadarAlarmRegionJson regionJson;

    /**
     * 告警区域属性信息字符串
     */
    private String regionJsonStr;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    private Integer deleteFlag;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 更新时间
     */
    private Date updateTime;
}
