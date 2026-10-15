package com.chinatower.platform.service;

import com.chinatower.platform.entity.WaterLevelInfo;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: IWaterLevelService
 * @description: 水位球导出服务接口
 * @author: fengty
 * @create: 2024-12-13 16:06
 */
public interface IWaterLevelService {
    List<WaterLevelInfo> exportWaterLevel(WaterLevelInfo waterLevelRequestVO);
}
