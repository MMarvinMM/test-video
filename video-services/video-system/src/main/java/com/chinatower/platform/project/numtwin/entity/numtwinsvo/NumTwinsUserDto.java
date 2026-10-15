package com.chinatower.platform.project.numtwin.entity.numtwinsvo;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/15 11:46
 */

@Data
public class NumTwinsUserDto {

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 优先级
     */
    private String userPriority;

    /**
     * 租户id
     */
    private String customerId;
}
