package com.chinatower.common.core.enums;

import cn.hutool.crypto.digest.MD5;
import com.chinatower.common.core.constant.CacheConstants;
import lombok.Getter;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 登录端
 * @data 创建时间：2023/3/30 11:33
 */
@Getter
public enum ClientType {
    APP_MANAGE("app_manage", "app端"), WEB_MANAGE("web_manage", "pc端"), WX_MANAGE("wx_manage", "小程序端"), PAD_MANAGE("pad_manage", "pad端");

    /**
     * 登录端
     */
    private final String clientType;
    /**
     * 描述
     */
    private final String remark;

    ClientType(String clientType, String remark) {
        this.clientType = clientType;
        this.remark = remark;
    }

    /**
     * 获取用户缓存key
     *
     * @param userId 用户id
     * @return 缓存key
     */
    public String getCacheUserKey(String userId) {
        return CacheConstants.getUserCacheKey(this.getClientType(),userId);
    }

    /**
     * 获取用户token缓存key
     *
     * @param userId 用户id
     * @return 缓存key
     */
    public String getCacheTokensKey(String userId) {
        return CacheConstants.getUserTokensKey(this.getClientType(),userId);
    }

    /**
     * 获取用户token黑名单缓存key
     *
     * @param tokenMd5 token加密值
     * @return 缓存key
     */
    public String getCacheTokenBlackListKey(String tokenMd5) {
        return CacheConstants.getTokenBlackListKey(this.getClientType(),tokenMd5);
    }

}
