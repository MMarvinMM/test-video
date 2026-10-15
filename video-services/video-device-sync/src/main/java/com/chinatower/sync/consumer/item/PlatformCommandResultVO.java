package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlatformCommandResultVO implements Serializable {

    /**
     * 流水号 标识原子性 异步调用必传 同步调用设备侧不兼容可在适配器初始化
     */
    private String serialNo;

    /**
     * 信号量(能力值) 单指令返回明确可不传多指令流水号相同时必传
     */
    private String fnId;

    /**
     * 信号量(能力值)值 单指令返回明确可不传多指令流水号相同时必传
     */
    private String fnValue;

    /**
     * 执行结果1成功0失败
     */
    private String result;

    /**
     * 执行产生错误时的信息，正常执行无返回
     */
    private String msg;

    /**
     * 错误码,业务平台需要根据业务码识别处理问题
     */
    private String errorCode;

    /**
     * 指令执行返回的其他信息
     */
    private String info;

    /**
     * 执行完成时间
     */
    private String excuTime;
}
