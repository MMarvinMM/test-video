package com.chinatower.platform.service.impl;

import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.platform.mapper.BdmCheckpointVehicleLicensePlateMapper;
import com.chinatower.platform.service.BdmCheckpointVehicleLicensePlateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: CheckpointVehicleLicensePlateServiceImpl
 * @description: 车牌号黑白名单导出服务实现
 * @author: fengty
 * @create: 2024-01-31 15:49
 */
@Service
@Slf4j
public class BdmCheckpointVehicleLicensePlateServiceImpl implements BdmCheckpointVehicleLicensePlateService {
    @Autowired
    BdmCheckpointVehicleLicensePlateMapper checkpointVehicleLicensePlateMapper;

    @Override
    public List<CheckpointVehicleLicensePlate> listVehicleLicensePlateByCondition(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate) {
        List<CheckpointVehicleLicensePlate> list = checkpointVehicleLicensePlateMapper.listVehicleLicensePlateByCondition(checkpointVehicleLicensePlate);
        return list;
    }
}
