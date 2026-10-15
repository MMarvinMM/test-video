package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceAttachmentVO implements Serializable {

    /**
     * 视频文件地址列表
     */
    private List<String> mediaFileUrlList;

    /**
     * 图片文件地址列表
     */
    private List<String> imgFileUrlList;
}
