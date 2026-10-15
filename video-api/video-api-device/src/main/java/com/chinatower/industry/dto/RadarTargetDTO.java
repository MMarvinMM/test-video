package com.chinatower.industry.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RadarTargetDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 设备编码列表
     */
    private List<String> deviceCodeList;

    /**
     * 时间阈值
     */
    private String timeBefore;
}
