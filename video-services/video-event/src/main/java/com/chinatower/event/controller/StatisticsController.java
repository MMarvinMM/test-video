package com.chinatower.event.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.StatisticsDTO;
import com.chinatower.event.entity.vo.EventAlarmNumberVO;
import com.chinatower.event.entity.vo.TodayAlarmRealtimeStatVO;
import com.chinatower.event.mapper.StatisticsMapper;
import com.chinatower.event.service.TodayAlarmStatService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * video-event
 * @ClassName StatisticsController
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/16
 **/

@RestController
@Slf4j
public class StatisticsController extends BaseController {

    @Resource
    StatisticsMapper statisticsMapper;

    @Resource
    private TodayAlarmStatService todayAlarmStatService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 功能描述：查询当天告警事件统计
     *
     * @param: statisticsDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/16
     * @version: 1.0
     */
    @PostMapping("/video/queryTodayAlarmNum")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result<EventAlarmNumberVO> queryTodayAlarmNum(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("查询当天告警事件统计---请求报文------{}", statisticsDTO.toString());
        //租户表
        EventAlarmNumberVO eventAlarmNumber = statisticsMapper.getEventAlarmDayNum(statisticsDTO);
        EventAlarmNumberVO alarmYesterdayNum = statisticsMapper.getEventAlarmYesterdayNum(statisticsDTO);
        if (null !=alarmYesterdayNum){
            eventAlarmNumber.setYesterdayAll(alarmYesterdayNum.getYesterdayAll());
        }else {
            eventAlarmNumber.setYesterdayAll(0);
        }
        DecimalFormat dF = new DecimalFormat("0.00");
        long todayAll = eventAlarmNumber.getTodayAll().longValue();
        long increase = todayAll - eventAlarmNumber.getYesterdayAll().longValue();
        String increasePer = Constants.ZERO;
        if (0 != todayAll) {
            increasePer = dF.format((float) increase / todayAll * 100);
        }
        eventAlarmNumber.setIncreasePer(increasePer);
        return Result.data(eventAlarmNumber);
    }

    /**
     * 首页三张卡片实时统计（合并原 getEventNum + queryTodayAlarmNum）
     * <p>
     * 返回 todayAll（今日告警）、penddingJudged（今日待研判）、realNum（今日真警），
     * 三项均限制 alarm_time 为今日，直接查告警月表。
     * </p>
     *
     * @param statisticsDTO 请求体（租户/行业/区域由 @DataScope 注入，可不传 tenantId）
     */
    @PostMapping("/industry/video/queryTodayAlarmStatRealtime")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, deviceColumn = "dataTable.device_code", tenantColumn = "dataTable.tenant_id",
            industryCode = "dataTable.industry_code", alarmSourceColumn = "dataTable.warning_source",
            alarmSourceVal = "1,2,3,4,5,6,7,8,9,10", areaPro = "dataTable.province_code",
            areaCity = "dataTable.city_code", areaCode = "dataTable.county_code")
    public Result<TodayAlarmRealtimeStatVO> queryTodayAlarmStatRealtime(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("首页告警卡片实时统计---请求报文------{}", statisticsDTO);
        return Result.data(todayAlarmStatService.queryTodayAlarmStatRealtime(statisticsDTO));
    }

    /**
     * 功能描述：按年或月查询告警事件统计、告警事件类型
     *
     * @param: statisticsDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/16
     * @version: 1.0
     */
    @PostMapping("/video/getEventAlarmNumYearMonth")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result<EventAlarmNumberVO> getEventAlarmNumYearMonth(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("按年或月查询告警事件统计、告警事件类型---请求报文------{}", JSON.toJSONString(statisticsDTO));
        return Result.data(statisticsMapper.getEventAlarmNumYearMonth(statisticsDTO));
    }

    /**
     * 功能描述：查询告警来源统计告警数量
     * @param statisticsDTO
     * @return
     */
    @PostMapping("/video/getAllEventAlarmNumYearMonth")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result getAllEventAlarmNumYearMonth(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("按年或月查询所有告警事件统计、告警事件类型---请求报文------{}", JSON.toJSONString(statisticsDTO));
        List<Map> statisticsList = statisticsMapper.getAllEventAlarmNumYearMonth(statisticsDTO);
        if (!CollectionUtils.isEmpty(statisticsList)) {
            List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
            for (Map<String,Object> map : statisticsList) {
                warningSourceEnum.forEach(item -> {
                    if (item.getDictValue().equals(MapUtils.getString(map,"warningSource"))) {
                        map.put("warningSourceName",item.getDictLabel());
                    }
                });
            }
        }
        return Result.data(statisticsList);
    }

    /**
     * 功能描述：按年或月查询告警事件类型
     *
     * @param: statisticsDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/16
     * @version: 1.0
     */
    @PostMapping("/video/getDevTypeYearMonth")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result<List<EventAlarmNumberVO>> getDevTypeYearMonth(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("按年或月查询告警事件类型接口---请求报文------{}", JSON.toJSONString(statisticsDTO));
        List<EventAlarmNumberVO> alarmNumberVOS = statisticsMapper.getInitDevTypeYearMonth(statisticsDTO);
        if (!CollectionUtils.isEmpty(alarmNumberVOS)) {
            this.getTypeName(alarmNumberVOS);
        } else {
            alarmNumberVOS = new ArrayList<>();
        }
        List<EventAlarmNumberVO> resList = new ArrayList<>();
        int count = 0;
        int sum = 0;
        for (EventAlarmNumberVO alarmNumberVO : alarmNumberVOS) {
            // 告警名称为空的过滤掉，不然前端会变空， 要不要记录到其它里面去呢？
            if (StringUtils.isEmpty(alarmNumberVO.getTypeName())) {
                sum += alarmNumberVO.getNum();
                continue;
            }
            count++;
            if (count <= 5) {
                resList.add(alarmNumberVO);
            } else {
                sum += alarmNumberVO.getNum();
            }
        }
        if (sum > 0) {
            EventAlarmNumberVO eventAlarm = new EventAlarmNumberVO();
            eventAlarm.setNum(sum);
            eventAlarm.setTypeId("aaa666");
            eventAlarm.setTypeName("其他");
            resList.add(eventAlarm);
        }
        return Result.data(resList);
    }

    /**
     * 功能描述：按照年月日查询事件上报趋势统计
     *
     * @param: statisticsDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/17
     * @version: 1.0
     */
    @PostMapping("/video/getEventTrend")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result<List<EventAlarmNumberVO>> getEventTrend(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("按照年月日查询事件上报趋势统计---请求报文------{}", statisticsDTO.toString());
        if (StringUtils.isEmpty(statisticsDTO.getTrendFlag())) {
            return Result.fail("入参标识不能为空");
        }
        List<EventAlarmNumberVO> eventAlarmNumberVOS;
        if (Constants.YEAR_STRING.equals(statisticsDTO.getTrendFlag())) {
            eventAlarmNumberVOS = statisticsMapper.getEventTrendYear(statisticsDTO);
        } else if (Constants.MONTH_STRING.equals(statisticsDTO.getTrendFlag())) {
            eventAlarmNumberVOS = statisticsMapper.getEventTrendMonth(statisticsDTO);
        } else if (Constants.DAY_STRING.equals(statisticsDTO.getTrendFlag())) {
            eventAlarmNumberVOS = statisticsMapper.getEventTrendDay(statisticsDTO);
        } else {
            return Result.fail("入参标识传入错误");
        }
        if (eventAlarmNumberVOS == null) {
            eventAlarmNumberVOS = new ArrayList<>();
        }
        return Result.data(eventAlarmNumberVOS);
    }

    /**
     * 功能描述：事件热力图统计
     *
     * @param: statisticsDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/3/3
     * @version: 1.0
     */
    @PostMapping("/video/getThermDiagramNum")
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public Result<List<EventAlarmNumberVO>> getThermDiagramNum(@RequestBody StatisticsDTO statisticsDTO) {
        log.info("事件热力图统计---请求报文------{}", statisticsDTO.toString());
        List<EventAlarmNumberVO> eventAlarmNumberVOS = statisticsMapper.getThermDiagramNum(statisticsDTO);
        if (eventAlarmNumberVOS == null) {
            eventAlarmNumberVOS = new ArrayList<>();
        }
        return Result.data(eventAlarmNumberVOS);
    }

    private void getTypeName(List<EventAlarmNumberVO> alarmNumberVOS) {
        List<String> ids = new ArrayList<>();
        for (EventAlarmNumberVO map : alarmNumberVOS) {
            ids.add(map.getTypeId());
        }
        List<AlarmType> alarmTypes = statisticsMapper.getTypeName(ids);
        alarmNumberVOS.forEach(alarmNumberVO -> {
            String typeId = alarmNumberVO.getTypeId();
            String typeName = "";
            if (StringUtils.isNotEmpty(typeId)) {
                for (AlarmType alarmType : alarmTypes) {
                    if (typeId.equals(String.valueOf(alarmType.getTypeId()))) {
                        typeName = alarmType.getTypeName();
                    }
                }
            }
            alarmNumberVO.setTypeName(typeName);
        });
    }
}
