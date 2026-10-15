package com.chinatower.analysis.modules.statistic.service;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.analysis.modules.common.Constants;
import com.chinatower.analysis.modules.statistic.dao.StatisticDAO;
import com.chinatower.analysis.modules.workbench.dao.WorkbenchDAO;
import com.chinatower.analysis.modules.workbench.service.WorkbenchService;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 告警统计
 * @author cuisj
 * @version 1.0
 * @date 2023/4/8 19:45
 */
@Service
@Slf4j
public class StatisticService {
    @Resource
    private WorkbenchDAO workbenchDAO;
    @Resource
    private StatisticDAO statisticDAO;
    @Resource
    private WorkbenchService workbenchService;

    /**
     * 事件统计分析统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn="dataTable.warning_type_id")
    public Result getEventStatisticalAnalysis(StatisticsVO statisticsVO) {
        List<Map> statisticsMap = workbenchDAO.getInitDevTypeYearMonth(statisticsVO);

        //查询来源为0(初始化)且没删除的告警类型
        List<Map> alarmTypeList = workbenchDAO.getAlarmType();

        Map type = typeTagCommon(statisticsMap, alarmTypeList, Constants.TYPE);
        List<String> typeList = (List<String>) type.get(Constants.TYPE_LIST);
        int sum = Integer.parseInt(type.get(Constants.SUM).toString()) ;

        List<Map> resList = new ArrayList<>();
        if (typeList.size() > 0) {
            statisticsVO.setTypeList(typeList);
            if (sum > 0) {
                statisticsVO.setOtherFlag(Constants.ONE_STRING);
            }

            resList = statisticDAO.getAlarmType(statisticsVO);
            List<Map> esaList = statisticDAO.getEventStatisticalAnalysis(statisticsVO);

            for(Map map : resList){
                map.put(Constants.NUM,0);
                esaList.forEach(item -> {
                    //判断告警类型表与告警类型统计表 类型值是否一样 并且创建时间和相应日期一样
                    if(map.get(Constants.TYPE_VALUE).toString().equals(item.get(Constants.TYPE_VALUE)) &&
                    item.get(Constants.CREATE_DATE).toString().equals(map.get(Constants.EVENT_DATE))){
                        map.put(Constants.NUM,item.get(Constants.NUM));
                    }
                });
            }
        }

        return Result.data(resList);
    }

    /**
     * 事件统计分析统计（告警标签维度）
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventAnalysisByTag(StatisticsVO statisticsVO) {
        List<Map> statisticsMap = workbenchDAO.getAlarmTagYearMonth(statisticsVO);
        //告警标签
        List<Map> tagsList = workbenchDAO.getTags(SecurityUtils.getLoginUser().getUser().getTenantId());

        Map type = typeTagCommon(statisticsMap, tagsList, Constants.TAG);
        List<String> tags = (List<String>) type.get(Constants.TYPE_LIST);
        int sum = Integer.parseInt(type.get(Constants.SUM).toString()) ;

        List<Map> resList = new ArrayList<>();
        if (tags.size() > 0) {
            statisticsVO.setTypeList(tags);
            if (sum > 0) {
                statisticsVO.setOtherFlag(Constants.ONE_STRING);
            }

            resList = statisticDAO.getTags(statisticsVO);
            List<Map> esaList = statisticDAO.getEventAnalysisByTag(statisticsVO);

            for(Map map : resList){
                map.put(Constants.NUM,0);
                esaList.forEach(item -> {
                    if(map.get(Constants.TAG_ID).toString().equals(item.get(Constants.TAG_ID)) &&
                            item.get(Constants.CREATE_DATE).toString().equals(map.get("eventDate"))){
                        map.put(Constants.NUM,item.get(Constants.NUM));
                    }
                });
            }
        }

        return Result.data(resList);
    }

    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventRealNum(StatisticsVO statisticsVO) {
        Map statisticsMap = statisticDAO.getEventAlarmDayNum(statisticsVO);
        Map yesterdayNum = statisticDAO.getEventAlarmYesterdayNum(statisticsVO);
        if (MapUtils.isEmpty(statisticsMap)) {
            statisticsMap = new HashMap();
            statisticsMap.put(Constants.ALL_NUM,0);
            statisticsMap.put(Constants.FALSE_NUM,0);
            statisticsMap.put(Constants.REAL_NUM,0);
        }
        if (MapUtils.isEmpty(yesterdayNum)) {
            statisticsMap = new HashMap();
            statisticsMap.put(Constants.YESTERDAY_ALL,0);
        }else {
            statisticsMap.put(Constants.YESTERDAY_ALL,yesterdayNum.get("yesterdayAll"));
        }

        return Result.data(statisticsMap);
    }

    /**
     * 按年或月查询告警事件统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventAlarmNumYearMonth(StatisticsVO statisticsVO) {
        return Result.data(statisticDAO.getEventAlarmNumYearMonth(statisticsVO));
    }

    /**
     * 办结事件处理时长统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventHandleTimeNum(StatisticsVO statisticsVO) {
        Map statisticsMap = statisticDAO.getEventHandleTimeNum(statisticsVO);
        if (MapUtils.isEmpty(statisticsMap)) {
            statisticsMap = new HashMap();
        }
        return Result.data(statisticsMap);
    }

    /**
     * 告警事件统计-告警区域处理时长
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumByHandle(AnalysisVO analysisVO) {
        String totalName = "";
        List<Map> eventNumByHandle = statisticDAO.getEventNumByHandle(analysisVO);
        if (CollectionUtils.isEmpty(eventNumByHandle)) {
            return Result.data(eventNumByHandle);
        }
        for (Map<String,String> map : eventNumByHandle) {
            String provinceName = workbenchDAO.getAlarmByCode(map.get("provinceId"));
            String cityName = workbenchDAO.getAlarmByCode(map.get("cityId"));
            String countyName = workbenchDAO.getAlarmByCode(map.get("countyId"));
            String streetName = workbenchDAO.getAlarmByCode(map.get("streetId"));
            if (provinceName == null){ provinceName = ""; }
            if (cityName == null){ cityName = ""; }
            if (countyName == null){ countyName = ""; }
            if (streetName == null){ streetName = ""; }
                    totalName = provinceName+cityName+countyName;
            map.put("regionName",totalName);

        }
        return Result.data(eventNumByHandle);
    }

    public Map typeTagCommon(List<Map> statisticsMap,List<Map> alarmTypeList,String type){
        List<String> typeList = new ArrayList<>();
        Map mapList = new HashMap<>();
        int count = 0;
        int sum = 0;
        for (Map map : statisticsMap) {
            count++;
            if (count <= 5) {
                alarmTypeList.forEach(item -> {
                    if (Constants.TYPE.equals(type)){
                        //遍历判断告警类型是否有与告警数量最高五位的告警类型相同
                        if(item.get(Constants.TYPE_VALUE).toString().equals(map.get(Constants.TYPE_VALUE))){
                            map.put(Constants.TYPE_NAME,item.get(Constants.TYPE_NAME));
                        }
                    }else if (Constants.TAG.equals(type)){
                        if(item.get(Constants.TAG_ID).toString().equals(map.get(Constants.TAG_ID))){
                            map.put(Constants.TAG_NAME,item.get(Constants.TAG_NAME));
                        }
                    }
                });
                if (Constants.TYPE.equals(type)){
                    typeList.add(String.valueOf(map.get(Constants.TYPE_VALUE)));
                }else if (Constants.TAG.equals(type)){
                    typeList.add(map.get(Constants.TAG_ID).toString());
                }
            } else {
                //其他类型的总数
                sum += (new BigDecimal(map.get(Constants.NUM).toString())).longValue();
            }
        }
        mapList.put(Constants.TYPE_LIST,typeList);
        mapList.put(Constants.SUM,sum);
        return mapList;
    }


}
