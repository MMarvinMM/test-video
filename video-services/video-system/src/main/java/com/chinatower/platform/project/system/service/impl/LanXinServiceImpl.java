package com.chinatower.platform.project.system.service.impl;




import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.project.system.domain.AppTokenData;
import com.chinatower.platform.project.system.domain.AppTokenResponse;
import com.chinatower.platform.project.system.domain.UserInfoResponse;
import com.chinatower.platform.project.system.domain.UserTokenResponse;
import com.chinatower.platform.project.system.service.LanXinService;
import com.chinatower.platform.project.system.utils.HMAC_SHA1;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


/**
 * 功能描述: 蓝信服务
 */
@Service
@Slf4j
public class LanXinServiceImpl implements LanXinService {


    @Value("${lanxinApp.domain:}")
    private String domain;

    @Value("${lanxinApp.id:}")
    private String appId;

    @Value("${lanxinApp.secret:}")
    private String appSecret;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取应用访问token
     */
    public String getAppToken() {
        String lanXinAppToken = "";
        try {
            lanXinAppToken  = RedisUtil.get("lanXin_app_token", RedisDBType.PRIMARY);
            if(StringUtils.isEmpty(lanXinAppToken)){
                String url = buildTokenUrl();
                log.info("buildTokenUrl:"+url);
                AppTokenResponse response = restTemplate.getForObject(url, AppTokenResponse.class);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    log.info("成功获取AppToken，有效期：{}秒", response.getData().getExpiresIn());
                    lanXinAppToken = response.getData().getAppToken();
                    RedisUtil.set("lanXin_app_token", lanXinAppToken, response.getData().getExpiresIn(), RedisDBType.PRIMARY);
                }
            }
        } catch (Exception e) {
            log.error("调用获取AppToken接口异常", e);
        }
        return lanXinAppToken;
    }


    /**
     * 获取人员访问TOKEN
     * @param code 前端传来的授权码
     * @return 用户token信息
     */
    public String getUserToken(String code) {
        String lanXinUserToken = "";
        try {
            // 首先获取应用token
            String appToken = getAppToken();
            String url = buildUserTokenUrl(appToken, code);
            UserTokenResponse response = restTemplate.getForObject(url, UserTokenResponse.class);
            if (response != null && response.isSuccess() && response.getData() != null) {
                log.info("成功获取UserToken，staffId：{}，有效期：{}秒，用户token：{}",
                        response.getData().getStaffId(),
                        response.getData().getExpiresIn(),
                        response.getData().getUserToken());
                lanXinUserToken = response.getData().getUserToken();
            }
        } catch (Exception e) {
            log.error("调用获取UserToken接口异常", e);
        }
        return lanXinUserToken;
    }



    /**
     * 获取人员基本信息
     * @param userToken 人员访问凭证
     * @return 人员基本信息
     */
    public String getUserInfo(String appToken,String userToken) {
        String phoneNumber = "";
        try {
            String url = buildUserInfoUrl(appToken, userToken);
            UserInfoResponse response = restTemplate.getForObject(url, UserInfoResponse.class);
            if (response != null && response.isSuccess() && response.getData() != null) {
                log.info("成功获取人员基本信息，手机号：{}，登录用户名：{}",
                        response.getData().getMobilePhone().getNumber(),
                        response.getData().getLoginName());
                phoneNumber = response.getData().getMobilePhone().getNumber();
            }
        } catch (Exception e) {
            log.error("调用获取人员基本信息接口异常", e);
        }
        return phoneNumber;
    }


    /**
     * 获取应用访问token
     */
    public String getApiToken() {
        String lanXinApiToken = "";
        try {
            lanXinApiToken  = RedisUtil.get("lanXin_js_api_token", RedisDBType.PRIMARY);
            if(StringUtils.isEmpty(lanXinApiToken)){
                String lanXinAppToken = getAppToken();
                String url = buildApiTokenUrl(lanXinAppToken);
                AppTokenResponse response = restTemplate.getForObject(url, AppTokenResponse.class);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    log.info("成功获取ApiToken，有效期：{}秒", response.getData().getExpiresIn());
                    lanXinApiToken = response.getData().getJsApiToken();
                    RedisUtil.set("lanXin_js_api_token", lanXinApiToken, response.getData().getExpiresIn(), RedisDBType.PRIMARY);
                }
            }
        } catch (Exception e) {
            log.error("调用获取ApiToken接口异常", e);
        }
        return lanXinApiToken;
    }


    /**
     * 获取应用访问token
     */
    public AppTokenData getApiTokenInfo(String url) {
        AppTokenData appTokenData = new AppTokenData();
        try {
            String lanXinApiToken = getApiToken();
            log.info("lanXinApiToken:"+lanXinApiToken);
            appTokenData.setJsApiToken(lanXinApiToken);
            String nonceStr = simpleUUID();
            appTokenData.setNonceStr(nonceStr);
            int timestamp = getCurrentTimestamp();
            appTokenData.setTimestamp(timestamp);
            appTokenData.setAppId(appId);
            String signature = generateSignature(lanXinApiToken, nonceStr, String.valueOf(timestamp), url);
            appTokenData.setSignature(signature);
        } catch (Exception e) {
            log.error("调用getApiTokenInfo接口异常", e);
        }
        return appTokenData;
    }

    /**
     * 生成签名
     *
     * @param jsApiToken 应用服务端生成的 js_api_token
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     * @param url 当前网页的完整URL，不包含#及其后面部分
     * @return 签名字符串
     */
    public static String generateSignature(String jsApiToken, String nonceStr, String timestamp, String url) {
        try {
            // 严格按照指定格式拼接字符串
            String string1 = String.format("js_api_token=%s&noncestr=%s&timestamp=%s&url=%s",
                    jsApiToken, nonceStr, timestamp, url);
            // 进行sha1加密
            return HMAC_SHA1.sha1(string1);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }






    public static String simpleUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取当前10位时间戳（单位：秒）
     * @return 10位时间戳
     */
    public static int getCurrentTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }


    /**
     * 构建请求URL
     */
    private String buildTokenUrl() {
        return String.format("%s/v1/apptoken/create?grant_type=client_credential&appid=%s&secret=%s",
                domain, appId, appSecret);
    }


    /**
     * 构建用户token请求URL
     */
    private String buildUserTokenUrl(String appToken, String code) {
        return String.format("%s/v1/usertoken/create?app_token=%s&grant_type=authorization_code&code=%s",
                domain, appToken, code);
    }


    /**
     * 构建用户信息请求URL
     */
    private String buildUserInfoUrl(String appToken, String userToken) {
        return String.format("%s/v1/users/fetch?app_token=%s&user_token=%s",
                domain, appToken, userToken);
    }

    /**
     * 构建请求URL
     */
    private String buildApiTokenUrl(String appToken) {
        return String.format("%s/v1/jsapitoken/create?app_token=%s",
                domain, appToken);
    }

}




