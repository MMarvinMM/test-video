package com.chinatower.device.mapper;

import com.chinatower.device.dto.CasCadeOrgInitDTO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.EnergyDeviceStationVO;
import com.chinatower.device.entity.vo.EnergyDeviceStatusStatisticsVO;
import com.chinatower.device.entity.vo.EnergyDeviceTreeVO;
import com.chinatower.device.entity.vo.EnergyDeviceVO;
import com.chinatower.platform.entity.device.CasCadeOrgBindVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnergyDeviceInfoMapper {

    List<EnergyDeviceStationVO> queryStationList(@Param("param") EnergyDeviceStationQueryReq param);

    List<EnergyDeviceVO> queryDeviceList(@Param("param") EnergyDeviceQueryReq param);

    List<EnergyDeviceStatusStatisticsVO> queryDeviceStatusStatisticsByStation(@Param("param") EnergyDeviceStatusStatisticsQueryReq param);

    EnergyDeviceStatusStatisticsVO queryDeviceStatusStatistics(@Param("param") EnergyDeviceStatusStatisticsQueryReq param);

    List<EnergyDeviceTreeVO> queryDeviceTree(@Param("param") EnergyDeviceTreeQueryReq param);

    List<String> queryDeviceListByCondition(@Param("param") EnergyDeviceQueryDTO param);

    List<String> queryOrderStationCodeList(@Param("tenantId")String tenantId, @Param("industryCode") String industryCode);

    List<EnergyDeviceStationVO> queryAllStationInfo();

    Integer updateStationRegionBatch(@Param("list")List<EnergyDeviceStationVO> list);

    Integer refreshChannelCustomCode();

    Integer refreshOrgCode();

    Integer insertCascadeOrgBatch(@Param("list")List<CasCadeOrgBindVO> list);

    List<CasCadeOrgInitDTO> queryCascadeDeviceOrgInfo();

    List<CasCadeOrgBindVO> queryCascadeOrgInfo();
}
