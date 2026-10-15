package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.annotation.Sensitive;
import com.chinatower.common.core.enums.SensitiveStrategy;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author iwhalecloud
 */
@Data
@Document("log_logininfo")
public class SysLogininfoEntity extends BaseMongoDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "LOG_SysLogininfor";

    /** ID */
    @Excel(name = "访问编号")
    @Id
    private String infoId;

    /** 用户账号 */
    @Excel(name = "用户名称")
    private String userName;


    /** 登录IP地址 */
    @Excel(name = "登录地址")
    private String ipaddr;

    /** 浏览器类型 */
    @Excel(name = "浏览器")
    private String browser;

    /** 操作系统 */
    @Excel(name = "操作系统")
    private String os;

    @Excel(name = "操作系统版本")
    private String osVersion;

    /** 登录状态 0成功 1失败 */
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    private String status;

    /** 提示消息 */
    @Excel(name = "提示消息")
    private String msg;

    /** 登录地点 */
    private String loginLocation;

    /** 访问时间 */
    @Excel(name = "登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date loginTime;

    /**
     * 客户端类型
     */
    private String clientType;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 手机型号
     */
    @Excel(name = "手机型号")
    private String  phoneModel;
    /**
     * APP版本号
     */
    @Excel(name = "APP版本号")
    private String  appVersion;

    @Sensitive(strategy = SensitiveStrategy.PHONE)
    @Excel(name = "手机号")
    private String  phonenumber;

    @Override
    public String getCollectionName() {
        if(this.collectionName == null){
            this.collectionName = TABLE_NAME;
        }
        return this.collectionName;
    }

    /** 客户id */
    private String tenantId;

    /** 客户名称 */
    private String tenantName;

    /** 客户名称 */
    private String userType;

    /** 铁塔组织id */
    private String deptId;
}
