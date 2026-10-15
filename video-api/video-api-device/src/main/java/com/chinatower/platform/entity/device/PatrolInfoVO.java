package com.chinatower.platform.entity.device;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName PatrolInfoVO
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/28
 **/

@Data
public class PatrolInfoVO {

    /**
     * 巡航路径id
     */
    private Long patrolId;

    /**
     * 巡航路径名称
     */
    private String patrolName;

    private String deviceCode;

    private String channelCode;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 巡航启动状态：1-停用；2-启用；
     */
    private String ifCruise;

    /**
     * 巡航计划时间段
     */
    private String startTimePeriod;

    /**
     * 预约启动状态：1-停用；2-启用；
     */
    private String startStop;

    /**
     * 预约启动时间
     */
    private Date startStopDate;

    private List<PatrolInfoVO> patrolVOList;
    /**
     * 原始巡航路径id
     */
    private String sourcePatrolId;

    private String appVerCode;

    private String industryCode;

}
