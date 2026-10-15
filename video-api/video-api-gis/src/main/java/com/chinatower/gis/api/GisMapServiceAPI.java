package com.chinatower.gis.api;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.*;

public interface GisMapServiceAPI {

    /**
     *
     * @return 关键字搜索查询返回
     */
    Result keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq);
    /**
     *
     * @return 经纬度搜索返回地址
     */
    Result codeToAdress(GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     *
     * @return 地址转经纬度返回
     */
    Result adressToCode(GisKeyWordQueryReq gisKeyWordQueryReq);
    /**
     *
     * @return 天气查询返回
     */
    Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq);
    /**
     *
     * @return 区域编码查询返回
     */
    Result districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq);
    /**
     *
     * @return 84转高德坐标
     */
    Result coordinateTransformation(GisKeyWordQueryReq gisKeyWordQueryReq);

    /**查询城市区域编码
     */
    @Deprecated
    Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq);

    /**
     *
     * @return 驾驶导航
     */
    DriveNavigationRes driveNavigation(DriveNavigationReq req);

    /**
     *
     * @return 步行导航
     */
    PedestrianNavigationRes pedestrianNavigation(PedestrianNavigationReq req);

    /**
     *
     * @return 公交
     */
    BusNavigationRes busNavigation(BusNavigationReq req);
}
