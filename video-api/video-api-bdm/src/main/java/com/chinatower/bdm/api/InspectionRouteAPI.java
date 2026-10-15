package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.api.factory.InspectionRouteFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "InspectionRouteAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = InspectionRouteFallbackFactory.class)
public interface InspectionRouteAPI {

    @PostMapping("/inspection/doProcess")
    Result<InspectionRouteProcessVO> doProcess(@RequestBody InspectionRouteProcessParam param);

    @PostMapping("/inspection/queryInspectionRoutePage4Sdk")
    Result<PageInfo<InspectionRoutePage4SdkVO>> queryInspectionRoutePage4Sdk(@RequestBody InspectionRoutePage4SdkParam param);

    @PostMapping("/inspection/doInspectionProgress")
    Result<InspectionRouteProcessVO> doInspectionProgress(@RequestBody InspectionRouteProcessParam param);

    @PostMapping("/inspection/queryInspectionLayerPage")
    Result<PageInfo<InspectionRouteRelLayerSdkVO>> queryInspectionLayerPage(@RequestBody InspectionRouteRelLayerPageSdkParam param);

    @GetMapping("/inspection/deletePolygonChecked")
    Result<String> deletePolygonChecked(@RequestParam String polygonId);

    /**
     * 查询用户范围内的巡查路线总数
     * @apiNote  查询用户范围内的巡查路线总数
     * @apiVersion 13.24
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *    "createTimeBegin": "2024-05-20 12:00:00",
     *    "createTimeEnd": "2024-05-21 12:00:00"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     allNum：巡查线路总数
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1707200971129,
     *     "data": {"allNum":42}
     * }
     */
    @PostMapping("/inspection/getInspectionRouteNum")
    Result<InspectionNumVO> getInspectionRouteNum(@RequestBody InspectionNumParam param);

    /**
     * 查询用户范围内巡查路线图斑的总数
     * @apiNote  查询用户范围内巡查路线图斑的总数
     * @apiVersion 13.25
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *    "createTimeBegin": "2024-05-20 12:00:00",
     *    "createTimeEnd": "2024-05-21 12:00:00"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     allNum：路线图斑总数
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1707200971129,
     *     "data": {"allNum":64}
     * }
     */
    @PostMapping("/inspection/getInspectionPolygonNum")
    Result<InspectionNumVO> getInspectionPolygonNum(@RequestBody InspectionNumParam param);

    /**
     * 查询地块巡检线路记录信息
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/inspection/queryRouteRecordInfo")
    Result<InspectionRouteRecordVO> queryRouteRecordInfo(@RequestBody InspectionRouteRecordParam param);

    /**
     * 查询视频巡检路线信息
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/video/inspection/queryRouteRecordInfo")
    Result<VideoInspectionRouteRecordVO> queryVideoRouteRecordInfo(@RequestBody VideoInspectionRouteRecordParam param);
}
