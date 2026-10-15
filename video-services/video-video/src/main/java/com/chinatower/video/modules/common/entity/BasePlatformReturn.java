package com.chinatower.video.modules.common.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName: BasePlatformReturn
 * @Description: 基础平台接口返回对象
 * @Author: zhujun
 * @Date: 2022/1/13
 **/
@Data
@ToString
public class BasePlatformReturn {

    /**
     * 返回编码
     * 请求成功：0000
     * 请求失败：8888
     */
    private String returnCode;

    /**
     * 返回信息
     */
    private String returnMsg;

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 业务信息
     */
    private Object data;
}
