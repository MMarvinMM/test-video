package com.chinatower.video.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *  抓图回调接口入参实体
 */
@Data
public class SnapshotBackVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //错误编码
    private String error_code;
    
    //设备编码
    private String deviceCode;
    
   //通道编码
    private String channelCode;
    
    //图片名称
    private String imageName;

    //图片途径
    private String imagePath;

    //接入节点编码
    private String accessNode;

    //受理节点编码
    private String acceptNode;

    /**
     * 设备厂家
     */
    private String modelCode;

    /**
     * 通道属性
     */
    private Integer attribute;

    private List<SnapshotBackAssistVO> assistPic;
}
