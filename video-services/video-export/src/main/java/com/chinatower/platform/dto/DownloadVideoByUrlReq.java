package com.chinatower.platform.dto;

import lombok.Data;

/**
 * 视频URL下载加水印并上传入参
 */
@Data
public class DownloadVideoByUrlReq {
    /**
     * 视频直链（可能携带 query 参数）
     */
    private String url;

    /**
     * 是否为H265编码：1/true 表示是，其它表示否（可选）
     */
    private String isH265;
}

