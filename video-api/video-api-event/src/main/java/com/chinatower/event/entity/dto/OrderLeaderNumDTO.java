package com.chinatower.event.entity.dto;

import java.io.Serializable;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警工单列表VO对象实体
 * <p>
 * 类名称: OrderLeaderNumDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/24 11:16
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderLeaderNumDTO extends BasePageDTO implements Serializable {
    /**
     * 告警单ID(yyyyMM+租户id+)
     */
    private String orderId;

    /**
     * 领导批示数量
     */
    private int leaderNum;


    private static final long serialVersionUID = 1L;
}