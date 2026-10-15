package com.chinatower.event.dto.smssendstatistics;

import lombok.Data;

import java.util.Date;
import java.util.List;

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

    private List<String> userList;
}
