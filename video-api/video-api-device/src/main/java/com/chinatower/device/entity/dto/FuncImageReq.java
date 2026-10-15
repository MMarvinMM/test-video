package com.chinatower.device.entity.dto;

import lombok.Data;


/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class FuncImageReq implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /** 图片URL */
    private String imageUrl;
 }
