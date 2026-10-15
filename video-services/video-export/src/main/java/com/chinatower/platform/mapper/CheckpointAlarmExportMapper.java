package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.CheckpointAlarmDTO;
import com.chinatower.common.core.vo.CheckpointAlarmExportVO;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CheckpointAlarmExportMapper {


    /**
     * 功能描述：导出车辆进出记录
     * @Author：zl
     * @param dto
     * @return List<CheckpointAlarmExportVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @DS(TableEnum.PLATFORM)
    List<CheckpointAlarmExportVO> exportCheckpointAlarmList(CheckpointAlarmDTO dto);

    List<SysDictData> getVehicleTypeDict(String dictType);
}
