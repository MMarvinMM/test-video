package com.chinatower.platform.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;

import com.chinatower.device.api.ITowerDeviceService;
import com.chinatower.device.api.IVideoDeviceMgmtService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.*;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 处理告警相关公共方法集合
 * @Author lyy
 * @Date 2024/8/15
 * @Version release_v1.6.90
 **/
@Component("warningOrderUtil")
@Slf4j
public class WarningOrderUtil {
    @Resource
    private IVideoDeviceMgmtService iVideoDeviceMgmtService;


    //处理摄像机标签
    public void handleDeviceLabel(List<WarningOrderExport> list, LoginUser loginUser) {
        try {
            log.info("handleDeviceLabel-{}",JSON.toJSONString(list));
            List<String> deviceCodeList = new ArrayList<>();
            for (WarningOrderExport warningOrderEntity : list) {
                String warningSource = warningOrderEntity.getWarningSource();
                String extraField = warningOrderEntity.getExtraField();
                if (Constants.WARNING_SOURCE_11.equals(warningSource) && !StringUtils.isEmpty(extraField)){
                    Object object = JSON.parse(extraField);
                    //摄像机标签
                    List<String> deviceLabel = new ArrayList<>();
                    if (object instanceof JSONObject){
                        String realAlarmSource = JSON.parseObject(extraField).getString(Constants.PARAM_REAL_WARNING_SOURCE);
                        if(!StringUtils.isEmpty(realAlarmSource) && Constants.WARNING_SOURCE_10.equals(realAlarmSource)){
                            //如果是第三方雷达告警，取雷达对象中的摄像机标签
                            JSONObject radarJSONObject = JSON.parseObject(extraField).getJSONObject(Constants.PARAM_RADAR_DEVICE);
                            if (radarJSONObject != null){
                                SatelliteRelDeviceVo radarDevice = radarJSONObject.toJavaObject(SatelliteRelDeviceVo.class);
                                if(radarDevice != null && !CollectionUtils.isEmpty(radarDevice.getDeviceLabel())){
                                    String deviceLabelString = appendDeviceLabel(radarDevice.getDeviceLabel());
                                    warningOrderEntity.setDeviceLabelString(deviceLabelString);
                                }
                            }
                        }else{
                            String deviceLabelStr = JSON.parseObject(extraField).getString(com.chinatower.event.constants.Constants.PARAM_DEVICE_LABEL);
                            JSONArray labelJsonArray = JSON.parseArray(deviceLabelStr);
                            if (!CollectionUtils.isEmpty(labelJsonArray)){
                                deviceLabel = labelJsonArray.toJavaList(String.class);
                                String deviceLabelString = appendDeviceLabel(deviceLabel);
                                warningOrderEntity.setDeviceLabelString(deviceLabelString);
                            }
                        }
                    }else if (object instanceof JSONArray){
                        JSONArray labelJsonArray = JSON.parseArray(extraField).getJSONObject(0).getJSONArray(com.chinatower.event.constants.Constants.PARAM_DEVICE_LABEL);
                        if (!CollectionUtils.isEmpty(labelJsonArray)){
                            deviceLabel = labelJsonArray.toJavaList(String.class);
                            String deviceLabelString = appendDeviceLabel(deviceLabel);
                            warningOrderEntity.setDeviceLabelString(deviceLabelString);
                        }

                    }
                }
                    //AI，一键，摄像机 直接取deviceCode
                if (Constants.WARNING_SOURCE_1.equals(warningSource) || Constants.WARNING_SOURCE_3.equals(warningSource)
                        || Constants.WARNING_SOURCE_4.equals(warningSource) || Constants.WARNING_SOURCE_10.equals(warningSource)){
                    if(!StringUtils.isEmpty(warningOrderEntity.getDeviceCode())){
                        String deviceCode = warningOrderEntity.getDeviceCode();
                        deviceCodeList.add(deviceCode);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(deviceCodeList)){
                DeviceLabelReq deviceLabelReq = new DeviceLabelReq();
                deviceLabelReq.setDeviceCodeList(deviceCodeList);
                deviceLabelReq.setTenantId(loginUser.getUser().getTenantId());
                deviceLabelReq.setIndustryCode(loginUser.getUser().getIndustryCode());
                log.info("handleDeviceLabel-调用queryDeviceLabelInfoByCondition入参入参",JSON.toJSONString(deviceCodeList));
                Result<List<DeviceLabelInfoVO>> result = iVideoDeviceMgmtService.queryDeviceLabelInfoByCondition(deviceLabelReq);
                log.info("handleDeviceLabel-调用queryDeviceLabelInfoByCondition入参返回结果",JSON.toJSONString(result));
                if (result != null && result.getCode() == 200 && result.getData() != null){
                    List<String> labelList = new ArrayList<>();
                    List<DeviceLabelInfoVO> data = result.getData();
                    for (WarningOrderExport warningOrderEntity : list) {
                        if(!Constants.WARNING_SOURCE_11.equals(warningOrderEntity.getWarningSource())){
                            //拼接办理说明
                            StringBuilder builder = new StringBuilder();
                            for (DeviceLabelInfoVO vo : data) {
                                if (!StringUtils.isEmpty(warningOrderEntity.getDeviceCode()) && warningOrderEntity.getDeviceCode().equals(vo.getDeviceCode())){
                                    //AI，一键，摄像机 直接取deviceCode
                                    labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                    for (int i = 0; i < labelList.size(); i++) {
                                        if (i+1 != labelList.size()){
                                            builder.append(labelList.get(i)).append(Constants.CRLF);
                                        }else {
                                            builder.append(labelList.get(i));
                                        }

                                    }
                                    continue;
                                }
                            }
                            warningOrderEntity.setDeviceLabelString(builder.toString());
                        }
                    }
                }
            }

        }catch (Exception e){
            log.info("handleDeviceLabel处理摄像机标签失败{}",e);
        }
    }

    public String appendDeviceLabel(List<String> labelList){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < labelList.size(); i++) {
            if (i+1 != labelList.size()){
                builder.append(labelList.get(i)).append(Constants.CRLF);
            }else {
                builder.append(labelList.get(i));
            }
        }
        return builder.toString();
    }

    //处理摄像机标签
    public void leaderHandleDeviceLabel(List<LeaderExport> list,LoginUser loginUser) {
        try {
            log.info("leaderHandleDeviceLabel-{}",JSON.toJSONString(list));
            List<String> deviceCodeList = new ArrayList<>();
            for (LeaderExport warningOrderEntity : list) {
                String warningSource = warningOrderEntity.getWarningSource();
                String extraField = warningOrderEntity.getExtraField();
                if (Constants.WARNING_SOURCE_11.equals(warningSource) && !StringUtils.isEmpty(extraField)){
                    Object object = JSON.parse(extraField);
                    //摄像机标签
                    List<String> deviceLabel = new ArrayList<>();
                    if (object instanceof JSONObject){
                        String realAlarmSource = JSON.parseObject(extraField).getString(Constants.PARAM_REAL_WARNING_SOURCE);
                        if(!StringUtils.isEmpty(realAlarmSource) && Constants.WARNING_SOURCE_10.equals(realAlarmSource)){
                            //如果是第三方雷达告警，取雷达对象中的摄像机标签
                            JSONObject radarJSONObject = JSON.parseObject(extraField).getJSONObject(Constants.PARAM_RADAR_DEVICE);
                            if (radarJSONObject != null){
                                SatelliteRelDeviceVo radarDevice = radarJSONObject.toJavaObject(SatelliteRelDeviceVo.class);
                                if(radarDevice != null && !CollectionUtils.isEmpty(radarDevice.getDeviceLabel())){
                                    String deviceLabelString = appendDeviceLabel(radarDevice.getDeviceLabel());
                                    warningOrderEntity.setDeviceLabelString(deviceLabelString);
                                }
                            }
                        }else{
                            String deviceLabelStr = JSON.parseObject(extraField).getString(Constants.PARAM_DEVICE_LABEL);
                            JSONArray labelJsonArray = JSON.parseArray(deviceLabelStr);
                            if (!CollectionUtils.isEmpty(labelJsonArray)){
                                deviceLabel = labelJsonArray.toJavaList(String.class);
                                String deviceLabelString = appendDeviceLabel(deviceLabel);
                                warningOrderEntity.setDeviceLabelString(deviceLabelString);
                            }
                        }
                    }else if (object instanceof JSONArray){
                        JSONArray labelJsonArray = JSON.parseArray(extraField).getJSONObject(0).getJSONArray(Constants.PARAM_DEVICE_LABEL);
                        if (!CollectionUtils.isEmpty(labelJsonArray)){
                            deviceLabel = labelJsonArray.toJavaList(String.class);
                            String deviceLabelString = appendDeviceLabel(deviceLabel);
                            warningOrderEntity.setDeviceLabelString(deviceLabelString);
                        }
                    }
                }
                //AI，一键，摄像机 直接取deviceCode
                if (Constants.WARNING_SOURCE_1.equals(warningSource) || Constants.WARNING_SOURCE_3.equals(warningSource)
                        || Constants.WARNING_SOURCE_4.equals(warningSource) || Constants.WARNING_SOURCE_10.equals(warningSource)){
                    if(!StringUtils.isEmpty(warningOrderEntity.getDeviceCode())){
                        String deviceCode = warningOrderEntity.getDeviceCode();
                        deviceCodeList.add(deviceCode);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(deviceCodeList)){
                DeviceLabelReq deviceLabelReq = new DeviceLabelReq();
                deviceLabelReq.setDeviceCodeList(deviceCodeList);
                deviceLabelReq.setTenantId(loginUser.getUser().getTenantId());
                deviceLabelReq.setIndustryCode(loginUser.getUser().getIndustryCode());
                log.info("leaderHandleDeviceLabel-调用queryDeviceLabelInfoByCondition入参入参",JSON.toJSONString(deviceCodeList));
                Result<List<DeviceLabelInfoVO>> result = iVideoDeviceMgmtService.queryDeviceLabelInfoByCondition(deviceLabelReq);
                log.info("leaderHandleDeviceLabel-调用queryDeviceLabelInfoByCondition入参返回结果",JSON.toJSONString(result));
                if (result != null && result.getCode() == 200 && result.getData() != null){
                    List<String> labelList = new ArrayList<>();
                    List<DeviceLabelInfoVO> data = result.getData();
                    for (LeaderExport warningOrderEntity : list) {
                        if(!Constants.WARNING_SOURCE_11.equals(warningOrderEntity.getWarningSource())){
                            //拼接办理说明
                            StringBuilder builder = new StringBuilder();
                            for (DeviceLabelInfoVO vo : data) {
                                if (!StringUtils.isEmpty(warningOrderEntity.getDeviceCode()) && warningOrderEntity.getDeviceCode().equals(vo.getDeviceCode())){
                                    //AI，一键，摄像机 直接取deviceCode
                                    labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                    for (int i = 0; i < labelList.size(); i++) {
                                        if (i+1 != labelList.size()){
                                            builder.append(labelList.get(i)).append(Constants.CRLF);
                                        }else {
                                            builder.append(labelList.get(i));
                                        }

                                    }
                                    continue;
                                }
                            }
                            warningOrderEntity.setDeviceLabelString(builder.toString());
                        }
                    }
                }
            }

        }catch (Exception e){
            log.info("leaderHandleDeviceLabel处理摄像机标签失败{}",e);
        }
    }

    //处理摄像机标签
    public void interceptHandleDeviceLabel(List<AlarmInterceptExport> list,LoginUser loginUser) {
        try {
            log.info("interceptHandleDeviceLabel-{}",JSON.toJSONString(list));
            List<String> deviceCodeList = new ArrayList<>();
            for (AlarmInterceptExport warningOrderEntity : list) {
                String warningSource = warningOrderEntity.getWarningSource();
                //AI，一键，摄像机 直接取deviceCode
                if (Constants.WARNING_SOURCE_1.equals(warningSource) || Constants.WARNING_SOURCE_3.equals(warningSource)
                        || Constants.WARNING_SOURCE_4.equals(warningSource) || Constants.WARNING_SOURCE_10.equals(warningSource)){
                    if(!StringUtils.isEmpty(warningOrderEntity.getDeviceCode())){
                        String deviceCode = warningOrderEntity.getDeviceCode();
                        deviceCodeList.add(deviceCode);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(deviceCodeList)){
                DeviceLabelReq deviceLabelReq = new DeviceLabelReq();
                deviceLabelReq.setDeviceCodeList(deviceCodeList);
                deviceLabelReq.setTenantId(loginUser.getUser().getTenantId());
                deviceLabelReq.setIndustryCode(loginUser.getUser().getIndustryCode());
                log.info("interceptHandleDeviceLabel-调用queryDeviceLabelInfoByCondition入参入参",JSON.toJSONString(deviceCodeList));
                Result<List<DeviceLabelInfoVO>> result = iVideoDeviceMgmtService.queryDeviceLabelInfoByCondition(deviceLabelReq);
                log.info("interceptHandleDeviceLabel-调用queryDeviceLabelInfoByCondition入参返回结果",JSON.toJSONString(result));
                if (result != null && result.getCode() == 200 && result.getData() != null){
                    List<String> labelList = new ArrayList<>();
                    List<DeviceLabelInfoVO> data = result.getData();
                    for (AlarmInterceptExport warningOrderEntity : list) {
                        if(!Constants.WARNING_SOURCE_11.equals(warningOrderEntity.getWarningSource())){
                            //拼接办理说明
                            StringBuilder builder = new StringBuilder();
                            for (DeviceLabelInfoVO vo : data) {
                                if (!StringUtils.isEmpty(warningOrderEntity.getDeviceCode()) && warningOrderEntity.getDeviceCode().equals(vo.getDeviceCode())){
                                    //AI，一键，摄像机 直接取deviceCode
                                    labelList = vo.getLabelInfoList().stream().map(v -> v.getLabelName()).collect(Collectors.toList());
                                    for (int i = 0; i < labelList.size(); i++) {
                                        if (i+1 != labelList.size()){
                                            builder.append(labelList.get(i)).append(Constants.CRLF);
                                        }else {
                                            builder.append(labelList.get(i));
                                        }

                                    }
                                    continue;
                                }
                            }
                            warningOrderEntity.setDeviceLabelString(builder.toString());
                        }

                    }
                }
            }

        }catch (Exception e){
            log.info("interceptHandleDeviceLabel处理摄像机标签失败{}",e);
        }
    }
}
