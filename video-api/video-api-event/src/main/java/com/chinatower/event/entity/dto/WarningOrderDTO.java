package com.chinatower.event.entity.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/19 09:41
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningOrderDTO extends BaseDataScopeEntity implements Serializable {
    /**
     * 告警单ID(yyyyMM+租户id+)
     * @required true
     */
    private String warningOrderId;

    /**
     * 告警类型编码
     * @required false
     */
    private String warningTypeId;

    /**
     * 所在省名称
     * @required false
     */
    private String provinceName;

    /**
     * 省份编码
     * @required false
     */
    private String provinceCode;

    /**
     * 所在市名称
     * @required false
     */
    private String cityName;

    /**
     * 地市编码
     * @required false
     */
    private String cityCode;

    /**
     * 县编码
     * @required false
     */
    private String countyCode;

    /**
     * 县名称
     * @required false
     */
    private String countyName;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    /**
     * 详细地址
     * @required false
     */
    private String address;

    /**
     * 当前环节ID
     * @required false
     */
    private String linkId;

    /**
     * 当前环节名称
     * @required false
     */
    private String linkName;

    /**
     * 事件等级，关联枚举值表
     * @required false
     */
    private String emergencyLevel;

    /**
     * 告警重要等级 1:1级,2:2级
     * @required false
     */
    private String importanceLevel;

    /**
     * 报警参数
     * @required false
     */
    private String alarmParam;

    /**
     * 报警参数值
     * @required false
     */
    private String alarmParamValue;

    /**
     * 表名
     */
    private String tableName;
}
