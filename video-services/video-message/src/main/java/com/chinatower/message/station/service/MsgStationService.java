package com.chinatower.message.station.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.message.api.model.MsgStationReq;
import com.chinatower.message.api.model.SampleMsgReq;
import com.chinatower.message.compenont.MessageResultCode;
import com.chinatower.message.station.entity.AlarmSoundDTO;
import com.chinatower.message.station.mapper.MsgStationMapper;
import com.chinatower.message.util.UUIDUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.page.PageMethod;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName MsgStationService
 * @Description 站内消息服务类
 * @Author WJD
 * @Date 2022/1/19 16:54
 */
@Service
@Slf4j
@Transactional
public class MsgStationService {


    @Resource
    private MsgStationMapper msgStationMapper;

    /**
     * @throws
     * @title
     * @description 消息推送与记录
     * @author LiZe
     * @updateTime 2022/1/20 14:57
     */
    public Result recordOutPutMsg(String orderStatus, String msgId,String userId,String notifyType,String notifyTitle,String address,
                                  Channel channel,String appVerCode,String alarmFlag,String showType,String industryFlag,
                                  String industryExtend,String industryCode,String soundCloseType,String soundDuration,String soundUrl,String orderStatusName) {
        String  userIdStr = userId.split("-")[0];
        //构建消息实体
        MsgStationReq req = MsgStationReq.builder()
                .uniId(IdWorker.getIdStr())
                .msgId(msgId)
                .userId(userIdStr)
                .notifyType(notifyType)
                .notifyTitle(notifyTitle)
                .orderStatus(orderStatus)
                .address(address)
                .appVerCode(appVerCode)
                .alarmFlag(alarmFlag)
                .showType(showType)
                .industryFlag(industryFlag)
                .industryExtend(industryExtend)
                .industryCode(industryCode)
                .creator(userIdStr)
                .soundCloseType(soundCloseType)
                .soundDuration(soundDuration)
                .soundUrl(soundUrl)
                .orderStatusName(orderStatusName)
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


    public void sampleImagePutMsg(String channelCode,String msgId,String userId,Channel channel,String image) {
        //构建消息实体
        SampleMsgReq req = SampleMsgReq.builder()
                .userId(userId).msgId(msgId).channelCode(channelCode).image(image).key("SampleSnapShot")
                .build();
//        //记录推送数据
//        int res = msgStationMapper.saveMessageInfo(req);
//        if (res <= 0) {
//            return Result.fail(MessageResultCode.MESSAGE_RECORD_ERROR);
//        }
        //推送消息
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(req)));
        log.info(msgId+"极简模式图片推送webSocket成功");
    }


    /**
     * 获取消息通知信息
     *
     * @param msgStationReq
     * @return
     */
//    @DataScope(userColumn = "t1.user_id")
    public List<Map<String, Object>> getMessageInfo(MsgStationReq msgStationReq) {
        PageMethod.startPage(msgStationReq.getPageNum(), msgStationReq.getPageSize());
        return msgStationMapper.getMessageInfo(msgStationReq);
    }

    /**
     * 一键清空通知信息
     *
     * @return
     */
    public Result oneClickClear() {
        SysUser userToken = SecurityUtils.getLoginUser().getUser();
        MsgStationReq req;
        //租户账号
        if(VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(userToken.getUserType())){
            //根据token里的登录账号，查询用户id（铁塔账号切换客户视角，用户id不会变）
            SysUser user1 = this.selectUserOnlyByUserName(userToken.getUserName());
            //根据租户用户消息更新为已读
            req = MsgStationReq.builder()
                    .ifRead("1") //已读
                    .userId(user1.getUserId())
                    .modifier(userToken.getUserId())
                    .industryCode(userToken.getIndustryCode())
                    .build();
        }else{
            //根据铁塔用户消息更新为已读
            req = MsgStationReq.builder()
                    .ifRead("1") //已读
                    .userId(userToken.getUserId())
                    .modifier(userToken.getUserId())
                    .build();
        }

        //更新通知信息
        int count = msgStationMapper.updateMessage(req);
        return Result.data(count);
    }

    /**
     * 保存消息
     * @return
     */
    public int saveMessageInfo(MsgBody msgBody) {
        String orderStatus = msgBody.getOrderStatus(); // 告警事件状态
        String msgId = msgBody.getMsgId(); // 告警ID
        String userId = msgBody.getUserId();  // 用户ID
        String notifyType = msgBody.getNotifyType(); // 通知类型
        String notifyTitle = msgBody.getNotifyTitle(); // 通知标题
        String address = msgBody.getAddress(); // 地址
        String soundCloseType = msgBody.getSoundCloseType();//声音关闭方式 1：自动关闭，2：手动关闭
        String soundDuration = msgBody.getSoundDuration();//播放时长（单位：秒）
        String soundId = msgBody.getSoundId();//声音唯一标识
        String industryCode = msgBody.getIndustryCode();
        String alarmSource = msgBody.getAlarmSource();
        String deviceCode = msgBody.getDeviceCode();
        String deviceName = msgBody.getDeviceName();

        String  userIdStr = userId.split("-")[0];

        String uuid = UUIDUtils.getUUID();
        //构建消息实体
        MsgStationReq req = MsgStationReq.builder()
                .uniId(uuid)
                .msgId(msgId)
                .userId(userIdStr)
                .notifyType(notifyType)
                .notifyTitle(notifyTitle)
                .orderStatus(orderStatus)
                .address(address)
                .creator(userIdStr)
                .soundCloseType(soundCloseType)
                .soundDuration(soundDuration)
                .soundId(soundId)
                .industryCode(industryCode)
                .alarmSource(alarmSource)
                .deviceCode(deviceCode)
                .deviceName(deviceName)
                .build();
        //记录推送数据
        return  msgStationMapper.saveMessageInfo(req);
    }

    /**
     * 更新消息
     *
     * @return
     */
    public Result updateMessage(String uniId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //根据token里的登录账号，查询用户id（铁塔账号切换客户视角，用户id不会变）
        SysUser user1 = this.selectUserOnlyByUserName(user.getUserName());
        //根据用户消息更新为已读
        MsgStationReq req = MsgStationReq.builder()
                .uniId(uniId)
                .ifRead("1") //已读
                .userId(user1.getUserId())
                .modifier(user.getUserId())
                .build();

        //更新通知信息
        int count = msgStationMapper.updateMessage(req);
        return Result.data(count);
    }


    public SysUser selectUserOnlyByUserName(String userName) {
        SysUser user = msgStationMapper.selectUserOnlyByUserName(userName);
        if(user != null && !StringUtils.isBlank(user.getPhonenumber())){
            user.setPhonenumber(user.getPhonenumber().replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2"));
        }
        return user;
    }

    public Map<String, Object> findSoundUrlByIds(List<String> soundIdFindList) {
        List<AlarmSoundDTO> list = msgStationMapper.findSoundUrlByIds(soundIdFindList);
        //list 转 map
        if(list != null && !list.isEmpty()){
            return list.stream()
                    .collect(Collectors.toMap(
                            alarmSoundDTO -> String.valueOf(alarmSoundDTO.getAlarmSoundId()),
                            AlarmSoundDTO::getDocUrl
                    ));
        }
        return null;
    }
}
