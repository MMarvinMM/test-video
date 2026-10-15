package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author lize
 * @Date 2024/11/4 10:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirPlanPageReq extends PageDTO {

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 执行状态
     */
    private List<String> status;

    private String statusStr;

    private String isItEnabled;

    /**
     * 需要查询的计划id集合
     */
    private List<String> planIds;

    /**l
     * 任务策略
     */
    private List<Integer> planType;

    /**
     * 计划开始时间
     */
    private String planStartTime;

    /**
     * 计划开始时间
     */
    private String planEndTime;

    /**
     * 航线ID
     */
    private String flightRouteId;

    /**
     * 航线名称
     */
    private String flightRouteName;

    /**
     * 无人机名称
     */
    private String deviceName;

    /**
     * 机场名称
     */
    private String uavNestName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 任务计划时间
     */
    private String realityStartTime;

    /**
     * 任务计划时间
     */
    private String realityEndTime;

    /**
     * 是否有复飞任务
     */
    private List<String> isRefly;

    /**
     * 是否开启自动断点续飞
     */
    private Boolean autoResume;

    /**
     * 起始机巢编码
     */
    private String startNestName;
    /**
     * 降落机巢编码
     */
    private String endNestName;
    /**
     * 是否蛙跳：0-否，1-是
     */
    private Integer frogLeap;
}
