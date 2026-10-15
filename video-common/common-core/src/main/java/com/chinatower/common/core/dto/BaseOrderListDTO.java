package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询告警列表参数
 */
@Data
public class BaseOrderListDTO extends BasePageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 角色ID
     */
    private String roleId;
    /**
     * 组织ID
     */
    private String orgId;
    /**
     * 查询几天内的数据
     */
    private String dayNum;
    /**
     * 告警类型ID
     */
    private String warningTypeId;
    /**
     * 告警等级ID
     */
    private String emergencyLevel;
    /**
     * 告警状态
     */
    private String orderStatus;
    /**
     * 是否是管理员
     */
    private boolean isAdmin;
    /**
     * 是否是处置人员
     */
    private boolean isHandle;
    /**
     * 是否是研判人员
     */
    private boolean isJudge;
    /**
     * 是否是调度人员
     */
    private boolean isDispatch;
    /**
     * 是否是核实人员
     */
    private boolean isCheck;
    /**
     * 告警标题
     */
    private String alarmBody;
    /**
     * 查询类型:0-全部；1待办;2已办
     */
    private String searchType;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;
    /**
     * APP关键字
     */
    private String keyword;
    /**
     * 设备编码
     */
    private String deviceCodeParm;
    /**
     * 通道编码
     */
    private String channelCodeParm;

    private String orderId;

    private String warningSource;

    private String provinceId;

    private String cityId;

    private String countyId;

    private String alarmTimeStart;

    private String alarmTimeEnd;

    private boolean isExport = false;

    /**
     * 是否超期：1-超期；2-未超期
     */
    private String ifTimeOut;

    /**
     * 环节表名
     */
    private String flowTableName;

    /**
     * 当前用户组织ID与上级所有组织ID
     */
    private List<String> orgIds;
    /**
     * 封装用户待办SQL
     */
    private String orderStatusSql;
    /**
     * 不包含的告警状态
     */
    private List<String> orderStatuses;
    /**
     * R-ZBZB-202112080822436 zzh 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;

    private List<String> deviceCode;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;

    private String tableName;


    /** 省编码 */
    private List<String> areaCodes;

    /** 组织编码 */
    private List<String> orgCodes;

    /** 用户编码 */
    private List<String> userIds;

    /** 客户编码 */
    private List<String> customerIds;

    /**摄像头编码 */
    private List<String> devices;

    /**区县编码 */
    private List<String> countys;

    /**订单状态 */
    private List<String> warningOrderStatus;

    /** 拼接sql */
    protected String sqlParam;

    /**
     * AI前端告警 1后端 2前端
     */
    private String noticeResource;

    /**
     * 置信度
     */
    private String confidenceLevel;

    /**
     * 环节配置和环节id
     */
    private String flowLinkIds;


    //是否二次识别
    private String isSecondIdentify;
    //二次识别结果
    private String secondIdentifyResult;

    /**
     * 物联设备编码/名称
     */
    private List<String> iotDeviceCode;
}
