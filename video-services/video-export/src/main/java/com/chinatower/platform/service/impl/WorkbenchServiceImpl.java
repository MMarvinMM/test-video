package com.chinatower.platform.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.mapper.WorkbenchMapper;
import com.chinatower.platform.service.WorkbenchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkbenchServiceImpl implements WorkbenchService {
    @Resource
    private WorkbenchMapper workbenchDAO;

    /**
     * 按年或月查询告警事件类型
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.MASTER,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getDevTypeYearMonth(StatisticsVO statisticsVO) {
        List<Map> statisticsMap = workbenchDAO.getInitDevTypeYearMonth(statisticsVO);

        //告警类型
        List<Map> alarmTypeList = workbenchDAO.getAlarmType();

        Map type = typeTagCommon(statisticsMap, alarmTypeList, "type",statisticsVO);
        List<Map> resList = (List<Map>) type.get("resList");
        int sum = Integer.parseInt(type.get("sum").toString()) ;

        if (sum > 0) {
            Map otherMap = new HashMap();
            otherMap.put(Constants.NUM,sum);
            otherMap.put(Constants.TYPE_VALUE,Constants.AAA_SIX);
            otherMap.put(Constants.TYPE_NAME,Constants.OTHER);
            resList.add(otherMap);
        }
        return Result.data(resList);
    }


    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.MASTER,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getEventNumByHour(AnalysisVO analysisVO) {
        log.info("查询租户24H分布,入参{}",analysisVO.toString());
        return Result.data(workbenchDAO.getEventNumByHour(analysisVO));
    }


    /**
     * 导出事件高发区域
     *
     * @param analysisVO
     * @return analysisVO（provinceId:省份编码 ）
     * @return analysisVO（cityId:市编码）
     * @return analysisVO（countyId:区县编码）
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.MASTER,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result exportGetEventNumByArea(AnalysisVO analysisVO){
        if (analysisVO.getCountyId() !=null && !"".equals(analysisVO.getCountyId())){
            String level = workbenchDAO.getAlarmByLevel(analysisVO.getCountyId());
            switch (level){
                case "2":
                    analysisVO.setAreaBoxNum(1);
                    break;
                case "3":
                    analysisVO.setAreaBoxNum(2);
                    break;
                case "4":
                    analysisVO.setAreaBoxNum(3);
                    break;
                default:
            }
        }else {
            analysisVO.setAreaBoxNum(0);
        }
        String totalName = "";
        List<Map> eventNumByArea = workbenchDAO.getEventNumByArea(analysisVO);
        if (CollectionUtils.isEmpty(eventNumByArea)) {
            return Result.data(eventNumByArea);
        }
        for (Map<String,String> map : eventNumByArea) {
            String provinceName = workbenchDAO.getAlarmByCode(map.get("provinceId"));
            String cityName = workbenchDAO.getAlarmByCode(map.get("cityId"));
            String countyName = workbenchDAO.getAlarmByCode(map.get("countyId"));
            String streetName = workbenchDAO.getAlarmByCode(map.get("streetId"));
            if (analysisVO.getAreaBoxNum() != null &&  !"".equals(analysisVO.getAreaBoxNum())){
                switch (analysisVO.getAreaBoxNum()){
                    case 0:
                        totalName = provinceName;
                        break;
                    case 1:
                        totalName = provinceName+cityName;
                        break;
                    case 2:
                        totalName = provinceName+cityName+countyName;
                        break;
                    case 3:
                        totalName = provinceName+cityName+countyName+streetName;
                        break;
                    default:
                }
            }else {
                totalName = provinceName+cityName+countyName;
            }
            map.put("regionName",totalName);

        }
        return Result.data(eventNumByArea);
    }

    public Map typeTagCommon(List<Map> statisticsMap, List<Map> tagsList, String type, StatisticsVO statisticsVO){
        List<Map> resList = new ArrayList<>();
        Map mapList = new HashMap<>();
        int count = 0;
        int sum = 0;
        for (Map map : statisticsMap) {
            count++;
            if (count <= 5 || "total".equals(statisticsVO.getTotalRemark())) {
                tagsList.forEach(item -> {
                    if ("type".equals(type)){
                        //遍历判断告警类型是否有与告警数量最高五位的告警类型相同
                        if(item.get(Constants.TYPE_VALUE).toString().equals(map.get(Constants.TYPE_VALUE))){
                            map.put(Constants.TYPE_NAME,item.get(Constants.TYPE_NAME));
                        }
                    }else if ("tag".equals(type)){
                        if(item.get(Constants.TAG_ID).toString().equals(map.get(Constants.TAG_ID))){
                            map.put(Constants.TAG_NAME,item.get(Constants.TAG_NAME));
                        }
                    }
                });
                resList.add(map);
            } else {
                //其他类型的总数
                sum += (new BigDecimal(map.get(Constants.NUM).toString())).longValue();
            }
        }
        mapList.put("resList",resList);
        mapList.put("sum",sum);
        return mapList;
    }
}
