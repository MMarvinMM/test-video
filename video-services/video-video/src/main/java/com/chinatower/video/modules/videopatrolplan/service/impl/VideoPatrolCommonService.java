package com.chinatower.video.modules.videopatrolplan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.constants.IndustryConstants;
import com.chinatower.video.modules.query.service.VideoQueryService;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName VideoRecordPlanService
 * @Description 视频通道巡航计划服务共通类
 * @Author chenmf
 * @Date 2022/1/18
 */
@Service
@Slf4j
public class VideoPatrolCommonService {
    @Autowired
    private ApiInfoUtils apiInfoUtils;
    @Autowired
    private VideoQueryService videoQueryService;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Resource
    PatrolInfoMapper patrolInfoMapper;
    @Resource
    private NetConnectUtils connectUtils;
    @Autowired
    VideoCommonService videoCommonService;
    @Autowired
    VideoControlService videoControlService;
    @Autowired
    VideoPatrolPlanInfoService videoPatrolPlanInfoService;

    /**
     * 将获取列表中的巡航计划ID整合成一个字符串，用逗号分隔；
     * 并取第一个巡航的时间段，做为整合巡航计划的时间段
     * @param
       */
    public PatrolInfoVO getPatrolPresetInfo(List<PatrolInfoVO> listData) {
        PatrolInfoVO patrolInfoVO = new PatrolInfoVO();
        //判断入参list是否为空
        if (CollectionUtils.isEmpty(listData)) {
            return patrolInfoVO;
        }

        String startTimePeriod = listData.get(0).getStartTimePeriod();
        String sourcePatrolId = listData.get(0).getPatrolId() + "";
        for (int i = 1; i < listData.size(); i++) {
            sourcePatrolId += VideoConstants.COMMA + listData.get(i).getPatrolId();
        }

        patrolInfoVO.setPatrolIdList(Arrays.asList(sourcePatrolId.split(VideoConstants.COMMA)));
        patrolInfoVO.setSourcePatrolId(sourcePatrolId);
        patrolInfoVO.setStartTimePeriod(startTimePeriod);
        return patrolInfoVO;
    }


    /**
     * 功能描述: 调用基础子平台，6.28.	增加巡航路径接口
     * @param addPatrolDTO,insertPatrolInfoVO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public Result<JSONObject> mergeAddCruisePatrol(AddPatrolReq addPatrolDTO, PatrolInfoVO insertPatrolInfoVO, DeviceInfoVO deviceInfoVO) {
        Result<JSONObject> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.ADD_PATROL_ERROR);
        //调南向接口，新增巡航路径接口
        try {
            NetConnectUtils.RequestParamter<AddPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by wangyt 20240118
            addPatrolDTO.setAcceptNode(deviceInfoVO.getPlatformCode());
            addPatrolDTO.setAccessNode(deviceInfoVO.getAccessNode());
            paramter.setBody(addPatrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ADD_CRUISE_PATROL);
            path.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
            path.setChannelCode(insertPatrolInfoVO.getChannelCode());
            paramter.setPath(path);
            log.info(VideoConstants.ADD_CRUISE_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.ADD_CRUISE_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.ADD_CRUISE_PATROL_ERROR +VideoConstants.EQUAL_INFO, e);
        }
        return res;
    }

    /**
     * 功能描述: 调用基础子平台，6.29.	删除巡航路径接口
     * @param queryPatrolInfoVO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public Result<JSONObject> mergeDelPatrol(PatrolInfoVO queryPatrolInfoVO, DeviceInfoVO deviceInfoVO) {
        Result<JSONObject> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.DELETE_PATROL_ERROR);
        // 调用统一网关接口，删除巡航路径
        SttPatrolReq delPatrolDTO = new SttPatrolReq();
        delPatrolDTO.setPatrolId(queryPatrolInfoVO.getPatrolId());
        delPatrolDTO.setPatrolType(1);
        try {
            NetConnectUtils.RequestParamter<SttPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by wangyt 20240118
            delPatrolDTO.setAcceptNode(deviceInfoVO.getPlatformCode());
            delPatrolDTO.setAccessNode(deviceInfoVO.getAccessNode());
            paramter.setBody(delPatrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DEL_PATROL);
            path.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
            path.setChannelCode(queryPatrolInfoVO.getChannelCode());
            paramter.setPath(path);
            log.info(VideoConstants.DEL_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DEL_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.DEL_PATROL_ERROR +VideoConstants.EQUAL_INFO, e);
        }
        return res;
    }


    /**
     * 功能描述: 调用基础子平台，6.31.	修改巡航路径接口
     * @param potrolMergeInfoVO,presetVOList,updatePatrolInfo
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public Result<JSONObject> mergeUpdatePatrol(PatrolInfoVO potrolMergeInfoVO,List<AddPatrolPresetReq> presetVOList, PatrolInfoVO updatePatrolInfo, DeviceInfoVO deviceInfoVO) {
        Result<JSONObject> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.UPT_PATROL_ERROR);
        //调南向接口，修改巡航路径接口
        ModPatrolReq updatePatrolDTO = new ModPatrolReq();
        updatePatrolDTO.setPatrolId(potrolMergeInfoVO.getPatrolId());
        updatePatrolDTO.setPatrolName(potrolMergeInfoVO.getPatrolName());
        updatePatrolDTO.setPresets(presetVOList);
        try {
            NetConnectUtils.RequestParamter<ModPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by wangyt 20240118
            updatePatrolDTO.setAcceptNode(deviceInfoVO.getPlatformCode());
            updatePatrolDTO.setAccessNode(deviceInfoVO.getAccessNode());
            paramter.setBody(updatePatrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UPDATE_CRUISE_PATROL);
            path.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
            path.setChannelCode(potrolMergeInfoVO.getChannelCode());
            paramter.setPath(path);
            log.info(VideoConstants.UPDATE_CRUISE_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.UPDATE_CRUISE_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.UPDATE_CRUISE_PATROL_ERROR +VideoConstants.EQUAL_INFO, e);
        }
        return res;
    }


    /**
     * 功能描述: 调用基础子平台，6.32.	查询巡航路径接口
     *  并判断巡航路径状态，若该巡航计划正在启动中，先停用巡航计划
     * @param queryPatrolInfoVO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public Result<JSONObject> mergeQueryAndDoPatrol(PatrolInfoVO queryPatrolInfoVO, DeviceInfoVO deviceInfoVO) {
        Result<JSONObject> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_PATROL_ERRO);
        VideoQueryPatrolData videoQueryPatrolData = new VideoQueryPatrolData();
        videoQueryPatrolData.setPatrolId(queryPatrolInfoVO.getPatrolId());
        videoQueryPatrolData.setChannelCode(queryPatrolInfoVO.getChannelCode());
        videoQueryPatrolData.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
        videoQueryPatrolData.setTenantId(queryPatrolInfoVO.getTenantId());
        videoQueryPatrolData.setIndustryCode(queryPatrolInfoVO.getIndustryCode());
        //调南向，查询巡航信息接口，queryPatrol
        Result<List<QueryPatrolData>> patrolRes = videoQueryService.queryPatrolNorth(videoQueryPatrolData, deviceInfoVO);
        res.setCode(patrolRes.getCode());
        res.setMsg(patrolRes.getMsg());

        //先判断巡航路径状态
        if (ResultCode.SUCCESS.getCode() == patrolRes.getCode()) {
            List<QueryPatrolData> list = patrolRes.getData();
            if (!CollectionUtils.isEmpty(list)) {
                for (QueryPatrolData queryPatrolData : list) {
                    int isCruise = queryPatrolData.getIsCruise();
                    if (IndustryConstants.CRUISE_START == isCruise
                            && queryPatrolInfoVO.getPatrolId().longValue() == queryPatrolData.getPatrolId()) {
                        //该巡航计划正在启动中，先停用巡航计划
                        SttPatrolReq stopPatrolDTO = new SttPatrolReq();
                        stopPatrolDTO.setPatrolId(queryPatrolInfoVO.getPatrolId());
                        stopPatrolDTO.setPatrolType(1);
                        try {
                            NetConnectUtils.RequestParamter<SttPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
                            //属地化数据统一改造 add by wangyt 20240118
                            stopPatrolDTO.setAcceptNode(deviceInfoVO.getPlatformCode());
                            stopPatrolDTO.setAccessNode(deviceInfoVO.getAccessNode());
                            paramter.setBody(stopPatrolDTO);
                            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_PATROL);
                            path.setDeviceCode(queryPatrolInfoVO.getDeviceCode());
                            path.setChannelCode(queryPatrolInfoVO.getChannelCode());
                            paramter.setPath(path);
                            log.info(VideoConstants.DOPATROL_OP + VideoConstants.REQUEST_LOG + paramter);
                            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                            log.info(VideoConstants.DOPATROL_OP + VideoConstants.RESPONSE_LOG + cr);
                            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
                            res = apiInfoUtils.transResult(cr, respDictData.getData());
                        }catch (Exception e){
                            log.error(VideoConstants.DOPATROL_ERROR +VideoConstants.EQUAL_INFO, e);
                        }
                    }
                }
            }
        }
        return res;
    }


    /**
     * 功能描述: 调用基础子平台，6.33.	启用/停用巡航路径接口
     * @param patrolId,updatePatrolInfo
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public Result<JSONObject> mergeDoPatrol(Long patrolId, PatrolInfoVO updatePatrolInfo, DeviceInfoVO deviceInfoVO) {
        Result<JSONObject> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.DO_PATROL_ERROR);
        //调南向接口，启动巡航计划接口
        SttPatrolReq stopPatrolDTO = new SttPatrolReq();
        stopPatrolDTO.setPatrolId(patrolId);
        stopPatrolDTO.setPatrolType(2);
        try {
            NetConnectUtils.RequestParamter<SttPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by wangyt 20240118
            stopPatrolDTO.setAcceptNode(deviceInfoVO.getPlatformCode());
            stopPatrolDTO.setAccessNode(deviceInfoVO.getAccessNode());
            paramter.setBody(stopPatrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DO_PATROL);
            path.setDeviceCode(updatePatrolInfo.getDeviceCode());
            path.setChannelCode(updatePatrolInfo.getChannelCode());
            paramter.setPath(path);
            log.info(VideoConstants.DOPATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DOPATROL_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        }catch (Exception e){
            log.error(VideoConstants.DOPATROL_ERROR +VideoConstants.EQUAL_INFO, e);
        }
        return res;
    }

    /**
     * 功能描述: 查询预置位信息，并将预置位集合重新排序，按照预约时间的先后顺序
     * @param updatePatrolInfo
     * @return:
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public List<AddPatrolPresetReq> patrolPresetHandle(PatrolInfoVO updatePatrolInfo) {
        List<AddPatrolPresetReq> newPresetVOList = new ArrayList<>();
        //1:按时间段整合只会存在一个巡航计划；2:按预置位整合可能存在多个巡航计划
        List<AddPatrolPresetReq> presetVOList = patrolInfoMapper.getPatrolPresetList(updatePatrolInfo);

        List<String> operatorList = presetVOList.stream().map(AddPatrolPresetReq::getOperator).collect(Collectors.toList());
        //查询已启动状态的巡航操作人id
        Map<String, String> userMap = videoPatrolPlanInfoService.getUserMap(operatorList);

        presetVOList.sort(new Comparator<AddPatrolPresetReq>() {
            @Override
            public int compare(AddPatrolPresetReq sj, AddPatrolPresetReq sj1) {
                if (videoControlService.ifPriority(sj.getDeviceCode(), sj.getChannelCode(), sj.getTenantId(), sj.getIndustryCode(),
                        userMap.get(sj.getOperator()), userMap.get(sj1.getOperator()), Constants.THREE)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        //批量插入巡航计划预置点位关系整合表
        if (!CollectionUtils.isEmpty(presetVOList)) {
            log.info("排序后预置位，{}",JSON.toJSONString(presetVOList));
            int sort = 1;
            for (AddPatrolPresetReq addPatrolPresetReq : presetVOList) {
                List<AddPatrolPresetReq> list = addPatrolPresetReq.getList();
                for (AddPatrolPresetReq patrolPresetReq : list) {
                    patrolPresetReq.setSort(sort++);
                    newPresetVOList.add(patrolPresetReq);
                }
            }
        }
        return newPresetVOList;
    }

    /**
     * 功能描述: 批量插入巡航计划预置点位关系整合表
     * @param potrolMergeInfoVO,presetVOList
     * @return:
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
    public void batchSavePatrolPresetMerge(PatrolInfoVO potrolMergeInfoVO, List<AddPatrolPresetReq> presetVOList) {
        //批量插入巡航计划预置点位关系整合表
        if (!CollectionUtils.isEmpty(presetVOList)) {
            List<PresetDTO> presetRelaMergeList = new ArrayList<>();
            for (int j = 0; j < presetVOList.size(); j++) {
                AddPatrolPresetReq addPatrolPresetReq = presetVOList.get(j);
                PresetDTO presetDTO = new PresetDTO();
                presetDTO.setPatrolId(potrolMergeInfoVO.getPatrolId());
                presetDTO.setDeviceCode(potrolMergeInfoVO.getDeviceCode());
                presetDTO.setPresetId(addPatrolPresetReq.getPresetId().longValue());
                presetDTO.setStaySeconds(addPatrolPresetReq.getStaySeconds());
                presetDTO.setSpeed(addPatrolPresetReq.getSpeed());
                presetDTO.setSort(j+1);
                presetDTO.setDeleteFlag(Integer.valueOf(DeviceConstants.DELETE_FLAG_NORMAL));
                presetRelaMergeList.add(presetDTO);
            }
            //批量新增
            patrolInfoMapper.batchSavePatrolPresetMerge(presetRelaMergeList);
        }
    }


    /**
     * 删除巡航计划
     * @param patrolInfoVO
     */
    public void deletePatrol(PatrolInfoVO patrolInfoVO, DeviceInfoVO deviceInfoVO) {
        log.info("删除巡航计划：" + JSON.toJSONString(patrolInfoVO));
        SttPatrolReq delPatrolDTO = new SttPatrolReq();
        delPatrolDTO.setPatrolId(patrolInfoVO.getPatrolId());
        delPatrolDTO.setPatrolType(1);
        try {
            NetConnectUtils.RequestParamter<SttPatrolReq> paramter = new NetConnectUtils.RequestParamter<>();
            //属地化数据统一改造 add by wangyt 20240118
            delPatrolDTO.setAcceptNode(deviceInfoVO.getPlatformCode());
            delPatrolDTO.setAccessNode(deviceInfoVO.getAccessNode());
            // 请求参数
            paramter.setBody(delPatrolDTO);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DEL_PATROL);
            // 接口路径参数
            path.setDeviceCode(patrolInfoVO.getDeviceCode());
            path.setChannelCode(patrolInfoVO.getChannelCode());
            paramter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.DEL_PATROL_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(deviceInfoVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(deviceInfoVO.getAccessNode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DEL_PATROL_OP + VideoConstants.RESPONSE_LOG + cr);
        }catch (Exception e){
            log.error(VideoConstants.DEL_PATROL_ERROR +VideoConstants.EQUAL_INFO, e);
        }
    }



}














