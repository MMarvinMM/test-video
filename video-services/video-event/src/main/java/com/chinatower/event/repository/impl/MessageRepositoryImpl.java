package com.chinatower.event.repository.impl;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dto.delaymessage.OrderDelayMessage;
import com.chinatower.event.dto.smssendstatistics.SmsSendStatisticsDto;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.entity.vo.ExportDocSensorMonitorVo;
import com.chinatower.event.entity.vo.OneFactoryVO;
import com.chinatower.event.entity.vo.SensorMonitorParam;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.mapper.FlowLinkAttrMapper;
import com.chinatower.event.mongodb.OrderDelayMessageMapper;
import com.chinatower.event.repository.*;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.request.*;
import com.chinatower.system.api.feign.client.NoticeRuleClient;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.NoticeExtendTimeVO;
import com.chinatower.system.api.model.NoticeExtendVO;
import com.chinatower.system.api.model.NoticeRuleFilterResultVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;
import com.chinatower.system.api.resp.SysDictDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.MessagePushDTO;
import com.chinatower.event.mapper.MessageSendInfoMapper;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.fegin.client.MsgFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: MessageRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/18 17:37
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class MessageRepositoryImpl implements IMessageRepository {

    @Autowired
    private MsgStationAPI msgStationAPI;

    @Autowired
    private MsgFeignClient msgFeignClient;

    @Autowired
    private MessageSendInfoMapper messageSendInfoMapper;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private ISysService sysService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private ICommonRepository iCommonRepository;

    @Autowired
    private OrderDelayMessageMapper orderDelayMessageMapper;

    @Autowired
    private NoticeRuleClient noticeRuleClient;

    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;

    private static final String STR_WORKBENCH = "workBench";

    /**
     * 短信模板编码
     */
    @Value("${sms.templateCode}")
    private String templateCode;

    /**
     * 短信模板编码
     */
    @Value("${sms.deviceTemplateCode}")
    private String deviceTemplateCode;

    /**
     * 物联告警，短信模板编码
     */
    @Value("${sms.iotTemplateCode}")
    private String iotTemplateCode;
    /**
     * 消息推送入口
     *
     * @param dto
     */
    @Override
    public void messagePush(MessagePushDTO dto) {
        /*if (!StringUtils.isEmpty(dto.getUserId())&&StringUtils.isEmpty(dto.getUserType())) {
            dto.setUserType(Constants.USER_TYPE_1);
        }
        else if (!StringUtils.isEmpty(dto.getRoleId())&&StringUtils.isEmpty(dto.getUserType())) {
            dto.setUserType(Constants.USER_TYPE_2);
        }*/
        log.info("消息推送入口,{}",JSON.toJSONString(dto));
        //推送用户
        List<String> userIdList = new ArrayList<>();
        //消息推送类型
        String msgNotifyType = dto.getMsgNotifyType();
        if (!StringUtils.isEmpty(msgNotifyType)) {
            List<String> pushTypes = new ArrayList<>();
            //推送类型
            if (msgNotifyType.contains(Constants.COMMA)) {
                boolean flag = false;
                //如果同时勾选了PC系统和PC有声，只保留PC有声即可
                if (msgNotifyType.contains(Constants.MESSAGE_PUSH_TYPE_PC_VIOCE)) {
                    flag = true;
                }
                String[] notifyTypes = msgNotifyType.split(Constants.COMMA);
                for (String type : notifyTypes) {
                    if (flag) {
                        if (!Constants.MESSAGE_PUSH_TYPE_PC_SYSTEM.equals(type)) {
                            pushTypes.add(type);
                        }
                    }
                    else {
                        pushTypes.add(type);
                    }
                }
            }
            else {
                pushTypes.add(msgNotifyType);
            }


            //推送角色
            List<String> roleIdList = new ArrayList<>();
            //用户类型为1用户时， userId必传
            //if (Constants.USER_TYPE_1.equals(dto.getUserType())) {
                if (!StringUtils.isEmpty(dto.getUserId())) {
                    if (dto.getUserId().contains(Constants.COMMA)) {
                        String[] userIds = dto.getUserId().split(Constants.COMMA);
                        for (String userId : userIds) {
                            userIdList.add(userId);
                        }
                    }
                    else {
                        userIdList.add(dto.getUserId());
                    }
                }
            //}
            //用户类型，2角色
            //else if (Constants.USER_TYPE_2.equals(dto.getUserType())) {
                //非流转过来时  角色不为空增加角色关联用户的推送
                if (!StringUtils.isEmpty(dto.getRoleId()) && !Constants.ONE.equals(dto.getFlowFlag())) {
                    if (dto.getRoleId().contains(Constants.COMMA)) {
                        String[] roleIds = dto.getRoleId().split(Constants.COMMA);
                        for (String roleId : roleIds) {
                            roleIdList.add(roleId);
                        }
                    }
                    else {
                        roleIdList.add(dto.getRoleId());
                    }
                    if (!StringUtils.isEmpty(dto.getTenantId())) {
                        //根据角色查询用户
                        List<Long> roles = new ArrayList<>();
                        for (String role:roleIdList) {
                            if(!StringUtils.isEmpty(role)){
                                roles.add(Long.parseLong(role));
                            }
                        }
                       List<SysUser> sysUserList = deviceRepository.getUserInfoByLongRoleIdsAndIndustryCode(roles, dto.getTenantId(),dto.getIndustryCode());
                        if (!CollectionUtils.isEmpty(sysUserList)) {
                            for (SysUser sysUser : sysUserList) {
                                userIdList.add(sysUser.getUserId());
                            }
                        }
                    }
                }
            //}
            VmFlowLinkAttrDto vmFlowLinkAttrDto =new VmFlowLinkAttrDto();
            if(dto.getVmFlowLinkAttrDto()!= null){
                vmFlowLinkAttrDto =dto.getVmFlowLinkAttrDto();
            }
            //增加推送租户管理员(非流转时)
            if(!Constants.ONE.equals(dto.getFlowFlag()) && Constants.ONE.equals(vmFlowLinkAttrDto.getCustomAdmin())){
                SysInfo adminUser = flowLinkAttrMapper.queryAdminUser(dto.getTenantId());
                if(adminUser!=null){
                    userIdList.add(adminUser.getUserId());
                }
            }
            if(CollectionUtils.isEmpty(userIdList)){
                log.info("订单号：{}，消息推送用户信息为空",dto.getOrderId());
                return;
            }
            //用户去重
            userIdList=userIdList.stream().distinct().collect(Collectors.toList());
            log.info("MessageRepositoryImpl-messagePush-调用权限过滤前userId:{}",JSON.toJSONString(userIdList));
            //实际推送用户,AI  一键 摄像机  雷达 物联和卫星 告警类型需调用用户权限接口过滤
            List<String> userIdRealList = new ArrayList<>();

            if(!Constants.ONE.equals(dto.getFlowFlag()) &&Constants.ONE.equals(vmFlowLinkAttrDto.getDataFilterPermiss()) && !Constants.TWO.equals(dto.getNotifyType())
                    && !StringUtils.isEmpty(dto.getWarningSource())){
                String flag =Constants.ONE;
                try{
                    Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
                    if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                        flag= suppressDictData.getData().get(0).getDictValue();
                    }
                    if(Constants.ONE.equals(flag)){
                        Result<List<SysUser>> reponse =iCommonRepository.getUserByDeptAndDeviceScope(userIdList,dto.getTenantId(),dto.getDeviceCode(),
                                dto.getIndustryCode(),dto.getAppVerCode(),dto.getWarningSource(),
                                dto.getCityId(),dto.getCountyId(),dto.getProvinceId(),dto.getWarningTypeId());
                        log.info("MessageRepositoryImpl-messagePush-根据设备编号获取有权限的用户列表-resp:{}",JSON.toJSONString(reponse));
                        List<SysUser> sysUserRealList = new ArrayList<>();
                        if(reponse !=null && ResultCode.SUCCESS.getCode() == reponse.getCode()&&reponse.getData()!=null){
                            JSONArray arrayList = JSON.parseArray(JSON.toJSONString(reponse.getData()));
                            for (Object object:arrayList) {
                                sysUserRealList.add(JSON.parseObject(JSON.toJSONString(object),SysUser.class));
                            }
                        }
                        for (SysUser user: sysUserRealList) {
                            userIdRealList.add(user.getUserId());
                        }
                    }else{
                        userIdRealList = userIdList;
                    }
                }catch (Exception e){
                    log.error("MessageRepositoryImpl-messagePush-根据设备编号获取有权限的用户列表erroe-返回原有用户-错误信息:",e);
                    userIdRealList =userIdList;
                }
            }else{
                log.info("MessageRepositoryImpl-messagePush-不调用华苏接口-dto入参:{}",JSON.toJSONString(dto));
                userIdRealList =userIdList;
            }
            log.info("MessageRepositoryImpl-messagePush-调用权限过滤后userId:{}",JSON.toJSONString(userIdRealList));
            if(CollectionUtils.isEmpty(userIdRealList)){
                log.info("订单号：{}，过滤用户后消息推送用户信息为空",dto.getOrderId());
                return;
            }
            //根据推送类型推送
            List<MsgBody> msgBodyList = new ArrayList<>();
            log.info("APP消息推送，{},{}",JSON.toJSONString(userIdRealList),JSON.toJSONString(dto));
            // app消息推送
            this.sendToApp(dto,userIdRealList);
            if(!StringUtils.isEmpty(dto.getDeviceCode()) && Constants.DEVICE_WARNING_SOURCE.contains(dto.getWarningSource())){
                List<String> deviceCodeArray = new ArrayList<>();
                deviceCodeArray.add(dto.getDeviceCode());
                List<DeviceSiteVO> deviceSiteVOList = deviceRepository.queryDeviceList(deviceCodeArray);
                String deviceName = deviceSiteVOList.get(0).getDeviceName();
                dto.setDeviceName(deviceName);
            }
            for (String pushType : pushTypes) {
                try{
                    // pc消息推送
                    if (Constants.MESSAGE_PUSH_TYPE_PC_VIOCE.equals(pushType) || Constants.MESSAGE_PUSH_TYPE_PC_SYSTEM.equals(pushType)) {
                        this.getPcSendMsgParam(vmFlowLinkAttrDto,dto,userIdRealList,pushType,msgBodyList);
                    }
                    // 短信消息推送
                    else if (Constants.MESSAGE_PUSH_TYPE_SMS.equals(pushType) && Constants.MSG_SEND_TIME_TYPE_Y.equals(dto.getMsgSendTimeFlag())) {
                        try {
                            List<SmsSendStatisticsDto> insertList =new ArrayList<>();
                            String todayString = DateUtils.date2Str(new Date(),"yyyy-MM-dd");
                            //根据用户ID查询用户信息
                            log.info("短信消息推送，{},{}", JSON.toJSONString(userIdRealList), JSON.toJSONString(dto));
                            SmsSendStatisticsDto smsSendStatisticsDto = new SmsSendStatisticsDto();
                            smsSendStatisticsDto.setSendDate(todayString);
                            smsSendStatisticsDto.setUserList(userIdRealList);
                            String smaCount=vmFlowLinkAttrDto.getTimeLimitPush();

                            //查询上述可发送用户在当日未超短信数量的用户
                            List<SmsSendStatisticsDto> smsSendStatisticsDtoList = messageSendInfoMapper.getSmsSendStatistics(smsSendStatisticsDto);
                            List<SysUserVO> sysUserList = deviceRepository.getUserInfoByUserIds(userIdRealList);
                            String phoneNumber="";
                            for (SysUserVO sysUser : sysUserList) {
                                //发送短信统计没有该用户
                                String flag =Constants.ZERO;
                                for (SmsSendStatisticsDto smsSendStatistics:smsSendStatisticsDtoList) {
                                    if(sysUser.getUserId().equals(smsSendStatistics.getUserId())){
                                        flag =Constants.ONE;
                                        if (!StringUtils.isEmpty(sysUser.getPhonenumber()) && (StringUtils.isEmpty(smaCount) || smsSendStatistics.getSendCount()<Integer.valueOf(smaCount))) {
                                            if(StringUtils.isEmpty(phoneNumber)){
                                                phoneNumber=sysUser.getPhonenumber();
                                            }else{
                                                phoneNumber+=Constants.COMMA+sysUser.getPhonenumber();
                                            }
                                        }else{
                                            log.info("该用户不再发送短信短信数量超限不再推送:"+sysUser.getUserId()+"-短信配置限制:"+smaCount);
                                        }
                                        break;
                                    }
                                }
                                if(Constants.ZERO.equals(flag)){
                                    if (!StringUtils.isEmpty(sysUser.getPhonenumber())) {
                                        if(StringUtils.isEmpty(phoneNumber)){
                                            phoneNumber=sysUser.getPhonenumber();
                                        }else{
                                            phoneNumber+=Constants.COMMA+sysUser.getPhonenumber();
                                        }
                                    }
                                    SmsSendStatisticsDto insertSmsSendStatisticsDto = new SmsSendStatisticsDto();
                                    insertSmsSendStatisticsDto.setUserId(sysUser.getUserId());
                                    insertSmsSendStatisticsDto.setSendDate(todayString);
                                    insertList.add(insertSmsSendStatisticsDto);
                                }
                            }
                            if(!StringUtils.isEmpty(phoneNumber)){
                                //根据用户ID查询用户信息
                                log.info("短信消息推送实际用户，{},{}", JSON.toJSONString(phoneNumber), JSON.toJSONString(dto));
                                this.sendMsg(dto,phoneNumber);
                            }


                            //更新上述用户当日短信数量加1
                            if(!CollectionUtils.isEmpty(smsSendStatisticsDtoList)){
                                smsSendStatisticsDto.setUserList(smsSendStatisticsDtoList.stream().map(user -> user.getUserId()).collect(Collectors.toList()));
                                messageSendInfoMapper.updateSmsSendStatistics(smsSendStatisticsDto);
                            }
                            //插入上述用户当日第一次接收短信消息数据
                            if(!CollectionUtils.isEmpty(insertList)){
                                messageSendInfoMapper.insertSmsSendStatistics(insertList);
                            }

                        } catch (Exception e) {
                            log.error("短信消息推送异常-推送类型:", e);
                        }
                    }
                }catch (Exception e){
                    log.error("消息推送异常-推送类型:{}",e);
                }

            }
            log.info("PC消息推送，{}",JSON.toJSONString(msgBodyList));
            try {
                if (!CollectionUtils.isEmpty(msgBodyList)) {
                    msgStationAPI.sendMsg2TargetServiceBatch(msgBodyList);
                }
            }catch (Exception e){
                log.error("PC消息推送消息异常，异常信息:{}",e);
            }
            //微信通知消息（仅限告警上报和事件流转）
            try {
                if(Constants.ONE.equals(dto.getWxSendFlag())){
                    WxPushRequest wxPushRequest = new WxPushRequest();
                    WxMsgData wxMsgData = new WxMsgData();
                    wxPushRequest.setId(dto.getOrderId());
                    wxPushRequest.setUserIds(userIdRealList.toArray(new String[userIdRealList.size()]));
                    wxMsgData.setFirst("");
                    wxMsgData.setTime3(dto.getAlarmTime());
                    wxMsgData.setThing4(dto.getAddress());
                    wxMsgData.setThing8(dto.getNotifyTitle());
                    if(Constants.ONE.equals(dto.getEnableAcceptanceMode())){
                        wxMsgData.setPhrase9("待受理");
                    }else{
                        if(Constants.TWO.equals(dto.getRealLinkType())){
                            wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_2);
                        }else if(Constants.FOUR.equals(dto.getRealLinkType())){
                            wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_4);
                        }else if(Constants.ONE.equals(dto.getRealLinkType())){
                            wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_1);
                        }else if(Constants.FIVE.equals(dto.getRealLinkType())){
                            wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_5);
                        }else{
                            wxMsgData.setPhrase9(EnumUtil.FlowNode.getName(dto.getRealLinkType()));
                        }
                    }
                    wxPushRequest.setExtrasparam(wxMsgData);
                    wxPushRequest.setIndustryCode(dto.getIndustryCode());
                    log.info("微信公众号消息消息推送，{}",JSON.toJSONString(wxPushRequest));
                    msgFeignClient.sendWxMessage(wxPushRequest);
                }
            }catch (Exception e){
                log.error("微信公众号消息推送消息异常,异常信息:{}",e);
            }

        }
    }

    /**
     * pc消息推送参数封装
     * @param vmFlowLinkAttrDto
     * @param dto
     * @param userIdRealList
     * @param pushType
     * @param msgBodyList
     */
    public void getPcSendMsgParam(VmFlowLinkAttrDto vmFlowLinkAttrDto,MessagePushDTO dto,List<String> userIdRealList,String pushType,List<MsgBody> msgBodyList){
        String soundCloseType = vmFlowLinkAttrDto.getTimeRemind();
        //手动关闭入参枚举值为2，本地枚举值为0
        if(Constants.ZERO.equals(soundCloseType)){
            soundCloseType = Constants.TWO;
        }
        String timeRemindAuto = vmFlowLinkAttrDto.getTimeRemindAuto();
        String soundDuration  = vmFlowLinkAttrDto.getTimeRemindNumber();
        if(StringUtils.isEmpty(soundDuration)){
            soundDuration = Constants.FIVE;
        }else{
            if(Constants.ONE.equals(timeRemindAuto)){
                soundDuration = String.valueOf(60 * Integer.parseInt(soundDuration));
            }
        }
        String soundId = vmFlowLinkAttrDto.getVoiceAlram();
        if(StringUtils.isEmpty(soundDuration)){
            soundDuration = Constants.FIVE;
        }
        if(StringUtils.isEmpty(soundId)){
            soundId=Constants.ONE;
        }
        boolean timeInterval =false;
        //判断当前时间是否在该时间区间内（HH:mm:ss）
        String[] nowTime = com.chinatower.common.core.utils.DateUtils.dateTimeNow("HH:mm:ss").split(":");
        if(!StringUtils.isEmpty(vmFlowLinkAttrDto.getTimeStartAlram()) && !StringUtils.isEmpty(vmFlowLinkAttrDto.getTimeEndAlram())){
            String[] beginTime = vmFlowLinkAttrDto.getTimeStartAlram().split(":");
            String[] endTime = vmFlowLinkAttrDto.getTimeEndAlram().split(":");
            //判断当前时间是否在告警声音配置时间区间内
            timeInterval = this.compareTime(nowTime,beginTime,endTime);
        }else{
            timeInterval=true;
        }
      /*  String deviceName = "";
        if(!StringUtils.isEmpty(dto.getDeviceCode()) && Constants.DEVICE_WARNING_SOURCE.contains(dto.getWarningSource())){
            List<String> deviceCodeArray = new ArrayList<>();
            deviceCodeArray.add(dto.getDeviceCode());
            List<DeviceSiteVO> deviceSiteVOList = deviceRepository.queryDeviceList(deviceCodeArray);
            deviceName = deviceSiteVOList.get(0).getDeviceName();
            dto.setDeviceName(deviceName);
        }*/
        for (String userId : userIdRealList) {
            MsgBody msgBody = new MsgBody();
            msgBody.setDeviceCode(dto.getDeviceCode());
            msgBody.setAlarmSource(dto.getWarningSource());
            msgBody.setDeviceName(dto.getDeviceName());
            msgBody.setAddress(dto.getAddress());
            msgBody.setMsgId(dto.getOrderId());
            msgBody.setNotifyTitle(dto.getNotifyTitle());
            //通知类型:1：告警消息 5：系统消息，6 ：超期提醒
            msgBody.setNotifyType(Constants.NOTIFY_TYPE_1);
            msgBody.setOrderStatus(dto.getOrderStatus());
            if(Constants.ONE.equals(dto.getEnableAcceptanceMode())){
                msgBody.setOrderStatusName("待受理");
            }else{
                if(Constants.TWO.equals(dto.getOrderStatus())){
                    msgBody.setOrderStatusName("待调度");
                }else if(Constants.FOUR.equals(dto.getOrderStatus())){
                    msgBody.setOrderStatusName("待处置");
                }else if(Constants.ONE.equals(dto.getOrderStatus())){
                    msgBody.setOrderStatusName("待研判");
                }else if(Constants.FIVE.equals(dto.getOrderStatus())){
                    msgBody.setOrderStatusName("待核实");
                }
            }
            msgBody.setUserId(userId);
            //判断当前时间是否在该时间区间内（HH:mm:ss）,在区间内才提示消息
            if (Constants.MESSAGE_PUSH_TYPE_PC_VIOCE.equals(pushType) && timeInterval) {
                msgBody.setAlarmFlag(Constants.PARAM_ALARM_FLAG_1);
            }
            else {
                msgBody.setAlarmFlag(Constants.PARAM_ALARM_FLAG_0);
            }
            msgBody.setShowType(Constants.SHOW_TYPE_ALL);
            msgBody.setAppVerCode(dto.getAppVerCode());
            msgBody.setIndustryCode(dto.getIndustryCode());
            msgBody.setSoundCloseType(soundCloseType);
            msgBody.setSoundDuration(soundDuration);
            msgBody.setSoundId(soundId);
            msgBodyList.add(msgBody);
        }
        if(Constants.ONE.equals(vmFlowLinkAttrDto.getIfLaterRemind())){
            List<OrderDelayMessage> delayList = new ArrayList<>();
            String collectionName = TableConstant.SYS_ORDER_DELAY_TEBLE;
            //流程属于稍后提醒,插入稍后提醒表，并清空pc消息推送
            for (MsgBody msgBody:msgBodyList) {
                OrderDelayMessage delay = new OrderDelayMessage();
                BeanUtil.copyProperties(msgBody,delay);
                delay.setFlowId(String.valueOf(vmFlowLinkAttrDto.getAlarmFlowId()));
                delay.setLinkId(String.valueOf(vmFlowLinkAttrDto.getLinkId()));
                delay.setMessageJson(JSON.toJSONString(msgBody));
                delay.setCreateTime(DateUtils.dateLaterString(DateUtils.dateToString(new Date()), Constants.EIGHT_NUM));
                delay.setOrderId(msgBody.getMsgId());
                delay.setUserId(msgBody.getUserId());
                delay.setOperateStatus(Constants.ZERO);
                delay.setAlarmTime(dto.getAlarmTime());
                delay.setSource(Constants.ZERO);
                delayList.add(delay);
            }
            //插入延迟消息记录表
            if(!CollectionUtils.isEmpty(delayList)){
                orderDelayMessageMapper.insertAll(delayList,collectionName, MongoDBType.PRIMARY);
            }
        }
    }


    public void sendMsg(MessagePushDTO dto,String phoneNumber){
        HashMap map = new HashMap<>();
        map.put("dateTime", dto.getAlarmTime());
        map.put("address", dto.getAddress());
        map.put("type", dto.getWarningTypeName());
        // 内网推送
        CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
        if(!StringUtils.isEmpty(dto.getDeviceCode()) && Constants.DEVICE_WARNING_SOURCE.contains(dto.getWarningSource())){
            map.put("deviceName",dto.getDeviceName());
            commonSmsRequest.setTemplateCode(deviceTemplateCode);
        }else if (!StringUtils.isEmpty(dto.getExtraField()) && Constants.WARNING_SOURCE_8.equals(dto.getWarningSource())) {
            //v1.6.80 针对物联告警，在实时告警短信中增加‘描述’
            map.put("desc", getDescription(dto.getExtraField()));
            commonSmsRequest.setTemplateCode(iotTemplateCode);  //givh_gjdd_iot
        }else{
            //templateCode:givh_gjdd
            commonSmsRequest.setTemplateCode(templateCode);
        }
        commonSmsRequest.setMsgType(Constants.MSG_TYPE_SMS);
        //commonSmsRequest.setPhone(sysUser.getPhonenumber());
        commonSmsRequest.setPhone(phoneNumber);
        commonSmsRequest.setRequestMap(JSON.toJSONString(map));

        commonSmsRequest.setAppVerCode(dto.getAppVerCode());
        commonSmsRequest.setIndustryCode(dto.getIndustryCode());
        msgFeignClient.sendMessage(commonSmsRequest);
        /*if(!StringUtils.isEmpty(phoneNumber)){
            String[] phone = phoneNumber.split(",");
            for (String number:phone) {
                MsgRequestVO requestVO = new MsgRequestVO();
                requestVO.setSmsTemplateCode(templateCode);
                requestVO.setPhoneNumber(number);
                requestVO.setParaMap(JSON.toJSONString(map));
                msgFeignClient.innovationSendMessage(requestVO);
            }
        }*/
    }

    /**
     * 拼接物联告警描述信息
     * @param extraField
     * @return
     */
    private String getDescription(String extraField) {
        ExportDocSensorMonitorVo param = JSONObject.parseObject(extraField, ExportDocSensorMonitorVo.class);
        String extraFieldResult = "";
        for (SensorMonitorParam vo:param.getMonitors()) {
            String value = "";
            List<OneFactoryVO> valueList = vo.getNewMonitors();
            for (OneFactoryVO oneFactoryVO : valueList) {
                if (value != ""){
                    value += ",";
                }
                value += " " + oneFactoryVO.getItemName() + ": " + oneFactoryVO.getItemCode();
            }
            extraFieldResult += value;
        }
        return extraFieldResult;
    }

    public SysUser getUser(String userId,String appVerCode,String industryCode,String deviceCode){
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setAppVerCode(appVerCode);
        user.setIndustryCode(industryCode);
        user.setDeviceCodeList(Arrays.asList(deviceCode));
        return user;
    }

    /**
     * 判断当前时间是否在配置时间区间内
     * @param nowTime 当前时间
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public boolean compareTime(String[] nowTime,String[] beginTime,String[] endTime){
        int nowTotal=Integer.valueOf(nowTime[0])*3600+Integer.valueOf(nowTime[1])*60+Integer.valueOf(nowTime[2]);
        int beginTotal=Integer.valueOf(beginTime[0])*3600+Integer.valueOf(beginTime[1])*60+Integer.valueOf(beginTime[2]);
        int endTotal=Integer.valueOf(endTime[0])*3600+Integer.valueOf(endTime[1])*60+Integer.valueOf(endTime[2]);
        if(nowTotal >= beginTotal && nowTotal <= endTotal){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 极光推送接口
     * @param dto
     */
    private void sendToApp(MessagePushDTO dto,List<String> userIdRealList) {
        try {
            //所有推送极光报文
            List<JgPushRequest> jgPushRequestList = new ArrayList<>();
            //真实实时推送的极光报文
            List<JgPushRequest> jgPushRequestRealList = new ArrayList<>();
            //需要过滤的用户报文
            List<NoticeRuleFilterVO> noticeRuleFilterVOList = new ArrayList<>();
            //插入延迟消息记录
            List<OrderDelayMessage> delayList = new ArrayList<>();
            for (String userid:userIdRealList) {
                JgPushRequest jgPushRequest = new JgPushRequest();
                jgPushRequest.setType(Constants.ONE);
                jgPushRequest.setAlias(userid);
                jgPushRequest.setMsgTitle(dto.getNotifyTitle());
                jgPushRequest.setMsgContent(dto.getAddress());
                // 处理额外参数，用于展示详情
                HashMap<String, String> extras = new HashMap<>();


                if (Constants.SYS_MSG_TYPE_1.equals(dto.getNotifyType())) {
                    // 告警
                    extras.put("orderId", dto.getOrderId());
                    extras.put(Constants.PARAM_ORDER_STATUS, dto.getRealLinkType());
                    String appUrl = this.joiningUrl(dto);
                    extras.put(Constants.APP_URL, appUrl);  //事件详情
                    // 据君南侧要求改标题
                    if(Constants.ONE.equals(dto.getEnableAcceptanceMode())){
                        jgPushRequest.setNotificationTitle("待受理");
                    }else{
                        if(Constants.TWO.equals(dto.getRealLinkType())){
                            jgPushRequest.setNotificationTitle("待调度");
                        }else if(Constants.FOUR.equals(dto.getRealLinkType())){
                            jgPushRequest.setNotificationTitle("待处置");
                        }else if(Constants.ONE.equals(dto.getRealLinkType())){
                            jgPushRequest.setNotificationTitle("待研判");
                        }else if(Constants.FIVE.equals(dto.getRealLinkType())){
                            jgPushRequest.setNotificationTitle("待核实");
                        }else{
                            jgPushRequest.setNotificationTitle(EnumUtil.FlowNode.getName(dto.getRealLinkType()));
                        }
                    }
                }
                else if (Constants.SYS_MSG_TYPE_2.equals(dto.getNotifyType())) {
                    // 运营研判
                    extras.put("warningOrderId", dto.getOrderId());
                    String appUrl = this.joiningUrl(dto);
                    extras.put(Constants.APP_URL, appUrl);  //研判详情
                    jgPushRequest.setNotificationTitle("待处理");
                }
                else if (Constants.SYS_MSG_TYPE_3.equals(dto.getNotifyType())) {
                    // 领导批示
                    extras.put("orderId", dto.getOrderId());
                    extras.put(Constants.PARAM_ORDER_STATUS, dto.getOrderStatus());
                    extras.put(Constants.AUDIT_ID, dto.getOrderId());
                    String appUrl = this.joiningUrl(dto);
                    extras.put(Constants.APP_URL, appUrl);  //领导批示详情
                    jgPushRequest.setNotificationTitle("待批示");
                }
                else if (Constants.SYS_MSG_TYPE_4.equals(dto.getNotifyType())) {
                    // 巡查任务
                    extras.put("taskId", dto.getMsgId());
                    extras.put("taskStatus", dto.getRealLinkType());
                    extras.put("taskDesc", dto.getAddress());
                    if ("0".equals(dto.getRealLinkType())) {
                        jgPushRequest.setNotificationTitle("待认领");
                    }
                    else if ("2".equals(dto.getRealLinkType())) {
                        jgPushRequest.setNotificationTitle("待处理");
                    }
                }
                jgPushRequest.setExtrasparam(JSON.toJSONString(extras));
                jgPushRequest.setAppVerCode(dto.getAppVerCode());
                jgPushRequest.setIndustryCode(dto.getIndustryCode());
                log.info("调用极光推送接口入参:{}", JSON.toJSONString(jgPushRequest));
                jgPushRequestList.add(jgPushRequest);
                NoticeRuleFilterVO vo = new NoticeRuleFilterVO();
                if(StringUtils.isEmpty(dto.getFlowOverTime())){
                    vo.setAlarmTime(dto.getAlarmTime());
                }else{
                    vo.setAlarmTime(dto.getFlowOverTime());
                }
                vo.setIndustryCode(dto.getIndustryCode());
                vo.setIsNow(Constants.ONE);
                vo.setUserId(userid);
                vo.setOrderId(dto.getOrderId());
                vo.setWarningSource(dto.getWarningSource());
                if(StringUtils.isEmpty(dto.getTypeValue()) || Constants.PARAM_NULL.equals(dto.getTypeValue())){
                    dto.setTypeValue(dto.getWarningTypeId());
                }
                vo.setWarningTypeId(dto.getTypeValue());
                noticeRuleFilterVOList.add(vo);
                //调用极光接口
                //msgFeignClient.pushNewsByUserId(jgPushRequest);
            }
            if(Constants.PARAM_INDUSTRY_CODE_999999.equals(dto.getIndustryCode())){
                log.info("sendToApp-激光推送铁塔用户报文:"+JSON.toJSONString(jgPushRequestList));

                List<MsgSwitchRequest> userList = new ArrayList<>();
                for (JgPushRequest req : jgPushRequestList) {
                    MsgSwitchRequest request = new MsgSwitchRequest();
                    request.setUserId(req.getAlias());
                    request.setIndustryCode(Constants.PARAM_INDUSTRY_CODE_999999);
                    userList.add(request);
                }
                Map<String, SysMsgSwitch> switchResultMap = new HashMap<>();
                log.info("push-message-sendToApp-queryByUserList-req:"+JSON.toJSONString(userList));
                Result<Map<String, SysMsgSwitch>> switchMap = msgFeignClient.queryByUserList(userList);
                log.info("push-message-sendToApp-queryByUserList-resp:"+JSON.toJSONString(switchMap));
                if(switchMap != null && ResultCode.SUCCESS.getCode() == switchMap.getCode()){
                    switchResultMap = switchMap.getData();
                }
                for (JgPushRequest request:jgPushRequestList) {
                    //实时推送
                    for (String key:switchResultMap.keySet()) {
                        if(key.equals(request.getAlias()+"_"+Constants.PARAM_INDUSTRY_CODE_999999)){
                            request.setMsgType(String.valueOf(switchResultMap.get(key).getAlarmSwitch()));
                            break;
                        }
                    }
                    //request.setMsgType(Constants.ONE);
                    request.setSound(Constants.SOUND_ID);
                    jgPushRequestRealList.add(request);
                }
            }else{
                log.info("sendToApp-激光推送过滤前报文:"+JSON.toJSONString(jgPushRequestList));
                log.info("sendToApp-调用激光过滤用户req:"+JSON.toJSONString(noticeRuleFilterVOList));
                Result<List<NoticeRuleFilterResultVO>> result=noticeRuleClient.filterUserList(noticeRuleFilterVOList);
                log.info("sendToApp-调用激光过滤用户resp:"+JSON.toJSONString(result));
                if(result != null){
                    List<NoticeRuleFilterResultVO> resultList = result.getData();
                    //过滤匹配可发送用户
                    if(resultList != null && !CollectionUtils.isEmpty(resultList)){
                        for (NoticeRuleFilterResultVO resultVo:resultList) {
                            for (JgPushRequest request:jgPushRequestList) {
                                if(resultVo.getUserId().equals(request.getAlias()) && resultVo.getOrderId().equals(dto.getOrderId())
                                        && (resultVo.getIndustryCode().equals(request.getIndustryCode()) || Constants.PARAM_INDUSTRY_CODE_999999.equals(request.getIndustryCode()))
                                        // 增加是否命中规则判断
                                        && !CollectionUtils.isEmpty(resultVo.getNoticeExtendVO())){

                                    List<NoticeExtendVO> noticeExtendVO = resultVo.getNoticeExtendVO();
                                    //极光过滤接口是否返回有配置区间
                                    for (NoticeExtendVO extendVO : noticeExtendVO) {
                                        List<NoticeExtendTimeVO> timeList = extendVO.getTimeList();
                                        if (!CollectionUtils.isEmpty(timeList)){
                                            NoticeExtendTimeVO noticeExtendTimeVO = timeList.get(Constants.ZERO_NUM);
                                            String startTime = noticeExtendTimeVO.getStartTime();
                                            String endTime = noticeExtendTimeVO.getEndTime();
                                            //判断告警时间是否在有效时间段内
                                            if (com.chinatower.common.core.utils.DateUtils.isEffectiveDate(dto.getAlarmTime().substring(10,16), startTime, endTime)) {
                                                //实时推送
                                                request.setMsgType(resultVo.getMsgType());
                                                request.setSound(extendVO.getSound());
                                                jgPushRequestRealList.add(request);
                                                break;
                                            }
                                        }
                                    }
                                    // 过滤所有需要稍后推送的规则插入到稍后推送记录表中  没有则不插入
                                    noticeExtendVO = noticeExtendVO.stream().filter(temp -> Constants.ONE.equals(temp.getIsDelay())).collect(Collectors.toList());
                                    if(!CollectionUtils.isEmpty(noticeExtendVO)){
                                        //插入延迟消息记录表
                                        OrderDelayMessage delay = new OrderDelayMessage();
                                        BeanUtil.copyProperties(dto,delay);
                                        delay.setMessageJson(JSON.toJSONString(request));
                                        delay.setSource(Constants.ONE);
                                        delay.setOperateStatus(Constants.ZERO);
                                        delay.setUserId(request.getAlias());
                                        delay.setOrderId(dto.getOrderId());
                                        //mongo插入当前时间日期加8
                                        delay.setCreateTime(DateUtils.dateLaterString(DateUtils.dateToString(new Date()), Constants.EIGHT_NUM));
                                        delay.setWarningTypeId(dto.getTypeValue());
                                        delay.setNoticeExtendVO(noticeExtendVO);
                                        delay.setMsgType(resultVo.getMsgType());
                                        delayList.add(delay);
                                    }
                                    break;
                                }else if(resultVo.getUserId().equals(request.getAlias()) && resultVo.getOrderId().equals(dto.getOrderId())
                                        && (resultVo.getIndustryCode().equals(request.getIndustryCode()) || Constants.PARAM_INDUSTRY_CODE_999999.equals(request.getIndustryCode()))
                                        // 增加是否命中规则判断
                                        && CollectionUtils.isEmpty(resultVo.getNoticeExtendVO())){
                                    log.info("极光推送常规配置实时发送:{}",JSON.toJSONString(resultVo));
                                    //实时消息推送
                                    request.setMsgType(resultVo.getMsgType());
                                    request.setSound("");
                                    jgPushRequestRealList.add(request);
                                    break;
                                }
                            }
                        }

                        //插入延迟消息表
                        if(!CollectionUtils.isEmpty(delayList)){
                            orderDelayMessageMapper.insertAll(delayList,TableConstant.SYS_ORDER_DELAY_TEBLE, MongoDBType.PRIMARY);
                        }
                    }
                }
            }
            //调用极光消息批量发送
            log.info("sendToApp-激光推送过滤后报文:"+JSON.toJSONString(jgPushRequestRealList));
            msgFeignClient.batchPushNews(jgPushRequestRealList);
        }
        catch (Exception e) {
            log.error("极光推送异常：", e);
        }
    }

    //告警事件地址
    private static final String ALARM_JUDGE_URL = "/pages/event/event-judge/judge-detail?detail=";
    private static final String ALARM_DISPATCH_URL = "/pages/event/event-dispatch/event-dispatch-detail?detail=";
    private static final String ALARM_DEAL_URL = "/pages/event/event-deal/event-deal-detail/event-deal-detail?detail=";
    private static final String ALARM_VERIFY_URL = "/pages/event/verify/verify-detail?detail=";
    private static final String ALARM_OPERATE_URL = "/pages/event/operate/operate-detail?detail=";
    private static final String ALARM_LEADER_URL = "/pages/event/leader-determine/leader-determine-detail/leader-determine-detail?detail=";
    public static final String UTF8="utf-8";

    private String joiningUrl(MessagePushDTO dto) {
        String appUrl ="";
        try{
            String orderStatus ="";
            String ifEdit ="1";  //默认可以编辑 1可以编辑 0不可以编辑
            //研判
            String warningOrderId = "";
            if (Constants.SYS_MSG_TYPE_1.equals(dto.getNotifyType())) {
                orderStatus = dto.getOrderStatus();
                Map detail = new HashMap();
                detail.put("eventId",dto.getOrderId());
                detail.put(Constants.PARAM_ORDER_STATUS,dto.getOrderStatus());
                detail.put(Constants.AUDIT_ID,dto.getAuditId());
                detail.put("ifEdit",ifEdit);
                switch (orderStatus){
                    case "1":
                        appUrl = ALARM_JUDGE_URL;
                        break;
                    case "2":
                        appUrl = ALARM_DISPATCH_URL;
                        break;
                    case "4":
                        appUrl = ALARM_DEAL_URL;
                        break;
                    case "5":
                        appUrl = ALARM_VERIFY_URL;
                        break;
                    default:
                        break;
                }

                appUrl = appUrl + URLEncoder.encode(JSON.toJSONString(detail), UTF8) ;
            }
            else if (Constants.SYS_MSG_TYPE_2.equals(dto.getNotifyType())) {
                // 运营研判
                Map detail = new HashMap();
                detail.put("warningOrderId",dto.getOrderId());
                detail.put(Constants.PARAM_ORDER_STATUS,dto.getOrderStatus());
                appUrl = ALARM_OPERATE_URL + URLEncoder.encode(JSON.toJSONString(detail), UTF8) ;
            }
            else if (Constants.SYS_MSG_TYPE_3.equals(dto.getNotifyType())) {
                // 领导批示
                Map detail = new HashMap();
                detail.put("eventId",dto.getOrderId());
                detail.put(Constants.PARAM_ORDER_STATUS,dto.getRealLinkType());
                detail.put(Constants.AUDIT_ID,dto.getOrderId());
                detail.put("determineStatus",dto.getReminderStatus());
                appUrl = ALARM_LEADER_URL + URLEncoder.encode(JSON.toJSONString(detail), UTF8) ;
            }

        }catch(Exception e){
            log.error("APP跳转URL拼接异常：{}",e);
        }

        return appUrl;
    }
}
