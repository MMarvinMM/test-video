package com.chinatower.receive.vo.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: lc
 * @Date: 2022年4月24日
 * @Description:
 * 车辆卡口摄像机告警接收请求实体
 */
@Data
public class ReceiveCheckpointAlarmVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -4592163995318480545L;

    /** 告警单ID */
    private String warningOrderId;
    /** 告警类型 */
    private String alarmType;
    /** 告警时间 */
    private String alarmTime;
    /** 事件标题 */
    private String alarmBody;
    /** 设备编码 */
    private String deviceCode;
    /** 设备名称 */
    private String deviceName;
    /** 通道编码 */
    private String channelCode;
    /** 通道名称 */
    private String channelName;
    /** P位置 */
    private String pan;
    /** T位置 */
    private String tilt;
    /** Z位置 */
    private String zoom;
    /** 设备经度 */
    private String longitude;
    /** 设备纬度 */
    private String latitude;
    /** 告警编码 */
    private String alarmCode;
    /** 算法编码 */
    private String algorithmCode;
    /** 原始图片文件url */
    private String originalPictureUrl;
    /** 监控任务唯一标识 */
    private String taskId;
    /** 报警视频url */
    private String alarmVideoUrl;
    /** 报警框图url */
    private String alarmPictureUrl;
    /** 市编码 */
    private String cityCode;
    /** 县编码 */
    private String countyCode;
    /** 省编码 */
    private String provinceCode;
    /** 市名称 */
    private String cityName;
    /** 县名称*/
    private String countyName;
    /** 省名称 */
    private String provinceName;
    /** 1-1级，需应用在告警列表展示，默认值为1；2-2级，需在工作台内静默展示；*/
    private String importanceLevel;
    /** 告警来源 1-算法AI告警；2-雷达告警；3-无人机告警；*/
    private String alarmSource;
    /** 车牌号 */
    private String vehicleNumber;
    /** 车牌类型 */
    private String vehicleType;
    /** 车牌颜⾊ */
    private String vehicleNumColor;
    /** 车辆颜⾊ */
    private String vehicleColor;
    /** 租户名称 */
    private String tenantName;

}
