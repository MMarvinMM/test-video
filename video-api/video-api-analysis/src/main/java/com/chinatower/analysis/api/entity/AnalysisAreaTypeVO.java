package com.chinatower.analysis.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisAreaTypeVO {
    private static final long serialVersionUID = 1L;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

    /**
     * 区县名称
     * @required false
     */
    private String countyName;


    private List<AnalysisAreaTypeVO> alarms;

    private String num;

    private String processedNum;

    /**
     * 告警类型
     * @required false
     */
    private String warningTypeId;


    private String warningTypeName;
}
