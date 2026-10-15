package com.chinatower.platform.mapper;

import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: BdmCheckpointVehicleLicensePlateMapper
 * @description: 车牌号黑白名单导出
 * @author: fengty
 * @create: 2024-01-31 15:56
 */
@Mapper
public interface BdmCheckpointVehicleLicensePlateMapper {
    List<CheckpointVehicleLicensePlate> listVehicleLicensePlateByCondition(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);
}
