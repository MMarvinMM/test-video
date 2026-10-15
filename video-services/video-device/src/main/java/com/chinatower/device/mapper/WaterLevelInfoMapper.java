package com.chinatower.device.mapper;

import com.chinatower.device.dto.SysAreaBusinessWaterLevel;
import com.chinatower.device.dto.WaterLevelInfo;
import com.chinatower.platform.entity.devicemgmt.vo.SysAreaBusinessCheckpointVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelInfoMapper
 * @description: 水位管理数据传输层
 * @author: fengty
 * @create: 2024-12-12 11:37
 */
@Repository
public interface WaterLevelInfoMapper {
    List<WaterLevelInfo> queryWaterLevelDeviceInfoList(WaterLevelInfo waterLevelInfo);

    List<WaterLevelInfo> queryWaterDeviceList(List<String> deviceCodes);

    int insertWaterLevelInfo(List<WaterLevelInfo> list);

    int logicDeleteByIds(List<Integer> ids, String userName);

    List<SysAreaBusinessWaterLevel> getDeviceList(WaterLevelInfo waterLevelInfo);

    List<SysAreaBusinessWaterLevel> selectSysArea(WaterLevelInfo waterLevelInfo);
}
