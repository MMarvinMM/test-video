package com.chinatower.message.api.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 低空态势飞行点位通知用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowAltPointMsg implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 扩展信息,需要通知给小塔的信息
     */
    private JSONObject extendInfo;

    private WebSocketMsgType webSocketMsgType;

    public void putFormatMessage(String message) {
        extendInfo.put("message", message);
    }

}
