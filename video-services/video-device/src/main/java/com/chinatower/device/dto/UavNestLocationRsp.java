package com.chinatower.device.dto;

import lombok.Data;

import java.util.List;

/**
 * 无人机平台接口响应实体类
 * 4.1.19.基站经纬度查询接口
 */
@Data
public class UavNestLocationRsp {

    private String deviceCode;

    private String latitude;

    private String longitude;

    private String height;
}
