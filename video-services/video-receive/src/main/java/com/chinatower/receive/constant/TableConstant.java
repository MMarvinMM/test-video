package com.chinatower.receive.constant;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TableConstant.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/24 10:46
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public class TableConstant {
    /**
     * 工单告警表
     * vm_warning_order_t_
     */
    public final static String WARN_ORDER_TEBLE = "vm_warning_order_t_";

    /**
     * 流程记录表
     * vm_warning_order_t_
     */
    public final static String WARN_ORDER_PROCESS_TEBLE = "vm_order_flow_record_t_";

    /**
     * 卫星告警表（mongo）
     * vm_warning_order_t_
     */
    public final static String WARN_ORDER_SATELLITE_TEBLE = "vm_order_satellite_t_";

    /**
     * 推送第三方告警记录表（mongo）
     * vm_warning_order_t_
     */
    public final static String SYS_HISTORY_LOGS_TEBLE = "sys_history_logs_t_";

    /**
     * 延迟消息推送（mongo）
     * vm_warning_order_t_
     */
    public final static String SYS_ORDER_DELAY_TEBLE = "order_delay_message_t";


    /**
     * 告警视频表表
     * vm_warning_order_t_
     */
    public final static String WARN_VIDEO_TEBLE = "vm_warning_video_t_";

    /**
     * 状态字段
     */
    public static final String FIELD_DELETE_FLAG = "delete_flag";

    /**
     * 收藏表
     */
    public static final String WAR_ORD_COLL = "vm_warning_order_collection_t";

    /**
     * 告警事件框图图片告警点表
     * vm_warning_original_point_t_
     */
    public final static String WARN_ORIGINAL_POINT_TABLE = "vm_warning_original_point_t_";

    /**
     * 告警研判表
     * vm_alarm_operate_t
     */
    public final static String WARN_ALARM_OPERATE = "vm_alarm_operate_t";

    /**
     * 属地运营研判表
     */
    public final static String WARN_LOCAL_ALARM_OPERATE = "vm_local_alarm_operate_t";

    /**
     * 回收站表
     * vm_warning_intercept_t
     */
    public final static String WARN_ALARM_INTERCEPT = "vm_warning_intercept_t";

    /**
     * 物联设备
     * vm_warning_sensor_monitor_t_
     */
    public final static String WARN_ALARM_SENSOR = "vm_warning_sensor_monitor_t_";

    /**
     * 告警原始图片区域
     * vm_warning_sensor_monitor_t_
     */
    public final static String VM_WARNING_ORIGINAL_AREA_T = "vm_warning_original_area_t";

    /**
     * 告警原始图片区域点
     * vm_warning_sensor_monitor_t_
     */
    public final static String VM_WARNING_ORIGINAL_POINT_T = "vm_warning_original_point_t";
}
