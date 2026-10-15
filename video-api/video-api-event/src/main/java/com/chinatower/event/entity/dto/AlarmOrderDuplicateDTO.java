package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BasePageDTO;
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
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/4 14:30
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmOrderDuplicateDTO extends BasePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警结束时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeEnd;

    /**
     * 告警开始时间
     * @required false
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTimeStart;

    /**
     * 角色ID
     * @required false
     */
    private String roleId;

    /**
     * 查询类型:0-全部；1待办；2已办
     * @required false
     */
    private String searchType;

    /**
     * 经度
     * @required false
     */
    private String longitude;

    /**
     * 纬度
     * @required false
     */
    private String latitude;

    private String currentOrderId;

    private String creator;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 告警内容
     * @required false
     */
    private String alarmBody;

    /**
     * 告警类型编码
     * @required false
     */
    private String warningTypeId;

    /**
     * 事件状态
     * @required false
     */
    private String orderStatus;

    /**
     * 设备编码
     * @required false
     */
    private String deviceCode;

    /**
     * 事件编码
     * @required false
     */
    private String orderId;

    /**
     * 地市编码
     * @required false
     */
    private String cityId;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

    /**
     * 省份编码
     * @required false
     */
    private String provinceId;

    /**
     * 告警来源
     * @required false
     */
    private String warningSource;

    /**
     * 事件等级
     * @required false
     */
    private String emergencyLevel;

    /**
     * 关键字检索（事件编号或告警内容）
     * @required false
     */
    private String keyword;

    /**
     * 是否超期：1-超期；2-未超期
     * @required false
     */
    private String ifTimeOut;

    /**
     * 需过滤的order_status
     */
    private List<String> notInOrderStatus;

    private String tableName;

    /**
     * 行业版本
     * @required true
     */
    private String appVerCode;

    /**
     * 告警类型编码集合
     * @required false
     */
    private List<String> warningTypeIdList;

    /**
     * 下游任务/子场景 id 列表
     * @required false
     */
    private List<String> subSceneIdList;

    /**
     * 告警状态集合
     * @required false
     */
    private List<String> orderStatusList;

    /**
     * 空间范围 （单位：米）
     * @required false
     */
    private String spaceRange;

    /**
     * P范围 （范围值：0~90）
     * @required false
     */
    private String alarmRangeP;

    /**
     * T范围 （范围值：0~90）
     * @required false
     */
    private String alarmRangeT;
}
