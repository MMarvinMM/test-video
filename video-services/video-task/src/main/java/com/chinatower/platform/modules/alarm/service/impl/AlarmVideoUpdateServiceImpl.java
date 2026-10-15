package com.chinatower.platform.modules.alarm.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.AssembleUrlUtil;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.client.AlarmFlowDeployFeign;
import com.chinatower.event.api.model.AlarmByTaskReq;
import com.chinatower.event.entity.vo.*;
import com.chinatower.platform.asyncTask.AsyncCommon;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoPlayerAPI;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.modules.alarm.dao.AlarmVideoUpdateDAO;
import com.chinatower.platform.modules.alarm.service.AlarmVideoUpdateService;
import com.chinatower.platform.mongo.AlarmVideoUrlMongoMapper;
import com.chinatower.video.api.entity.DownloadProcessDTO;
import com.chinatower.video.api.entity.VideoDownloadVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.chinatower.common.entity.Result;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AlarmVideoUpdateServiceImpl implements AlarmVideoUpdateService {

    @Autowired
    private AlarmVideoUpdateDAO alarmVideoUpdateDAO;

    @Autowired
    private AlarmVideoUrlMongoMapper alarmVideoUrlMongoMapper;

    @Autowired
    private VideoPlayerAPI videoPlayerAPI;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private AlarmFlowDeployFeign alarmFlowDeployFeign;
    @Autowired
    private AsyncCommon asyncCommon;

    /**
     * 抓视频定时更新视频路径
     * @return
     */
    @Override
    public boolean alarmVideoUpdate() {
        try {
            //查询需要更新的告警
            Query query = new Query();
            query.addCriteria(Criteria.where("operateSourceType").is(2));
            query.addCriteria(Criteria.where("deleteFlag").is("0"));
            query.addCriteria(Criteria.where("downloadStatus").is(0));
            List<CaptureVideoVO> videoVOList = mongoTemplate.find(query,CaptureVideoVO.class,"vm_record_download");
            if (!CollectionUtils.isEmpty(videoVOList)){
                log.info("抓视频查询需要更新的告警结果{}", JSONArray.toJSONString(videoVOList));
            }else {
                log.info("抓视频未查询到需要更新告警");
                return true;
            }
            //根据批次id查询下载进度
            List<VideoDownloadVO> processlist = new ArrayList<>();
            for (CaptureVideoVO captureVideoVO : videoVOList) {
                VideoDownloadVO vo = new VideoDownloadVO();
                vo.setBatchId(captureVideoVO.getBatchId());
                vo.setDeviceCode(captureVideoVO.getDeviceCode());
                vo.setChannelCode(captureVideoVO.getChannelCode());
                processlist.add(vo);

            }
            List<DownloadProcessDTO> downloadVOList = new ArrayList<>();
            List<List<VideoDownloadVO>> partitionList = Lists.partition(processlist, 50);
                    ThreadParallelUtil.run(partitionList,f->{
                        log.info("抓视频调用查询下载进度接口入参：{}", JSONObject.toJSONString(f));
                        Result<Map<String, DownloadProcessDTO>> result = videoPlayerAPI.downloadProcess(f);
                        log.info("抓视频调用查询下载进度接口结果：{}", JSONObject.toJSONString(result));
                        if (result != null && result.getData().size() > 0 && result.getData() !=null){
                            downloadVOList.addAll(result.getData().values());
                        }
                        return null;
                    }, ThreadParallelUtil.ConfigFactory.SECOND_DOWN_VIDEO);
            //过滤下载进度为100%的查询结果
            List<DownloadProcessDTO> resultList = downloadVOList.stream()
                    .filter(entry -> "100%".equals(entry.getProcess()) && "1000".equals(entry.getProcessCode()))
                    .collect(Collectors.toList());
            List<String> batchIdList = resultList.stream().map(v -> v.getBatchId()).collect(Collectors.toList());

            //过滤下载进度为100% 需要更新的告警
            List<CaptureVideoVO> list = videoVOList.stream().filter(videoVo -> batchIdList.contains(videoVo.getBatchId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)){
                log.info("抓视频未查询到需要更新告警");
                return true;
            }
            //更新告警视频url入参
            List<CaptureVideoVO> updateList = new ArrayList<>();
            // 是否纯属地化
            boolean isDependenceFlag = this.isDependenceFlag();
            //匹配批次id 赋值更新信息
            for (CaptureVideoVO videoVO : list) {
                for (DownloadProcessDTO downloadProcessDTO : resultList) {
                    if (downloadProcessDTO.getBatchId().equals(videoVO.getBatchId())){
                        CaptureVideoVO vo = new CaptureVideoVO();
                        vo.setTableType(videoVO.getTableType());
                        vo.setWarningOrderId(videoVO.getWarningOrderId());
                        vo.setDownLoadUrl(downloadProcessDTO.getDownloadUrl());
                        String accessNode = downloadProcessDTO.getAccessNode();
                        String url = AssembleUrlUtil.getAssembleUrl(accessNode,downloadProcessDTO.getDownloadUrl(),!isDependenceFlag);
                        vo.setDownLoadUrl(url);
                        if(Constants.ONE.equals(videoVO.getTableType())){
                            vo.setTableName(Constants.WARN_WARNING_INTERCEPT);
                        }else if(Constants.TWO.equals(videoVO.getTableType())){
                            vo.setTableName(Constants.WARN_ORDER_TEBLE + videoVO.getWarningOrderId().substring(0, 6));
                        }else if(Constants.THREE.equals(videoVO.getTableType())){
                            vo.setTableName(Constants.WARN_ALARM_OPERATE);
                        }
                        updateList.add(vo);
                        break;
                    }
                }
            }
            //更新抓视频结果
            log.info("更新抓视频入参：{}", JSONObject.toJSONString(updateList));
            for (CaptureVideoVO captureVideoVO : updateList) {
                int i = alarmVideoUpdateDAO.updateVideoResult(captureVideoVO);
            }
            //压制
            List<CaptureVideoVO> interceptList = updateList.stream().filter(v -> "1".equals(v.getTableType())).collect(Collectors.toList());
            //研判
            List<CaptureVideoVO> operateList = updateList.stream().filter(v -> "3".equals(v.getTableType())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(interceptList)){
                List<String> idList = interceptList.stream().map(CaptureVideoVO::getWarningOrderId).collect(Collectors.toList());
                //根据告警id查询
                List<OrderInfoVO> interList = alarmVideoUpdateDAO.getOrderAlarmInfo(idList, Constants.WARN_WARNING_INTERCEPT);
                alarmVideoUrlMongoMapper.updateVideoUrl(interceptList,Constants.WARN_WARNING_INTERCEPT,interList);
            }

            if (!CollectionUtils.isEmpty(operateList)){
                List<String> idList = operateList.stream().map(CaptureVideoVO::getWarningOrderId).collect(Collectors.toList());
                List<OrderInfoVO> opList = alarmVideoUpdateDAO.getOrderAlarmInfo(idList, Constants.WARN_ALARM_OPERATE);
                alarmVideoUrlMongoMapper.updateVideoUrl(operateList,Constants.WARN_ALARM_OPERATE,opList);
            }
            log.info("抓视频定时更新视频路径->执行任务成功");
            return true;
        } catch (Exception e) {
            log.error("抓视频定时更新视频路径->执行任务失败:{}", e.getMessage(), e);
        }
        return false;
    }

    //taskBatchId查询定时更新二次识别结果
    @Override
    public boolean secondResultHandler() {
        try {
            //查询需要更新的告警
            Query query = new Query();
            query.addCriteria(Criteria.where("deleteFlag").is(Constants.ZERO));
            query.addCriteria(Criteria.where(Constants.ANALY_TYPE).is(Constants.ZERO));
            List<SecondRecogBatchVO> list = mongoTemplate.find(query, SecondRecogBatchVO.class, Constants.SECOND_RECOG_TASK_INFO);
            log.info("二次识别查询需要更新的告警结果：" + JSONObject.toJSONString(list));
            if (CollectionUtils.isEmpty(list)) {
                log.info("未查询到需要更新的二次识别任务");
            }
            long outTime = 20;
            Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SECOND_TASK_OUT_TIME);
            if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                outTime = Long.parseLong(suppressDictData.getData().get(0).getDictValue());
            }
            log.info("taskBatchId查询定时更新查询超时时间outTime:{}", outTime);
            //获取五分钟前的时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS);
            LocalDateTime beforeFiveDate = LocalDateTime.parse(LocalDateTime.now().minusSeconds(outTime).format(DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS)),formatter);
            List<String> outTimeTaskIdList = list.stream().filter(v->LocalDateTime.parse(v.getCreateTime(),formatter).isBefore(beforeFiveDate)).map(v -> v.getTaskId()).collect(Collectors.toList());
            log.info("taskBatchId查询定时更新查询超时数据：{}", outTimeTaskIdList);
            //查询告警需要更新的数据
            List<String> taskBatchIdAlarmList = list.stream().filter(v-> Constants.ONE.equals(v.getTaskType()) && !outTimeTaskIdList.contains(v.getTaskId())).map(v -> v.getTaskBatchId()).distinct().collect(Collectors.toList());
            //查询无人机需要更新的数据
            List<SecondRecogBatchVO> uavDataList = list.stream().filter(v -> Constants.TWO.equals(v.getTaskType()) && !outTimeTaskIdList.contains(v.getTaskId())).collect(Collectors.toList());
            List<String> taskBatchIdUavList = uavDataList.stream().map(v -> v.getTaskBatchId()).distinct().collect(Collectors.toList());
            //查询预置位需要更新的数据
            List<SecondRecogBatchVO> presetDataList = list.stream().filter(v -> Constants.THREE.equals(v.getTaskType()) && !outTimeTaskIdList.contains(v.getTaskId())).collect(Collectors.toList());
            List<String> taskBatchIdPresetList = presetDataList.stream().map(v -> v.getTaskBatchId()).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(taskBatchIdAlarmList)){
                this.secondResultUpdate(taskBatchIdAlarmList,Constants.ONE,outTimeTaskIdList,null);
            }else{
                log.info("未查询到告警需要更新的二次识别任务");
            }
            if (!CollectionUtils.isEmpty(taskBatchIdUavList)){
                this.secondResultUpdate(taskBatchIdUavList,Constants.TWO,outTimeTaskIdList,uavDataList);
            }else{
                log.info("未查询到无人机需要更新的二次识别任务");
            }
            if (!CollectionUtils.isEmpty(taskBatchIdPresetList)){
                this.secondResultUpdate(taskBatchIdPresetList,Constants.THREE,outTimeTaskIdList,presetDataList);
            }else{
                log.info("未查询到预置位需要更新的二次识别任务");
            }
            if (!CollectionUtils.isEmpty(outTimeTaskIdList)){
                this.outTimeTaskUpdate(outTimeTaskIdList);
            }else{
                log.info("未查询到超时分析的二次识别任务");
            }
        } catch (Exception e) {
            log.error("taskBatchId查询定时更新二次识别结果->执行任务失败:{}", e);
        }
        return true;
    }

    @Override
    public Result presetSecondRecong(Map<String, Object> o) {
        List<SecondRecogBackVO> data = JSONArray.parseArray(JSONObject.toJSONString(o.get("data")), SecondRecogBackVO.class);
        List<SecondRecogBatchVO> uavDataList = JSONArray.parseArray(JSONObject.toJSONString(o.get("uavDataList")), SecondRecogBatchVO.class);
        asyncCommon.receiveAiAlarm(data,uavDataList);   //更新批次mongo表状态
        this.batchMongoUpdate(data,Constants.THREE);
        return null;
    }

    /**
     * 更新二次识别结果
     * @param taskBatchIdList
     * @param type
     * @param outTimeTaskIdList
     * @param uavDataList
     */
    public void secondResultUpdate(List<String> taskBatchIdList,String type,List<String> outTimeTaskIdList,List<SecondRecogBatchVO> uavDataList) {
        for (String taskBatchId : taskBatchIdList) {
            //通过taskBatchId查询分析状态
            String platFormCode = null;
            String toPlatformCode = null;
            if(Constants.TWO.equals(type) || Constants.THREE.equals(type)){
                SecondRecogBatchVO secondRecogBatchVO = uavDataList.stream().filter(v -> taskBatchId.equals(v.getTaskBatchId())).findFirst().orElse(null);
                log.info("taskBatchId查询定时更新二次识别结果跨节点secondRecogBatchVO:{}", JSON.toJSONString(secondRecogBatchVO));
                platFormCode = secondRecogBatchVO.getAcceptNode();
                toPlatformCode = secondRecogBatchVO.getAccessNode();
            }
            AlarmByTaskReq alarmByTaskReq = new AlarmByTaskReq();
            alarmByTaskReq.setTaskBatchId(taskBatchId);
            alarmByTaskReq.setType(type);
            alarmByTaskReq.setPlatFormCode(toPlatformCode);
            alarmByTaskReq.setToPlatformCode(platFormCode);
            Result<List<SecondRecogBackVO>> result = alarmFlowDeployFeign.alarmByTaskBatchIdSelf(alarmByTaskReq);
            log.info("taskBatchId查询定时更新二次识别结果{}", JSON.toJSONString(result));
            if (result != null && !CollectionUtils.isEmpty(result.getData())) {
                List<SecondRecogBackVO> data = result.getData();
                log.info("taskBatchId查询定时更新二次识别结果taskBatchId：{}， data：{}", taskBatchId, data);
                data = data.stream().filter(v -> !outTimeTaskIdList.contains(v.getTaskId())).collect(Collectors.toList());
                //1为告警，告警更新vm_warning_order_t_识别结果
                if (Constants.ONE.equals(type)){
                    alarmFlowDeployFeign.secondRecogBack(data);
                }
                //2为无人机，更新
                if (Constants.TWO.equals(type)){
                    asyncCommon.receiveUavAlarm(data,uavDataList);
                }
                //3为预置位，更新
                if (Constants.THREE.equals(type)){
                    asyncCommon.receiveAiAlarm(data,uavDataList);
                }
                //更新批次mongo表状态
                this.batchMongoUpdate(data,type);
            } else {
                log.info("未查询到该批次分析任务：{}", taskBatchId);
            }
        }

    }

    /**
     * 更新累计设备因子
     * @param outTimeTaskId
     */
    public void outTimeTaskUpdate(List<String> outTimeTaskId) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.SECOND_RECOG_TASK_INFO);
        //接收需要更新累计设备因子数据
            Query query = new Query();
            query.addCriteria(Criteria.where(Constants.DESC_TASK_ID_PARAM).in(outTimeTaskId));
            Update update = new Update();
            update.set("updateTime", DateUtils.getTime());
            update.set(Constants.ANALY_TYPE,Constants.FIRST_LINK);
            bulkOperations.upsert(query, update);
        bulkOperations.execute();
    }


    //更新批次mongo表状态
    public void batchMongoUpdate(List<SecondRecogBackVO> data,String type) {
            log.info("taskBatchId查询定时更新二次识别结果更新mongo:{}", JSON.toJSONString(data));
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.SECOND_RECOG_TASK_INFO);
            //接收需要更新新增的二次识别批次信息
            for (SecondRecogBackVO listVO : data) {
                Query query = new Query();
                query.addCriteria(Criteria.where(Constants.DESC_TASK_ID_PARAM).is(listVO.getTaskId()));
                Update update = new Update();
                update.set("updateTime", DateUtils.getTime());
                update.set(Constants.ANALY_TYPE,listVO.getAnalyType());
                if (ObjectUtil.isNotNull(listVO)){
                    update.set("resultJson",JSONObject.toJSONString(listVO));
                }
                if (Constants.TWO.equals(type) || Constants.THREE.equals(type)){
                    update.set("secondJudgeResult",listVO.getSecondJudgeResult());
                    update.set("originalPictureUrl",listVO.getOriginalPictureUrl());
                    update.set("alarmPictureUrl",listVO.getAlarmPictureUrl());
                    update.set("alarmVideoUrl",listVO.getAlarmVideoUrl());
                }
                bulkOperations.upsert(query, update);
            }
            bulkOperations.execute();

    }


    /**
     * 判断是否属地化
     * @return
     */
    public boolean isDependenceFlag(){
        // 是否属地化
        Boolean isDependenceFlag = false;
        AjaxResult ajaxResult = systemFeignClient.getConfValue(Constants.DEPENDENCE_MANAGE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            log.info("DEPENDENCE_MANAGE====>{}",sysConfig);
            if (sysConfig != null && sysConfig.getConfigValue().equals(Constants.ZERO)) {
                isDependenceFlag = true;
            }
        }
        return isDependenceFlag;
    }

}
