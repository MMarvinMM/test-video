package com.chinatower.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chinatower.common.core.annotation.Trace;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.consumer.dao.SensorAttrValueAllMongoDAO;
import com.chinatower.platform.consumer.dao.VmOrderSensorDevicesDAO;
import com.chinatower.platform.consumer.item.VmOrderSensorDevicesDO;
import com.chinatower.platform.data.sdk.entity.BasicResponse;
import com.chinatower.platform.data.sdk.entity.QueryDeviceCodeListByIndustryCodeEntity;
import com.chinatower.platform.data.sdk.entity.VmOrderSensorDevicesDTO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.google.common.base.Stopwatch;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * video-out-data
 */
@Slf4j
@RestController
public class SensorController {
    @Resource
    VmOrderSensorDevicesDAO vmOrderSensorDevicesDAO;

    @Resource
    SensorAttrValueAllMongoDAO sensorAttrValueAllMongoDAO;

    /**
     * 查询客户订购传感器设备
     * @param entity
     * @return
     */
    @Trace
    @PostMapping("/queryDeveceCodeListByIndustryCode")
    public BasicResponse<List<VmOrderSensorDevicesDTO>> queryDeviceCodeListByIndustryCode(@RequestBody QueryDeviceCodeListByIndustryCodeEntity entity) {
        HttpServletRequest request = RequestUtils.getRequest();
        String industryCode = request.getHeader("X-INDUSTRY_CODE");
        String tenantId = entity.getTenantId();
        List<VmOrderSensorDevicesDO> devicesDOList = vmOrderSensorDevicesDAO.select(industryCode, tenantId);
        List<VmOrderSensorDevicesDTO> devicesDTOList = devicesDOList.stream().map(it -> {
            VmOrderSensorDevicesDTO dto = new VmOrderSensorDevicesDTO();
            BeanUtil.copyProperties(it, dto);
            return dto;
        }).collect(Collectors.toList());
        BasicResponse<List<VmOrderSensorDevicesDTO>> response = new BasicResponse<>();
        response.setResponseBody(devicesDTOList);
        return response;
    }

    /**
     * 查询传感器时间段数据--APP端使用
     *
     */
    @ApiOperation(value = "查询传感器时间段数据")
    @PostMapping("/getPeriodData")
    public Result<List<SensorAttrMapVO>> getPeriodData(@RequestBody DeviceReqDTO deviceReqDTO) {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            if (StringUtils.isBlank(deviceReqDTO.getDeviceCode())) {
                return Result.fail(Constants.DEVICE_CODE_NOT_EMPTY);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = null;
            Date end = null;
            try {
                start = simpleDateFormat.parse(deviceReqDTO.getStartDate());
                end = simpleDateFormat.parse(deviceReqDTO.getEndDate());
            } catch (ParseException e) {
                log.error("日期转换异常", e);
            }
            if (start == null || end == null) {
                return Result.fail("日期格式有误");
            }

            List<SensorAttrMapVO> attrMapVOList = new ArrayList<>();

            boolean stop = false;
            int i = 0;
            log.info("SensorController getPeriodData step1 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            // 从开始时间向后获取--超过半年强制停
            while (!stop){
                if (i == 6){
                    break;
                }
                String tableDate = DateUtils.dateToStringYM(start);
                List<SensorAttrMapVO> sensorAttrMapVOS = sensorAttrValueAllMongoDAO.listSensorPeriodDataByTimeFrequency(deviceReqDTO, tableDate);
                //累计总数
                attrMapVOList.addAll(sensorAttrMapVOS);
                //判断是否到达初始日期
                stop = DateUtils.sameMouth(start,end);
                //转到下个月
                String lastMonth = DateUtils.getNextMonth(DateUtils.dateToStringYM(start));
                start = DateUtils.stringToDateYM(lastMonth);
                i++;
                log.info("SensorController getPeriodData step2 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            }

//            Date startTime = simpleDateFormat.parse(deviceReqDTO.getStartDate());
//            Date endTime = simpleDateFormat.parse(deviceReqDTO.getEndDate());
//            List<SensorAttrMapVO> resultList = attrMapVOList.stream().filter(item -> DateUtils.stringToDateYMDHMS(item.get(Constants.MONITOR_TIME).toString()).before(endTime)
//                    && DateUtils.stringToDateYMDHMS(item.get(Constants.MONITOR_TIME).toString()).after(startTime)).collect(Collectors.toList());
            List<SensorAttrMapVO> sortedResult;
            sortedResult = attrMapVOList.stream().filter(x -> {
                return ((x.get(Constants.MONITOR_TIME) != null) && (DateUtils.string2Date(x.get(Constants.MONITOR_TIME).toString()) != null));
            }).sorted((e1, e2) -> {
                Date date1 = DateUtils.string2Date(e1.get(Constants.MONITOR_TIME).toString());
                Date date2 = DateUtils.string2Date(e2.get(Constants.MONITOR_TIME).toString());
                return date1.compareTo(date2);
            }).collect(Collectors.toList());
            if (sortedResult.size() > 5000){
                //先按天分组
                List<SensorAttrMapVO> resultMap = new ArrayList<>();
                Map<String,List<SensorAttrMapVO>> visMap =
                        sortedResult.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyy-MM-dd")
                                .format(DateUtils.stringToDateYMDHMS(item.get(Constants.MONITOR_TIME).toString()))));
                for (Map.Entry<String,List<SensorAttrMapVO>> entry : visMap.entrySet()){
                    List<SensorAttrMapVO> entryValue = entry.getValue();
                    if (entryValue.size() >= 2){
                        Set<String> keyString = entryValue.get(0).keySet();
                        SensorAttrMapVO maxAttrMap = new SensorAttrMapVO();
                        SensorAttrMapVO minAttrMap = new SensorAttrMapVO();
                        maxAttrMap.put(Constants.MONITOR_TIME,entryValue.get(0).get(Constants.MONITOR_TIME));
                        maxAttrMap.put(Constants.DEVICECODE,entryValue.get(0).get(Constants.DEVICECODE));
                        minAttrMap.put(Constants.MONITOR_TIME,entryValue.get(entryValue.size()-1).get(Constants.MONITOR_TIME));
                        minAttrMap.put(Constants.DEVICECODE,entryValue.get(0).get(Constants.DEVICECODE));
                        for (String key : keyString){
                            if (!key.equals(Constants.MONITOR_TIME) && !key.equals(Constants.DEVICECODE) && !key.equals("_id")){
                                List<String> valueString = entryValue.stream().filter(x -> {
                                    return ((x != null) && (x.get(key) != null));
                                }).map(item -> item.get(key).toString()).collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(valueString)) {
                                    String maxValue = Collections.max(valueString);
                                    String minValue = Collections.min(valueString);
                                    maxAttrMap.put(key,String.valueOf(maxValue));
                                    minAttrMap.put(key,String.valueOf(minValue));
                                }
                            }
                        }
                        resultMap.add(maxAttrMap);
                        resultMap.add(minAttrMap);
                    }else {
                        resultMap.addAll(entryValue);
                    }
                }
                sortedResult =  resultMap.stream().sorted((e1, e2) -> {
                    Date date1 = DateUtils.string2Date(e1.get(Constants.MONITOR_TIME).toString());
                    Date date2 = DateUtils.string2Date(e2.get(Constants.MONITOR_TIME).toString());
                    return date1.compareTo(date2);
                }).collect(Collectors.toList());
                log.info("SensorController getPeriodData step3 sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            }
            return Result.data(sortedResult);
        } catch (Exception e) {
            log.error("查询传感器时间段数据失败, e:", e);
            return Result.fail("查询传感器时间段数据失败");
        }
    }

    /**
     * 查询传感器时间段数据
     *
     * @return
     * @author zheng.yan
     * @date 2022/5/11
     * @version
     */
    @ApiOperation(value = "查询传感器时间段数据")
    @PostMapping("/getSensorPeriodData")
    public Result<List<SensorAttrMapVO>> getSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO) {
        try {
            if (StringUtils.isBlank(deviceReqDTO.getDeviceCode())) {
                return Result.fail(Constants.DEVICE_CODE_NOT_EMPTY);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = null;
            Date end = null;
            try {
                start = simpleDateFormat.parse(deviceReqDTO.getStartDate());
                end = simpleDateFormat.parse(deviceReqDTO.getEndDate());
            } catch (ParseException e) {
                log.error("日期转换异常", e);
            }
            if (start == null || end == null) {
                return Result.fail("日期格式有误");
            }
            String tableDate = DateUtils.dateToStringYM(start);
            return Result.data(sensorAttrValueAllMongoDAO.listSensorPeriodData(deviceReqDTO, tableDate));
        } catch (Exception e) {
            log.error("查询传感器时间段数据失败, e:", e);
            return Result.fail("查询传感器时间段数据失败");
        }
    }
}
