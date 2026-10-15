package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AlarmTypeReqVo implements Serializable {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 是否打开运营研判模式 1：打开 0关闭
     */
    private String isOperation;

    /**
     * 告警标签集合
     */
    private List<AlarmTypeReqVo> alarmTypeList;

    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 告警类型
     */
    private String operationAlarmTypeStr;

    /**
     * 修改人
     */
    private String modifier;

    /**
     *运营研判模式：1:串行 2:并行
     */
    private String operationType;

    /**
     * 删除标识
     */
    private Integer deleteFlag;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 研判节点
     */
    private String operationNode;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 应用版本编码
     */
    private String appVerCode;

    private String operationDept;

    /**
     * 同步标识
     */
    private Boolean syncFlag = false;
}
