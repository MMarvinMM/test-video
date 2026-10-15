package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

/**
 * 传感器状态数据对象
 * Created by zhujun on 2022/3/31.
 */
@Data
public class SensorStatusReportDTO extends BaseMongoDTO {
    // 设备编码
    private String deviceCode;
    // 更新时间 格式yyyy-MM-dd HH:mm:ss
    private String lastTime;
    // 在线状态 在线状态: 0不在线 1在线
    private int onlineStatus;

    /**
     * 传感器监测因子状态列表
     */
    // 监测因子中文描述
    private String monitorDiscription;

    // 监测因子名称
    private String monitorName;

    // 监测因子状态 有效状态：0-无效；1-有效
    private int status;

    // 监测时间 格式yyyy-MM-dd HH:mm:ss
    private String monitorTime;

    public SensorStatusReportDTO() {
        this.collectionName = "sensor_status_report";
    }
}
