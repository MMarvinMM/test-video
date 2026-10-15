package com.chinatower.analysis.api.factory;

import com.chinatower.analysis.api.AnalysisAPI;
import com.chinatower.analysis.api.AnalysisExportAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


@Component
public class AnalysisExportFallbackFactory implements FallbackFactory<AnalysisExportAPI> {
    @Override
    public AnalysisExportAPI create(Throwable throwable) {
        return new AnalysisExportAPI() {

            @Override
            public Result getNumAll (@RequestBody StatisticsVO statisticsVO){
                return Result.fail("获取事件统计数量方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getDeviceNumAll(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getEventNumListApp(@RequestBody AnalysisVO analysisVO){
                return Result.fail("获取告警时间统计-告警数量折线图方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getEventInterceptNumApp(@RequestBody AnalysisVO analysisVO){
                return Result.fail("获取压制事件统计数量折线图方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getDevTypeYearMonthApp(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEventNumByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEventAlarmNumSource(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getEventNumByHour(@RequestBody AnalysisVO analysisVO){
                return Result.fail("获取今日事件高发时段24时分布方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getEventNumByInterceptHour(@RequestBody AnalysisVO analysisVO){
                return Result.fail("获取24h告警压制统计方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getEvenAccuracy (@RequestBody StatisticsVO statisticsVO){
                return Result.fail("获取告警准确率分析方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getEvenAreaAccuracy(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenTypeAccuracy(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenSourceAccuracy(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenAreaCompletion(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenDeptCompletion(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getCustomNum (@RequestBody CustomVO customVO){
                return Result.fail("获取用户总览方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getCustomNumByLoginTime(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginNumRank(CustomVO customVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginDurationRank(CustomVO customVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginNumByHour(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginTypeByPcOs(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginTypeByPcBrower(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginTypeByAppOs(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginTypeByPhoneModel(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginTypeByAppVersion(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginSuccessRate(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginFailureRate(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<DeviceStatisticsVO> getDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计监控设备数量失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getCameraNumByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderAlgorithmRank(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getIotDeviceNumAll(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<IotDeviceStatisticsVo> getIotDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计设备-物联设备总览失败:" + throwable.getMessage());
            }

            @Override
            public Result<UavDeviceStatisticsVo> getUavDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计设备-无人机设备总览失败:" + throwable.getMessage());
            }

            @Override
            public Result<RadarDeviceStatisticsVo> getRadarDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计设备-雷达设备总览失败:" + throwable.getMessage());
            }

            @Override
            public Result<HornDeviceStatisticsVo> getHornDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计设备-大喇叭设备总览失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getIotDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getUavDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getRadarDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getHornDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getCustomNumByDept (@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEventNumByAreaAndType (@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenGridCompletion (@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getStartAfterAlgorithmTaskRank (@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }


            @Override
            public TableDataInfo getRecorderDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<DeviceStatisticsVO> getRecorderDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计设备-执法记录仪设备总览失败:" + throwable.getMessage());
            }

            @Override
            public Result getAlarmTagYearMonth (@RequestBody StatisticsVO statisticsVO){
                return Result.fail("查询告警数量根据告警标签分类:" + throwable.getMessage());
            }
        };
    }
}
