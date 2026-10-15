package com.chinatower.bdm.modules.statistics.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.api.entity.GridKeeperRefVO;
import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.bdm.api.entity.InspectionConfStatisticsVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface InspectStatisticsDAO {
     /**查询总览网格数量*/
     @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "dataTable.grid_id")
     Map getGridTotal(InspectStatisticsVo inspectStatisticsVo);

     /**查询网格信息通过gridId*/
     Map getGridById(InspectStatisticsVo inspectStatisticsVo);

     /**查询网格员信息通过keeperId*/
     List<Map> getGridKeeperById(InspectStatisticsVo inspectStatisticsVo);

     /**查询总览网格员数量*/
     @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridKeeperColumn = "dataTable.keeper_id")
     Map getGridKeeperTotal(InspectStatisticsVo inspectStatisticsVo);

     /**查询总览巡查任务数量*/
     @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "dataTable.grid_id",gridKeeperColumn = "dataTable.keeper_id")
     Map getInspectTaskTotal(InspectStatisticsVo inspectStatisticsVo);

     /**查询总览出勤数量*/
     @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "s.grid_id",gridKeeperColumn = "dataTable.keeper_id")
     Map getAttendanceTotal(InspectStatisticsVo inspectStatisticsVo);

     /**查询网格层级数量排行*/
     List<Map> getGridLevel(InspectStatisticsVo inspectStatisticsVo);

     /**查询网网格性别统计*/
     Map getGridKeeperGender(InspectStatisticsVo inspectStatisticsVo);

     /**查询网格员年龄分布*/
     Map getGridKeeperAge(InspectStatisticsVo inspectStatisticsVo);

     /**网格统计*/
     List<Map> getGridStatistics(InspectStatisticsVo inspectStatisticsVo);

     /**通过网格id查询网格长*/
     List<Map> getKeeperNameByGridId(InspectStatisticsVo inspectStatisticsVo);


     /**网格出勤率排行*/
     List<Map> getGridAttendance(InspectStatisticsVo inspectStatisticsVo);

     /**网格长勤率排行*/
     List<Map> getGridKeeperAttendance(InspectStatisticsVo inspectStatisticsVo);

     /**网格长勤率排行*/
     List<Map> getGridKeeperAttendanceByTwo(InspectStatisticsVo inspectStatisticsVo);

     /**网格打卡状态统计*/
     List<Map> getGridClockState(InspectStatisticsVo inspectStatisticsVo);

     /**网格员打卡状态统计*/
     List<Map> getGridKeeperClockState(InspectStatisticsVo inspectStatisticsVo);

     /**
      * 根据网格员id查询归属的网格名称，用,拼接
      * @param list
      * @return
      */
     List<GridKeeperRefVO> getGridNameLevelByKeeper(List<String> list);

     /**网格巡查任务数量排行*/
     List<Map> getGridInspectTask(InspectStatisticsVo inspectStatisticsVo);

     /**网格任务完成率排行*/
     List<Map> getGridInspectTaskCompletionRate(InspectStatisticsVo inspectStatisticsVo);

     /**根据网格员类型统计网格任务完成率*/
     @DS(EbikeDataBaseConstant.BASE)
     @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "dataTable.grid_id",gridKeeperColumn = "r.keeper_id")
     List<Map> getGridTaskCompletionRateByType(InspectStatisticsVo inspectStatisticsVo);

     /**巡查任务反馈端统计*/
     List<Map> getGridInspectTaskBack(InspectStatisticsVo inspectStatisticsVo);

     /**巡查反馈24小时分布*/
     List<Map> getGridInspectTaskBackByHour(InspectStatisticsVo inspectStatisticsVo);

     /**查询单个巡查反馈24小时分布*/
     List<Map> getGridInspectTaskBackByHourById(InspectStatisticsVo inspectStatisticsVo);

     /**巡查任务按日期统计*/
     List<Map> getGridInspectTaskByDay(InspectStatisticsVo inspectStatisticsVo);

     /**巡查任务按日期统计*/
     List<Map> getGridInspectTaskByDayById(InspectStatisticsVo inspectStatisticsVo);

     /**巡查人任务数量排行*/
     List<Map> getGridInspectTaskByPerson(InspectStatisticsVo inspectStatisticsVo);

     /**巡查人任务完成率排行*/
     List<Map> getGridInspectTaskRateByPerson(InspectStatisticsVo inspectStatisticsVo);

     InspectionConfStatisticsVO getInspectionConf(InspectionConfStatisticsVO inspectionConfParam);
}
