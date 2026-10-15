package com.chinatower.video.modules.videorecordplan.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.video.api.entity.AddPatrolPresetReq;
import com.chinatower.video.api.entity.AddPatrolReq;
import com.chinatower.video.api.entity.ChannelVideoPlanVO;
import com.chinatower.video.api.entity.PatrolInfoVO;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;

public interface IVideoRecordPlanInfoAsyncService {
	
	void saveChannelVideoPlanMerge(ApiInfoDTO apiInfoDTO, ChannelVideoPlanVO planMaxVo,
                                   ChannelVideoPlanVO channelVideoPlanVO, String recordPlanName);
	
//	void updatePatrolInfo(PatrolInfoVO patrolInfoVO);

    Result<JSONObject> deleteRecordPlan(ApiInfoDTO apiInfoDTO);

    Result<JSONObject> updateRecordPlan(ApiInfoDTO apiInfoDTO);

//    void mergeAddCruisePatrol(AddPatrolReq addPatrolDTO, PatrolInfoVO insertPatrolInfoVO, String patrolName,
//              List<AddPatrolPresetReq> presetVOList, String[] startTimePeriodArr,String hour,PatrolInfoVO patrolInfoVO, DeviceInfoVO deviceInfoVO);
//    Result<JSONObject> mergeQueryAndDoPatrol(PatrolInfoVO queryPatrolInfoVO, DeviceInfoVO deviceInfoVO);
//
//    Result<JSONObject> mergeDoPatrol(Long patrolId, PatrolInfoVO updatePatrolInfo, DeviceInfoVO deviceInfoVO);
//
//    Result<JSONObject> mergeDelPatrol(PatrolInfoVO queryPatrolInfoVO, DeviceInfoVO deviceInfoVO);
//
//    PatrolInfoVO getPatrolPresetInfo(List<PatrolInfoVO> listData);
//
//    List<AddPatrolPresetReq> patrolPresetHandle(PatrolInfoVO updatePatrolInfo);
//
//    void batchSavePatrolPresetMerge(PatrolInfoVO potrolMergeInfoVO, List<AddPatrolPresetReq> presetVOList);
//
//    Result<JSONObject> mergeUpdatePatrol(PatrolInfoVO potrolMergeInfoVO, List<AddPatrolPresetReq> presetVOList,
//                           PatrolInfoVO updatePatrolInfo, DeviceInfoVO deviceInfoVO);

}
