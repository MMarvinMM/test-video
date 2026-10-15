package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AirMapFallbackFactory;
import com.chinatower.device.entity.dto.AnnotationMapReq;
import com.chinatower.device.entity.dto.CorrespondPointReq;
import com.chinatower.device.entity.dto.FlyAreaReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 无人机地图相关
 */
@FeignClient(contextId = "airMapService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirMapFallbackFactory.class)
public interface IAirMapService {

    /**
     *  无人机地图标注(客户级别)
     *
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/airMap/addAnnotationMap")
    Result addAnnotationMap(@RequestBody AnnotationMapReq annotationMapReq);

    /**
     * 无人机地图标注查询(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/airMap/queryAnnotationMapList")
    TableDataInfo queryAnnotationMapList(@RequestBody AnnotationMapReq annotationMapReq);

    /**
     * 无人机地图标注删除(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/airMap/delAnnotationMap")
    Result delAnnotationMap(@RequestBody AnnotationMapReq annotationMapReq);

    /**
     * 无人机地图标注默认名称(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/airMap/defaultAnnotationMapName")
    Result defaultAnnotationMapName(@RequestBody AnnotationMapReq annotationMapReq);

    /**
     * 校验无人机地图标注名称(客户级别)
     * @param annotationMapReq
     * @return
     */
    @PostMapping("/airMap/checkAnnotationMapName")
    Result checkAnnotationMapName(@RequestBody AnnotationMapReq annotationMapReq);

    /**
     * 添加飞行区(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/airMap/addFlyArea")
    Result addFlyArea(@RequestBody FlyAreaReq flyAreaReq);

    /**
     * 删除飞行区(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/airMap/delFlyArea")
    Result delFlyArea(@RequestBody FlyAreaReq flyAreaReq);

    /**
     * 飞行区列表(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/airMap/queryFlyAreaList")
    TableDataInfo queryFlyAreaList(@RequestBody FlyAreaReq flyAreaReq);

    /**
     * 飞行区列表名称校验(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/airMap/checkFlyAreaName")
    Result checkFlyAreaName(@RequestBody FlyAreaReq flyAreaReq);

    /**
     * 飞行区编辑(客户级别)
     * @param flyAreaReq
     * @return
     */
    @PostMapping("/airMap/editFlyArea")
    Result editFlyArea(@RequestBody FlyAreaReq flyAreaReq);

    /**
     * 对照点 新增/编辑
     */
    @PostMapping("/airMap/editCorrespondPoint")
    Result editCorrespondPoint(@RequestBody CorrespondPointReq correspondPointReq);

    /**
     * 对照点 删除
     */
    @PostMapping("/airMap/delCorrespondPoint")
    Result delCorrespondPoint(@RequestBody CorrespondPointReq correspondPointReq);

    /**
     * 对照点 列表
     */
    @PostMapping("/airMap/queryCorrespondPointList")
    TableDataInfo queryCorrespondPointList(@RequestBody CorrespondPointReq correspondPointReq);

    /**
     * 对照点唯一名称校验
     */
    @PostMapping("/airMap/checkCorrespondPointName")
    Result checkCorrespondPointName(@RequestBody CorrespondPointReq correspondPointReq);

    /**
     * 查询禁飞区下的航线
     */
    @PostMapping("/airMap/queryAirLineByFlyArea")
    TableDataInfo queryAirLineByFlyArea(@RequestBody FlyAreaReq flyAreaReq);
}
