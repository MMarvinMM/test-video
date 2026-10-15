package com.chinatower.device.utils;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.dataobject.UavFlightStatusVO;
import com.chinatower.device.mapper.UavDeviceMapper;
import com.chinatower.event.constants.Constants;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.video.api.ICasCadeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
public class DeviceCommonUtils {

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private UavDeviceMapper uavDeviceMapper;

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");

    public PoiContraryGeocodingQueryRes getRegionInfoByLonAndLat(String longitude, String latitude){
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", longitude, latitude));
        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && CollectionUtils.isNotEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                if (data.getRegion() != null) {
                    return data;
                }
            } catch (Exception e) {
                log.error("通过经纬度转化省市区失败:", e);
            }
        }
        return null;
    }

    public boolean isDependenceFlag() {
        // 是否属地化
        Boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(Constants.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            log.info("DEPENDENCE_MANAGE====>{}", sysConfig);
            if (sysConfig != null && sysConfig.getConfigValue().equals(Constants.ZERO)) {
                isDependenceFlag = true;
            }
        }
        return isDependenceFlag;
    }

    /**
     * 飞行状态业务处理
     * 1、飞行中：无人机正在飞行中，状态为飞行中。
     * 2、空闲：无人机没有飞行且今天/明天/后天没有飞行计划，状态为空闲。
     * 3、计划待执行：无人机没有飞行且今天/明天/后天有飞行计划，状态为计划待执行+今天/明天/后天+执行时间（展示为HH:MM）。若有多个计划取最近的执行时间。
     * @param deviceCodes 无人机设备编码
     */
    public List<UavFlightStatusVO> getUavFlightStatus(List<String> deviceCodes) {
        log.info("获取无人机飞行状态，入参: {}", JSON.toJSONString(deviceCodes));
        if (CollectionUtils.isEmpty(deviceCodes)) {
            return new ArrayList<>();
        }
        //计算当前时间~后天（23:59:59）
        Date currentTime = new Date();
        Date endTime = calculateEndTime();

        try {
            //查询设备的最新任务状态
            List<Map<String, Object>> allLatestTasks = uavDeviceMapper.getLatestTaskStatus(deviceCodes, currentTime);
            Map<String, Map<String, Object>> latestTaskMap = convertToDeviceMap(allLatestTasks);
            log.info("最新任务状态查询结果: {}", latestTaskMap.keySet());

            //查询每个设备的最早未来计划
            List<Map<String, Object>> futurePlanMaps = uavDeviceMapper.getFuturePlanTasks(deviceCodes, currentTime, endTime);
            Map<String, Map<String, Object>> futurePlanMap = convertToDeviceMap(futurePlanMaps);
            log.info("未来计划查询结果: {}", futurePlanMap.keySet());

            // 组装飞行状态
            List<UavFlightStatusVO> result = deviceCodes.stream()
                    .map(deviceCode -> buildFlightStatusVO(deviceCode, latestTaskMap, futurePlanMap, currentTime))
                    .collect(Collectors.toList());

            log.info("无人机飞行状态查询完成，返回结果数量：{}", result.size());
            return result;
        } catch (Exception e) {
            log.error("查询无人机飞行状态发生异常：", e);
            return Collections.emptyList();
        }
    }

    /**
     * 构建单个设备的飞行状态
     * @param deviceCode
     * @param taskMap
     * @param planMap
     * @param currentTime
     * @return
     */
    private UavFlightStatusVO buildFlightStatusVO(String deviceCode, Map<String, Map<String, Object>> taskMap,
                                       Map<String, Map<String, Object>> planMap, Date currentTime) {
        UavFlightStatusVO vo = new UavFlightStatusVO();
        vo.setDeviceCode(deviceCode);

        try {
            // 解析任务状态
            String taskStatus = null;
            if (taskMap != null) {
                Map<String, Object> innerMap = taskMap.get(deviceCode);
                if (innerMap != null && innerMap.containsKey(Constants.PARAM_STATUS)) {
                    taskStatus = String.valueOf(innerMap.get(Constants.PARAM_STATUS));
                }
                if (innerMap != null && innerMap.containsKey(Constants.PARAM_PLAN_TASK_ID)) {
                    vo.setPlanTaskId(String.valueOf(innerMap.get(Constants.PARAM_PLAN_TASK_ID)));
                }
            }

            // 解析计划时间
            Date planTime = null;
            if (planMap != null && planMap.containsKey("planTime")) {
                try {
                    planTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, String.valueOf(planMap.get("planTime")));
                } catch (Exception e) {
                    log.error("计划时间格式解析失败，设备: {}", deviceCode, e);
                }
            }
            vo.setPlanTime(planTime);

            // 判断飞行状态
            if (Constants.THREE.equals(taskStatus)) {
                vo.setFlightStatus(Constants.ONE); // 飞行中
            } else {
                if (planTime == null) {
                    vo.setFlightStatus(Constants.TWO); // 空闲
                } else {
                    vo.setFlightStatus(Constants.ZERO); // 待执行
                    String dayDesc = getDayDesc(planTime, currentTime);
                    //计划执行时间（如：今天/明天/后天 18:00）
                    vo.setPlanTimeHHmm(dayDesc + " " + TIME_FORMATTER.format(planTime));
                }
            }
        } catch (Exception e) {
            log.error("处理设备[{}]飞行状态时发生异常", deviceCode, e);
        }

        return vo;
    }

    //判断计划时间属于今天/明天/后天
    private String getDayDesc(Date planTime, Date currentTime) {
        if (planTime == null || currentTime == null) {
            log.warn("计划时间或当前时间为空，无法判断日期描述");
            return "";
        }

        Calendar planCal = Calendar.getInstance();
        planCal.setTime(planTime);
        int planDay = planCal.get(Calendar.DAY_OF_YEAR);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentTime);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);

        if (planDay == currentDay) return "今天";
        if (planDay == currentDay + 1) return "明天";
        if (planDay == currentDay + 2) return "后天";
        return "";
    }

    //计算「后天23:59:59」的时间
    private Date calculateEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //加2天（到后天）
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Map<String, Map<String, Object>> convertToDeviceMap(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream()
                .filter(map -> map.get(Constants.PARAM_DEVICE_CODE) != null)
                .collect(Collectors.toMap(
                        map -> map.get(Constants.PARAM_DEVICE_CODE).toString(),
                        Function.identity(),
                        (oldVal, newVal) -> oldVal
                ));
    }
}

