package com.chinatower.device.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.entity.dto.IndTenantDeviceDTO;
import com.chinatower.industry.dto.IndEventTrendDTO;
import com.chinatower.industry.dto.IndStatisticDTO;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Name StatisticMapper
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 11 : 18
 */
@Mapper
@DS("statistic")
public interface IndStatisticMapper {
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn = "dataTable.tenant_id",deviceColumn = "dataTable.device_code",industryCode = "dataTable.industry_code")
    IndDeviceVO getStatisticsDevices(IndTenantDeviceDTO indTenantDeviceDTO);

    IndEventAlarmNumVO getEventAlarmNumYearMonth(IndStatisticDTO indStatisticDTO);

    List<IndDevTypeVO> getDevTypeYearMonth(IndStatisticDTO indStatisticDTO);

    List<IndEventNumVO> getEventNumByArea(IndStatisticDTO indStatisticDTO);

    List<IndEventTrendVO> getEventTrendYear(IndEventTrendDTO indEventTrendDTO);

    List<IndEventTrendVO> getEventTrendMonth(IndEventTrendDTO indEventTrendDTO);

    List<IndEventTrendVO> getEventTrendDay(IndEventTrendDTO indEventTrendDTO);

    List<IndEventTrendVO> getEventTrendQuery(IndEventTrendDTO indEventTrendDTO);

   List<Map>  getEventNumByHour(IndAnalysisVO indAnalysisVO);

    @DS("base")
    List<Map<String, String>> getTypeName(List<String> ids);

    List<Map> getAllEventAlarmNumYearMonth(IndStatisticDTO indStatisticDTO);


    @MapKey("label")
    Map getEventTrendYearSearch(IndEventTrendDTO indEventTrendDTO);

    @MapKey("label")
    Map getEventTrendMonthSearch(IndEventTrendDTO indEventTrendDTO);

    @MapKey("label")
    Map getEventTrendDaySearch(IndEventTrendDTO indEventTrendDTO);
}
