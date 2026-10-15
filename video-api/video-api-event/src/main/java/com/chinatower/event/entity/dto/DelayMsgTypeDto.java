package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class DelayMsgTypeDto implements Serializable {

    /**
     * 用户Id
     * @required true
     */
    private String userId;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;

    /**
     * 消息类型
     * @required true
     */
    private String msgType;

}
