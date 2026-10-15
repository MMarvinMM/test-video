package com.chinatower.platform.entity.admin;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author bruce
 */
@Data
public class SysLogininfor extends BaseDataScopeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long infoId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 操作系统
     */
    private String os;
    /**
     * 操作系统版本
     */
    private String osVersion;
    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private Date loginTime;

    /**
     * 客户端类型
     */
    private String clientType;
    /**
     * 手机型号
     */
    private String  phoneModel;
    /**
     * APP版本号
     */
    private String  appVersion;
    /**
     * 手机号
     */
    private String  phonenumber;

    /**
     * 用户类型0铁塔 2-客户
     */
    private String  userType;

    /**
     * 租户ID
     */
    private String  tenantId;

    /**
     * 部门ID
     */
    private String  deptId;
    /**
     * 租户名称
     */
    private String  tenantName;
    /**
     * 用户昵称
     */
    private String nickName;
}
