package com.chinatower.device.mongodb.dto;

import com.chinatower.device.entity.vo.RadarDeviceVO;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 雷达摄像机设备关联表
 */
@Data
@Document(collection = "radar_device_config_rel")
public class RadarDeviceConfigRelDTO extends BaseMongoDTO {

    private String id;
    // 设备编码
    private String deviceCode;
    // 监测时间 格式yyyy-MM-dd HH:mm:ss
    private Date updateTime;
    // 雷达关联摄像机相关信息
    private RadarDeviceVO radarDeviceInfo;

    public RadarDeviceConfigRelDTO() {
        this.collectionName = "radar_device_config_rel";
    }

}
