package com.chinatower.event.entity.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TradeOrderLeaderAuditDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/22 15:01
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TradeOrderLeaderAuditDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 告警事件编码
     * @required true
     */
    private String orderId;

    /**
     * 来源：0铁塔 2租户
     * @required false
     */
    private String source;

    /**
     * 用户类型，1用户，2角色
     * @required false
     */
    private String userType;

    /**
     * 消息通知类型，1：APP(废弃，走APP用户维度配置), 2：短信，3：PC,多种方式中间用逗号隔开
     * @required false
     */
    private String msgNotifyType;

    /**
     * 角色ID，中间用逗号隔开
     * @required false
     */
    private String roleId;

    /**
     * 申请内容
     * @required true
     */
    private String content;

    /**
     * 领导ID
     * @required true
     */
    private String leaderId;

    /**
     * 租户ID
     * @required false
     */
    private String tenantId;

    /**
     * 领导批示ID集合
     * @required false
     */
    private List<String> leaderIdList;
}
