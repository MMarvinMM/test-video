package com.chinatower.platform.modules.statistics.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.modules.alarm.service.InterceptService;
import com.chinatower.platform.modules.statistics.dao.TowerStatisticsDAO;
import com.chinatower.platform.modules.statistics.entity.*;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.statistics.dao.StatisticsDAO;
import com.chinatower.platform.utils.DataUtil;
import com.chinatower.common.core.utils.TableUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * @className: StatisticsService
 * @description: StatisticsService
 * @author: csj
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class StatisticsService {
    @Resource
    private StatisticsDAO statisticsDAO;

    @Autowired
    private StatisticsService self;

    private String sourceStaticTaskTip = "按照告警来源沉淀数据定时";

    @Resource
    private SystemFeignClient systemFeignClient;
    @Resource
    private  TowerStatisticsDAO towerStatisticsDAO;

    @Autowired
    private InterceptService interceptService;

    @Autowired
    private MongoTemplate mongoTemplate;


        /**
     * UAV飞行任务处理函数
     * 该函数用于处理无人机飞行任务的统计工作，根据传入的日期参数获取飞行任务数据并插入统计表
     *
     * @param param 日期参数字符串，多个日期用逗号分隔，格式为yyyy-MM，如果为空则处理当前月份
     * @return ReturnT<String> 执行结果，成功返回ReturnT.SUCCESS，失败返回相应的错误信息
     */
    @XxlJob("uavFlightTasksHandler")
    public ReturnT<String> uavFlightTasksHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            // 遍历处理每个指定日期的飞行任务统计
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info("飞行统计数据统计表-"+dates[i]);
                    XxlJobLogger.log("飞行统计数据统计表-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<UavFlightTaskVO> list = getUavFlightTask(dateVO);
                    // 获取到飞行任务数据后插入统计表
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertUavFlightTask(dateVO, list,param));
                    }
                }
            }
        } else {
            log.info("飞行统计数据统计表");
            XxlJobLogger.log("飞行统计数据统计表");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            //取前一天的数据
            LocalDate previousDay = LocalDate.now().minusDays(1);
            dateVO.setHandleDate(previousDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            List<UavFlightTaskVO> list = getUavFlightTask(dateVO);
            // 获取到飞行任务数据后插入统计表
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertUavFlightTask(dateVO, list,param));
            }
        }
        return ReturnT.SUCCESS;
    }


    private List<UavFlightTaskVO> getUavFlightTask(DateVO dateVO){
        return statisticsDAO.getUavFlightTask(dateVO);
    }

 /*   public static void main(String[] args) {
        String a = "{\"wayline.wpml\":{\"_declaration\":{\"_attributes\":{\"encoding\":\"UTF-8\",\"version\":\"1.0\"}},\"kml\":{\"Document\":{\"Folder\":[{\"wpml:waylineId\":\"0\",\"Placemark\":[{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:actionGroup\":{\"wpml:actionGroupStartIndex\":\"0\",\"wpml:action\":[{\"wpml:actionActuatorFunc\":\"gimbalRotate\",\"wpml:actionId\":\"0\",\"wpml:actionActuatorFuncParam\":{\"wpml:gimbalRotateTime\":\"10\",\"wpml:gimbalHeadingYawBase\":\"aircraft\",\"wpml:gimbalRotateTimeEnable\":\"0\",\"wpml:gimbalRollRotateEnable\":\"0\",\"wpml:payloadPositionIndex\":\"0\",\"wpml:gimbalRollRotateAngle\":\"0\",\"wpml:gimbalRotateMode\":\"absoluteAngle\",\"wpml:gimbalPitchRotateAngle\":\"-90\",\"wpml:gimbalPitchRotateEnable\":\"1\",\"wpml:gimbalYawRotateEnable\":\"0\",\"wpml:gimbalYawRotateAngle\":\"0\"}},{\"wpml:actionActuatorFunc\":\"takePhoto\",\"wpml:actionId\":\"1\",\"wpml:actionActuatorFuncParam\":{\"wpml:payloadPositionIndex\":\"0\",\"wpml:payloadLensIndex\":\"visable,ir\",\"wpml:useGlobalPayloadLensIndex\":\"0\"}}],\"wpml:actionGroupEndIndex\":\"3\",\"wpml:actionGroupMode\":\"sequence\",\"wpml:actionGroupId\":\"0\",\"wpml:actionTrigger\":{\"wpml:actionTriggerParam\":\"2\",\"wpml:actionTriggerType\":\"multipleTiming\"}},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"0\",\"Point\":{\"coordinates\":\"122.24043681436022,43.64718520163566\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"9.17\",\"wpml:waypointTurnMode\":\"coordinateTurn\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"1\",\"Point\":{\"coordinates\":\"122.24042777871838,43.64681342101962\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"9.17\",\"wpml:waypointTurnMode\":\"coordinateTurn\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"2\",\"Point\":{\"coordinates\":\"122.24005573817793,43.64677336958284\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"0\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"3\",\"Point\":{\"coordinates\":\"122.24006458741948,43.64713757353656\"},\"wpml:executeHeight\":\"120\"}],\"wpml:duration\":\"24.91876448230473\",\"wpml:executeHeightMode\":\"relativeToStartPoint\",\"wpml:autoFlightSpeed\":\"4.5\",\"wpml:distance\":\"112.13444017037129\",\"wpml:templateId\":\"0\"},{\"wpml:waylineId\":\"1\",\"Placemark\":[{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:actionGroup\":{\"wpml:actionGroupStartIndex\":\"0\",\"wpml:action\":[{\"wpml:actionActuatorFunc\":\"gimbalRotate\",\"wpml:actionId\":\"0\",\"wpml:actionActuatorFuncParam\":{\"wpml:gimbalRotateTime\":\"10\",\"wpml:gimbalHeadingYawBase\":\"aircraft\",\"wpml:gimbalRotateTimeEnable\":\"0\",\"wpml:gimbalRollRotateEnable\":\"0\",\"wpml:payloadPositionIndex\":\"0\",\"wpml:gimbalRollRotateAngle\":\"0\",\"wpml:gimbalRotateMode\":\"absoluteAngle\",\"wpml:gimbalPitchRotateAngle\":\"-85\",\"wpml:gimbalPitchRotateEnable\":\"1\",\"wpml:gimbalYawRotateEnable\":\"0\",\"wpml:gimbalYawRotateAngle\":\"0\"}},{\"wpml:actionActuatorFunc\":\"takePhoto\",\"wpml:actionId\":\"1\",\"wpml:actionActuatorFuncParam\":{\"wpml:payloadPositionIndex\":\"0\",\"wpml:payloadLensIndex\":\"visable,ir\",\"wpml:useGlobalPayloadLensIndex\":\"0\"}}],\"wpml:actionGroupEndIndex\":\"3\",\"wpml:actionGroupMode\":\"sequence\",\"wpml:actionGroupId\":\"0\",\"wpml:actionTrigger\":{\"wpml:actionTriggerParam\":\"2\",\"wpml:actionTriggerType\":\"multipleTiming\"}},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"0\",\"Point\":{\"coordinates\":\"122.24043565623421,43.647090923066806\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"9.17\",\"wpml:waypointTurnMode\":\"coordinateTurn\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"1\",\"Point\":{\"coordinates\":\"122.24042662062155,43.646719142444404\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"9.17\",\"wpml:waypointTurnMode\":\"coordinateTurn\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"2\",\"Point\":{\"coordinates\":\"122.24005458066458,43.64667909099906\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"0\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"0\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"3\",\"Point\":{\"coordinates\":\"122.24006342987755,43.64704329495898\"},\"wpml:executeHeight\":\"120\"}],\"wpml:duration\":\"24.918764482655693\",\"wpml:executeHeightMode\":\"relativeToStartPoint\",\"wpml:autoFlightSpeed\":\"4.5\",\"wpml:distance\":\"112.13444017195062\",\"wpml:templateId\":\"1\"},{\"wpml:waylineId\":\"2\",\"Placemark\":[{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"90\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:actionGroup\":{\"wpml:actionGroupStartIndex\":\"0\",\"wpml:action\":[{\"wpml:actionActuatorFunc\":\"gimbalRotate\",\"wpml:actionId\":\"0\",\"wpml:actionActuatorFuncParam\":{\"wpml:gimbalRotateTime\":\"10\",\"wpml:gimbalHeadingYawBase\":\"aircraft\",\"wpml:gimbalRotateTimeEnable\":\"0\",\"wpml:gimbalRollRotateEnable\":\"0\",\"wpml:payloadPositionIndex\":\"0\",\"wpml:gimbalRollRotateAngle\":\"0\",\"wpml:gimbalRotateMode\":\"absoluteAngle\",\"wpml:gimbalPitchRotateAngle\":\"-85\",\"wpml:gimbalPitchRotateEnable\":\"1\",\"wpml:gimbalYawRotateEnable\":\"0\",\"wpml:gimbalYawRotateAngle\":\"0\"}},{\"wpml:actionActuatorFunc\":\"takePhoto\",\"wpml:actionId\":\"1\",\"wpml:actionActuatorFuncParam\":{\"wpml:payloadPositionIndex\":\"0\",\"wpml:payloadLensIndex\":\"visable,ir\",\"wpml:useGlobalPayloadLensIndex\":\"0\"}}],\"wpml:actionGroupEndIndex\":\"1\",\"wpml:actionGroupMode\":\"sequence\",\"wpml:actionGroupId\":\"0\",\"wpml:actionTrigger\":{\"wpml:actionTriggerParam\":\"2\",\"wpml:actionTriggerType\":\"multipleTiming\"}},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"0\",\"Point\":{\"coordinates\":\"122.24052508904232,43.64698182661631\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"0\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"90\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"1\",\"Point\":{\"coordinates\":\"122.23973283279746,43.64699197769156\"},\"wpml:executeHeight\":\"120\"}],\"wpml:duration\":\"14.207207876131813\",\"wpml:executeHeightMode\":\"relativeToStartPoint\",\"wpml:autoFlightSpeed\":\"4.5\",\"wpml:distance\":\"63.93243544259316\",\"wpml:templateId\":\"2\"},{\"wpml:waylineId\":\"3\",\"Placemark\":[{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"180\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:actionGroup\":{\"wpml:actionGroupStartIndex\":\"0\",\"wpml:action\":[{\"wpml:actionActuatorFunc\":\"gimbalRotate\",\"wpml:actionId\":\"0\",\"wpml:actionActuatorFuncParam\":{\"wpml:gimbalRotateTime\":\"10\",\"wpml:gimbalHeadingYawBase\":\"aircraft\",\"wpml:gimbalRotateTimeEnable\":\"0\",\"wpml:gimbalRollRotateEnable\":\"0\",\"wpml:payloadPositionIndex\":\"0\",\"wpml:gimbalRollRotateAngle\":\"0\",\"wpml:gimbalRotateMode\":\"absoluteAngle\",\"wpml:gimbalPitchRotateAngle\":\"-85\",\"wpml:gimbalPitchRotateEnable\":\"1\",\"wpml:gimbalYawRotateEnable\":\"0\",\"wpml:gimbalYawRotateAngle\":\"0\"}},{\"wpml:actionActuatorFunc\":\"takePhoto\",\"wpml:actionId\":\"1\",\"wpml:actionActuatorFuncParam\":{\"wpml:payloadPositionIndex\":\"0\",\"wpml:payloadLensIndex\":\"visable,ir\",\"wpml:useGlobalPayloadLensIndex\":\"0\"}}],\"wpml:actionGroupEndIndex\":\"3\",\"wpml:actionGroupMode\":\"sequence\",\"wpml:actionGroupId\":\"0\",\"wpml:actionTrigger\":{\"wpml:actionTriggerParam\":\"2\",\"wpml:actionTriggerType\":\"multipleTiming\"}},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"0\",\"Point\":{\"coordinates\":\"122.24043797249182,43.64727948020292\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"180\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"9.17\",\"wpml:waypointTurnMode\":\"coordinateTurn\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"1\",\"Point\":{\"coordinates\":\"122.24042893682088,43.646907699593235\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"180\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"9.17\",\"wpml:waypointTurnMode\":\"coordinateTurn\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"2\",\"Point\":{\"coordinates\":\"122.24005689569691,43.646867648165056\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"0\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"180\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"3\",\"Point\":{\"coordinates\":\"122.24006574496701,43.64723185211253\"},\"wpml:executeHeight\":\"120\"}],\"wpml:duration\":\"24.918764481942944\",\"wpml:executeHeightMode\":\"relativeToStartPoint\",\"wpml:autoFlightSpeed\":\"4.5\",\"wpml:distance\":\"112.13444016874324\",\"wpml:templateId\":\"3\"},{\"wpml:waylineId\":\"4\",\"Placemark\":[{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"1\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"-90\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"fixed\"},\"wpml:actionGroup\":{\"wpml:actionGroupStartIndex\":\"0\",\"wpml:action\":[{\"wpml:actionActuatorFunc\":\"gimbalRotate\",\"wpml:actionId\":\"0\",\"wpml:actionActuatorFuncParam\":{\"wpml:gimbalRotateTime\":\"10\",\"wpml:gimbalHeadingYawBase\":\"aircraft\",\"wpml:gimbalRotateTimeEnable\":\"0\",\"wpml:gimbalRollRotateEnable\":\"0\",\"wpml:payloadPositionIndex\":\"0\",\"wpml:gimbalRollRotateAngle\":\"0\",\"wpml:gimbalRotateMode\":\"absoluteAngle\",\"wpml:gimbalPitchRotateAngle\":\"-85\",\"wpml:gimbalPitchRotateEnable\":\"1\",\"wpml:gimbalYawRotateEnable\":\"0\",\"wpml:gimbalYawRotateAngle\":\"0\"}},{\"wpml:actionActuatorFunc\":\"takePhoto\",\"wpml:actionId\":\"1\",\"wpml:actionActuatorFuncParam\":{\"wpml:payloadPositionIndex\":\"0\",\"wpml:payloadLensIndex\":\"visable,ir\",\"wpml:useGlobalPayloadLensIndex\":\"0\"}}],\"wpml:actionGroupEndIndex\":\"1\",\"wpml:actionGroupMode\":\"sequence\",\"wpml:actionGroupId\":\"0\",\"wpml:actionTrigger\":{\"wpml:actionTriggerParam\":\"2\",\"wpml:actionTriggerType\":\"multipleTiming\"}},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"0\",\"Point\":{\"coordinates\":\"122.24078475251504,43.64698014415935\"},\"wpml:executeHeight\":\"120\"},{\"wpml:waypointHeadingParam\":{\"wpml:waypointHeadingAngleEnable\":\"0\",\"wpml:waypointHeadingPathMode\":\"followBadArc\",\"wpml:waypointHeadingPoiIndex\":\"0\",\"wpml:waypointHeadingAngle\":\"-90\",\"wpml:waypointPoiPoint\":\"0.000000,0.000000,0.000000\",\"wpml:waypointHeadingMode\":\"followWayline\"},\"wpml:isRisky\":\"0\",\"wpml:waypointTurnParam\":{\"wpml:waypointTurnDampingDist\":\"0\",\"wpml:waypointTurnMode\":\"toPointAndStopWithDiscontinuityCurvature\"},\"wpml:waypointSpeed\":\"4.5\",\"wpml:useStraightLine\":\"1\",\"wpml:waypointGimbalHeadingParam\":{\"wpml:waypointGimbalPitchAngle\":\"0\",\"wpml:waypointGimbalYawAngle\":\"0\"},\"wpml:index\":\"1\",\"Point\":{\"coordinates\":\"122.23999249636039,43.64699029703387\"},\"wpml:executeHeight\":\"120\"}],\"wpml:duration\":\"14.207207439063694\",\"wpml:executeHeightMode\":\"relativeToStartPoint\",\"wpml:autoFlightSpeed\":\"4.5\",\"wpml:distance\":\"63.932433475786624\",\"wpml:templateId\":\"4\"}],\"wpml:missionConfig\":{\"wpml:payloadInfo\":{\"wpml:payloadPositionIndex\":\"0\",\"wpml:payloadEnumValue\":\"99\",\"wpml:payloadSubEnumValue\":\"1\"},\"wpml:flyToWaylineMode\":\"safely\",\"wpml:finishAction\":\"goHome\",\"wpml:executeRCLostAction\":\"goBack\",\"wpml:globalTransitionalSpeed\":\"15\",\"wpml:takeOffSecurityHeight\":\"120\",\"wpml:globalRTHHeight\":\"120\",\"wpml:droneInfo\":{\"wpml:droneSubEnumValue\":\"1\",\"wpml:droneEnumValue\":\"100\"},\"wpml:exitOnRCLost\":\"goContinue\"}},\"_attributes\":{\"xmlns\":\"http://www.opengis.net/kml/2.2\",\"xmlns:wpml\":\"http://www.dji.com/wpmz/1.0.5\"}}},\"template.xml\":{\"_declaration\":{\"_attributes\":{\"encoding\":\"UTF-8\",\"version\":\"1.0\"}},\"kml\":{\"Document\":{\"wpml:author\":\"geoai\",\"wpml:createTime\":{},\"_comment\":[\"geoai 模版文件 1.0\",\"xxx需要替换\"],\"wpml:updateTime\":{},\"Folder\":{\"wpml:payloadParam\":{\"wpml:focusMode\":\"firstPoint\",\"wpml:scanningMode\":\"repetitive\",\"wpml:imageFormat\":\"visable,ir\",\"wpml:returnMode\":\"singleReturnFirst\",\"wpml:payloadPositionIndex\":\"0\",\"wpml:samplingRate\":\"240000\",\"wpml:meteringMode\":\"average\"},\"Placemark\":{\"wpml:caliFlightEnable\":\"0\",\"wpml:inclinedFlightSpeed\":\"15\",\"wpml:inclinedGimbalPitch\":\"-85\",\"wpml:mappingHeadingParam\":{\"wpml:mappingHeadingMode\":\"followWayline\",\"wpml:mappingHeadingAngle\":\"0\"},\"wpml:smartObliqueEnable\":\"0\",\"wpml:efficiencyFlightModeEnable\":\"0\",\"wpml:direction\":\"0\",\"_comment\":\"根据实际调整航线方向 跟 外扩距离\",\"wpml:gimbalPitchAngle\":\"-85\",\"wpml:ellipsoidHeight\":\"75\",\"wpml:margin\":\"0\",\"wpml:shootType\":\"time\",\"wpml:gimbalPitchMode\":\"fixed\",\"wpml:overlap\":{\"wpml:inclinedCameraOverlapW\":\"50\",\"wpml:inclinedCameraOverlapH\":\"80\",\"wpml:orthoCameraOverlapH\":{},\"wpml:orthoCameraOverlapW\":{}},\"Polygon\":{\"outerBoundaryIs\":{\"LinearRing\":{\"coordinates\":\"122.23981114496125,43.64710514401781,0\\n                122.2406126241607,43.64720769715192,0\\n                122.24068109485603,43.646840690959664,0\\n                122.23996546536515,43.646763651528836,0\"}}},\"wpml:height\":\"120\"},\"wpml:autoFlightSpeed\":\"4.5\",\"wpml:templateId\":\"0\",\"wpml:waylineCoordinateSysParam\":{\"wpml:heightMode\":\"relativeToStartPoint\",\"wpml:globalShootHeight\":\"119.73814384832292\",\"wpml:coordinateMode\":\"WGS84\"},\"wpml:templateType\":\"mapping3d\"},\"wpml:missionConfig\":{\"wpml:takeOffRefPoint\":\"122.24172965344526,43.647585660269364,188.04\",\"wpml:payloadInfo\":{\"wpml:payloadPositionIndex\":{},\"wpml:payloadEnumValue\":\"99\",\"wpml:payloadSubEnumValue\":\"1\"},\"wpml:flyToWaylineMode\":\"safely\",\"_comment\":[\"需要创建航线时，替换参考起飞点信息\",\"根据无人机、相机具体信息\"],\"wpml:finishAction\":\"goHome\",\"wpml:executeRCLostAction\":\"goBack\",\"wpml:globalTransitionalSpeed\":\"15\",\"wpml:takeOffSecurityHeight\":\"120\",\"wpml:globalRTHHeight\":\"120\",\"wpml:takeOffRefPointAGLHeight\":\"155\",\"wpml:droneInfo\":{\"wpml:droneSubEnumValue\":\"1\",\"wpml:droneEnumValue\":\"100\"},\"wpml:exitOnRCLost\":\"goContinue\"}},\"_attributes\":{\"xmlns\":\"http://www.opengis.net/kml/2.2\",\"xmlns:wpml\":\"http://www.dji.com/wpmz/1.0.5\"}}},\"lineInfo\":{\"area\":2499.27,\"planTime\":24.918764481942944,\"routeDistance\":112.13444016874324},\"uosInfo\":{\"baseShootHeight\":0.2,\"takeOffSecurityHeight\":120,\"uavType\":\"100\",\"riskPlan\":[],\"tiltBoundary\":\"122.23981114496125,43.64710514401781,122.2406126241607,43.64720769715192,122.24068109485603,43.646840690959664,122.23996546536515,43.646763651528836,122.23981114496125,43.64710514401781\",\"rthHeight\":120,\"disconnectionStrategy\":\"goContinue\",\"speed\":4.5,\"cameraType\":\"M4TD\",\"tiltSurveyDatumAlt\":0.2,\"finishAction\":\"goHome\",\"riskList\":\"[{\\\"key\\\":\\\"notDetectionRisk\\\",\\\"type\\\":\\\"warning\\\"},{\\\"type\\\":\\\"warning\\\",\\\"key\\\":\\\"airPointAirspaceRisk\\\"},{\\\"type\\\":\\\"warning\\\",\\\"key\\\":\\\"heightPermittedAreaRisk\\\"}]\",\"heightMode\":\"relativeToStartPoint\",\"taskName\":\"倾斜摄影KKK\",\"multiList\":[{\"fileName\":\"file.kmz\",\"takeOffLatitude\":43.64718520163566,\"kmzPath\":\"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/7154a91e6fbc6ceb920931db038c9a0c/89de64414bba4560989790487f1de187.kmz?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1748397726&Signature=R1mjzy%2FITbfZ25J3DgBKOVoFeu0%3D\",\"returnMiles\":232.89122073950216,\"tiltPhotoCount\":\"25,25,14,25,14\",\"landLongitude\":122.23999249636039,\"predictMiles\":464.2681894278169,\"landAltitude\":120,\"takeOffAltitude\":120,\"highestPoint\":120,\"photoCount\":101,\"takeOffLongitude\":122.24043681436022,\"tiltPointCount\":\"4,4,2,4,2\",\"pointCount\":16,\"tiltPredictFlyTime\":\"24.918764481942944,24.918764482655693,14.207207876131813,24.918764481942944,14.207207439063694\",\"predictFlyTime\":143.7803705479771,\"tiltGsd\":0.74,\"taskMiles\":971.5560544544608,\"tiltPredictMiles\":\"112.13444016874324,112.13444017195062,63.93243544259316,112.13444016874324,63.932433475786624\",\"landLatitude\":43.64699029703387,\"tiltSpace\":2499.27,\"wholeKmz\":false},{\"fileName\":\"file.kmz\",\"kmzPath\":\"https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/7154a91e6fbc6ceb920931db038c9a0c/89de64414bba4560989790487f1de187.kmz?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1748397726&Signature=R1mjzy%2FITbfZ25J3DgBKOVoFeu0%3D\",\"predictFlyTime\":103.17070876173709,\"wholeKmz\":true}],\"tiltDeltaTime\":25,\"globalShootHeight\":119.73814384832292},\"actionPoList\":\"\"}";
        List<Point> list = getPoints(a,new ArrayList<>());

        System.out.println(JSON.toJSONString(list));

    }*/

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> getAllUser(){
        return  statisticsDAO.getAllUser();
    }

    /**
     * 解析kmz文件中的航点位置数据
     * @param waypointCoordinates
     * @param listPoint
     * @return
     */
    public List<Point> getPoints(String waypointCoordinates,List<Point> listPoint) {
        if (!StringUtils.isEmpty(waypointCoordinates)) {
            JSONObject json = JSONObject.parseObject(waypointCoordinates);
            if (json != null) {
                JSONObject wpml = json.getJSONObject(Constants.WAYLINEWPML);
                if (wpml != null) {
                    JSONObject kml = wpml.getJSONObject(Constants.KML);
                    if (kml != null) {
                        JSONObject document = kml.getJSONObject(Constants.DOCUMENT);
                        if (document != null) {
                            String folder = document.getString(Constants.FOLDER);
                            JSONArray arrayFolder = new JSONArray();
                            if (!StringUtils.isEmpty(folder) && folder.substring(0, 1).equals("[")) {
                                arrayFolder = document.getJSONArray(Constants.FOLDER);
                            } else {
                                arrayFolder.add(document.getJSONObject(Constants.FOLDER));
                            }
                            for (int i = 0; i < arrayFolder.toArray().length; i++) {
                                JSONObject folderObject = arrayFolder.getJSONObject(i);
                                String mark = folderObject.getString(Constants.PLACEMARK);
                                JSONArray arrayMark = new JSONArray();
                                if (!StringUtils.isEmpty(mark) && mark.substring(0, 1).equals("[")) {
                                    arrayMark = folderObject.getJSONArray(Constants.PLACEMARK);
                                } else {
                                    arrayMark.add(folderObject.getJSONObject(Constants.PLACEMARK));
                                }
                                for (int j = 0; j < arrayMark.toArray().length; j++) {
                                    Point pointDto = new Point();
                                    JSONObject jsonObject = arrayMark.getJSONObject(j);
                                    JSONObject point = jsonObject.getJSONObject(Constants.POINT);
                                    String coordinates = point.getString(Constants.COORDINATES);
                                    String executeHeight = jsonObject.getString(Constants.WPMLHEIGHT);
                                    pointDto.setAltitude(executeHeight);
                                    if (!StringUtils.isEmpty(coordinates) && coordinates.contains(Constants.COMMA)) {
                                        String[] str = coordinates.split(Constants.COMMA);
                                        pointDto.setLongitude(str[0]);
                                        pointDto.setLatitude(str[1]);
                                    }
                                    listPoint.add(pointDto);
                                }

                            }
                        }
                    }
                }
            }
        }
        return listPoint;
    }


    public boolean insertUavFlightTask(DateVO dateVO, List<UavFlightTaskVO> list,String param){
        try {
                log.info("insertUavFlightTask入参-list:{},dateVO:{},param:{}", JSONObject.toJSONString(list),JSONObject.toJSONString(dateVO), param);
            //删除当天的数据
            this.deleteUavFlightTask(dateVO);
            if(!CollectionUtils.isEmpty(list)){
                //根据 handleDate taskId集合 所在的月份查询月告警数据
                String handleDate = dateVO.getHandleDate();
                List<String> taskIdList = list.stream().map(UavFlightTaskVO::getTaskRecordId).collect(Collectors.toList());
                String tableName = TableUtil.getTableNameByString(handleDate.substring(0,7).replace("-", ""), Constants.WARN_ORDER_TEBLE);
                Map<String, List<StatisticalAnalysisWarningOrderVO>> warningOrderMap = Collections.emptyMap();
                if (!taskIdList.isEmpty()) {
                    log.info("查询告警月份表：{}", tableName);
                    List<StatisticalAnalysisWarningOrderVO> monthAlarmData = statisticsDAO.queryMonthAlarmData(tableName, taskIdList);
                    //根据任务id统计相关告警数量
                    warningOrderMap = monthAlarmData.stream()
                            .collect(Collectors.groupingBy(StatisticalAnalysisWarningOrderVO::getTaskRecordId));
                }

                //查询所有用户
                List<SysUser> userList  = this.getAllUser();
                for (UavFlightTaskVO taskVo:list) {

                    //封装告警数据
                    if (!warningOrderMap.isEmpty() && warningOrderMap.containsKey(taskVo.getTaskRecordId())){
                        List<StatisticalAnalysisWarningOrderVO>  warningOrderList = warningOrderMap.get(taskVo.getTaskRecordId());
                        taskVo.setAlgorithmInfoMap(JSON.toJSONString(warningOrderList));
                    }

                    String waypointCoordinates = taskVo.getWaypointCoordinates();
                    try{
                        List<Point> listPoint = new ArrayList<>();
                        listPoint = this.getPoints(waypointCoordinates,listPoint);
                        if(CollectionUtils.isEmpty(listPoint)){
                            taskVo.setWaypointCoordinates("");
                        }else{
                            taskVo.setWaypointCoordinates(JSON.toJSONString(listPoint));
                        }

                    }catch (Exception e){
                        log.error("获取航点点位经纬度异常:"+e);
                    }
                    for (SysUser user:userList) {
                        if(!StringUtils.isEmpty(taskVo.getTaskCreator()) && taskVo.getTaskCreator().equals(user.getUserName())){
                            taskVo.setTaskCreatorId(user.getUserId());
                        }
                    }
                }
            }
            //插入当前的数据
            List<List<UavFlightTaskVO>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    mongoTemplate.insert(item, Constants.UAV_FLIGHT_TASKS)
            );
            // 插入新的状态记录
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Constants.UAV_FLIGHT_TASKS_STATISTICS_STATUS);
            Query updateQuery = new Query();
            //无参数 当天数据插入记录
            if (StringUtils.isEmpty(param)) {
                updateQuery.addCriteria(Criteria.where("taskTime").is(dateVO.getHandleDate()));
            }else{
                updateQuery.addCriteria(Criteria.where("taskTime").is(param));
            }
            Update update = new Update();
            update.set("finishFlag",1);
            bulkOperations.upsert(updateQuery, update);
            bulkOperations.execute();

        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            return false;
        }
        return true;
    }

    /**
     * 删除无人机飞行任务
     * 根据传入的日期参数，按天或按月删除对应的无人机飞行任务记录
     *
     * @param dateVO 日期参数对象，包含处理日期和天标识
     *               - 如果day不为空且非空字符串，则按天删除
     *               - 否则按月删除
     */
    public void deleteUavFlightTask(DateVO dateVO){
        Query query;
        if (dateVO.getDay() != null && !dateVO.getDay().isEmpty()) {
            // 按天删除：匹配 YYYY-MM-DD
            String targetDateStr = dateVO.getHandleDate(); // 假设格式为 "yyyy-MM-dd"
            // 构造日期范围字符串查询条件
            String startOfDay = targetDateStr + " 00:00:00";
            String endOfDay = targetDateStr + " 23:59:59";

            query = Query.query(Criteria.where(Constants.PLAN_EXECUTE_TIME)
                    .gte(startOfDay)
                    .lte(endOfDay));
        } else {
            // 按月删除：匹配 YYYY-MM
            String handleDate = dateVO.getHandleDate(); // 假设格式为 "yyyy-MM"
            // 构造月份范围字符串查询条件
            String startOfMonth = handleDate + "-01 00:00:00";
            // 计算月末日期
            Date monthStart = DateUtil.parse(handleDate, "yyyy-MM");
            Date monthEnd = DateUtil.endOfMonth(monthStart);
            String endOfMonth = DateUtil.format(monthEnd, "yyyy-MM-dd") + " 23:59:59";

            query = Query.query(Criteria.where(Constants.PLAN_EXECUTE_TIME)
                    .gte(startOfMonth)
                    .lte(endOfMonth));
        }
        mongoTemplate.remove(query, Constants.UAV_FLIGHT_TASKS);
    }

    /**
     * 告警发生区域统计表-租户视角
     * @param param 例："2022-09,2022-08-01,2022-07,2022-06" （yyyy-MM/yyyy-MM-dd）
     *              多个日期逗号间隔，支持执行多个月份、多天，或者一天、一月，也可以不传，不传就代表实时执行
     */
    @XxlJob("eventAreaForTenantHandler")
    public ReturnT<String> eventAreaForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警发生区域统计表-租户视角-"+dates[i]);
                    XxlJobLogger.log("告警发生区域统计表-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventAreaListByTenant(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventAreaByTenantParam(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警发生区域统计表-租户视角");
            XxlJobLogger.log("告警发生区域统计表-租户视角");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventAreaListByTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventAreaByTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventAreaListByTenant(DateVO dateVO){
        return statisticsDAO.getEventAreaListByTenant(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAreaByTenant(DateVO dateVO, List<Map> list){
        try {
            //删除当天的数据
            statisticsDAO.deleteEventAreaByTenant(dateVO);
            //插入当前的数据
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventAreaByTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAreaByTenantParam(DateVO dateVO, List<Map> list){
        try {
            //删除当天的数据
            statisticsDAO.deleteEventAreaByTenant(dateVO);
            //插入当前的数据
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventAreaByTenantParam(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 工作台-告警等级统计表-租户视角
     */
    @XxlJob("eventLevelForTenantHandler")
    public ReturnT<String> eventLevelForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警等级统计表-租户视角-"+dates[i]);
                    XxlJobLogger.log("告警等级统计表-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventLevelListByTenant(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventLevelByTenant(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警等级统计表-租户视角");
            XxlJobLogger.log("告警等级统计表-租户视角");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventLevelListByTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventLevelByTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventLevelListByTenant(DateVO dateVO){
        return statisticsDAO.getEventLevelListByTenant(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventLevelByTenant(DateVO dateVO, List<Map> list){
        try {
            //删除当天的数据
            statisticsDAO.deleteEventLevelByTenant(dateVO);
            //新增当天的数据
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventLevelByTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 工作台事件数量统计表-租户用
     */
    @XxlJob("eventNumForTenantHandler")
    public ReturnT<String> eventNumForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("更新工作台事件数量统计表-租户用-"+dates[i]);
                    XxlJobLogger.log("更新工作台事件数量统计表-租户用-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    //将两个list中，相同租户、区县、设备的数据数量相加
                    List<DurationVO> durationRes = getEventNumForTenants(dateVO);
                    if (!CollectionUtils.isEmpty(durationRes)) {
                        DataUtil.returnString(self.insertEventNumByTenants(dateVO, durationRes));
                    }
                }
            }
        } else{
            log.info("更新工作台事件数量统计表-租户用 getEventNumForTenant");
            //根据当前时间，获取告警事件分月表名
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            dateVO.setStartDate(DateUtil.format(DateUtil.beginOfDay(DateUtil.yesterday()),"yyyy-MM-dd HH:mm:ss"));
            dateVO.setEndDate(DateUtil.format(DateUtil.date(),"yyyy-MM-dd HH:mm:ss"));
            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = getEventNumForTenant(dateVO);

            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.insertEventNumByTenant(dateVO,durationRes));
            }
        }

        return ReturnT.SUCCESS;
    }

    private List<DurationVO> getEventNumForTenant(DateVO dateVO) {
        //对全量统计字段进行查询、累加、更新操作
        //获取近六个月
        String dateEnd = DateUtils.dateTimeNow(TableUtil.getMinTableDate()); //日期转化为字符串，只有年月
        //获取当前月的表名
        String dateStr = DateUtils.dateTimeNow(DateUtils.DATE_TIME_YM_FORMAT); //日期转化为字符串，只有年月
        boolean dateFlag = true;
        List<DurationVO> resList = new ArrayList<>();

        //今日告警数量统计
        List<DurationVO> todayNumList = statisticsDAO.getEventTodayNumByTenant(dateVO);
        resList.addAll(todayNumList);

        while (dateFlag) {
            dateVO.setTableName(TableUtil.getTableName(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT,dateStr),Constants.WARN_ORDER_TEBLE));
            List<DurationVO> eventNumList = statisticsDAO.getEventNumByTenant(dateVO);
            resList.addAll(eventNumList);
            if (dateEnd.equals(dateStr)) {
                dateFlag = false;
            } else {
                dateStr = DateUtils.getlastMonth(dateStr);
            }
        }

        //将两个list中，相同租户、区县、设备的数据数量相加
        List<DurationVO> durationRes = new ArrayList<>();
        resList.stream().collect(Collectors.groupingBy(item ->
                (item.getTenantId() + item.getProvinceId() + item.getCityId() + item.getCountyId()+item.getTownId() + item.getDeviceCode()) + item.getAppVerCode() + item.getIndustryCode() + item.getWarningSource(), Collectors.toList())).forEach(
                (id, transfer) -> {
                    transfer.stream().reduce((a, b) ->
                            new DurationVO(a.getHandleDateTime(),a.getTenantId(), a.getProvinceId(), a.getCityId(), a.getCountyId(),a.getTownId(), a.getDeviceCode(), a.getAppVerCode(), a.getWarningSource(),
                                    a.getYesterdayAll() + b.getYesterdayAll(),
                                    a.getTodayAll() + b.getTodayAll(),
                                    a.getFalseAll() + b.getFalseAll(),
                                    a.getTodayProcess() + b.getTodayProcess(),
                                    a.getTodayComplete() + b.getTodayComplete(),
                                    a.getPenddingReceive() + b.getPenddingReceive(),
                                    a.getPenddingJudged() + b.getPenddingJudged(),
                                    a.getPendingDispatched() + b.getPendingDispatched(),
                                    a.getPeddingDisposal() + b.getPeddingDisposal(),
                                    a.getPendingApprove() + b.getPendingApprove(),
                                    a.getOvertime() + b.getOvertime(), a.getIndustryCode())).ifPresent(durationRes::add);
                });
        return durationRes;
    }

    private List<DurationVO> getEventNumForTenants(DateVO dateVO) {
        List<DurationVO> resList = new ArrayList<>();

        //今日告警数量统计
        List<DurationVO> todayNumList = statisticsDAO.getEventTodayNumByTenants(dateVO);
        resList.addAll(todayNumList);
            return resList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByTenant(DateVO dateVO,List<DurationVO> list){
        try {
            statisticsDAO.deleteEventNumByTenant(dateVO);
            List<List<DurationVO>> partitionList = Lists.partition(list, 1000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventNumByTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByTenants(DateVO dateVO,List<DurationVO> list){
        try {
            statisticsDAO.deleteEventNumByTenants(dateVO);
            List<List<DurationVO>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventNumByTenants(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀数据
     */
    @XxlJob("eventByHourForTenantHandler")
    public ReturnT<String> eventByHourForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.EVENT_DAY_TENANT+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_DAY_TENANT+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumListByHourNew(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumByHour(dateVO, list));
                    }
                }
            }
        }else {
            log.info(Constants.EVENT_DAY_TENANT);
            XxlJobLogger.log(Constants.EVENT_DAY_TENANT);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumListByHour(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumByHour(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumListByHour(DateVO dateVO){
        return statisticsDAO.getEventNumListByHour(dateVO);
    }

    private List<Map> getEventNumListByHourNew(DateVO dateVO){
        return statisticsDAO.getEventNumListByHourNew(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByHour(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteEventNumByHour(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                statisticsDAO.insertEventNumByHour(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀数据
     */
    @XxlJob("eventByHourForTenantHandlerNew")
    public ReturnT<String> eventByHourForTenantHandlerNew(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.EVENT_DAY_TENANT+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_DAY_TENANT+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumListByHourNewNews(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumByHourNews(dateVO, list));
                    }
                }
            }
        }else {
            log.info(Constants.EVENT_INTERCEPT_HOUR_TENANT);
            XxlJobLogger.log(Constants.EVENT_DAY_TENANT);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumListByHourNews(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumByHourNews(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumListByHourNews(DateVO dateVO){
        return statisticsDAO.getEventNumListByHourNews(dateVO);
    }

    private List<Map> getEventNumListByHourNewNews(DateVO dateVO){
        return statisticsDAO.getEventNumListByHourNewNews(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByHourNews(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteEventNumByHourNews(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventNumByHourNews(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀压制数据
     */
    @XxlJob("eventByInterceptHourForTenantHandler")
    public ReturnT<String> eventByInterceptHourForTenantHandler(String param) {
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(com.chinatower.event.constants.Constants.TASK_SELECT_INTERCEPT_POWER_FLAG);
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info(Constants.EVENT_INTERCEPT_HOUR_TENANT+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TENANT+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getEventNumListByInterceptHour(dateVO,suppressDictData);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumByInterceptHour(dateVO, list));
                    }
                }
            }
        }else {
            log.info(Constants.EVENT_INTERCEPT_HOUR_TENANT);
            XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TENANT);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getEventNumListByInterceptHour(dateVO,suppressDictData);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumByInterceptHour(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumListByInterceptHour(DateVO dateVO, Result<List<SysDictDataResponse>> suppressDictData){
        return interceptService.getEventNumListByInterceptHour(dateVO,suppressDictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByInterceptHour(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteEventNumByInterceptHour(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventNumByInterceptHour(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀压制数据
     */
    @XxlJob("eventByInterceptHourForTenantHandlerNew")
    public ReturnT<String> eventByInterceptHourForTenantHandlerNew(String param) {
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(com.chinatower.event.constants.Constants.TASK_SELECT_INTERCEPT_POWER_FLAG);
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info(Constants.EVENT_INTERCEPT_HOUR_TENANT+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TENANT+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getEventNumListByInterceptHourNew(dateVO,suppressDictData);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumByInterceptHourNew(dateVO, list));
                    }
                }
            }
        }else {
            log.info(Constants.EVENT_INTERCEPT_HOUR_TENANT);
            XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TENANT);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getEventNumListByInterceptHourNew(dateVO,suppressDictData);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumByInterceptHourNew(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumListByInterceptHourNew(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData){
        return interceptService.getEventNumListByInterceptHourNew(dateVO,suppressDictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByInterceptHourNew(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteEventNumByInterceptHourNew(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventNumByInterceptHourNew(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  办结事件处理时长统计(租户)
     * @param param 例："2022-09,2022-08-01,2022-07,2022-06" （yyyy-MM/yyyy-MM-dd）
     *              多个日期逗号间隔，支持执行多个月份、多天，或者一天、一月，也可以不传，不传就代表实时执行
     */
    @XxlJob("eventDurationForTenantHandler")
    public ReturnT<String> eventDurationForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("------办结事件处理时长统计(租户)沉淀数据定时-------"+dates[i]);
                    XxlJobLogger.log("办结事件处理时长统计(租户)沉淀数据定时-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);

                    //将两个list中，相同租户、区县、设备的数据数量相加
                    List<DurationVO> durationRes = getEventDurationForTenant(dateVO);

                    if (!CollectionUtils.isEmpty(durationRes)) {
                        DataUtil.returnString(self.addEventDurationTenantParam(durationRes, dateVO));
                    }
                }
            }
        } else {
            log.info("------办结事件处理时长统计(租户)沉淀数据定时------");
            XxlJobLogger.log("办结事件处理时长统计(租户)沉淀数据定时");
            //根据当前时间，获取告警事件分月表名
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));

            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = getEventDurationForTenant(dateVO);

            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.addEventDurationTenant(durationRes, dateVO));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<DurationVO> getEventDurationForTenant(DateVO dateVO) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String lastMonth = String.valueOf(year)+String.valueOf(month);
        List<DurationVO> durationList = new ArrayList<>();
        // 循环近六个月的表名，查询近六个月表中今日完结事件数量
        for (int i = 0;i<5;i++) {
            List<DurationVO> voList = new ArrayList<>();
            dateVO.setTableName(TableUtil.getTableName(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT, lastMonth),
                    Constants.WARN_ORDER_TEBLE));
            voList = statisticsDAO.getEventDurationTenant(dateVO);
            durationList.addAll(voList);
            lastMonth = DateUtils.getlastMonth(lastMonth);

        }


        // 将两个list中，相同租户、区县、设备的数据数量相加
        List<DurationVO> durationRes = new ArrayList<>();
        durationList.stream()
                .collect(Collectors.groupingBy(
                        item -> (item.getTenantId() + item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getDeviceCode() + item.getWarningSource() + item.getIndustryCode() + item.getYearStr()+item.getMonthStr()+item.getDayStr()+item.getAlarmTime()),
                        Collectors.toList()))
                .forEach((id, transfer) -> {
                    transfer.stream()
                            .reduce((a, b) -> new DurationVO(a.getTenantId(), a.getProvinceId(), a.getCityId(), a.getCountyId(), a.getDeviceCode(), a.getWarningSource(),
                                    a.getLessOne() + b.getLessOne(), a.getOneFive() + b.getOneFive(),
                                    a.getFiveTen() + b.getFiveTen(), a.getMoreTen() + b.getMoreTen(), a.getIndustryCode(), a.getModifierDate(),a.getAlarmTime(),a.getYearStr(),a.getMonthStr(),a.getDayStr()))
                            .ifPresent(durationRes::add);
                });
        return durationList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventDurationTenant(List<DurationVO> list, DateVO dateVO){
        try {
            statisticsDAO.delEventDurationTenant(dateVO);
            statisticsDAO.addEventDurationTenant(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean addEventDurationTenantParam(List<DurationVO> list, DateVO dateVO){
        try {
            statisticsDAO.delEventDurationTenant(dateVO);
            statisticsDAO.addEventDurationTenantParam(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  告警每日维度统计
     */
    @XxlJob("eventNumByDayForTenantHandler")
    public ReturnT<String> eventNumByDayForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.ALARM_DAILY_STATISTICS_MSG+dates[i]);
                    XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MSG+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumDayListForTenants(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumDayTenants(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.ALARM_DAILY_STATISTICS_MSG);
            XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MSG);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumDayListForTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumDayTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumDayListForTenant(DateVO dateVO){
        List<Map> eventNumDays = statisticsDAO.getEventNumDayListForTenant(dateVO);
        //租户名称
        List<Map<String,String>> tenants = towerStatisticsDAO.getTenants();
        //网格等级
        List<Map<String, String>> grids = towerStatisticsDAO.getGrids();
        Map<String, String> tenantMap = tenants.stream().collect( Collectors.toMap( t -> t.get(Constants.PARAM_TENANTID), t -> t.get(Constants.PARAM_TENANT_NAME), (o, n) -> n, HashMap::new));
        Map<String, String> gridMap = grids.stream().collect(
                Collectors.toMap(
                        t -> String.valueOf(t.get(Constants.GRID_ID)),
                        t -> String.valueOf(t.get(Constants.GRID_LEVEL)),
                        (o, n) -> n,
                        HashMap::new
                )
        );
        for (Map map : eventNumDays) {
            map.put(Constants.PARAM_TENANT_NAME, MapUtil.getStr(tenantMap,map.get(Constants.PARAM_TENANT_ID)));
            map.put(Constants.GRID_LEVEL, MapUtil.getStr(gridMap,map.get("grid_id")));
        }
        return eventNumDays;
    }

    private List<Map> getEventNumDayListForTenants(DateVO dateVO){
        List<Map> eventNumDays = statisticsDAO.getEventNumDayListForTenants(dateVO);
        //租户名称
        List<Map<String,String>> tenants = towerStatisticsDAO.getTenants();
        //网格等级
        List<Map<String, String>> grids = towerStatisticsDAO.getGrids();
        Map<String, String> tenantMap = tenants.stream().collect( Collectors.toMap( t -> t.get(Constants.PARAM_TENANTID), t -> t.get(Constants.PARAM_TENANT_NAME), (o, n) -> n, HashMap::new));
        Map<String, String> gridMap = grids.stream().collect(
                Collectors.toMap(
                        t -> String.valueOf(t.get(Constants.GRID_ID)),
                        t -> String.valueOf(t.get(Constants.GRID_LEVEL)),
                        (o, n) -> n,
                        HashMap::new
                )
        );
        for (Map map : eventNumDays) {
            map.put(Constants.PARAM_TENANT_NAME, MapUtil.getStr(tenantMap,map.get(Constants.PARAM_TENANT_ID)));
            map.put(Constants.GRID_LEVEL, MapUtil.getStr(gridMap,map.get("grid_id")));
        }
        return eventNumDays;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumDayTenant(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumDayTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumDayTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumDayTenants(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumDayTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumDayTenants(item)
            );

        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  告警每日维度统计-人工上报
     */
    @XxlJob("eventNumByDayForTenantHandlerManual")
    public ReturnT<String> eventNumByDayForTenantHandlerManual(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.ALARM_DAILY_STATISTICS_MANUAL_MSG+dates[i]);
                    XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MANUAL_MSG+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumDayListForTenantsManual(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumDayTenantsManual(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.ALARM_DAILY_STATISTICS_MANUAL_MSG);
            XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MANUAL_MSG);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumDayListForTenantManual(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumDayTenantManual(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumDayListForTenantManual(DateVO dateVO){
        List<Map> eventNumDays = statisticsDAO.getEventNumDayListForTenantManual(dateVO);
        //租户名称
        List<Map<String,String>> tenants = towerStatisticsDAO.getTenants();
        Map<String, String> tenantMap = tenants.stream().collect( Collectors.toMap( t -> t.get(Constants.PARAM_TENANTID), t -> t.get(Constants.PARAM_TENANT_NAME), (o, n) -> n, HashMap::new));
        for (Map map : eventNumDays) {
            map.put(Constants.PARAM_TENANT_NAME, MapUtil.getStr(tenantMap,map.get(Constants.PARAM_TENANT_ID)));
        }
        return eventNumDays;
    }

    private List<Map> getEventNumDayListForTenantsManual(DateVO dateVO){
        List<Map> eventNumDays = statisticsDAO.getEventNumDayListForTenantsManual(dateVO);
        //租户名称
        List<Map<String,String>> tenants = towerStatisticsDAO.getTenants();
        Map<String, String> tenantMap = tenants.stream().collect( Collectors.toMap( t -> t.get(Constants.PARAM_TENANTID), t -> t.get(Constants.PARAM_TENANT_NAME), (o, n) -> n, HashMap::new));
        for (Map map : eventNumDays) {
            map.put(Constants.PARAM_TENANT_NAME, MapUtil.getStr(tenantMap,map.get(Constants.PARAM_TENANT_ID)));
        }
        return eventNumDays;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumDayTenantManual(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumDayTenantManual(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumDayTenantManual(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumDayTenantsManual(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumDayTenantManual(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumDayTenantsManual(item)
            );

        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }


    /**
     * 按照告警来源统计数据 (租户视角)
     * @param param
     * @return
     */
    @XxlJob("eventSourceForTenantHandler")
    public ReturnT<String> eventSourceForTenantHandler(String param) {
        List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
        Map<String, String> sourceMap = warningSourceEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel));
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("按照告警来源统计数据-租户视角-"+dates[i]);
                    XxlJobLogger.log("按照告警来源统计数据-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<EventSourceVo> list = getEventSourceListForTenant(dateVO,sourceMap);
                    if(!CollectionUtils.isEmpty(list)){
                        DataUtil.returnString(self.addEventSourceTenantForParams(dateVO,list));
                    }
                }
            }
        } else {
            log.info(sourceStaticTaskTip);
            XxlJobLogger.log(sourceStaticTaskTip);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<EventSourceVo> list = getEventSourceListForTenant(dateVO,sourceMap);
            if(!CollectionUtils.isEmpty(list)){
                return DataUtil.returnString(self.addEventSourceTenant(dateVO,list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<EventSourceVo> getEventSourceListForTenant(DateVO dateVO, Map<String, String> sourceMap){
        List<EventSourceVo> list = statisticsDAO.getEventSourceListForTenant(dateVO);
         for (EventSourceVo eventSourceVo : list) {
            eventSourceVo.setWarningSourceName(MapUtil.getStr(sourceMap,eventSourceVo.getWarningSource()));
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventSourceTenant(DateVO dateVO,List<EventSourceVo> list){
        try{
            statisticsDAO.delEventSourceTenant(dateVO);
            List<List<EventSourceVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventSourceTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventSourceTenantForParams(DateVO dateVO,List<EventSourceVo> list){
        try{
            statisticsDAO.delEventSourceTenant(dateVO);
            List<List<EventSourceVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventSourceTenantForParams(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照告警来源统计数据 (铁塔视角)
     * @param param
     * @return
     */
    @XxlJob("eventSourceForTowerHandler")
    public ReturnT<String> eventSourceForTowerHandler(String param) {
        List<SysDictDataResponse> warningSourceEnum = systemFeignClient.dictDataType(Constants.WARNING_SOURCE_COMMON).getData();
        Map<String, String> sourceMap = warningSourceEnum.stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel));
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("按照告警来源统计数据-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("按照告警来源统计数据-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<EventSourceVo> list = getEventSourceListForTower(dateVO,sourceMap);
                    if(!CollectionUtils.isEmpty(list)){
                        DataUtil.returnString(self.addEventSourceTowerForParams(dateVO,list));
                    }
                }
            }
        } else {
            log.info(sourceStaticTaskTip);
            XxlJobLogger.log(sourceStaticTaskTip);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<EventSourceVo> list = getEventSourceListForTower(dateVO,sourceMap);
            if(!CollectionUtils.isEmpty(list)){
                return DataUtil.returnString(self.addEventSourceTower(dateVO,list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<EventSourceVo> getEventSourceListForTower(DateVO dateVO, Map<String, String> sourceMap){
        List<EventSourceVo> list = statisticsDAO.getEventSourceListForTower(dateVO);
          for (EventSourceVo eventSourceVo : list) {
            eventSourceVo.setWarningSourceName(MapUtil.getStr(sourceMap,eventSourceVo.getWarningSource()));
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventSourceTower(DateVO dateVO,List<EventSourceVo> list){
        try{
            statisticsDAO.delEventSourceTower(dateVO);
            List<List<EventSourceVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventSourceTower(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventSourceTowerForParams(DateVO dateVO,List<EventSourceVo> list){
        try{
            statisticsDAO.delEventSourceTower(dateVO);
            List<List<EventSourceVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventSourceTowerForParams(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @XxlJob("eventNumByTagForTenantHandler")
    public ReturnT<String> eventNumByTagForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警标签维度统计沉淀数据-租户视角-"+dates[i]);
                    XxlJobLogger.log("告警标签维度统计沉淀数据-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<WarningOrderTagVo> list = getEventNumTagListForTenant(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumTagTenant(dateVO, list));
                    }
                }
            }
        } else {
            log.info("------告警标签维度统计(租户)沉淀数据定时------");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            //根据当前时间，获取告警事件分月表名
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<WarningOrderTagVo> list = getEventNumTagListForTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumTagTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }
    private List<WarningOrderTagVo> getEventNumTagListForTenant(DateVO dateVO){
        List<Map> tagIdList = statisticsDAO.queryTagIdTT(dateVO);
        if (CollectionUtils.isEmpty(tagIdList)){
            return new ArrayList<>();
        }
        List<String> orderIdList = tagIdList.stream().map(v -> String.valueOf(v.get(Constants.PARAM_ORDER_ID))).collect(Collectors.toList());
        dateVO.setOrderIdList(orderIdList);
        List<WarningOrderTagVo> list = statisticsDAO.getEventNumTagListForTenant(dateVO);
        List<WarningOrderTagVo> listResult = new ArrayList<>();
        Map<String, List<Map<String,String>>> groupedByAlarmType = tagIdList.stream()
                .collect(Collectors.groupingBy(
                        map -> String.valueOf(map.get(Constants.PARAM_ORDER_ID)),
                        Collectors.mapping(
                                map -> {
                                    Map<String, String> tagMap = new HashMap<>();
                                    tagMap.put(Constants.TAG_ID, String.valueOf(map.get(Constants.TAG_ID)));
                                    tagMap.put(Constants.TAG_NAME, String.valueOf(map.get(Constants.TAG_NAME)));
                                    return tagMap;
                                },
                                Collectors.toList()
                        )
                ));
        for (WarningOrderTagVo x:list){
            List<Map<String,String>> tagList = groupedByAlarmType.get(x.getWarningOrderId());
            if (tagList!=null && !tagList.isEmpty()){
                for (Map<String,String> map:tagList){
                    WarningOrderTagVo warningOrderTagVo = new  WarningOrderTagVo();
                    BeanUtils.copyProperties(x, warningOrderTagVo);
                    warningOrderTagVo.setTagId(map.get(Constants.TAG_ID));
                    warningOrderTagVo.setTagName(map.get(Constants.TAG_NAME));
                    listResult.add(warningOrderTagVo);
                }
            }
        }
        List<WarningOrderTagVo> durationRes = new ArrayList<>();
        listResult.stream().collect(Collectors.groupingBy(item ->
                        (item.getTenantId() + item.getCountyCode() + item.getCityCode() + item.getProvinceCode()
                                + item.getAppVerCode() + item.getTagId() + item.getDeviceCode() + item.getWarningSource() + item.getGridId() + item.getIndustryCode() + DataUtil.dateToStringYMD(DateUtils.parseDate(item.getAlarmTime()))),
                Collectors.toList())).forEach(
                (id,transfer) -> {transfer.stream().reduce((a,b) ->
                        new WarningOrderTagVo(
                                a.getTenantId(),
                                a.getCountyCode(),
                                a.getAppVerCode(),
                                a.getProvinceCode(),
                                a.getCityCode(),
                                a.getDeviceCode(),
                                a.getWarningSource(),
                                a.getYearStr(),
                                a.getMonthStr(),
                                a.getDayStr(),
                                a.getAlarmTime(),
                                a.getHandleDateTime(),
                                a.getTagId(),
                                a.getNum()+b.getNum(),
                                a.getAiNum()+b.getAiNum(),
                                a.getJudgeNum()+b.getJudgeNum(),
                                a.getRealNum()+b.getRealNum(),
                                a.getFalseNum()+b.getFalseNum(),
                                a.getRepeatCompleteNum()+b.getRepeatCompleteNum(),
                                a.getOrderCompleteRealNum()+b.getOrderCompleteRealNum(),
                                a.getIndustryCode(),a.getGridId(),a.getTagName())).ifPresent(durationRes::add);
                });
        return durationRes;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumTagTenant(DateVO dateVO,List<WarningOrderTagVo> list){
        try {
            statisticsDAO.delEventTagTenant(dateVO);
            List<List<WarningOrderTagVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventTagTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @XxlJob("eventNumByTagForTenantHandlerTower")
    public ReturnT<String> eventNumByTagForTenantHandlerTower(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警标签维度统计沉淀数据-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警标签维度统计沉淀数据-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<WarningOrderTagVo> list = getEventNumTagListForTenantTower(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumTagTenantTower(dateVO, list));
                    }
                }
            }
        } else {
            log.info("------告警标签维度统计(租户)沉淀数据定时------");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            //根据当前时间，获取告警事件分月表名
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<WarningOrderTagVo> list = getEventNumTagListForTenantTower(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumTagTenantTower(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }
    private List<WarningOrderTagVo> getEventNumTagListForTenantTower(DateVO dateVO){
        List<Map> tagIdList = statisticsDAO.queryTagIdTT(dateVO);
        if (CollectionUtils.isEmpty(tagIdList)){
            return new ArrayList<>();
        }
        List<String> orderIdList = tagIdList.stream().map(v -> String.valueOf(v.get(Constants.PARAM_ORDER_ID))).collect(Collectors.toList());
        dateVO.setOrderIdList(orderIdList);
        List<WarningOrderTagVo> list = statisticsDAO.getEventNumTagListForTenantTower(dateVO);
        List<WarningOrderTagVo> listResult = new ArrayList<>();
        if (CollectionUtils.isEmpty(tagIdList)){
            return new ArrayList<>();
        }
        Map<String, List<Map<String,String>>> groupedByAlarmType = tagIdList.stream()
                .collect(Collectors.groupingBy(
                        map -> String.valueOf(map.get(Constants.PARAM_ORDER_ID)),
                        Collectors.mapping(
                                map -> {
                                    Map<String, String> tagMap = new HashMap<>();
                                    tagMap.put(Constants.TAG_ID, String.valueOf(map.get(Constants.TAG_ID)));
                                    tagMap.put(Constants.TAG_NAME, String.valueOf(map.get(Constants.TAG_NAME)));
                                    return tagMap;
                                },
                                Collectors.toList()
                        )
                ));
        for (WarningOrderTagVo x:list){
            List<Map<String,String>> tagList = groupedByAlarmType.get(x.getWarningOrderId());
            if (tagList!=null && !tagList.isEmpty()){
                for (Map<String,String> map:tagList){
                    WarningOrderTagVo warningOrderTagVo = new  WarningOrderTagVo();
                    BeanUtils.copyProperties(x, warningOrderTagVo);
                    warningOrderTagVo.setTagId(map.get(Constants.TAG_ID));
                    warningOrderTagVo.setTagName(map.get(Constants.TAG_NAME));
                    listResult.add(warningOrderTagVo);
                }
            }
        }
        List<WarningOrderTagVo> durationRes = new ArrayList<>();
        listResult.stream().collect(Collectors.groupingBy(item ->
                        (item.getCountyCode() + item.getCityCode() + item.getProvinceCode() + item.getTagId() + DataUtil.dateToStringYMD(DateUtils.parseDate(item.getAlarmTime()))),
                Collectors.toList())).forEach(
                (id,transfer) -> {transfer.stream().reduce((a,b) ->
                        new WarningOrderTagVo(
                                a.getCountyCode(),
                                a.getProvinceCode(),
                                a.getCityCode(),
                                a.getYearStr(),
                                a.getMonthStr(),
                                a.getDayStr(),
                                a.getAlarmTime(),
                                a.getHandleDateTime(),
                                a.getTagId(),
                                a.getNum()+b.getNum(),
                                a.getAiNum()+b.getAiNum(),
                                a.getJudgeNum()+b.getJudgeNum(),
                                a.getRealNum()+b.getRealNum(),a.getFalseNum()+b.getFalseNum(),a.getRepeatCompleteNum()+b.getRepeatCompleteNum(),a.getTagName())).ifPresent(durationRes::add);
                });
        return durationRes;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumTagTenantTower(DateVO dateVO,List<WarningOrderTagVo> list){
        try {
            statisticsDAO.delEventTagTenantTower(dateVO);
            List<List<WarningOrderTagVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventTagTenantTower(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  告警月度维度统计
     */
    @XxlJob("eventNumByMonthForTenantHandler")
    public ReturnT<String> eventNumByMonthForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警月度维度统计(租户)沉淀数据定时-"+dates[i]);
                    XxlJobLogger.log("告警月度维度统计(租户)沉淀数据定时-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVOS(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumMonthListForTenant(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumMonthTenants(dateVO, list));
                    }
                }
            }
        } else {
            log.info("------告警月度维度统计(租户)沉淀数据定时------");
            XxlJobLogger.log("------告警月度维度统计(租户)沉淀数据定时------");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumMonthListForTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumMonthTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumMonthListForTenant(DateVO dateVO){
        return statisticsDAO.getEventNumMonthListForTenant(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumMonthTenant(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumMonthTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumMonthTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumMonthTenants(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumMonthTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumMonthTenants(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  告警年度维度统计(租户)
     */
    @XxlJob("eventNumByYearForTenantHandler")
    public ReturnT<String> eventNumByYearForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            log.info(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            XxlJobLogger.log(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<DurationVO> durationRes = getEventNumByYearForTenants(param);
            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.addEventNumYearTenant(dateVO, durationRes));
            }
        }else{
            log.info(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            XxlJobLogger.log(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<DurationVO> durationRes = getEventNumByYearForTenant(dateVO);
            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.addEventNumYearTenant(dateVO, durationRes));
            }
        }
        return ReturnT.SUCCESS;
    }
    private List<DurationVO> getEventNumByYearForTenants(String param){
        try {
            List<DurationVO> yearList = new ArrayList<>();
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0, 4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警年度维度统计-铁塔视角-" + dates[i]);
                    XxlJobLogger.log("告警年度维度统计-铁塔视角-" + dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVOS(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                    List<DurationVO> eventList = statisticsDAO.getEventNumYearTenant(dateVO);
                    if (!eventList.isEmpty()){
                        yearList.addAll(eventList);
                    }
                    statisticsDAO.delEventNumYearTenant(dateVO);
                }
            }
            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = new ArrayList<>();
            yearList.stream().collect(Collectors.groupingBy(item ->
                    (item.getTenantId() + item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getDeviceCode() + item.getWarningSource() + item.getIndustryCode()+item.getHandleYear()), Collectors.toList())).forEach(
                    (id, transfer) -> {
                        transfer.stream().reduce((a, b) ->
                                new DurationVO(a.getTenantId(), a.getProvinceId(), a.getCityId(), a.getCountyId(),
                                        a.getDeviceCode(), a.getWarningSource(), a.getAppVerCode(),
                                        a.getTodayAll() + b.getTodayAll(), a.getIndustryCode(),a.getHandleYear())).ifPresent(durationRes::add);
                    });
            return durationRes;
        }catch (Exception e){
            return null;
        }
    }

    private List<DurationVO> getEventNumByYearForTenant(DateVO dateVO){
        try {
            //根据当前时间，获取告警事件分月表名
            String dateStr = DateUtils.dateToStringYM(Constants.ym.parse(dateVO.getHandleMonth())); //日期转化为字符串，只有年月
            boolean dateFlag = true;
            List<DurationVO> yearList = new ArrayList<>();
            while (dateFlag) {
                dateVO.setTableName(Constants.WARN_ORDER_TEBLE + dateStr);
                List<DurationVO> eventList = statisticsDAO.getEventNumYearTenant(dateVO);
                yearList.addAll(eventList);
                if (Constants.MIN_TABLE_DATE.equals(dateStr) || Constants.STR_01.equals(dateStr.substring(4))) {
                    dateFlag = false;
                } else {
                    dateStr = DateUtils.getlastMonth(dateStr);
                }
            }

            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = new ArrayList<>();
            yearList.stream().collect(Collectors.groupingBy(item ->
                    (item.getTenantId() + item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getDeviceCode() + item.getWarningSource() + item.getIndustryCode()+item.getHandleYear()), Collectors.toList())).forEach(
                    (id, transfer) -> {
                        transfer.stream().reduce((a, b) ->
                                new DurationVO(a.getTenantId(), a.getProvinceId(), a.getCityId(), a.getCountyId(),
                                        a.getDeviceCode(), a.getWarningSource(), a.getAppVerCode(),
                                        a.getTodayAll() + b.getTodayAll(), a.getIndustryCode(),a.getHandleYear())).ifPresent(durationRes::add);
                    });
            return durationRes;
        }catch (Exception e){
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumYearTenant(DateVO dateVO,List<DurationVO> list){
        try {
            statisticsDAO.delEventNumYearTenant(dateVO);
            Map map = new HashMap<>();
            map.put(Constants.LIST,list);
            map.put(Constants.HANDLEDATETIME,dateVO.getHandleDateTime());
            statisticsDAO.addEventNumYearTenant(map);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  告警类型维度统计(租户)
     */
    @XxlJob("eventTypeForTenantHandler")
    public ReturnT<String> eventTypeForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警类型维度统计(租户)沉淀数据定时-"+dates[i]);
                    XxlJobLogger.log("告警类型维度统计(租户)沉淀数据定时-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventTypeForTenant(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventTypeTenant(dateVO, list));
                    }
                }
            }
        } else {
            log.info("------告警类型维度统计(租户)沉淀数据定时------");
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            //根据当前时间，获取告警事件分月表名
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventTypeForTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventTypeTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventTypeForTenant(DateVO dateVO){
        List<Map> eventTypeForTenant = statisticsDAO.getEventTypeForTenant(dateVO);
        //告警类型
        List<Map<String,String>> alarmTypeList = statisticsDAO.getAlarmType();
        Map<String, String> typeMap = alarmTypeList.stream().collect( Collectors.toMap( t -> t.get("typeId"), t -> t.get("typeName"), (o, n) -> n, HashMap::new));
        for (Map map : eventTypeForTenant) {
            map.put("typeName",MapUtil.getStr(typeMap,map.get("warning_type_id")));
        }

        return eventTypeForTenant;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventTypeTenant(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventTypeTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventTypeTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 方案 A：下游任务维度统计(租户) — 写 vm_statistics_event_sub_scene；与类型汇总 Job 并行、互不改写。
     * 方案1：仅沉淀非空 sub_scenes；查询为空时仍执行删除，避免历史回退脏数据残留。
     */
    @XxlJob("eventSubSceneForTenantHandler")
    public ReturnT<String> eventSubSceneForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length() >= 7) {
                    String year = dates[i].substring(0, 4);
                    String month = dates[i].substring(5, 7);
                    log.info("下游任务维度统计(租户)沉淀数据定时-" + dates[i]);
                    XxlJobLogger.log("下游任务维度统计(租户)沉淀数据定时-" + dates[i]);
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                    List<Map> list = getEventSubSceneForTenant(dateVO);
                    // 即使当日无真实下游，也要 del+写，清掉历史错误回退数据
                    DataUtil.returnString(self.addEventSubSceneTenant(dateVO,
                            list == null ? Collections.emptyList() : list));
                }
            }
        } else {
            log.info("------下游任务维度统计(租户)沉淀数据定时------");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventSubSceneForTenant(dateVO);
            return DataUtil.returnString(self.addEventSubSceneTenant(dateVO,
                    list == null ? Collections.emptyList() : list));
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventSubSceneForTenant(DateVO dateVO) {
        List<Map> rows = statisticsDAO.getEventSubSceneForTenant(dateVO);
        List<Map<String, String>> alarmTypeList = statisticsDAO.getAlarmType();
        Map<String, String> typeMap = alarmTypeList.stream().collect(
                Collectors.toMap(t -> t.get("typeId"), t -> t.get("typeName"), (o, n) -> n, HashMap::new));
        for (Map map : rows) {
            map.put("typeName", MapUtil.getStr(typeMap, map.get("sub_scene_code")));
        }
        return rows;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventSubSceneTenant(DateVO dateVO, List<Map> list) {
        try {
            statisticsDAO.delEventSubSceneTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item -> statisticsDAO.addEventSubSceneTenant(item));
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 方案 A 扩展：下游 × 小时告警沉淀（写 vm_statistics_event_hour_sub_scene）
     */
    @XxlJob("eventHourSubSceneForTenantHandler")
    public ReturnT<String> eventHourSubSceneForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length() >= 7) {
                    String year = dates[i].substring(0, 4);
                    String month = dates[i].substring(5, 7);
                    log.info("下游×小时告警统计沉淀-" + dates[i]);
                    XxlJobLogger.log("下游×小时告警统计沉淀-" + dates[i]);
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                    List<Map> list = fillSubSceneTypeName(statisticsDAO.getEventHourSubSceneForTenant(dateVO));
                    DataUtil.returnString(self.addEventHourSubSceneTenant(dateVO,
                            list == null ? Collections.emptyList() : list));
                }
            }
        } else {
            log.info("------下游×小时告警统计沉淀------");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = fillSubSceneTypeName(statisticsDAO.getEventHourSubSceneForTenant(dateVO));
            return DataUtil.returnString(self.addEventHourSubSceneTenant(dateVO,
                    list == null ? Collections.emptyList() : list));
        }
        return ReturnT.SUCCESS;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventHourSubSceneTenant(DateVO dateVO, List<Map> list) {
        try {
            statisticsDAO.delEventHourSubSceneTenant(dateVO);
            Lists.partition(list, 5000).forEach(item -> statisticsDAO.addEventHourSubSceneTenant(item));
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 方案 A 扩展：下游 × 日压制沉淀（拦截 JOIN 月表 sub_scenes）
     */
    @XxlJob("interceptSubSceneForTenantHandler")
    public ReturnT<String> interceptSubSceneForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length() >= 7) {
                    String year = dates[i].substring(0, 4);
                    String month = dates[i].substring(5, 7);
                    log.info("下游×日压制统计沉淀-" + dates[i]);
                    XxlJobLogger.log("下游×日压制统计沉淀-" + dates[i]);
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                    List<Map> list = fillSubSceneTypeName(statisticsDAO.getInterceptSubSceneForTenant(dateVO));
                    DataUtil.returnString(self.addInterceptSubSceneTenant(dateVO,
                            list == null ? Collections.emptyList() : list));
                }
            }
        } else {
            log.info("------下游×日压制统计沉淀------");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = fillSubSceneTypeName(statisticsDAO.getInterceptSubSceneForTenant(dateVO));
            return DataUtil.returnString(self.addInterceptSubSceneTenant(dateVO,
                    list == null ? Collections.emptyList() : list));
        }
        return ReturnT.SUCCESS;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addInterceptSubSceneTenant(DateVO dateVO, List<Map> list) {
        try {
            statisticsDAO.delInterceptSubSceneTenant(dateVO);
            Lists.partition(list, 5000).forEach(item -> statisticsDAO.addInterceptSubSceneTenant(item));
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 方案 A 扩展：下游 × 小时压制沉淀
     */
    @XxlJob("interceptHourSubSceneForTenantHandler")
    public ReturnT<String> interceptHourSubSceneForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length() >= 7) {
                    String year = dates[i].substring(0, 4);
                    String month = dates[i].substring(5, 7);
                    log.info("下游×小时压制统计沉淀-" + dates[i]);
                    XxlJobLogger.log("下游×小时压制统计沉淀-" + dates[i]);
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                    List<Map> list = fillSubSceneTypeName(statisticsDAO.getInterceptHourSubSceneForTenant(dateVO));
                    DataUtil.returnString(self.addInterceptHourSubSceneTenant(dateVO,
                            list == null ? Collections.emptyList() : list));
                }
            }
        } else {
            log.info("------下游×小时压制统计沉淀------");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = fillSubSceneTypeName(statisticsDAO.getInterceptHourSubSceneForTenant(dateVO));
            return DataUtil.returnString(self.addInterceptHourSubSceneTenant(dateVO,
                    list == null ? Collections.emptyList() : list));
        }
        return ReturnT.SUCCESS;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addInterceptHourSubSceneTenant(DateVO dateVO, List<Map> list) {
        try {
            statisticsDAO.delInterceptHourSubSceneTenant(dateVO);
            Lists.partition(list, 5000).forEach(item -> statisticsDAO.addInterceptHourSubSceneTenant(item));
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /** 下游码补名称（sys_alarm_type） */
    private List<Map> fillSubSceneTypeName(List<Map> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return rows;
        }
        List<Map<String, String>> alarmTypeList = statisticsDAO.getAlarmType();
        Map<String, String> typeMap = alarmTypeList.stream().collect(
                Collectors.toMap(t -> t.get("typeId"), t -> t.get("typeName"), (o, n) -> n, HashMap::new));
        for (Map map : rows) {
            map.put("typeName", MapUtil.getStr(typeMap, map.get("sub_scene_code")));
        }
        return rows;
    }

    /**
     * 统计分析-告警区域处理时长维度统计-租户视角
     */
    @XxlJob("eventAreaHandleForTenantHandler")
    public ReturnT<String> eventAreaHandleForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警区域处理时长维度统计-租户视角-"+dates[i]);
                    XxlJobLogger.log("告警区域处理时长维度统计-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    log.info("告警区域处理时长维度统计-租户视角");
                    List<DurationVO> list = getEventAreaHandleForTenant(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                         DataUtil.returnString(self.insertEventAreaHandleByTenant(dateVO, list));
                    }
                }
            }
        } else {
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            DateVO dateVO = new DateVO();
            dateVO.setYear(String.valueOf(year));
            dateVO.setMonth(String.valueOf(month));
            dateVO.setDay(String.valueOf(day));
            dateVO.setHandleDate(DateUtils.getDate());
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            log.info("告警区域处理时长维度统计-租户视角");
            List<DurationVO> list = getEventAreaHandleForTenant(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                 return DataUtil.returnString(self.insertEventAreaHandleByTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<DurationVO> getEventAreaHandleForTenant(DateVO dateVO){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String lastMonth = String.valueOf(year)+String.valueOf(month);
        List<DurationVO> durationList = new ArrayList<>();
        // 循环近六个月的表名，查询近六个月表中今日完结事件数量
        for (int i = 0;i<5;i++) {
            List<DurationVO> voList = new ArrayList<>();
            dateVO.setTableName(TableUtil.getTableName(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT, lastMonth),
                    Constants.WARN_ORDER_TEBLE));
            voList = statisticsDAO.getEventAreaHandleForTenant(dateVO);
            durationList.addAll(voList);
            lastMonth = DateUtils.getlastMonth(lastMonth);
        }


        // 将两个list中，相同租户、区县、设备的数据数量相加
        List<DurationVO> durationRes = new ArrayList<>();
        durationList.stream()
                .collect(Collectors.groupingBy(
                        item -> (item.getTenantId()+item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getDeviceCode() + item.getWarningSource() + item.getIndustryCode() + item.getYearStr()+item.getMonthStr()+item.getDayStr()+item.getAlarmTime()),
                        Collectors.toList()))
                .forEach((id, transfer) -> {
                    transfer.stream()
                            .reduce((a, b) -> new DurationVO(a.getTenantId(), a.getProvinceId(), a.getCityId(), a.getCountyId(), a.getDeviceCode(), a.getWarningSource(),
                                    a.getIndustryCode(), a.getModifierDate(),a.getAlarmTime(),a.getYearStr(),a.getMonthStr(),a.getDayStr(),a.getRegionName(),
                                    a.getHandleDuration()+b.getHandleDuration(),
                                    a.getDurationNum()+b.getDurationNum()))
                            .ifPresent(durationRes::add);
                });
        return durationList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAreaHandleByTenant(DateVO dateVO,List<DurationVO> list){
        try {
            //删除当天的数据
            statisticsDAO.deleteEventAreaHandleByTenant(dateVO);
            //新增当天的数据
            List<List<DurationVO>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventAreaHandleByTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    private static final String COUNTY_ID = "county_id";
    private static final String DEVICE_NUM = "deviceNum";
    private static final String DEV_ONLINE_NUM = "devOnlineNum";
    private static final String DEV_OFFLINE_NUM = "devOfflineNum";
    private static final String TENANT_ID = "tenant_id";
    private static final String PRODUCT_CODE = "product_code";
    private static final String INDUSTRY_CODE = "industry_code";
    private static final String APP_VER_CODE = "app_ver_code";
    /**
     * 工作台、告警事件、全景展示-设备通道数量统计表-租户视角
     */
    @XxlJob("deviceStatisticForTenantHandler")
    public ReturnT<String> deviceStatisticForTenantHandler(String param) {
        log.info("更新设备通道数量统计表-租户视角");
        DateVO dateVO = DataUtil.setDateVO();
        List<Map> reslutList = getDeviceStatisticForTenant();
        if (!CollectionUtils.isEmpty(reslutList)) {
            return DataUtil.returnString(self.insertDeviceStatisticTenant(dateVO,reslutList));
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getDeviceStatisticForTenant(){
        String localNode="";
        //获取当前节点
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(com.chinatower.event.constants.Constants.PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null) {
                localNode = sysConfigInfo.getConfigValue();
            }
        }
        List<Map> chanList = statisticsDAO.getChannelStatisticTenant(localNode);
        List<Map> devList = statisticsDAO.getDevStatisticTenant(localNode);
        List<Map> reslutList = new ArrayList<>();
        reslutList.addAll(chanList);
        reslutList.addAll(devList);
        return reslutList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertDeviceStatisticTenant(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteDeviceStatisticTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertDeviceStatisticTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 工作台事件数量统计表-租户用-行业版本与告警状态维度
     */
    @XxlJob("eventNumForTenantByAppVerCodeHandler")
    public ReturnT<String> eventNumForTenantByAppVerCodeHandler(String param) {
        log.info("更新工作台事件数量统计表-租户用");
        //根据当前时间，获取告警事件分月表名
        DateVO dateVO = DataUtil.setDateVO();
        dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));

        List<OrderNumVO> list = getEventNumForTenantNew(dateVO);

        if (!CollectionUtils.isEmpty(list)) {
            return DataUtil.returnString(self.insertEventNumByTenantNew(list));
        }
        return ReturnT.SUCCESS;
    }

    private List<OrderNumVO> getEventNumForTenantNew(DateVO dateVO) {
        //对全量统计字段进行查询、累加、更新操作
        //获取当前月的表名
        String dateStr = DateUtils.dateTimeNow(DateUtils.DATE_TIME_YM_FORMAT); //日期转化为字符串，只有年月
        boolean dateFlag = true;
        List<OrderNumVO> resList = new ArrayList<>();

        while (dateFlag) {
            List<OrderNumVO> eventNumList = statisticsDAO.getEventNumListNew(dateVO);
            resList.addAll(eventNumList);
            if (Constants.MIN_TABLE_DATE.equals(dateStr)) {
                dateFlag = false;
            } else {
                dateStr = DateUtils.getlastMonth(dateStr);
                dateVO.setTableName(TableUtil.getTableNameByString(dateStr, Constants.WARN_ORDER_TEBLE));
            }
        }

        //将两个list中，相同的数据数量相加
        List<OrderNumVO> list = new ArrayList<>();
        resList.stream().collect(Collectors.groupingBy(item ->
                (item.getTenantId() + item.getAppVerCode() + item.getOrderStatus() + item.getWarningSource()
                        + item.getDeviceCode() + item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getIndustryCode()), Collectors.toList())).forEach(
                (id, transfer) -> {
                    transfer.stream().reduce((a, b) ->
                            new OrderNumVO(a.getTenantId(), a.getAppVerCode(), a.getOrderStatus(), a.getWarningSource(),
                                    a.getDeviceCode(), a.getProvinceId(), a.getCityId(), a.getCountyId(), a.getOrderNum() + b.getOrderNum(),
                                    a.getOvertimeNum() + b.getOvertimeNum(), b.getIndustryCode())).ifPresent(list::add);
                });
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByTenantNew(List<OrderNumVO> list){
        try {
            statisticsDAO.deleteEventNumNew();
            List<List<OrderNumVO>> partitionList = Lists.partition(list, 1000);
            partitionList.forEach(item ->
                    statisticsDAO.insertEventNumNew(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  告警每日维度统计-租户用-行业版本与告警状态维度
     */
    @XxlJob("eventNumByDayForTenantByAppVerCodeHandler")
    public ReturnT<String> eventNumByDayForTenantByAppVerCodeHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警每日维度统计-行业版本与告警状态维度-租户视角-"+dates[i]);
                    XxlJobLogger.log("告警每日维度统计-行业版本与告警状态维度-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<EventSourceVo> list = getEventNumDayListForTenantNews(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumDayTenantNews(dateVO, list));
                    }
                }
            }
        } else {
            log.info("------告警每日维度统计(租户)沉淀数据定时------");
            XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MSG);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            //根据当前时间，获取告警事件分月表名
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumDayListForTenantNew(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumDayTenantNew(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumDayListForTenantNew(DateVO dateVO){
        return statisticsDAO.getEventNumDayListForTenantNew(dateVO);
    }

    private List<EventSourceVo> getEventNumDayListForTenantNews(DateVO dateVO){
        return statisticsDAO.getEventNumDayListForTenantNews(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumDayTenantNew(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumDayTenantNew(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumDayTenantNew(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumDayTenantNews(DateVO dateVO,List<EventSourceVo> list){
        try {
            statisticsDAO.delEventNumDayTenantNew(dateVO);
            List<List<EventSourceVo>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumDayTenantNews(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警压制事件统计表-租户视角
     * @return
     */
    @XxlJob("eventInterceptNumForTenant")
    public ReturnT<String> eventInterceptNumForTenant(String param) {
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(com.chinatower.event.constants.Constants.TASK_SELECT_INTERCEPT_POWER_FLAG);
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info(Constants.EVENT_INTERCEPT_DAY_TENANT+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_INTERCEPT_DAY_TENANT+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getInterceptNum(dateVO,suppressDictData);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertInterceptNum(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.EVENT_INTERCEPT_DAY_TENANT);
            XxlJobLogger.log(Constants.EVENT_INTERCEPT_DAY_TENANT);
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getInterceptNum(dateVO,suppressDictData);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertInterceptNum(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getInterceptNum(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData){
        return interceptService.getInterceptNum(dateVO,suppressDictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertInterceptNum(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteInterceptNum(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertInterceptNum(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警压制事件统计表-租户视角
     * @return
     */
    @XxlJob("eventInterceptNumForTenantNew")
    public ReturnT<String> eventInterceptNumForTenantNew(String param) {
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(com.chinatower.event.constants.Constants.TASK_SELECT_INTERCEPT_POWER_FLAG);
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info(Constants.EVENT_INTERCEPT_DAY_TENANT+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_INTERCEPT_DAY_TENANT+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getInterceptNumNew(dateVO,suppressDictData);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertInterceptNumNew(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.EVENT_INTERCEPT_DAY_TENANT);
            XxlJobLogger.log(Constants.EVENT_INTERCEPT_DAY_TENANT);
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getInterceptNumNew(dateVO,suppressDictData);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertInterceptNumNew(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getInterceptNumNew(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData){
        return interceptService.getInterceptNumNew(dateVO,suppressDictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertInterceptNumNew(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteInterceptNumNew(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertInterceptNumNew(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  昨日告警每日维度统计
     */
    @XxlJob("eventYesterdayNumByDayForTenantHandler")
    public ReturnT<String> eventYesterdayNumByDayForTenantHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否符合要求（长度大于等于7，表示至少包含 yyyy-MM）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.ALARM_DAILY_STATISTICS_MSG+dates[i]);
                    XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MSG+dates[i]);
                    // 初始化日期对象（DateVO）并设置表名
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumYesterdayListForTenant(dateVO);
                    // 如果列表不为空，则将统计结果写入数据库
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumYesterdayTenant(dateVO, list));
                    }
                }
            }
        } else {
            log.info(Constants.ALARM_DAILY_STATISTICS_MSG);
            XxlJobLogger.log(Constants.ALARM_DAILY_STATISTICS_MSG);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumYesterdayListForTenant(dateVO);
            // 如果列表不为空，则将统计结果写入数据库
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumYesterdayTenant(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }


    private List<Map> getEventNumYesterdayListForTenant(DateVO dateVO){
        List<Map> eventNumDays = statisticsDAO.getEventNumYesterdayListForTenant(dateVO);
        return eventNumDays;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumYesterdayTenant(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.delEventNumYesterdayTenant(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.addEventNumYesterdayTenant(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 摄像机告警事件统计表-租户视角
     * @return
     */
    @XxlJob("eventDeviceRelForTenant")
    public ReturnT<String> eventDeviceRelForTenant(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("摄像机维度告警事件关系沉淀表-租户视角-"+dates[i]);
                    XxlJobLogger.log("摄像机维度告警事件关系沉淀表-租户视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getDeviceNum(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertDeviceNum(dateVO, list));
                    }
                }
            }
        } else {
            log.info("摄像机维度告警事件关系沉淀表-租户视角");
            XxlJobLogger.log("摄像机维度告警事件关系沉淀表-租户视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getDeviceNum(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertDeviceNum(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getDeviceNum(DateVO dateVO){
        return statisticsDAO.getDeviceNum(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertDeviceNum(DateVO dateVO,List<Map> list){
        try {
            statisticsDAO.deleteDeviceNum(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertDeviceNum(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 用户数量按组织统计-租户视角
     * @return
     */
    @XxlJob("userNumByDeptForTenant")
    public ReturnT<String> userNumByDeptForTenant(String param) {
                    log.info("用户数量按组织关系沉淀表-租户视角");
                    XxlJobLogger.log("用户数量按组织关系沉淀表-租户视角");
                    List<Map> list = getUserNumByDept();
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertUserNumByDept(list));
                    }

        return ReturnT.SUCCESS;
    }


    private List<Map> getUserNumByDept(){
        return statisticsDAO.getUserNumByDept();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertUserNumByDept(List<Map> list){
        try {
            statisticsDAO.deleteUserNumByDept();
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    statisticsDAO.insertUserNumByDept(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

}
