package com.chinatower.video.modules.device.entity;

import com.chinatower.video.modules.arButton.entity.ArButtonInfo;
import lombok.Data;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: DeviceDTO
 * @description: 用于video服务查询device相关表出入参
 * @author: fengty
 * @create: 2024-06-06 14:24
 */
@Data
public class DeviceDTO extends ArButtonInfo {
    private String DeviceName;

    private List<String> deviceCodes;
}
