package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.entity.vo.VmAlarmTypeTransferVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lyy
 * @version 1.6.00
 * @date 2023/06/30
 */
@Data
@ApiModel(description = "告警转移配置查询入参")
public class VmAlarmTransferConfigDTO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -6966106571145117171L;
    /**
     *  主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 告警转移策略名称
     */
    private String alarmTransferName;

    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警类型
     */
    private String alarmType;

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
     * 告警来源+告警类型集合
     */
    private List<VmAlarmTypeTransferVO> alarmSourceAndTypeList;

    /**
     * 操作类型
     */
    private String operType;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeEnd;
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeStart;
    /** 修改结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeEnd;
    /** 修改开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeStart;

    /** sql排序 */
    private String sort;
    private String sortName;

    /**
     * 分页参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;
}
