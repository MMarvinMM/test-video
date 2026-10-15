package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区域公共参数VO
 */
@Data
public class AlarmNumByDateAndTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 时间段
     */
    private String label;

    /**
     * 告警总数量
     * @required true
     */
    private Integer totalNum;

    private List<NumByTypeVo> numByTypeList;
}
