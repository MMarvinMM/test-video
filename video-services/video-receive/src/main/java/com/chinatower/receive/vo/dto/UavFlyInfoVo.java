package com.chinatower.receive.vo.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/18 14:20
 */

@Data
public class UavFlyInfoVo {

    /**
     * 实际起飞时间
     */
    private String realityTime;

    /**
     * 飞行时长
     */
    private String flyTime;

    /**
     * 任务id
     */
    private String taskRecordId;

    /**
     * 计划起飞时间
     */
    private String planTime;
}
