package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警进度处理统计
 * 告警状态 6完结 1研判 2调度 4处置 5核实 9受理
 * 研判结果 1真警 2误报 3重复告警
 *
 * @author lisy
 */
@Data
public class AlarmStatisticsProcessingNumVO implements Serializable {

    private static final long serialVersionUID = 2934248871780343431L;

    /**
     * 待研判数量 1
     * @required true
     */
    private int judgeNum;

    /**
     * 待调度数量 2
     * @required true
     */
    private int dispatchNum;

    /**
     * 待处置数量 4
     * @required true
     */
    private int handleNum;

    /**
     * 待核实数量 5
     * @required true
     */
    private int approveNum;

    /**
     * 待受理数量 9
     * @required true
     */
    private int acceptanceNum;

    /**
     * 已完结数量
     * @required true
     */
    private int orderCompleteNum;

}
