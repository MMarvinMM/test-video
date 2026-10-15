package com.chinatower.analysis.modules.statistic.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.modules.common.Constants;
import com.chinatower.analysis.modules.statistic.dao.BigScreenDAO;
import com.chinatower.analysis.modules.workbench.dao.WorkbenchDAO;
import com.chinatower.analysis.modules.workbench.util.StatDimensionConstants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chinatower.common.core.enums.WarningSourceEnum.IOT_ALARM;

/**
 * 大屏告警相关统计
 * @author cuisj
 * @version 1.0
 * @date 2023/4/8 19:45
 */
@Service
@Slf4j
public class BigScreenService {
    @Resource
    private BigScreenDAO bigScreenDAO;

    @Resource
    private WorkbenchDAO workbenchDAO;

    /**
     * 获取设备统计
     * @param vo
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id", industryCode="a.industry_code")
    public Result<List<DevicesDataVO>> getDevicesStatistics(QueryParamVO vo){
        vo.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<DevicesDataVO> list = bigScreenDAO.getDevicesStatistics(vo);
        if(!CollectionUtils.isEmpty(list)) {
            List<AreaDataVO> areaDataVOList = bigScreenDAO.getAreaList(getAreaCodes(JSON.parseArray(JSON.toJSONString(list))));
            for (DevicesDataVO devicesDataVO : list) {
                areaDataVOList.forEach(item -> {
                    if (item.getAreaCode().equals(devicesDataVO.getAreaCode())) {
                       BeanUtils.copyProperties(item, devicesDataVO);
                    }
                });
            }
        }
        return Result.data(list);
    }

    /**
     * 获取告警统计
     * @param vo
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id",
            industryCode="a.industry_code", alarmSourceColumn="a.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "a.province_id"
            ,areaCity = "a.city_id",areaCode = "a.county_id",typeValueColumn = "a.warning_type_id")
    public Result<List<EventDataVO>> getEventStatistics(QueryParamVO vo){
        Result result = checkParam(vo);
        if(result.getCode() == ResultCode.FAILURE.getCode()){
            return result;
        }
        List<EventDataVO> list = bigScreenDAO.getEventStatistics(vo);
        if(!CollectionUtils.isEmpty(list)){
            List<AreaDataVO> areaDataVOList = bigScreenDAO.getAreaList(getAreaCodes(JSON.parseArray(JSON.toJSONString(list))));
            for(EventDataVO eventDataVO : list){
                areaDataVOList.forEach(item -> {
                    if(item.getAreaCode().equals(eventDataVO.getAreaCode())){
                        BeanUtils.copyProperties(item,eventDataVO);
                    }
                });
            }
        }
        return Result.data(list);
    }

    /**
     * 参数校验
     * @param vo
     * @return
     */
    private Result checkParam(QueryParamVO vo){
        if(vo == null){
            return Result.fail("参数错误");
        }

//        if(StringUtils.isEmpty(vo.getProvinceId()) && StringUtils.isEmpty(vo.getCityId())){
//            return Result.fail("省份编码或市编码不能为空");
//        }
        return Result.success("");
    }

    private List<String> getAreaCodes(JSONArray json){
        List<String> list = new ArrayList<>();
        for(Object object : json){
            JSONObject obj = (JSONObject) object;
            list.add(obj.getString("areaCode"));
        }
        return list;
    }

    /**
     * 获取告警看板统计-查询当月数据
     * @param vo
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id",
            industryCode="a.industry_code", alarmSourceColumn="a.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "a.province_id"
            ,areaCity = "a.city_id",areaCode = "a.county_id",typeValueColumn = "a.warning_type_id")
    public Result<List<EventBoardDataVO>> getEventBoardStatistics(EventBoardParamVO vo){
        if(vo == null){
            return Result.fail("参数错误");
        }


        if(StringUtils.isEmpty(vo.getAreaCode())){
            return Result.fail("省份编码或市编码不能为空");
        }

        if(StringUtils.isEmpty(vo.getDate())){
            return Result.fail("年月不能为空");
        }

        return Result.data(bigScreenDAO.getEventBoardStatistics(vo));
    }

    /**
     * 站点维度top10
     * @param vo
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id",
            industryCode="a.industry_code", alarmSourceColumn="a.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "a.province_id"
            ,areaCity = "a.city_id",areaCode = "a.county_id",typeValueColumn = "a.warning_type_id")
    public Result<List<DeviceTopTenDataVO>> getDeviceTopTen(DeviceTopTenParamVO vo){
        List<DeviceTopTenDataVO> list = bigScreenDAO.getDeviceTopTen(vo);
        if(!CollectionUtils.isEmpty(list)){
            List<DeviceTopTenDataVO> deviceList = bigScreenDAO.getDeviceList(getDeviceCodes(JSON.parseArray(JSON.toJSONString(list))));
            // 筛选出物联告警对应的设备编码
            List<String> iotDeviceCodes = list.stream().filter(item -> item.getWarningSource().equals(IOT_ALARM.getTypeValue())).map(item -> item.getDeviceCode()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(iotDeviceCodes)) {
                List<DeviceTopTenDataVO> iotDeviceList = bigScreenDAO.getIotDeviceList(iotDeviceCodes);
                deviceList.addAll(iotDeviceList);
            }
            for(DeviceTopTenDataVO deviceTopTenDataVO : list){
                deviceList.forEach(item -> {
                    if(item.getDeviceCode().equals(deviceTopTenDataVO.getDeviceCode())){
                        deviceTopTenDataVO.setDeviceName(item.getDeviceName());
                    }
                });
            }
        }
        return Result.data(list);
    }

    private List<String> getDeviceCodes(JSONArray json){
        List<String> list = new ArrayList<>();
        for(Object object : json){
            JSONObject obj = (JSONObject) object;
            list.add(obj.getString("deviceCode"));
        }
        return list;
    }

    /**
     * 告警类型统计
     * @param vo
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id",typeValueColumn = "dataTable.warning_type_id")
    public Result<List<EventStatisticDataVO>> getEventTypeStatistics(EventStatisticParamVO vo){
        // TYPE：告警类型（含场景）；SUB_TASK：下游任务 + 小模型，剔场景
        List<String> typeList;
        List<EventStatisticDataVO> esaList;
        if (StatDimensionConstants.isSubTask(vo.getStatDimension())) {
            Set<String> sceneIds = loadSceneIdSet();
            Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
            // 下游表：保留下游（可与场景 type_value 重叠）；剔纯场景
            List<String> subCodes = filterPureSceneCodes(bigScreenDAO.getAllEventType(vo), sceneIds, downstreamIds);
            String dim = vo.getStatDimension();
            vo.setStatDimension(StatDimensionConstants.TYPE);
            List<String> smallCodes;
            List<EventStatisticDataVO> typeEsa;
            try {
                // TYPE 侧只取纯小模型（非场景且非下游）
                smallCodes = filterSmallModelCodes(bigScreenDAO.getAllEventType(vo), sceneIds, downstreamIds);
                typeEsa = filterSmallModelStatisticRows(bigScreenDAO.getEventTypeStatistics(vo), sceneIds, downstreamIds);
            } finally {
                vo.setStatDimension(dim);
            }
            LinkedHashSet<String> mergedCodes = new LinkedHashSet<>();
            if (!CollectionUtils.isEmpty(subCodes)) {
                mergedCodes.addAll(subCodes);
            }
            if (!CollectionUtils.isEmpty(smallCodes)) {
                mergedCodes.addAll(smallCodes);
            }
            typeList = new ArrayList<>(mergedCodes);
            List<EventStatisticDataVO> subEsa = filterPureSceneStatisticRows(
                    bigScreenDAO.getEventTypeStatistics(vo), sceneIds, downstreamIds);
            esaList = mergeEventTypeStatisticRows(subEsa, typeEsa);
        } else {
            typeList = bigScreenDAO.getAllEventType(vo);
            esaList = bigScreenDAO.getEventTypeStatistics(vo);
        }
        if(CollectionUtils.isEmpty(typeList)){
            return Result.success("暂无数据");
        }
        // 查询告警类型列表，按照连续日期分组
        List<EventStatisticDataVO> list = bigScreenDAO.getAlarmType(typeList,vo);
        if (esaList == null) {
            esaList = new ArrayList<>();
        }

        for(EventStatisticDataVO dataVO : list){
            dataVO.setNum(0);
            esaList.forEach(item -> {
                if(dataVO.getTypeId().equals(item.getTypeId()) &&
                        dataVO.getEventDate().equals(item.getEventDate())){
                    dataVO.setNum(item.getNum());
                }
            });
        }
        return Result.data(list);
    }

    private Set<String> loadSceneIdSet() {
        List<String> sceneIdList = workbenchDAO.getSceneIds();
        if (CollectionUtils.isEmpty(sceneIdList)) {
            return new HashSet<>();
        }
        return sceneIdList.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<String> loadDownstreamAlarmTypeIdSet() {
        List<String> ids = workbenchDAO.getDownstreamAlarmTypeIds();
        if (CollectionUtils.isEmpty(ids)) {
            return new HashSet<>();
        }
        return ids.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /** 下游优先：在下游白名单中保留；否则剔除场景编码 */
    private static boolean keepSubTaskCustomerCode(String code, Set<String> sceneIds, Set<String> downstreamIds) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }
        if (downstreamIds != null && downstreamIds.contains(code)) {
            return true;
        }
        return sceneIds == null || !sceneIds.contains(code);
    }

    private static List<String> filterPureSceneCodes(List<String> codes, Set<String> sceneIds,
                                                     Set<String> downstreamIds) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return codes.stream()
                .filter(code -> keepSubTaskCustomerCode(code, sceneIds, downstreamIds))
                .collect(Collectors.toList());
    }

    private static List<String> filterSmallModelCodes(List<String> codes, Set<String> sceneIds,
                                                      Set<String> downstreamIds) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return codes.stream()
                .filter(StringUtils::isNotEmpty)
                .filter(code -> downstreamIds == null || !downstreamIds.contains(code))
                .filter(code -> sceneIds == null || !sceneIds.contains(code))
                .collect(Collectors.toList());
    }

    private static List<EventStatisticDataVO> filterPureSceneStatisticRows(List<EventStatisticDataVO> rows,
                                                                           Set<String> sceneIds,
                                                                           Set<String> downstreamIds) {
        if (CollectionUtils.isEmpty(rows)) {
            return new ArrayList<>();
        }
        return rows.stream()
                .filter(row -> row != null && keepSubTaskCustomerCode(row.getTypeId(), sceneIds, downstreamIds))
                .collect(Collectors.toList());
    }

    private static List<EventStatisticDataVO> filterSmallModelStatisticRows(List<EventStatisticDataVO> rows,
                                                                            Set<String> sceneIds,
                                                                            Set<String> downstreamIds) {
        if (CollectionUtils.isEmpty(rows)) {
            return new ArrayList<>();
        }
        return rows.stream()
                .filter(row -> row != null && StringUtils.isNotEmpty(row.getTypeId()))
                .filter(row -> downstreamIds == null || !downstreamIds.contains(row.getTypeId()))
                .filter(row -> sceneIds == null || !sceneIds.contains(row.getTypeId()))
                .collect(Collectors.toList());
    }

    /** 下游优先，其次并入小模型（同 typeId+日期不重复加总） */
    private static List<EventStatisticDataVO> mergeEventTypeStatisticRows(List<EventStatisticDataVO> primary,
                                                                          List<EventStatisticDataVO> secondary) {
        LinkedHashMap<String, EventStatisticDataVO> byKey = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(primary)) {
            for (EventStatisticDataVO row : primary) {
                if (row == null || StringUtils.isEmpty(row.getTypeId()) || StringUtils.isEmpty(row.getEventDate())) {
                    continue;
                }
                byKey.putIfAbsent(row.getTypeId() + "|" + row.getEventDate(), row);
            }
        }
        if (!CollectionUtils.isEmpty(secondary)) {
            for (EventStatisticDataVO row : secondary) {
                if (row == null || StringUtils.isEmpty(row.getTypeId()) || StringUtils.isEmpty(row.getEventDate())) {
                    continue;
                }
                byKey.putIfAbsent(row.getTypeId() + "|" + row.getEventDate(), row);
            }
        }
        return new ArrayList<>(byKey.values());
    }

    /**
     * 查询告警地块面积
     * @param paramVO
     * @return
     */
    @DataScope(dbType = EbikeDataBaseConstant.SLAVE,deviceColumn="a.device_code",tenantColumn="a.tenant_id",
            industryCode="a.industry_code", areaPro = "a.province_id"
            ,areaCity = "a.city_id",areaCode = "a.county_id")
    public Result<List<Map<String, Object>>> getEventPlotAreaStatistics(EventBoardParamVO paramVO){
        String createTime = DateUtil.format(new Date(), Constants.YYYY_MM_DD);
        paramVO.setDate(createTime);
        List<Map<String, Object>> list = bigScreenDAO.getEventPlotAreaStatistics(paramVO);
        list.forEach(e -> {
            String plotAreaSumObj = MapUtil.getStr(e,"plotAreaSum");
            if(!StringUtils.isEmpty(plotAreaSumObj)){
                BigDecimal plotAreaSum = new BigDecimal(plotAreaSumObj).setScale(2, BigDecimal.ROUND_HALF_UP);
                e.put("plotAreaSum", plotAreaSum);
            }
        });
        return Result.data(list);
    }
}
