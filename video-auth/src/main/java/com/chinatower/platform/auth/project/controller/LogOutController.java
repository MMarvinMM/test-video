package com.chinatower.platform.auth.project.controller;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.config.AuthProperties;
import com.chinatower.platform.common.utils.HttpServletUtils;
import com.chinatower.platform.common.utils.ServletUtils;
import com.chinatower.platform.common.utils.security.MD5Util;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * video-auth
 * @Description: 登出controller
 * @Author: 张帅
 * @Date: 2021/11/24 17:31
 **/
@RestController
@Slf4j
public class LogOutController {

    @Resource
    private ConsumerTokenServices consumerTokenServices;

    @Resource
    private AuthProperties authProperties;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 登出
     * @return
     */
    @PostMapping("/base/logout")
    public Result logout() {
        Result result = Result.success("操作成功！");
        String userKey = HttpServletUtils.getRequestHeader(FeignConstants.HEADER_USER_KEY);
        if (StrUtil.isNotEmpty(userKey)) {
            try {
                String[] split = userKey.split(":");
                String clientId = split[0];
                String userId = split[1];
                String cacheKey = CacheConstants.getUserCacheKey(clientId, userId);
                RedisUtil.delete(cacheKey, RedisDBType.PRIMARY);
                log.info("LogOutController cacheKey:{},del:{}", cacheKey, RedisUtil.hasKey(cacheKey, RedisDBType.PRIMARY));
                String token = ServletUtils.getRequest().getHeader("Authorization");
                log.info("LogOutController token:{}", token);
                String realToken = token.replace("Bearer ", "");
                consumerTokenServices.revokeToken(realToken);
                //如果是web端 重置连续刷新过期时间次数0
                if (ClientType.WEB_MANAGE.getClientType().equals(clientId)){
                    //更新用户对应web端刷新次数
                    BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,Constants.REFRESH_COUNT_PC);
                    Query updateQuery = new Query();
                    updateQuery.addCriteria(Criteria.where(Constants.USER_KEY).is(cacheKey));
                    Update update = new Update();
                    update.set(Constants.USER_REFRESH_COUNT,0);
                    bulkOperations.upsert(updateQuery, update);
                    bulkOperations.execute();
                }
                //加黑名单
                CompletableFuture.runAsync(() -> {
                    String userTokensKey = CacheConstants.getUserTokensKey(clientId, userId);
                    Set<ZSetOperations.TypedTuple<String>> typedTuples = RedisUtil.zRangeByScoreWithScores(userTokensKey, Long.valueOf(System.currentTimeMillis()).doubleValue(), Double.MAX_VALUE, RedisDBType.PRIMARY);
                    boolean containThis = false;
                    long tokenSeconds = ClientType.WEB_MANAGE.getClientType().equals(clientId) ? authProperties.getWebTokenValiditySeconds() : authProperties.getAppTokenValiditySeconds();
                    long defaultExpireMs = tokenSeconds * 1000;
                    if (typedTuples != null) {
                        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
                            String tokenOne = typedTuple.getValue();
                            Double expire = typedTuple.getScore();
                            //默认值，保证不报错
                            long time = defaultExpireMs;
                            if (expire != null) {
                                time = Math.max(expire.longValue() - System.currentTimeMillis(), 0);
                            }

                            if (!containThis && realToken.equals(tokenOne)) {
                                containThis = true;
                            }
                            String tokenMd5 = MD5Util.hash(tokenOne);
                            String tokenBlackListKey = CacheConstants.getTokenBlackListKey(clientId, tokenMd5);
                            RedisUtil.set(tokenBlackListKey, tokenOne, time, TimeUnit.MILLISECONDS, RedisDBType.PRIMARY);
                        }
                        //加黑名单后删除用户token缓存，防止值过大
                        RedisUtil.delete(userTokensKey, RedisDBType.PRIMARY);
                    }
                    if (!containThis) {
                        log.info("退出登录->当前token直接加入黑名单");
                        String tokenMd5 = MD5Util.hash(realToken);
                        String tokenBlackListKey = CacheConstants.getTokenBlackListKey(clientId, tokenMd5);
                        RedisUtil.set(tokenBlackListKey, realToken, defaultExpireMs, TimeUnit.MILLISECONDS, RedisDBType.PRIMARY);
                    }
                });

            } catch (Exception e) {
                log.error("Redis退出登录操作异常", e);
            }
        }
        return result;
    }

}
