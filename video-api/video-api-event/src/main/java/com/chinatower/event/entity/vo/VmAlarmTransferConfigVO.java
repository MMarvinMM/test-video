package com.chinatower.event.entity.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.event.entity.dto.VmAlarmFlowTemplateTypeDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className: VmAlarmTransferConfigVO
 * @description: 告警转移策略 bean
 * @author: lyy
 * @date: 2023/06/30
 * @version v1.6.00
 */
@Data
public class VmAlarmTransferConfigVO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 告警转移策略ID
     */
    private Long alarmTransferId;

    /**
     * 告警转移策略名称
     */
    private String alarmTransferName;

    /**
     * 是否从未处理 （1：是  0：否）
     */
    private String neverProcessFlag;

    /**
     * 事件状态 （1待研判 、2待调度、4待处置、5待核实、6已完结、9待受理）
     */
    private String orderStatus;

    /**
     * 告警转移时间 （0：每天 1：每月）
     */
    private String alarmTransferTime;

    /**
     * 日期 （1-31正整数）
     */
    private String transferDate;

    /**
     * 转移时间间隔（单位天，1-366正整数）
     */
    private Integer transferInterval;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 行业应用版本名称
     */
    private String industryName;

    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 关联的告警类型集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VmAlarmTypeTransferVO> alarmTypeTransferList;

}
