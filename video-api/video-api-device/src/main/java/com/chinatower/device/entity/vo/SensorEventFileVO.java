package com.chinatower.device.entity.vo;

import lombok.Data;
/**
 * 传感器监测事件文件信息
 */
import java.io.Serializable;
import java.util.List;

@Data
public class SensorEventFileVO implements Serializable {

    private String deviceCode;

    private String deviceType;

    private String monitorTime;

    private String relationId;

    private List<FileInfoVO> files;

}
