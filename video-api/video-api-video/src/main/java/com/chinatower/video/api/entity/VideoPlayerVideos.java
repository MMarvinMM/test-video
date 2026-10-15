package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class VideoPlayerVideos {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 开始时间(格式：yyyy-MM-dd HH:mm:ss）
     */
    private String startTime;
    /**
     * 结束时间(格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;
    /**
     * 录像类型 0-普通录像；1-报警录像
     */
    private int recordType;
    /**
     * 文件唯一编码
     */
    private String fileCode;
    /**
     * 图片缩略图 图片路径url（公网）, storageType=1中心存储方式时必填
     */
    private String fileImageUrl;
}
