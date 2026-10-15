package com.chinatower.platform.constants;

/**
 * 北向接口相关常量
 * Created by zheng.yan on 2022/1/18.
 */
public class DeviceNorthIntfConstants {
    public static final String RETURN_CODE = "returnCode";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MSG = "errorMsg";
    public static final String DATA = "data";
    /** 正常状态 */
    public static final String SUCCESS_STATUS_CODE = "0000";

    /** 添加巡航路径 */
    public static final String SERVICE_NAME_ADDCRUISEPATROL = "addCruisePatrol";
    /** 删除巡航路径 */
    public static final String SERVICE_NAME_DELPATROL = "delPatrol";
    /** 批量删除巡航路径 */
    public static final String SERVICE_NAME_BATCHDELPATROL = "batchDelPatrol";
    /** 修改巡航路径 */
    public static final String SERVICE_NAME_UPDATECRUISEPATROL = "updateCruisePatrol";
    /** 启用/停用巡航路径*/
    public static final String SERVICE_NAME_DOPATROL = "doPatrol";
    /** 批量启用/停用巡航路径*/
    public static final String SERVICE_NAME_BATCHSTARTORENDPATROL = "batchStartOrEndPatrol";
    public static final String PATROL_ID = "patrolId";

    /** 增加预置点 */
    public static final String SERVICE_NAME_ADD_CAMERA_PRESET = "addCameraPreset";
    public static final String PRESET_ID = "presetId";
    public static final String PRESET_NAME = "presetName";
    public static final String CREATE_TIME = "createTime";
    /** 增加看守位 */
    public static final String GUARDID = "guardId";

    /** 删除预置点 */
    public static final String SERVICE_NAME_DEL_CAMERA_PRESET = "delCameraPreset";

    public static final String QUERY_DEVICE_BY_CHANNEL_CODE = "queryDeviceByChannelCode";
    /** 批量 删除预置点 */
    public static final String SERVICE_NAME_BATCH_DEL_CAMERA_PRESET = "batchDelCameraPreset";
    /** 修改预置点 */
    public static final String SERVICE_NAME_UPDATE_PRESET_INFO = "updatePresetInfo";
    /** 设备/通道/预置点/监控区域/算法关系推送 */
    public static final String SERVICE_NAME_SYCH_DEVICE_ALGORITHMS = "synchDeviceAlgorithms";
    /** 设置看守位 */
    public static final String SERVICE_NAME_DO_SET_GUARD = "doSetGuard";

    /** 新增通道录像计划 */
    public static final String SERVICE_NAME_ADD_RECORDPLAN = "addRecordPlan";
    /** 修改通道录像计划 */
    public static final String SERVICE_NAME_UPDATE_RECORDPLAN = "updateRecordPlan";
    /** 批量启用/停用通道录像计划 */
    public static final String SERVICE_NAME_BATCH_STARTOREND_RECORDPLAN = "batchStartOrEndRecordPlan";
    /** 删除通道录像计划 */
    public static final String SERVICE_NAME_DELETE_RECORDPLAN = "deleteRecordPlan";
    /** 批量删除通道录像计划 */
    public static final String SERVICE_NAME_BATCH_DELETE_RECORDPLAN = "batchDeleteRecordPlan";
    /** 设备/通道/预置点/监控区域/算法关系推 */
    public static final String SERVICE_NAME_SYNCH_DEVICE_ALGORITHMS = "synchDeviceAlgorithms";
    /** 查询巡航路径列表 */
    public static final String SERVICE_NAME_QUERY_PATROL = "queryPatrol";
    /** 查询事件告警详情 */
    public static final String SERVICE_NAME_GET_ALARM_INFOL = "getAlarmInfo";

    /** 算法状态查询 */
    public static final String SYNCH_ALGORITHM_CONFIG_STATUS = "synchAlgorithmConfigStatus";
    /** 获取ptz */
    public static final String QUERY_PTZ_POSITION = "queryPtzPosition";

    public static final String IS_ALL_Day = "isAllDay";
    public static final String TIMESECTIONS = "timeSections";
    public static final String RECORD_PLAN_ID = "recordPlanId";
    public static final String STORAGE_TYPE = "storageType";
    public static final String STREAM_TYPE = "streamType";
    public static final String STORAGEDAY = "storageDay";
    public static final String ISAUDIO = "isAudio";
    public static final String RECORDPLANNAME = "recordPlanName";
    public static final String RECORDPLANSTATUS = "recordPlanStatus";
    public static final String STARTDATE = "startDate";
    public static final String ENDDATE = "endDate";
    public static final String DEVICECODE = "deviceCode";
    public static final String CHANNELCODE = "channelCode";
    public static final String RECORDPLANIDS = "recordPlanIds";
    /** 6.36.	增加通道告警联动预案策略*/
    public static final String ADD_ALARM_LINKAGE_LIST = "addAlarmLinkageList";
    /** 6.37.	修改通道告警联动预案策略*/
    public static final String UPDATE_ALARM_LINKAGE_LIST = "updateAlarmLinkageList";
    /** 6.38.	删除通道告警联动预案策略*/
    public static final String DEL_ALARM_LINKAGE_LIST = "delAlarmLinkageList";

    public static final String TOPIC_MESSAGE = "topicMessage:";
    public static final String TOPIC_NAME = "topicName:";
    public static final String SEND_SUCCESS = "发送成功!";
    public static final String CHANNEL_CODE = "channel_code";
    public static final String CHANNEL_ID = "channel_id";
    public static final String DELETE_FLAG = "delete_flag";
    public static final String LABEL_NUM = "labelNum";
    public static final String NUM = "num";
    public static final String IS_MONITOR_NUM = "isMonitorNum";
    public static final String BEIJING_PROVINCE = "110000";
    public static final String BEIJING_CITY = "110100";
    public static final String HAIDIAN_COUNTY = "110108";
    public static final String GET_USER_INFO_ERROR = "获取用户信息失败";
    public static final String TOWER_CANNOT_OPERATE = "铁塔账号不可操作";
    public static final String RESERVE_PLAN_NOT_EXIST = "预案不存在！";
    public static final String RESERVE_PLAN_NAME_UNIPUE = "预案名称重复，请修改";


    public static final String REQUEST_LOG = "请求报文:";
    public static final String RESPONSE_LOG = "返回报文:";

    public static final String ADD_ALARM_LINKAGE_ERROR = "增加通道告警联动预案策略处理结果异常";
    public static final String UPDATE_ALARM_LINKAGE_ERROR = "修改通道告警联动预案策略处理结果异常";
    public static final String DELETE_ALARM_LINKAGE_ERROR = "删除通道告警联动预案策略处理结果异常";
    public static final String DELETE_ALARM_LINKAGE_TIMEOUT = "删除通道告警联动预案策略超时";
    public static final String UPDATE_ALARM_LINKAGE_TIMEOUT = "修改通道告警联动预案策略超时";
    public static final String ADD_ALARM_LINKAGE_TIMEOUT = "增加通道告警联动预案策略超时";
    public static final String GET_DEVICE_INFO_DETAIL_ERROR = "设备信息查询处理结果异常";

    /**
     * 受理节点
     */
    public static final String ACCEPTNODE = "acceptNode";
    /**
     * 接入节点
     */
    public static final String ACCESSNODE = "accessNode";
}
