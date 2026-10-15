package com.chinatower.video.modules.videorecordplan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.casCade.service.ICasCadeQueryService;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.ApiInfoUtils;
import com.chinatower.video.modules.videopatrolplan.dataobject.PatrolInfoDO;
import com.chinatower.video.modules.videopatrolplan.dto.ApiInfoDTO;
import com.chinatower.video.modules.videopatrolplan.mapper.PatrolInfoMapper;
import com.chinatower.video.modules.videopatrolplan.repository.IPatrolInfoRepository;
import com.chinatower.video.modules.videopatrolplan.service.impl.VideoPatrolCommonService;
import com.chinatower.video.modules.videorecordplan.dataobject.ChannelVideoPlanDO;
import com.chinatower.video.modules.videorecordplan.dataobject.PlanTimeSectionDO;
import com.chinatower.video.modules.videorecordplan.mapper.ChannelVideoPlanMapper;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideoRecordPlanInfoAsyncService implements IVideoRecordPlanInfoAsyncService {

    @Autowired
    private ApiInfoUtils apiInfoUtils;

    @Resource
    private ChannelVideoPlanMapper channelVideoPlanMapper;

    @Resource
//    PatrolInfoMapper patrolInfoMapper;
//
//    @Autowired
//    private IPatrolInfoRepository patrolInfoRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private NetConnectUtils connectUtils;
    @Autowired
    private VideoPatrolCommonService videoPatrolCommonService;
    @Resource
    private ICasCadeQueryService iCasCadeQueryService;

    @Autowired
    VideoCommonService videoCommonService;


    public Result<JSONObject> deleteRecordPlan(ApiInfoDTO apiInfoDTO) {
        try {
            JSONObject body = JSON.parseObject(apiInfoDTO.getInJsonStr());
            NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(body);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DELETE_RECORD_PLAN);
            // 接口路径参数
            path.setDeviceCode(apiInfoDTO.getDeviceCode());
            path.setChannelCode(apiInfoDTO.getChannelCode());
            paramter.setPath(path);

            //属地化数据统一改造 add by wangyt 20240117
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(apiInfoDTO.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.DELETE_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.DELETE_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            return apiInfoUtils.transResult(cr, respDictData.getData());
        } catch (Exception e) {
            log.error(VideoConstants.DELETE_RECORD_PLAN_ERROR + VideoConstants.EQUAL_INFO, e);
            return Result.fail(VideoConstants.DELETE_RECORD_PLAN_ERROR);
        }
    }


    public Result<JSONObject> updateRecordPlan(ApiInfoDTO apiInfoDTO) {
        try {
            JSONObject body = JSON.parseObject(apiInfoDTO.getInJsonStr());
            NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(body);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UPDATE_RECORD_PLAN);
            // 接口路径参数
            path.setDeviceCode(apiInfoDTO.getDeviceCode());
            path.setChannelCode(apiInfoDTO.getChannelCode());
            paramter.setPath(path);

            //属地化数据统一改造 add by wangyt 20240117
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(apiInfoDTO.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.UPDATE_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.UPDATE_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            return apiInfoUtils.transResult(cr, respDictData.getData());
        } catch (Exception e) {
            log.error(VideoConstants.UPDATE_RECORD_PLAN_ERROR + VideoConstants.EQUAL_INFO, e);
            return Result.fail(VideoConstants.UPDATE_RECORD_PLAN_ERROR);
        }
    }


    public void saveChannelVideoPlanMerge(ApiInfoDTO apiInfoDTO, ChannelVideoPlanVO planMaxVo, ChannelVideoPlanVO channelVideoPlanVO, String recordPlanName) {
        Result<JSONObject> res = null;
        try {
            NetConnectUtils.RequestParamter<JSONObject> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            JSONObject body = JSON.parseObject(apiInfoDTO.getInJsonStr());
            paramter.setBody(body);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.ADD_RECORD_PLAN);
            // 接口路径参数
            path.setDeviceCode(apiInfoDTO.getDeviceCode());
            path.setChannelCode(apiInfoDTO.getChannelCode());
            paramter.setPath(path);

            //属地化数据统一改造 add by wangyt 20240117
            CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(apiInfoDTO.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.ADD_RECORD_PLAN_OP + VideoConstants.REQUEST_LOG + paramter);
            com.chinatower.common.core.vo.ResultEntity cr = connectUtils.post(paramter, com.chinatower.common.core.vo.ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.ADD_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + cr);
            Result<List<SysDictDataResponse>> respDictData = systemFeignClient.dictDataType(String.valueOf(DeviceConstants.NORTH_INTERFACE_MESSAGE));
            res = apiInfoUtils.transResult(cr, respDictData.getData());
        } catch (Exception e) {
            log.error(VideoConstants.ADD_RECORD_PLAN_ERROR + VideoConstants.EQUAL_INFO, e);
        }

        if (ResultCode.SUCCESS.getCode() == res.getCode()) {

            JSONObject resultJson = res.getData();

            long recordPlanId = resultJson.getJSONObject("data").getLong("recordPlanId");
            planMaxVo.setRecordPlanId(recordPlanId);
            ChannelVideoPlanDO channelVideoPlanDO = new ChannelVideoPlanDO();

            BeanUtils.copyProperties(planMaxVo, channelVideoPlanDO);
            channelVideoPlanDO.setRecordPlanId(recordPlanId);
            channelVideoPlanDO.setDeviceCode(planMaxVo.getDeviceCode());
            channelVideoPlanDO.setChannelCode(planMaxVo.getChannelCode());
            channelVideoPlanDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
            channelVideoPlanDO.setCreator(channelVideoPlanVO.getUsername());
            channelVideoPlanDO.setModifier(channelVideoPlanVO.getUsername());
            channelVideoPlanDO.setCreateTime(new Date());
            channelVideoPlanDO.setUpdateTime(new Date());
            channelVideoPlanDO.setRecordPlanName(recordPlanName);
            channelVideoPlanDO.setRecordPlanStatus(planMaxVo.getRecordPlanStatus());
            //插入通道录像计划整合表
            channelVideoPlanMapper.saveChannelVideoPlanMerge(channelVideoPlanDO);
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
                    planTimeSectionDO.setRecordPlanId(planMaxVo.getRecordPlanId());
                    planTimeSectionDO.setCreator(channelVideoPlanVO.getUsername());
                    planTimeSectionDO.setModifier(channelVideoPlanVO.getUsername());
                    planTimeSectionDO.setCreateTime(new Date());
                    planTimeSectionDO.setUpdateTime(new Date());
                    planTimeSectionDO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
                    planTimeSectionDO.setDeviceCode(planMaxVo.getDeviceCode());
                    list.add(planTimeSectionDO);
                }
                channelVideoPlanMapper.saveTimeSectionsMergeBatch(list);
            }
        }else{
            throw new CustomException(res.getMsg(), com.chinatower.common.core.constant.HttpStatus.ERROR_4001);
        }
    }

//    public void updatePatrolInfo(PatrolInfoVO patrolInfoVO) {
//        //更新巡航计划表的执行状态;     先将巡航基础表该设备下所有的巡航启动状态都置为1-停用
//        PatrolInfoDO upInfo = new PatrolInfoDO();
//        upInfo.setDeviceCode(patrolInfoVO.getDeviceCode());
//        upInfo.setChannelCode(patrolInfoVO.getChannelCode());
//        upInfo.setIfCruise(Constants.ONE);
//        patrolInfoMapper.updatePatrolInfo(upInfo);
//        //再根据整合表中的sourcePatrolId字段，将巡航基础表已启动的巡航计划置为2-启用
//        List<PatrolInfoVO> mergeList = patrolInfoMapper.getPatrolMergeListNew(patrolInfoVO);
//        if (mergeList.size() > 0) {
//            List<String> partolList = Arrays.asList(mergeList.get(0).getSourcePatrolId().split(VideoConstants.COMMA));
//            List<PatrolInfoDO> list = new ArrayList<>();
//            for (String pId : partolList) {
//                upInfo.setPatrolId(Long.parseLong(pId));
//                upInfo.setIfCruise(Constants.TWO);
//                list.add(upInfo);
//            }
//            patrolInfoMapper.updatePatrolInfoBatch(list);
//        }
//        log.info(VideoConstants.ID2+patrolInfoVO.getPatrolId()+VideoConstants.DEVICE+ patrolInfoVO.getDeviceCode() + "：整合结束！");
//    }


    /**
     * 功能描述: 调用基础子平台，6.28.	增加巡航路径接口
     * @param addPatrolDTO,insertPatrolInfoVO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public void mergeAddCruisePatrol(AddPatrolReq addPatrolDTO, PatrolInfoVO insertPatrolInfoVO,
//                    String patrolName,List<AddPatrolPresetReq> presetVOList, String[] startTimePeriodArr,
//                                       String hour,PatrolInfoVO patrolInfoVO, DeviceInfoVO deviceInfoVO) {
//        Result<JSONObject> mergeAddCruisePatrolRes = videoPatrolCommonService.mergeAddCruisePatrol(addPatrolDTO,insertPatrolInfoVO,deviceInfoVO);
//        if (ResultCode.SUCCESS.getCode() == mergeAddCruisePatrolRes.getCode()) {
//            Long patrolId = mergeAddCruisePatrolRes.getData().getJSONObject(DeviceNorthIntfConstants.DATA).getLong(DeviceNorthIntfConstants.PATROL_ID);
//            insertPatrolInfoVO.setPatrolId(patrolId);
//            insertPatrolInfoVO.setPatrolName(patrolName);
//            //插入巡航计划整合表
//            patrolInfoMapper.savePatrolMerge(insertPatrolInfoVO);
//            //批量插入巡航计划预置点位关系整合表
//            batchSavePatrolPresetMerge(insertPatrolInfoVO, presetVOList);
//            //如在时间段内，启动巡航，反之，则等待定时任务执行
//            if (Arrays.asList(startTimePeriodArr).contains(hour)) {
//                //调用基础子平台，6.33.启用/停用巡航路径接口
//                Result<JSONObject> mergeDoPatrolRes = mergeDoPatrol(patrolId,insertPatrolInfoVO,deviceInfoVO);
//                if (ResultCode.SUCCESS.getCode() != mergeDoPatrolRes.getCode()) {
//                    //add by liuxt 20231020 新增巡航计划成功但启用失败，则调用基础子平台，删除该巡航计划，保证应用和基础数据一致
//                    PatrolInfoVO newPatrolInfoVO = new PatrolInfoVO();
//                    newPatrolInfoVO.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
//                    newPatrolInfoVO.setChannelCode(insertPatrolInfoVO.getChannelCode());
//                    newPatrolInfoVO.setPatrolId(insertPatrolInfoVO.getPatrolId());
//                    log.info("启用失败删除该巡航计划" + VideoConstants.REQUEST_LOG + JSONObject.toJSONString(newPatrolInfoVO));
//                    videoPatrolCommonService.deletePatrol(newPatrolInfoVO,deviceInfoVO);
//                    //add by liuxt 增加基础报错信息记录 start
//                    try{
//                        BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                        vo.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
//                        vo.setChannelCode(insertPatrolInfoVO.getChannelCode());
//                        vo.setKeywords(DeviceConstants.PATROL_ID);
//                        vo.setKeywordsValue(patrolId.toString());
//                        vo.setRequest(JSONObject.toJSONString(insertPatrolInfoVO));
//                        vo.setResponse(JSONObject.toJSONString(mergeDoPatrolRes));
//                        vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                        vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                        videoCommonService.saveBaseErrorRecord(vo);
//                    }catch (Exception e){
//                        log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//                    }
//                    //add by liuxt 增加基础报错信息记录 end
//                    throw new CustomException(mergeDoPatrolRes.getMsg(), HttpStatus.ERROR_4001);
//                }else {
//                    //将整合表巡航状态更新为启动
//                    PatrolInfoVO uptPatrolInfo1 = new PatrolInfoVO();
//                    uptPatrolInfo1.setPatrolId(patrolId);
//                    uptPatrolInfo1.setIfCruise(Constants.TWO);
//                    uptPatrolInfo1.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
//                    patrolInfoMapper.updatePatrolMerge(uptPatrolInfo1);
//                }
//            }
//        } else {
//            //add by liuxt 增加基础报错信息记录 start
//            try{
//                BaseErrorRecordVO vo = new BaseErrorRecordVO();
//                vo.setDeviceCode(insertPatrolInfoVO.getDeviceCode());
//                vo.setChannelCode(insertPatrolInfoVO.getChannelCode());
//                vo.setKeywords(DeviceConstants.PATROL_ID);
//                vo.setKeywordsValue("");
//                vo.setRequest(JSONObject.toJSONString(insertPatrolInfoVO));
//                vo.setResponse(JSONObject.toJSONString(mergeAddCruisePatrolRes));
//                vo.setCurrentRow(Integer.toString(Thread.currentThread().getStackTrace()[1].getLineNumber()));
//                vo.setCurrentClass(Thread.currentThread().getStackTrace()[1].getClassName());
//                videoCommonService.saveBaseErrorRecord(vo);
//            }catch (Exception e){
//                log.error(VideoConstants.ADD_BASIC_ERR_MSG,e);
//            }
//            //add by liuxt 增加基础报错信息记录 end
//            throw new CustomException(mergeAddCruisePatrolRes.getMsg(), HttpStatus.ERROR_4001);
//        }
//        updatePatrolInfo(patrolInfoVO);
//    }

    /**
     * 功能描述: 调用基础子平台，6.29.	删除巡航路径接口
     *
     * @param queryPatrolInfoVO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public Result<JSONObject> mergeDelPatrol(PatrolInfoVO queryPatrolInfoVO, DeviceInfoVO deviceInfoVO) {
//        return videoPatrolCommonService.mergeDelPatrol(queryPatrolInfoVO,deviceInfoVO);
//    }

    /**
     * 功能描述: 调用基础子平台，6.31.	修改巡航路径接口
     * @param potrolMergeInfoVO,presetVOList,updatePatrolInfo
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public Result<JSONObject> mergeUpdatePatrol(PatrolInfoVO potrolMergeInfoVO, List<AddPatrolPresetReq> presetVOList, PatrolInfoVO updatePatrolInfo, DeviceInfoVO deviceInfoVO) {
//        return videoPatrolCommonService.mergeUpdatePatrol(potrolMergeInfoVO, presetVOList, updatePatrolInfo,deviceInfoVO);
//    }

    /**
     * 功能描述: 调用基础子平台，6.32.	查询巡航路径接口
     * 并判断巡航路径状态，若该巡航计划正在启动中，先停用巡航计划
     * @param queryPatrolInfoVO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public Result<JSONObject> mergeQueryAndDoPatrol(PatrolInfoVO queryPatrolInfoVO, DeviceInfoVO deviceInfoVO) {
//        return videoPatrolCommonService.mergeQueryAndDoPatrol(queryPatrolInfoVO,deviceInfoVO);
//    }

    /**
     * 功能描述: 调用基础子平台，6.33.	启用/停用巡航路径接口
     * @param patrolId,updatePatrolInfo
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public Result<JSONObject> mergeDoPatrol(Long patrolId, PatrolInfoVO updatePatrolInfo, DeviceInfoVO deviceInfoVO) {
//        return videoPatrolCommonService.mergeDoPatrol(patrolId, updatePatrolInfo,deviceInfoVO);
//    }

    /**
     * 将获取列表中的巡航计划ID整合成一个字符串，用逗号分隔；
     * 并取第一个巡航的时间段，做为整合巡航计划的时间段
     * @param
     */
//    public PatrolInfoVO getPatrolPresetInfo(List<PatrolInfoVO> listData) {
//        return videoPatrolCommonService.getPatrolPresetInfo(listData);
//    }

    /**
     * 功能描述: 查询预置位信息，并将预置位集合重新排序，按照预约时间的先后顺序
     * @param updatePatrolInfo
     * @return:
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public List<AddPatrolPresetReq> patrolPresetHandle(PatrolInfoVO updatePatrolInfo) {
//        return videoPatrolCommonService.patrolPresetHandle(updatePatrolInfo);
//    }

    /**
     * 功能描述: 批量插入巡航计划预置点位关系整合表
     * @param potrolMergeInfoVO,presetVOList
     * @return:
     * @auther: zhaozj
     * @date: 2023/11/14
     * @version: 1.6.30
     */
//    public void batchSavePatrolPresetMerge(PatrolInfoVO potrolMergeInfoVO, List<AddPatrolPresetReq> presetVOList) {
//        videoPatrolCommonService.batchSavePatrolPresetMerge(potrolMergeInfoVO, presetVOList);
//    }



}











