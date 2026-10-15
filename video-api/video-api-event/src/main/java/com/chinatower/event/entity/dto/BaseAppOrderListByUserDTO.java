package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * APP获取当前登录人上报的事件列表入参
 */
@Data
public class BaseAppOrderListByUserDTO extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * @required true
     */
    private String userId;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 角色ID
     * @required false
     */
    private String roleId;

    /**
     * 组织ID
     * @required false
     */
    private String orgId;

    /**
     * 模块：1-事件处置；2-事件研判；3-事件调度；4-事件核实
     * @required false
     */
    private String orderType;

    /**
     * 查询类型：0-已上报；
     * 事件处置：1-全部；2-待处置；3-已处置；4-待认领
     * 事件研判：1-全部；2-待研判；3-已研判
     * 事件调度：1-全部；2-待调度；3-已调度
     * 事件核实：1-全部；2-待核实；3-已核实
     * @required false
     */
    private String searchType;

    /**
     * 是否超期：1-超期；2-未超期
     * @required false
     */
    private String ifTimeOut;

    /**
     * 是否是管理员
     * @required false
     */
    private boolean isAdmin;

    /**
     * 是否是处置人员
     * @required false
     */
    private boolean isHandle;

    /**
     * 告警开始时间
     * @required true
     */
    private String alarmTimeStart;

    /**
     * 告警结束时间
     * @required true
     */
    private String alarmTimeEnd;

    /**
     * 告警类型ID
     * @required false
     */
    private String warningTypeId;

    /**
     * 告警等级ID
     * @required false
     */
    private String emergencyLevel;

    /**
     * 告警标题
     * @required false
     */
    private String alarmBody;

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

    /**
     * 环节表名
     */
    private String flowTableName;

    /**
     * 领导批示状态，查询领导批示列表用
     * @required true
     */
    private String leaderStatus;

    /**
     * 告警状态
     * @required false
     */
    private String orderStatus;

    /**
     * 当前用户组织ID与上级所有组织ID
     * @required false
     */
    private List<String> orgIds;

    /**
     * 设备编码
     * @required false
     */
    private String deviceCodeParm;

    /**
     * 通道编码
     * @required false
     */
    private String channelCodeParm;

    /**
     * 告警单id
     * @required false
     */
    private String orderId;

    /**
     * 领导批示状态
     * @required false
     */
    private String status;

    /**
     * R-ZBZB-202112080822436 zzh 告警重要等级 1:1级,2:2级
     * @required false
     */
    private String importanceLevel;

    /**
     * 设备厂家
     * @required false
     */
    private String modelCode;

    /**
     * 设备信息集合
     * @required false
     */
    private List<AlarmOrderByDeviceDTO> devList;
}
