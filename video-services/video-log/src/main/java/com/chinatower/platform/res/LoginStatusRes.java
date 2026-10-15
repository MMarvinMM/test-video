package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: 登录状态统计
 * @data 创建时间：2023/3/29 13:55
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LoginStatusRes {
    /**
     * 状态0-成功 1-失败
     */
    private String status;
    /**
     * 次数
     */
    private Long count;
}
