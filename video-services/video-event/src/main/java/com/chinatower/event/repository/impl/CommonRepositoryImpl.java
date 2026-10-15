package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dto.AuthRegisterPush;
import com.chinatower.event.dto.DeviceInfoByPush;
import com.chinatower.event.dto.PushThirdDto;
import com.chinatower.event.mapper.VideoPresetMapper;
import com.chinatower.event.mapper.VmAlarmSuppressStrategyMapper;
import com.chinatower.event.mongodb.HistoryLogMapper;
import com.chinatower.event.repository.ICommonRepository;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.OkhttpUtil;
import com.chinatower.event.util.CommonUtils;
import com.chinatower.event.vo.PushAlarmThirdVO;
import com.chinatower.event.vo.PushThirdVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.entity.HistoryLogDto;
import com.chinatower.framework.common_service.response.ProcessResult;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.user.GetUserByDeptAndDeviceScopeRequest;
import com.chinatower.video.api.entity.VideoPresetDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: CommonRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 21:22
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class CommonRepositoryImpl implements ICommonRepository {

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Resource
    private IDeviceRepository iDeviceRepository;

    @Autowired
    private VmAlarmSuppressStrategyMapper strategyDao;

    @Autowired
    private HistoryLogMapper historyLogMapper;

    @Autowired
    private VideoPresetMapper videoPresetMapper;

    @Override
    public Result<List<SysUser>> getUserByDeptAndDeviceScope(List<String> userIdList, String tenantId, String deviceCode, String industryCode, String appVerCode, String alarmSource, String cityId, String countyId, String provinceId, String alarmTypeId) {
        //调用接口获取有摄像机权限的客户
        GetUserByDeptAndDeviceScopeRequest request = new GetUserByDeptAndDeviceScopeRequest();
        request.setUserIdList(userIdList);
        request.setDeviceCode(deviceCode);
        request.setIndustryCode(industryCode);
        request.setAppVerCode(appVerCode);
        request.setTenantId(tenantId);
        request.setAlarmSource(alarmSource);
        request.setCityId(cityId);
        request.setProvinceId(provinceId);
        request.setCountyId(countyId);
        request.setTypeValue(alarmTypeId);
        log.info("CommonRepositoryImpl-getUserByDeptAndDeviceScope-根据设备编号获取有权限的用户列表-req:{}", JSON.toJSONString(request));
        Result<List<SysUser>> reponse = systemFeignClient.getUserByDeptAndDeviceScope(request);
        log.info("CommonRepositoryImpl-getUserByDeptAndDeviceScope-根据设备编号获取有权限的用户列表-resp:{}", JSON.toJSONString(reponse));
        return reponse;
    }

    @Override
    public ProcessResult pushThird(PushThirdDto dto) {
        log.info("pushThird start:" + JSONObject.toJSONString(dto));
        PushThirdVO vo = new PushThirdVO();
        PushAlarmThirdVO push = new PushAlarmThirdVO();
        String originalJson = dto.getOriginalJson();
        List<ReceiveAlarmVO> alarms = dto.getAlarmList();
            if(!CollectionUtils.isEmpty(alarms)){
                List<String> deviceList = new ArrayList<>();
                for (ReceiveAlarmVO alarmsVo:alarms) {
                    if(!StringUtils.isEmpty(alarmsVo.getDeviceCode())){
                        deviceList.add(alarmsVo.getDeviceCode());
                    }
                }
                List<String> customerCodeList =alarms.stream().map(o -> o.getTenantId()).collect(Collectors.toList());
                List<DeviceInfoByPush> deviceInfoList = new ArrayList<>();
                if(!CollectionUtils.isEmpty(deviceList)){
                    //获取所有摄像机信息
                    deviceInfoList =iDeviceRepository.getDevInfo(deviceList);
                }
                List<AuthRegisterPush> callUrlList = iDeviceRepository.getAuthRegister(customerCodeList);
                //查询设备相关信息
                for(ReceiveAlarmVO alarm : alarms){
                    DeviceInfoByPush deviceInfo = null;
                    for (DeviceInfoByPush deviceVo:deviceInfoList) {
                        if(deviceVo.getDeviceCode().equals(alarm.getDeviceCode())){
                            deviceInfo =deviceVo;
                        }
                    }

                    HistoryLogDto historyLogDto = new HistoryLogDto();
                    try{
                        String customerCode = alarm.getTenantId();
                        historyLogDto.setType(Constants.CODE_910);
                        historyLogDto.setOriginalJson(originalJson);
                        historyLogDto.setCustomerCode(customerCode);
                        historyLogDto.setAlarmCode(alarm.getAlarmCode());
                        AuthRegisterPush registerPush =null;
                        for (AuthRegisterPush registerPushVo:callUrlList) {
                            if(registerPushVo.getTenantId().equals(alarm.getTenantId())){
                                registerPush =registerPushVo;
                            }
                        }
                        if(registerPush !=null){
                            String type = Constants.CODE_910;
                            String callbackUrl = registerPush.getCallbackUrl();
                            if(StringUtils.isEmpty(callbackUrl)){
                                log.info("注册回调地址错误！");
                                continue;
                            }
                            historyLogDto.setUrl(callbackUrl);
                            push.setZoom(alarm.getZoom());
                            push.setTilt(alarm.getTilt());
                            push.setPan(alarm.getPan());
                            push.setAlarmType(alarm.getWarningTypeId());
                            push.setAlarmVideoUrl(alarm.getAlarmVideoUrl());
                            push.setOriginalPictureUrl(alarm.getOriginalPictureUrl());
                            push.setAlarmPictureUrl(alarm.getAlarmPictureUrl());
                            push.setEventLatitude(alarm.getLatitude());
                            push.setEventLongitude(alarm.getLongitude());
                            push.setWarningOrderId(alarm.getWarningOrderId());
                            push.setAlarmBody(alarm.getAlarmBody());
                            push.setAlarmTime(alarm.getAlarmTime());
                            push.setDeviceCode(alarm.getDeviceCode());
                            push.setAlarmCode(alarm.getAlarmCode());
                            push.setDeviceName(alarm.getDeviceName());
                            push.setChannelCode(alarm.getChannelCode());
                            push.setChannelName(alarm.getChannelName());
                            push.setAddress(alarm.getAddress());
                            push.setWarningSource(alarm.getWarningSource());
                            push.setExtraField(alarm.getExtraField());
                            if(alarm.getExtraInfo() != null && !StringUtils.isEmpty(alarm.getExtraInfo().getPresetId())){
                                try {
                                    VideoPresetDO videoPresetDO =videoPresetMapper.selectVideoPreById(alarm.getExtraInfo().getPresetId(),alarm.getDeviceCode());
                                    if (videoPresetDO != null) {
                                        push.setPresetId(alarm.getExtraInfo().getPresetId());
                                        push.setPresetName(videoPresetDO.getPresetName());
                                    }
                                }catch (Exception e){
                                    log.error("推送第三方告警获取预置位名称报错:{}",e);
                                }
                            }else{
                                push.setPresetId("");
                                push.setPresetName("");
                            }
                            if(deviceInfo != null){
                                push.setRegionCode(deviceInfo.getCountyId());
                                push.setRegionName(deviceInfo.getRegionName());
                                push.setLocation(deviceInfo.getLocation());
                                push.setDeviceLatitude(deviceInfo.getLatitude());
                                push.setDeviceLongitude(deviceInfo.getLongitude());
                            }

                            if(alarm.getExtraInfo() != null){
                                push.setFrontAlarmPicUrl(alarm.getExtraInfo().getFrontAlarmPicUrl());
                                push.setFrontAlarmVedioUrl(alarm.getExtraInfo().getFrontAlarmVedioUrl());
                            }
                            //910:告警
                            vo.setEventId(type);
                            vo.setData(push);
                            JSONObject joIn = (JSONObject)JSONObject.toJSON(vo);
                            //将joIn里的null值替换为“”
                            CommonUtils.filterNull(joIn);
                            historyLogDto.setRequestJson(JSON.toJSONString(joIn));
                            Long startTime = System.currentTimeMillis();

                            log.info("第三方推送入参：" + JSONObject.toJSONString(joIn));
                            JSONObject result =OkhttpUtil.doPost(JSONObject.toJSONString(joIn),callbackUrl);
                            Long expendTime = System.currentTimeMillis() - startTime;
                            historyLogDto.setExpendTime(String.valueOf(expendTime));
                            historyLogDto.setResponseJson(JSON.toJSONString(result));
                            historyLogDto.setCreateTime(new Date());
                            log.info("==========" + new Exception().getStackTrace()[0].getMethodName() + "返回报文：" + JSON.toJSONString(result));
                            historyLogDto.setStatus(Constants.ONE);
                            this.save(alarm.getAlarmTime(),historyLogDto);
                            return new ProcessResult(ProcessResult.SUCCESS, Constants.INTERCEPT_SUCCESS_MESSAGE, result);
                        }else{
                            log.info("推送第三方平台异常，未配置API！");
                        }
                    }catch (Exception e){
                        log.error("推送第三方平台异常" , e);
                        historyLogDto.setStatus(Constants.ZERO);
                        historyLogDto.setError(JSON.toJSONString(e));
                        this.save(alarm.getAlarmTime(),historyLogDto);
                        return new ProcessResult(ProcessResult.ERROR, Constants.INTERCEPT_FAIL_MESSAGE, e.getMessage());
                    }
                }
            }
        //}
        log.info("pushThird end");
        return new ProcessResult(ProcessResult.SUCCESS,Constants.INTERCEPT_SUCCESS_MESSAGE);
    }
    public void save(String alarmTime, HistoryLogDto historyLogDto){
        //空白处遗留建表语句
        Date date = DateUtils.string2Date(alarmTime);
        String yearMonth = DateUtils.dateToStringYM(date);
        String collectionName = TableConstant.SYS_HISTORY_LOGS_TEBLE + yearMonth;
        Boolean exists = historyLogMapper.collectionExists(collectionName, MongoDBType.PRIMARY);
        if (!exists) {
            historyLogMapper.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
        }
        List<HistoryLogDto> list = new ArrayList<>();
        //第三方调用日志入mongo
        list.add(historyLogDto);
        historyLogMapper.insertAll(list,collectionName,MongoDBType.PRIMARY);
    }
}
