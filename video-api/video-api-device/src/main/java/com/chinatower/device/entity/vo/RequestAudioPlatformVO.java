package com.chinatower.device.entity.vo;

import lombok.Data;

/**
 * @author willem
 * @date 2024/10/22
 * @description
 */
@Data
public class RequestAudioPlatformVO<T> {
    private String returnCode;
    private String returnMsg;
    private String errorCode;
    private String errorMsg;
    private T data;
}
