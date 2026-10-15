package com.chinatower.video.api.entity;

import lombok.Data;

/**
 * 获取/释放云台控制权返回Data
 * Created by zhujun on 2022/1/13.
 */
@Data
public class DoLockOrReleasePtzData {

    // 1获得锁 0没获得锁
    private int lock;

    // 请求令牌
    private String token;

    // 可用时间
    private int duration;

    // 等待时长
    private int waitDuration;

    // 排队位次
    private int queue;

    // 排队时间
    private int wait;

    // 申请锁的当前用户
    private String userCode;

    //占用的用户编码
    private String currentExecuteUserCode;

    // 用户名称
    private String userName;

    // 用户名称（明文）
    private String realUserName;

    // 组织名称
    private String deptName;

    @Data
    public static class DoLockOrReleasePtzDataEntity extends CommonResultEntiry {
        private DoLockOrReleasePtzData data;
    }
}
