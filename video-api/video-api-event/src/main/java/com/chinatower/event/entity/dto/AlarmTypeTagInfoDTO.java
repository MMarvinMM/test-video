package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmTypeTagInfoDTO extends BaseDataScopeEntity {

    /**
     * 告警类型标签编码列表
     */
    private List<String> typeValueList;
}
