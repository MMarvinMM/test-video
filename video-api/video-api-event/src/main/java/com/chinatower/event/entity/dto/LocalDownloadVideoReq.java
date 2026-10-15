package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class LocalDownloadVideoReq {

    private String warningOrderId;

    private List<String> videoUrls;

    private String platformCode;

    /**
     * 下载顺序
     */
    private Integer serNumber;
}
