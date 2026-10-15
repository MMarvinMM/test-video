package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class AuditionResultVO implements Serializable {

    private String returnCode;

    private String returnMsg;

    private String errorCode;

    private String errorMsg;

    private AuditionV2VO data;
}
