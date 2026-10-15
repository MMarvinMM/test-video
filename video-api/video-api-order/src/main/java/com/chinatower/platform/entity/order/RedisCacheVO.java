package com.chinatower.platform.entity.order;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

/**
 * 缓存项信息表
 * @author luozhenghong
 * @date 2020-07-04
 */
@Data
public class RedisCacheVO extends BaseIndustryEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 缓存键
     * @required true
     */
    private String cacheKey;

    /**
     * 缓存值
     * @required true
     */
    private String cacheValue;

    private String cacheType;

    /**
     * 超时事件
     * required false
     */
    private Long cacheTimeout;


}
