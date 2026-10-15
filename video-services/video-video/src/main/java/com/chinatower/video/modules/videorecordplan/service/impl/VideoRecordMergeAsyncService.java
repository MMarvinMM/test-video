package com.chinatower.video.modules.videorecordplan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.util.TimeMer;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;
import com.chinatower.video.modules.videorecordplan.dto.TimeBucket;
import com.chinatower.video.modules.videorecordplan.dto.TimeNode;
import com.chinatower.video.modules.videorecordplan.mapper.ChannelVideoPlanMapper;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordMergeAsyncService;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideoRecordMergeAsyncService implements IVideoRecordMergeAsyncService {
    private static final String DATE_FORMAT = "2020-01-01 ";
    @Resource
    private ChannelVideoPlanMapper channelVideoPlanMapper;
    @Resource
    private IVideoRecordPlanInfoAsyncService videoRecordPlanInfoAsyncService;
    @Resource
    ICasCadeQueryService iCasCadeQueryService;

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void mergeRecordPlan(ChannelVideoPlanVO channelVideoPlanVO) {
        log.info(VideoConstants.MERGE_RECORD_PLAN_SYNC_START + System.currentTimeMillis());
        //获取录像计划信息
        List<ChannelVideoPlanVO> planList = channelVideoPlanMapper.getChannelVideoPlanList(channelVideoPlanVO);
        //如果没有，那么判断整合表是否存在，如果存在，则删除
        if(CollectionUtils.isEmpty(planList)){
            //判断是否有整合计划
            List<ChannelVideoPlanVO> planMergeList = channelVideoPlanMapper.getChannelVideoPlanMergeList(channelVideoPlanVO);
            if(planMergeList != null && planMergeList.size() >0 ) {
                //如果存在，就做删除处理

                long recordPlanId = planMergeList.get(0).getRecordPlanId();
                channelVideoPlanMapper.deleteChannelVideoPlan(planMergeList.get(0));
                channelVideoPlanMapper.deleteTimeSections(planMergeList.get(0));

                JSONObject inJson = new JSONObject();
                inJson.put("recordPlanId", recordPlanId);
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(channelVideoPlanVO.getDeviceCode());
                //南向加字段 add by liuxt 20240117
                inJson.put(DeviceNorthIntfConstants.ACCEPTNODE, casCadeVO.getPlatformCode());
                inJson.put(DeviceNorthIntfConstants.ACCESSNODE, casCadeVO.getCascadeToCode());

                ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
                apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_DELETE_RECORDPLAN);
                apiInfoDTO.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                apiInfoDTO.setInJsonStr(inJson.toJSONString());
                apiInfoDTO.setTowerCode("");
                apiInfoDTO.setChannelCode(channelVideoPlanVO.getChannelCode());
                apiInfoDTO.setTenantId(channelVideoPlanVO.getTenantId());
                // 改为异步调用
                Result<JSONObject> res = videoRecordPlanInfoAsyncService.deleteRecordPlan(apiInfoDTO);
                if (ResultCode.SUCCESS.getCode() != res.getCode()) {
                    throw new CustomException(res.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
                }
            }

        }else{//多条数据

            //判断是否有整合计划
            List<ChannelVideoPlanVO> planMergeList = channelVideoPlanMapper.getChannelVideoPlanMergeList(channelVideoPlanVO);
            if(planMergeList != null && planMergeList.size() >0 ){
                //如果存在，就做更新处理
                //获取开始时间取最小，终止时间取最大,是否全天录像，是否开启声音，存储最大天数
                ChannelVideoPlanVO planMaxVo = channelVideoPlanMapper.getMaxChannelVideoPlan(channelVideoPlanVO);
                planMaxVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                planMaxVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                planMaxVo.setRecordPlanId(planMergeList.get(0).getRecordPlanId());

                //inJson.put(DeviceNorthIntfConstants.TIMESECTIONS,planVo.getTimeSections());
                JSONObject inJson = new JSONObject();

                //如果存在全体存储，那么通道录像计划时间段表不需要整合，取全天数据即可
                if (planMaxVo.getIsAllDay() == 1) {
                    ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                    queryVo.setIsAllDay(1);
                    queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                    List<ChannelVideoPlanVO> planAllList = channelVideoPlanMapper.getChannelVideoPlanList(queryVo);
                    if (!CollectionUtils.isEmpty(planAllList)) {
                        List<TimeSectionVO> timeSections = channelVideoPlanMapper.getAllTimeSections(planAllList.get(0));
                        planMaxVo.setTimeSections(timeSections);
                        inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, timeSections);
                    }

                } else {//不存在全天存储，那么需要合并时间段

                    List<TimeSectionVO> tmpList = new ArrayList<>();
                    for (int j = 1; j <= 7; ++j) {
                        ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                        queryVo.setDayOfWeek(j);
                        queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                        queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                        List<TimeSectionVO> timeSectionsList = channelVideoPlanMapper.getTimeSectionsList(queryVo);
                        if (!CollectionUtils.isEmpty(timeSectionsList)) {
                            List<TimeBucket> timeBucketList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (TimeSectionVO list : timeSectionsList) {
                                String startTime = DATE_FORMAT + list.getStartTime().substring(0, 2) + ":" + list.getStartTime().substring(2, 4) + ":" + list.getStartTime().substring(4, 6);
                                String endtTime = DATE_FORMAT + list.getEndTime().substring(0, 2) + ":" + list.getEndTime().substring(2, 4) + ":" + list.getEndTime().substring(4, 6);
                                try {
                                    timeBucketList.add(new TimeBucket(sdf.parse(startTime), sdf.parse(endtTime)));


                                } catch (Exception e) {
                                    log.error("时间转换错误：", e);
                                }
                            }

                            List<TimeNode> nodeList = TimeMer.merge(timeBucketList);
                            List<TimeBucket> timeBuckets = TimeMer.convert(nodeList);
                            for (TimeBucket timeBucket : timeBuckets) {
                                TimeSectionVO tmpTS = new TimeSectionVO();
                                tmpTS.setDayOfWeek(j);
                                tmpTS.setStartTime(sdf.format(timeBucket.getStart()).substring(11,13)+sdf.format(timeBucket.getStart()).substring(14,16)+sdf.format(timeBucket.getStart()).substring(17,19));
                                tmpTS.setEndTime(sdf.format(timeBucket.getEnd()).substring(11,13)+sdf.format(timeBucket.getEnd()).substring(14,16)+sdf.format(timeBucket.getEnd()).substring(17,19));
                                tmpList.add(tmpTS);
                            }
                        }
                    }
                    planMaxVo.setTimeSections(tmpList);
                    inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, tmpList);

                }
                //更新通道录像计划整合表
                channelVideoPlanMapper.updateChannelVideoPlanMerge(planMaxVo);
                //先删除通道录像计划时间段整合表
                channelVideoPlanMapper.deleteTimeSections(planMaxVo);
                //更新通道录像计划时间段整合表
                List<TimeSectionVO> timeSectionVOList = planMaxVo.getTimeSections();
                if (!CollectionUtils.isEmpty(timeSectionVOList)) {
                    List<PlanTimeSectionDO> list = new ArrayList<>();
                    for (int i = 0; i < timeSectionVOList.size(); i++) {
                        TimeSectionVO timeSectionVO = timeSectionVOList.get(i);
                        PlanTimeSectionDO planTimeSectionDO = new PlanTimeSectionDO();
                        planTimeSectionDO.setDayOfWeek(timeSectionVO.getDayOfWeek());
                        planTimeSectionDO.setEndTime(timeSectionVO.getEndTime());
                        planTimeSectionDO.setStartTime(timeSectionVO.getStartTime());
                        planTimeSectionDO.setRecordPlanId(planMergeList.get(0).getRecordPlanId());
                        planTimeSectionDO.setCreator(channelVideoPlanVO.getUsername());
                        planTimeSectionDO.setModifier(channelVideoPlanVO.getUsername());
                        planTimeSectionDO.setCreateTime(new Date());
                        planTimeSectionDO.setUpdateTime(new Date());
                        planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                        planTimeSectionDO.setDeviceCode(planMergeList.get(0).getDeviceCode());
                        list.add(planTimeSectionDO);
                    }
                    channelVideoPlanMapper.saveTimeSectionsMergeBatch(list);
                }


                inJson.put(DeviceNorthIntfConstants.IS_ALL_Day, planMaxVo.getIsAllDay());
                inJson.put(DeviceNorthIntfConstants.STREAM_TYPE, planMaxVo.getStreamType());
                inJson.put(DeviceNorthIntfConstants.STORAGEDAY, planMaxVo.getStorageDay());
                inJson.put(DeviceNorthIntfConstants.ISAUDIO, planMaxVo.getIsAudio());
                inJson.put(DeviceNorthIntfConstants.RECORDPLANNAME, "录像计划整合"+String.valueOf(System.currentTimeMillis()));
                inJson.put(DeviceNorthIntfConstants.RECORDPLANSTATUS, planMaxVo.getRecordPlanStatus());
                inJson.put(DeviceNorthIntfConstants.STARTDATE, planMaxVo.getStartDate());
                inJson.put(DeviceNorthIntfConstants.ENDDATE, planMaxVo.getEndDate());
                inJson.put(DeviceNorthIntfConstants.DEVICECODE, planMaxVo.getDeviceCode());
                inJson.put(DeviceNorthIntfConstants.CHANNELCODE, planMaxVo.getChannelCode());
                inJson.put(DeviceNorthIntfConstants.RECORD_PLAN_ID, planMergeList.get(0).getRecordPlanId());
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(planMaxVo.getDeviceCode());
                //南向加字段 add by liuxt 20240117
                inJson.put(DeviceNorthIntfConstants.ACCEPTNODE, casCadeVO.getPlatformCode());
                inJson.put(DeviceNorthIntfConstants.ACCESSNODE, casCadeVO.getCascadeToCode());
                ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
                apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_UPDATE_RECORDPLAN);
                apiInfoDTO.setDeviceCode(planMaxVo.getDeviceCode());
                apiInfoDTO.setInJsonStr(inJson.toJSONString());
                apiInfoDTO.setOrderCode(planMaxVo.getOrderCode());
                apiInfoDTO.setChannelCode(planMaxVo.getChannelCode());
                apiInfoDTO.setUserCode(planMaxVo.getUserCode());
                apiInfoDTO.setTenantId(channelVideoPlanVO.getTenantId());
                // 改为异步调用
                Result<JSONObject> res = videoRecordPlanInfoAsyncService.updateRecordPlan(apiInfoDTO);
                if (ResultCode.SUCCESS.getCode() != res.getCode()) {
                    throw new CustomException(res.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
                }
            }else {
                //新增

                //获取开始时间取最小，终止时间取最大,是否全天录像，是否开启声音，存储最大天数
                ChannelVideoPlanVO planMaxVo = channelVideoPlanMapper.getMaxChannelVideoPlan(channelVideoPlanVO);
                planMaxVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                planMaxVo.setChannelCode(channelVideoPlanVO.getChannelCode());

                //inJson.put(DeviceNorthIntfConstants.TIMESECTIONS,planVo.getTimeSections());
                JSONObject inJson = new JSONObject();

                //如果存在全体存储，那么通道录像计划时间段表不需要整合，取全天数据即可
                if (planMaxVo.getIsAllDay() == 1) {
                    ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                    queryVo.setIsAllDay(1);
                    queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                    queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                    List<ChannelVideoPlanVO> planAllList = channelVideoPlanMapper.getChannelVideoPlanList(queryVo);
                    if (!CollectionUtils.isEmpty(planAllList)) {
                        List<TimeSectionVO> timeSections = channelVideoPlanMapper.getAllTimeSections(planAllList.get(0));
                        planMaxVo.setTimeSections(timeSections);
                        inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, timeSections);
                    }

                } else {//不存在全天存储，那么需要合并时间段

                    List<TimeSectionVO> tmpList = new ArrayList<>();
                    for (int j = 1; j <= 7; ++j) {
                        ChannelVideoPlanVO queryVo = new ChannelVideoPlanVO();
                        queryVo.setDayOfWeek(j);
                        queryVo.setDeviceCode(channelVideoPlanVO.getDeviceCode());
                        queryVo.setChannelCode(channelVideoPlanVO.getChannelCode());
                        List<TimeSectionVO> timeSectionsList = channelVideoPlanMapper.getTimeSectionsList(queryVo);
                        if (!CollectionUtils.isEmpty(timeSectionsList)) {
                            List<TimeBucket> timeBucketList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (TimeSectionVO list : timeSectionsList) {
                                String startTime = DATE_FORMAT + list.getStartTime().substring(0, 2) + ":" + list.getStartTime().substring(2, 4) + ":" + list.getStartTime().substring(4, 6);
                                String endtTime = DATE_FORMAT + list.getEndTime().substring(0, 2) + ":" + list.getEndTime().substring(2, 4) + ":" + list.getEndTime().substring(4, 6);
                                try {
                                    timeBucketList.add(new TimeBucket(sdf.parse(startTime), sdf.parse(endtTime)));


                                } catch (Exception e) {
                                    log.error("时间转换错误：", e);
                                }
                            }

                            List<TimeNode> nodeList = TimeMer.merge(timeBucketList);
                            List<TimeBucket> timeBuckets = TimeMer.convert(nodeList);
                            for (TimeBucket timeBucket : timeBuckets) {
                                TimeSectionVO tmpTS = new TimeSectionVO();
                                tmpTS.setDayOfWeek(j);
                                tmpTS.setStartTime(sdf.format(timeBucket.getStart()).substring(11,13)+sdf.format(timeBucket.getStart()).substring(14,16)+sdf.format(timeBucket.getStart()).substring(17,19));
                                tmpTS.setEndTime(sdf.format(timeBucket.getEnd()).substring(11,13)+sdf.format(timeBucket.getEnd()).substring(14,16)+sdf.format(timeBucket.getEnd()).substring(17,19));
                                tmpList.add(tmpTS);
                            }
                        }
                    }
                    planMaxVo.setTimeSections(tmpList);
                    inJson.put(DeviceNorthIntfConstants.TIMESECTIONS, tmpList);

                }
                String recordPlanName = "录像计划整合-"+String.valueOf(System.currentTimeMillis());
                inJson.put(DeviceNorthIntfConstants.IS_ALL_Day, planMaxVo.getIsAllDay());
                inJson.put(DeviceNorthIntfConstants.STREAM_TYPE, planMaxVo.getStreamType());
                inJson.put(DeviceNorthIntfConstants.STORAGEDAY, planMaxVo.getStorageDay());
                inJson.put(DeviceNorthIntfConstants.ISAUDIO, planMaxVo.getIsAudio());
                inJson.put(DeviceNorthIntfConstants.RECORDPLANNAME, recordPlanName);
                inJson.put(DeviceNorthIntfConstants.RECORDPLANSTATUS, planMaxVo.getRecordPlanStatus());
                inJson.put(DeviceNorthIntfConstants.STARTDATE, planMaxVo.getStartDate());
                inJson.put(DeviceNorthIntfConstants.ENDDATE, planMaxVo.getEndDate());
                inJson.put(DeviceNorthIntfConstants.DEVICECODE, planMaxVo.getDeviceCode());
                inJson.put(DeviceNorthIntfConstants.CHANNELCODE, planMaxVo.getChannelCode());
                //属地化数据统一改造 add by liuxt 20240116
                CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(planMaxVo.getDeviceCode());
                //南向加字段 add by liuxt 20240117
                inJson.put(DeviceNorthIntfConstants.ACCEPTNODE, casCadeVO.getPlatformCode());
                inJson.put(DeviceNorthIntfConstants.ACCESSNODE, casCadeVO.getCascadeToCode());

                ApiInfoDTO apiInfoDTO = new ApiInfoDTO();
                apiInfoDTO.setServiceName(DeviceNorthIntfConstants.SERVICE_NAME_ADD_RECORDPLAN);
                apiInfoDTO.setDeviceCode(planMaxVo.getDeviceCode());
                apiInfoDTO.setInJsonStr(inJson.toJSONString());
                apiInfoDTO.setOrderCode(planMaxVo.getOrderCode());
                apiInfoDTO.setChannelCode(planMaxVo.getChannelCode());
                apiInfoDTO.setUserCode(planMaxVo.getUserCode());
                apiInfoDTO.setTenantId(channelVideoPlanVO.getTenantId());
                // 改为异步调用
                videoRecordPlanInfoAsyncService.saveChannelVideoPlanMerge(apiInfoDTO, planMaxVo, channelVideoPlanVO, recordPlanName);

            }
        }
        log.info(VideoConstants.MERGE_RECORD_PLAN_SYNC_END + System.currentTimeMillis());
    }

	
}

















