package com.chinatower.gis.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.factory.GisFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "BdmGridAPI",
        value = ServiceNameConstants.GIS_SERVICE,
        fallbackFactory = GisFallbackFactory.class)
public interface GisAPI {

    /**
     * 地块查询其他类型查询
     */
    @PostMapping("/gis/keyWordQuery")
    Result keyWordQuery(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     * 将经纬度坐标转换为详细的结构化地址
     */
    @PostMapping("/gis/codeToAdress")
    Result codeToAdress(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     * 将详细的结构化地址转换为经纬度坐标
     */
    @PostMapping("/gis/adressToCode")
    Result adressToCode(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     * 查询目标区域当前/未来的天气情况
     */
    @PostMapping("/gis/weatherQuery")
    Result  weatherQuery(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);


    /**
     * 行政区域查询
     */
    @PostMapping("/gis/districtQuery")
    Result<List<DistrictsVO>> districtQuery(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);


    /**
     * 将84坐标转换为当前地图可用坐标
     */
    @PostMapping("/gis/coordinateTransformation")
    Result coordinateTransformation(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     * 将坐标信息转化行政区域信息
     */
    @Deprecated
    @PostMapping("/gis/countryAreaQuery")
    Result countryAreaQuery(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);


    /**
     *  获得行政区经纬度
     */
    @PostMapping("/gis/getLatitude")
    Result getLatitude(@RequestBody GisKeyWordQueryReq gisKeyWordQueryReq);


    /**
     * 驾车导航
     */
    @PostMapping("/gis/driveNavigation")
    Result<DriveNavigationRes> driveNavigation(@RequestBody DriveNavigationReq req);


    /**
     * 步行导航
     */
    @PostMapping("/gis/pedestrianNavigation")
    Result<PedestrianNavigationRes> pedestrianNavigation(@RequestBody PedestrianNavigationReq req);

    /**
     * 公交导航
     */
    @PostMapping("/gis/busNavigation")
    Result<BusNavigationRes> busNavigation(@RequestBody BusNavigationReq req);
}
