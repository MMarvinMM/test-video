package com.chinatower.platform.entity.request;

import lombok.Data;

@Data
public class WxMsgData {

    private String first;
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private String keyword4;
    private String keyword5;
    private String remark;
    /**
     * 事件类型
     * @required true
     */
    private String thing8;
    /**
     * 告警时间
     * @required true
     */
    private String time3;
    /**
     * 事件状态
     * @required true
     */
    private String phrase9;
    /**
     * 告警地点
     * @required true
     */
    private String thing4;
    /**
     * 事件标题
     * @required true
     */
    private String thing15;
    /**
     * 事发地点
     * @required true
     */
    private String thing14;
}
