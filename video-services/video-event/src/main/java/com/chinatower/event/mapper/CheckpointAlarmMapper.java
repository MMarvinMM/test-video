package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.common.core.vo.CheckpointAlarmExportVO;
import com.chinatower.event.api.model.CheckAlarmByDeviceVO;
import com.chinatower.event.api.model.DeviceInfoEntity;
import com.chinatower.event.dto.CheckpointWarningOrderAddDTO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CheckpointAlarmMapper {

    List<CheckpointAlarmDTO> getCheckpointAlarmList(CheckpointAlarmDTO dto);

    List<CheckpointAlarmExportVO> exportCheckpointAlarmList(CheckpointAlarmDTO dto);

    @DS(EbikeDataBaseConstant.BASE)
    List<String> getCheckpointDeviceList(CheckpointAlarmDTO dto);

    @DS(EbikeDataBaseConstant.BASE)
    List<SysDictData> getVehicleTypeDict(String dictType);

    @DS(EbikeDataBaseConstant.PLATFORM)
    List<CheckAlarmByDeviceVO> getCheckAlarmByDevice(CheckAlarmByDeviceVO dto);

    @DS(EbikeDataBaseConstant.BASE)
    List<CheckAlarmByDeviceVO> getDeviceByTown(CheckAlarmByDeviceVO dto);

    @DS(EbikeDataBaseConstant.PLATFORM)
    List<CheckAlarmByDeviceVO> getCheckpointAlarmInfo(CheckpointAlarmDTO dto);

    /**
     * 批量插入车辆进出记录
     */
    int batchInsert(List<CheckpointWarningOrderAddDTO> list);
}
