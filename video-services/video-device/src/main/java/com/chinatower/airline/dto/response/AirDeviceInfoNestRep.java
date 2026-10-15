package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoNestRep {

    /**
     * UUID
     * */
    private String uuid;
    /**
     * 基站编码
     * */
    private String number;
    /**
     * 基站类型
     * */
    private String type;

    /**
     * 基站类型名称
     * */
    private String typeName;
}
