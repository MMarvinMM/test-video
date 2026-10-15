package com.chinatower.event.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Auther: luo.zhenghong
 * @Date: 2022-7-25
 * @Description:
 * 物联设备告警接收请求实体
 */
@Data
public class AntiShakeValueVO {
    private static final long serialVersionUID = 1L;
    /**
     * 触发次数 符合阈值次数
     */
    private BigDecimal triggerValue;
    /**
     * 持续超标次数 符合阈值次数
     */
    private BigDecimal continueValue;
    /**
     * 突然超标次数  不符合阈值次数
     */
    private BigDecimal suddenValue;
    /**
     * 触发次数 redis超时时间设置
     */
    private Long timeoutValue;
    /**
     * 物联配置id
     */
    private String configId;
    /**
     * 物联规则id
     */
    private String groupId;

    /**
     * 存储类型 1：redis   2：MongoDB
     */
    private String valueType;
}
