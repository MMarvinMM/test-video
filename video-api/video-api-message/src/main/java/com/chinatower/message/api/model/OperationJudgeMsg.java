package com.chinatower.message.api.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI智能研判通知用户实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationJudgeMsg implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 行业编码
     */
    @Builder.Default
    private String industryCode = "999999";

    /**
     * 扩展信息,需要通知给小塔的信息
     */
    @Builder.Default
    private JSONObject extendInfo = new JSONObject();

    @Builder.Default
    private WebSocketMsgType webSocketMsgType = WebSocketMsgType.OPEN_DIGITAL_HUMANS;

    public void putFormatMessage(String message) {
        extendInfo.put("message", message);
    }

}
