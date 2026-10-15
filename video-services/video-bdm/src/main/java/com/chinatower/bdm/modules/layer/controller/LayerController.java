package com.chinatower.bdm.modules.layer.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.layer.domain.layerTree;
import com.chinatower.bdm.modules.layer.service.ILayerService;
import com.chinatower.bdm.modules.layer.service.LayerPgService;
import com.chinatower.common.core.comparator.PinYinComparator;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.enums.PGLayerTempEnums;
import com.chinatower.platform.utils.BigDecimalUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * video-bdm
 * <p>
 * 图层管理 前端控制器
 * </p>
 *
 * @author ch
 * @since 2023-04-07
 */
@Api(tags = "图层管理")
@Slf4j
@RestController
@RequestMapping("/layer")
@RequiredArgsConstructor
public class LayerController extends TowerBaseController {


    private final ILayerService layerService;
    private final LayerPgService layerPgService;

    /**
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图层列表", notes = "图层列表")
    @PostMapping("/list")
    @DataPermissions(layerColumn = "l.id")
    public TableDataInfo list(@RequestBody LayerQueryDTO dto) {
        return getDataTable(layerService.list(dto));
    }


    /**
     *  图层树查询
     */
    @PostMapping("/layerTree")
    @DataPermissions(layerColumn = "l.id")
    public Result<List<layerTree>> layerTree(LayerQueryDTO dto) {
        return layerService.layerTree(dto);
    }

    /**
     * @return 深林资源图层列表
     */
    @ApiOperation(value = "深林资源图层列表", notes = "深林资源图层列表")
    @GetMapping("/slzyLayerList")
    @DataPermissions(layerColumn = "l.id")
    public Result<List<LayerResponseDTO>> slzyLayerList(LayerQueryDTO dto) {
        dto.setAbbreviation(PGLayerTempEnums.SLZY.getLayerType());
        List<LayerResponseDTO> list = layerService.list(dto);
        double m = 0.0015D;
        list.forEach(res -> {
            //转化为亩
            double mj = BigDecimalUtils.multiply(res.getLayerArea(), m);
            double round = BigDecimalUtils.round(mj, 1, 2);
            res.setLayerArea(round);
        });
        return Result.data(list);
    }


    /**
     * @return SDK 图层列表
     */
    @ApiOperation(value = "SDK 图层列表", notes = "图层列表")
    @PostMapping("/baseLayerList")
    @DataPermissions(layerColumn = "l.id")
    public Result<List<LayerResponseDTO>> baseLayerList(@RequestBody BaseLayerListQueryDTO dto) {
        List<LayerResponseDTO> list = layerService.baseLayerList(dto);
        return Result.data(list);
    }


    /**
     * @return 查询某一个类别的面积和图斑数量数据
     */
    @ApiOperation(value = "深林资源图层列表", notes = "深林资源图层列表")
    @GetMapping("/layerCountAndArea")
    public Result<LayerCountAndAreaDTO> layerCountAndArea(@RequestParam("abbreviation") String abbreviation) {
        return layerService.layerCountAndArea(abbreviation);
    }

    /**
     * @return 查询某一个类别的面积和图斑数量数据
     */
    @ApiOperation(value = "图层类别所有数据分类统计", notes = "深林资源图层列表")
    @GetMapping("/layerClassifyAreaStatistics")
    public Result<List<LayerSlzyStatisticsDTO>> layerClassifyAreaStatistics(@RequestParam("code") String code, @RequestParam(value = "ratioFlag", required = false) Integer ratioFlag, @RequestParam("abbreviation") String abbreviation) {
        log.info("图层类别所有数据分类统计 {} {} {}", code, abbreviation, ratioFlag);
        return layerService.layerClassifyAreaStatistics(code, ratioFlag, abbreviation);
    }

    @ApiOperation(value = "SDK类别查询", notes = "SDK类别查询")
    @GetMapping("/selectLayerList")
    public Result<List<LayerResponseDTO>> selectLayerList(@RequestParam(value = "abbreviation",required = false) String abbreviation, @RequestParam(value = "layerTypeId",required = false) String layerTypeId) {
        if (StringUtils.isEmpty(abbreviation) && StringUtils.isEmpty(layerTypeId)) {
            return Result.fail("类型简称和ID不能同时为空");
        }
        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setAbbreviation(abbreviation);
        dto.setLayerTypeId(layerTypeId);
        List<LayerResponseDTO> list = layerService.list(dto);
        return Result.data(list);
    }



    /**
     * @return 新增修改图层类型
     */
    @ApiOperation(value = "图层列表", notes = "矢量图层列表")
    @GetMapping("/getTenantList")
    @DataPermissions(layerColumn = "l.id")
    public Result<List<TenantLayerListDTO>> getTenantList(LayerQueryDTO dto) {
        //设置查询矢量图层
        dto.setCategory("1");
        List<LayerResponseDTO> dtoList = layerService.list(dto);
        //图层首字母排序
        List<LayerResponseDTO> list = dtoList.stream().filter(d -> StringUtils.isNotEmpty(d.getLayerName())).sorted((o1, o2) -> new PinYinComparator().compare(o1.getCustomName(), o2.getCustomName())).collect(Collectors.toList());

        //map首字母排序
        Map<String, List<LayerResponseDTO>> map = list.stream().collect(Collectors.groupingBy(LayerResponseDTO::getLayerTypeName));
        final List<Map.Entry<String, List<LayerResponseDTO>>> infos = new ArrayList<>(map.entrySet());
        infos.sort((o1, o2) -> new PinYinComparator().compare(o1.getKey(), o2.getKey()));

        List<TenantLayerListDTO> dtos = new ArrayList<>();

        if(null != dto.getArTagFlag() && 1==dto.getArTagFlag()){
            for (Map.Entry<String, List<LayerResponseDTO>> entry : infos) {
                List<LayerResponseDTO> layers = entry.getValue();
                boolean allMarked = layers.stream().allMatch(layer -> layer.getMarkFlag() == 1);// 检查组内所有图层是否都被收藏
                // 如果组内所有图层都被收藏，则整个组标记为 1
                int groupMarkFlag = allMarked ? 1 : 0;
                dtos.add(TenantLayerListDTO.builder()
                        .layers(layers)
                        .abbreviation(layers.get(0).getAbbreviation())
                        .layerTypeName(entry.getKey())
                        .markFlag(groupMarkFlag) // 设置 markFlag 状态
                        .build());
            }
        }else{
            for (Map.Entry<String, List<LayerResponseDTO>> entry : infos) {
                dtos.add(TenantLayerListDTO.builder().layers(entry.getValue()).abbreviation(entry.getValue().get(0).getAbbreviation()).layerTypeName(entry.getKey()).build());
            }
        }
        return Result.data(dtos);
    }


    @ApiOperation(value = "图层列表", notes = "非矢量图层列表")
    @GetMapping("/nonVector/getTenantList")
    public Result<List<LayerResponseDTO>> getNonVectorTenantList(LayerQueryDTO dto) {
        dto.setCategory("2");
        dto.setPublishStatusFlag("1");
        dto.setIsAsc("asc");
        dto.setOrderByColumn("customName");
        return Result.data(layerService.list(dto));
    }

    @ApiOperation(value = "图层列表", notes = "图层列表")
    @GetMapping("/getTenantListByType")
    public Result<List<LayerResponseDTO>> getTenantListByType(String category) {
        LayerQueryDTO dto = new LayerQueryDTO();
        dto.setCategory(category);
        dto.setIsAsc("asc");
        dto.setOrderByColumn("customName");
        return Result.data(layerService.list(dto));
    }

    @ApiOperation(value = "图层列表", notes = "非矢量图层列表")
    @PostMapping("/getTenantList/multiType")
    @DataPermissions(layerColumn = "l.id")
    public Result<Map<String, List<LayerResponseDTO>>> getTenantListMultiType(@RequestBody LayerQueryDTO dto) {
        return Result.data(layerService.getTenantListMultiType(dto));
    }



    /**
     * @param id 图层ID
     * @return
     */
    @ApiOperation(value = "图层详情", notes = "图层详情")
    @GetMapping("/info")
    public Result<LayerInfoDTO> info(String id) {
        return layerService.info(id);
    }

    /**
     * @param dto
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层管理", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "新增修改图层", notes = "新增修改图层")
    @PostMapping("/addOrModify")
    public Result<String> addLayer(@Validated @RequestBody LayerAddDTO dto) {
        if (dto.getIsPg() == 1) {
            if ("1".equals(dto.getCategory())) {
                String layerId = layerPgService.addLayer(dto);
                return Result.data(layerId,"请求成功");
            } else {
                return layerService.addLayer(dto);
            }
        } else {
            return layerService.addLayer(dto);
        }

    }

    @ApiOperation(value = "根据图层ID查询图层", notes = "根据图层ID查询图层")
    @GetMapping("/selectLayerById")
    public Result<LayerResponseDTO> selectLayerById(@RequestParam(value = "layerId") String layerId,@RequestParam(value = "category",required = false,defaultValue = "2") String category) {
        return Result.data(layerService.selectLayerById(layerId,category));
    }


    /**
     * @param vo
     * @return 新增修改图层类型
     */
    @TowerLog(title = "图层管理", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除图层", notes = "删除图层")
    @PostMapping("/deleted")
    public Result<String> deleted(@RequestBody DeleteDTO vo) {
        return layerService.deleted(vo);
    }

    @ApiOperation(value = "全量查询图层中文和拼音对照表", notes = "全量查询图层中文和拼音对照表")
    @PostMapping("/getPolygonAttrDict")
    Result<List<PolygonAttrDictVO>> getPolygonAttrDict(@RequestBody PolygonAttrDictVO polygonAttrDictVO) {
        return layerService.getPolygonAttrDict(polygonAttrDictVO);
    }

    @ApiOperation(value = "工具箱-地块查询，构造查询弹窗属性", notes = "工具箱-地块查询，构造查询弹窗属性")
    @PostMapping("/getLayerTypeAttrFilter")
    Result<Map<String, List<LayerTypeAttrTilterVO>>> getLayerTypeAttrFilter(@RequestBody LayerTypeAttrTilterVO layerTypeAttrTilterVO) {
        return layerService.getLayerTypeAttrFilter(layerTypeAttrTilterVO);
    }

    @ApiOperation(value = "永久基本农田面积", notes = "永久基本农田面积")
    @PostMapping("/getLayerArea")
    @DataPermissions(layerColumn = "t1.id",tenantColumn = "t1.tenant_id", industryCode = "t1.industry_code")
    Result<List<LayerAreaVO>> getLayerArea(@RequestBody LayerAreaVO layerAreaVO) {
        return layerService.getLayerArea(layerAreaVO);
    }


    @ApiOperation(value = "根据图层图斑信息查询设备通道", notes = "根据图层图斑信息查询设备通道")
    @PostMapping("/getDeviceByPolygon")
    Result<InspectionRouteRelLayerSdkVO> getDeviceByPolygon(@RequestBody InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO) {
        return layerService.getDeviceByPolygon(inspectionRouteRelLayerSdkVO);
    }

    @ApiOperation(value = "查询视图联动的图层信息", notes = "查询视图联动的图层信息")
    @PostMapping("/getPolygonLayers")
    Result<List<InspectionRouteRelLayerSdkVO>> getPolygonLayers(@RequestBody InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO) {
        return layerService.getPolygonLayers(inspectionRouteRelLayerSdkVO);
    }

    @ApiOperation(value = "查询视图联动的图斑信息", notes = "查询视图联动的图斑信息")
    @PostMapping("/getPolygonFeatures")
    Result<List<InspectionRouteRelLayerSdkVO>> getPolygonFeatures(@RequestBody InspectionRouteRelLayerSdkVO inspectionRouteRelLayerSdkVO) {
        return layerService.getPolygonFeatures(inspectionRouteRelLayerSdkVO);
    }


    @ApiOperation(value = "修改默认套合图层", notes = "修改默认套合图层")
    @PostMapping("/updateNestFlag")
    Result<String> updateNestFlag(@RequestBody LayerQueryDTO layerQueryDTO) {
        layerService.updateNestFlag(layerQueryDTO);
        return Result.success("修改默认套合图层成功");
    }


    /**
     * 获取租户、行业矢量图层的所有默认套合图层
     *
     * @param layerQueryDTO tenantId industryCode
     * @return List<LayerResponseDTO>
     */
    @PostMapping("/getNestList")
    @DataPermissions(layerColumn = "l.id")
    Result<Map<String, LayerResponseDTO>> getNestList(@RequestBody LayerQueryDTO layerQueryDTO) {
        return Result.data(layerService.getNestList(layerQueryDTO));
    }



    @ApiOperation(value = "查询图层面积图斑数量", notes = "查询图层面积图斑数量")
    @PostMapping("/layerAreaCount")
    Result<List<LayerAreaCountVO>> layerAreaCount(@RequestBody List<String> layerIds) {
        return Result.data(layerService.layerAreaCount(layerIds));
    }

    @Deprecated
    @ApiOperation(value = "查询探而未采图层ID", notes = "查询探而未采图层ID")
    @PostMapping("/mineralsLayer")
    Result<MineralsLayerVO> mineralsLayer() {
//        MineralsLayerVO mineralsLayerVO = layerPgService.mineralsLayer(1, 15, null);
//        if (mineralsLayerVO != null) {
//            log.info("查询探而未采图层ID接口 返回:{}", mineralsLayerVO);
//        }
        return Result.success("启用");
    }

    @ApiOperation(value = "计算探而未采", notes = "计算探而未采")
    @PostMapping("/computeTewcLayer")
    Result<MineralsLayerVO> computeTewcLayer(@RequestBody MineralsLayerReqVO vo) {
        if (StringUtils.isEmpty(vo.getTkq()) || StringUtils.isEmpty(vo.getCkq())){
            throw new CustomException("请选择探矿权/采矿权图层");
        }
        MineralsLayerVO mineralsLayerVO = layerPgService.mineralsLayer(15, vo);
        if (mineralsLayerVO != null) {
            log.info("计算探而未采 返回:{}", mineralsLayerVO);
        }
        return Result.data(mineralsLayerVO);
    }


    /**
     * 根据图层类型，汇总数量
     * @param dto
     * @return
     */
    @PostMapping("/getLayerCount")
    public Result<List<LayerCountRes>> getLayerCount(@RequestBody LayerQueryDTO dto) {
        return Result.data(layerService.getLayerCount(dto));
    }

}
