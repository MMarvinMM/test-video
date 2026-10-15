package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 广播子平台
 * 4.10.上传音频响应参数实体类
 */
@Data
public class HornAudioUploadRspVO implements Serializable {

    // 区域编码
    private String regionCode;

    // 音频ID
    private String audioId;

    // 音频名称
    private String audioName;

    // 音频描述
    private String audioDesc;

    // 文件状态 UPLOADING,SUCCEED,FAILED
    private String fileStatus;

    // 审核状态 AUDITING,PASS,REJECT
    private String auditStatus;

    // 文件MD5值
    private String md5;
}
