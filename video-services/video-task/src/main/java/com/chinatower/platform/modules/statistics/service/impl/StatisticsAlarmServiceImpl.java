package com.chinatower.platform.modules.statistics.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.analysis.api.AnalysisAPI;
import com.chinatower.analysis.api.TTAnalysisServiceAPI;
import com.chinatower.analysis.api.entity.AlarmOrderJudgeVO;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.analysis.api.entity.JudgeVO;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.platform.modules.alarm.service.OperateService;
import com.chinatower.platform.modules.statistics.dao.StatisticsAlarmDAO;
import com.chinatower.platform.modules.statistics.dao.StatisticsDAO;
import com.chinatower.platform.modules.statistics.entity.AlarmJudgeVO;
import com.chinatower.platform.modules.statistics.service.StatisticsAlarmService;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: StatisticsAlarmService
 * @description: 运营研判告警统计
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Service
@Slf4j
public class StatisticsAlarmServiceImpl implements StatisticsAlarmService {

    private static final String PLATFORM_CODE = "platform_code";

    @Resource
    private StatisticsAlarmDAO statisticsAlarmDao;

    @Resource
    private StatisticsDAO statisticsDAO;

    @Autowired
    private OperateService operateService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private NetConnectUtils connectUtils;

    @Override
    public List<JudgeVO> getEventJudgeAlarm(DateVO dateVO) {
        List<JudgeVO> resList = new ArrayList<>();
        String platFormCode = dateVO.getPlatFormCode();
        List<String> upgradeColonyCodeList = dateVO.getUpgradeColonyCodeList();
        //查询下级节点数据
        resList = this.getEventJudgeAlarmLowerLevel(dateVO,platFormCode,"1",upgradeColonyCodeList);
        //查询本节点数据
        resList.addAll(this.getEventJudgeAlarmSelf(dateVO,platFormCode));
        // 下级回传也可能含场景编码，汇总后再剔一次
        return excludeSceneJudgeVos(resList);
    }

    //查询本节点数据
    public List<JudgeVO> getEventJudgeAlarmSelf(DateVO dateVO, String platFormCode) {
        //将两个list中，相同省份、地市、区县、设备、告警源、算法编码的数据数量相加
        List<JudgeVO> resList = new ArrayList<>();
        try{
            //查询告警工单表昨日告警数量和昨日告警研判数量信息
            List<AlarmOrderJudgeVO> alarmList = statisticsAlarmDao.getAlarmNumList(dateVO);
            //查询研判表昨日告警研判数量信息
            //List<AlarmOrderJudgeVO> operateList = statisticsAlarmDao.getOperateNumList(dateVO);
            List<AlarmOrderJudgeVO> operateList = operateService.getOperateNumList(dateVO);

            List<AlarmOrderJudgeVO> totalList = new ArrayList<>();
            totalList.addAll(alarmList);
            totalList.addAll(operateList);
            List<AlarmOrderJudgeVO> resultList = new ArrayList<>();
            if(totalList!=null && !totalList.isEmpty()){
                List<AlarmOrderJudgeVO> finalResultList = resultList;
                totalList.stream().collect(Collectors.groupingBy(item ->
                        (item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getWarningSource() + item.getDeviceCode()
                                + item.getAlgorithmCode()+ item.getWarningTypeId()) + item.getTenantId() + item.getTenantName(), Collectors.toList())).forEach(
                        (id,transfer) -> {
                            if(transfer.size()<=1){
                                transfer.stream().forEach(a->{
                                    AlarmOrderJudgeVO alarmOrderJudgeVO = new AlarmOrderJudgeVO(a.getProvinceId(),a.getCityId(), a.getCountyId(), a.getWarningTypeId(),
                                            a.getAlgorithmCode(),a.getWarningSource(),a.getDeviceCode(),a.getTenantId(),a.getTenantName(),
                                            a.getSerialAlarmNum()+a.getParallelAlarmNum()+a.getNotOpenAlarmNum(),
                                            a.getNotOpenAlarmNum(),
                                            a.getSerialAlarmNum(),
                                            a.getParallelAlarmNum(),
                                            a.getSerialJudgeNum()+a.getParallelJudgeNum()+a.getNotOpenJudgeNum(),
                                            a.getNotOpenJudgeNum(),
                                            a.getSerialJudgeNum(),
                                            a.getParallelJudgeNum(),
                                            a.getSerialRealAlarmNum()+a.getParallelRealAlarmNum()+a.getNotOpenRealAlarmNum(),
                                            a.getNotOpenRealAlarmNum(),
                                            a.getSerialRealAlarmNum(),
                                            a.getParallelRealAlarmNum(),
                                            a.getSerialFalseAlarmNum()+a.getParallelFalseAlarmNum()+a.getNotOpenFalseAlarmNum(),
                                            a.getNotOpenFalseAlarmNum(),
                                            a.getSerialFalseAlarmNum(),
                                            a.getParallelFalseAlarmNum());
                                    finalResultList.add(alarmOrderJudgeVO);
                                });
                            }else{
                                transfer.stream().reduce((a,b) ->
                                        new AlarmOrderJudgeVO(a.getProvinceId(),a.getCityId(), a.getCountyId(), a.getWarningTypeId(),
                                                a.getAlgorithmCode(),a.getWarningSource(),a.getDeviceCode(),a.getTenantId(),a.getTenantName(),
                                                a.getSerialAlarmNum()+b.getSerialAlarmNum()+a.getParallelAlarmNum()+b.getParallelAlarmNum()+a.getNotOpenAlarmNum()+b.getNotOpenAlarmNum(),
                                                a.getNotOpenAlarmNum()+b.getNotOpenAlarmNum(),
                                                a.getSerialAlarmNum()+b.getSerialAlarmNum(),
                                                a.getParallelAlarmNum()+b.getParallelAlarmNum(),
                                                a.getSerialJudgeNum()+b.getSerialJudgeNum()+a.getParallelJudgeNum()+b.getParallelJudgeNum()+a.getNotOpenJudgeNum()+b.getNotOpenJudgeNum(),
                                                a.getNotOpenJudgeNum()+b.getNotOpenJudgeNum(),
                                                a.getSerialJudgeNum()+b.getSerialJudgeNum(),
                                                a.getParallelJudgeNum()+b.getParallelJudgeNum(),
                                                a.getSerialRealAlarmNum()+b.getSerialRealAlarmNum()+a.getParallelRealAlarmNum()+b.getParallelRealAlarmNum()+a.getNotOpenRealAlarmNum()+b.getNotOpenRealAlarmNum(),
                                                a.getNotOpenRealAlarmNum()+b.getNotOpenRealAlarmNum(),
                                                a.getSerialRealAlarmNum()+b.getSerialRealAlarmNum(),
                                                a.getParallelRealAlarmNum()+b.getParallelRealAlarmNum(),
                                                a.getSerialFalseAlarmNum()+b.getSerialFalseAlarmNum()+a.getParallelFalseAlarmNum()+b.getParallelFalseAlarmNum()+a.getNotOpenFalseAlarmNum()+b.getNotOpenFalseAlarmNum(),
                                                a.getNotOpenFalseAlarmNum()+b.getNotOpenFalseAlarmNum(),
                                                a.getSerialFalseAlarmNum()+b.getSerialFalseAlarmNum(),
                                                a.getParallelFalseAlarmNum()+b.getParallelFalseAlarmNum())).
                                        ifPresent(finalResultList::add);
                            }
                        });
            }
            if(resultList!=null && !resultList.isEmpty()){
                // 大模型不展示场景：剔除 sys_scene_config 场景编码，保留下游任务/小模型类型
                resultList = excludeSceneWarningTypes(resultList);
                Map<String, String> typeNameMap = buildWarningTypeNameMap();
                String platformName = statisticsAlarmDao.getPlatformNameByCode(platFormCode);
                resultList.stream().forEach(item->{
                    JudgeVO judgeVO = new JudgeVO();
                    BeanUtils.copyProperties(item, judgeVO);
                    judgeVO.setColonyCode(platFormCode);
                    judgeVO.setColonyName(platformName);
                    if (StringUtils.isNotEmpty(item.getWarningTypeId())) {
                        judgeVO.setWarningTypeName(typeNameMap.get(item.getWarningTypeId()));
                    }
                    resList.add(judgeVO);
                });
            }
            log.info("------铁塔视角--运营研判--算法编码维度统计AI告警定时------结束");
        }catch(Exception e){
            log.info("******铁塔视角--运营研判--算法编码维度统计AI告警定时******系统异常信息******：" + e);
        }
        return resList;
    }


    //查询本节点数据
    public List<JudgeVO> getEventJudgeAlarmSelfParam(DateVO dateVO, String platFormCode) {
        //将两个list中，相同省份、地市、区县、设备、告警源、算法编码的数据数量相加
        List<JudgeVO> resList = new ArrayList<>();
        try{
            //查询告警工单表昨日告警数量和昨日告警研判数量信息
            List<AlarmOrderJudgeVO> alarmList = statisticsAlarmDao.getAlarmNumListParam(dateVO);
            //查询研判表昨日告警研判数量信息
            List<AlarmOrderJudgeVO> operateList = operateService.getOperateNumListParam(dateVO);
            List<AlarmOrderJudgeVO> totalList = new ArrayList<>();
            totalList.addAll(alarmList);
            totalList.addAll(operateList);
            List<AlarmOrderJudgeVO> resultList = new ArrayList<>();
            if(totalList!=null && !totalList.isEmpty()){
                List<AlarmOrderJudgeVO> finalResultList = resultList;
                totalList.stream().collect(Collectors.groupingBy(item ->
                        (item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getWarningSource() + item.getDeviceCode()
                                + item.getAlgorithmCode()+ item.getWarningTypeId()) + item.getTenantId() + item.getTenantName() + item.getDateTime(), Collectors.toList())).forEach(
                        (id,transfer) -> {
                            if(transfer.size()<=1){
                                transfer.stream().forEach(a->{
                                    AlarmOrderJudgeVO alarmOrderJudgeVO = new AlarmOrderJudgeVO(a.getProvinceId(),a.getCityId(), a.getCountyId(), a.getWarningTypeId(),
                                            a.getAlgorithmCode(),a.getWarningSource(),a.getDeviceCode(),a.getTenantId(),a.getTenantName(),
                                            a.getSerialAlarmNum()+a.getParallelAlarmNum()+a.getNotOpenAlarmNum(),
                                            a.getNotOpenAlarmNum(),
                                            a.getSerialAlarmNum(),
                                            a.getParallelAlarmNum(),
                                            a.getSerialJudgeNum()+a.getParallelJudgeNum()+a.getNotOpenJudgeNum(),
                                            a.getNotOpenJudgeNum(),
                                            a.getSerialJudgeNum(),
                                            a.getParallelJudgeNum(),
                                            a.getSerialRealAlarmNum()+a.getParallelRealAlarmNum()+a.getNotOpenRealAlarmNum(),
                                            a.getNotOpenRealAlarmNum(),
                                            a.getSerialRealAlarmNum(),
                                            a.getParallelRealAlarmNum(),
                                            a.getSerialFalseAlarmNum()+a.getParallelFalseAlarmNum()+a.getNotOpenFalseAlarmNum(),
                                            a.getNotOpenFalseAlarmNum(),
                                            a.getSerialFalseAlarmNum(),
                                            a.getParallelFalseAlarmNum(),
                                            a.getDateTime(),
                                            a.getYearStr(),
                                            a.getMonthStr(),
                                            a.getDayStr());
                                    finalResultList.add(alarmOrderJudgeVO);
                                });
                            }else{
                                transfer.stream().reduce((a,b) ->
                                        new AlarmOrderJudgeVO(a.getProvinceId(),a.getCityId(), a.getCountyId(), a.getWarningTypeId(),
                                                a.getAlgorithmCode(),a.getWarningSource(),a.getDeviceCode(),a.getTenantId(),a.getTenantName(),
                                                a.getSerialAlarmNum()+b.getSerialAlarmNum()+a.getParallelAlarmNum()+b.getParallelAlarmNum()+a.getNotOpenAlarmNum()+b.getNotOpenAlarmNum(),
                                                a.getNotOpenAlarmNum()+b.getNotOpenAlarmNum(),
                                                a.getSerialAlarmNum()+b.getSerialAlarmNum(),
                                                a.getParallelAlarmNum()+b.getParallelAlarmNum(),
                                                a.getSerialJudgeNum()+b.getSerialJudgeNum()+a.getParallelJudgeNum()+b.getParallelJudgeNum()+a.getNotOpenJudgeNum()+b.getNotOpenJudgeNum(),
                                                a.getNotOpenJudgeNum()+b.getNotOpenJudgeNum(),
                                                a.getSerialJudgeNum()+b.getSerialJudgeNum(),
                                                a.getParallelJudgeNum()+b.getParallelJudgeNum(),
                                                a.getSerialRealAlarmNum()+b.getSerialRealAlarmNum()+a.getParallelRealAlarmNum()+b.getParallelRealAlarmNum()+a.getNotOpenRealAlarmNum()+b.getNotOpenRealAlarmNum(),
                                                a.getNotOpenRealAlarmNum()+b.getNotOpenRealAlarmNum(),
                                                a.getSerialRealAlarmNum()+b.getSerialRealAlarmNum(),
                                                a.getParallelRealAlarmNum()+b.getParallelRealAlarmNum(),
                                                a.getSerialFalseAlarmNum()+b.getSerialFalseAlarmNum()+a.getParallelFalseAlarmNum()+b.getParallelFalseAlarmNum()+a.getNotOpenFalseAlarmNum()+b.getNotOpenFalseAlarmNum(),
                                                a.getNotOpenFalseAlarmNum()+b.getNotOpenFalseAlarmNum(),
                                                a.getSerialFalseAlarmNum()+b.getSerialFalseAlarmNum(),
                                                a.getParallelFalseAlarmNum()+b.getParallelFalseAlarmNum(),
                                                a.getDateTime(),
                                                a.getYearStr(),
                                                a.getMonthStr(),
                                                a.getDayStr())).
                                        ifPresent(finalResultList::add);
                            }
                        });
            }
            if(resultList!=null && !resultList.isEmpty()){
                // 大模型不展示场景：剔除 sys_scene_config 场景编码，保留下游任务/小模型类型
                resultList = excludeSceneWarningTypes(resultList);
                Map<String, String> typeNameMap = buildWarningTypeNameMap();
                String platformName = statisticsAlarmDao.getPlatformNameByCode(platFormCode);
                resultList.stream().forEach(item->{
                    JudgeVO judgeVO = new JudgeVO();
                    BeanUtils.copyProperties(item, judgeVO);
                    judgeVO.setColonyCode(platFormCode);
                    judgeVO.setColonyName(platformName);
                    if (StringUtils.isNotEmpty(item.getWarningTypeId())) {
                        judgeVO.setWarningTypeName(typeNameMap.get(item.getWarningTypeId()));
                    }
                    resList.add(judgeVO);
                });
            }
            log.info("------铁塔视角--运营研判--算法编码维度统计AI告警定时------结束");
        }catch(Exception e){
            log.info("******铁塔视角--运营研判--算法编码维度统计AI告警定时******系统异常信息******：" + e);
        }
        return resList;
    }

    /**
     * 剔除大模型纯场景编码，避免算法统计按场景维度展示。
     * 编码若同时是场景 type_value 与下游 alarm_type（如 9212102），按下游保留。
     */
    private List<AlarmOrderJudgeVO> excludeSceneWarningTypes(List<AlarmOrderJudgeVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        Set<String> sceneIds = loadSceneIdSet();
        if (sceneIds.isEmpty()) {
            return list;
        }
        Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
        return list.stream()
                .filter(item -> item != null
                        && keepWarningTypeId(item.getWarningTypeId(), sceneIds, downstreamIds))
                .collect(Collectors.toList());
    }

    /** 汇总结果（含下级回传）再剔纯场景编码；下游任务编码即使与场景重叠也保留 */
    private List<JudgeVO> excludeSceneJudgeVos(List<JudgeVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        Set<String> sceneIds = loadSceneIdSet();
        if (sceneIds.isEmpty()) {
            return list;
        }
        Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
        return list.stream()
                .filter(item -> item != null
                        && keepWarningTypeId(item.getWarningTypeId(), sceneIds, downstreamIds))
                .collect(Collectors.toList());
    }

    /**
     * 纯场景剔除；在 sys_scene_alarm_rel 中的编码一律按下游保留。
     */
    private boolean keepWarningTypeId(String warningTypeId, Set<String> sceneIds, Set<String> downstreamIds) {
        if (StringUtils.isEmpty(warningTypeId)) {
            return false;
        }
        if (downstreamIds.contains(warningTypeId)) {
            return true;
        }
        return !sceneIds.contains(warningTypeId);
    }

    private Set<String> loadSceneIdSet() {
        List<String> sceneIds = statisticsDAO.getSceneIds();
        if (CollectionUtils.isEmpty(sceneIds)) {
            return Collections.emptySet();
        }
        return sceneIds.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }

    private Set<String> loadDownstreamAlarmTypeIdSet() {
        List<String> ids = statisticsDAO.getDownstreamAlarmTypeIds();
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        return ids.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }

    /**
     * 网关拉下级算法统计的时间窗：yyyy-MM-dd → 当日；yyyy-MM → 整月。
     */
    private String[] resolveJudgeAlarmQueryRange(String handleDate) {
        if (StringUtils.isEmpty(handleDate)) {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            return new String[]{today + " 00:00:00", today + " 23:59:59"};
        }
        if (handleDate.length() >= 10) {
            String day = handleDate.substring(0, 10);
            return new String[]{day + " 00:00:00", day + " 23:59:59"};
        }
        String ym = handleDate.length() >= 7 ? handleDate.substring(0, 7) : handleDate;
        YearMonth yearMonth = YearMonth.parse(ym);
        String begin = ym + "-01 00:00:00";
        String end = ym + "-" + String.format("%02d", yearMonth.lengthOfMonth()) + " 23:59:59";
        return new String[]{begin, end};
    }

    /**
     * 告警类型/下游任务名称：先 sys_alarm_type，再用 sys_scene_alarm_rel 覆盖。
     */
    private Map<String, String> buildWarningTypeNameMap() {
        Map<String, String> typeMap = new HashMap<>();
        List<Map<String, String>> alarmTypeList = statisticsDAO.getAlarmType();
        if (!CollectionUtils.isEmpty(alarmTypeList)) {
            for (Map<String, String> t : alarmTypeList) {
                if (t == null || StringUtils.isEmpty(t.get("typeId"))) {
                    continue;
                }
                typeMap.put(t.get("typeId"), t.get("typeName"));
            }
        }
        List<Map<String, String>> subSceneNames = statisticsDAO.getSubSceneAlarmTypeNames();
        if (!CollectionUtils.isEmpty(subSceneNames)) {
            for (Map<String, String> t : subSceneNames) {
                if (t == null || StringUtils.isEmpty(t.get("typeId")) || StringUtils.isEmpty(t.get("typeName"))) {
                    continue;
                }
                typeMap.put(t.get("typeId"), t.get("typeName"));
            }
        }
        return typeMap;
    }

    //查询下级节点数据
    public List<JudgeVO> getEventJudgeAlarmLowerLevel(DateVO dateVO, String platFormCode,String type, List<String> upgradeColonyCodeList) {
        List<JudgeVO> resList = new ArrayList<>();
        if (StringUtils.isNotEmpty(platFormCode)){
            //查询当前节点的下级节点
            List<Map<String,String>> lowerPlatformList = this.getAllPlatformCode(platFormCode);
            List<String> platformCodeList = lowerPlatformList.stream()
                    .map(map -> map.get(Constants.PLATFORM_CODE))
                    .distinct()
                    .collect(Collectors.toList());
            String msgId = UUID.randomUUID().toString().replace("-", "");
            log.info("全部下级节点:{}" , JSONObject.toJSONString(lowerPlatformList));
            if (!CollectionUtils.isEmpty(lowerPlatformList)){
                //异步查询下级节点的算法统计数据
                ThreadParallelUtil.run(platformCodeList, toPlatformCode->{
                    try {
                        //判断该节点 是否是已升级节点，是就走级联网关调用该节点feign统计接口，否就走网关转发调用该节点的算法统计接口
                        if (!CollectionUtils.isEmpty(upgradeColonyCodeList) && upgradeColonyCodeList.contains(toPlatformCode)){
                            log.info("调用级联网关查询feign-getEventJudgeAlarm接口入参:{},开始执行节点:{}" , JSONObject.toJSONString(dateVO),toPlatformCode);
                            Result result = RemoteNodeUtils.create(toPlatformCode)
                                    .getFeign(TTAnalysisServiceAPI.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                                    .getEventJudgeAlarm(dateVO);
                            if (result.getCode() == ResultCode.SUCCESS.getCode()){
                                log.info("调用级联网关查询feign-getEventJudgeAlarm结果：{}" + JSONObject.toJSONString(result));
                                if (ObjectUtil.isNotNull(result.getData())){
                                    List<JudgeVO> alarmJudgeVOList = JSONObject.parseArray(JSON.toJSONString(result.getData()),JudgeVO.class);
                                    resList.addAll(alarmJudgeVOList);
                                }
                            }else {
                                log.info("调用级联网关查询feign-getEventJudgeAlarm失败：{}，当前节点:{},失败下级节点:{},msgId:{}" + JSONObject.toJSONString(result),platFormCode,toPlatformCode,msgId);
                            }
                        }else {
                            // 有参重跑(type=2)：读接口 getAlgorithmJudgeAlarm 为时间窗聚合，无按日 dateTime；
                            // 写入 addJudgeAlarmParam 会变成 create_date=NULL，列表按时间窗查空。未升级属地改由本级自算或属地自行重跑。
                            if (!"1".equals(type)) {
                                log.info("有参重跑跳过未升级下级网关拉数, toPlatformCode={}", toPlatformCode);
                                return null;
                            }
                            NetConnectUtils.RequestParamter<AlarmJudgeVO> paramter = new NetConnectUtils.RequestParamter<>();
                            AlarmJudgeVO vo = new AlarmJudgeVO();
                            String[] range = resolveJudgeAlarmQueryRange(dateVO.getHandleDate());
                            vo.setBeginDate(range[0]);
                            vo.setEndDate(range[1]);
                            vo.setDimensionFlag("1");
                            // 请求参数
                            paramter.setBody(vo);
                            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_ALGORITHM_JUDGE_ALARM);
                            paramter.setPath(path);
                            String requestTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            log.info("调用网关转发调用该节点的算法统计接口入参:{},开始执行节点:{}" , JSONObject.toJSONString(dateVO),toPlatformCode);
                            ResultEntity cr = connectUtils.post(paramter, ResultEntity.class,
                                    NetConnectUtils.HeaderKeyEnum.X_MsgId.of(msgId),
                                    NetConnectUtils.HeaderKeyEnum.X_RequsetTime.of(requestTime),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platFormCode),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(Constants.NEWLAND_BOOT_INNER),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                            log.info("调用网关查询算法统计结果：" + JSONObject.toJSONString(cr));
                            if (ObjectUtil.isNotNull(cr) && ObjectUtil.isNotNull(cr.getRows())){
                                List<AlarmJudgeVO> alarmJudgeVOList = JSONObject.parseArray(JSON.toJSONString(cr.getRows()),AlarmJudgeVO.class);
                                if (!CollectionUtils.isEmpty(alarmJudgeVOList)){
                                    String colonyName = "";
                                    //回显属地名称
                                    for (Map<String, String> stringMap : lowerPlatformList) {
                                        if (stringMap.get(Constants.PLATFORM_CODE).equals(toPlatformCode)){
                                            colonyName = stringMap.get("platform_name");
                                        }
                                    }
                                    for (AlarmJudgeVO alarmJudgeVO : alarmJudgeVOList) {
                                        JudgeVO judgeVO = new JudgeVO();
                                        BeanUtils.copyProperties(alarmJudgeVO, judgeVO);
                                        judgeVO.setColonyCode(toPlatformCode);
                                        judgeVO.setColonyName(colonyName);
                                        resList.add(judgeVO);
                                    }
                                }
                            }
                        }
                    }catch (Exception e) {
                        log.error("当前节点:{},失败下级节点:{},msgId:{}",platFormCode,toPlatformCode,msgId);
                        log.error("调用失败{}",e);

                    }
                    return null;
                }, ThreadParallelUtil.ConfigFactory.TASK_ALGORITHM_STATISTICS);
            }
        }
        return resList;
    }
    @Override
    public void insertEventJudgeAlarm(DateVO dateVO, List<JudgeVO> list) {
        try {
            //删除当天的数据
            statisticsAlarmDao.delJudgeAlarm(dateVO);
            List<List<JudgeVO>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    //插入当前的数据
                    statisticsAlarmDao.addJudgeAlarm(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }


    @Override
    public List<JudgeVO> getEventJudgeAlarmParam(DateVO dateVO) {
        List<JudgeVO> resList = new ArrayList<>();
        String platFormCode = dateVO.getPlatFormCode();
        List<String> upgradeColonyCodeList = dateVO.getUpgradeColonyCodeList();
        //查询下级节点数据
        resList = this.getEventJudgeAlarmLowerLevel(dateVO,platFormCode,"2",upgradeColonyCodeList);
        //查询本节点数据
        resList.addAll(this.getEventJudgeAlarmSelfParam(dateVO,platFormCode));
        // 下级回传也可能含场景编码，汇总后再剔一次
        return excludeSceneJudgeVos(resList);
    }
    @Override
    public int insertEventJudgeAlarmParam(DateVO dateVO, List<JudgeVO> list) {
        try {
            //删除当天的数据（空 list 也删，清历史场景脏数据）
            statisticsAlarmDao.delJudgeAlarmParam(dateVO);
            if (CollectionUtils.isEmpty(list)) {
                return 0;
            }
            // 无 dateTime 时 create_date 会写成 NULL，读侧按时间窗查不到
            List<JudgeVO> datedList = list.stream()
                    .filter(item -> item != null && StringUtils.isNotEmpty(item.getDateTime()))
                    .collect(Collectors.toList());
            if (datedList.size() < list.size()) {
                log.warn("insertEventJudgeAlarmParam 丢弃无 dateTime 行: total={}, kept={}",
                        list.size(), datedList.size());
            }
            if (CollectionUtils.isEmpty(datedList)) {
                return 0;
            }
            return statisticsAlarmDao.addJudgeAlarmParam(datedList);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    /**
     * 获取指定平台及其下级节点的所有平台编码。
     * 方法会递归查询指定平台的下级节点，最多递归 5 层，将所有平台编码信息汇总到结果列表中返回。
     *
     * @param platFormCode 当前平台的编码
     * @return 包含所有下级平台编码的列表，每个元素是一个 Map，包含平台信息
     */
    public List<Map<String,String>> getAllPlatformCode(String platFormCode){
        //查询当前节点的下级节点
        List<Map<String,String>> allPlatformCodeList = new ArrayList<>();
        // 初始化当前需要查询的父节点编码列表（从传入的 `platFormCode` 开始）
        List<String> parentCodeList = new ArrayList<>();
                parentCodeList.add(platFormCode);
                // 设置递归查询的最大层级为 5
                int i = 1;
                while (i<=5){
                    i++; // 每循环一次，层级递增
                    // 调用 DAO 方法查询当前父节点列表的下级节点信息
                    List<Map<String, String>> mapList = statisticsAlarmDao.getPlatformCodeList(parentCodeList);
                    // 提取下级节点的 `platformCode` 列表，并去重
                    parentCodeList = mapList.stream()
                            .map(map -> map.get(Constants.PLATFORM_CODE))
                            .distinct()
                            .collect(Collectors.toList());
                    // 如果查询结果不为空，则将查询到的节点信息加入结果列表
                    if(!CollectionUtils.isEmpty(parentCodeList)){
                        allPlatformCodeList.addAll(mapList);
                    }else{
                        // 如果查询结果为空，说明没有下级节点，终止循环
                        i=6;
                    }
                }
        return allPlatformCodeList;
    }



}
