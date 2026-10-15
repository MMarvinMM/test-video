package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: 用户登录统计
 * @data 创建时间：2023/3/28 17:06
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class UserLoginRes {
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 登录次数
     */
    private Long count;
}
