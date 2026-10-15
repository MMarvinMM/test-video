package com.chinatower.platform.auth.project.entity.resp;

/**
 * @Description: 校验密码返回实体类
 * @Date: 2022年 11月 23日 15:35
 */
public class CheckPwdResponse {
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 手机号码
     */
    private String phonenumber;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 是否白名单（0-否，1-是）
     */
    private Integer isWhite;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getIsWhite() {
        return isWhite;
    }

    public void setIsWhite(Integer isWhite) {
        this.isWhite = isWhite;
    }
}
