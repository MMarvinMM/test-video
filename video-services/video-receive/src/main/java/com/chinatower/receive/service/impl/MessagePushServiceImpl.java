package com.chinatower.receive.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.service.IFlowLinkService;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.MessagePushService;
import com.chinatower.receive.vo.dto.AlarmTypeInfoDTO;
import com.chinatower.receive.vo.dto.MessagePushDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MessagePushServiceImpl implements MessagePushService {

    @Autowired
    private IFlowLinkService IFlowLinkService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public MessagePushDTO batchPushMessage(List<ReceiveAlarmVO> alarms) {
        log.info("====== 告警推送开始，{}", JSON.toJSONString(alarms));
        for (ReceiveAlarmVO alarm : alarms) {
            MessagePushDTO messagePushDTO = new MessagePushDTO();
            setBaseAttributes(messagePushDTO, alarm);

            // 检查必要属性是否为空
            if (StringUtils.isEmpty(messagePushDTO.getAlarmFlowId()) || StringUtils.isEmpty(messagePushDTO.getLinkId())) {
                continue; // 不处理该告警
            }

            // 设置用户类型
            if (!StringUtils.isEmpty(messagePushDTO.getUserId())) {
                messagePushDTO.setUserType(Constants.USER_TYPE_1);
            }
            else if (!StringUtils.isEmpty(messagePushDTO.getRoleId())) {
                messagePushDTO.setUserType(Constants.USER_TYPE_2);
            }

            // 处理短信发送时间配置
            handleMessageSendTime(alarm.getVmFlowLinkAttrDto(), messagePushDTO);

            //flowId等于模板id 则flowId置空
            String flowTemp = alarm.getFlowTemplate();
            Long alarmFlowId = org.apache.commons.lang3.StringUtils.isNotBlank(flowTemp) && flowTemp.equals(messagePushDTO.getAlarmFlowId())?null:Long.valueOf(messagePushDTO.getAlarmFlowId());
            Long flowTemplate = org.apache.commons.lang3.StringUtils.isNotBlank(flowTemp)?Long.valueOf(flowTemp):null;
            if (!StringUtils.isEmpty(messagePushDTO.getMsgNotifyType())) {
                populateMessagePushDTO(messagePushDTO, alarm, alarmFlowId, flowTemplate);

                // 领导批示消息推送
                if (Constants.SYS_MSG_TYPE_3.equals(messagePushDTO.getNotifyType()) && Constants.YES_FLAG.equals(messagePushDTO.getLeaderFlag())) {
                    return messagePushDTO;
                }

                if (Constants.TWO.equals(messagePushDTO.getNotifyType())) {
                    // 运营研判消息推送
                    configureOperationAnalysis(messagePushDTO);
                } else {
                    // 其他通知类型推送
                    messagePushDTO.setWxSendFlag(Constants.ONE);
                }
            }
        }
        return null;
    }

    private void populateMessagePushDTO(MessagePushDTO messagePushDTO, ReceiveAlarmVO alarm, Long alarmFlowId, Long flowTemplate) {
        String address = messagePushDTO.getAddress() == null?"":messagePushDTO.getAddress();
        String alarmTime = messagePushDTO.getAlarmTime() == null?"":messagePushDTO.getAlarmTime();
        String warningTypeId = messagePushDTO.getTypeValue() == null?"":messagePushDTO.getTypeValue();
        AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
        String warningTypeName = (alarmTypeInfo != null) ? alarmTypeInfo.getTypeName() : "";

        //如果不是从事件流转过来有指定用户得，查询该工单匹配流程得角色和用户
        if(!Constants.ONE.equals(messagePushDTO.getFlowFlag())){
            if(alarm.getVmFlowLinkAttrDto() != null){
                //有勾选网格员处理模式
                this.getUserOrRoleList(alarm.getVmFlowLinkAttrDto(),messagePushDTO.getGridId(),alarmFlowId,messagePushDTO.getLinkId(),flowTemplate,messagePushDTO);
            }
        }

        if (StringUtils.isEmpty(messagePushDTO.getAddress())) {
            messagePushDTO.setAddress(address);
        }
        if (StringUtils.isEmpty(messagePushDTO.getNotifyTitle())) {
            messagePushDTO.setNotifyTitle(warningTypeName);
        }

        //包含短信时，通知组装短信通知内容
        if (messagePushDTO.getMsgNotifyType().contains(Constants.MESSAGE_PUSH_TYPE_SMS)) {
            messagePushDTO.setAlarmTime(alarmTime);
            messagePushDTO.setWarningTypeName(warningTypeName);
        }
    }

    private void configureOperationAnalysis(MessagePushDTO messagePushDTO) {
        messagePushDTO.setUserType(Constants.USER_TYPE_1);
        messagePushDTO.setNotifyAdminFlag(Constants.ZERO);

        String warningTypeId = messagePushDTO.getTypeValue() == null?"":messagePushDTO.getTypeValue();
        AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
        String warningTypeName = (alarmTypeInfo != null) ? alarmTypeInfo.getTypeName() : "";

        if (StringUtils.isEmpty(messagePushDTO.getAddress())) {
            messagePushDTO.setAddress(messagePushDTO.getAddress() == null ? "" : messagePushDTO.getAddress());
        }
        if (StringUtils.isEmpty(messagePushDTO.getNotifyTitle())) {
            messagePushDTO.setNotifyTitle(warningTypeName);
        }

        List<String> userIdList = deviceRepository.getAlarmOperateByRole(
                ObjectUtil.isNull(messagePushDTO.getProvinceId()) ? "" : messagePushDTO.getProvinceId().substring(0,4),
                ObjectUtil.isNull(messagePushDTO.getCityId()) ? "" : messagePushDTO.getCityId()
        );
        messagePushDTO.setUserId(String.join(Constants.COMMA, userIdList));
        messagePushDTO.setMsgNotifyType("1,3,4");
        messagePushDTO.setOrderStatus(Constants.ONE);
        messagePushDTO.setIndustryCode(Constants.PARAM_INDUSTRY_CODE_999999);
        messagePushDTO.setAppVerCode(Constants.PARAM_APP_VER_CODE_1);
    }

    private void handleMessageSendTime(VmFlowLinkAttrDto vmFlowLinkAttrDto, MessagePushDTO messagePushDTO) {
        if (vmFlowLinkAttrDto != null) {
            messagePushDTO.setVmFlowLinkAttrDto(vmFlowLinkAttrDto);
            //配置的短信模板时间区间
            String msgSendTimeInterval = vmFlowLinkAttrDto.getMsgSendTimeInterval();
            log.info("ReceiveRepositoryImpl-messagePush-短信模板配置时间区间-"+JSON.toJSONString(msgSendTimeInterval));
            if(!StringUtils.isEmpty(msgSendTimeInterval) && msgSendTimeInterval.contains(Constants.SEP)){
                try{
                    //根据-切分为配置开始时间和结束后时间
                    String[] msgSendTimeArr = msgSendTimeInterval.split(Constants.SEP);
                    if(!msgSendTimeArr[0].contains(Constants.COLON) || !msgSendTimeArr[1].contains(Constants.COLON)){
                        log.info("ReceiveRepositoryImpl-messagePush-短信模板配置时间格式有误-默认发送");
                    }else{
                        //判断当前时间是否在该时间区间内（HH:mm:ss）
                        String[] nowTime = com.chinatower.common.core.utils.DateUtils.dateTimeNow("HH:mm:ss").split(Constants.COLON);
                        String[] beginTime = msgSendTimeArr[0].split(Constants.COLON);
                        String[] endTime = msgSendTimeArr[1].split(Constants.COLON);
                        //如果当前时间不在配置范围内，不予发送短信
                        if(!this.compareTime(nowTime,beginTime,endTime)){
                            messagePushDTO.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                        }
                    }
                }catch (Exception e){
                    log.error("ReceiveRepositoryImpl-messagePush-短信模板配置时间比较报错-默认发送:",e);
                }
            }
            //定时推送不发送短信消息
            if(Constants.ONE.equals(vmFlowLinkAttrDto.getTimePushType())){
                messagePushDTO.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
            }
        }
    }

    private void setBaseAttributes(MessagePushDTO dto, ReceiveAlarmVO alarm) {
        dto.setRegionAlarmFlag(alarm.getRegionAlarmFlag());
        dto.setOrderId(alarm.getWarningOrderId());
        dto.setMsgId(alarm.getWarningOrderId());
        dto.setTenantId(alarm.getTenantId());
        dto.setAlarmTime(alarm.getAlarmTime());
        dto.setAlarmFlowId(alarm.getFlowId());
        dto.setFlowTemplate(alarm.getFlowTemplate());
        dto.setIndustryCode(alarm.getIndustryCode());
        dto.setAppVerCode(alarm.getAppVerCode());
        dto.setLinkId(alarm.getLinkId());
        dto.setNotifyType(alarm.getNotifyType());
        dto.setCreator("");
        dto.setNotifyTitle(alarm.getAlarmBody());
        dto.setTypeValue(String.valueOf(alarm.getAlarmType()));
        dto.setAddress(alarm.getAddress());
        dto.setUserType(alarm.getUserType());
        dto.setMsgNotifyType(alarm.getMsgNotifyType());
        dto.setEnableAcceptanceMode(alarm.getEnableAcceptanceMode());
        String processId = alarm.getProcessId();
        if (!StringUtils.isEmpty(processId)) {
            if (processId.startsWith(Constants.COMMA)) {
                processId = processId.substring(1, processId.length());
            }
            if (processId.endsWith(Constants.COMMA)) {
                processId = processId.substring(0, processId.length() - 1);
            }
        }
        dto.setUserId(processId);
        String roleId = alarm.getRoleId();
        if (!StringUtils.isEmpty(roleId)) {
            if (roleId.startsWith(Constants.COMMA)) {
                roleId = roleId.substring(1, roleId.length());
            }
            if (roleId.endsWith(Constants.COMMA)) {
                roleId = roleId.substring(0, roleId.length() - 1);
            }
        }
        dto.setRoleId(roleId);
        dto.setOrderStatus(alarm.getOrderStatus());
        dto.setCountyId(alarm.getCountyCode());
        dto.setCityId(alarm.getCityCode());
        dto.setProvinceId(alarm.getProvinceCode());
        dto.setWarningSource(alarm.getWarningSource());
        dto.setDeviceCode(alarm.getDeviceCode());
        dto.setRealLinkType(alarm.getRealLinkType());
        dto.setGridId(alarm.getGridId());
        dto.setExtraField(alarm.getExtraField());
        dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_Y);
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
     * 获取流程匹配得所以用户及角色
     */
    public void getUserOrRoleList(VmFlowLinkAttrDto vmFlowLinkAttrDto,String gridId,Long alarmFlowId,String linkId,Long flowTemplate,MessagePushDTO dto){
        //有勾选网格员处理模式
        if(vmFlowLinkAttrDto.getUserType() != null) {
            String gridFlag = null;
            String gridUserList = null;
            if (vmFlowLinkAttrDto.getUserType().contains(Constants.THREE)) {
                //查询是否有网格员
                if (!StringUtils.isEmpty(gridId)) {
                    gridUserList = deviceRepository.getGridUserByGridId(gridId);
                }
                if (!org.apache.commons.lang3.StringUtils.isEmpty(gridUserList)) {
                    gridFlag = Constants.ONE;
                }
            }
            AlarmFlowConfigModel configModel = new AlarmFlowConfigModel();
            configModel.setId(alarmFlowId);
            configModel.setLinkId(Long.valueOf(linkId));
            configModel.setGridFlag(gridFlag);
            configModel.setTemplateId(flowTemplate);
            configModel.setAppVerCode(dto.getAppVerCode());
            configModel.setIndustryCode(dto.getIndustryCode());
            configModel.setWarningSource(dto.getWarningSource());
            List<FlowModel> flowModelList = IFlowLinkService.getFlowLink(configModel);
            FlowModel flowLinkAttrModel = flowModelList.get(0);
            String flowUserList = "";
            String flowRoleList = "";
            List<String> userTypeList = Arrays.asList(vmFlowLinkAttrDto.getUserType().split(Constants.COMMA));
            //用户
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(flowLinkAttrModel.getUserId())) {
                flowUserList = flowLinkAttrModel.getUserId();
            }
            //角色
            if ( org.apache.commons.lang3.StringUtils.isNotEmpty(flowLinkAttrModel.getRoleId())) {
                flowRoleList = flowLinkAttrModel.getRoleId();
            }
            //网格员
            if (userTypeList.contains(Constants.USER_TYPE_3) && gridFlag != null) {
                if (StringUtils.isEmpty(flowUserList)) {
                    flowUserList = gridUserList;
                } else {
                    flowUserList += "," + gridUserList;
                }
            }
            dto.setUserId(flowUserList);
            dto.setRoleId(flowRoleList);
        }
    }
}
