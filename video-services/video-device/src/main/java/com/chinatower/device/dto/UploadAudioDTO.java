package com.chinatower.device.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author willem
 * @date 2024/10/22
 * @description
 */
@Data
public class UploadAudioDTO {
    /**
     * 音频场景  固定值:Broadcast
     */
    private String audioScene = "Broadcast";
    /**
     * 音频文件
     */
    private MultipartFile file;
    /**
     * 接入接点
     */
    private String accessNode;
    /**
     * 受理接点
     */
    private String acceptNode;
    /**
     * 音频描述
     */
    private String desc;
    /**
     * 区域编码
     */
    private String regionCode;
}
