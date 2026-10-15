package com.chinatower.platform.auth.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.project.mapper.SysUserMapper;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 功能说明: JwtToken信息补充
 *
 * @author bruce
 * @date 2021-11-18
 */
@Component
@Slf4j
public class JwtTokenEnhancer implements TokenEnhancer {
    @Resource
    private AuthProperties authProperties;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.info("JwtTokenEnhancer enhance");
        LoginUser securityUser = (LoginUser) authentication.getPrincipal();
        String clientId = authentication.getOAuth2Request().getClientId();
        Map<String, Object> info = new HashMap<>();
        //把用户ID设置到JWT中
        String userId = securityUser.getUser().getUserId();
        info.put("userId", userId);
        info.put("user_name", clientId + "|" + securityUser.getUser().getUserName());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        Calendar now = Calendar.getInstance();
        if (clientId.startsWith(OauthConstants.USER_LOGIN_TYPE_WEB)) {//web端用户
            now.add(Calendar.SECOND, authProperties.getWebTokenValiditySeconds());
        } else {//app端用户
            now.add(Calendar.SECOND, authProperties.getAppTokenValiditySeconds());
        }
        ((DefaultOAuth2AccessToken) accessToken).setExpiration(now.getTime());
        //把用户信息写入缓存
        securityUser.getUser().setUserId(userId);
        cacheLoginUser(securityUser, accessToken, clientId);
        return accessToken;
    }

    private void cacheLoginUser(LoginUser securityUser, OAuth2AccessToken accessToken, String clientId) {
        securityUser.setLoginTime(System.currentTimeMillis());
        securityUser.setToken(accessToken.getValue());
        long expireTime = accessToken.getExpiresIn();
        securityUser.setExpireTime(accessToken.getExpiration().getTime());
        // 根据uuid将loginUser缓存
        SysUser user = securityUser.getUser();
        String userKey = clientId + ":" + user.getUserId();
        String cacheKey = CacheConstants.USER_INDUSTRY_CODE + userKey;

        String platformCode = "";
        SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
        if (ObjectUtil.isNotNull(sysConfig)) {
            platformCode = sysConfig.getConfigValue();
        }
        log.info("platformCode="+platformCode);
        List<SysIndustryViewRel> list = userMapper.selectUserAppVerCodesByDeptId(securityUser.getUser().getDeptId(),platformCode);
        if (RedisUtil.hasKey(cacheKey, RedisDBType.PRIMARY)) {
            //检查缓存,只更新不删除
            log.info("AuthorizationManager ==> checkLoginUser: 1 cache key:{}", cacheKey);
            if (CollUtil.isNotEmpty(list)) {
                Map<Object, Object> objectObjectMap = RedisUtil.hGetAll(cacheKey, RedisDBType.PRIMARY);
                Set redisList = objectObjectMap.keySet();
                Set dbList = new HashSet();
                list.stream().map(SysIndustryViewRel::getIndustryCode).forEach(val -> {
                    dbList.add(val);
                    if (RedisUtil.hExists(cacheKey, val, RedisDBType.PRIMARY)) {
                        log.info("AuthorizationManager ==> checkLoginUser: 2 update:cache key:{},industryCode:{}", cacheKey, val);
                        //更新信息
                        LoginUser loginUser = JSONObject.parseObject(RedisUtil.hGet(cacheKey, val, RedisDBType.PRIMARY), LoginUser.class);
                        updateLoginUserInfo(loginUser, securityUser);
                        RedisUtil.hset(cacheKey, val, JSON.toJSONString(loginUser), RedisDBType.PRIMARY);
                    } else {
                        log.info("AuthorizationManager ==> checkLoginUser: 3 add:cache key:{},industryCode:{}", cacheKey, val);
                        //新增
                        RedisUtil.hset(cacheKey, val, JSON.toJSONString(BeanUtil.copyProperties(securityUser, LoginUser.class))
                                , RedisDBType.PRIMARY);
                    }
                });

                Set deleteList = new HashSet(redisList);
                deleteList.removeAll(dbList);
                deleteList.forEach(val -> {
                    if("999999".equals(val)){
                        //删除
                        RedisUtil.hDelete(cacheKey, RedisDBType.PRIMARY, val);
                    }
                });
            }
        } else {
            //设置缓存
            log.info("AuthorizationManager ==> cacheLoginUser: 1 cache key:{}", cacheKey);
            if (CollUtil.isNotEmpty(list)) {
                list.stream().map(SysIndustryViewRel::getIndustryCode).forEach(val -> {
                    RedisUtil.hset(cacheKey, val, JSON.toJSONString(BeanUtil.copyProperties(securityUser, LoginUser.class))
                            , RedisDBType.PRIMARY);
                });
                log.info("AuthorizationManager ==> cacheLoginUser: 2 getList:cache key:{},list:{}", cacheKey, list);
            } else {
                //兜底，保证能登录
                log.info("AuthorizationManager ==> cacheLoginUser: 3 default industry cache key:{}", cacheKey);
                RedisUtil.hset(cacheKey, "000000",
                        JSON.toJSONString(BeanUtil.copyProperties(securityUser, LoginUser.class)), RedisDBType.PRIMARY);
            }
        }
        RedisUtil.expire(cacheKey, expireTime, TimeUnit.SECONDS, RedisDBType.PRIMARY);
        RedisUtil.delete(CacheConstants.USER_BLACK_LIST + userKey, RedisDBType.PRIMARY);
    }


    /**
     * 更新登录用户信息
     */
    private void updateLoginUserInfo(LoginUser cache, LoginUser db) {
        cache.setPermissions(db.getPermissions());
        SysUser cacheUser = cache.getUser();
        String chooseTradeId = cacheUser.getChooseTradeId();
        String chooseAppVerCode = cacheUser.getChooseAppVerCode();
        String industryCode = cacheUser.getIndustryCode();
        String appVerCode = cacheUser.getAppVerCode();

        //多行业的话先沿用之前缓存的行业信息
        SysUser dbUser = BeanUtil.copyProperties(db.getUser(), SysUser.class);
        boolean isMultiIndustry = dbUser.getChooseTradeId() == null
                || dbUser.getChooseAppVerCode() == null
                || dbUser.getIndustryCode() == null
                || dbUser.getAppVerCode() == null;
        if (isMultiIndustry) {
            dbUser.setChooseTradeId(chooseTradeId);
            dbUser.setChooseAppVerCode(chooseAppVerCode);
            dbUser.setIndustryCode(industryCode);
            dbUser.setAppVerCode(appVerCode);
            dbUser.setDept(cacheUser.getDept());
            dbUser.setDeptId(cacheUser.getDeptId());
            dbUser.setDeptIds(cacheUser.getDeptIds());
            dbUser.setAreaTree(cacheUser.getAreaTree());
        }

        cache.setUser(dbUser);
    }

}
