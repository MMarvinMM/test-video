package com.chinatower.event.vo;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName WarningOrderVO
 * @Deacription
 * @Author chenmf
 * @Date 2021/12/1 17:00
 * @Version 1.0
 **/
@Data
public class WarningOrderVO extends BaseMongoDTO {
    //告警ID
    private String warningOrderId;
    //告警时间
    private String alarmTime;
    private Date alarmDate;
    //设备编码
    private String deviceCode;
    //通道编码
    private String channelCode;
    //租户ID
    private String tenantId;
    //告警类型
    private String alarmType;
    private String alarmTypeName;
    //关联算法告警类型
    private String alarmMergeType;
    //主从告警标志
    private int alarmFlag;
    //主告警ID
    private String mainAlarmId;
    //告警内容
    private String alarmBody;
    //设备名称
    private String deviceName;
    //通道名称
    private String channelName;
    //P位置
    private String pan;
    //T位置
    private String tilt;
    //Z位置
    private String zoom;
    //事件源经度
    private String longitude;
    //事件源纬度
    private String latitude;
    //告警编码
    private String alarmCode;
    //算法编码
    private String algorithmCode;
    //原始图片文件url
    private String originalPictureUrl;
    //监控任务唯一标识
    private String taskId;
    //报警视频url
    private String alarmVideoUrl;
    //报警框图url
    private String alarmPictureUrl;
    //告警重要等级
    private int importanceLevel;
    //雷达监测属性
    private String radarParam;
    //扩展字段属性
    private String extendField;
    //告警来源
    private String alarmSource;

    public WarningOrderVO() {
        this.collectionName = "vm_warning_order_t";
    }

    @Override
    public void setCollectionName(String args) {
        this.collectionName = args;
    }
}
