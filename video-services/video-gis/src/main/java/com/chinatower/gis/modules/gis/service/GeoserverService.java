package com.chinatower.gis.modules.gis.service;

public interface GeoserverService {

    /**
     * 读取切片文件流
     * @param x
     * @param y
     * @param z
     */
    void terrain(String x,String y,String z);
}
