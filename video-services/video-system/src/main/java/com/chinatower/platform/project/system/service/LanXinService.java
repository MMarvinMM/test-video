package com.chinatower.platform.project.system.service;


import com.chinatower.platform.project.system.domain.AppTokenData;
import com.chinatower.platform.project.system.domain.UserInfoResponse;
import com.chinatower.platform.project.system.domain.UserTokenResponse;

/**
 * 功能描述: 蓝信服务
 */
public interface LanXinService {


    /**
     * 获取应用访问token
     */
    String getAppToken();

    /**
     * 获取人员访问TOKEN
     * @param code 前端传来的授权码
     * @return 用户token信息
     */
    String getUserToken(String code);


    /**
     * 获取人员基本信息
     * @param userToken 人员访问凭证
     * @return 人员基本信息
     */
    String getUserInfo(String appToken,String userToken);


    /**
     * 获取JSAPI访问TOKEN
     * @param code 人员访问凭证
     * @return 人员基本信息
     */
    String getApiToken();

    /**
     * 获取JSAPI访问TOKEN及前端所需信息
     * @param url
     * @return 人员基本信息
     */
    AppTokenData getApiTokenInfo(String url);
}
