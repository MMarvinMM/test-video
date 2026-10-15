package com.chinatower.device.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.service.IPolygonPatchService;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import com.chinatower.platform.annotation.DataPermissions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * video-device
 * @author yy
 */
@RestController
@Api(value = "配置管理",tags = "图斑标绘")
@Slf4j
public class PolygonPatchConfigController extends TowerBaseController {

    @Resource
    IPolygonPatchService polygonPatchService;


    /**
     * 查询所有图斑、电子围栏列表
     * 根据设备、通道、预置位查询所有图斑、电子围栏列表
     *
     * @param polygonPatchVo polygonPatchVo
     * @return TableDataInfo
     * @author yiyao
     * @date 2023/04/06
     * @version release_v1.5.80
     */
    @ApiOperation(value = "根据设备、通道、预置位查询所有图斑、电子围栏列表")
    @PostMapping("/polygonPatch/queryList")
    @DataPermissions(layerColumn = "t2.id")
    public TableDataInfo queryPolygonPatchList(@RequestBody PolygonPatchVo polygonPatchVo) {
        log.info("查询图斑绑定的电子围栏列表,{}", JSON.toJSONString(polygonPatchVo));
        List<PolygonPatchConfig> list = polygonPatchService.queryPolygonPatchList(polygonPatchVo);
        return getDataTable(list);
    }


    /**
     * 查询图斑电子围栏 (提供给微创api)
     * 根据设备、通道、预置位查询图斑电子围栏 (提供给微创api)
     * @param polygonPatchVo polygonPatchVo
     * @return Result
     * @author yiyao
     * @date 2023/05/10
     * @version release_v1.5.85
     */
    @ApiOperation(value = "根据设备、通道、预置位查询图斑、电子围栏列表")
    @PostMapping("/polygonPatch/query")
    @Deprecated
    @DataPermissions(layerColumn = "t2.id")
    public Result<PolygonPatchConfig> queryPolygonPatch(@RequestBody @Valid PolygonPatchVo polygonPatchVo) {
        log.info("废弃接口调用");
        log.info("查询图斑绑定的电子围栏列表,{}", JSON.toJSONString(polygonPatchVo));
        return Result.data(polygonPatchService.queryPolygonPatch(polygonPatchVo));
    }


    /**
     * 新增图斑对应的电子围栏
     * 根据设备、通道、预置位新增图斑对应的电子围栏
     * @param polygonPatchList polygonPatchList
     * @return Result
     * @author yiyao
     * @date 2023/04/06
     * @version release_v1.5.80
     */
    @ApiOperation(value = "根据设备、通道、预置位新增图斑对应的电子围栏")
    @PostMapping("/polygonPatch/batch")
    public Result<String> updatePolygonPatch(@RequestBody List<PolygonPatchConfig> polygonPatchList) {
        polygonPatchService.addOrUpdatePolygonPatch(polygonPatchList, false);
        return Result.success("图斑标绘成功");
    }


    /**
     * 删除图斑对应的电子围栏
     * 根据设备、通道、预置位删除图斑对应的电子围栏
     * @param polygonPatchVo polygonPatchVo
     * @return DeviceByDistanceDTO
     * @author yiyao
     * @date 2023/04/06
     * @version release_v1.5.80
     */
    @ApiOperation(value = "根据设备编码，获取附近指定范围的所有设备")
    @PostMapping("/polygonPatch/delete")
    public Result<String> deletePolygonPatch(@RequestBody PolygonPatchVo polygonPatchVo) {
        polygonPatchService.deletePolygonPatch(polygonPatchVo);
        return Result.success("删除图斑标绘成功");
    }

    /**
     * 图层id图斑id判断电子围栏
     * 根据图层id图斑id判断是否已存在电子围栏
     * @param polygonPatchVo polygonPatchVo
     * @return DeviceByDistanceDTO
     * @author yiyao
     * @date 2023/04/06
     * @version release_v1.5.80
     */
    @ApiOperation(value = "根据图层id图斑id判断是否已存在电子围栏")
    @PostMapping("/polygonPatch/isExist")
    public Result<Integer> isExist(@RequestBody PolygonPatchVo polygonPatchVo) {
        return Result.data(polygonPatchService.isExistByPatchId(polygonPatchVo));
    }


    /**
     * 图层id判断电子围栏
     * 根据图层id判断是否已存在电子围栏
     * @param polygonPatchVo
     * @return
     */
    @ApiOperation(value = "根据图层id判断是否已存在电子围栏")
    @PostMapping("/polygonPatch/isExistByLayer")
    public Result<Integer> isExistByLayer(@RequestBody PolygonPatchVo polygonPatchVo) {
        return Result.data(polygonPatchService.isExistByLayer(polygonPatchVo));
    }


    /**
     * 查询图层下是否有绑定的图斑信息
     * 提供给华苏使用询图层下是否有绑定的图斑信息
     * @param polygonPatchVo polygonPatchVo
     * @return DeviceByDistanceDTO
     * @author yiyao
     * @date 2023/04/10
     * @version release_v1.5.80
     */
    @ApiOperation(value = "图层下是否有图斑被绑定")
    @PostMapping("/polygonPatch/queryByLayerId")
    public Result<Integer> queryPolygonByLayerId(@RequestBody PolygonPatchVo polygonPatchVo) {
        return Result.data(polygonPatchService.queryPolygonByLayerId(polygonPatchVo));

    }

    /**
     * 图层图斑id获取对应的电子围栏和设备信息
     * @param polygonPatchVo polygonPatchVo
     * @return DeviceByDistanceDTO
     * @author yiyao
     * @date 2023/04/10
     * @version release_v1.5.80
     */
    @ApiOperation(value = "根据图层图斑id获取对应的电子围栏和设备信息")
    @PostMapping("/polygonPatch/queryByLayerIdAndFeatureId")
    @Deprecated
    public Result<PolygonPatchConfig> queryByLayerIdAndFeatureId(@RequestBody PolygonPatchVo polygonPatchVo) {
        log.info("废弃接口调用");
        PolygonPatchConfig polygonPatchConfig = polygonPatchService.queryByLayerIdAndFeatureId(polygonPatchVo);
        return Result.data(polygonPatchConfig);

    }

    /**
     * 查询摄像机预置位在图斑标绘中绘制的电子围栏。
     * 根据“地图-图层列表”选择的图层，查询该客户该行业应用该摄像机该通道该预置位在“配置管理-图斑标绘”中绘制的电子围栏。
     * @param polygonPatchVo polygonPatchVo
     * @return Result<List<PolygonPatchConfig>>
     * @author wanglei
     * @date 2023/08/14
     * @version release_v1.6.10
     */
    @ApiOperation(value = "根据“地图-图层列表”选择的图层，查询该客户该行业应用该摄像机该通道该预置位在“配置管理-图斑标绘”中绘制的电子围栏。")
    @PostMapping("/polygonPatch/queryPolygonPatchShow")
    @DataPermissions(layerColumn = "t2.id")
    public Result<List<PolygonPatchConfig>> queryPolygonPatchShow(@RequestBody PolygonPatchVo polygonPatchVo) {
        log.info("查询该客户该行业应用该摄像机该通道该预置位该图层的电子围栏,{}", JSON.toJSONString(polygonPatchVo));
        List<PolygonPatchConfig> list = polygonPatchService.queryPolygonPatchShow(polygonPatchVo);
        return Result.data(list);
    }


    /**
     * 批量查询摄像机预置位在图斑标绘中绘制的电子围栏。
     * 根据“地图-图层列表”选择的图层，查询该客户该行业应用该摄像机该通道该预置位在“配置管理-图斑标绘”中绘制的电子围栏。
     * @param polygonPatchVoList polygonPatchVo
     * @return Result<List<PolygonPatchConfig>>
     * @author liuxt
     * @date 2025/03/20
     * @version release_v1.7.30
     */
    @ApiOperation(value = "根据“地图-图层列表”选择的图层，批量查询该客户该行业应用该摄像机该通道该预置位在“配置管理-图斑标绘”中绘制的电子围栏。")
    @PostMapping("/polygonPatch/queryPolygonPatchShowBatch")
    @DataPermissions(layerColumn = "t2.id")
    public Result<List<PolygonPatchConfig>> queryPolygonPatchShowBatch(@RequestBody PolygonPatchVo polygonPatchVo) {
        log.info("查询该客户该行业应用该摄像机该通道该预置位该图层的电子围栏,{}", JSON.toJSONString(polygonPatchVo));
        List<PolygonPatchConfig> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(polygonPatchVo.getPolygonPatchs())){
            list = polygonPatchService.queryPolygonPatchShowBatch(polygonPatchVo);
        }
        return Result.data(list);
    }

    /**
     * 获取图斑标会最大编号
     * @param polygonPatchVo
     * @return
     */
    @PostMapping("/polygonPatch/queryMaxNumber")
    @DataPermissions(layerColumn = "t2.id")
    public Result<Integer> queryMaxNumber(@RequestBody PolygonPatchVo polygonPatchVo) {
        return Result.data(polygonPatchService.queryMaxNumber(polygonPatchVo));
    }

    /**
     * 清洗图斑标绘数据，将百分比坐标转为经纬度，存到新字段
     * @return
     */
    @PostMapping("/polygonPatch/initPolygonLonLat")
    public Result<String> initPolygonLonLat() {
        return Result.data(polygonPatchService.initPolygonLonLat());
    }
}
