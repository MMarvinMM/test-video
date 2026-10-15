package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName EventNumNewDTO
 * @Deacription 获取事件统计数量DTO
 * @Author chenmf
 * @Date 2022/6/7 14:05
 * @Version 1.0
 **/
@Data
public class EventNumNewDTO implements Serializable {
    
    /**
     * 今日事件数量
     */
    private int todayAll;
    
    /**
     * 今日处理中数量
     */
    private int todayProcess;
    
    /**
     * 今日已办结数量
     */
    private int todayComplete;
    
    /**
     * 待研判数量
     */
    private int penddingJudged;
    
    /**
     * 待调度数量
     */
    private int pendingDispatched;
    
    /**
     * 待处置数量
     */
    private int peddingDisposal;
    
    /**
     * 待核实数量
     */
    private int pendingApprove;
    
    /**
     * 待批示数量
     */
    private int penddingLeaderApprove;
    
    /**
     * 已超期数量
     */
    private int overtime;
}
