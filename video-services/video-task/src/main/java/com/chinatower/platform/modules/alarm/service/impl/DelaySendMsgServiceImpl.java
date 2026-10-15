package com.chinatower.platform.modules.alarm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.MsgStationAPI;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.modules.alarm.dao.DelaySendMsgDAO;
import com.chinatower.platform.modules.alarm.entity.JgPushRequestDto;
import com.chinatower.platform.modules.alarm.entity.OrderDelayMessage;
import com.chinatower.platform.modules.alarm.entity.delaysendmsg.DelayFlowVo;
import com.chinatower.platform.modules.alarm.service.DelaySendMsgService;
import com.chinatower.platform.mongo.OrderDelayMessageMapper;
import com.chinatower.system.api.feign.client.NoticeRuleClient;
import com.chinatower.system.api.model.NoticeExtendVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DelaySendMsgServiceImpl implements DelaySendMsgService {
    @Autowired
    OrderDelayMessageMapper orderDelayMessageMapper;

    @Autowired
    private NoticeRuleClient noticeRuleClient;

    @Autowired
    private DelaySendMsgDAO delaySendMsgDAO;

    @Autowired
    private MsgStationAPI msgStationAPI;

    @Autowired
    private MsgFeignClient msgFeignClient;

    /**
     * 发送延迟消息方法：
     * 根据消息来源区分 PC 和 App 消息，并分别发送。
     * 处理未处理的推送消息数据，通过 MongoDB 查询后按来源进行分发。
     *
     * @param param 预留参数，当前未使用
     */
    @Override
    public void sendDelaySendMsg(String param) {
        // 获取当前时间时分秒
        String hhMmSsString = DateUtils.parseDateToStr("HH:mm:ss",new Date());
        log.info("sendDelaySendMsg-执行时间:"+hhMmSsString);
        // 查询所有mongo中未处理得推送消息数据
        QueryEntity queryEntity = new QueryEntity(Constants.SYS_ORDER_DELAY_TEBLE, OrderDelayMessage.class);
        queryEntity.eq(Constants.PARAM_OPERATE_STATUS, Constants.PARAM_ZERO); // 查询操作状态为 "0"（未处理）的消息
        SortEntity sortEntity=new SortEntity();
        sortEntity.asc(Constants.PARAM_CREATE_TIME); // 按创建时间升序排序
        List<OrderDelayMessage> list = orderDelayMessageMapper.findList(queryEntity, sortEntity, MongoDBType.PRIMARY);

        List<OrderDelayMessage> pcDelayList;
        List<OrderDelayMessage> appDelayList;
        log.info(JSON.toJSONString(list));
        // 如果查询结果非空，按消息来源区分 PC 和 App 消息
        if(!CollectionUtils.isEmpty(list)){
            // 筛选 PC 消息（来源为 "0"）
            pcDelayList=list.stream().filter(l -> Constants.PARAM_ZERO.equals(l.getSource())).collect(Collectors.toList());
            // 筛选 App 消息（来源为 "1"）
            appDelayList=list.stream().filter(l -> Constants.ONE.equals(l.getSource())).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(pcDelayList)){
                // 调用方法发送 PC 消息
                this.pcSendDelayMsg(pcDelayList,hhMmSsString);
            }
            if(!CollectionUtils.isEmpty(appDelayList)){
                // 调用方法发送 App 消息
                this.appSendDelayMsg(appDelayList,hhMmSsString);
            }
        }
    }

    /**
     * app延迟消息发送
     * @param appDelayList
     */
    public void appSendDelayMsg(List<OrderDelayMessage> appDelayList,String hhMmSsString){
        try{
            log.info("appSendDelayMsg-app延迟消息推送开始:{}",JSON.toJSONString(appDelayList));
            //原有所有推送消息
            List<JgPushRequestDto> jgPushRequestList =new ArrayList<>();
            //实际推送得延迟消息
            List<JgPushRequest> jgPushRequestRealList =new ArrayList<>();
            //去除原有推送报文集合
            //List<String> json =appDelayList.stream().map(user -> user.getMessageJson()).collect(Collectors.toList());
            appDelayList.forEach(j ->{
                JgPushRequestDto request = JSON.parseObject(j.getMessageJson(),JgPushRequestDto.class);
                request.setOrderId(j.getOrderId());
                request.setWarningSource(j.getWarningSource());
                request.setWarningTypeId(j.getWarningTypeId());
                request.setNoticeExtendVO(j.getNoticeExtendVO());
                request.setAlarmTime(j.getAlarmTime());
                request.setOperateTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,j.getCreateTime()));
                //兼容老数据没有该字段情况，新数据按照新增字段消息类型传递
                if(!StringUtils.isEmpty(j.getMsgType())){
                    request.setMsgType(j.getMsgType());
                }
                jgPushRequestList.add(request);
            });

            /*//需要过滤的用户报文
            List<NoticeRuleFilterVO> noticeRuleFilterVOList = new ArrayList<>();
            appDelayList.forEach(appDelay ->{
                NoticeRuleFilterVO vo = new NoticeRuleFilterVO();
                BeanUtil.copyProperties(appDelay,vo);
                vo.setIsNow(Constants.PARAM_ZERO);
                noticeRuleFilterVOList.add(vo);
            });

            log.info("sendToApp-调用激光过滤用户req:"+JSON.toJSONString(noticeRuleFilterVOList));
            Result<List<NoticeRuleFilterResultVO>> result=noticeRuleClient.filterUserList(noticeRuleFilterVOList);
            log.info("sendToApp-调用激光过滤用户resp:"+JSON.toJSONString(result));*/

            //获取当前时间分钟数
            int mins = Integer.parseInt(hhMmSsString.split(Constants.COLON)[0]) * 60 + Integer.parseInt(hhMmSsString.split(Constants.COLON)[1]);
            log.info("appSendDelayMsg-当前时间总分钟数:"+mins);
            //过滤匹配可发送用户
            for (JgPushRequestDto request:jgPushRequestList) {
                if(!CollectionUtils.isEmpty(request.getNoticeExtendVO())){
                    //稍后提醒

                    List<NoticeExtendVO> noticeExtendVOList = request.getNoticeExtendVO().stream().filter(temp ->
                            DateUtils.isEffectiveDate(hhMmSsString.substring(0, 5),
                                    temp.getTimeList().get(Constants.INT_ZERO).getStartTime(),
                                    temp.getTimeList().get(Constants.INT_ZERO).getEndTime())).collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(noticeExtendVOList)){
                        //（当前事件分钟数-告警事件分钟数）除以间隔时间取余为0既符合
                        NoticeExtendVO resultVo = noticeExtendVOList.get(0);
                        if(((mins-this.getAlarmTimeMin(request.getOperateTime()))%Integer.parseInt(resultVo.getDelayTime()) == 0)){
                            JgPushRequest requestReal = new JgPushRequest();
                            BeanUtil.copyProperties(request,requestReal);
                            requestReal.setType(Constants.ONE);
                            //替换声音
                            requestReal.setSound(resultVo.getSound());
                            jgPushRequestRealList.add(requestReal);
                        }
                    }
                }
            }
            //调用极光消息批量发送
            log.info("appSendDelayMsg-app延迟消息推送实际报文:"+JSON.toJSONString(jgPushRequestRealList));
            // 分批次调用  由于极光推送接口异步处理  此处不在额外做异步处理
            List<List<JgPushRequest>> lists = Lists.partition(jgPushRequestRealList, 500);
            for (List<JgPushRequest> list : lists) {
                msgFeignClient.batchPushNews(list);
            }
        }catch (Exception e){
            log.error("appSendDelayMsg-延迟消息app发送异常:",e);
        }
    }

    /**
     * 将告警时间转换为分钟数。
     * 根据传入的告警时间字符串，计算从当天 00:00 开始到该时间点的总分钟数。
     * 如果告警时间为空，则返回 0。
     *
     * @param alarmTime 告警时间字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 从当天 00:00 到告警时间的分钟数；如果告警时间为空，返回 0
     */
    public Integer getAlarmTimeMin(String alarmTime){
        //兼容mongo表中告警时间为空字段
        if(StringUtils.isEmpty(alarmTime)){
            return 0;
        }
        Date date = DateUtils.parseDate(alarmTime);
        String hhMm = DateUtils.parseDateToStr("HH:mm",date);
        String[] time = hhMm.split(":");
        // 将小时和分钟转换为总分钟数
        Integer mins = Integer.parseInt(time[0])*60+Integer.parseInt(time[1]);
        return mins;
    }

    /**
     * 将告警时间转换为秒数。
     * 根据传入的告警时间字符串，计算从当天 00:00 开始到该时间点的总秒数。
     * 如果告警时间为空，则返回 0。
     *
     * @param alarmTime 告警时间字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 从当天 00:00 到告警时间的总秒数；如果告警时间为空，返回 0
     */
    public Integer getAlarmTimeSec(String alarmTime){
        //兼容mongo表中告警时间为空字段
        if(StringUtils.isEmpty(alarmTime)){
            return 0;
        }
        Date date = DateUtils.parseDate(alarmTime);
        String hhMm = DateUtils.parseDateToStr("HH:mm",date);
        String[] time = hhMm.split(":");
        // 将小时和分钟转换为总秒数
        Integer mins = Integer.parseInt(time[0])*60*60+Integer.parseInt(time[1])*60;
        return mins;
    }

    /**
     * pc延迟消息发送
     * @param pcDelayList
     */
    public void pcSendDelayMsg(List<OrderDelayMessage> pcDelayList,String hhMmSsString){
        try{
            //查询所有是延迟消息推送的环节所配置配置的属性
            List<DelayFlowVo> delayFlowList = delaySendMsgDAO.getDealyFlowList();
            List<MsgBody> msgBodyList = new ArrayList<>();
            //循环所有环节匹配延迟消息推送中的多有工单
            for (DelayFlowVo flow:delayFlowList) {
                //获取当前时间分钟秒数
                int mins = Integer.parseInt(hhMmSsString.split(Constants.COLON)[0]) * 60 *60 + Integer.parseInt(hhMmSsString.split(Constants.COLON)[1]) *60;

                String soundCloseType = flow.getTimeRemind();
                //手动关闭入参枚举值为2，本地枚举值为0
                if(Constants.PARAM_ZERO.equals(soundCloseType)){
                    soundCloseType = Constants.TWO;
                }
                String timeRemindAuto = flow.getTimeRemindAuto();
                String soundDuration  = flow.getTimeRemindNumber();
                if(StringUtils.isEmpty(soundDuration)){
                    soundDuration  = Constants.FIVE;
                }else{
                    if(Constants.ONE.equals(timeRemindAuto)){
                        soundDuration = String.valueOf(60 * Integer.parseInt(soundDuration));
                    }
                }
                String soundId = flow.getVoiceAlram();
                boolean timeInterval =false;
                //判断当前时间是否在该时间区间内（HH:mm:ss）
                if(!StringUtils.isEmpty(flow.getTimeStartAlram()) && !StringUtils.isEmpty(flow.getTimeEndAlram())){
                    String[] nowTime = hhMmSsString.split(Constants.COLON);
                    String[] beginTime = flow.getTimeStartAlram().split(Constants.COLON);
                    String[] endTime = flow.getTimeEndAlram().split(Constants.COLON);
                    //判断当前时间是否在告警声音配置时间区间内
                    timeInterval = this.compareTime(nowTime,beginTime,endTime);
                }else{
                    timeInterval=true;
                }
                String finalSoundCloseType = soundCloseType;
                String finalSoundDuration = soundDuration;
                boolean finalTimeInterval = timeInterval;
                pcDelayList.forEach(delay ->{
                    //获取配置中的时间间隔，换算为秒
                    String interval =flow.getLaterRemind();
                    if(Constants.ONE.equals(flow.getLaterRemindCompany())){
                        //分钟
                        interval=String.valueOf(Integer.parseInt(interval) * 60);
                    }else if(Constants.TWO.equals(flow.getLaterRemindCompany())){
                        //小时
                        interval=String.valueOf(Integer.parseInt(interval) * 60 * 60);
                    }
                    //判断时间是不是在间隔区间(时间区间是秒的话直接通行，定时任务是一分钟一次，秒的话全部符合)
                    if(((mins-this.getAlarmTimeSec(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,delay.getCreateTime()))) % Integer.parseInt(interval) == 0) ||Constants.PARAM_ZERO.equals(flow.getLaterRemindCompany())){
                        if(flow.getFlowId() !=null && delay.getFlowId() !=null){
                            //自定义流程匹配
                            if(flow.getFlowId().equals(delay.getFlowId()) && flow.getAppVerCode().equals(delay.getAppVerCode())
                                    && flow.getLinkId().equals(delay.getLinkId()) && flow.getIndustryCode().equals(delay.getIndustryCode())) {
                                this.getMsgBody(delay.getMessageJson(), finalSoundCloseType, finalSoundDuration, soundId, finalTimeInterval, msgBodyList);
                            }
                        }else if(flow.getFlowId() ==null && Constants.PARAM_NULL.equals(delay.getFlowId()) && flow.getLinkId().equals(delay.getLinkId()) && flow.getIndustryCode().equals(delay.getIndustryCode())){
                            //行业默认流程匹配
                            this.getMsgBody(delay.getMessageJson(),finalSoundCloseType,finalSoundDuration,soundId, finalTimeInterval,msgBodyList);
                        }
                    }
                });
            }
            if(!CollectionUtils.isEmpty(msgBodyList)){
                //发送pc消息
                log.info("pcSendDelayMsg-pc延迟消息推送实际报文:"+JSON.toJSONString(msgBodyList));
                msgStationAPI.sendMsg2TargetServiceBatch(msgBodyList);
            }
        }catch (Exception e){
            log.error("pcSendDelayMsg-延迟pc消息发送异常:",e);
        }
    }


    /**
     * 根据输入的 JSON 消息数据，构建消息体对象，并设置相关的声音属性和报警标志。
     * 最终将生成的消息体对象添加到消息体列表中。
     *
     * @param messageJson     消息体的 JSON 字符串，用于生成 MsgBody 对象
     * @param soundCloseType  声音关闭类型
     * @param soundDuration   声音时长
     * @param soundId         声音 ID
     * @param timeInterval    是否设置报警标志，true 表示设置为报警，false 表示不报警
     * @param msgBodyList     消息体列表，将生成的消息体对象添加到该列表中
     */
    public void getMsgBody(String messageJson,String soundCloseType,String soundDuration,String soundId,boolean timeInterval,List<MsgBody> msgBodyList){
        MsgBody msgBody = JSON.parseObject(messageJson,MsgBody.class);
        // 设置声音相关属性
        msgBody.setSoundCloseType(soundCloseType);
        msgBody.setSoundDuration(soundDuration);
        msgBody.setSoundId(soundId);
        if (timeInterval) {
            msgBody.setAlarmFlag(Constants.ONE);
        }
        else {
            msgBody.setAlarmFlag(Constants.PARAM_ZERO);
        }
        msgBodyList.add(msgBody);
    }


    /**
     * 判断当前时间是否在配置时间区间内
     * @param nowTime 当前时间
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public boolean compareTime(String[] nowTime,String[] beginTime,String[] endTime){
        int nowTotal=Integer.parseInt(nowTime[0])*3600+Integer.parseInt(nowTime[1])*60+Integer.parseInt(nowTime[2]);
        int beginTotal=Integer.parseInt(beginTime[0])*3600+Integer.parseInt(beginTime[1])*60+Integer.parseInt(beginTime[2]);
        int endTotal=Integer.parseInt(endTime[0])*3600+Integer.parseInt(endTime[1])*60+Integer.parseInt(endTime[2]);
        if(nowTotal >= beginTotal && nowTotal <= endTotal){
            return true;
        }else{
            return false;
        }
    }

}
