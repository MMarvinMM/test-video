package com.chinatower.device.service;

import com.chinatower.common.entity.TreeSelect;
import com.chinatower.device.dto.WaterLevelInfo;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelInfoService
 * @description: 水位管理服务层接口
 * @author: fengty
 * @create: 2024-12-12 11:35
 */
public interface IWaterLevelInfoService {
    List<WaterLevelInfo> queryWaterLevelDeviceInfoPage(WaterLevelInfo waterLevelInfo);

    int addWaterDevice(List<String> deviceCodes);

    int deleteWaterDevice(List<Integer> ids);

    List<TreeSelect> initWaterLevelDeviceInfo(WaterLevelInfo waterLevelInfo);
}
