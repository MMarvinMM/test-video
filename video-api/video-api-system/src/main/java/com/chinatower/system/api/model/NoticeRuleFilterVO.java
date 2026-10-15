package com.chinatower.system.api.model;

import lombok.Data;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 查询基本信息
 * @data 创建时间：2023/8/21 16:45
 */
@Data
public class NoticeRuleFilterVO {
    /**
    * 告警编码
    * @required false
    */
    private String orderId;

    /**
     * 告警来源
     * @required false
     */
    private String warningSource;

    /**
     * 告警类型编码
     * @required false
     */
    private String warningTypeId;

    /**
     * 形成告警时间
     * @required false
     */
    private String alarmTime;
    /**
     * 用户ID
     * @required false
     */
    private String userId;

    /**
     * 行业
     * @required false
     */
    private String industryCode;

    /**
     * 是否实时请求 0：否  1：是
     * @required false
     */
    private String isNow;


}
