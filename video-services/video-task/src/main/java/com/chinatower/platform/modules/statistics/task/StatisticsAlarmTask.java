package com.chinatower.platform.modules.statistics.task;

import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.analysis.api.entity.JudgeVO;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.modules.statistics.service.StatisticsAlarmService;
import com.chinatower.platform.modules.statistics.service.UserStatisticsService;
import com.chinatower.platform.utils.DataUtil;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: StatisticsAlarmTask
 * @description: 统计分析定时任务
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class StatisticsAlarmTask {

    @Resource
    private StatisticsAlarmService statisticsAlarmService;

    @Resource
    private UserStatisticsService userStatisticsService;

    @Autowired
    private SystemFeignClient systemFeignClient;
    /**
     * 运营研判统计分析-铁塔视角
     */
    @XxlJob("eventJudgeAlarmHandler")
    public ReturnT<String> eventJudgeAlarmHandler(String param) {
        //获取当前节点
        String platFormCode = this.getSelfPlatFormCode();
        //获取算法统计已升级属地编码
        List<String> upgradeColonyCodeList = this.getUpgradeColonyCode();
        if (StringUtils.isNotEmpty(param)) {
            eventJudgeAlarmHandlerParam(param,platFormCode,upgradeColonyCodeList);
        } else {
            log.info("运营研判统计分析-铁塔视角");
            XxlJobLogger.log("运营研判统计分析-铁塔视角");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            Date dateTime = DateUtil.addDays(new Date(), -1);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sf.format(dateTime);
            dateVO.setHandleDate(dateStr);
            dateVO.setTableName(TableUtil.getTableName(dateTime, Constants.WARN_ORDER_TEBLE));
            dateVO.setPlatFormCode(platFormCode);
            dateVO.setUpgradeColonyCodeList(upgradeColonyCodeList);
            List<JudgeVO> list = statisticsAlarmService.getEventJudgeAlarm(dateVO);
            // 空结果仍先删后写，清掉历史「按场景维度」脏数据
            statisticsAlarmService.insertEventJudgeAlarm(dateVO,
                    list == null ? new ArrayList<>() : list);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 运营研判统计分析
     * @param param
     * @param platFormCode
     * @param upgradeColonyCodeList
     */
    private void eventJudgeAlarmHandlerParam(String param,String platFormCode,List<String> upgradeColonyCodeList) {
        String[] dates = param.split(",");
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] != null && dates[i].length()>=7) {
                String year = dates[i].substring(0, 4);
                String month = dates[i].substring(5, 7);
                log.info("运营研判统计分析-铁塔视角-入参" + dates[i]);
                XxlJobLogger.log("运营研判统计分析-铁塔视角-入参" + dates[i]);
                //初始化执行时间
                DateVO dateVO = DataUtil.setDateVO(dates[i]);
                dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                dateVO.setPlatFormCode(platFormCode);
                dateVO.setUpgradeColonyCodeList(upgradeColonyCodeList);
                List<JudgeVO> list = statisticsAlarmService.getEventJudgeAlarmParam(dateVO);
                // 空结果仍先删后写，清掉历史「按场景维度」脏数据
                statisticsAlarmService.insertEventJudgeAlarmParam(dateVO,
                        list == null ? new ArrayList<>() : list);
            }
        }
    }



    //获取算法统计已升级属地编码
    public List<String> getUpgradeColonyCode(){
        List<String> codeList = new ArrayList<>();
        try {
            Result<List<SysDictData>> result = systemFeignClient.getDictDataByType(Constants.ALGORITHM_TASK_UPGRADE_COLONY);
            if (result.getCode() != ResultCode.SUCCESS.getCode()) {
                log.info("algorithm_task_upgrade_colony-获取算法统计已升级属地失败:{}", JSON.toJSONString(result));
            }else {
                if (!CollectionUtils.isEmpty(result.getData())) {
                    codeList = result.getData().stream().map(v -> v.getDictValue()).collect(Collectors.toList());
                }
            }
        }catch (Exception e){
            throw new CustomException("algorithm_task_upgrade_colony-获取算法统计已升级属地失败:{}",e);
        }

        return codeList;
    }

    /**
     * 获取当前节点
     * @return
     */
    public String getSelfPlatFormCode(){
        String configValue ="";
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))){
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")),SysConfig.class);
            if(sysConfigInfo != null){
                configValue = sysConfigInfo.getConfigValue();
            }else{
                log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
            }
        }else {
            log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
        }
        return configValue;
    }

    /**
     * 统计分析-用户统计
     * @param param
     * @return
     */
    @XxlJob("eventUserStatisticsHandler")
    public ReturnT<String> eventUserStatisticsHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info("统计分析-用户统计定时任务-"+dates[i]);
                    XxlJobLogger.log("统计分析-用户统计定时任务-mongo数据入沉淀表"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    //用户登录时间统计
                    List<Map> timeList = userStatisticsService.getEventCustomListForType(dateVO);
                    if(!CollectionUtils.isEmpty(timeList)){
                        DataUtil.returnString(userStatisticsService.addEventCustomListForType(dateVO,timeList));
                    }
                    //用户登录次数排行
                    List<Map> countList = userStatisticsService.getEventCustomLoginCounts(dateVO);
                    if(!CollectionUtils.isEmpty(countList)){
                        DataUtil.returnString(userStatisticsService.addEventCustomLoginCounts(dateVO,countList));
                    }
                    //用户登录24小时分布
                    List<Map> hourList = userStatisticsService.getEventUserStatistics(dateVO);
                    if (!CollectionUtils.isEmpty(hourList)) {
                        DataUtil.returnString(userStatisticsService.insertEventUserStatistics(dateVO, hourList));
                    }
                    //PC操作系统
                    List<Map> pcList = userStatisticsService.getPcSystemCounts(dateVO);
                    if(!CollectionUtils.isEmpty(pcList)){
                        DataUtil.returnString(userStatisticsService.addPcSystemCounts(dateVO,pcList));
                    }
                    //PC浏览器排行
                    List<Map> browerList = userStatisticsService.getPcBrowserCounts(dateVO);
                    if(!CollectionUtils.isEmpty(browerList)){
                        DataUtil.returnString(userStatisticsService.addPcBrowserCounts(dateVO,browerList));
                    }
                    //APP操作系统
                    List<Map> appList = userStatisticsService.getAppSystemCounts(dateVO);
                    if(!CollectionUtils.isEmpty(appList)){
                        DataUtil.returnString(userStatisticsService.addAppSystemCounts(dateVO,appList));
                    }
                    //APP手机型号
                    List<Map> modelList = userStatisticsService.getIphoneModelCounts(dateVO);
                    if(!CollectionUtils.isEmpty(modelList)){
                        DataUtil.returnString(userStatisticsService.addIphoneModelCounts(dateVO,modelList));
                    }
                    //APP版本排行
                    List<Map> versionList = userStatisticsService.getAppVersionCounts(dateVO);
                    if(!CollectionUtils.isEmpty(versionList)){
                        DataUtil.returnString(userStatisticsService.addAppVersionCounts(dateVO,versionList));
                    }
                    //用户登录成功率分析
                    List<Map> sRateList = userStatisticsService.getSuccessRateCounts(dateVO);
                    if(!CollectionUtils.isEmpty(sRateList)){
                        DataUtil.returnString(userStatisticsService.addSuccessRateCounts(dateVO,sRateList));
                    }
                    //用户登录失败原因分析
                    List<Map> fCauseList = userStatisticsService.getFailCauseCounts(dateVO);
                    if(!CollectionUtils.isEmpty(fCauseList)){
                        DataUtil.returnString(userStatisticsService.addFailCauseCounts(dateVO,fCauseList));
                    }
                }
            }
        } else {
            DateVO dateVO = DataUtil.setDateVO();
            //用户登录时间统计
            List<Map> timeList = userStatisticsService.getEventCustomListForType(dateVO);
            if(!CollectionUtils.isEmpty(timeList)){
                DataUtil.returnString(userStatisticsService.addEventCustomListForType(dateVO,timeList));
            }
            //用户登录次数排行
            List<Map> countList = userStatisticsService.getEventCustomLoginCounts(dateVO);
            if(!CollectionUtils.isEmpty(countList)){
                DataUtil.returnString(userStatisticsService.addEventCustomLoginCounts(dateVO,countList));
            }
            //用户登录24小时分布
            List<Map> hourList = userStatisticsService.getEventUserStatistics(dateVO);
            if (!CollectionUtils.isEmpty(hourList)) {
                DataUtil.returnString(userStatisticsService.insertEventUserStatistics(dateVO, hourList));
            }
            //PC操作系统
            List<Map> pcList = userStatisticsService.getPcSystemCounts(dateVO);
            if(!CollectionUtils.isEmpty(pcList)){
                DataUtil.returnString(userStatisticsService.addPcSystemCounts(dateVO,pcList));
            }
            //PC浏览器排行
            List<Map> browerList = userStatisticsService.getPcBrowserCounts(dateVO);
            if(!CollectionUtils.isEmpty(browerList)){
                DataUtil.returnString(userStatisticsService.addPcBrowserCounts(dateVO,browerList));
            }
            //APP操作系统
            List<Map> appList = userStatisticsService.getAppSystemCounts(dateVO);
            if(!CollectionUtils.isEmpty(appList)){
                DataUtil.returnString(userStatisticsService.addAppSystemCounts(dateVO,appList));
            }
            //APP手机型号
            List<Map> modelList = userStatisticsService.getIphoneModelCounts(dateVO);
            if(!CollectionUtils.isEmpty(modelList)){
                DataUtil.returnString(userStatisticsService.addIphoneModelCounts(dateVO,modelList));
            }
            //APP版本排行
            List<Map> versionList = userStatisticsService.getAppVersionCounts(dateVO);
            if(!CollectionUtils.isEmpty(versionList)){
                DataUtil.returnString(userStatisticsService.addAppVersionCounts(dateVO,versionList));
            }
            //用户登录成功率分析
            List<Map> sRateList = userStatisticsService.getSuccessRateCounts(dateVO);
            if(!CollectionUtils.isEmpty(sRateList)){
                DataUtil.returnString(userStatisticsService.addSuccessRateCounts(dateVO,sRateList));
            }
            //用户登录失败原因分析
            List<Map> fCauseList = userStatisticsService.getFailCauseCounts(dateVO);
            if(!CollectionUtils.isEmpty(fCauseList)){
                DataUtil.returnString(userStatisticsService.addFailCauseCounts(dateVO,fCauseList));
            }
        }
        return ReturnT.SUCCESS;
    }
}
