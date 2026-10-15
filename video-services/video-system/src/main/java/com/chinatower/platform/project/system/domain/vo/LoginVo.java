package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.common.utils.security.RsaUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginVo {

    @NotBlank(message="登录账号不能为空!")
    private String username;

    @NotBlank(message="登录密码不能为空!")
    private String password;

    @NotBlank(message="验证码不能为空!")
    private String code;

    private String uuid;

    public String getUsername() {
        try {
            return RsaUtils.decryptByPrivateKey(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

    public String getPassword() {
        try {
            return RsaUtils.decryptByPrivateKey(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
}
