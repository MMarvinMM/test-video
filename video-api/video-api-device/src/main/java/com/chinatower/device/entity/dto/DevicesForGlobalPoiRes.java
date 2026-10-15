package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevicesForGlobalPoiRes{
    /**
     * 搜索类型
     */
    private String searchType;

    /**
     * 设备编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 唯一标识
     */
    private String uuid;


    /**
     * 是否清空 0：否  1：是
     */
    private String isClear;
}
