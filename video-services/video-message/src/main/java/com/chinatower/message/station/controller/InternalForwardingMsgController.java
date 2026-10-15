package com.chinatower.message.station.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.CompletableFutureHelper;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.client.IAlarmTypeService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.message.api.model.*;
import com.chinatower.message.compenont.MessageResultCode;
import com.chinatower.message.conf.ServerConfig;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.kafka.MessageKafkaProducer;
import com.chinatower.message.station.service.MsgStationService;
import com.chinatower.message.websocket.config.NettyConfig;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * video-message
 * @title 
 * @description
 * @author LiZe 
 * @updateTime 2022/1/19 11:44 
 * @throws 
 */
@Api(value = "内部消息接口-Api", tags = "内部消息处理接口")
@RestController
@RequestMapping("/InternalForwardingMsg")
@Slf4j
public class InternalForwardingMsgController {

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    MsgStationService msgStationService;
    @Autowired
    IAlarmTypeService alarmTypeService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    MessageKafkaProducer kafkaProducer;

    @Resource
    FileUrlEncryptUtil fileUrlEncryptUtil;

    private final String REDIS_KEY_SOUND_URL = "msg:sound:url";

    private static final String LOG_FORMAT = "<==>用户与消息服务通道连接已断开";

    /**
     * 向目标服务器转发消息
     * @description: 向目标服务器转发消息
     * @param: uid 用户id
     * @return: msgIn4 站内信消息
     * @author LiZe
     * @date: 2022/2/9 15:29
     * @version: release V1.1
     */
    @ApiOperation(value = "内部消息转发", notes = "向目标服务器转发消息")
    @PostMapping(value = "/sendMsg2TargetService")
    public Result sendMsg2TargetService(@RequestBody  MsgBody req){
        log.info("sendMsg2TargetService入参:"+ JSONObject.toJSON(req));
        String orderStatus = req.getOrderStatus(); // 告警事件状态
        String msgId = req.getMsgId(); // 告警ID
        String userId = req.getUserId();  // 用户ID
        String notifyType = req.getNotifyType(); // 通知类型
        String industryCode = req.getIndustryCode();
        /**非空验证**/
        if(StringUtils.isNotEmpty(notifyType)){
            if(!"5".equals(notifyType)){ //非系统消息
                if(StringUtils.isEmpty(orderStatus) || StringUtils.isEmpty(msgId)
                        || StringUtils.isEmpty(userId) ){
                    log.error("入参:用户Id,告警Id,告警状态,通知类型不能为空");
                    return   Result.success(MessageResultCode.REQ_PARAMS_ERROR);
                }
            }
        }else{
            log.error("入参:通知类型不能为空");
            return   Result.success(MessageResultCode.REQ_PARAM_ERROR);
        }


        //保存消息  2022-10-20 先保存 后推送/ 用户不存在连接 也可以推送 前置保存，避免重复
        msgStationService.saveMessageInfo(req);
        //获取声音
//        String soundId = req.getSoundId();
//        if(StringUtils.isNotEmpty(soundId)){
//            Result<AlarmSoundDO> alarmSoundDOResult = alarmTypeService.selectById(Long.parseLong(soundId));
//            log.info("查询声音url接口返回:"+ JSONObject.toJSON(alarmSoundDOResult));
//            if(ResultCode.SUCCESS.getCode() == alarmSoundDOResult.getCode()){
//                AlarmSoundDO data = alarmSoundDOResult.getData();
//                //event接口已经加密过了
//                req.setSoundUrl(data.getDocUrl());
//            }
//        }

        //接受消息 发送kafka
        try {
            MsgKafkaConsumerModel<MsgBody> msgKafkaConsumerModel = new MsgKafkaConsumerModel<>();
            msgKafkaConsumerModel.setType(1);
            msgKafkaConsumerModel.setT(req);
            kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG, JSON.toJSONString(msgKafkaConsumerModel));
        } catch (Exception e) {
            log.info("推送kafka报错");
            e.printStackTrace();
        }
        return   Result.success(MessageResultCode.SUCCESS);
    }

    /**
     * 批量 向目标服务器转发消息
     * @description: 向目标服务器转发消息
     * @param: uid 用户id
     * @return: msgIn4 站内信消息
     * @author LiZe
     * @date: 2022/2/9 15:29
     * @version: release V1.1
     */
    @ApiOperation(value = "内部批量消息转发", notes = "向目标服务器批量转发消息")
    @PostMapping(value = "/sendMsg2TargetServiceBatch")
    public Result sendMsg2TargetServiceBatch(@RequestBody List<MsgBody> reqList){
        log.info("sendMsg2TargetServiceBatch入参:"+ JSONObject.toJSON(reqList));
        // 用于存储所有音效URL，包括缓存和数据库查询结果
        Map<String, Object> soundUrlMap = new HashMap<>();

        // 收集未命中缓存的音效ID
        Set<String> soundIdFindSet = reqList.stream()
                .map(MsgBody::getSoundId)
                .collect(Collectors.toSet());

        // 没有缓存的声音ID查询MySQL，并更新到soundUrlMap中
        if (CollectionUtil.isEmpty(soundUrlMap)) {
            Map<String, Object> dbSoundUrlMap = msgStationService.findSoundUrlByIds(new ArrayList<>(soundIdFindSet));
            if (null != dbSoundUrlMap) {
                soundUrlMap.putAll(dbSoundUrlMap);
            }
        }

        // 加密URL并更新到encUrlMap中
        List<String> encUrls = soundUrlMap.values().stream().map(Object::toString).collect(Collectors.toList());
        Map<String, String> encUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(encUrls);

        // 更新MsgBody中的SoundUrl
        for (MsgBody msgBody : reqList) {
            String soundId = msgBody.getSoundId();
            if (soundId != null) {
                if (soundUrlMap.containsKey(soundId) && encUrlMap.containsKey(String.valueOf(soundUrlMap.get(soundId)))) {
                    msgBody.setSoundUrl(encUrlMap.get(String.valueOf(soundUrlMap.get(soundId))));
                }
            }
        }

        for (MsgBody req: reqList) {
            CompletableFutureHelper.orTimeout(
                    CompletableFuture.supplyAsync(() -> this.sendMsg2TargetService(req), threadPoolTaskExecutor)
                            .exceptionally(e -> {
                                log.error("多线程调用sendMsg2TargetService异常" , e);
                                return null;
                            }),
                    60, TimeUnit.SECONDS).exceptionally(e -> {
                log.error("多线程调用sendMsg2TargetService超时", e);
                return null; }
            );
        }
        return   Result.success(MessageResultCode.SUCCESS);
    }

    /**
     * 向前端转发消息（已废弃）
     * @description: 向前端推送消息
     * @param: uid 用户id
     * @return: msgIn4 站内信消息
     * @author LiZe
     * @date: 2022/2/9 15:28
     * @version: release V1.1
     */
    @ApiOperation(value = "消息推送", notes = "向前端推送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderStatus",value = "告警事件状态",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "msgId",value = "告警ID",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "用户ID",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "notifyType",value = "通知类型",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "notifyTitle",value = "通知标题",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "address",value = "地址",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "appVerCode",value = "行业版本号",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "alarmFlag",value = "告警状态",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "showType",value = "显示位置",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "industryFlag",value = "行业FLAG",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "industryExtend",value = "扩展信息",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "industryCode",value = "行业编码",dataType = "String",required = true,paramType = "query")
    })
    @PostMapping(value = "/messagePush")
    public Result messagePush(
//            @RequestParam("channelCode") String channelCode,
//            @RequestParam("msgIn4") String msgIn4
            @RequestParam("orderStatus") String orderStatus,
            @RequestParam("msgId") String msgId,
            @RequestParam("userId") String userId,
            @RequestParam("notifyType") String notifyType,
            @RequestParam("notifyTitle") String notifyTitle,
            @RequestParam("address") String address,
            @RequestParam("appVerCode") String appVerCode,
            @RequestParam("alarmFlag") String alarmFlag,
            @RequestParam("showType") String showType,
            @RequestParam("industryFlag") String industryFlag,
            @RequestParam("industryExtend") String industryExtend,
            @RequestParam("industryCode") String industryCode,
            @RequestParam("soundCloseType") String soundCloseType,
            @RequestParam("soundDuration") String soundDuration,
            @RequestParam("soundUrl") String soundUrl,
            @RequestParam("orderStatusName") String orderStatusName
    ) throws Exception {
        //获取当前服务器中-用户对应长链接管道
        Channel channel = NettyConfig.getUserChannelMap().get(userId);
        if(null == channel){
            //TODO:: 是否插入失败管道
            log.warn(userId + LOG_FORMAT);
            return Result.fail(MessageResultCode.MESSAGE_CHANNEL_CONNECT_FAIL);
        }
        //向用户通道输出消息  + 入库
        return msgStationService.recordOutPutMsg(orderStatus,msgId,userId,notifyType,notifyTitle,address,
                channel,appVerCode,alarmFlag,showType,industryFlag,industryExtend,industryCode,soundCloseType,soundDuration,soundUrl,orderStatusName);
    }


    /**
     * 转发雷达消息推送
     * @param model
     * @return
     */
    @PostMapping(value = "/radarMessagePush")
    public Result<Boolean> radarMessagePush(@RequestBody RadarBindUserModel model) {
        try {
            log.info("radarMessagePush 入参 model = {}",JSON.toJSONString(model));
            // 租户id:用户名:行业编码_随机字符
            String userKey = model.getUserKey();
            RadarObject radarObject = model.getRadarObject();

            //获取当前服务器中-用户对应长链接管道
            Channel channel = NettyConfig.getUserChannelMap().get(userKey);
            if(null == channel){
                log.warn(userKey + LOG_FORMAT);
                return Result.fail(MessageResultCode.MESSAGE_CHANNEL_CONNECT_FAIL);
            }
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(radarObject)));
            return Result.success("push消息成功");
        } catch (Exception e) {
            log.error("push 失败 ", e);
            return Result.fail(MessageResultCode.FAILURE);
        }
    }

    /**
     * 极简模式图片上传
     * @param data
     * @return
     */
    @PostMapping(value = "/upload/sampleImg",consumes = "application/json")
    public Result<Map<String, String>> presetImg(@RequestBody JSONObject data) {
        String msgId = UUID.randomUUID().toString();
        log.info("msgId:" + msgId);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getParameter("userId");
        String channelCode = request.getParameter("channelCode");

        log.info("========="+ msgId +"上传极简模式请求报文:" + data.toJSONString() + "=========上传极简模式图片请求参数:" + userId + "," + channelCode);
        if (!DeviceConstants.ZERO.equals(data.getString("error_code"))) {
            log.info("========"+ msgId +"上传极简模式图片失败error_code!=0");
            return Result.fail("上传失败");
        }
        //base64转成MultipartFile流
        String image = "data:image/png;base64," + data.get("image").toString();
//        MultipartFile m = BASE64DecodedMultipartFile.base64ToMultipart(image);

        Channel channel = NettyConfig.getUserChannelMap().get(userId);
        if(null == channel){
            //TODO:: 是否插入失败管道
            log.warn(userId + LOG_FORMAT);
        }else {
            log.info("msgId:" + msgId + "获取通道成功");
            msgStationService.sampleImagePutMsg(channelCode,msgId,userId,channel,image);
        }
        return Result.success("回调成功");
    }

    @PostMapping(value = "/sendPresetArrivalNoticeBatch")
    Result sendPresetArrivalNoticeBatch(@RequestBody List<SendPresetArrivalNoticeVO> reqList){
        log.info("sendPresetArrivalNoticeBatch入参数量:" + reqList.size());
        for (SendPresetArrivalNoticeVO req: reqList) {
            CompletableFutureHelper.orTimeout(
                    CompletableFuture.supplyAsync(() -> this.sendPresetArrivalNotice(req), threadPoolTaskExecutor)
                            .exceptionally(e -> {
                                log.error("多线程调用sendPresetArrivalNotice异常" , e);
                                return null;
                            }),
                    60, TimeUnit.SECONDS).exceptionally(e -> {
                log.error("多线程调用sendPresetArrivalNotice超时", e);
                return null; }
            );
        }
        return  Result.success(MessageResultCode.SUCCESS);
    }

    public Result<String> sendPresetArrivalNotice(SendPresetArrivalNoticeVO notice){
        log.info("sendPresetArrivalNotice入参:"+ JSONObject.toJSON(notice));
        //接受消息 发送kafka
        try {
            MsgKafkaConsumerModel<SendPresetArrivalNoticeVO> msgKafkaConsumerModel = new MsgKafkaConsumerModel<>();
            msgKafkaConsumerModel.setType(3);
            msgKafkaConsumerModel.setT(notice);
            kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG, JSON.toJSONString(msgKafkaConsumerModel));
        } catch (Exception e) {
            log.error("推送kafka报错",e);
        }
        return Result.success(MessageResultCode.SUCCESS);
    }




}
