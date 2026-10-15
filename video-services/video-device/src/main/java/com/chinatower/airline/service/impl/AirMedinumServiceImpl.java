package com.chinatower.airline.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.airline.constants.Constants;
import com.chinatower.airline.dto.request.AirVideoReq;
import com.chinatower.airline.dto.response.UavNestChannelResp;
import com.chinatower.airline.mapper.AirLineSensorInfoMapper;
import com.chinatower.airline.mapper.AirLineMapper;
import com.chinatower.airline.mapper.AirTaskMapper;
import com.chinatower.airline.service.AirMedinumService;
import com.chinatower.airline.util.UavOkhttpUtil;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.utils.security.MD5Util;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WLF
 * @description
 * @date 2025/1/14 16:00
 */
@Service
@Slf4j
public class AirMedinumServiceImpl implements AirMedinumService {
    @Resource
    private UavOkhttpUtil okhttpUtil;

    @Autowired
    private AirLineMapper airLineMapper;

    @Autowired
    private AirLineSensorInfoMapper airLineSensorInfoMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private AirTaskMapper airTaskMapper;

    public static final String PLATFORM_CODE = "platform_code";

    private static final String SUCCESS_CODE = com.chinatower.platform.constants.Constants.SUCCESS_CODE;

    /**
     * @param airVideoReq
     * @return
     */
    @Override
    public Result getVideoRealtimeUrl(AirVideoReq airVideoReq) {
        NetConnectUtils.RequestParamter<AirVideoReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airVideoReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UAV_GET_VIDEO_REALTIMEURL);
        pathEdit.setDeviceCode(airVideoReq.getDeviceCode());
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airVideoReq.getAccessNode(), airVideoReq.getAcceptNode());
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject = JSON.parseObject(resultEntity.getData().toString());
            return Result.data(jsonObject);
        }
        return Result.fail("获取实时视频地址失败");
    }

    /**
     *
     * @param airVideoReq
     * @return
     */
    @Override
    public Result switchLiveStream(AirVideoReq airVideoReq) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String platformCode = loginUser.getUser().getPlatformCode();
        airVideoReq.setAcceptNode(platformCode);
        NetConnectUtils.RequestParamter<AirVideoReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(airVideoReq);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.SET_VIDEO_QUALITY);
        pathEdit.setDeviceCode(airVideoReq.getDeviceCode());
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airVideoReq.getAccessNode(), platformCode);
        if (ObjectUtil.isNotNull(resultEntity)) {
            return Result.data("设置成功");
        }
        return Result.success("设置成功");
    }

    /**
     * 获取无人机机巢直播流地址
     * @param airVideoReq
     * @return
     */
    @Override
    public Result<UavNestChannelResp> getUavNestStream(AirVideoReq airVideoReq) {
        UavNestChannelResp channelResp = new UavNestChannelResp();
        channelResp.setDeviceCode(airVideoReq.getDeviceCode());
        List<UavNestChannelResp.ChannelDTO> channelDTOList = new ArrayList<>();
        // 查询当前无人机机巢设备下的通道信息
        List<Map<String, Object>> channelList = airLineMapper.queryChannelInfo(airVideoReq.getDeviceCode());
        if (CollectionUtils.isEmpty(channelList)) {
            log.info("未查询到机巢对应的通道信息！");
            return Result.data(null);
        }

        // 循环调用飞控平台-4.5.1获取机巢视频里接口
        for (Map<String, Object> channelMap : channelList) {
            NetConnectUtils.RequestParamter<AirVideoReq> paramterEdit = new NetConnectUtils.RequestParamter<>();
            paramterEdit.setBody(airVideoReq);
            NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
            pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.NEST_CHANNELS_GET_VIDEO_REALTIMEURL);
            pathEdit.setDeviceCode(airVideoReq.getDeviceCode());
            String channelCode = String.valueOf(channelMap.get(Constants.CHANNEL_CODE));
            pathEdit.setChannelCode(String.valueOf(channelCode));
            paramterEdit.setPath(pathEdit);

            try {
                ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, airVideoReq.getAccessNode(), airVideoReq.getAcceptNode());
                log.info("getUavNestStream-调用飞控获取机巢视频流接口返回:{}", JSON.toJSONString(resultEntity));
                if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
                    JSONObject dataObj = JSON.parseObject(resultEntity.getData().toString());
                    log.debug("解析后的JSON对象: {}", dataObj.toJSONString());
                    if (dataObj != null) {
                        String streamUrl = dataObj.containsKey("streamUrl") ? dataObj.getString("streamUrl") : null;
                        UavNestChannelResp.ChannelDTO channelDTO = new UavNestChannelResp.ChannelDTO();
                        channelDTO.setChannelCode(channelCode);
                        channelDTO.setChannelType(String.valueOf(channelMap.get(Constants.CHANNEL_TYPE)));
                        channelDTO.setStreamUrl(streamUrl);
                        channelDTO.setVideoType(dataObj.containsKey("videoType") ? dataObj.getString("videoType") : null);
                        channelDTOList.add(channelDTO);
                    }
                }
            } catch (Exception e) {
                log.error("调用飞控获取机巢视频流接口失败：", e);
            }
        }
        channelResp.setChannelList(channelDTOList);
        return Result.data(channelResp);
    }

    @Override
    public void liveHorn(AirHornReq airHornReq) {
        // 1. 基础参数校验
        validateBaseParams(airHornReq);

        // 2. 获取上下文信息
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        String platformCode = loginUser.getPlatformCode();
        String accessNode = airHornReq.getAccessNode();

        // 3. 步骤1：停止播放
        stopPlay(airHornReq, platformCode, accessNode);

        // 4. 步骤2：设置音量
        setPlayVolume(airHornReq, platformCode, accessNode);

        // 5. 步骤3：根据音频类型设置播放模式（文本/文件）
        setPlayMode(airHornReq, platformCode, accessNode);
    }
    /**
     * 基础参数校验
     */
    private void validateBaseParams(AirHornReq airHornReq) {
        if (airHornReq == null) {
            throw new CustomException("请求参数不能为空");
        }
        if (StringUtils.isEmpty(airHornReq.getDeviceCode())) {
            throw new CustomException("设备编码必传");
        }
    }

    /**
     * 停止播放
     */
    private void stopPlay(AirHornReq airHornReq, String platformCode, String accessNode) {
        PlayStopDto playStopDto = new PlayStopDto();
        BeanUtils.copyProperties(airHornReq, playStopDto);
        // 复用通用HTTP请求方法
        ResultEntity result = sendPostRequest(playStopDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SPEAKER_STOP, platformCode, accessNode);
        if (!SUCCESS_CODE.equals(result.getReturnCode())) {
            throw new CustomException("停止播放失败:" + result.getErrorMsg());
        }
    }

    /**
     * 设置播放音量
     */
    private void setPlayVolume(AirHornReq airHornReq, String platformCode, String accessNode) {
        PlayVolumeSetDto playVolumeSetDto = new PlayVolumeSetDto();
        BeanUtils.copyProperties(airHornReq, playVolumeSetDto);
        playVolumeSetDto.setOptType(Constants.HORN_VOLUMN);
        playVolumeSetDto.setValue(airHornReq.getPlayVolume());

        ResultEntity result = sendPostRequest(playVolumeSetDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SPEAKER_SET, platformCode, accessNode);
        if (!SUCCESS_CODE.equals(result.getReturnCode())) {
            throw new CustomException("音量设置失败:" + result.getErrorMsg());
        }
    }

    /**
     * 设置播放模式（文本TTS/文件URL）
     */
    private void setPlayMode(AirHornReq airHornReq, String platformCode, String accessNode) {
        // 修复原代码：属性拷贝目标对象错误（原代码写成了playStopDto）
        if ("1".equals(airHornReq.getAudioStatus())) {
            // 文本播放（TTS）
            PlayTtsSetDto dto = new PlayTtsSetDto();
            BeanUtils.copyProperties(airHornReq, dto);
            dto.setText(airHornReq.getHornText());

            ResultEntity result = sendPostRequest(dto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_PLAY_TTS, platformCode, accessNode);
            if (!SUCCESS_CODE.equals(result.getReturnCode())) {
                throw new CustomException("tts播放失败:" + result.getErrorMsg());
            }
        } else {
            // 文件播放（URL）
            PlayModeSetDto playModeSetDto = new PlayModeSetDto();
            BeanUtils.copyProperties(airHornReq, playModeSetDto);
            playModeSetDto.setUrl(airHornReq.getHornUrl());
            playModeSetDto.setMd5(MD5Util.hash(airHornReq.getHornUrl()));
            ResultEntity result = sendPostRequest(playModeSetDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SPEAKER_PLAY, platformCode, accessNode);
            if (!SUCCESS_CODE.equals(result.getReturnCode())) {
                throw new CustomException("文件播放失败:" + result.getErrorMsg());
            }
        }
    }

    /**
     * 通用HTTP POST请求方法（复用重复逻辑）
     * @param body 请求体对象
     * @param url 请求URL
     * @param platformCode 平台编码
     * @param accessNode 接入节点
     * @return 响应结果
     */
    private <T> ResultEntity sendPostRequest(T body, NetConnectUtils.RequestParamter.URLMappingEnum url, String platformCode, String accessNode) {
        NetConnectUtils.RequestParamter<T> paramter = new NetConnectUtils.RequestParamter<>();
        paramter.setBody(body);

        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(url);
        paramter.setPath(path);

        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramter, accessNode, platformCode);
        log.info("通用请求入参：{},出参:{}",paramter,JSON.toJSONString(resultEntity));
        // 统一空值校验：如果返回null，直接判定为失败
        if (resultEntity == null) {
            throw new CustomException("调用网关接口返回空结果");
        }
        return resultEntity;
    }

    @Override
    public ResultEntity stopPlay(PlayStopDto playStopDto) {
        return sendPostRequest(playStopDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SPEAKER_STOP, getSelfPlatFormCode(), playStopDto.getAccessNode());
    }

    @Override
    public ResultEntity setPlayVolume(PlayVolumeSetDto playVolumeSetDto) {
        return sendPostRequest(playVolumeSetDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SPEAKER_SET, getSelfPlatFormCode(), playVolumeSetDto.getAccessNode());
    }

    @Override
    public ResultEntity setPlayTts(PlayTtsSetDto dto) {
        return sendPostRequest(dto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_PLAY_TTS, getSelfPlatFormCode(), dto.getAccessNode());
    }

    @Override
    public ResultEntity setPlay(PlayModeSetDto playModeSetDto) {
        return sendPostRequest(playModeSetDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SPEAKER_PLAY, getSelfPlatFormCode(), playModeSetDto.getAccessNode());
    }

    @Override
    public ResultEntity searchlightSwitch(SearchlightSwitchDto searchlightSwitchDto) {
        return sendPostRequest(searchlightSwitchDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SEARCHLIGHT_SWITCH, getSelfPlatFormCode(), searchlightSwitchDto.getAccessNode());
    }

    @Override
    public ResultEntity searchlight(SearchlightDto searchlightDto) {
        return sendPostRequest(searchlightDto, NetConnectUtils.RequestParamter.URLMappingEnum.MOUNT_SEARCHLIGHT_SET, getSelfPlatFormCode(), searchlightDto.getAccessNode());
    }

    @Override
    public void taskStatusProcess(AirTaskStatusProcessDto airTaskStatusProcessDto) {

        if(StringUtils.isEmpty(airTaskStatusProcessDto.getTaskRecordId())) {
            log.error("推送任务id为空，推送参数:{}",JSON.toJSONString(airTaskStatusProcessDto));
            return;
        }
        //根据任务id查询任务详情
        AirTaskReq airTaskReq = airTaskMapper.selectTaskInfo(airTaskStatusProcessDto.getTaskRecordId());
        if(Objects.isNull(airTaskReq) || StringUtils.isEmpty(airTaskReq.getFlightRouteId())) {
            log.error("任务信息为空或飞行航线id为空:{}",airTaskStatusProcessDto.getTaskRecordId());
            return;
        }
        //根据机巢查询挂载型号
        UavDeviceDTO uavDeviceDTO = queryUavByNestInfo(airTaskReq);
        if(Objects.isNull(uavDeviceDTO) || StringUtils.isEmpty(uavDeviceDTO.getMountType())) {
            log.error("根据机巢查询挂载型号为空:{}",JSON.toJSONString(airTaskReq));
            return;
        }
        String mountCode = uavDeviceDTO.getUavMountModel();
        String mountType = uavDeviceDTO.getMountType();
        Map<String, String> mountMap = new HashMap<>();
        if (mountCode != null && mountType != null) {
            String[] codes = mountCode.split(",");
            String[] types = mountType.split(",");
            // 确保两个数组都不为空且长度匹配
            if (codes.length > 0 && types.length > 0 && codes.length == types.length) {
                for (int i = 0; i < types.length; i++) {
                    // 去除可能的空格
                    String type = types[i] != null ? types[i].trim() : "";
                    String code = codes[i] != null ? codes[i].trim() : "";

                    if (!type.isEmpty()) {
                        mountMap.put(type, code);
                    }
                }
            }
        }
        if(StringUtils.isEmpty(mountCode)) {
            log.error("根据机巢查询挂载型号为空:{}",JSON.toJSONString(airTaskReq));
            return;
        }
        if (uavDeviceDTO.getMountType().contains(Constants.ONE)) {
            //返航关闭喇叭
            if (Constants.FIVE.equals(airTaskStatusProcessDto.getExecStatus())) {
                log.info("【返航处理】设备{}进入返航状态，停止所有喇叭播放", airTaskStatusProcessDto.getDeviceCode());
//                handleReturnHome(airTaskStatusProcessDto,mountCode,airTaskReq);

                PlayStopDto stopDto = new PlayStopDto();
                stopDto.setDeviceCode(airTaskStatusProcessDto.getDeviceCode());
                stopDto.setMountType(mountMap.get(Constants.ONE));
                stopDto.setAccessNode(airTaskReq.getAccessNode());
                stopPlay(stopDto);
                return;
            }
        }
        if (ObjectUtil.isEmpty(airTaskStatusProcessDto.getWaypointIndex())) {
            log.error("当前消息未接收航点到达通知，不需处理:{}", JSON.toJSONString(airTaskStatusProcessDto));
            return;
        }

        String pointIndex = airTaskStatusProcessDto.getWaypointIndex();
        Integer flightMainType = airLineMapper.queryFlightMainTypeByFlightRouteId(airTaskReq.getFlightRouteId());
        // 正常航线时
        if (Objects.equals(flightMainType, 0)) {
            try {
                pointIndex = String.valueOf(Integer.parseInt(pointIndex) - 1);
            } catch (NumberFormatException e) {
                log.warn("航点索引格式异常, 不做索引纠偏, flightRouteId={}, waypointIndex={}",
                        airTaskReq.getFlightRouteId(), pointIndex);
            }
        }
        List<VmAirLineSensorInfo> vmAirLineSensorInfos = airLineSensorInfoMapper.selectByFlightRouteIdAndPointIndex(
                airTaskReq.getFlightRouteId(), pointIndex);
        if(CollectionUtils.isEmpty(vmAirLineSensorInfos)) {
            log.info("【无配置】设备{}航线{}航点{}未查询到配置, flightMainType={}",
                    airTaskStatusProcessDto.getDeviceCode(), airTaskReq.getFlightRouteId(), pointIndex, flightMainType);
            return;
        }
        if(uavDeviceDTO.getMountType().contains(Constants.ONE)) {
            log.info("【航点到达处理】设备{}进入航点到达第一次，航点到达",airTaskStatusProcessDto.getDeviceCode());
//            {"deviceCode":"41050200003027000002","mountType":"15","optType":"volumn","value":"100"}
            PlayVolumeSetDto playVolumeSetDto = new PlayVolumeSetDto();
            playVolumeSetDto.setOptType("volumn");
            playVolumeSetDto.setMountType(mountMap.get(Constants.ONE));
            playVolumeSetDto.setValue("100");
            playVolumeSetDto.setDeviceCode(airTaskReq.getStartNestCode());
            setPlayVolume(playVolumeSetDto);


        }
        //到达航点
        if(Constants.ONE.equals(airTaskStatusProcessDto.getWaypointState())) {
            if(uavDeviceDTO.getMountType().contains(Constants.ONE)) {
                //只要配置了先停止播放，不考虑之前配置的什么
//                handleReturnHome(airTaskStatusProcessDto,mountCode,airTaskReq);
                PlayStopDto stopDto = new PlayStopDto();
                stopDto.setDeviceCode(airTaskStatusProcessDto.getDeviceCode());
                stopDto.setMountType(mountMap.get(Constants.ONE));
                stopDto.setAccessNode(airTaskReq.getAccessNode());
                stopPlay(stopDto);

            }
            for(VmAirLineSensorInfo airLineHornInfo : vmAirLineSensorInfos) {
                if(Constants.ONE.equals(airLineHornInfo.getSensorType())) {
                    //开启
                    if(Constants.ONE.equals(airLineHornInfo.getHornStatus())) {
                        log.info(">>> taskStatusProcess    开启>>>>> airLineHornInfo： {}",JSONObject.toJSONString(airLineHornInfo)  );
                        //文本
                        if(Constants.ONE.equals(airLineHornInfo.getAudioStatus())) {
                            log.info(">>> taskStatusProcess    开启 文本>>>>> airLineHornInfo： {}",JSONObject.toJSONString(airLineHornInfo)  );
                            PlayTtsSetDto dto = new PlayTtsSetDto();
                            dto.setText(airLineHornInfo.getContentValue());
                            dto.setMountType(mountMap.get(Constants.ONE));
                            dto.setPlayType(Constants.TWO.equals(airLineHornInfo.getHornNumType()) ? Constants.HORN_CYCLE : Constants.HORN_SINGLE);
                            dto.setAccessNode(airTaskReq.getAccessNode());
                            dto.setDeviceCode(airLineHornInfo.getDeviceCode());
                            log.info(">>> taskStatusProcess    参数 文本 >>>>> dto： {}",JSONObject.toJSONString(dto)  );
                            setPlayTts(dto);
                        }else {
                            //
                            log.info(">>> taskStatusProcess    开启 pcm >>>>> airLineHornInfo： {}",JSONObject.toJSONString(airLineHornInfo)  );
                            PlayModeSetDto dto = new PlayModeSetDto();
                            dto.setMountType(mountMap.get(Constants.ONE));
                            String obsFileUrl = fileUrlEncryptUtil.dealObsFileUrl(airLineHornInfo.getHornTypeUrl());
                            dto.setUrl(obsFileUrl);
                            dto.setAccessNode(airTaskReq.getAccessNode());
                            dto.setDeviceCode(airLineHornInfo.getDeviceCode());
                            dto.setPlayType(Constants.TWO.equals(airLineHornInfo.getHornNumType()) ? Constants.HORN_CYCLE : Constants.HORN_SINGLE);
                            dto.setMd5(airLineHornInfo.getMd5());
                            log.info(">>> taskStatusProcess    参数 pcm >>>>> dto： {}",JSONObject.toJSONString(dto)  );
                            setPlay(dto);
                        }
                    }else {
                        log.info(">>> taskStatusProcess    关闭 handleReturnHome  stopPlay>>>>> ");
//                        handleReturnHome(airTaskStatusProcessDto,mountCode,airTaskReq);
//                        PlayStopDto stopDto = new PlayStopDto();
//                        stopDto.setDeviceCode(airTaskStatusProcessDto.getDeviceCode());
//                        stopDto.setMountType(mountMap.get(Constants.ONE));
//                        stopDto.setAccessNode(airTaskReq.getAccessNode());
//                        stopPlay(stopDto);
                    }
                }else {
                    log.info(">>> taskStatusProcess   开启探照灯>>>>> ");
                    //开启探照灯
                    SearchlightSwitchDto searchlightSwitchDto = new SearchlightSwitchDto();
                    searchlightSwitchDto.setMountType(mountMap.get(Constants.TWO));
                    searchlightSwitchDto.setAccessNode(airTaskReq.getAccessNode());
                    searchlightSwitchDto.setDeviceCode(airLineHornInfo.getDeviceCode());
                    searchlightSwitch(searchlightSwitchDto);
                    //调整探照灯亮度
                    SearchlightDto searchlightDto = new SearchlightDto();
                    searchlightDto.setAccessNode(airTaskReq.getAccessNode());
                    searchlightDto.setValue(String.valueOf(airLineHornInfo.getBrightness()));
                    searchlightDto.setDeviceCode(airLineHornInfo.getDeviceCode());
                    searchlightDto.setOptType(Constants.BRIGHTNESS);
                    searchlightSwitchDto.setMountType(mountMap.get(Constants.TWO));
                    searchlight(searchlightDto);
                }
            }
        }
    }

    /**
     * 根据机巢查询挂载类型
     * @param airTaskReq
     * @return
     */
    private UavDeviceDTO queryUavByNestInfo(AirTaskReq airTaskReq) {
        AirDeviceDto param = new AirDeviceDto();
        param.setAcceptNode(airTaskReq.getAcceptNode());
        param.setUavNestCode(airTaskReq.getStartNestCode());
        param.setTenantId(airTaskReq.getTenantId());
        param.setIndustryCode(airTaskReq.getIndustryCode());
        param.setDeviceCode(airTaskReq.getUavCode());
        return airTaskMapper.queryUavByNestInfo(param);
    }

    /**
     * 处理返航逻辑：精准停止指定设备的所有播放
     */
    private void handleReturnHome(AirTaskStatusProcessDto dto,String mountCode,AirTaskReq airTaskReq) {
        PlayStopDto stopDto = new PlayStopDto();
        stopDto.setDeviceCode(dto.getDeviceCode());
        stopDto.setMountType(mountCode);
        stopDto.setAccessNode(airTaskReq.getAccessNode());
        stopPlay(stopDto);
    }


    /**
     * 获取当前节点
     * @return
     */
    public String getSelfPlatFormCode(){
        String configValue ="";
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))){
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")),SysConfig.class);
            if(sysConfigInfo != null){
                configValue = sysConfigInfo.getConfigValue();
            }else{
                log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
            }
        }else {
            log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
        }
        return configValue;
    }

    @Override
    public String querySearchlightStatus(AirSearchlightStatusDto dto) {
        if(StringUtils.isEmpty(dto.getMountCode()) || StringUtils.isEmpty(dto.getUavNestCode())) {
            throw new CustomException("机巢编码或探照灯编码不能为空");
        }
        String key = Constants.SET_SEARCHLIGHT_STATUS_KEY + dto.getUavNestCode()+ dto.getMountCode();
        String redisToken = RedisUtil.get(key, RedisDBType.PRIMARY);
        return StringUtils.isEmpty(redisToken) ? Constants.ZONE : redisToken;
    }

    @Override
    public void setSearchlightStatus(AirSearchlightStatusDto dto) {
        if(StringUtils.isEmpty(dto.getMountCode()) || StringUtils.isEmpty(dto.getUavNestCode())) {
            throw new CustomException("机巢编码或探照灯编码不能为空");
        }
        String key = Constants.SET_SEARCHLIGHT_STATUS_KEY + dto.getUavNestCode() + dto.getMountCode();
        boolean status = RedisUtil.set(key,dto.getMountStatus(), 2*60, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        if(!status) {
            throw new CustomException("探照灯状态设置失败");
        }
    }

    /**
     * @param dto
     */
    @Override
    public Result calibration(AirSearchlightStatusDto dto) {
        NetConnectUtils.RequestParamter<AirSearchlightStatusDto> paramterEdit = new NetConnectUtils.RequestParamter<>();
        paramterEdit.setBody(dto);
        NetConnectUtils.RequestParamter.Path pathEdit = new NetConnectUtils.RequestParamter.Path();
        pathEdit.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.UAV_CALIBRATION);
        paramterEdit.setPath(pathEdit);
        ResultEntity resultEntity = okhttpUtil.doPostUavGateWay(paramterEdit, null, null);
        if (ObjectUtil.isNotNull(resultEntity) && ObjectUtil.isNotNull(resultEntity.getData())) {
            JSONObject jsonObject = JSON.parseObject(resultEntity.getData().toString());
            return Result.data(jsonObject);
        }
        return Result.success("标定成功");
    }
}
