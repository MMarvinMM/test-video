package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.Date;

/**
 * @desription: 卫片图层
 * @data 创建时间：2024/5/8 11:34
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class VmSatelliteLayerConfigDTO {
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 图层简称
     */
    private String abbreviation;
    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警类型
     */
    private String alarmType;
    /**
     * 告警类型名称
     */
    private String alarmTypeName;
    /**
     * 模板名称
     */
    private String layerTempName;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 版本号
     */
    private String appVerCode;
    /**
     *  PG表名
     */
    private String layerName;

    /**
     * 字段图层名称
     */
    private String customName;
    /**
     * 图层数量
     */
    private Integer polygonCount;
    /**
     * 是否生成告警
     */
    private String isBuildAlarm;
}
