package com.chinatower.platform.auth.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: 切换用户对象
 * @Author: 张帅
 * @Date: 2022/03/13 22:26
 **/
@Data
public class SwitchUserRequest  implements Serializable {

    /**
     * 切换类型：0-铁塔切换到租户，2-租户切换到铁塔
     */
    @NotBlank(message = "切换类型不能为空!")

    private String switchType;

    /**
     * 要切换的目标userId
     */
    @NotNull(message = "切换userName不能为空!")
    private String userName;

    /**
     * 选择的行业
     */
    private String chooseTradeId;
    /**
     * 选择的版本
     */
    private String chooseAppVerCode;

    /**
     * 1-不设置过期时间
     */
    private String expireFlag;

    /**
     * 是否需要解密标识（1需要，0不需要）
     */
    private String decryptFlag;
}
