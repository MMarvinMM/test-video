package com.chinatower.device.dto;

import lombok.Data;

import java.util.List;

/**
 * 无人机平台接口请求实体类
 * 4.1.19.基站经纬度查询接口
 */
@Data
public class UavNestLocationReq {

    private List<String> deviceCodeList;

}
