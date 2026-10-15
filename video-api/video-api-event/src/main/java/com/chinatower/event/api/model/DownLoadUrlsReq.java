package com.chinatower.event.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/6 14:14
 */
@Data
public class DownLoadUrlsReq {
    private List<String> photoUrls;
    private List<String> videoUrls;
    private String isZip;
}
