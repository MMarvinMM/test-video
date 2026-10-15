package com.chinatower.video.api.entity;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能说明: 视频管理-简约模式，抓图接口出参实体
 * @param:
 * @return:
 * @author: hued
 * @date: 2022年10月10日16:29:42
 * @requestID  R-ZBZB-202210080955525 视频管理-实时视频增加简约模式
 */
@Data
@ToString
public class SnapShotSimpleRes extends BaseMongoDTO implements Serializable{
    private String uuid;
    private String deviceCode;
    private String deviceName;
    private String channelCode;
    private String channelName;
    //通道状态，0在线，1离线
    private String status;
    //最新抓图地址
    private String url;
    //最新一次抓图时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastUrlDate;
    //最新一次请求时间
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastReqDate;

    private String backUrl;
}
