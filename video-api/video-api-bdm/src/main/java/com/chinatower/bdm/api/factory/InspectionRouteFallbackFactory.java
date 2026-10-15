package com.chinatower.bdm.api.factory;

import com.chinatower.bdm.api.InspectionRouteAPI;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.common.entity.Result;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InspectionRouteFallbackFactory implements FallbackFactory<InspectionRouteAPI> {
    @Override
    public InspectionRouteAPI create(Throwable throwable) {
        return new InspectionRouteAPI() {
            @Override
            public Result<InspectionRouteProcessVO> doProcess(InspectionRouteProcessParam param) {
                return Result.fail("处理巡航路线点位流程失败" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<InspectionRoutePage4SdkVO>> queryInspectionRoutePage4Sdk(InspectionRoutePage4SdkParam param) {
                return Result.fail("巡查线路列表分页查询失败" + throwable.getMessage());
            }

            @Override
            public Result<InspectionRouteProcessVO> doInspectionProgress(InspectionRouteProcessParam param) {
                return Result.fail("巡检进度接口处理失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<InspectionRouteRelLayerSdkVO>> queryInspectionLayerPage(InspectionRouteRelLayerPageSdkParam param) {
                return Result.fail("查询巡检路线点下地块信息失败," + throwable.getMessage());
            }

            @Override
            public Result<String> deletePolygonChecked(String polygonId) {
                return Result.fail("删除电子围栏校验接口处理失败," + throwable.getMessage());
            }

            @Override
            public Result<InspectionNumVO> getInspectionRouteNum(InspectionNumParam param) {
                return Result.fail("查询巡查路线总数接口处理失败," + throwable.getMessage());
            }

            @Override
            public Result<InspectionNumVO> getInspectionPolygonNum(InspectionNumParam param) {
                return Result.fail("查询巡查路线图斑总数接口处理失败," + throwable.getMessage());
            }

            @Override
            public Result<InspectionRouteRecordVO> queryRouteRecordInfo(InspectionRouteRecordParam param) {
                return Result.fail("查询巡查路线记录接口处理失败," + throwable.getMessage());
            }

            @Override
            public Result<VideoInspectionRouteRecordVO> queryVideoRouteRecordInfo(VideoInspectionRouteRecordParam param) {
                return Result.fail("查询视频巡查路线记录接口处理失败," + throwable.getMessage());
            }
        };

    }
}
