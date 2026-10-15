package com.chinatower.video.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadProcessDTO implements Serializable {

    /**
     * 批次id
     */
    private String batchId;

    /**
     * 进度百分比：10%，当进度为100%表示下载完成
     */
    private String process;

    /**
     * 文件地址(分片时间30分钟)
     */
    private String downloadUrl;

    /**
     * 文件名称(分片时间30分钟)
     */
    private String fileName;

    /**
     * 文件顺序
     */
    private Integer order;

    /**
     * 1000正常，其他编码为下载异常，错误代码详见6.1.5章节
     */
    private String processCode;

    private Integer fileSize;

    private String deviceCode;

    private String channelCode;

    private String startTime;

    private String endTime;

    private String accessNode;

    private String addNodeUrl;
}
