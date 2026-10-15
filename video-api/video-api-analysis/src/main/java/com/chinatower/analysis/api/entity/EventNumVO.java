package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警数量统计返回参数
 */
@Data
public class EventNumVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 今日告警总数
     * @required true
     */
    private int todayAll;

    /**
     * 今日处理中数量
     * @required true
     */
    private int todayProcess;

    /**
     * 今日已完结数量
     * @required true
     */
    private int todayComplete;

    /**
     * 总处理中数量
     * @required true
     */
    private int totalProcess;

    /**
     * 总已完结数量
     * @required true
     */
    private int totalComplete;

    /**
     * 待研判数量
     * @required true
     */
    private int penddingJudged;

    /**
     * 待调度数量
     * @required true
     */
    private int pendingDispatched;

    /**
     * 待处置数量
     * @required true
     */
    private int peddingDisposal;

    /**
     * 待核实数量
     * @required true
     */
    private int pendingApprove;

    /**
     * 待批示数量
     * @required true
     */
    private int penddingLeaderApprove;

    /**
     * 超期数量
     * @required true
     */
    private int overtime;

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
     * 告警数量
     * @required true
     */
    private int allNum;

    /**
     * 真警数量
     * @required true
     */
    private int realNum;

    /**
     * 重复告警数量
     * @required true
     */
    private int repeatCompleteNum;

    /**
     * 误报数量
     * @required true
     */
    private int falseNum;

    /**
     * 处理中数量
     * @required true
     */
    private int processNum;

    /**
     * 准确率
     * @required true
     */
    private String accuracy;

    /**
     * 完结率
     * @required true
     */
    private String completionRate;

    /**
     * 准确率排序
     * @required true
     */
    private String accuracys;

    /**
     * 完结率排序
     * @required true
     */
    private String completionRates;

    /**
     * 告警压制数量
     * @required true
     */
    private int suppressNum;

    /**
     * 待受理数量
     * @required true
     */
    private int acceptanceNum;

    /**
     * 日均告警数量
     * @required true
     */
    private int avgNum;

    /**
     * 摄像机名称
     * @required true
     */
    private String deviceName;

    /**
     * 摄像机编码
     * @required true
     */
    private String deviceCode;

    /**
     * 摄像机编码
     * @required true
     */
    private List<String> deviceCodeList;
}
