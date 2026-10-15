package com.chinatower.event.vo.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @className: VmAlarmDeviceSuppressVo
 * @description: 告警压制设备
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Data
public class VmAlarmDeviceSuppressVO {


    /**
     * device_id
     */
    private Integer deviceId;
    /**
     * 告警压制策略ID
     */
    private Integer alarmSuppressId;
    /**
     * 摄像机code
     */
    private String deviceCode;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 创建人
     */
    private String createrBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 最后更新时间
     */
    private Date updateTime;
    /**
     * 删除标识 0删除 1正常
     */
    private String deleteFlag;

    private List<Integer> strategyIdList;

    private String startDate;

    private String endDate;

    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;

    private String orderByString;
}
