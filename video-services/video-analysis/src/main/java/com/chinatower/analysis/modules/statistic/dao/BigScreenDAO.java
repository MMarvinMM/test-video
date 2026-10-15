package com.chinatower.analysis.modules.statistic.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BigScreenDAO {

    /**
     * 获取设备统计
     * @param vo
     * @return
     */
    List<DevicesDataVO> getDevicesStatistics(QueryParamVO vo);

    /**
     * 获取告警统计
     * @param vo
     * @return
     */
    List<EventDataVO> getEventStatistics(QueryParamVO vo);

    /**
     * 通过区域编码获取名称和经纬度
     * @param list
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<AreaDataVO> getAreaList(List<String> list);

    /**
     * 获取告警看板统计-查询当月数据
     * @param vo
     * @return
     */
    List<EventBoardDataVO> getEventBoardStatistics(EventBoardParamVO vo);

    /**
     * 站点维度top10
     * @param vo
     * @return
     */
    List<DeviceTopTenDataVO> getDeviceTopTen(DeviceTopTenParamVO vo);

    /**
     * 通过设备编码获取设备信息
     * @param list
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<DeviceTopTenDataVO> getDeviceList(List<String> list);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<DeviceTopTenDataVO> getIotDeviceList(List<String> list);
    /**
     * 获取符合条件所有告警类型
     * @param vo
     * @return
     */
    List<String> getAllEventType(EventStatisticParamVO vo);

    /**
     * 获取告警类型统计
     * @param vo
     * @return
     */
    List<EventStatisticDataVO> getEventTypeStatistics(EventStatisticParamVO vo);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<EventStatisticDataVO> getAlarmType(@Param("list") List<String> list,@Param("vo") EventStatisticParamVO vo);

    /**
     * 查询告警地块面积
     * @param eventBoardParamVO
     * @return
     */
    List<Map<String, Object>> getEventPlotAreaStatistics(EventBoardParamVO eventBoardParamVO);
}
