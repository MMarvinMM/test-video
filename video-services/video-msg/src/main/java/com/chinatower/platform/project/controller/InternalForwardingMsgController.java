package com.chinatower.platform.project.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.compenont.MessageResultCode;
import com.chinatower.platform.common.config.ServerConfig;
import com.chinatower.platform.common.websocket.config.NettyConfig;
import com.chinatower.platform.project.service.MsgStationService;
import com.chinatower.platform.project.utils.HttpClientUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.netty.channel.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * video-msg (废弃)
 * @description: 内部消息处理接口
 * @param:
 * @return: 
 * @author LiZe 
 * @date: 2022/2/9 15:47 
 * @version: release V1.1
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

    /**
     * @description: 向目标服务器转发消息
     * @param: uid 用户id
     * @return: msgIn4 站内信消息
     * @author LiZe
     * @date: 2022/2/9 15:29
     * @version: release V1.1
     */
    @ApiOperation(value = "内部消息转发", notes = "向目标服务器转发消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderStatus",value = "告警事件状态",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "msgId",value = "告警ID",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "userId",value = "用户ID",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "notifyType",value = "通知类型",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "notifyTitle",value = "通知标题",dataType = "String",required = true,paramType = "query"),
            @ApiImplicitParam(name = "address",value = "地址",dataType = "String",required = true,paramType = "query")
    })
    @PostMapping(value = "/sendMsg2TargetService")
    public Result sendMsg2TargetService(
            @RequestParam("orderStatus") String orderStatus,
            @RequestParam("msgId") String msgId,
            @RequestParam("userId") String userId,
            @RequestParam("notifyType") String notifyType,
            @RequestParam("notifyTitle") String notifyTitle,
            @RequestParam("address") String address
    ){


        //保存消息   2022-10-28修改 铁塔测试249问题 未登录用户不推送告警但是要存表
        int res = msgStationService.saveMessageInfo(orderStatus,msgId,userId,notifyType,notifyTitle,address);

       //目标用户连接是否存在于消息服务
       Boolean userExist = RedisUtil.hasKey(userId, RedisDBType.PRIMARY);
       //目标用户连接-存在 于消息服务
       if(Boolean.TRUE.equals(userExist)){
           Map<Object, Object> channelAndDomains = RedisUtil.hGetAll(userId, RedisDBType.PRIMARY);

           //保存成功,往前端发送消息
           if(res > 0 ){
               //批量转发消息到各个服务（channelCode格式：userId-10位随机数，domain格式：192.168.1.1:3306）
               channelAndDomains.forEach((channelCode,domain)->{
                   //向其他消息服务转发MSG
                   HttpClientUtils.httpPostWithParams((String)channelCode,
                           orderStatus,
                           msgId,
                           notifyType,
                           notifyTitle,
                           address, (String)domain);
               });
           }else{
               log.warn("记录推送消息异常,请联系管理员");
               return   Result.success(MessageResultCode.MESSAGE_RECORD_ERROR);
           }

        }
        //目标用户连接-不存在 于集群消息服务
        if(Boolean.FALSE.equals(userExist)){
            log.warn("目标用户连接，不存在于消息服务");
            return   Result.success(MessageResultCode.MESSAGE_NO_USER_TO_TRANSFORM_ERROR);
        }
        return   Result.success(MessageResultCode.SUCCESS);
    }


    /**
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
            @ApiImplicitParam(name = "address",value = "地址",dataType = "String",required = true,paramType = "query")
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
            @RequestParam("address") String address
    ) throws Exception {
        //获取当前服务器中-用户对应长链接管道
        Channel channel = NettyConfig.getUserChannelMap().get(userId);
        if(null == channel){
            //TODO:: 是否插入失败管道
            log.warn(userId + "<==>用户与消息服务通道连接已断开");
            return Result.fail(MessageResultCode.MESSAGE_CHANNEL_CONNECT_FAIL);
        }
        //向用户通道输出消息  + 入库
            return msgStationService.recordOutPutMsg(orderStatus,msgId,userId,notifyType,notifyTitle,address,channel);
    }

}
