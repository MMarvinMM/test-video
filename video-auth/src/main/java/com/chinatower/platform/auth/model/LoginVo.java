package com.chinatower.platform.auth.model;

import cn.hutool.core.util.StrUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginVo {

    /**
     * 请求来源：1：pc 2：app 3：wx小程序 4：pad 5：win客户端
     */
    private String source;

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
                String decUsername = RsaUtils.decryptByPrivateKey(username);
                if(username.equals(decUsername)){//如果密码解密后和原密码一致，代表加密失败，则返回空字符串
                    return "";
                }else{
                    return decUsername;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return username;
    }

    public String getPassword() {
        try {
            if (StrUtil.isNotEmpty(password)) {
                String decPassword = RsaUtils.decryptByPrivateKey(password);
                if(password.equals(decPassword)){//如果密码解密后和原密码一致，代表加密失败，则返回空字符串
                    return "";
                }else{
                    return decPassword;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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
