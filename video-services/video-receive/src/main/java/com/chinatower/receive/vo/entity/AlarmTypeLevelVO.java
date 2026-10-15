package com.chinatower.receive.vo.entity;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AlarmTypeLevelVO
 * @Deacription 告警类型等级实体
 * @Author lyy
 * @Date 2025/01/10 15:53
 * @Version 1.7.20
 **/
@Data
public class AlarmTypeLevelVO extends BaseIndustryEntity implements Serializable {

    /**
     * 类型值
     */
    private String typeValue;

    /**
     * 告警等级
     */
    private String alarmLevel;

}
