package com.chinatower.platform.project.mapper;

import com.chinatower.platform.entity.request.MsgStationReq;
import com.chinatower.platform.project.entity.request.MessageVO;

import java.util.List;
import java.util.Map;

/**
 * @title
 * @description 消息通知业务持久层
 * @author LiZe
 * @updateTime 2022/1/20 14:45
 * @throws
 */
public interface MsgStationMapper {


    /**
     * 保存消息推送记录
     * @param uid 用户id
     * @param msgIn4 推送消息
     * @param id 业务id
     * @return 返回插入成功数量
     */
//    @Insert("insert into video_message_record (id,user_id,message_info) values (#{id},#{uid},#{msgIn4})")
//    int saveMsgRecord(@Param("id")String id,@Param("uid")String uid, @Param("msgIn4") String msgIn4);


    /**
     * 查询消息通知
     * @param msgStationReq 消息通知
     * @return List<MessageVO> 消息通知
     */
    List<Map<String,Object>> getMessageInfo(MsgStationReq msgStationReq);

    /**
     * 查询消息通知
     * @param msgStationReq 消息通知
     * @return List<MessageVO> 返回插入成功数量
     */
    int saveMessageInfo(MsgStationReq msgStationReq);

    /**
     * 更新消息通知
     * @param msgStationReq 消息通知
     * @return List<MessageVO> 返回更新成功数量
     */
    int updateMessage(MsgStationReq msgStationReq);

}
