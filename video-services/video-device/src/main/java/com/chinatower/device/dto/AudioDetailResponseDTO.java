package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author willem
 * @date 2024/10/22
 */
@Data
public class AudioDetailResponseDTO implements Serializable {
    /**
     * 音频描述
     */
    private String audioDesc;
    /**
     * 音频名称
     */
    private String audioName;
    /**
     * 音频id
     */
    private String audioId;
    /**
     * 审核状态
     */
    private String auditStatus;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 审核人
     */
    private String auditUser;
    /**
     *  内容类型 0-音频 1-文本
     */
    private String contentType;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 删除标识
     */
    private String deleted;
    /**
     * 时长
     */
    private Integer duration;
    /**
     * 文件格式
     */
    private String fileFormat;
    /**
     * 文件id
     */
    private String fileId;
    /**
     * 文件状态
     */
    private String fileStatus;
    /**
     * 文件url
     */
    private String fileUrl;
    /**
     * 文件夹ID
     */
    private String folderId;
    /**
     * 文件md5
     */
    private String md5;
    /**
     * 区域编码
     */
    private String regionCode;
    /**
     * 原始文件md5
     */
    private String originFileMd5;
    /**
     * 文件大小
     */
    private Integer size;
    /**
     * 更新时间
     */
    private String updateTime;
}
