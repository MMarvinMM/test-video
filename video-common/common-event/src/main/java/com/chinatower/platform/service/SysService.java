package com.chinatower.platform.service;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 16:53
 */
public interface SysService {


    /**
     * 获取当前登录用户id(切换后取对应账号的id)
     * @return
     */
    String getLoginUserId();

}
