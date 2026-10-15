package com.chinatower.device.dto;

import lombok.Data;

@Data
public class AlgorithmHornContentVO {

    /**
     * 内容标题
     */
    private String contentTitle;

    /**
     * 文件上传后的url的地址
     */
    private String fileUrl;

    private String fileId;

    /**
     * 文本内容信息
     */
    private String contentValue;

    /**
     * 文件名称
     */
    private String fileName;

}
