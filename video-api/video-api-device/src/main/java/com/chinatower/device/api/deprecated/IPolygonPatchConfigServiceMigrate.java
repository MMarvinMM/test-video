package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.factory.PolygonPatchConfigFallbackFactory;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import com.chinatower.device.entity.vo.PolygonPatchVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(contextId = "polygonPatchConfigService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = PolygonPatchConfigFallbackFactory.class)
public interface IPolygonPatchConfigServiceMigrate {


    @PostMapping("/polygonPatch/isExist")
    Result<Integer> isExist(@RequestBody PolygonPatchVo polygonPatchVo);


    @ApiOperation(value = "根据图层id判断是否已存在电子围栏")
    @PostMapping("/polygonPatch/isExistByLayer")
    public Result<Integer> isExistByLayer(@RequestBody PolygonPatchVo polygonPatchVo);

    @ApiOperation(value = "根据设备、通道、预置位查询图斑、电子围栏列表")
    @PostMapping("/polygonPatch/query")
    @Deprecated
    Result<PolygonPatchConfig> queryPolygonPatch(@RequestBody @Valid PolygonPatchVo polygonPatchVo);

    @PostMapping("/polygonPatch/queryList")
    TableDataInfo queryPolygonPatchList(@RequestBody PolygonPatchVo polygonPatchVo);
}
