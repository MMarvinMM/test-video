package com.chinatower.platform.entity.admin;


import com.chinatower.common.core.dto.PageDTO;

/**
 * 用户对象 sys_enforcement_info
 *
 * @author zhang.xionghui
 */
public class SysEnforcementInfo extends PageDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型 （0系统用户 2租户）
     */
    private String userType;

    /** 执法编号 */
    private String enforcement;

    /** 润之认证路由 */
    private String adopt;

    /** 润之登录路由 */
    private String login;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEnforcement() {
        return enforcement;
    }

    public void setEnforcement(String enforcement) {
        this.enforcement = enforcement;
    }

    public String getAdopt() {
        return adopt;
    }

    public void setAdopt(String adopt) {
        this.adopt = adopt;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}