package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class DownloadVideoReq {

    private String warningOrderId;

    private List<String> videoUrls;

    private String name;
}
