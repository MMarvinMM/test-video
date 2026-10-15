package com.chinatower.device.entity.vo;

import lombok.Data;

/**
 * AIot能力平台通用响应对象
 */
@Data
public class AIotPlatformDataRsp {

    /**
     * 流水号
     */
    private String serialNo;

    /**
     * 信号量
     */
    private String fnId;

    /**
     * 信号量(能力值)值
     */
    private Object fnValue;

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
     * 执行完成时间
     */
    private String excuTime;

    /**
     * 执行结果之外返回的结果信息 比如执行完产生的记录信息
     */
    private String info;
}
