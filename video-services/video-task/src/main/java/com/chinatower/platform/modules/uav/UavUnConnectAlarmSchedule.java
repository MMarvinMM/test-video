package com.chinatower.platform.modules.uav;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.AirAlTaskLogReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.event.api.IReceiveApiService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.SecondExtraFieldInfoVo;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.uav.entity.AirCorrespondPhotoRep;
import com.chinatower.platform.modules.uav.entity.AirLinePhotoTask;
import com.chinatower.platform.modules.uav.entity.UavAlarmInfo;
import com.chinatower.platform.modules.uav.service.AirUavService;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WLF
 * @description
 * @date 2025/2/19 16:14
 */
@Component
@Slf4j
public class UavUnConnectAlarmSchedule {
    @Value("${sms.templateCode.uavUnConnectAlarm}")
    private String uavUnConnectAlarmCode;
    @Resource
    private MsgFeignClient msgFeignClient;
    @Resource
    private AirUavService airUavService;
    /**
     * uav失联延迟告警
     *
     * @param
     * @return
     */
    @XxlJob("uavUnConnectAlarm")
    public ReturnT<String> UavUnConnectAlarm(String param) {
        try {
            List<UavAlarmInfo> uavAlarmInfos = airUavService.queryUavAlarmList();
            if (CollectionUtils.isNotEmpty(uavAlarmInfos)) {
                List<String> ids = uavAlarmInfos.stream().map(UavAlarmInfo::getId).collect(Collectors.toList());
                log.info("无人机失联延迟告警任务uavAlarmInfos:{}", JSONObject.toJSON(uavAlarmInfos));
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.DATE_YMDHMS_STR);
                uavAlarmInfos.stream().forEach(uavAlarmInfo -> {
                    String userIds = uavAlarmInfo.getSendUserId();
                    List<UserInfoDto> userInfoDtoList = airUavService.queryUserInfo(Arrays.asList(userIds.split(",")));
                    String phoneNumbersStr = userInfoDtoList.stream()
                            .map(UserInfoDto::getPhonenumber)
                            .collect(Collectors.joining(","));
                    Map<String, Object> map = new HashMap<>();
                    LocalDateTime dateTime = LocalDateTime.parse(uavAlarmInfo.getUnConnectTime(), inputFormatter);
                    String outputDate = dateTime.format(outputFormatter);
                    map.put("deviceName", uavAlarmInfo.getUavName());
                    map.put("time",outputDate);
                    map.put("address", uavAlarmInfo.getAddress());
                    map.put("longitude", uavAlarmInfo.getLongitude());
                    map.put("latitude", uavAlarmInfo.getLatitude());
                    CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
                    commonSmsRequest.setMsgType(Constants.MSG_TYPE_SMS);
                    commonSmsRequest.setPhone(phoneNumbersStr);
                    commonSmsRequest.setRequestMap(JSON.toJSONString(map));
                    commonSmsRequest.setTemplateCode(uavUnConnectAlarmCode);
                    log.info("无人机失联延迟告警任务短信发送信息:{}", JSONObject.toJSON(commonSmsRequest));
                    try{
                        msgFeignClient.sendMessage(commonSmsRequest);
                    }catch (Exception e){
                        log.error("sms error");
                    }
                });
                //批量更新无人机失联告警信息
                airUavService.updateUavAlarmInfo(ids);
            }
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.info("无人机失联延迟告警任务:{}", JSONObject.toJSON(e));
            return ReturnT.FAIL;
        }
    }
}
