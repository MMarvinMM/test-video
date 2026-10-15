package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "查询客户的告警来源及告警类型", description = "查询客户的告警来源及告警类型")
public class AlarmSourceSubTypeDTO extends BaseDataScopeEntity {

    /**
     * 告警来源集合
     * @required false
     */
    private List<String> alarmSources;

    /**
     * 设备类型
     * @required false
     */
    private String categoryCode;

    /**
     * 受理节点
     */
    private String locationCode;

    /**
     * 是否包含第三方告警类型 true-包含 false-不包含
     */
    private Boolean withThirdAlarmType;

    /**
     * 是否只包含卫星火点
     */
    private Boolean onlySatelliteFirePointFlag;

    /**
     * 是否包含算法模型配置
     */
    private Boolean withAlgorithmModelConfig;

    /**
     * 算法编码列表
     */
    private List<String> algorithmCodeList;

    /**
     * 大模型算法编码列表
     */
    private List<String> modelAlgorithmCodeList;
}
