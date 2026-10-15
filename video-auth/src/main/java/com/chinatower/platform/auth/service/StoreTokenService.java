package com.chinatower.platform.auth.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.platform.auth.config.AuthProperties;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * token存储服务
 *
 * @author qq on 2023/5/12
 */
@Service
@Slf4j
public class StoreTokenService {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String USER_NAME = "user_name";
    private static final String USER_ID = "userId";
    private static final int TOKEN_MAX_SIZE = 30;

    @Resource
    private AuthProperties authProperties;

    public void cacheToken(Map<String, Object> tokenMap) {
        try {
            if (tokenMap == null) {
                return;
            }
            Object un = tokenMap.get(USER_NAME);
            Object uid = tokenMap.get(USER_ID);
            if (un == null || uid == null) {
                log.error("token存储->token解析异常{}", JSON.toJSONString(tokenMap));
                return;
            }
            Object at = tokenMap.get(ACCESS_TOKEN);
            Object rt = tokenMap.get(REFRESH_TOKEN);
            Object ex = tokenMap.get(EXPIRES_IN);

            String username = un.toString();
            String clientId = username.substring(0, username.indexOf("|"));
            long now = System.currentTimeMillis();
            Set<ZSetOperations.TypedTuple<String>> values = new HashSet<>();
            int tokenSeconds = (ex == null ? (ClientType.WEB_MANAGE.getClientType().equals(clientId) ? authProperties.getWebTokenValiditySeconds() : authProperties.getAppTokenValiditySeconds()) : Integer.parseInt(ex.toString()));
            long tokenExpirationTime = 1000L * tokenSeconds + now;
            if (at != null) {
                DefaultTypedTuple<String> typedTuple1 = new DefaultTypedTuple<>(at.toString(), Long.valueOf(tokenExpirationTime).doubleValue());
                values.add(typedTuple1);
            }
            int seconds = ClientType.WEB_MANAGE.getClientType().equals(clientId) ? authProperties.getWebRefreshTokenValiditySeconds() : authProperties.getAppRefreshTokenValiditySeconds();
            long refreshTokenExpirationTime = 1000L * seconds + now;
            if (rt != null) {
                DefaultTypedTuple<String> typedTuple2 = new DefaultTypedTuple<>(rt.toString(), Long.valueOf(refreshTokenExpirationTime).doubleValue());
                values.add(typedTuple2);
            }
            String key = CacheConstants.getUserTokensKey(clientId, uid.toString());
            if (!values.isEmpty()) {
                Long size = RedisUtil.zSize(key, RedisDBType.PRIMARY);
                log.info("1-token存储->token长度：{},key={}", size, key);
                if (size > TOKEN_MAX_SIZE) {
                    log.info("token存储->token长度大于{}，目前大小：{},key={}", TOKEN_MAX_SIZE, size, key);
                }
                RedisUtil.zAdd(key, values, RedisDBType.PRIMARY);
                RedisUtil.expire(key, Math.max(tokenSeconds, seconds), TimeUnit.SECONDS, RedisDBType.PRIMARY);
            }
            RedisUtil.zRemoveRangeByScore(key, 0, Long.valueOf(now).doubleValue(), RedisDBType.PRIMARY);
            log.info("2-token存储->token长度：{},key={}", RedisUtil.zSize(key, RedisDBType.PRIMARY), key);
        } catch (Exception e) {
            log.error("token存储->store异常,{}", e.getMessage(), e);
        }
    }
}
