package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警数量返回结果VO
 */
@Data
public class EventDataVO extends AreaDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 待研判数量
     * @required true
     */
    private int judgeNum;

    /**
     * 待调度数量
     * @required true
     */
    private int dispatchNum;

    /**
     * 待认领数量
     * @required true
     */
    private int grabNum;

    /**
     * 待处置数量
     * @required true
     */
    private int handleNum;

    /**
     * 待核实数量
     * @required true
     */
    private int approveNum;

    /**
     * 已完结数量
     * @required true
     */
    private int orderCompleteNum;

    /**
     * 虚假告警完结数量
     * @required true
     */
    private int falseCompleteNum;

    /**
     * 重复告警完成数量
     * @required true
     */
    private int repeatCompleteNum;

    /**
     * 告警事件数量
     * @required true
     * */
    private int todayAll;

    /**
     * 已完结数量
     * @required true
     * */
    private int todayComplete;

    /**
     * 待受理数量
     * @required true
     * */
    private int acceptanceNum;
}
