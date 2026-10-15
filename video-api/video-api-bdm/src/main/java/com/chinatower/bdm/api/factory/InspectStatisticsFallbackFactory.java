package com.chinatower.bdm.api.factory;

import com.chinatower.bdm.api.InspectStatisticsAPI;
import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.bdm.api.entity.InspectionConfStatisticsVO;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class InspectStatisticsFallbackFactory implements FallbackFactory<InspectStatisticsAPI> {
    @Override
    public InspectStatisticsAPI create(Throwable throwable) {
        return new InspectStatisticsAPI() {
            @Override
            public Result inspectOverview(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
                return Result.fail("查询巡查统计总览失败" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getGridLevel(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getGridKeeperGender(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
                return Result.fail("查询网格性别统计失败" + throwable.getMessage());
            }

            @Override
            public Result getGridKeeperAge(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
                return Result.fail("查询网格年龄统计失败" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getGridStatistics(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridAttendance(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridKeeperAttendance(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridKeeperAttendanceByTwo(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridClockState(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridKeeperClockState(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridInspectTask(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridInspectTaskCompletionRate(InspectStatisticsVo inspectStatisticsVo) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getGridTaskCompletionRateByType(InspectStatisticsVo inspectStatisticsVo) {
                return Result.fail("统计网格任务完成率失败" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getGridTaskCompletionRateToPage(InspectStatisticsVo inspectStatisticsVo) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridInspectTaskBack(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridInspectTaskBackByHour(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridInspectTaskByDay(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getGridInspectTaskByPerson(@RequestBody InspectStatisticsVo inspectStatisticsVo){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<InspectionConfStatisticsVO> getInspectionConf(@RequestBody InspectionConfStatisticsVO inspectionConfParam){
                return Result.fail("查询巡查计划总数失败" + throwable.getMessage());
            }

        };
    }
}
