package com.chinatower.video.modules.videorecordplan.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.annotation.DeviceOrdAuthScope;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.videopatrolplan.service.IVideoPatrolPlanInfoService;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VideoRecordPlanService
 * @Description 视频通道录像计划服务类
 * @Author chenmf
 * @Date 2022/1/18
 */
@Service
@Slf4j
public class VideoRecordPlanInfoBatchService implements IVideoRecordPlanInfoBatchService {
    @Resource
    private VideoRecordPlanInfoService videoRecordPlanInfoService;
//    @Resource
//    private IVideoRecordPlanInfoAsyncService videoRecordPlanInfoAsyncService;
    @Resource
    private IVideoPatrolPlanInfoService videoPatrolPlanInfoService;
//    @Resource
//    PatrolInfoMapper patrolInfoMapper;
//    @Autowired
//    private IPatrolInfoRepository patrolInfoRepository;
//    @Autowired
//    private SystemFeignClient systemFeignClient;

    @Autowired
    VideoCommonService videoCommonService;

    /**
     * 批量停用/启用通道录像计划(订单调用)
     *
     * @param channelVideoPlanRequestVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchStartEndRecordPlan(ChannelVideoPlanRequestVO channelVideoPlanRequestVO) {
        log.info(VideoConstants.batchStartEndRecordPlan_REQ + JSON.toJSONString(channelVideoPlanRequestVO));
        List<ChannelVideoPlanVO> recordPlanList = channelVideoPlanRequestVO.getRecordPlanList();
        if (!CollectionUtils.isEmpty(recordPlanList)) {
            List<Long> recordPlanIdList = new ArrayList<>();
            for (ChannelVideoPlanVO planVO : recordPlanList) {
                recordPlanIdList.add(planVO.getRecordPlanId());
            }

            BatchDeleteChannelVideoPlanVO delVO = new BatchDeleteChannelVideoPlanVO();
            delVO.setRecordPlanIdList(recordPlanIdList);
            delVO.setTenantId(recordPlanList.get(0).getTenantId());
            delVO.setIndustryCode(channelVideoPlanRequestVO.getIndustryCode());
            //批量停用/启用通道录像计划共同方法
            if (videoRecordPlanInfoService.recordPlanCommon(delVO, channelVideoPlanRequestVO.getUsername(), recordPlanList.get(0).getRecordPlanStatus() + "")) {
                throw new CustomException(VideoConstants.PARAM_DATA_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            log.info(VideoConstants.BATCH_START_END_SUCCESS);
        } else {
            throw new CustomException(VideoConstants.PLAN_NOT_NULL, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     *
     * @param length int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    private static int buildRandom(int length) {
        int num = 1;
        double random = new SecureRandom().nextDouble();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) (random * num);
    }

    /**
     * @Description: 调另一个service的方法，解决事务不管用的问题
     * @Author: wangyt
     * @Date: 2024/3/3
     * @param patrolDTOList
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     **/
    @Override
    public List<Map<String, String>> batchDoPatrol(List<SttPatrolReq> patrolDTOList) {
        List<Map<String, String>> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(patrolDTOList)){
            if(patrolDTOList.get(0).getPatrolType() == VideoConstants.ONE){
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put(VideoConstants.CODE, String.valueOf(VideoConstants.TWO_HUNDRED));
                resultMap.put(VideoConstants.MSG, VideoConstants.PATROL_WAY_STOP_SUCCESS_MSG);
                resultList.add(resultMap);
            }
            for (SttPatrolReq req:patrolDTOList) {
                Map<String, String> resultMap = new HashMap<>();
                try {
                    Result<String> doPatrolResult = videoPatrolPlanInfoService.doPatrol(req);
                    resultMap.put(VideoConstants.CODE, String.valueOf(doPatrolResult.getCode()));
                    if(req.getPatrolType() == VideoConstants.TWO){
                        resultMap.put(VideoConstants.MSG, doPatrolResult.getMsg());
                        if(doPatrolResult.getCode() == VideoConstants.TWO_HUNDRED){
                            String msg = doPatrolResult.getMsg().substring(0,4);
                            if(msg.equals(VideoConstants.START_SUCCESS_MSG)){
                                resultMap.put(VideoConstants.MSG, doPatrolResult.getMsg());
                            }else if(msg.equals(VideoConstants.NOT_IN_TIME_MSG)){
                                resultMap.put(VideoConstants.MSG, req.getPatrolName()+VideoConstants.PATROL_MSG+doPatrolResult.getMsg());
                            }else{
                                resultMap.put(VideoConstants.MSG, VideoConstants.PATROL_MSG+req.getPatrolName()+VideoConstants.PATROL_TYPE_START+VideoConstants.SUCCESS_STATUS_MSG);
                            }
                        }else if(doPatrolResult.getCode() == VideoConstants.TWO_HUNDRED_FOUR){
                            resultMap.put(VideoConstants.MSG, req.getPatrolName()+doPatrolResult.getMsg());
                        }
                        resultList.add(resultMap);
                    }
                }catch (Exception e){
                    log.error(DeviceConstants.PATROL_BATCH_START_END_ERROR +VideoConstants.EQUAL_INFO, e);
                    if(req.getPatrolType() == VideoConstants.TWO){
                        resultMap.put(VideoConstants.CODE, VideoConstants.STRING_400);
                        resultMap.put(VideoConstants.MSG, VideoConstants.PATROL_MSG+req.getPatrolName()+VideoConstants.PATROL_TYPE_START+VideoConstants.FAIL_STATUS_MSG);
                        resultList.add(resultMap);
                    }
                }
            }
        }
        return resultList;
    }
    
    /**
     * 批量停用/启用巡航计划
     */
    @DeviceOrdAuthScope
    @Transactional(rollbackFor = Exception.class)
    public void batchStartEndPatrol(List<SttPatrolReq> patrolDTOList)
    {
    	if( patrolDTOList != null && patrolDTOList.size() > DeviceConstants.ZERO_NUM )
    	{
    		for( SttPatrolReq patrolDTO : patrolDTOList )
    		{
                try{
                    patrolDTO.setSource(Constants.ONE);
                    videoPatrolPlanInfoService.doPatrol(patrolDTO);
                }catch (Exception e){
                    log.info("批量停用/启用巡航计划异常={}={}",patrolDTO.getPatrolId(),e);
                }
            }
    	}
    }
    
//    @DeviceOrdAuthScope
//    @Transactional(rollbackFor = Exception.class)
//    public Result<String> doPatrol(SttPatrolReq patrolDTO) {
//        if (patrolDTO.getPatrolId() == null) {
//            throw new CustomException(DeviceConstants.PATROL_ID_NOT_NULL);
//        }
//        String msg="";
//
//        //查询该巡航路径的巡航时间段
//        List<String> paIdList;
//        QueryWrapper<PatrolInfoDO> patrolQuery = new QueryWrapper<>();
//        patrolQuery.eq("patrol_id", patrolDTO.getPatrolId()).eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
//        List<PatrolInfoDO> patrolList = patrolInfoRepository.list(patrolQuery);
//        if (CollectionUtils.isEmpty(patrolList)) {
//            throw new CustomException(DeviceConstants.PATROL_NOT_EXIST);
//        }
//
//        //获取设备信息
//        String deviceCode = patrolDTO.getDeviceCode();
//        DeviceInfoVO deviceInfoVO = patrolInfoMapper.getDeviceInfo(deviceCode);
//
//        //如果该通道下有其他的巡航计划在巡航,需提示“该巡航仅可在某个时间段进行巡航”
//        if (DeviceConstants.PATROL_PLAN_STATUS_START == patrolDTO.getPatrolType()) {
//            QueryWrapper<PatrolInfoDO> startedPatrolQuery = new QueryWrapper<>();
//            startedPatrolQuery.eq(DeviceTableConstants.DEVICE_CODE, patrolDTO.getDeviceCode()).eq(DeviceTableConstants.CHANNEL_CODE, patrolDTO.getChannelCode());
//            startedPatrolQuery.eq("start_stop", DeviceConstants.PATROL_PLAN_STATUS_START).eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
//            List<PatrolInfoDO> startedPatrolResList = patrolInfoRepository.list(startedPatrolQuery);
//
//            List<String> timeList = new ArrayList<>();
//            //其他巡航计划正在巡航
//            if (!CollectionUtils.isEmpty(startedPatrolResList)) {
//                for (PatrolInfoDO pa : startedPatrolResList) {
//                    timeList.addAll(Arrays.asList(pa.getStartTimePeriod().split(DeviceConstants.COMMA)));
//                }
//                List<String> aLLTimeList = Arrays.asList(DeviceConstants.PATROL_TIME_PERIOD.split(DeviceConstants.COMMA));
//                List<String> leftoverList = aLLTimeList.stream().filter(time -> !timeList.contains(time)).collect(Collectors.toList());
//
//                paIdList = new ArrayList<>(Arrays.asList(patrolList.get(0).getStartTimePeriod().split(DeviceConstants.COMMA)));
//
//                List<String> resList = paIdList.stream().filter(time -> leftoverList.contains(time)).collect(Collectors.toList());
//                //被占用，提示无法启动
//                if (CollectionUtils.isEmpty(resList) && !DeviceConstants.TWO_FLAG.equals(deviceInfoVO.getPartrolMode())) {
//                    return Result.fail(204,DeviceConstants.PATROL_OCCUPY_NOT_START);
//                } else if(listCompareValue(timeList, paIdList) || DeviceConstants.TWO_FLAG.equals(deviceInfoVO.getPartrolMode())){
//                    // 如果互不影响则提示成功
//                } else {
//                    //更新巡航计划为【启动】，并提示可巡航时间段
//                    //更新预约启动状态字段
//                    StringBuilder sb = new StringBuilder("启动成功，该巡航计划在");
//                    paIdList.retainAll(timeList);
//                    sb = appendMsg(sb, paIdList);
//                    sb.append("已被占用，仅可在");
//                    sb = appendMsg(sb, resList);
//                    sb.append("执行该计划。");
//                    msg = sb.toString();
//                    PatrolInfoDO uptPatrol = new PatrolInfoDO();
//                    uptPatrol.setPatrolId(patrolDTO.getPatrolId());
//                    uptPatrol.setStartStop(patrolDTO.getPatrolType() + "");
//                    /*
//                    如果巡航启用成功，且部分时间被占用，只能在可用时间巡航的话，需要在启用成功时更新一下这个巡航的巡航时间
//                    巡航计划为08:00～24:00，启动巡航计划时提示：“启动成功，该巡航计划在08:00～09:00，10:00～11:00已被占用，
//                    仅可在09:00～10:00，11:00～24:00执行该计划。”
//                     */
//                    String newStartTimePeriod = resList.stream().map(String::valueOf).collect(Collectors.joining(","));
//                    uptPatrol.setStartTimePeriod(newStartTimePeriod);
//                    patrolInfoRepository.updatePatrolStartTime(uptPatrol);
//
//                }
//            }
//        }
//
//        //判断当前时间是否在启动时间段内
//        if (StringUtils.isNotBlank(patrolList.get(0).getStartTimePeriod())) {
//            String[] startTimePeriodArr = patrolList.get(0).getStartTimePeriod().split(DeviceConstants.COMMA);
//            //获取真实的巡航时段
//            String hour = videoCommonService.getActualHour();
//            //不在时间段内，更新巡航计划为【启动】，等待定时任务执行
//            if (!Arrays.asList(startTimePeriodArr).contains(hour)) {
//                if (DeviceConstants.PATROL_PLAN_STATUS_START == patrolDTO.getPatrolType()) {
//                    msg = DeviceConstants.PATROL_NOT_ARRIVE_START_TIME;
//                }
//            }
//        }
//
//        PatrolInfoDO uptPatrol = new PatrolInfoDO();
//        uptPatrol.setPatrolId(patrolDTO.getPatrolId());
//        uptPatrol.setStartStop(patrolDTO.getPatrolType() + "");
//        patrolInfoRepository.updatePatrolStartTime(uptPatrol);
//
//        //整合巡航计划
//        PatrolInfoVO vo = new PatrolInfoVO();
//        vo.setDeviceCode(patrolDTO.getDeviceCode());
//        vo.setChannelCode(patrolDTO.getChannelCode());
//        vo.setPatrolId(patrolDTO.getPatrolId());
//        this.mergePatrol(vo);
//
//        //如果是停用操作，将实际执行时间端，还原为设置的时间段
//        if (DeviceConstants.PATROL_PLAN_STATUS_STOP == patrolDTO.getPatrolType()) {
//            List<PatrolInfoDO> list = new ArrayList<>();
//            PatrolInfoDO patrolInfoDO = new PatrolInfoDO();
//            patrolInfoDO.setPatrolId(patrolList.get(0).getPatrolId());
//            patrolInfoDO.setStartTimePeriod(patrolList.get(0).getInitStartTimePeriod());
//            list.add(patrolInfoDO);
//            patrolInfoMapper.updatePatrolStartTimeBatch(list);
//        }
//
//        if(com.chinatower.platform.common.utils.StringUtils.isNotEmpty(msg)){
//            return Result.success(msg);
//        }else {
//            return Result.success("停用/启用巡航路径成功!");
//        }
//    }


    /**
     * 批量停用/启用巡航计划---整合巡航路径
     * @param patrolInfoVO
     * @return
     */
//    @Transactional(rollbackFor = Exception.class)
//    public void mergePatrol(PatrolInfoVO patrolInfoVO) {
//        log.info(VideoConstants.ID2+patrolInfoVO.getPatrolId()+VideoConstants.DEVICE+ patrolInfoVO.getDeviceCode() + "：整合开始！");
//        String deviceCode = patrolInfoVO.getDeviceCode();
//        //1.根据设备编码获取设备信息，以及巡航配置partrol_mode 1:按时间段整合 2:按预置位整合'
//        DeviceInfoVO deviceInfoVO = patrolInfoMapper.getDeviceInfo(deviceCode);
//
//        //获取来源节点编码,即本节点
//        AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.PLATFORM_CODE);
//        if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
//            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
//            if(!org.springframework.util.ObjectUtils.isEmpty(sysConfig)){
//                deviceInfoVO.setPlatformCode(sysConfig.getConfigValue());
//            }
//        }
//
//        //获取真实的巡航时段
//        String hour = videoCommonService.getActualHour();
//        patrolInfoVO.setStartTimePeriod(hour);
//
//        boolean updatePatrolSync = true;
//        if(!ObjectUtils.isEmpty(deviceInfoVO)){
//            //2.通过设备编码、通道编码查vm_patrol_info_t表中是否包含当前时间段且已预约启动的所有巡航计划
//            List<PatrolInfoVO> patrolList = patrolInfoMapper.getPatrolInfoList(patrolInfoVO);
//            //2.1 不存在包含当前时间段且已预约启动的所有巡航计划
//            if(patrolList.size()==0){
//                //2.1.1 根据设备编码、通道编码查vm_patrol_info_merge_t表，是否存在整合巡航计划
//                List<PatrolInfoVO> patrolMergeList = patrolInfoMapper.getPatrolMergeListById(patrolInfoVO);
//                //2.1.2 若存在，把整合的巡航计划停止并删除；不存在，则不做处理
//                if(!CollectionUtils.isEmpty(patrolMergeList) && patrolMergeList.size() > 0) {
//                    PatrolInfoVO queryPatrolInfoVO = patrolMergeList.get(0);
//                    queryPatrolInfoVO.setIndustryCode(patrolInfoVO.getIndustryCode());
//                    //删除巡航计划整合表
//                    patrolInfoMapper.deletePatrolMerge(queryPatrolInfoVO);
//                    //删除巡航计划预置点关系表
//                    patrolInfoMapper.deletePatrolPresetMerge(queryPatrolInfoVO);
//                    //调用基础子平台，6.32.查询巡航路径接口；并判断巡航路径状态，若该巡航计划正在启动中，先停用巡航计划
//                    Result<JSONObject> mergeQueryAndDoPatrolRes = videoRecordPlanInfoAsyncService.mergeQueryAndDoPatrol(queryPatrolInfoVO, deviceInfoVO);
//                    if (ResultCode.SUCCESS.getCode() != mergeQueryAndDoPatrolRes.getCode()) {
//                        //add by liuxt 增加基础报错信息记录 start
//                        try{
//                            BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                            vo.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
//                            vo.setChannelCode(queryPatrolInfoVO.getChannelCode());
//                            vo.setKeywords(DeviceConstants.PATROL_ID);
//                            vo.setKeywordsValue(queryPatrolInfoVO.getPatrolId().toString());
//                            vo.setRequest(JSONObject.toJSONString(queryPatrolInfoVO));
//                            vo.setResponse(JSONObject.toJSONString(mergeQueryAndDoPatrolRes));
//                            vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                            vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                            videoCommonService.saveBaseErrorRecord(vo);
//                        }catch (Exception e){
//                            log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//                        }
//                        //add by liuxt 增加基础报错信息记录 end
//                        throw new CustomException(mergeQueryAndDoPatrolRes.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
//                    }
//                    //调用基础子平台，6.29.删除巡航路径接口
//                    Result<JSONObject> mergeDelPatrolRes = videoRecordPlanInfoAsyncService.mergeDelPatrol(queryPatrolInfoVO, deviceInfoVO);
//                    if (ResultCode.SUCCESS.getCode() != mergeDelPatrolRes.getCode()) {
//                        //add by liuxt 增加基础报错信息记录 start
//                        try{
//                            BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                            vo.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
//                            vo.setChannelCode(queryPatrolInfoVO.getChannelCode());
//                            vo.setKeywords(DeviceConstants.PATROL_ID);
//                            vo.setKeywordsValue(queryPatrolInfoVO.getPatrolId().toString());
//                            vo.setRequest(JSONObject.toJSONString(queryPatrolInfoVO));
//                            vo.setResponse(JSONObject.toJSONString(mergeDelPatrolRes));
//                            vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                            vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                            videoCommonService.saveBaseErrorRecord(vo);
//                        }catch (Exception e){
//                            log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//                        }
//                        //add by liuxt 增加基础报错信息记录 end
//                        throw new CustomException(mergeDelPatrolRes.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
//                    }
//                }
//            } else {
//                //2.2 存在包含当前时间段且已预约启动的所有巡航计划
//                String[] startTimePeriodArr = patrolList.get(0).getStartTimePeriod().split(DeviceConstants.COMMA);
//
//                //2.2.1 根据设备编码、通道编码查vm_patrol_info_merge_t表，是否存在整合且已预约启动的巡航计划
//                List<PatrolInfoVO> patrolMergeList = patrolInfoMapper.getPatrolMergeList(patrolInfoVO);
//                //2.2.1.1 存在整合且已预约启动的巡航计划,做更新操作。
//                if(patrolMergeList != null && patrolMergeList.size() >0 ) {
//                    PatrolInfoVO updatePatrolInfo = patrolList.get(0);
//                    PatrolInfoVO potrolMergeInfoVO = patrolMergeList.get(0);
//                    potrolMergeInfoVO.setIndustryCode(patrolInfoVO.getIndustryCode());
//                    //判断是否需要执行更新
//                    // 1 当前已启动的巡航计划与本次更新的巡航计划是同一个，且更新的巡航计划的修改时间大于巡航计划的启动时间，则需要做处理。a.after(b):a在b之后为true；
//                    // 2 当前已启动的巡航计划与本次更新的巡航计划不是同一个
//                    //如果整合表没有正在执行的计划，或者执行的计划与整合计划是同一个，则更新
//                    if ((Constants.ONE).equals(potrolMergeInfoVO.getIfCruise())
//                            || potrolMergeInfoVO.getSourcePatrolId().indexOf(updatePatrolInfo.getPatrolId().toString())!=-1
//                            || Arrays.asList(startTimePeriodArr).contains(hour) ) {
//                        PatrolInfoVO patrolInfoVOTemp = new PatrolInfoVO();
//                        patrolInfoVOTemp.setDeviceCode(patrolInfoVO.getDeviceCode());
//                        patrolInfoVOTemp.setChannelCode(patrolInfoVO.getChannelCode());
//                        patrolInfoVOTemp.setStartTimePeriod(hour);
//
//                        //更新巡航计划整合表
//                        PatrolInfoVO uptPatrolInfo = new PatrolInfoVO();
//                        BeanUtils.copyProperties(updatePatrolInfo, uptPatrolInfo);
//                        uptPatrolInfo.setPatrolId(potrolMergeInfoVO.getPatrolId());
//                        uptPatrolInfo.setPatrolName(potrolMergeInfoVO.getPatrolName());
//
//                        //按照1按时间整合，sourcePatrolId为巡航基础表中的patrolId；
//                        // 2:按预置位整合，sourcePatrolId为所有巡航基础表中的patrolId拼接而成
//                        PatrolInfoVO patrolHourInfo = videoRecordPlanInfoAsyncService.getPatrolPresetInfo(patrolList);
//                        if (!ObjectUtils.isEmpty(patrolHourInfo)) {
//                            uptPatrolInfo.setStartTimePeriod(patrolHourInfo.getStartTimePeriod());
//                            uptPatrolInfo.setSourcePatrolId(patrolHourInfo.getSourcePatrolId());
//                            updatePatrolInfo.setPatrolIdList(patrolHourInfo.getPatrolIdList());
//                        }
//                        //更新整合表
//                        patrolInfoMapper.updatePatrolMerge(uptPatrolInfo);
//                        //先删再增关联表
//                        patrolInfoMapper.deletePatrolPresetMerge(uptPatrolInfo);
//                        //查询预置位信息，并将预置位集合重新排序，按照预约时间的先后顺序
//                        List<AddPatrolPresetReq> presetVOList = videoRecordPlanInfoAsyncService.patrolPresetHandle(updatePatrolInfo);
//                        //批量插入巡航计划预置点位关系整合表
//                        videoRecordPlanInfoAsyncService.batchSavePatrolPresetMerge(potrolMergeInfoVO, presetVOList);
//
//                        //调用基础子平台，6.32.查询巡航路径接口；并判断巡航路径状态，若该巡航计划正在启动中，先停用巡航计划
//                        Result<JSONObject> mergeQueryAndDoPatrolRes = videoRecordPlanInfoAsyncService.mergeQueryAndDoPatrol(potrolMergeInfoVO, deviceInfoVO);
//                        if (ResultCode.SUCCESS.getCode() != mergeQueryAndDoPatrolRes.getCode()) {
//                            //add by liuxt 增加基础报错信息记录 start
//                            try{
//                                BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                                vo.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
//                                vo.setChannelCode(potrolMergeInfoVO.getChannelCode());
//                                vo.setKeywords(DeviceConstants.PATROL_ID);
//                                vo.setKeywordsValue(potrolMergeInfoVO.getPatrolId().toString());
//                                vo.setRequest(JSONObject.toJSONString(potrolMergeInfoVO));
//                                vo.setResponse(JSONObject.toJSONString(mergeQueryAndDoPatrolRes));
//                                vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                                videoCommonService.saveBaseErrorRecord(vo);
//                            }catch (Exception e){
//                                log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//                            }
//                            //add by liuxt 增加基础报错信息记录 end
//                            throw new CustomException(mergeQueryAndDoPatrolRes.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
//                        }
//
//                        //调用基础子平台，6.31.修改巡航路径接口
//                        Result<JSONObject> mergeUpdatePatrolRes = videoRecordPlanInfoAsyncService.mergeUpdatePatrol(potrolMergeInfoVO, presetVOList, updatePatrolInfo, deviceInfoVO);
//                        if (ResultCode.SUCCESS.getCode() != mergeUpdatePatrolRes.getCode()) {
//                            //add by liuxt 增加基础报错信息记录 start
//                            try{
//                                BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                                vo.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
//                                vo.setChannelCode(potrolMergeInfoVO.getChannelCode());
//                                vo.setKeywords(DeviceConstants.PATROL_ID);
//                                vo.setKeywordsValue(potrolMergeInfoVO.getPatrolId().toString());
//                                vo.setRequest(JSONObject.toJSONString(potrolMergeInfoVO));
//                                vo.setResponse(JSONObject.toJSONString(mergeUpdatePatrolRes));
//                                vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                                videoCommonService.saveBaseErrorRecord(vo);
//                            }catch (Exception e){
//                                log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//                            }
//                            //add by liuxt 增加基础报错信息记录 end
//                            throw new CustomException(mergeUpdatePatrolRes.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
//                        }else {
//                            //如在时间段内，启动巡航，反之，则等待定时任务执行
//                            if (Arrays.asList(startTimePeriodArr).contains(hour)) {
//                                //调用基础子平台，6.33.启用/停用巡航路径接口
//                                Result<JSONObject> mergeDoPatrolRes = videoRecordPlanInfoAsyncService.mergeDoPatrol(potrolMergeInfoVO.getPatrolId(), updatePatrolInfo, deviceInfoVO);
//                                if (ResultCode.SUCCESS.getCode() != mergeDoPatrolRes.getCode()) {
//                                    //add by liuxt 增加基础报错信息记录 start
//                                    try{
//                                        BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                                        vo.setDeviceCode(updatePatrolInfo.getDeviceCode());
//                                        vo.setChannelCode(updatePatrolInfo.getChannelCode());
//                                        vo.setKeywords(DeviceConstants.PATROL_ID);
//                                        vo.setKeywordsValue(potrolMergeInfoVO.getPatrolId().toString());
//                                        vo.setRequest(JSONObject.toJSONString(updatePatrolInfo));
//                                        vo.setResponse(JSONObject.toJSONString(mergeDoPatrolRes));
//                                        vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                                        vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                                        videoCommonService.saveBaseErrorRecord(vo);
//                                    }catch (Exception e){
//                                        log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//                                    }
//                                    //add by liuxt 增加基础报错信息记录 end
//                                    throw new CustomException(mergeDoPatrolRes.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
//                                } else {
//                                    //启动巡航计划成功之后，将整合表巡航状态更新为启动
//                                    PatrolInfoVO uptPatrolInfo1 = new PatrolInfoVO();
//                                    uptPatrolInfo1.setPatrolId(potrolMergeInfoVO.getPatrolId());
//                                    uptPatrolInfo1.setIfCruise(Constants.TWO);
//                                    uptPatrolInfo1.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
//                                    patrolInfoMapper.updatePatrolMerge(uptPatrolInfo1);
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    //2.2.1.2 不存在整合且已预约启动的巡航计划,做新增操作。
//                    updatePatrolSync = false;
//                    PatrolInfoVO insertPatrolInfoVO = patrolList.get(0);
//                    AddPatrolReq addPatrolDTO = new AddPatrolReq();
//                    String patrolName = VideoConstants.MERGE_PATROL_NAME + System.currentTimeMillis()+ buildRandom(8);
//                    addPatrolDTO.setPatrolName(patrolName);
//                    //按照1按时间整合，sourcePatrolId为巡航基础表中的patrolId；
//                    // 2:按预置位整合，sourcePatrolId为所有巡航基础表中的patrolId拼接而成
//                    PatrolInfoVO patrolHourInfo = videoRecordPlanInfoAsyncService.getPatrolPresetInfo(patrolList);
//                    if (!ObjectUtils.isEmpty(patrolHourInfo)) {
//                        insertPatrolInfoVO.setStartTimePeriod(patrolHourInfo.getStartTimePeriod());
//                        insertPatrolInfoVO.setSourcePatrolId(patrolHourInfo.getSourcePatrolId());
//                        insertPatrolInfoVO.setPatrolIdList(patrolHourInfo.getPatrolIdList());
//                    }
//                    //查询预置位信息，并将预置位集合重新排序，按照预约时间的先后顺序
//                    List<AddPatrolPresetReq> presetVOList = videoRecordPlanInfoAsyncService.patrolPresetHandle(insertPatrolInfoVO);
//                    addPatrolDTO.setPresets(presetVOList);
//
//                    //调用基础子平台，6.28.增加巡航路径接口
//                    videoRecordPlanInfoAsyncService.mergeAddCruisePatrol(addPatrolDTO,insertPatrolInfoVO,patrolName,presetVOList,startTimePeriodArr,hour,patrolInfoVO,deviceInfoVO);
//                }
//            }
//        }
//        if(updatePatrolSync){
//            videoRecordPlanInfoAsyncService.updatePatrolInfo(patrolInfoVO);
//        }
//    }
    
    /**
     * 比较两个list有没有相同的值
     *
     * @return
     * @author wangyt
     * @date 2022/9/16
     * @version
     */
    private boolean listCompareValue(List<String> list1, List<String> list2){
        boolean flag = true;
        compare : for (String value1:list1) {
            for (String value2:list2) {
                if(value1.equals(value2)){
                    flag = false;
                    break compare;
                }
            }
        }
        return flag;
    }

    /**
     * 编辑返回信息
     *
     * @return
     * @author wangyt
     * @date 2022/9/16
     * @version
     */
    private StringBuilder appendMsg(StringBuilder sb, List<String> dataList){
        if(dataList.size() == 1){
            sb.append(dataList.get(0)).append(":00").append("~").append(Integer.valueOf(dataList.get(0))+1).append(":00");
        }else{
            for(int i=0; i<=dataList.size()-1; i++){
                if(i == 0){
                    sb.append(Integer.valueOf(dataList.get(i))).append(":00").append("~");
                }
                if(i+1 == dataList.size()){
                    sb.append(Integer.valueOf(dataList.get(i))+1).append(":00");
                }else{
                    Integer begin = Integer.valueOf(dataList.get(i));
                    Integer end = Integer.valueOf(dataList.get(i+1));
                    if(end - begin > 1){
                        sb.append(Integer.valueOf(dataList.get(i))+1).append(":00").append(",");
                        sb.append(dataList.get(i+1)).append(":00").append("~");
                    }
                }
            }
        }
        return sb;
    }


}












