package com.chinatower.bdm.modules.licensePlateLibrary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CheckpointVehicleLicensePlateMapper extends BaseMapper<CheckpointVehicleLicensePlate> {
    void updateVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    int insertVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    int checkVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    void deleteVehicleLicensePlate(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    CheckpointVehicleLicensePlate getVehicleLicensePlateDetail(String id);



    List<CheckpointVehicleLicensePlate> listVehicleLicensePlateByCondition(CheckpointVehicleLicensePlate checkpointVehicleLicensePlate);

    void deleteGiveLicenseRelByVehicleNumber(String vehicleNumber,int blackOrWhit);
}
