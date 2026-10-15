package com.chinatower.platform.project.custom.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * api注册表
 */
@Data
@ToString
//客户编码
public class AuthRegisterFuncApiVO {
    private String id;

    //注册api外键
    private String registerId;

    //订阅API接口
    private String type;

    //订阅API接口名称
    private String typeName;

    //回调地址
    private String callbackUrl;

    //是否有效 0失效 1有效
    private String deleteFlag;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    //api接口名称
    private String apiIntfName;

    //api接口编码
    private String apiIntfCode;

    //api接口类型 1000：服务端，1100：客户端
    private String apiIntfType;

    //状态
    private String oldStatus;

    //状态
    private String oldDeleteFlag;

    //状态
    private String status;

    //客户编码
    private String customerCode;


    //appKey
    private String appKey;

    //secretKey
    private String secretKey;

    //是否开放api 0:否  1:是
    private String isOpen;

}