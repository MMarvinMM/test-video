package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class CommonResultEntiry {
    private String returnCode;

    /**
     * Y 返回信息.  ->请求成功：0000 /请求失败：8888.
     */
    private String returnMsg;

    /**
     * 错误编码. retureCode为8888时必须写明错误代码.
     */

    private String errorCode;

    /**
     * 错误消息->retureCode为8888时必须写明错误代码.
     */
    private String errorMsg;
}
