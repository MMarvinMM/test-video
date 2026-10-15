package com.chinatower.gis.modules.gis.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.map.GisPoiKeyWordQueryReq;
import com.chinatower.gis.api.entity.map.PoiKeyWordQueryRes;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.gis.modules.gis.service.GeoserverService;
import com.chinatower.platform.enums.GisMapServiceEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 读取geoserver 数据
 * @data 创建时间：2023/9/27 10:54
 */
@Api(value = "geoserver", tags = "geoserver接口")
@Slf4j
@RestController
@RequestMapping("/geo")
public class GeoserverController {

    @Resource
    GeoserverService geoserverService;

    /**
     * 读取切片文件流
     *
     * @param x
     * @param y
     * @param z
     */
    @ApiOperation(value = "读取切片数据")
    @GetMapping("/terrain")
    public void terrain(String x, String y, String z) {
        geoserverService.terrain(x, y, z);
    }
}
