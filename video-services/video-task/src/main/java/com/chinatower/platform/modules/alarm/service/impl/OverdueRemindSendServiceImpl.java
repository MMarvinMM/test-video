package com.chinatower.platform.modules.alarm.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.JudgeStatisticsVO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.OverdueRemindDto;
import com.chinatower.event.util.EnumUtil;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.platform.entity.request.*;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.alarm.dao.JudgeStatisticsDAO;
import com.chinatower.platform.modules.alarm.dao.OverdueRemindMapper;
import com.chinatower.platform.modules.alarm.service.OverdueRemindSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OverdueRemindSendServiceImpl implements OverdueRemindSendService {

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
    private OverdueRemindMapper overdueRemindMapper;

    @Autowired
    private MsgFeignClient msgFeignClient;

    @Autowired
    private MsgStationAPI msgStationAPI;

    @Autowired
    private JudgeStatisticsDAO judgeStatisticsDAO;

    /**
     * 发送超期/受理提醒
     */
    @Override
    public void overdueRemindSendMsg() {
        //获取当前时间年月日时分
        String remindTime = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", new Date());
        log.info("overdueRemindSendMsg-执行查询待发送时间:" + remindTime);

        //查询所有当前时间分钟数的待发送超期/受理提醒
        List<OverdueRemindDto> overdueRemindList = overdueRemindMapper.getOverdueRemindList(remindTime);

        log.info("overdueRemindSendMsg-List:" + JSON.toJSONString(overdueRemindList));
        if(!CollectionUtils.isEmpty(overdueRemindList)){

            //根据告警类型id查询告警类型名称（待补充）
            List<JudgeStatisticsVO> listAlarmType = judgeStatisticsDAO.getAlarmTypeList();
            Map<String, String> alarmTypeMap = listAlarmType.stream().collect(Collectors.toMap(JudgeStatisticsVO::getAlarmTypeId, JudgeStatisticsVO::getAlarmTypeName,
                    (v1, v2) -> v1));

            this.sendToApp(overdueRemindList,alarmTypeMap);
            this.sendToPc(overdueRemindList,alarmTypeMap);
            this.sendToWechat(overdueRemindList,alarmTypeMap);
        }
    }

    /**
     * app极光推送
     * 负责处理推送的用户列表、消息内容组装，并调用极光推送接口发送消息。
     *
     * @param overdueRemindList  超期提醒信息列表
     * @param alarmTypeMap       告警类型映射，用于将告警类型 ID 转换为名称
     */
    public void sendToApp(List<OverdueRemindDto> overdueRemindList,Map<String,String> alarmTypeMap) {
        //所有推送极光报文
        List<JgPushRequest> jgPushRequestList = new ArrayList<>();
        List<MsgSwitchRequest> userList = new ArrayList<>();
        for (OverdueRemindDto dto : overdueRemindList) {
            String[] userIds = dto.getRemindProcessId().split(",");
            for (String userId:userIds) {
                MsgSwitchRequest request = new MsgSwitchRequest();
                request.setUserId(userId);
                request.setIndustryCode(dto.getIndustryCode());
                userList.add(request);
            }
        }
        // 查询用户消息开关状态（调用远程接口）
        Map<String, SysMsgSwitch> switchResultMap = new HashMap<>();
        log.info("sendToApp-queryByUserList-req:"+JSON.toJSONString(userList));
        Result<Map<String, SysMsgSwitch>> switchMap = msgFeignClient.queryByUserList(userList);
        log.info("sendToApp-queryByUserList-resp:"+JSON.toJSONString(switchMap));
        if(switchMap != null && ResultCode.SUCCESS.getCode() == switchMap.getCode()){
            switchResultMap = switchMap.getData();
        }
        // 遍历超期提醒列表，组装极光推送消息
        for (OverdueRemindDto dto : overdueRemindList) {
            try{
                // 获取需要提醒的用户 ID 列表
                String[] userIds = dto.getRemindProcessId().split(",");
                for (String userId:userIds) {
                    // 处理额外参数，用于展示详情
                    HashMap<String, String> extras = new HashMap<>();
                    // 告警订单 ID
                    extras.put(Constants.PARAM_ORDER_ID, dto.getWarningOrderId());
                    extras.put(Constants.PARAM_ORDER_STATUS, dto.getOrderStatus()); // 订单状态
                    String appUrl = this.joiningUrl(dto); // 构建跳转 URL
                    extras.put(Constants.APP_URL, appUrl);
                    // 构建极光推送请求
                    JgPushRequest jgPushRequest = new JgPushRequest();
                    jgPushRequest.setAlias(userId);
                    jgPushRequest.setMsgContent(dto.getAddress());
                    // 设置消息标题和通知标题
                    if(Constants.ONE.equals(dto.getOverdueRemindSign())){
                        jgPushRequest.setNotificationTitle("待受理");
                    }else{
                        // 据君南侧要求改标题
                        if(Constants.TWO.equals(dto.getOrderStatus())){
                            jgPushRequest.setNotificationTitle("待调度");
                        }else if(Constants.FOUR.equals(dto.getOrderStatus())){
                            jgPushRequest.setNotificationTitle("待处置");
                        }else if(Constants.ONE.equals(dto.getOrderStatus())){
                            jgPushRequest.setNotificationTitle("待研判");
                        }else if(Constants.FIVE.equals(dto.getOrderStatus())){
                            jgPushRequest.setNotificationTitle("待核实");
                        }else{
                            jgPushRequest.setNotificationTitle(EnumUtil.FlowNode.getName(dto.getOrderStatus()));
                        }
                    }
                    // 设置超期提醒标题（告警类型 + 标题 + 超期提醒）
                    jgPushRequest.setMsgTitle(alarmTypeMap.get(dto.getWarningTypeId())+jgPushRequest.getNotificationTitle()+"超期提醒");
                    jgPushRequest.setNotificationTitle("超期提醒");
                    // 设置推送的额外参数、应用版本号、行业代码
                    jgPushRequest.setExtrasparam(JSON.toJSONString(extras));
                    jgPushRequest.setAppVerCode(dto.getAppVerCode());
                    jgPushRequest.setIndustryCode(dto.getIndustryCode());
                    for (String key:switchResultMap.keySet()) {
                        if(key.equals(userId+"_"+dto.getIndustryCode())){
                            jgPushRequest.setMsgType(String.valueOf(switchResultMap.get(key).getAlarmOverdueSwitch()));
                            break;
                        }
                    }
                    jgPushRequest.setType(Constants.ONE);
                    //jgPushRequest.setMsgType(Constants.ONE);
                    jgPushRequestList.add(jgPushRequest);
                }
            }catch (Exception e){
                log.error("OverdueRemindSendServiceImpl-sendToApp组装报文出错:",e);
            }

        }
        // 调用极光消息批量发送
        log.info("OverdueRemindSendServiceImpl-sendToApp-激光推送过滤后报文:"+JSON.toJSONString(jgPushRequestList));
        msgFeignClient.batchPushNews(jgPushRequestList);
    }

    /**
     * 发送至pc端
     * @param overdueRemindList  超期提醒信息列表
     * @param alarmTypeMap     告警类型映射，用于将告警类型 ID 转换为名称
     */
    public void sendToPc(List<OverdueRemindDto> overdueRemindList,Map<String,String> alarmTypeMap){
        //根据工单号返回要查询得工单表，从工单表查询告警来源及告警摄像机code和摄像机名称
        List<String> tableList = new ArrayList<>();
        List<String> orderIds = new ArrayList<>();
        for (OverdueRemindDto dto:overdueRemindList) {
            // 根据工单号生成表名（WARN_ORDER_TEBLE + 工单号前 6 位）
            String tableName = WARN_ORDER_TEBLE + dto.getWarningOrderId().substring(0, 6);
            if (!tableList.contains(tableName)){
                tableList.add(tableName);
            }
            orderIds.add(dto.getWarningOrderId());
        }
        // 根据表名和工单号查询告警来源及设备信息
        List<OverdueRemindDto> deviceList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(tableList)){
            for (String tableName:tableList) {
                List<OverdueRemindDto> list = judgeStatisticsDAO.getDeviceInfoByOrderIds(orderIds,tableName);
                if(!CollectionUtils.isEmpty(list)){
                    deviceList.addAll(list);
                }
            }

        }
        List<MsgBody> msgBodyList = new ArrayList<>();
        for (OverdueRemindDto dto:overdueRemindList) {
            for (OverdueRemindDto device:deviceList) {
                if(dto.getWarningOrderId().equals(device.getWarningOrderId())){
                    dto.setWarningSource(device.getWarningSource());
                    dto.setDeviceCode(device.getDeviceCode());
                    dto.setDeviceName(device.getDeviceName());
                    break;
                }
            }
            // 获取需要提醒的用户列表
            List<String> userIdList = Arrays.asList(dto.getRemindProcessId().split(","));
            for (String userId : userIdList) {
                MsgBody msgBody = new MsgBody();
                msgBody.setAddress(dto.getAddress());
                msgBody.setMsgId(dto.getWarningOrderId());
                // 设置标题（根据订单状态决定）
                String title = "";
                if(Constants.ONE.equals(dto.getOverdueRemindSign())){
                    title="待受理";
                }else{
                    // 据君南侧要求改标题
                    if(Constants.TWO.equals(dto.getOrderStatus())){
                        title="待调度";
                    }else if(Constants.FOUR.equals(dto.getOrderStatus())){
                        title="待处置";
                    }else if(Constants.ONE.equals(dto.getOrderStatus())){
                        title="待研判";
                    }else if(Constants.FIVE.equals(dto.getOrderStatus())){
                        title="待核实";
                    }
                }
                // 设置消息体的其他字段
                msgBody.setOrderStatusName(title);
                msgBody.setNotifyTitle(alarmTypeMap.get(dto.getWarningTypeId())+title+"超期提醒");
                //通知类型:1：告警消息 5：系统消息，6 ：超期提醒
                msgBody.setNotifyType(Constants.ORDER_STATUS_6);
                msgBody.setOrderStatus(dto.getOrderStatus());
                msgBody.setUserId(userId);
                msgBody.setAlarmFlag(Constants.PARAM_ALARM_FLAG_1);
                msgBody.setShowType(Constants.SHOW_TYPE_ALL);
                msgBody.setAppVerCode(dto.getAppVerCode());
                msgBody.setIndustryCode(dto.getIndustryCode());
                msgBody.setSoundCloseType(Constants.ZERO);
                msgBody.setSoundDuration(Constants.FIVE);
                msgBody.setSoundId(Constants.ONE);
                msgBody.setAlarmSource(dto.getWarningSource());
                msgBody.setDeviceName(dto.getDeviceName());
                msgBody.setDeviceCode(dto.getDeviceCode());
                msgBodyList.add(msgBody);
            }
        }

        log.info("OverdueRemindSendServiceImpl-sendToPc-PC消息推送，{}",JSON.toJSONString(msgBodyList));
        try {
            if (!CollectionUtils.isEmpty(msgBodyList)) {
                //调用消息批量推送
                msgStationAPI.sendMsg2TargetServiceBatch(msgBodyList);
            }
        }catch (Exception e){
            log.error("OverdueRemindSendServiceImpl-sendToPc-PC消息推送消息异常,异常信息：",e);
        }
    }

    /**
     * 发送给微信小程序
     * @param overdueRemindList
     * @param alarmTypeMap
     */
    private void sendToWechat(List<OverdueRemindDto> overdueRemindList,Map<String,String> alarmTypeMap){

        List<WxPushRequest> wxMsgDataList = new ArrayList<>();
        for (OverdueRemindDto dto:overdueRemindList) {
            try{
                WxPushRequest wxPushRequest = new WxPushRequest();
                WxMsgData wxMsgData = new WxMsgData();
                wxPushRequest.setId(dto.getWarningOrderId());
                wxPushRequest.setUserIds(dto.getRemindProcessId().split(","));
                wxMsgData.setFirst("有一条超期事件请查看");
                wxMsgData.setTime3(dto.getAlarmTime());
                wxMsgData.setThing4(dto.getAddress());
                if(Constants.ONE.equals(dto.getOverdueRemindSign())){
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
                        wxMsgData.setPhrase9(EnumUtil.FlowNode.getName(dto.getOrderStatus()));
                    }
                }
                wxMsgData.setThing8(alarmTypeMap.get(dto.getWarningTypeId())+wxMsgData.getPhrase9()+"超期提醒");
                wxPushRequest.setExtrasparam(wxMsgData);
                wxPushRequest.setIndustryCode(dto.getIndustryCode());
                wxMsgDataList.add(wxPushRequest);
                log.info("微信公众号消息消息推送，{}",JSON.toJSONString(wxPushRequest));
                msgFeignClient.sendWxMessage(wxPushRequest);
            }catch (Exception e){
                log.error("OverdueRemindSendServiceImpl-sendToWechat消息推送消息异常,异常信息：",e);
            }
            //msgFeignClient.sendMultipleWxMessage(wxPushRequest);
        }
    }

    /**
     * url拼接处理
     * @param dto
     * @return
     */
    private String joiningUrl(OverdueRemindDto dto) {
        String appUrl = "";
        try {
            String orderStatus = "";
            // 超期提醒推送消息不可编辑
            String ifEdit = "0";  //默认可以编辑 1可以编辑 0不可以编辑
            //研判
            orderStatus = dto.getOrderStatus();
            Map detail = new HashMap();
            detail.put("eventId", dto.getWarningOrderId());
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
