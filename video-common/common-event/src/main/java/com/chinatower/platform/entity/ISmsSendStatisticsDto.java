package com.chinatower.platform.entity;

import lombok.Data;

import java.util.List;

@Data
public class ISmsSendStatisticsDto {

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

    private List<String> userList;
}
