package com.chinatower.platform.service;

import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: CheckpointVehicleLicensePlateService
 * @description: 车牌号黑白名单导出服务接口
 * @author: fengty
 * @create: 2024-01-31 15:45
 */
public interface BdmCheckpointVehicleLicensePlateService {
    List<CheckpointVehicleLicensePlate> listVehicleLicensePlateByCondition(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);
}
