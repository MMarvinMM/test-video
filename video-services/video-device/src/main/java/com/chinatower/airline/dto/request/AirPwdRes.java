package com.chinatower.airline.dto.request;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/5/20 14:57
 */
@Data
public class AirPwdRes {
    private String havePwd;
    private String needPwd;
    private String pwdStr;
    private String isCheck;
    private String errorStr;
    private String password;
    private String errorTimesLimit;
    private String lockTime;
    private String lockTimeUnit;
    private String adminName;
}
