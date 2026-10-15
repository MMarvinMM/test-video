package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

import java.util.List;

/**
 * 发送消息Dto
 */
@Data
public class SmsSendStatisticsDto {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 发送日期（yyyy-MM-dd）
     */
    private String sendDate;

    /**
     * 发送条数
     */
    private Integer sendCount;

    /**
     * 用户集合
     */
    private List<String> userList;
}
