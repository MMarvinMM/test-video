package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class BaseErrorRecordVO {

    private String id;

    private String deviceCode;

    private String channelCode;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 关键字的值
     */
    private String keywordsValue;


    /**
     * 请求报文
     */
    private String request;


    /**
     * 返回报文
     */
    private String response;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 当前行
     */
    private String currentRow;

    /**
     * 当前类
     */
    private String currentClass;

}
