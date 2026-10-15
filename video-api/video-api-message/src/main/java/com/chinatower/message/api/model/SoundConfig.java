package com.chinatower.message.api.model;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class SoundConfig extends CommonFeignEntity implements Serializable {

    //消息来源：1-大屏控制台，2-工作台
    private Integer source;

    //指令类型：1-声音控制，2-弹窗控制
    private Integer messageType;

    //操作指令：1-开启，2-关闭
    private Integer msgValue;

    //告警事件编码
    private String msgId;

    //告警事件状态
    private String orderStatus;

    /**
     * 4：声音开关控制
     */
    private Integer type;
}
