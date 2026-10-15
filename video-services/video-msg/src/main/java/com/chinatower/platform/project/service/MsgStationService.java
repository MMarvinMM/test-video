package com.chinatower.platform.project.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.MsgStationReq;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 * @ClassName MsgStationService
 * @Description 站内消息服务类
 * @Author WJD
 * @Date 2022/1/19 16:54
 */

public interface MsgStationService {




    /**
     * @title
     * @description  消息推送与记录
     * @author LiZe
     * @updateTime 2022/1/20 14:57
     * @throws
     */
    public Result recordOutPutMsg(String orderStatus, String msgId,String userId,String notifyType,String notifyTitle,String address, Channel channel) ;


    /**
     * 获取消息通知信息
     * @param msgStationReq
     * @return
     */
    public List<Map<String,Object>> getMessageInfo(MsgStationReq msgStationReq);

    /**
     * 一键清空通知信息
     * @return
     */
    public Result oneClickClear();

    /**
     * 保存消息
     * @return
     */
    int saveMessageInfo(String orderStatus, String msgId,String userId,String notifyType,String notifyTitle,String address);



}
