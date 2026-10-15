package com.chinatower.message.station.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName: MessageVO
 * @Description: 消息通知VO
 * @Author: wjd
 * @Date: 2022/1/25 16:07
 **/
@Data
@ToString
public class MessageVO {
    private static final long serialVersionUID = 1L;

    private String uniId; //唯一id
    private String msgId; //告警/内容ID
    private String userId; //用户ID
    private String notifyType; //通知类型:1系统提醒
    private String notifyTypeName; //通知类型名称
    private String notifyTitle; //通知标题
    private String creator; //创建人
    private String creatorDate; //创建时间
    private String startTime; //开始时间
    private String endTime;//结束时间
    private String ifRead; //是否已读：1是0否
    private String deleteFlag;//删除标志
    private int pageNum; //页码
    private int pageSize; //每页条数

}
