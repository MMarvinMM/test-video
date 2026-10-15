package com.chinatower.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desription: gis 服务实现类
 * @data 创建时间：2023/8/15 13:46
 * @author chuan hao
 * @version 1.0
 *
 */

@Getter
@AllArgsConstructor
public enum GisMapServiceEnums {

    MAP_WORLD("01","mapWorldServiceImpl","天地图"),
    GD_WORLD("02","gdWorldServiceImpl","高德"),
    OFFLINE_WORLD("03","offlineWorldServiceImpl","高德离线"),
    OFFLINE_ENCRYPT_WORLD("04","offlineEncryptWorldServiceImpl","高德离线加密");

    /**
     * 类型
     */
    private String mapType;
    /**
     * 实现类名称
     */
    private String serviceName;
    /**
     * 备注
     */
    private String remark;

    public static GisMapServiceEnums get(String mapType){
        for (GisMapServiceEnums enums : GisMapServiceEnums.values()){
            if (enums.getMapType().equals(mapType)){
                return enums;
            }
        }
        return MAP_WORLD;
    }
}
