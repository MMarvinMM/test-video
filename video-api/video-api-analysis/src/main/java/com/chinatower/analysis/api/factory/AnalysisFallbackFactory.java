package com.chinatower.analysis.api.factory;

import com.chinatower.analysis.api.AnalysisAPI;
import com.chinatower.analysis.api.entity.*;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AnalysisFallbackFactory implements FallbackFactory<AnalysisAPI> {
    @Override
    public AnalysisAPI create(Throwable throwable) {
        return new AnalysisAPI(){

            /**
             * 获取事件统计数量
             *
             * @param analysisVO
             * @return
             */
            @Override
            public Result<EventNumVO> getEventNum(AnalysisVO analysisVO) {
                return Result.fail("获取事件统计数量方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getAlarmNumByLabel(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 获取事件统计数量
             *
             * @param analysisVO
             * @return
             */
            @Override
            public Result<EventNumVO> getIndustryEventNum(AnalysisVO analysisVO) {
                return Result.fail("获取事件统计数量方法失败:" + throwable.getMessage());
            }

            /**
             * 统计监控设备数量
             *
             * @param analysisVO
             * @return
             */
            @Override
            public Result<DeviceStatisticsVO> getDeviceStatistics(AnalysisVO analysisVO) {
                return Result.fail("统计监控设备数量方法失败:" + throwable.getMessage());
            }

            /**
             * 统计监控设备数量
             *
             * @param analysisVO
             * @return
             */
            @Override
            public Result<DeviceStatisticsVO> getIndustryDeviceStatistics(AnalysisVO analysisVO) {
                return Result.fail("统计监控设备数量方法失败:" + throwable.getMessage());
            }

            /**
             * 获取设备统计
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<DevicesDataVO>> getDevicesStatistics(QueryParamVO vo) {
                return Result.fail("获取设备统计方法失败:" + throwable.getMessage());
            }


            /**
             * 获取设备统计
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<DevicesDataVO>> getIndustryDevicesStatistics(QueryParamVO vo) {
                return Result.fail("获取设备统计方法失败:" + throwable.getMessage());
            }

            /**
             * 获取告警统计
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<EventDataVO>> getEventStatistics(QueryParamVO vo) {
                return Result.fail("获取告警统计方法失败:" + throwable.getMessage());
            }

            /**
             * 获取告警统计
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<EventDataVO>> getIndustryEventStatistics(QueryParamVO vo) {
                return Result.fail("获取告警统计方法失败:" + throwable.getMessage());
            }

            /**
             * 获取告警看板统计-查询当月数据
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<EventBoardDataVO>> getEventBoardStatistics(EventBoardParamVO vo) {
                return Result.fail("获取告警看板统计-查询当月数据方法失败:" + throwable.getMessage());
            }


            /**
             * 获取告警看板统计-查询当月数据
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<EventBoardDataVO>> getIndustryEventBoardStatistics(EventBoardParamVO vo) {
                return Result.fail("获取告警看板统计-查询当月数据方法失败:" + throwable.getMessage());
            }

            /**
             * 站点维度top10
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<DeviceTopTenDataVO>> getDeviceTopTen(@Valid DeviceTopTenParamVO vo) {
                return Result.fail("站点维度top10方法失败:" + throwable.getMessage());
            }

            /**
             * 站点维度top10
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<DeviceTopTenDataVO>> getIndustryDeviceTopTen(@Valid DeviceTopTenParamVO vo) {
                return Result.fail("站点维度top10方法失败:" + throwable.getMessage());
            }

            /**
             * 告警类型统计
             *
             * @param vo
             * @return
             */
            @Override
            public Result<List<EventStatisticDataVO>> getEventTypeStatistics(@Valid EventStatisticParamVO vo) {
                return Result.fail("告警类型统计方法失败:" + throwable.getMessage());
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
            public TableDataInfo getDeviceNumInEvent(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<List<Map<String, Object>>> getEventPlotAreaStatistics(EventBoardParamVO paramVO) {
                return Result.fail("获取告警地块面积方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getEventAlarmNumSourceIndustry(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEventAlarmNumTypeIndustry(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getEvenAreaCompletionIndustry(@RequestBody AnalysisVO analysisVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getEventNumListAppMonth(@RequestBody AnalysisVO analysisVO){
                return Result.fail("告警统计月份折线图方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getDevTypeYearMonthAppTop(@RequestBody AnalysisVO analysisVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getDeviceDayAndNightNum(@RequestBody StatisticsVO statisticsVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getAlarmStatisticsProcessingNum(StatisticsVO statisticsVO) {
                return Result.fail("告警进度处理统计方法失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getCamaraDeviceAreaDistributionNum(StatisticsVO statisticsVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getIotDeviceAreaDistributionNum(StatisticsVO statisticsVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result getAlarmEventTrendNumByHour(AnalysisVO analysisVO) {
                return Result.fail("24小时告警趋势方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getEventNumLevelList(AnalysisVO analysisVO) {
                return Result.fail("获取事件统计数量小时/日期折线图(含告警等级对应数量)失败:" + throwable.getMessage());
            }

            @Override
            public Result getDeviceMaxLevelList(AnalysisVO analysisVO) {
                return Result.fail("查询设备未处理最高告警等级失败:" + throwable.getMessage());
            }
            @Override
            public Result<List<AlarmNumByDateAndTypeVO>> getEventNumTypeList(AnalysisVO analysisVO) {
                return Result.fail("获取事件统计数量小时/日期折线图(含告警类型对应数量):" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getDevTypeYearMonthAppScreen(@RequestBody AnalysisVO analysisVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }
        };
    }
}
