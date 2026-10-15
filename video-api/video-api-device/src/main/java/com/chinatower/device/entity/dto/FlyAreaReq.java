package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author WLF
 * @description
 * @date 2025/3/11 14:57
 */
@Data
public class FlyAreaReq extends PageDTO {
    private String id;
    private String flyName;
    //限飞区、作业区
    private String type;
    private String flyType;
    private String queryName;
    private String geometry;
    private String properties;
    private String status;
    private String createUser;
    private String updateUser;
    private String flightRouteId;
    private List<String> deviceCodes;
    private String deviceCodesStr;
    private List<String> flightRouteIds;
    private Set<String> allDeviceCodes;
}
