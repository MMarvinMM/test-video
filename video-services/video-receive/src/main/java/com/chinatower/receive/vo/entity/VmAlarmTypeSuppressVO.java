package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @className: VmAlarmTypeSuppressVo
 * @description: 告警压制告警类型bean
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Data
public class VmAlarmTypeSuppressVO {


    /**
     * collection_id
     */
    private Integer collectionId;
    /**
     * 告警压制策略ID
     */
    private Integer alarmSuppressId;
    /**
     * 类型分类ID
     */
    private String alarmTypeClassId;
    /**
     * 告警类型ID
     */
    private String alarmTypeId;
    /**
     * 告警类型值
     */
    private String typeValue;

    /**
     * 告警类型对应的阈值
     */
    private String thresholdValue;

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
