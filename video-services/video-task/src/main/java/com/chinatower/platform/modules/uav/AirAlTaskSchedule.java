package com.chinatower.platform.modules.uav;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.AirAlTaskLogReq;
import com.chinatower.device.entity.dto.AirLinePointTaskReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.SecondExtraFieldInfoVo;
import com.chinatower.event.entity.vo.SecondRecogFileVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import com.chinatower.platform.modules.uav.entity.AirCorrespondPhotoRep;
import com.chinatower.platform.modules.uav.entity.AirLinePhotoTask;
import com.chinatower.platform.modules.uav.service.AirUavService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WLF
 * @description
 * @date 2024/11/18 19:18
 */
@Component
@Slf4j
public class AirAlTaskSchedule {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Resource
    private IAirLineService iAirLineService;
    @Resource
    private SystemFeignClient systemFeignClient;
    @Resource
    private ITradeWarningOrderService iTradeWarningOrderService;
    @Resource
    private AirUavService airUavService;

    /**
     * 飞行实时流任务下发补偿任务
     *
     * @param param
     * @return
     */
    @XxlJob("uavTaskCompensate")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> uavTaskCompensate(String param) {
        try {
            XxlJobLogger.log("开始执行飞行实时流任务！");
            log.info("开始执行飞行实时流任务");
            long outTime = 20;
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SECOND_TASK_OUT_TIME);
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                outTime = Long.parseLong(suppressDictData.getData().get(0).getDictValue());
            }
            //获取五分钟前的时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS);
            LocalDateTime beforeFiveDate = LocalDateTime.parse(LocalDateTime.now().minusSeconds(outTime).format(DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS)), formatter);
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 扫描mongo中发送失败的任务
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("returnCode").is("8888")).addCriteria(Criteria.where("createTime").gte(beforeFiveDate).lte(now)); // 添加条件，选择createTIME在beforeFiveDate之后到now之前的记录
            List<AirAlTaskLogReq> airAlTaskLogReqs = mongoTemplate.find(query, AirAlTaskLogReq.class, Constants.AIR_TASK_AL_SYNC_LOG);
            log.info("飞行实时流任务下发补偿任务执行条数{}",airAlTaskLogReqs.size());
            //调用device服务重新下发算法任务
            iAirLineService.retryAirAlSync(airAlTaskLogReqs);
            XxlJobLogger.log("定时任务执行完毕！");
            log.info("飞行实时流任务完毕");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("飞行实时流任务下发补偿任务:", JSONObject.toJSONString(e));
            log.error("飞行实时流任务完毕" + JSONObject.toJSONString(e));
            return ReturnT.FAIL;
        }
    }

    /**
     * 无人机航线照片对比定时任务
     * 逻辑：对每个任务获取基准照片集和对比照片集 → 倒序排序 → 配对 → 下发二次识别
     */
    @XxlJob("uavAirLinePhotoCompare")
    public ReturnT<String> uavAirLinePhotoCompare(String param) {
        try {
            // 1. 获取待处理的任务列表
            List<AirLinePhotoTask> tasks = airUavService.queryUavAirLinePhotoCompare();
            if (CollectionUtils.isEmpty(tasks)) {
                log.info("未找到待处理的照片对比任务");
                return ReturnT.SUCCESS;
            }
            // 2. 准备二次识别任务列表
            List<SecondRecogSendVO> recognitionTasks = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for (AirLinePhotoTask task : tasks) {
                try {
                    log.info("处理照片对比任务: taskId={}, baseTaskId={}, compareTaskId={}",
                            task.getId(), task.getTaskRecordId(), task.getCompareTaskRecordId());

                    // 3. 获取基准照片集（倒序）
                    List<AirCorrespondPhotoRep> basePhotos = getSortedPhotos(task, task.getTaskRecordId());
                    if (CollectionUtils.isEmpty(basePhotos)) {
                        log.info("基准任务照片为空: taskRecordId={}", task.getTaskRecordId());
                        continue;
                    }

                    // 4. 获取对比照片集（倒序）
                    List<AirCorrespondPhotoRep> comparePhotos = getSortedPhotos(task, task.getCompareTaskRecordId());
                    if (CollectionUtils.isEmpty(comparePhotos)) {
                        log.info("对比任务照片为空: compareTaskRecordId={}", task.getCompareTaskRecordId());
                        continue;
                    }

                    // 5. 构建图片配对关系
                    buildPhotoPairs(task, basePhotos, comparePhotos, recognitionTasks);

                } catch (Exception e) {
                    log.error("处理照片对比任务失败: taskId={}, error={}", task.getTaskRecordId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("照片对比任务执行失败", e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 无人机航点算法自动二次识别任务
     */
    @XxlJob("uavAirPointAnalysisTask")
    public ReturnT<String> uavAirPointAnalysisTask(String param) {
        try {
            // 1. 获取待处理的任务列表
            Query query = new Query();
            Date currentTime = new Date();
            Criteria criteria = Criteria.where("status").is(Integer.valueOf(com.chinatower.common.core.constant.Constants.ZERO));
            if (param != null && !param.trim().isEmpty()) {
                // 有参数时：处理指定日期的任务
                SimpleDateFormat paramSdf = new SimpleDateFormat("yyyy/MM/dd");
                Date specifiedDate = paramSdf.parse(param.trim());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(specifiedDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date startOfDay = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date endOfDay = calendar.getTime();
                criteria.and("executeTime").gte(startOfDay).lte(endOfDay);
                log.info("处理指定日期任务: {}, 时间范围: {} - {}", param, startOfDay, endOfDay);
            } else {
                TimeZone utc = TimeZone.getTimeZone("UTC");
                Calendar calendar = Calendar.getInstance(utc);
                Date currentUtcTime = calendar.getTime();
                calendar.add(Calendar.HOUR_OF_DAY, -6);
                Date sixHoursAgoUtc = calendar.getTime();
                // 使用UTC时间范围查询
                criteria.and("executeTime").gte(sixHoursAgoUtc).lte(currentUtcTime);
                log.info("处理过去6小时内的任务, 时间范围: {} - {}", sixHoursAgoUtc, currentUtcTime);
            }
            query.addCriteria(criteria);
            List<AirLinePointTaskReq> airLinePointTaskReq = mongoTemplate.find(query, AirLinePointTaskReq.class,com.chinatower.event.constants.Constants.UAV_AIR_POINT_TASK);
            log.info("无人机航点算法自动二次识别任务{}",JSONObject.toJSONString(airLinePointTaskReq));
            airLinePointTaskReq.forEach(task -> {
                AirLinePhotoTask airLinePhotoTask = new AirLinePhotoTask();
                airLinePhotoTask.setAccessNode(task.getAccessNode());
                airLinePhotoTask.setLatitude(task.getLatitude());
                airLinePhotoTask.setLongitude(task.getLongitude());
                airLinePhotoTask.setDeviceCode(task.getDeviceCode());
                List<AirCorrespondPhotoRep> airCorrespondPhotoReps = getSortedPhotos(airLinePhotoTask,task.getTaskRecordId());
                if (CollectionUtils.isNotEmpty(airCorrespondPhotoReps)) {
                    // 下发二次识别任务
                    buildPhotoAny(airCorrespondPhotoReps,task);
                    // 2. 如果查询到数据，进行批量更新
                    if (CollectionUtils.isNotEmpty(airLinePointTaskReq)) {
                        // 提取所有的 taskRecordId
                        List<String> taskRecordIds = airLinePointTaskReq.stream()
                                .map(AirLinePointTaskReq::getTaskRecordId)
                                .distinct()
                                .collect(Collectors.toList());
                        // 批量更新状态为1
                        Query updateQuery = new Query(Criteria.where("taskRecordId").in(taskRecordIds));
                        Update update = new Update().set("status", 1);
                        UpdateResult updateResult = mongoTemplate.updateMulti(updateQuery, update, AirLinePointTaskReq.class, com.chinatower.event.constants.Constants.UAV_AIR_POINT_TASK);
                        log.info("成功更新 {} 条记录的状态为1", updateResult.getModifiedCount());
                    }
                }
            });
        } catch (Exception e) {
            log.error("无人机航点算法自动二次识别任务执行失败", e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    private void buildPhotoAny(List<AirCorrespondPhotoRep> airCorrespondPhotoReps,AirLinePointTaskReq airLinePhotoTask) {
        List<String> algorithmCodes = Arrays.stream(airLinePhotoTask.getAlgorithmCodes().split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        log.info("航点算法自动二次识别任务算法编码集合{}",JSONObject.toJSONString(algorithmCodes));
        airCorrespondPhotoReps.forEach(task -> {
            List<SecondRecogSendVO> secondRecogSendVOS = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            algorithmCodes.forEach(algorithmCode -> {
                SecondRecogSendVO vo = new SecondRecogSendVO();
                vo.setAlgorithmCode(algorithmCode);
                vo.setMakeAlarmFlag(Constants.ONE);
                vo.setLatitude(task.getLatitude());
                vo.setLongitude(task.getLongitude());
                SecondRecogFileVO secondRecogFileVO = new SecondRecogFileVO();
                vo.setTaskType(Constants.TWO);
                secondRecogFileVO.setImgUrls(task.getPhotoAccessUrl()); // 基准图片
                vo.setFileInfo(secondRecogFileVO);
                SecondExtraFieldInfoVo secondExtraFieldInfoVo = new SecondExtraFieldInfoVo();
                secondExtraFieldInfoVo.setPicId(task.getId());
                secondExtraFieldInfoVo.setDeviceCode(airLinePhotoTask.getDeviceCode());
                secondExtraFieldInfoVo.setAirTaskId(airLinePhotoTask.getTaskRecordId());
                secondExtraFieldInfoVo.setAirLineId(airLinePhotoTask.getFlightRouteId());
                vo.setExtraFieldInfo(secondExtraFieldInfoVo);
                secondRecogSendVOS.add(vo);
            });
            // 下发二次识别
            if (CollectionUtils.isNotEmpty(secondRecogSendVOS)) {
                log.info("无人机航点算法自动二次识别任务执行, 数量: {},json:{}", secondRecogSendVOS.size(), JSONObject.toJSONString(secondRecogSendVOS));
                iTradeWarningOrderService.uavSynAnalysisTask(secondRecogSendVOS);
            }
            ids.add(task.getId());

        });
    }


    /**
     * 获取排序后的照片列表（按时间倒序）
     */
    private List<AirCorrespondPhotoRep> getSortedPhotos(AirLinePhotoTask task, String taskRecordId) {
        CorrespondPointReq req = new CorrespondPointReq();
        req.setTaskRecordId(taskRecordId);
        req.setLatitude(task.getLatitude());
        req.setLongitude(task.getLongitude());
        req.setAccessNode(task.getAccessNode());
        req.setDeviceCode(task.getDeviceCode());
        req.setSort(Constants.ONE);
        req.setTenantId(task.getTenantId());
        req.setIndustryCode(task.getIndustryCode());
        Result result = iAirLineService.queryCorrespondPhotoList(req);
        log.info("航点算法自动二次识别任务查询{}",JSONObject.toJSONString(result));
        if (!isValidResult(result)) {
            return Collections.emptyList();
        }
        List<AirCorrespondPhotoRep> photos = convertToPhotoList(result.getData());
        return photos;
    }

    /**
     * 构建图片配对关系
     */
    private void buildPhotoPairs(AirLinePhotoTask task,
                                 List<AirCorrespondPhotoRep> basePhotos,
                                 List<AirCorrespondPhotoRep> comparePhotos,
                                 List<SecondRecogSendVO> outputList) {
        // 以较小的集合为准，避免越界
        int pairCount = Math.min(comparePhotos.size(), basePhotos.size());
        for (int i = 0; i < pairCount; i++) {
            List<SecondRecogSendVO> secondRecogSendVOS = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            SecondRecogSendVO vo = new SecondRecogSendVO();
            vo.setAlgorithmCode(task.getAlgorithmCode());
            vo.setMakeAlarmFlag(Constants.ONE);
            vo.setLatitude(task.getLatitude());
            vo.setLongitude(task.getLongitude());
            // 设置图片对信息
            SecondRecogFileVO secondRecogFileVO = new SecondRecogFileVO();
            vo.setTaskType(Constants.TWO);
            secondRecogFileVO.setImgUrls(basePhotos.get(i).getPhotoAccessUrl()); // 基准图片
            secondRecogFileVO.setBaselinePicUrl(comparePhotos.get(i).getPhotoAccessUrl()); // 对比图片
            vo.setFileInfo(secondRecogFileVO);
            SecondExtraFieldInfoVo secondExtraFieldInfoVo = new SecondExtraFieldInfoVo();
            secondExtraFieldInfoVo.setPicId(comparePhotos.get(i).getPhotoId());
            secondExtraFieldInfoVo.setDeviceCode(task.getDeviceCode());
            secondExtraFieldInfoVo.setAirTaskId(task.getCompareTaskRecordId());
            secondExtraFieldInfoVo.setAirLineId(task.getFlightRouteId());
            vo.setExtraFieldInfo(secondExtraFieldInfoVo);
            outputList.add(vo);
            secondRecogSendVOS.add(vo);
            log.info("生成图片对: base={}, compare={},jsonObject={}",
                    basePhotos.get(i).getPhotoId(), comparePhotos.get(i).getPhotoId(), JSONObject.toJSONString(vo));

            // 下发二次识别
            if (CollectionUtils.isNotEmpty(secondRecogSendVOS)) {
                log.info("准备下发二次识别任务, 数量: {},json:{}", secondRecogSendVOS.size(), JSONObject.toJSONString(secondRecogSendVOS));
                iTradeWarningOrderService.uavSynAnalysisTask(secondRecogSendVOS);
            }

            ids.add(task.getId());
            // 更新定时任务表状态
            if (CollectionUtils.isNotEmpty(ids)){
                airUavService.updatePhotoCompareTaskStatus(ids);
            }
        }
    }

    // 辅助方法
    private boolean isValidResult(Result result) {
        return result != null  && result.getData() != null;
    }

    private List<AirCorrespondPhotoRep> convertToPhotoList(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 忽略未知字段
            return mapper.convertValue(data,
                    mapper.getTypeFactory().constructCollectionType(List.class, AirCorrespondPhotoRep.class));
        } catch (Exception e) {
            log.error("照片列表类型转换失败", e);
            return Collections.emptyList();
        }
    }
}
