package com.chinatower.event.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOrderDuplicateDTO.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2022/12/4 10:18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmOrderDuplicateDTO extends BasePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 告警结束时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;
    /** 告警开始时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeStart;
    private String roleId;

    private String searchType;

    private String longitude;

    private String latitude;

    private String currentOrderId;

    private String creator;

    private String tenantId;

    private String alarmBody;

    private String warningTypeId;

    private String orderStatus;

    private List<String> deviceCode;
    private String devCode;
    private String warnSource;

    private String orderId;

    private String cityId;

    private String countyId;
    private String provinceId;
    private String warningSource;
    private String emergencyLevel;
    private String keyword;
    private String ifTimeOut;

    /** 需过滤的order_status */
    private List<String> notInOrderStatus;

    private String tableName;
    private String appVerCode;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /**
     * 告警类型标签id列表
     */
    private List<String> tagIdList;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 摄像机厂家集合
     */
    private List<String> modelIds;

    /**
     * 摄像机标签标签
     */
    private List<String> deviceLabelIdList;

    /**
     * 组织ID
     */
    private List<String> orgIdList;

    /**
     *  摄像机、雷达、物联设备编码集合
     */
    private List<String> deviceCodeList;

    /**
     *  卫星区域集合
     */
    private List<SysDeptDeviceVO> satelliteList;

    /**
     *  雷达区域集合
     */
    private List<SysDeptDeviceVO> radarList;

    private List<AlarmOrderByDeviceDTO> devList;

    /**
     * 告警类型批量查询
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务/子场景 id 列表（与 WarningOrderMapper#subScenesOnlyFilter 对齐）
     */
    private List<String> subSceneIdList;

    /**
     * 告警状态批量查询
     */
    private List<String> orderStatusList;

    /**
     * 告警原因批量查询
     */
    private List<String> alarmReasonList;

    /**
     * P位置
     */
    private String transPan;

    /**
     * T位置
     */
    private String transTitle;

    /**
     * Z位置
     */
    private String transZoom;

    /**
     * 空间范围 （单位：米）
     */
    private String spaceRange;

    /**
     * P范围 （范围值：0~90）
     */
    private String alarmRangeP;

    /**
     * 转义最大P范围
     */
    private String alarmRangeMaxP;

    /**
     * 转义最小P范围
     */
    private String alarmRangeMinP;

    /**
     * T范围 （范围值：0~90）
     */
    private String alarmRangeT;

    /**
     * 转义最大T范围
     */
    private String alarmRangeMaxT;

    /**
     * 转义最小T范围
     */
    private String alarmRangeMinT;

    /**
     * 摄像机厂家
     */
    private String modelId;

    /**
     * 摄像机焦距范围
     */
    private String focusRange;

    /**
     * 水平向下为负 0否1是
     */
    private String vertiDownNeg;

    //P范围判断需要的参数
    private String reAlarmRangePMax;
    private String reAlarmRangePMin;
    //P值范围 0~360
    private String rangePMax; //360
    private String rangePMin; //0

}
