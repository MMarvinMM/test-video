package com.chinatower.event.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.AlarmTypeInfoDTO;
import com.chinatower.event.dto.VmFlowLinkDto;
import com.chinatower.event.dto.WarnOrderStatusDTO;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IMessageRepository;
import com.chinatower.event.repository.MessagePushService;
import com.chinatower.event.service.OperateService;
import com.chinatower.platform.entity.IVmMessagePushDTO;
import com.chinatower.platform.service.MessageSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能说明: 告警消息推送实现类
 * @author: lyy
 * @date: 2024/08/08
 * @requestID 告警接收业务代码迁移
 */
@Slf4j
@Component
public class MessagePushServiceImpl implements MessagePushService {

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private MessageSendService messageSendService;

    @Override
    public void messagePush(IVmMessagePushDTO dto) {
        log.info(Constants.LOG_MESSAGEPUSH_PARAM + JSON.toJSONString(dto));
        try {
            //默认在短信发送区间内
            dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_Y);

            if (StringUtils.isEmpty(dto.getAlarmFlowId()) || StringUtils.isEmpty(dto.getLinkId())) {
                return;
            }

            String tenantId = dto.getTenantId();
            String linkId = dto.getLinkId();
            String msgNotifyType = dto.getMsgNotifyType(); //消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC系统消息 4、PC告警声音
            String notifyAdminFlag = dto.getNotifyAdminFlag();
            String leaderFlag = dto.getLeaderFlag(); //是否通知领导，0，不通知，1通知
            if (!StringUtils.isEmpty(dto.getUserId())) {
                dto.setUserType(Constants.USER_TYPE_1);
            } else if (!StringUtils.isEmpty(dto.getRoleId())) {
                dto.setUserType(Constants.USER_TYPE_2);
            }

            //flowId等于模板id 则flowId置空
            Long alarmFlowId = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate()) && dto.getFlowTemplate().equals(dto.getAlarmFlowId()) ? null : Long.valueOf(dto.getAlarmFlowId());
            Long flowTemplate = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate()) ? Long.valueOf(dto.getFlowTemplate()) : null;
            VmFlowLinkDto vmFlowLinkDto = flowLinkService.getFlowLinkAndConfig(dto.getAppVerCode(), dto.getIndustryCode(), tenantId, flowTemplate, alarmFlowId, Integer.parseInt(linkId));
            if (vmFlowLinkDto != null) {
                configureMsgSendTime(dto, vmFlowLinkDto.getFlowLinkAttrDto());

                if (!StringUtils.isEmpty(msgNotifyType)) {
                    dto.setMsgNotifyType(msgNotifyType);
                    dto.setNotifyAdminFlag(notifyAdminFlag);
                    String orderId = dto.getOrderId();
                    String address = dto.getAddress() == null ? "" : dto.getAddress();
                    String alarmTime = dto.getAlarmTime() == null ? "" : dto.getAlarmTime();
                    String warningTypeId = dto.getTypeValue() == null ? "" : dto.getTypeValue();
                    try {
                        if (Constants.SYS_MSG_TYPE_2.equals(dto.getNotifyType())) {
                            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId, null);
                            if (alarmOperateDTO != null) {
                                populateDtoFromOperateDTO(dto, alarmOperateDTO);
                            }
                        } else {
                            String alarmTableName = TableConstant.WARN_ORDER_TEBLE + orderId.substring(0, 6);
                            AlarmOrderDTO alarmOrderDTO = new AlarmOrderDTO();
                            alarmOrderDTO.setOrderId(orderId);
                            alarmOrderDTO.setTableName(alarmTableName);
                            WarnOrderStatusDTO warnOrderStatusDTO = warningOrderMapper.getAlarmOrder(alarmOrderDTO);
                            if (warnOrderStatusDTO != null) {
                                populateDtoFromWarnOrderStatusDTO(dto, warnOrderStatusDTO);
                            }
                        }
                    } catch (Exception e) {
                        log.error("根据事件状态以及事件编码查询事发地点，查询告警信息异常", e);
                    }
                    AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
                    String warningTypeName = alarmTypeInfo != null ? alarmTypeInfo.getTypeName() : "";
                    if (StringUtils.isEmpty(dto.getNotifyTitle())) {
                        dto.setNotifyTitle(warningTypeName);
                    }
                    //包含短信时，通知组装短信通知内容
                    if (msgNotifyType.contains(Constants.MESSAGE_PUSH_TYPE_SMS)) {
                        dto.setAlarmTime(alarmTime);
                        dto.setAddress(address);
                        dto.setWarningTypeName(warningTypeName);
                    }
                    //领导批示时判断是否配置通知
                    if (Constants.SYS_MSG_TYPE_3.equals(dto.getNotifyType()) && Constants.YES_FLAG.equals(leaderFlag)) {
                        //messageRepository.messagePush(dto);
                        messageSendService.sendMessagePush(dto);
                    } else {
                        dto.setWarningTypeName(warningTypeName);
                        dto.setWxSendFlag(Constants.ONE);
                        messageSendService.sendMessagePush(dto);
                    }
                }
            }
            if (Constants.TWO.equals(dto.getNotifyType())) {
                // 运营研判消息推送
                performOperationAnalysisPush(dto);
            }
        } catch (Exception e) {
            log.error(MessageConstants.MESSAGE_PUSH_ERROR_MESSAGE, e);
        }
    }

    private void performOperationAnalysisPush(IVmMessagePushDTO dto) {
        dto.setUserType(Constants.USER_TYPE_1);
        dto.setNotifyAdminFlag(Constants.ZERO);
        // 根据事件状态以及事件编码查询事发地点
        String orderId = dto.getOrderId();
        String address = dto.getAddress() == null ? "" : dto.getAddress();
        String warningTypeId = dto.getTypeValue() == null ? "" : dto.getTypeValue();
        String warningTypeName = "";
        try {
            AlarmOperateDTO alarmOperateDTO = operateService.getOperateInfo(orderId, null);
            if (alarmOperateDTO != null) {
                address = alarmOperateDTO.getAddress();
                warningTypeId = alarmOperateDTO.getWarningTypeId();
                warningTypeName = alarmOperateDTO.getWarningTypeName();
                dto.setDeviceCode(alarmOperateDTO.getDeviceCode());
                dto.setCityId(alarmOperateDTO.getCityId());
                dto.setProvinceId(alarmOperateDTO.getProvinceId());
                dto.setCountyId(alarmOperateDTO.getCountyId());
                dto.setWarningSource(alarmOperateDTO.getWarningSource());
            }
        } catch (Exception e) {
            log.error("根据事件状态以及事件编码查询事发地点，查询告警信息异常", e);
        }
        //地址为空时，赋值地址
        if (StringUtils.isEmpty(dto.getAddress()) || !StringUtils.isEmpty(address)) {
            dto.setAddress(address);
        }
        AlarmTypeInfoDTO alarmTypeInfo = deviceRepository.getAlarmTypeInfoById(warningTypeId);
        if (alarmTypeInfo != null) {
            warningTypeName = alarmTypeInfo.getTypeName();
        }
        if (StringUtils.isEmpty(dto.getNotifyTitle())) {
            dto.setNotifyTitle(warningTypeName);
        }
        // 运营研判数据给铁塔角色运营研判下的所有用户进行推送
        List<String> userIdList = deviceRepository.getAlarmOperateByRole(ObjectUtil.isNull(dto.getProvinceId()) ? "" : dto.getProvinceId().substring(0, 4), ObjectUtil.isNull(dto.getCityId()) ? "" : dto.getCityId());
        dto.setUserId(userIdList.stream().collect(Collectors.joining(Constants.COMMA)));
        dto.setMsgNotifyType("1,3,4");
        dto.setOrderStatus(Constants.ONE);
        //行业版本编码
        dto.setIndustryCode(Constants.PARAM_INDUSTRY_CODE_999999);
        dto.setAppVerCode(Constants.PARAM_APP_VER_CODE_1);
        messageSendService.sendMessagePush(dto);
    }

    private void populateDtoFromWarnOrderStatusDTO(IVmMessagePushDTO dto, WarnOrderStatusDTO warnOrderStatusDTO) {
        dto.setGridId(StringUtils.isEmpty(dto.getGridId()) ? warnOrderStatusDTO.getGridId() : dto.getGridId());
        dto.setAddress(StringUtils.isEmpty(dto.getAddress()) ? warnOrderStatusDTO.getAddress() : dto.getAddress());
        dto.setAlarmTime(StringUtils.isEmpty(dto.getAlarmTime()) ? warnOrderStatusDTO.getAlarmTime() : dto.getAlarmTime());
        dto.setWarningTypeId(StringUtils.isEmpty(dto.getWarningTypeId()) ? warnOrderStatusDTO.getWarningTypeId() : dto.getWarningTypeId());
        dto.setIndustryCode(StringUtils.isEmpty(dto.getIndustryCode()) ? warnOrderStatusDTO.getIndustryCode() : dto.getIndustryCode());
        dto.setAppVerCode(StringUtils.isEmpty(dto.getAppVerCode()) ? warnOrderStatusDTO.getTradeVerCode() : dto.getAppVerCode());
        dto.setDeviceCode(StringUtils.isEmpty(dto.getDeviceCode()) ? warnOrderStatusDTO.getDeviceCode() : dto.getDeviceCode());
        dto.setCityId(StringUtils.isEmpty(dto.getCityId()) ? warnOrderStatusDTO.getCityId() : dto.getCityId());
        dto.setProvinceId(StringUtils.isEmpty(dto.getProvinceId()) ? warnOrderStatusDTO.getProvinceId() : dto.getProvinceId());
        dto.setCountyId(StringUtils.isEmpty(dto.getCountyId()) ? warnOrderStatusDTO.getCountyId() : dto.getCountyId());
        dto.setWarningSource(StringUtils.isEmpty(dto.getWarningSource()) ? warnOrderStatusDTO.getWarningSource() : dto.getWarningSource());
        dto.setNotifyTitle(StringUtils.isEmpty(dto.getNotifyTitle()) ? warnOrderStatusDTO.getAlarmBody() : dto.getNotifyTitle());
        dto.setExtraField(StringUtils.isEmpty(dto.getExtraField()) ? warnOrderStatusDTO.getExtraField() : dto.getExtraField());
    }

    private void populateDtoFromOperateDTO(IVmMessagePushDTO dto, AlarmOperateDTO alarmOperateDTO) {
        dto.setGridId(StringUtils.isEmpty(dto.getGridId()) ? alarmOperateDTO.getGridId() : dto.getGridId());
        dto.setAddress(StringUtils.isEmpty(dto.getAddress()) ? alarmOperateDTO.getAddress() : dto.getAddress());
        dto.setAlarmTime(StringUtils.isEmpty(dto.getAlarmTime()) ? alarmOperateDTO.getAlarmTime() : dto.getAlarmTime());
        dto.setWarningTypeId(StringUtils.isEmpty(dto.getWarningTypeId()) ? alarmOperateDTO.getWarningTypeId() : dto.getWarningTypeId());
        dto.setIndustryCode(StringUtils.isEmpty(dto.getIndustryCode()) ? alarmOperateDTO.getTradeId() : dto.getIndustryCode());
        dto.setAppVerCode(StringUtils.isEmpty(dto.getAppVerCode()) ? alarmOperateDTO.getTradeVerCode() : dto.getAppVerCode());
        dto.setDeviceCode(StringUtils.isEmpty(dto.getDeviceCode()) ? alarmOperateDTO.getDeviceCode() : dto.getDeviceCode());
        dto.setCityId(StringUtils.isEmpty(dto.getCityId()) ? alarmOperateDTO.getCityId() : dto.getCityId());
        dto.setProvinceId(StringUtils.isEmpty(dto.getProvinceId()) ? alarmOperateDTO.getProvinceId() : dto.getProvinceId());
        dto.setCountyId(StringUtils.isEmpty(dto.getCountyId()) ? alarmOperateDTO.getCountyId() : dto.getCountyId());
        dto.setWarningSource(StringUtils.isEmpty(dto.getWarningSource()) ? alarmOperateDTO.getWarningSource() : dto.getWarningSource());
        dto.setNotifyTitle(StringUtils.isEmpty(dto.getNotifyTitle()) ? alarmOperateDTO.getAlarmBody() : dto.getNotifyTitle());
    }

    private void configureMsgSendTime(IVmMessagePushDTO dto, VmFlowLinkAttrDto vmFlowLinkAttrDto) {
        if (vmFlowLinkAttrDto != null) {
            dto.setVmFlowLinkAttrDto(vmFlowLinkAttrDto);
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
                            dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
                        }
                    }
                }catch (Exception e){
                    log.error("ReceiveRepositoryImpl-messagePush-短信模板配置时间比较报错-默认发送:",e);
                }
            }
            //定时推送不发送短信消息
            if(Constants.ONE.equals(vmFlowLinkAttrDto.getTimePushType())){
                dto.setMsgSendTimeFlag(Constants.MSG_SEND_TIME_TYPE_N);
            }
        }
    }

    /**
     * 获取流程匹配得所以用户及角色
     */
    public void getUserOrRoleList(VmFlowLinkAttrDto vmFlowLinkAttrDto,String gridId,Long alarmFlowId,String linkId,Long flowTemplate,IVmMessagePushDTO dto){
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
            List<FlowModel> flowModelList = flowLinkService.getFlowLink(configModel);
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
}
