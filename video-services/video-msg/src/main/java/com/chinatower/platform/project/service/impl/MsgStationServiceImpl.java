package com.chinatower.platform.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.compenont.MessageResultCode;
import com.chinatower.platform.common.constant.MsgConstants;
import com.chinatower.platform.entity.request.MsgStationReq;
import com.chinatower.platform.project.mapper.MsgStationMapper;
import com.chinatower.platform.project.service.MsgStationService;
import com.chinatower.platform.utils.SecurityUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MsgStationServiceImpl
 * @Description 消息服务实现类
 * @Author D
 * @Date 2022/2/26 16:11
 */
@Service
@Slf4j
@Transactional
public class MsgStationServiceImpl implements MsgStationService {


    @Resource
    private MsgStationMapper msgStationMapper;


    /**
     * @throws
     * @title
     * @description 消息推送与记录
     * @author LiZe
     * @updateTime 2022/1/20 14:57
     */
    public Result recordOutPutMsg(String orderStatus, String msgId,String userId,String notifyType,String notifyTitle,String address, Channel channel) {
         String  userIdStr = userId.split("-")[0];
        //构建消息实体
        MsgStationReq req = MsgStationReq.builder()
                .uniId(IdWorker.getIdStr())
                .msgId(MsgStationReq.EMPTY)
                .userId(userIdStr)
                .notifyType(MsgStationReq.SYSTEM_WARNING)
                .notifyTitle(notifyTitle)
                .orderStatus(orderStatus)
                .address(address)
                .creator(userIdStr)
                .build();
//        //记录推送数据
//        int res = msgStationMapper.saveMessageInfo(req);
//        if (res <= 0) {
//            return Result.fail(MessageResultCode.MESSAGE_RECORD_ERROR);
//        }
        //推送消息
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(req)));
        return Result.success(MessageResultCode.SUCCESS);
    }


    /**
     * 获取消息通知信息
     *
     * @param msgStationReq
     * @return
     */
    public List<Map<String, Object>> getMessageInfo(MsgStationReq msgStationReq) {
        return msgStationMapper.getMessageInfo(msgStationReq);
    }

    /**
     * 一键清空通知信息
     *
     * @return
     */
    public Result oneClickClear() {
        //根据用户消息更新为已读
        MsgStationReq req = MsgStationReq.builder()
                .ifRead(MsgConstants.IF_READ) //已读
                .userId(SecurityUtils.getLoginUser().getUser().getUserId())
                .modifier(SecurityUtils.getLoginUser().getUser().getUserId())
                .build();

        //更新通知信息
        int count = msgStationMapper.updateMessage(req);
        return Result.data(count);
    }

    /**
     * 保存消息
     * @return
     */
    @Override
    public int saveMessageInfo(String orderStatus, String msgId, String userId, String notifyType, String notifyTitle, String address) {
        String  userIdStr = userId.split("-")[0];
        //构建消息实体
        MsgStationReq req = MsgStationReq.builder()
                .uniId(IdWorker.getIdStr())
                .msgId(MsgStationReq.EMPTY)
                .userId(userIdStr)
                .notifyType(MsgStationReq.SYSTEM_WARNING)
                .notifyTitle(notifyTitle)
                .orderStatus(orderStatus)
                .address(address)
                .creator(userIdStr)
                .build();
        //记录推送数据
        return  msgStationMapper.saveMessageInfo(req);
    }


}
