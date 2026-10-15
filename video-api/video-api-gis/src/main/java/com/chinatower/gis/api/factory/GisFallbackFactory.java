package com.chinatower.gis.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisAPI;
import com.chinatower.gis.api.entity.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class GisFallbackFactory implements FallbackFactory<GisAPI> {
    private static final Logger log = LoggerFactory.getLogger(GisFallbackFactory.class);

    @Override
    public GisAPI create(Throwable throwable) {
        log.error("调用三种地图接口失败:{}", throwable.getMessage());
        return new GisAPI() {
            @Override
            public Result keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用关键字搜索方法失败:" + throwable.getMessage());
            }

            @Override
            public Result codeToAdress(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用将详细的结构化地址转换为经纬度坐标方法失败:" + throwable.getMessage());
            }

            @Override
            public Result adressToCode(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用将详细的结构化地址转换为经纬度坐标方法失败:" + throwable.getMessage());
            }

            @Override
            public Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用查询目标区域当前/未来的天气情况方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DistrictsVO>> districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用行政区域查询方法失败:" + throwable.getMessage());
            }

            @Override
            public Result coordinateTransformation(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用将84坐标转换为当前地图可用坐标方法失败:" + throwable.getMessage());
            }

            @Override
            public Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用将坐标信息转化行政区域信息方法失败:" + throwable.getMessage());
            }

            @Override
            public Result getLatitude(GisKeyWordQueryReq gisKeyWordQueryReq) {
                return Result.fail("调用获取行政区经纬度方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<DriveNavigationRes> driveNavigation(DriveNavigationReq req) {
                return Result.fail("调用驾驶导航方法失败:" + throwable.getMessage());
            }

            @Override
            public Result pedestrianNavigation(PedestrianNavigationReq req) {
                return Result.fail("调用步行导航方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<BusNavigationRes> busNavigation(BusNavigationReq req) {
                return Result.fail("调用公交导航方法失败:" + throwable.getMessage());
            }
        };
    }
}
