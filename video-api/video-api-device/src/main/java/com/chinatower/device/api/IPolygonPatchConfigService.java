package com.chinatower.device.api;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IPolygonPatchConfigServiceMigrate;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.validation.Valid;

@Component
public class IPolygonPatchConfigService {

    @Resource
    IPolygonPatchConfigServiceMigrate iPolygonPatchConfigServiceMigrate;

    /**
     * 根据图斑id判断是否已存在电子围栏
     * @apiNote 根据图斑id判断是否已存在电子围栏
     * @apiVersion 3.50
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "layerId": "234235"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * data 存在电子围栏数量
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1681094780240,
     *     "data": 3
     * }
     * @param polygonPatchVo polygonPatchVo
     * @return DeviceByDistanceDTO
     */
    @PostMapping("/polygonPatch/isExist")
    public Result<Integer> isExist(@RequestBody PolygonPatchVo polygonPatchVo){
        return iPolygonPatchConfigServiceMigrate.isExist(polygonPatchVo);
    }


    @ApiOperation(value = "根据图层id判断是否已存在电子围栏")
    @PostMapping("/polygonPatch/isExistByLayer")
    public Result<Integer> isExistByLayer(@RequestBody PolygonPatchVo polygonPatchVo) {
        return iPolygonPatchConfigServiceMigrate.isExistByLayer(polygonPatchVo);
    }

    /**
     * 根据设备、通道、预置位查询图斑电子围栏 (提供给微创api)
     * @apiNote 根据设备、通道、预置位查询图斑电子围栏 (提供给微创api)
     * @apiVersion 3.51
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode": "11010800831327000004",
     *     "channelCode": "11010800001310000047",
     *     "presetId": 53528,
     *     "monitorAreaId": "1681726130217999445971652",
     *     "industryCode": "000000",
     *     "tenantId": "122035807"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *  polygonId 电子围栏id
     *  monitorAreaId 监控区域id
     *  polygonPoints 电子围栏点位集合
     *  deviceCode 设备编码
     *  channelCode 通道编码
     *  presetId 预置位id
     *  presetName 预置位名称
     *  layerId 图层id
     *  layerName 图层名称
     *  featureId 图斑id
     *  createBy 创建人
     *  createTime 创建时间
     *  updateBy 更新人
     *  updateTime 更新时间
     *  configId 绑定
     *  polygonName 电子围栏名称
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1683706994678,
     *     "data": {
     *         "polygonId": "ccd821b55f474ea2b9232e6af798b07d",
     *         "monitorAreaId": "1681726130217999445971652",
     *         "polygonPoints": "[{\"monitorPointId\":\"1\",\"percentX\":0.796,\"percentY\":0.127},{\"monitorPointId\":\"2\",\"percentX\":0.908,\"percentY\":0.243},{\"monitorPointId\":\"3\",\"percentX\":0.889,\"percentY\":0.041},{\"monitorPointId\":\"4\",\"percentX\":0.777,\"percentY\":0.084},{\"monitorPointId\":\"5\",\"percentX\":0.797,\"percentY\":0.125}]",
     *         "deviceCode": "11010800831327000004",
     *         "channelCode": "11010800001310000047",
     *         "presetId": "53528",
     *         "presetName": "预置位5",
     *         "layerId": "fa412ffee376671bd1b4c2ac26d31338",
     *         "layerName": "测试0112",
     *         "featureId": "lhx.13",
     *         "createBy": "junnan01",
     *         "createTime": "2023-04-17 18:08:58",
     *         "updateBy": null,
     *         "updateTime": null,
     *         "polygonName": "电子围栏2"
     *     }
     * }
     */
    @ApiOperation(value = "根据设备、通道、预置位查询图斑、电子围栏列表")
    @PostMapping("/polygonPatch/query")
    public Result<PolygonPatchConfig> queryPolygonPatch(@RequestBody @Valid PolygonPatchVo polygonPatchVo) {
        //参数pagingSwitch，传入"pagingSwitch":"0"
        //新增queryType，传入 "queryType":1
        polygonPatchVo.setPagingSwitch("0");
        polygonPatchVo.setQueryType(1);
        //目标接口
        TableDataInfo tableDataInfo = iPolygonPatchConfigServiceMigrate.queryPolygonPatchList(polygonPatchVo);
        ObjectMapper mapper = new ObjectMapper();
        PolygonPatchConfig polygonPatchConfig = mapper.convertValue(tableDataInfo.getRows().get(0), PolygonPatchConfig.class);
        return Result.data(polygonPatchConfig);
    }

}
