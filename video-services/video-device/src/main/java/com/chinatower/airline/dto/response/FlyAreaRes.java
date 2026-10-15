package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/3/11 15:27
 */
@Data
public class FlyAreaRes {
    private String id;
    private String flyName;
    private String flyType;
    //限飞区、作业区
    private String type;
    private String queryName;
    private String geometry;
    private String properties;
    private String createUser;
    private String updateUser;
    private String status;
    private List<List<Double>> shapePoints; // 多边形顶点坐标
    private String wkt;                    // WKT 格式几何图形
    private String deviceCodesStr;
    private List<String> deviceCodes;
    private List<String> deviceNames;
    private List<DeviceAirLineRes> deviceResList;
}
