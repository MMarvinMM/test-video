package com.chinatower.bdm.api.factory;

import com.chinatower.bdm.api.LayerApi;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
public class LayerFallbackFactory implements FallbackFactory<LayerApi> {
    @Override
    public LayerApi create(Throwable throwable) {
        return new LayerApi() {
            @Override
            public Result<List<TenantLayerListDTO>> getTenantLayerList(LayerQueryDTO dto) {
                return Result.fail("查询图层失败" + throwable.getMessage());
            }


            @Override
            public Result<List<LayerPolygonDTO>> getTenantLayerPolygonList(LayerQueryDTO dto) {
                return Result.fail("查询图层图斑失败" + throwable.getMessage());
            }

            @Override
            public Result<String> addTenantLayerPolygonList(LayerPolygonAddDTO dto) {
                return Result.fail("新增图层图斑失败" + throwable.getMessage());
            }

            @Override
            public Result<List<PolygonAttrDictVO>> getPolygonAttrDict(@RequestBody PolygonAttrDictVO polygonAttrDictVO){
                return Result.fail("全量查询图层中文和拼音对照表失败" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, List<LayerTypeAttrTilterVO>>> getLayerTypeAttrFilter(@RequestBody LayerTypeAttrTilterVO layerTypeAttrTilterVO){
                return Result.fail("工具箱-地块查询失败" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerAreaVO>> getLayerArea(@RequestBody LayerAreaVO layerAreaVO){
                return Result.fail("永久基本农田面积失败" + throwable.getMessage());
            }


            @Override
            public Result<InspectionRouteRelLayerSdkVO> getDeviceByPolygon(@RequestBody InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO){
                return Result.fail("根据图层图斑信息查询设备通道" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerResponseDTO>> slzyLayerList(LayerQueryDTO dto) {
                return Result.fail("查询深林资源图层列表" + throwable.getMessage());
            }


            @Override
            public Result<String> addLayer(LayerAddDTO dto) {
                return Result.fail("上传图层" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerFieldsDTO>> layerFields(String abbreviation) {
                return Result.fail("获得图层字段错误" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerFieldsFilterDTO>> layerFieldsFilters(String abbreviation) {
                return Result.fail("获得图层筛选属性错误" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerFieldsFilterDTO>> advancedQueryLayerFields(String abbreviation) {
                return Result.fail("获得高级查询图层字段错误" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerTypeAttrFilterVO>> dictByAbbreviation(String abbreviation) {
                return Result.fail("根据简称获取所有字典错误" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, LayerResponseDTO>> getNestList(LayerQueryDTO layerQueryDTO) {
                return Result.fail("查询租户行业下的默认套合图层错误" + throwable.getMessage());
            }


            @Override
            public Result<List<LayerAreaCountVO>> layerAreaCount(List<String> layerIds) {
                return Result.fail("获得图层面积图层数量错误" + throwable.getMessage());
            }

            @Override
            public Result<MineralsLayerVO> mineralsLayer() {
                return Result.fail("查询探而未采图层ID" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerResponseDTO>> getNonVectorTenantList(LayerQueryDTO dto) {
                return Result.fail("查询非矢量图层列表" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, List<LayerResponseDTO>>> getTenantListMultiType(LayerQueryDTO dto) {
                return Result.fail("图层列表-查询非矢量图层列表" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerResponseDTO>> selectLayerList(String abbreviation, String layerTypeId) {
                return Result.fail("图层列表查询错误" + throwable.getMessage());
            }

            @Override
            public Result<LayerResponseDTO> selectLayerById(String layerId) {
                return Result.fail("图层详情查询错误" + throwable.getMessage());
            }


            @Override
            public Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(String code, Integer ratioFlag, String abbreviation) {

                return Result.fail(" 租户 + 行业 下 分组面积统计" + throwable.getMessage());
            }

            @Override
            public Result<LayerCountAndAreaDTO> layerCountAndArea(String abbreviation) {
                return Result.fail("租户 + 行业 下 所有面积和小班数量" + throwable.getMessage());
            }

            @Override
            public Result<MineralsLayerVO> computeTewcLayer(MineralsLayerReqVO vo) {
                return Result.fail("租户 + 行业 探而未采图层计算错误" + throwable.getMessage());
            }

            @Override
            public Result<String> deleted(DeleteDTO vo) {
                return Result.fail("删除图层错误" + throwable.getMessage());
            }

            @Override
            public Result<List<LayerResponseDTO>> baseLayerList(BaseLayerListQueryDTO dto) {
                return Result.fail("查询图层列表错误" + throwable.getMessage());
            }
            @Override
            public Result<List<LayerCountRes>> getLayerCount(LayerQueryDTO dto) {
                return Result.fail("统计图层数量错误" + throwable.getMessage());
            }

        };
    }
}
