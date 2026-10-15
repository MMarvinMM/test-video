package com.chinatower.bdm.modules.licensePlateLibrary.mapper;

import com.chinatower.bdm.api.entity.CheckpointVehicleTypeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 车辆类型mapper
 * @author Liqi
 * @date 2024/01/25
 * @requestID
 */
@Mapper
public interface CheckpointVehicleTypeMapper {

    List<CheckpointVehicleTypeVO> getVehicleTypeList(CheckpointVehicleTypeVO checkpointVehicleTypeVO);


    void updateVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO);

    void insertVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO);

    int checkVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO);

    void deleteVehicleType(CheckpointVehicleTypeVO checkpointVehicleTypeVO);
}
