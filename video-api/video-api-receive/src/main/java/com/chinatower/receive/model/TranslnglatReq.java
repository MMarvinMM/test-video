package com.chinatower.receive.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: songxinfeng
 * @Date: 2024/12/9 16:35
 */
@Data
public class TranslnglatReq implements Serializable {

    /** 摄像机P */
    private String pan;
    /** 摄像机T */
    private String tilt;
    /** 摄像机Z */
    private String zoom;
    /**
     * 摄像机编码
     */
    private String deviceCode;
}
