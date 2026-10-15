package com.chinatower.airline.mapper;

import com.chinatower.device.entity.dto.VmAirLineSensorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cuibaoluo
 * @date 2025/12/16
 * @description：
 */
@Mapper
public interface AirLineSensorInfoMapper {

    /**
     * 批量插入航线喇叭信息
     * @param list 喇叭信息列表
     * @return 受影响行数
     */
    int batchInsert(@Param("list") List<VmAirLineSensorInfo> list);

    /**
     * 根据航线ID查询未删除的喇叭信息列表
     * @param flightRouteId 航线ID
     * @return 喇叭信息列表
     */
    List<VmAirLineSensorInfo> selectByFlightRouteId(@Param("flightRouteId") String flightRouteId);


    List<VmAirLineSensorInfo> selectByFlightRouteIdAndPointIndex(@Param("flightRouteId") String flightRouteId, @Param("pointIndex") String pointIndex );
    /**
     * 批量查询多个航线ID的喇叭信息
     * @param flightRouteIds 航线ID列表
     * @return 喇叭信息列表
     */
    List<VmAirLineSensorInfo> selectBatchByFlightRouteIds(@Param("flightRouteIds") List<String> flightRouteIds);

    /**
     * 删除航线对应的数据
     * @param flightRouteId
     * @return
     */
    int logicDeleteByFlightRouteId(@Param("flightRouteId") String flightRouteId);
}
