package com.chinatower.analysis.api.factory;

import com.chinatower.analysis.api.AnalysisExportTTAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


@Component
public class AnalysisExportTTFallbackFactory implements FallbackFactory<AnalysisExportTTAPI> {
    @Override
    public AnalysisExportTTAPI create(Throwable throwable) {
        return new AnalysisExportTTAPI() {

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
            public TableDataInfo getEventNumDevApp(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
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
            public TableDataInfo getEventAccuracyByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenCompletionByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEventInterceptNumTenant(@RequestBody StatisticsVO statisticsVO){
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
            public TableDataInfo getCustomUserRank(@RequestBody CustomVO customVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
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
            public TableDataInfo getLoginNumRank(@RequestBody CustomVO customVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginDurationRank(@RequestBody CustomVO customVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getLoginNumRankByTenant(@RequestBody CustomVO customVO){
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
            public TableDataInfo getLoginSuccessByTenant(@RequestBody CustomVO customVO){
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
            public TableDataInfo getOrderDeviceByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getTenantOrderDeviceRank(@RequestBody AnalysisVO analysisVO){
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
            public TableDataInfo getOrderIotDeviceByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderUavDeviceByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderRadarDeviceByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderHornDeviceByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderTenantByIotDevice(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderTenantByUavDevice(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderTenantByRadarDevice(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderTenantByHornDevice(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<DeviceStatisticsVO> getRecorderDeviceStatistics (@RequestBody AnalysisVO analysisVO){
                return Result.fail("统计设备-执法记录仪总览失败:" + throwable.getMessage());
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
            public TableDataInfo getOrderRecorderDeviceByTenant(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getOrderTenantByRecorderDevice(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<EventNumVO> getNumAllByDeviceCode(AnalysisVO analysisVO) {
                return Result.fail("获取告警准确率分析方法失败:" + throwable.getMessage());
            }
        };
    }
}
