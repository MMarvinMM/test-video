package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ApiInfoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/1 15:52
 * @Version 1.0
 **/
@Data
public class ApiInfoDTO implements Serializable {
    
    private String serviceName;
    private String inJsonStr;
    private String deviceCode;
    private String channelCode;
    private String orderCode;
    private String tenantId;
    private String userCode;
    private String towerCode;
    private String token;
}
