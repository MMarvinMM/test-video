package com.chinatower.platform.auth.project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.auth.project.entity.request.RefreshRequest;
import com.chinatower.platform.auth.service.StoreTokenService;
import com.chinatower.platform.auth.service.RefreshService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.security.MD5Util;
import com.chinatower.platform.constants.Constants;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * video-auth
 * @Description: 刷新token
 * @Author: 张帅
 * @Date: 2021/12/30 11:35
 **/
@RestController
@Slf4j
public class RefreshController {


    @Autowired
    private RefreshService refreshService;

    @Resource
    private StoreTokenService storeTokenService;

    /**
     * 刷新token
     *
     * @return 结果
     */
    @PostMapping("/refreshToken")
    public Result refreshToken(@Validated @RequestBody RefreshRequest refreshRequest) {
        Result ajax = Result.success("刷新成功！");
        Map<String, Object> tokenMap;
        try {
            String appId = refreshRequest.getAppId();
            if(Constants.ONE.equals(refreshRequest.getSource())){
                appId = Constants.APP_ID;
            }
            if (checkToken(refreshRequest)) {
                return Result.fail(ResultCode.UN_AUTH);
            }

            tokenMap = refreshService.refreshToken(refreshRequest.getRefreshToken(), appId);
            if (null == tokenMap) {
                log.error("认证服务器异常");
                ajax = Result.fail("认证服务器异常！");
            } else {
                log.info("刷新token成功");
                storeTokenService.cacheToken(tokenMap);
                ajax = Result.success("刷新token成功！");
                ajax.setData(tokenMap);
            }
        } catch (Exception e) {
            log.error("刷新token异常", e);
            if (e instanceof CustomException) {
                ajax = Result.fail(e.getMessage());
            } else {
                ajax = Result.fail(ResultCode.ERROR);
            }
        }
        return ajax;
    }

    private boolean checkToken(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        String appId = refreshRequest.getAppId();
        if(Constants.ONE.equals(refreshRequest.getSource())){
            appId = Constants.APP_ID;
        }
        String clientId = appId.split(":")[0];
        String tokenMd5 = MD5Util.hash(refreshToken);
        String tokenBlackListKey = CacheConstants.getTokenBlackListKey(clientId, tokenMd5);
        Boolean inBlack = RedisUtil.hasKey(tokenBlackListKey, RedisDBType.PRIMARY);
        if (inBlack != null && inBlack) {
            String token = RedisUtil.get(tokenBlackListKey, RedisDBType.PRIMARY);
            return refreshToken.equalsIgnoreCase(token);
        }
        return false;
//
//        String userId = parseUserId(refreshToken);
//        String userKey = clientId + ":" + userId;
//        String cacheKey = CacheConstants.USER_BLACK_LIST + userKey;
//        return RedisUtil.hasKey(cacheKey, RedisDBType.PRIMARY);
    }

    private static String parseUserId(String token) {
        Jwt jwt = JwtHelper.decode(token);
        String claimsStr = jwt.getClaims();
        JSONObject jsonObject = JSON.parseObject(claimsStr);
        return jsonObject.getString("userId");
    }


    @PostMapping("/login/refreshWebLoginTime")
    public Result refreshPcLoginTime() {
        refreshService.refreshPcLoginTime();
        return Result.success("success");
    }
}
