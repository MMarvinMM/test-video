package com.chinatower.platform.modules.alarm.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.util.DateUtils;
import com.chinatower.gis.api.GisLayerAPI;
import com.chinatower.gis.api.entity.layer.StatisticDkmjReq;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.dao.AlarmPlotAreaDAO;
import com.chinatower.platform.modules.alarm.entity.AlarmPlotAreaDto;
import com.chinatower.platform.modules.alarm.service.AlarmPlotAreaService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Name AlarmPlotAreaServiceImpl
 * @Description 告警地块面积定时任务
 * @Author lyy
 * @Date 2023/08/11
 * @version v1.6.10
 */
@Service
@Slf4j
public class AlarmPlotAreaServiceImpl implements AlarmPlotAreaService {

    @Autowired
    private AlarmPlotAreaDAO alarmPlotAreaDAO;

    @Autowired
    private GisLayerAPI gisLayerAPI;

    /**
     * 统计告警地块面积
     * 根据日期查询并统计当日处理中的告警信息，计算图斑面积信息，并按租户、行业、设备、省市区分组汇总。
     * @param dateVO 包含日期和表名的请求参数对象
     * @return 告警图斑面积的统计结果列表，每个结果包含租户、行业、设备、省市区及总面积信息
     */
    @Override
    public  List<AlarmPlotAreaDto> getAlarmPlotAreaList(DateVO dateVO) {
        // 根据传入的日期构造查询开始时间和结束时间
        Date alarmTimeStart = DateUtils.string2Date(dateVO.getHandleDate() + Constants.STERT_DATE_HMS); // 当日开始时间
        Date alarmTimeEnd = DateUtils.string2Date(dateVO.getHandleDate() + Constants.END_DATE_HMS); // 当日结束时间
        Map<String, Object> param = new HashMap<>();
        param.put(Constants.PARAM_TABLE_NAME, dateVO.getTableName()); // 表名
        param.put(Constants.PARAM_ALARM_TIME_START, alarmTimeStart);
        param.put(Constants.PARAM_ALARM_TIME_END, alarmTimeEnd);
        log.info("AlarmPlotAreaServiceImpl-getAlarmPlotAreaList-req:{}",JSON.toJSONString(param));
        // 查询当日处理中的告警信息（工单列表）
        List<Map<String, Object>> orderList = alarmPlotAreaDAO.getTaskIdForAlarmTime(param);
        // 提取工单中的 taskId 列表，去重
        List<String> taskIdList = orderList.stream().map(e -> e.get(Constants.PARAM_TASK_ID).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskIdList)) {
            return new ArrayList<>();
        }
        // 根据taskId批量获取图层、图斑信息
        List<Map<String, Object>> layerPolygonList = alarmPlotAreaDAO.getLayerPolygon(taskIdList);
        List<Map<String, Object>> plotAreaList = new ArrayList<>();
        // 处理图层和图斑信息
        if (!CollectionUtils.isEmpty(layerPolygonList)) {
            List<String> layerIdList = layerPolygonList.stream().filter(item -> !StringUtils.isEmpty(item.get(Constants.PARAM_LAYER_ID)))
                    .map(item -> item.get(Constants.PARAM_LAYER_ID).toString()).collect(Collectors.toList());
            // 遍历每个图层的图斑信息
            layerPolygonList.forEach(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put(Constants.PARAM_LAYER_ID, item.get(Constants.PARAM_LAYER_ID)); // 图层 ID
                map.put(Constants.PARAM_TASK_ID, item.get(Constants.PARAM_TASK_ID));  // 工单 ID
                // 获取与当前图层关联的所有 featureId
                ArrayList<String> featureIdList = new ArrayList<>();
                layerIdList.forEach(layerId -> {
                    if (item.containsValue(layerId)) {
                        featureIdList.add(item.get(Constants.PARAM_FEATURE_ID).toString());
                    }
                });
                // 构造请求参数对象，用于调用 GIS 服务统计图斑面积
                StatisticDkmjReq statisticDkmjReq = new StatisticDkmjReq();
                statisticDkmjReq.setLayerID(item.get(Constants.PARAM_LAYER_ID).toString());
                statisticDkmjReq.setFeatureIDs(featureIdList);
                log.info("获取图斑面积/getFeatures/statisticDkmj入参 req:{}", JSON.toJSONString(statisticDkmjReq));
                try {
                    // 获取图斑面积
                    Result result = gisLayerAPI.statisticDkmj(statisticDkmjReq);
                    log.info("获取图斑面积/getFeatures/statisticDkmj出参 resp:{}", JSON.toJSONString(result));
                    // 如果调用成功且返回数据不为空，保存图斑面积；否则设置为 0
                    if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && !StringUtils.isEmpty(result.getData())) {
                        map.put(Constants.PARAM_SUM, result.getData());
                    } else {
                        map.put(Constants.PARAM_SUM, 0);
                    }
                    plotAreaList.add(map);
                } catch (Exception e) {
                    log.error("获取图斑面积失败！", e);
                }
            });
        }
        // 将图斑信息与工单租户、行业、设备信息组合
        List<Map<String, Object>> alarmPlotAreaList = plotAreaList.stream().map(m1 -> {
            orderList.stream().filter(m2 -> Objects.equals(m1.get(Constants.PARAM_TASK_ID), m2.get(Constants.PARAM_TASK_ID))).forEach(m2 -> {
                m1.put(Constants.PARAM_TENANTID, m2.get(Constants.PARAM_TENANTID)); // 租户 ID
                m1.put(Constants.PARAM_PROVINCE_CODE, m2.get(Constants.PARAM_PROVINCE_CODE));  // 省编码
                m1.put(Constants.PARAM_CITY_CODE, m2.get(Constants.PARAM_CITY_CODE)); // 市编码
                m1.put(Constants.PARAM_COUNTY_CODE, m2.get(Constants.PARAM_COUNTY_CODE));// 区编码
                m1.put(Constants.PARAM_DEVICE_CODE, m2.get(Constants.PARAM_DEVICE_CODE));// 设备编码
                m1.put(Constants.PARAM_INDUSTRY_CODE, m2.get(Constants.PARAM_INDUSTRY_CODE)); // 行业编码
            });
            return m1;
        }).collect(Collectors.toList());
        List<AlarmPlotAreaDto> list = new ArrayList<>();
        // 根据租户、行业、设备、省市区进行分组，对每个组中的元素的sum字段进行求和
        alarmPlotAreaList.stream().collect(Collectors.groupingBy(item -> (item.get(Constants.PARAM_TENANTID) + item.get(Constants.PARAM_PROVINCE_CODE).toString() + item.get(Constants.PARAM_CITY_CODE).toString() + item.get(Constants.PARAM_COUNTY_CODE).toString() + item.get(Constants.PARAM_DEVICE_CODE).toString() + item.get(Constants.PARAM_INDUSTRY_CODE).toString()), Collectors.toList())).forEach(
                (id, transfer) -> {
                    transfer.stream().reduce((a, b) -> {
                        Map<String, Object> newMap = new HashMap<>(a);
                        BigDecimal decimal1 = new BigDecimal(a.get(Constants.PARAM_SUM).toString());
                        BigDecimal decimal2 = new BigDecimal(b.get(Constants.PARAM_SUM).toString());
                        newMap.put(Constants.PARAM_SUM, decimal1.add(decimal2)); // 累加 sum 字段
                        return newMap;
                    }).ifPresent(reducedMap -> list.add(new AlarmPlotAreaDto(reducedMap.get(Constants.PARAM_TENANTID).toString(), reducedMap.get(Constants.PARAM_PROVINCE_CODE).toString(),
                            reducedMap.get(Constants.PARAM_CITY_CODE).toString(), reducedMap.get(Constants.PARAM_COUNTY_CODE).toString(), reducedMap.get(Constants.PARAM_DEVICE_CODE).toString(),
                            reducedMap.get(Constants.PARAM_SUM).toString(), reducedMap.get(Constants.PARAM_INDUSTRY_CODE).toString())));
                });
        return list;
    }

    /**
     * 新增告警地块面积统计数据。
     * 先删除指定日期的旧数据，再批量插入新的统计数据。
     * @param dateVO 包含日期信息的请求参数对象，用于定位需要删除的数据
     * @param list   要插入的告警图斑面积统计数据列表
     * @return 插入操作是否成功，成功返回 true，失败返回 false
     */
    @Override
    public boolean insertAlarmPlotArea(DateVO dateVO, List<AlarmPlotAreaDto> list) {
        try {
            // 删除指定日期的数据
            alarmPlotAreaDAO.deleteAlarmPlotArea(dateVO);
            // 将待插入的数据按每 5000 条分批
            List<List<AlarmPlotAreaDto>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    alarmPlotAreaDAO.addAlarmPlotArea(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            // 回滚当前事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
}
