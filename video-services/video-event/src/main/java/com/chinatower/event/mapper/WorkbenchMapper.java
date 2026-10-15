package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.vo.entity.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.device.RecorderDeviceDTO;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface WorkbenchMapper {

    /**
     * 统计监控设备数量
     * @param analysisVO (year 年 month 月 day 日)
     *
     * @return deviceCountTotal 摄像机总数
     * @return devCountOnline 摄像机在线数
     * @return devCountOffline 摄像机离线数
     * @return channelCountTotal 通道总数
     * @return channelCountOnline   通道在线数
     * @return channelCountOffline 通道离线数
     */
    Map<String,Object> getStatisticsDevices(AnalysisVO analysisVO);

    /**统计物联设备数量*/
    IotDeviceStatisticsVo getIotDeviceStatisticsOrder(@Param("param") AnalysisVO analysisVO);

    /**查询无人机设备数量根据设备类型分组*/
    List<DeviceTypeStatisticsVO> getUavDeviceStatisticsByType(@Param("param") AnalysisVO analysisVO);

    /**查询喇叭设备数量根据设备类型分组*/
    List<DeviceTypeStatisticsVO> getHornDeviceStatisticsByType(@Param("param") AnalysisVO analysisVO);

    /**查询物联设备数量根据设备类型分组*/
    List<DeviceTypeStatisticsVO> getIotDeviceStatisticsByType(@Param("param") AnalysisVO analysisVO);

    /**查询雷达设备数量根据设备类型分组*/
    List<DeviceTypeStatisticsVO> getRadarDeviceStatisticsByType(@Param("param") AnalysisVO analysisVO);

    HornDeviceStatisticsVo getHornDeviceStatistics(@Param("param") AnalysisVO analysisVO);

    RadarDeviceStatisticsVo getRadarDeviceStatisticsOrder(@Param("param") AnalysisVO analysisVO);

    List<RecorderDeviceDTO> getRecorderDeviceStatistics(RecorderDeviceVO recorderDeviceVO);

}
