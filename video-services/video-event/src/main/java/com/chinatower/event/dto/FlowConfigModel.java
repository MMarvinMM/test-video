package com.chinatower.event.dto;

import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流程批量配置参数模型
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 14:49
 */
@Data
public class FlowConfigModel implements Serializable {
    private static final long serialVersionUID = 180445998218157431L;

    /** 流程配置数据 */
    private List<AlarmFlowConfigModel> flowConfigList;
    /** 告警参数 */
    private AlarmTypeClassDTO alarmTypeClass;
}
