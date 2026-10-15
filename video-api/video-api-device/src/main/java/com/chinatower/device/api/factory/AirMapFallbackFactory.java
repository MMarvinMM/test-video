package com.chinatower.device.api.factory;

import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IAirMapService;
import com.chinatower.device.entity.dto.AnnotationMapReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.device.entity.dto.FlyAreaReq;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * 降级处理
 */
@Component
public class AirMapFallbackFactory implements FallbackFactory<IAirMapService> {

    @Override
    public IAirMapService create(Throwable throwable) {
        return new IAirMapService() {
            @Override
            public Result addAnnotationMap(AnnotationMapReq annotationMapReq) {
                return Result.fail("无人机地图标注失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryAnnotationMapList(AnnotationMapReq annotationMapReq) {
                TableDataInfo failResult = new TableDataInfo();
                failResult.setCode(ResultCode.FAILURE.getCode());
                return failResult;
            }

            @Override
            public Result delAnnotationMap(AnnotationMapReq annotationMapReq) {
                return Result.fail("无人机地图标注删除失败:" + throwable.getMessage());
            }

            @Override
            public Result defaultAnnotationMapName(AnnotationMapReq annotationMapReq) {
                return Result.fail("获取无人机地图标注默认名称失败:" + throwable.getMessage());
            }

            @Override
            public Result checkAnnotationMapName(AnnotationMapReq annotationMapReq) {
                return Result.fail("校验无人机地图标注名称失败:" + throwable.getMessage());
            }

            @Override
            public Result addFlyArea(FlyAreaReq flyAreaReq) {
                return Result.fail("添加飞行区失败:" + throwable.getMessage());
            }

            @Override
            public Result delFlyArea(FlyAreaReq flyAreaReq) {
                return Result.fail("删除飞行区失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryFlyAreaList(FlyAreaReq flyAreaReq) {
                TableDataInfo failResult = new TableDataInfo();
                failResult.setCode(ResultCode.FAILURE.getCode());
                return failResult;
            }

            @Override
            public Result checkFlyAreaName(FlyAreaReq flyAreaReq) {
                return Result.fail("飞行区列表名称校验失败:" + throwable.getMessage());
            }

            @Override
            public Result editFlyArea(FlyAreaReq flyAreaReq) {
                return Result.fail("飞行区编辑失败:" + throwable.getMessage());
            }

            @Override
            public Result editCorrespondPoint(CorrespondPointReq correspondPointReq) {
                return Result.fail("对照点新增/编辑失败:" + throwable.getMessage());
            }

            @Override
            public Result delCorrespondPoint(CorrespondPointReq correspondPointReq) {
                return Result.fail("对照点删除失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryCorrespondPointList(CorrespondPointReq correspondPointReq) {
                TableDataInfo failResult = new TableDataInfo();
                failResult.setCode(ResultCode.FAILURE.getCode());
                return failResult;
            }

            @Override
            public Result checkCorrespondPointName(CorrespondPointReq correspondPointReq) {
                return Result.fail("对照点唯一名称校验:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryAirLineByFlyArea(FlyAreaReq flyAreaReq) {
                TableDataInfo failResult = new TableDataInfo();
                failResult.setCode(ResultCode.FAILURE.getCode());
                return failResult;
            }
        };
    }
}
