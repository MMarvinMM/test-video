package com.chinatower.event.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.flow.FlowLinkService;
import com.chinatower.event.dto.MessagePushDTO;
import com.chinatower.event.dto.delaymessage.OrderDelayMessage;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.event.mongodb.OrderDelayMessageMapper;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.SupervisionMsgSendService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.event.vo.DeviceSiteVO;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.request.*;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.system.api.feign.client.NoticeRuleClient;
import com.chinatower.system.api.model.NoticeExtendTimeVO;
import com.chinatower.system.api.model.NoticeExtendVO;
import com.chinatower.system.api.model.NoticeRuleFilterResultVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SupervisionMsgSendServiceImpl implements SupervisionMsgSendService {

    //告警事件地址
    private static final String ALARM_JUDGE_URL = "/pages/event/event-judge/judge-detail?detail=";
    private static final String ALARM_DISPATCH_URL = "/pages/event/event-dispatch/event-dispatch-detail?detail=";
    private static final String ALARM_DEAL_URL = "/pages/event/event-deal/event-deal-detail/event-deal-detail?detail=";
    private static final String ALARM_VERIFY_URL = "/pages/event/verify/verify-detail?detail=";
    private static final String ALARM_OPERATE_URL = "/pages/event/operate/operate-detail?detail=";
    private static final String ALARM_LEADER_URL = "/pages/event/leader-determine/leader-determine-detail/leader-determine-detail?detail=";
    public static final String UTF8="utf-8";
    public final static String WARN_ORDER_TEBLE = "vm_warning_order_t_";

    @Autowired
    private MsgFeignClient msgFeignClient;

    @Autowired
    private MsgStationAPI msgStationAPI;

    @Autowired
    private FlowLinkService flowLinkService;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private NoticeRuleClient noticeRuleClient;

    @Autowired
    private OrderDelayMessageMapper orderDelayMessageMapper;

    @Override
    public void supervisionSendMsg(MessagePushDTO dto) throws Exception {
        log.info("督办消息推送入口,{}",JSON.toJSONString(dto));
        //推送用户
        List<String> userIds = Arrays.asList(dto.getUserId().split(Constants.COMMA));
        if(CollectionUtils.isEmpty(userIds)){
            log.info("订单号：{}，消息推送用户信息为空",dto.getOrderId());
            return;
        }
        log.info("APP消息推送，{},{}",JSON.toJSONString(userIds),JSON.toJSONString(dto));
        this.sendToApp(dto, userIds);

        if(!StringUtils.isEmpty(dto.getDeviceCode()) && Constants.DEVICE_WARNING_SOURCE.contains(dto.getWarningSource())){
            List<String> deviceCodeArray = new ArrayList<>();
            deviceCodeArray.add(dto.getDeviceCode());
            List<DeviceSiteVO> deviceSiteVOList = deviceRepository.queryDeviceList(deviceCodeArray);
            String deviceName = deviceSiteVOList.get(0).getDeviceName();
            dto.setDeviceName(deviceName);
        }
        //pc消息推送
        this.sendToPc(dto, userIds);

        //微信通知消息（仅限告警上报和事件流转）
        this.sendToWechat(dto);
    }

    /**
     * app极光推送
     * @param dto
     */
    private void sendToApp(MessagePushDTO dto, List<String> userIdList) {
        //极光推送报文
        List<JgPushRequest> jgPushRequestList = new ArrayList<>();
        //真实实时推送的极光报文
        List<JgPushRequest> jgPushRequestRealList = new ArrayList<>();
        //需要过滤的用户报文
        List<NoticeRuleFilterVO> noticeRuleFilterVOList = new ArrayList<>();
        //插入延迟消息记录
        List<OrderDelayMessage> delayList = new ArrayList<>();
        try {
            for (String userId : userIdList) {
                // 处理额外参数，用于展示详情
                HashMap<String, String> extras = new HashMap<>();
                extras.put(Constants.PARAM_ORDER_ID, dto.getOrderId());
                extras.put(Constants.PARAM_ORDER_STATUS, dto.getOrderStatus());
                String appUrl = this.joiningUrl(dto);
                extras.put(Constants.APP_URL, appUrl);
                JgPushRequest jgPushRequest = new JgPushRequest();
                jgPushRequest.setAlias(userId);
                jgPushRequest.setMsgContent(dto.getHandleUserName() + "进行了督办，请尽快处理！");
                jgPushRequest.setMsgTitle(dto.getNotifyTitle()); //事件标题
                jgPushRequest.setNotificationTitle(dto.getNotifyTitle() + " 督办"); //事件标题+督办
                // 据君南侧要求改标题
                if(Constants.ONE.equals(dto.getEnableAcceptanceMode())){
                    jgPushRequest.setOrderStatusName("待受理");
                }else{
                    if(Constants.TWO.equals(dto.getRealLinkType())){
                        jgPushRequest.setOrderStatusName("待调度");
                    }else if(Constants.FOUR.equals(dto.getRealLinkType())){
                        jgPushRequest.setOrderStatusName("待处置");
                    }else if(Constants.ONE.equals(dto.getRealLinkType())){
                        jgPushRequest.setOrderStatusName("待研判");
                    }else if(Constants.FIVE.equals(dto.getRealLinkType())){
                        jgPushRequest.setOrderStatusName("待核实");
                    }else{
                        jgPushRequest.setOrderStatusName(EnumUtil.FlowNode.getName(dto.getRealLinkType()));
                    }
                }
                jgPushRequest.setExtrasparam(JSON.toJSONString(extras));
                jgPushRequest.setAddress(dto.getAddress());
                jgPushRequest.setAppVerCode(dto.getAppVerCode());
                jgPushRequest.setIndustryCode(dto.getIndustryCode());
                jgPushRequest.setType(Constants.ONE);
                jgPushRequestList.add(jgPushRequest);
                NoticeRuleFilterVO vo = new NoticeRuleFilterVO();
                if(StringUtils.isEmpty(dto.getFlowOverTime())){
                    vo.setAlarmTime(dto.getAlarmTime());
                }else{
                    vo.setAlarmTime(dto.getFlowOverTime());
                }
                vo.setIndustryCode(dto.getIndustryCode());
                vo.setIsNow(Constants.ONE);
                vo.setUserId(userId);
                vo.setOrderId(dto.getOrderId());
                vo.setWarningSource(dto.getWarningSource());
                vo.setWarningTypeId(dto.getTypeValue());
                noticeRuleFilterVOList.add(vo);
            }
            log.info("SupervisionMsgSendServiceImpl-sendToApp-极光推送过滤用户req:"+JSON.toJSONString(noticeRuleFilterVOList));
            Result<List<NoticeRuleFilterResultVO>> result=noticeRuleClient.filterUserList(noticeRuleFilterVOList);
            log.info("SupervisionMsgSendServiceImpl-sendToApp-极光推送过滤用户resp:"+JSON.toJSONString(result));
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
                            }
                            //插入延迟消息表
                            if(!CollectionUtils.isEmpty(delayList)){
                                orderDelayMessageMapper.insertAll(delayList, TableConstant.SYS_ORDER_DELAY_TEBLE, MongoDBType.PRIMARY);
                            }
                        }
                    }
                }
            }
            //调用极光消息批量发送
            log.info("SupervisionMsgSendServiceImpl-sendToApp-极光推送过滤后督办消息报文:"+JSON.toJSONString(jgPushRequestRealList));
            msgFeignClient.batchPushNews(jgPushRequestRealList);
        } catch (Exception e) {
            log.error("SupervisionMsgSendServiceImpl-sendToApp组装报文出错:",e);
        }
    }

    public void sendToPc(MessagePushDTO dto, List<String> userIdList) throws Exception {
        List<MsgBody> msgBodyList = new ArrayList<>();
        //获取告警流程配置自定义、默认配置信息
        if (!StringUtils.isEmpty(dto.getAlarmFlowId()) && !StringUtils.isEmpty(dto.getLinkId())) {
            Long alarmFlowId = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate()) && dto.getFlowTemplate().equals(dto.getAlarmFlowId())?null:Long.valueOf(dto.getAlarmFlowId());
            Long flowTemplate = org.apache.commons.lang3.StringUtils.isNotBlank(dto.getFlowTemplate())?Long.valueOf(dto.getFlowTemplate()):null;
            VmFlowLinkAttrDto vmFlowLinkAttrDto = flowLinkService.getFlowLinkAttrBycondition(dto.getAppVerCode(), dto.getIndustryCode(), dto.getTenantId(),
                    flowTemplate, alarmFlowId, Integer.parseInt(dto.getLinkId()));
            if (vmFlowLinkAttrDto != null) {
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
                for (String userId : userIdList) {
                    MsgBody msgBody = new MsgBody();
                    msgBody.setAddress(dto.getAddress());
                    msgBody.setMsgId(dto.getOrderId());
                    msgBody.setNotifyTitle(dto.getNotifyTitle());
                    //通知类型:1：告警消息 5：系统消息，6 ：超期提醒，7：督办提醒
                    msgBody.setNotifyType(Constants.SEVEN);
                    msgBody.setOrderStatus(dto.getOrderStatus());
                    msgBody.setUserId(userId);
                    //判断当前时间是否在该时间区间内（HH:mm:ss）,在区间内才提示消息
                    if (timeInterval) {
                        msgBody.setAlarmFlag(Constants.PARAM_ALARM_FLAG_1);
                    }else {
                        msgBody.setAlarmFlag(Constants.PARAM_ALARM_FLAG_0);
                    }
                    msgBody.setShowType(Constants.SHOW_TYPE_ALL);
                    msgBody.setAppVerCode(dto.getAppVerCode());
                    msgBody.setIndustryCode(dto.getIndustryCode());
                    msgBody.setSoundCloseType(soundCloseType);
                    msgBody.setSoundDuration(soundDuration);
                    msgBody.setSoundId(soundId);
                    msgBody.setAlarmSource(dto.getWarningSource());
                    msgBody.setDeviceName(dto.getDeviceName());
                    msgBody.setDeviceCode(dto.getDeviceCode());
                    msgBodyList.add(msgBody);
                }
            }
        }
        log.info("SupervisionMsgSendServiceImpl-sendToPc-PC消息推送，{}",JSON.toJSONString(msgBodyList));
        try {
            if (!CollectionUtils.isEmpty(msgBodyList)) {
                msgStationAPI.sendMsg2TargetServiceBatch(msgBodyList);
            }
        }catch (Exception e){
            log.error("SupervisionMsgSendServiceImpl-sendToPc-PC消息推送消息异常,异常信息：",e);
        }
    }

    /**
     * 判断当前时间是否在配置时间区间内
     * @param nowTime 当前时间
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    private boolean compareTime(String[] nowTime, String[] beginTime, String[] endTime) {
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
     * 微信小程序督办消息推送
     * @param dto
     */
    private void sendToWechat(MessagePushDTO dto){
        List<WxPushRequest> wxMsgDataList = new ArrayList<>();
        try{
            WxPushRequest wxPushRequest = new WxPushRequest();
            WxMsgData wxMsgData = new WxMsgData();
            wxPushRequest.setId(dto.getOrderId());
            wxPushRequest.setUserIds(dto.getUserId().split(Constants.COMMA));
            wxMsgData.setFirst("设备告警通知");
            wxMsgData.setTime3(dto.getAlarmTime()); //告警时间
            wxMsgData.setThing4(dto.getAddress()); //告警地点
            if(Constants.ONE.equals(dto.getEnableAcceptanceMode())){
                wxMsgData.setPhrase9("待受理");
            }else{
                if(Constants.TWO.equals(dto.getOrderStatus())){
                    wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_2);
                }else if(Constants.FOUR.equals(dto.getOrderStatus())){
                    wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_4);
                }else if(Constants.ONE.equals(dto.getOrderStatus())){
                    wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_1);
                }else if(Constants.FIVE.equals(dto.getOrderStatus())){
                    wxMsgData.setPhrase9(Constants.REAL_LINK_TYPE_MSG_5);
                }else{
                    wxMsgData.setPhrase9(EnumUtil.FlowNode.getName(dto.getOrderStatus())); //事件状态
                }
            }
            wxMsgData.setThing8(dto.getNotifyTitle()); //事件标题
            wxMsgData.setThing14(dto.getAddress()); //事发地点
            wxPushRequest.setExtrasparam(wxMsgData);
            wxPushRequest.setIndustryCode(dto.getIndustryCode());
            wxMsgDataList.add(wxPushRequest);
            log.info("微信公众号消息消息推送，{}",JSON.toJSONString(wxPushRequest));
            msgFeignClient.sendWxMessage(wxPushRequest);
        }catch (Exception e){
            log.error("SupervisionMsgSendServiceImpl-sendToWechat消息推送异常,异常信息：",e);
        }

    }

    private String joiningUrl(MessagePushDTO dto) {
        String appUrl = "";
        try {
            String orderStatus = "";
            // 超期提醒推送消息不可编辑
            String ifEdit = "0";  //默认可以编辑 1可以编辑 0不可以编辑
            //研判
            orderStatus = dto.getOrderStatus();
            Map detail = new HashMap();
            detail.put("eventId", dto.getOrderId());
            detail.put(Constants.PARAM_ORDER_STATUS, dto.getOrderStatus());
            detail.put("ifEdit", ifEdit);
            switch (orderStatus) {
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

            appUrl = appUrl + URLEncoder.encode(JSON.toJSONString(detail), UTF8);


        } catch (Exception e) {
            log.error("APP跳转URL拼接异常：", e);
        }
        return appUrl;
    }
}
