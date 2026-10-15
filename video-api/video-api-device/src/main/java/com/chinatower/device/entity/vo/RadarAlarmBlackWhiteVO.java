package com.chinatower.device.entity.vo;


import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 模板、区域、白名单关联实体类
 */
@Data
public class RadarAlarmBlackWhiteVO extends BaseIndustryEntity implements Serializable {

    /**
     * 白名单ID-自增
     */
    private Long id;

    /**
     * 告警模板id
     */
    private Long templateId;

    /**
     * 船名
     */
    private String shipName;

    /**
     * 模板生效开始日期
     */
    private String mmsi;

    /**
     * 名单类型 1-白名单 2-黑名单
     */
    private String listType;


}
