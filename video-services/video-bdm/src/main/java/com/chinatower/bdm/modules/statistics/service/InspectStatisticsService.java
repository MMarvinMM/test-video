package com.chinatower.bdm.modules.statistics.service;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.GridKeeperRefVO;
import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.bdm.api.entity.InspectionConfStatisticsVO;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.statistics.dao.InspectStatisticsDAO;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
@Slf4j
public class InspectStatisticsService extends TowerBaseController {
    @Resource
    private InspectStatisticsDAO inspectStatisticsDAO;
    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 查询总览 网格数量
     */
    public Map getInspectOverview(InspectStatisticsVo inspectStatisticsVo){
        log.info("getInspectOverview:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务统计
        Map inspectTaskTotal = inspectStatisticsDAO.getInspectTaskTotal(inspectStatisticsVo);
        //网格统计
        Map gridTotal = inspectStatisticsDAO.getGridTotal(inspectStatisticsVo);
        //网格员统计
        Map keeperTotal = inspectStatisticsDAO.getGridKeeperTotal(inspectStatisticsVo);
        //出勤率统计
        Map attendanceTotal = inspectStatisticsDAO.getAttendanceTotal(inspectStatisticsVo);
        //合并集合
        inspectTaskTotal.putAll(gridTotal);
        inspectTaskTotal.putAll(keeperTotal);
        inspectTaskTotal.putAll(attendanceTotal);
        //app 网格等级 标绘状态需要
        if (!StringUtil.isEmpty(inspectStatisticsVo.getGridId())){
            Map gridById = inspectStatisticsDAO.getGridById(inspectStatisticsVo);
            List<SysDictDataResponse> gridLevelEnum = systemFeignClient.dictDataType(Constants.GRID_LEVEL_FIELD).getData();
                gridLevelEnum.forEach(item -> {
                    if (item.getDictValue().equals(MapUtils.getString(gridById,Constants.GRID_LEVEL))) {
                        inspectTaskTotal.putAll(gridById);
                        inspectTaskTotal.put(Constants.GRID_LEVEL_NAME,item.getDictLabel());
                    }
                });

        }
        return inspectTaskTotal;
    }

    /**
     * 网格层级数量排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridLevel(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridLevel:" + JSON.toJSONString(inspectStatisticsVo));
        List<Map> gridLevel = inspectStatisticsDAO.getGridLevel(inspectStatisticsVo);
        //网格统计
        if (CollUtil.isNotEmpty(gridLevel)){
            this.handleGridLevel(gridLevel);
            Map gridTotal = inspectStatisticsDAO.getGridTotal(inspectStatisticsVo);
            for (Map map : gridLevel) {
                map.put("totalNum",gridTotal.get("gridNum"));
            }
        }
        return getDataTable(gridLevel);
    }

    /**
     * 网格性别统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public Map getGridKeeperGender(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridKeeperGender:" + JSON.toJSONString(inspectStatisticsVo));
        return inspectStatisticsDAO.getGridKeeperGender(inspectStatisticsVo);
    }

    /**
     * 网格员年龄分布
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public Map getGridKeeperAge(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridKeeperAge:" + JSON.toJSONString(inspectStatisticsVo));
        return inspectStatisticsDAO.getGridKeeperAge(inspectStatisticsVo);
    }

    /**
     * 网格统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "dataTable.grid_id",gridKeeperColumn = "g.keeper_id")
    public TableDataInfo getGridStatistics(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridStatistics:" + JSON.toJSONString(inspectStatisticsVo));
        List<Map> gridStatistics = inspectStatisticsDAO.getGridStatistics(inspectStatisticsVo);
        if (CollUtil.isEmpty(gridStatistics)){
            return getDataTable(new ArrayList<>());
        }
        this.handleGridLevel(gridStatistics);
        List<String> gridList = gridStatistics.stream().map(t -> String.valueOf(t.get(Constants.GRID_ID))).distinct().collect(toList());
        inspectStatisticsVo.setGridList(gridList);
        List<Map> keeperNameByGridId = inspectStatisticsDAO.getKeeperNameByGridId(inspectStatisticsVo);
        if (CollUtil.isNotEmpty(keeperNameByGridId)){
            for (Map gridStatistic : gridStatistics) {
                for (Map map : keeperNameByGridId) {
                    if (gridStatistic.get(Constants.GRID_ID).equals(map.get(Constants.GRID_ID))){
                        gridStatistic.put(Constants.KEEPER_NAME,map.get(Constants.KEEPER_NAME));
                        break;
                    }else {
                        gridStatistic.put(Constants.KEEPER_NAME,"-");
                    }
                }
            }

        }
        return getDataTable(gridStatistics);
    }

    /**
     * 网格出勤率排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridAttendance(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridAttendance:" + JSON.toJSONString(inspectStatisticsVo));
        return getDataTable(inspectStatisticsDAO.getGridAttendance(inspectStatisticsVo));
    }

    /**
     * 网格长/普通网格员出勤率排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridKeeperAttendance(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridKeeperAttendance:" + JSON.toJSONString(inspectStatisticsVo));
        return getDataTable(inspectStatisticsDAO.getGridKeeperAttendance(inspectStatisticsVo));
    }

    /**
     * 网格长/普通网格员出勤率排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridKeeperAttendanceByTwo(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridKeeperAttendance:" + JSON.toJSONString(inspectStatisticsVo));
        return getDataTable(inspectStatisticsDAO.getGridKeeperAttendanceByTwo(inspectStatisticsVo));
    }

    /**
     * 网格打卡状态统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridClockState(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridClockState:" + JSON.toJSONString(inspectStatisticsVo));
        List<Map> gridClockState = inspectStatisticsDAO.getGridClockState(inspectStatisticsVo);
        this.handleGridLevel(gridClockState);
        return getDataTable(gridClockState);
    }

    /**
     * 网格员打卡状态统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "g.grid_id",gridKeeperColumn = "dataTable.keeper_id")
    public TableDataInfo getGridKeeperClockState(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridKeeperClockState:" + JSON.toJSONString(inspectStatisticsVo));
        List<Map> list = inspectStatisticsDAO.getGridKeeperClockState(inspectStatisticsVo);
        if (!CollUtil.isEmpty(list)) {
            this.handleGridLevel(list);
         //根据网格员id查询归属的网格名称
            List<GridKeeperRefVO> refVOList = inspectStatisticsDAO.getGridNameLevelByKeeper(getKeeperIds(JSON.parseArray(JSON.toJSONString(list))));
            List<SysDictDataResponse> gridLevelEnum = systemFeignClient.dictDataType(Constants.GRID_LEVEL_FIELD).getData();
            if (!CollUtil.isEmpty(refVOList)) {
                for (Map vo : list) {
                    refVOList.forEach(item -> {
                        if (item.getKeeperId().toString().equals(vo.get("keeperId"))) {
                            vo.put(Constants.GRID_NAME,!StringUtils.isEmpty(item.getGridName()) ? item.getGridName().replace(Constants.COMMA, Constants.COMMA_CH) : Constants.EMPTY_STR);
                            vo.put(Constants.GRID_LEVEL,!StringUtils.isEmpty(item.getGridLevel()) ? item.getGridLevel().replace(Constants.COMMA, Constants.COMMA_CH) : Constants.EMPTY_STR);
                        }
                    });
                    // 将值按逗号分隔为单独的字符串
                    String[] valueArray = String.valueOf(vo.get(Constants.GRID_LEVEL)).split("，");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : valueArray) {
                        gridLevelEnum.forEach(item -> {
                            if (s.equals(item.getDictValue())){
                                stringBuilder.append(item.getDictLabel()+"，");
                            }
                        });
                    }
                    String value = String.valueOf(stringBuilder);
                    if (!StringUtils.isEmpty(value)){
                        vo.put(Constants.GRID_LEVEL_NAME,value.substring(0, value.length() - 1));
                    }

                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 网格巡查任务数量排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code",gridColumn = "dataTable.grid_id")
    public TableDataInfo getGridInspectTask(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTask:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTask(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 网格任务完成率排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskCompletionRate(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskCompletionRate:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTaskCompletionRate(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 根据网格员类型统计网格任务完成率
     */
    public List<List<Map<String, Object>>> getGridTaskCompletionRateByType(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridTaskCompletionRateByType:" + JSON.toJSONString(inspectStatisticsVo));
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridTaskCompletionRateByType(inspectStatisticsVo);
        Map<String, List<Map<String, Object>>> groupedResults = new HashMap<>();
        inspectTaskTotal.forEach(row -> {
            groupedResults.computeIfAbsent((String) row.get(Constants.GRID_ID), k -> new ArrayList<>()).add(row);
        });
        // 创建一个比较器来排序defaultMap
        Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                String keeperType1 = map1.getOrDefault(Constants.KEEPER_TYPE, "").toString();
                String keeperType2 = map2.getOrDefault(Constants.KEEPER_TYPE, "").toString();
                // 网格长在网格员之前
                if (Constants.ONE.equals(keeperType1) && Constants.TWO.equals(keeperType2)) {
                    return -1; // map1 (网格长) 小于 map2 (网格员)
                } else if (Constants.TWO.equals(keeperType1) && Constants.ONE.equals(keeperType2)) {
                    return 1; // map1 (网格员) 大于 map2 (网格长)
                } else {
                    return 0; // 保持不变
                }
            }
        };
        List<List<Map<String, Object>>> resultList = new ArrayList<>();
        groupedResults.forEach((key, value) -> {
            if (value.size() != 2) {
                boolean hasType1 = value.stream().anyMatch(row -> Constants.ONE.equals(row.get(Constants.KEEPER_TYPE)));
                boolean hasType2 = value.stream().anyMatch(row -> Constants.TWO.equals(row.get(Constants.KEEPER_TYPE)));
                Map<String, Object> defaultMap = new HashMap<>();
                defaultMap.put(Constants.GRID_ID, key);
                defaultMap.put(Constants.GRID_NAME, value.get(0).get(Constants.GRID_NAME).toString());
                defaultMap.put(Constants.KEEPER_ID, Constants.ZERO_NUM);
                if (!hasType1) {
                    defaultMap.put(Constants.KEEPER_TYPE, Constants.ONE);
                    defaultMap.put("keeperType", "网格长");
                }
                if (!hasType2) {
                    defaultMap.put(Constants.KEEPER_TYPE, Constants.TWO);
                    defaultMap.put("keeperType", "网格员");
                }
                defaultMap.put("keeperNum", Constants.ZERO_NUM);
                defaultMap.put("inspectionTaskNum", Constants.ZERO_NUM);
                defaultMap.put("taskCompletedNum", Constants.ZERO_NUM);
                defaultMap.put("taskIncompleteNum", Constants.ZERO_NUM);
                defaultMap.put("taskCompletedRate", BigDecimal.valueOf(0.00));
                value.add(defaultMap);
            }
            // 排序defaultMap
            value.sort(mapComparator);
            resultList.add(value);
        });
        /**
         * 根据网格员类型筛选时：
         * 筛选网格员，展示该租户该行业应用下所有的网格以及网格内所有的网格员信息（只展示网格员）
         * 筛选网格长，展示该租户该行业应用下所有的网格以及网格内所有的网格长信息（只展示网格长）
         */
        if (!StringUtils.isEmpty(inspectStatisticsVo.getKeeperType())) {
            log.info("getGridTaskCompletionRateByType-resp:{}" + JSON.toJSONString(resultList));
            List<List<Map<String, Object>>> filteredResult = filterResultListByKeeperType(resultList, inspectStatisticsVo.getKeeperType());
            return filteredResult;
        }
        return resultList;
    }

    public TableDataInfo getGridTaskCompletionRateToPage(InspectStatisticsVo inspectStatisticsVo){
        // 临时保存分页参数
        Integer originalPageNum = inspectStatisticsVo.getPageNum();
        Integer originalPageSize = inspectStatisticsVo.getPageSize();
        // 清除分页参数
        inspectStatisticsVo.setPageNum(null);
        inspectStatisticsVo.setPageSize(null);
        List<List<Map<String, Object>>> taskCompletionRates =this.getGridTaskCompletionRateByType(inspectStatisticsVo);
        long total = taskCompletionRates.size();

        List<List<Map<String, Object>>> pagedTaskCompletionRates = new ArrayList<>();
        // 分页处理
        Integer pageNum = originalPageNum;
        Integer pageSize = originalPageSize;
        if (pageNum != null && pageSize != null && pageNum > 0 && pageSize > 0){
            int skipNum = pageSize * (pageNum - 1); // 计算跳过的分组数
            // 确保 skipNum 在总数据范围内
            if (skipNum < total) {
                pagedTaskCompletionRates = taskCompletionRates.stream()
                        .skip(skipNum) // 跳过前面的分组
                        .limit(pageSize) // 限制结果到当前页的大小
                        .collect(Collectors.toList());
            }
        }
        TableDataInfo dataTable = getDataTable(pagedTaskCompletionRates);
        dataTable.setTotal(total);
        return dataTable;
    }

    private static List<List<Map<String, Object>>> filterResultListByKeeperType(List<List<Map<String, Object>>> resultList, String keeperType) {
        return resultList.stream()
                .map(innerList -> innerList.stream()
                        .filter(map -> map != null && map.get(Constants.KEEPER_TYPE) != null &&
                                keeperType.equals(map.get(Constants.KEEPER_TYPE).toString()))
                        .collect(Collectors.toList()))
                .filter(innerList -> !innerList.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 巡查任务反馈端统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskBack(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskBack:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTaskBack(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 巡查反馈24小时分布
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskBackByHour(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskBackByHour:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTaskBackByHour(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 查询单个巡查反馈24小时分布
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskBackByHourById(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskBackByHourById:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTaskBackByHourById(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 巡查任务按日期统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskByDay(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskByDay:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal  = inspectStatisticsDAO.getGridInspectTaskByDay(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 巡查任务按日期统计
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskByDayById(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskByDayById:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal  = inspectStatisticsDAO.getGridInspectTaskByDayById(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }


    /**
     * 巡查人任务数量排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskByPerson(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskByPerson:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTaskByPerson(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 巡查人任务完成率排行
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public TableDataInfo getGridInspectTaskRateByPerson(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridInspectTaskRateByPerson:" + JSON.toJSONString(inspectStatisticsVo));
        //巡查任务
        List<Map> inspectTaskTotal = inspectStatisticsDAO.getGridInspectTaskRateByPerson(inspectStatisticsVo);
        return getDataTable(inspectTaskTotal);
    }

    /**
     * 查询网格员信息
     */
    @DataScope(tenantColumn = "dataTable.tenant_id",industryCode = "dataTable.industry_code")
    public Map getGridKeeperById(InspectStatisticsVo inspectStatisticsVo){
        log.info("getGridKeeperById:" + JSON.toJSONString(inspectStatisticsVo));
        Map keeperMap = new HashMap<>();
        List<Map> getGridKeeperById = inspectStatisticsDAO.getGridKeeperById(inspectStatisticsVo);
        StringBuilder gridName = new StringBuilder();
        for (Map map : getGridKeeperById) {
            gridName.append(map.get(Constants.GRID_NAME)).append("，");
        }
        // 删除最后一个逗号和空格
        if (gridName.length() > 0) {
            gridName.setLength(gridName.length() - 1);
            keeperMap.putAll(getGridKeeperById.get(0));
            keeperMap.put(Constants.GRID_NAME, String.valueOf(gridName));
        }
        return keeperMap;
    }

    /**
     * 数据处理，jsonArray转list
     */
    private List<String> getKeeperIds(JSONArray json) {
        List<String> list = new ArrayList<>();
        for (Object object : json) {
            JSONObject obj = (JSONObject) object;
            String keeperId = obj.getString("keeperId");
            list.add(keeperId);
        }
        System.out.println(list);
        return list;
    }

    /**处理网格等级*/
    public void handleGridLevel(List<Map> list){
        List<SysDictDataResponse> gridLevelEnum = systemFeignClient.dictDataType(Constants.GRID_LEVEL_FIELD).getData();
        for (Map<String,Object> map : list) {
            gridLevelEnum.forEach(item -> {
                if (item.getDictValue().equals(MapUtils.getString(map,Constants.GRID_LEVEL))) {
                    map.put(Constants.GRID_LEVEL_NAME,item.getDictLabel());
                }
            });
        }
    }


    @DataScope(tenantColumn = "tenant_id",industryCode = "industry_code", gridColumn = "grid_id", gridKeeperColumn = "keeper_id")
    public InspectionConfStatisticsVO getInspectionConf(InspectionConfStatisticsVO inspectionConfParam){
        InspectionConfStatisticsVO inspectionConf = inspectStatisticsDAO.getInspectionConf(inspectionConfParam);
        return inspectionConf;
    }
}
