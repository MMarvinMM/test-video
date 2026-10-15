package com.chinatower.platform.api.entity;

import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author iwhalecloud
 */
@Data
@ToString
public class SysLogininfoEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 客户id */
    @Excel(name = "客户ID")
    private String tenantId;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String tenantName;

    /** 客户名称 */
    @Excel(name = "账号类型",readConverterExp = "0=铁塔,2=客户")
    private String userType;

    /** 铁塔组织id */
    private String deptId;


    /** ID */
    private String infoId;

    /** 用户账号 */
    @Excel(name = "登录账号")
    private String userName;

    /** 登录IP地址 */
    @Excel(name = "登录地址")
    private String ipaddr;

    @Excel(name = "登录端",readConverterExp = "web_manage=PC,app_manage=APP,wx_manage=小程序,web=PC,app=APP")
    private String clientType;

    /** 浏览器类型 */
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 浏览器版本
     */
    @Excel(name = "浏览器版本号")
    private String browserVersion;
    /**
     * 手机型号
     */
    @Excel(name = "手机型号")
    private String  phoneModel;

    /** 操作系统 */
    @Excel(name = "操作系统")
    private String os;
    /**
     * 系统版本
     */
    private String osVersion;
    @Excel(name = "手机号码")
    private String  phonenumber;

    /**
     * APP版本号
     */
    @Excel(name = "APP版本号")
    private String  appVersion;

    /** 登录状态 0成功 1失败 */
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    private String status;

    /** 提示消息 */
    @Excel(name = "操作信息")
    private String msg;

    /** 登录地点 */
    private String loginLocation;

    /** 访问时间 */
    @Excel(name = "登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date loginTime;





}
