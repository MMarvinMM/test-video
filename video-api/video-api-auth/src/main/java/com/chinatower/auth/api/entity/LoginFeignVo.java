package com.chinatower.auth.api.entity;

import cn.hutool.core.util.StrUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginFeignVo {

    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空!")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空!")
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * uuid标识
     */
    private String uuid;

    /**
     * 行业Id
     */
    private String tradeId;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 登录类型 1-图形验证码 2-短信验证码
     */
    private String loginType;

    /**
     * 切换行业标识
     */
    private String switchFlag;

    /**
     * 登录方式 :web,app默认是web
     */
    @NotBlank(message = "appId不能为空!")
    private String appId;

    public String getUsername() {
        try {
            if (StrUtil.isNotEmpty(username)) {
                return RsaUtils.decryptByPrivateKey(username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

    public String getPassword() {
        try {
            if (StrUtil.isNotEmpty(password)) {
                return RsaUtils.decryptByPrivateKey(password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public String getAppId() {
        return StrUtil.isEmpty(appId) ? "组织age:123456" : RsaUtils.decryptByPrivateKey(appId);
    }

    public String toOriginStringForLog() {
        return "LoginVo{" +
                "username=" + username +
                ", password=" + password +
                ", code=" + code +
                ", uuid=" + uuid +
                ", tradeId=" + tradeId +
                ", appVerCode=" + appVerCode +
                ", phoneNumber=" + phoneNumber +
                ", loginType=" + loginType +
                ", switchFlag=" + switchFlag +
                ", appId=" + appId +
                '}';
    }

}
