package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PointsRelationVO {
    /**
     * 告警子类编码
     */
    private String itemCode;

    /** 关联区域 */
    List<AlgorithmMonitorAreaVO> prMonitorArea;


    /** 绊线集合 */
    private List<TripThreadVO> prTripThreadList;

    /** 油气管线集合 */
    private List<OilGasPipelinesVO> prOilGasPipelinesList;

    /**
     * 文本标签集合
     */
    private List<TextLabelVO> prTextLabelList;

    /**
     * 关联区域串
     */
    private String points;
    /**
     * 绊线串
     */
    private String tripThread;
    /**
     * 管线串
     */
    private String oilGasPipelines;

    /**
     * 文本串
     */
    private String textLabels;

    /**
     * 唯一标绘id
     */
    private String pointId;

    /**
     * 告警子类名称
     */
    private String itemValue;

    /**
     * 分析频率 单位：秒
     */
    private Long timeInterval;

    /**
     * 算法分析时段集合
     */
    private List<AnalysisPeriodVO> prAnalysisPeriodList;

    /**
     * 运行时段串
     */
    private String analysisPeriod;



    /** 动态参数 */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String,String> dynamicParams;

    @JsonAnyGetter
    public Map<String,String> getDynamicParams() {
        return dynamicParams;
    }

    @JsonAnySetter
    public void putDynamicParam(String key, String value) {
        if (this.dynamicParams == null) {
            this.dynamicParams = new HashMap<>();
        }
        this.dynamicParams.put(key, value);
    }

    public void setDynamicParams(Map<String,String> dynamicParams) {
        this.dynamicParams = dynamicParams;
    }




}
