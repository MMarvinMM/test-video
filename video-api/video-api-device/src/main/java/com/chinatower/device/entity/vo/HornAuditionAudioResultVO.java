package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class HornAuditionAudioResultVO implements Serializable {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件URL
     */
    private String fileUrl;
    /**
     * 文件时长
     */
    private String fileDuration;
}
