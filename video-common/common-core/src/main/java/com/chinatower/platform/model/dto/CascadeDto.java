package com.chinatower.platform.model.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class CascadeDto {
    //来源属地编码
    private String fromPlatformCode;
    //目的地属地编码
    private String toPlatformCode;
    //属地应用，枚举值
    //通用，general
    //算法仓：algorithm
    //基础平台：base
    //AIOT：aiot
    //雷达平台：radar
    private String appId;
    //属地请求接口Path
    private String path;
    //请求类型
    private String method;

    private String body;

    //级联网关接口名
    private String uri;
}
