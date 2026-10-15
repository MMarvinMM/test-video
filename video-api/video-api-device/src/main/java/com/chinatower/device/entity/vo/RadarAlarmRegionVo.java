package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达告警区域实体类
 */

@Data
public class RadarAlarmRegionVo extends BaseIndustryEntity implements Serializable {


    /**
     * 告警区域ID
     */
    private Long regionId;

    /**
     * 告警区域名称
     */
    private String regionName;

    /**
     * 告警区域类型
     */
    private String regionType;

    /**
     * 关联模板数量
     */
    private String unionTemplateCount;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 行业应用名称
     */
    private String industryName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 告警区域地图信息
     */
    private String regionJson;


    /**
     * 告警区域地图信息实体类
     */
    private RadarAlarmRegionJson radarAlarmRegionJson;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 关联模板集合
     */
    private List<UnionTemplateVo> unionTemplateList;
}
