package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.industry.dto.DeviceInfoByDistanceDTO;
import com.chinatower.industry.dto.RadarTargetDTO;
import com.chinatower.industry.dto.DeviceByConditionDTO;
import com.chinatower.industry.dto.DeviceByDistanceDTO;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.entity.admin.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yy
 */
@Mapper
public interface IndustryDeviceMapper extends BaseMapper<DeviceInfoDO> {


    List<DeviceByDistanceDTO> queryDeviceByDistance(DeviceQueryVO deviceQueryVO);

    List<DeviceInfoByDistanceDTO> queryDeviceInfoByDistance(DeviceInfoQueryVO param);

    DeviceByDistanceDTO getLonLatByDeviceCode(DeviceQueryVO deviceQueryVO);

    List<DeviceByConditionDTO> queryDevicesByCondition(DevicesByConditionVO devicesByConditionVO);

    DevicesStatisticsResVO queryDevicesStatistics(DevicesStatisticsReqVO devicesStatisticsReqVO);

    List<DevicesDeptVO> queryDevicesByDept(DevicesStatisticsReqVO devicesStatisticsReqVO);

    List<String> queryRadarTargetId(@Param("param") RadarTargetDTO radarTargetDTO);

    void deleteRadarByTargetId(@Param("list") List<String> targetIdList);

    List<RadarDeviceStatusVO> queryRadarStatusList(@Param("param") RadarTargetDTO radarTargetDTO);
    /**
     * 获取所设置的最大阈值时间
     * @param configKey 系统配置键
     * @return SysConfig
     */
    SysConfig getSysConfig(String configKey);

    int countIndustryDeviceNum(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                               @Param("appVerCode") String appVerCode);

    int countIndustrySensorDeviceNum(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                               @Param("appVerCode") String appVerCode);

    int countIndustryRadarDeviceNum(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                               @Param("appVerCode") String appVerCode);

    int countIndustryUavDeviceNum(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                               @Param("appVerCode") String appVerCode);

    int countIndustryHornDeviceNum(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                               @Param("appVerCode") String appVerCode);

    int countIndustryRecorderDeviceNum(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                               @Param("appVerCode") String appVerCode);

    List<DeviceByConditionDTO> queryDeviceListByCondition(DevicesByConditionVO devicesByConditionVO);
}
