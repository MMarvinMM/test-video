package com.chinatower.bdm.modules.inspectionRoute.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.service.IInspectionRouteService;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.outdata.api.transfer.api.ITransferService;
import com.chinatower.platform.annotation.DataPermissions;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * video-bdm
 * <p>
 * 巡检路线配置 前端控制器
 * </p>
 *
 * @author lsh
 * @since 2023-07-28
 */
@RestController
@Slf4j
@RequestMapping("/inspection")
public class InspectionRouteController {

    @Autowired
    IInspectionRouteService inspectionRouteService;

    /**
     * 巡检线路配置分页查询
     * @param inspectionDTO 入参
     * @return Result<PageInfo<InspectionVO>>
     */
    @PostMapping("/queryList")
    public Result<PageInfo<InspectionVO>> queryInspectionRouteList(@RequestBody InspectionDTO inspectionDTO){
        return inspectionRouteService.queryListPage(inspectionDTO);
    }

    /**
     * 分页查询巡检线路信息
     * @param param
     * @return
     */
    @DataPermissions(layerColumn = "l.id")
    @PostMapping("/queryInspectionRoutePage4Sdk")
    public Result<PageInfo<InspectionRoutePage4SdkVO>> queryInspectionRoutePage4Sdk(@RequestBody InspectionRoutePage4SdkParam param) {
        return inspectionRouteService.queryInspectionRoutePage4Sdk(param);
    }

    /**
     * 启动巡检线路
     * @param param
     * @return
     */
    @PostMapping("/doProcess")
    public Result<InspectionRouteProcessVO> doProcess(@RequestBody InspectionRouteProcessParam param) {
        try {
            Result<InspectionRouteProcessVO> result = inspectionRouteService.doProcess(param);
            log.info("InspectionRouteController doProcess param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("InspectionRouteController doProcess error:", e);
            return Result.success(ResultCode.FAILURE + ":" + e.getMessage());
        }
    }

    /**
     * 查询巡检路线关联电子围栏信息
     * @param inspectionDTO 入参
     * @return Result<PageInfo<InspectionRelVO>>
     */
    @DataPermissions(layerColumn = "l.id")
    @PostMapping("/queryRelList")
    public Result<List<InspectionRelVO>> queryInspectionRelList(@RequestBody InspectionDTO inspectionDTO){
        if (inspectionDTO.getInspectionRouteId() == null){
            return Result.fail("巡检路线ID不能为空！");
        }
        return inspectionRouteService.queryRelList(inspectionDTO);
    }

    /**
     * 查询客户下的所有图层
     * @param layerQueryDTO 图层查询条件
     * @return Result<List<TenantLayerListDTO>>
     */
    @DataPermissions(layerColumn = "l.id")
    @PostMapping("/queryLayerList")
    public Result<List<LayerTreeVO>> queryLayerList(@RequestBody LayerQueryDTO layerQueryDTO){
        return inspectionRouteService.queryLayerList(layerQueryDTO);
    }


    /**
     * 根据图层、图斑编码查询对应的电子围栏信息
     * @param polygonPatchConfigDTO 入参
     * @return Result<PolygonPathVO> 电子围栏信息
     */
    @PostMapping("/queryPolygonPath")
    public Result<List<PolygonPatchConfigVO>> queryPolygonPath(@RequestBody PolygonPatchConfigDTO polygonPatchConfigDTO){
        return inspectionRouteService.queryPolygonPathInfo(polygonPatchConfigDTO);
    }

    /**
     * 新增巡检路线
     * @param dto 入参
     * @return Result<String>
     */
    @PostMapping("/insertInspectionRoute")
    public Result<String> insertInspectionRoute(@RequestBody InspectionRouteFenceVO dto){
        return inspectionRouteService.insertInspectionRoute(dto);
    }

    /**
     * 巡检进度入口
     * @param param
     * @return
     */
    @PostMapping("/doInspectionProgress")
    public Result<InspectionRouteProcessVO> doInspectionProgress(@RequestBody InspectionRouteProcessParam param) {
        if (Objects.isNull(param.getInspectionRouteId())) {
            return Result.fail("巡检路线编码不能为空");
        }
        try {
            Result<InspectionRouteProcessVO> result = inspectionRouteService.doInspectionProgress(param);
            log.info("InspectionRouteController doInspectionProgress param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("InspectionRouteController doInspectionProgress error:", e);
            return Result.success(ResultCode.FAILURE + ":" + e.getMessage());
        }
    }

    /**
     * 查询巡检点下地块信息
     * @param param
     * @return
     */
    @DataPermissions(layerColumn = "layer.id")
    @PostMapping("/queryInspectionLayerPage")
    public Result<PageInfo<InspectionRouteRelLayerSdkVO>> queryInspectionLayerPage(@RequestBody InspectionRouteRelLayerPageSdkParam param) {
        if (Objects.isNull(param.getInspectionRouteId())) {
            return Result.fail("巡检路线编码不能为空");
        }
        return inspectionRouteService.queryInspectionLayerPage(param);
    }
    /**
     * 删除巡检路线配置
     * @param inspectionDTO 入参
     * @return Result<String>
     */
    @PostMapping("/deleteInspectionRoute")
    public Result<String> deleteInspectionRoute(@RequestBody InspectionDTO inspectionDTO){
        return inspectionRouteService.deleteInspectionRoute(inspectionDTO);
    }

    /**
     * 自动生成巡检路线名称
     * @return Result<String> 巡检路线名称
     */
    @RequestMapping("/generateInspectionName")
    public Result<String> generateInspectionName(){
        return inspectionRouteService.createInspectionName();
    }

    /**
     * 编辑巡检路线
     * @param dto 入参
     * @return Result<String>
     */
    @PostMapping("/updateInspectionRoute")
    public Result<String> updateInspectionRoute(@RequestBody InspectionRouteFenceVO dto){
        return inspectionRouteService.updateInspectionRoute(dto);
    }

    /**
     *  删除电子围栏校验
     * @param polygonId 电子围栏ID
     * @return Result<String>
     */
    @GetMapping("/deletePolygonChecked")
    public Result<String> deletePolygonChecked(@RequestParam String polygonId){
        return inspectionRouteService.deletePolygonChecked(polygonId);
    }

    /**
     * 查询客户、行业、摄像机通道、预置位下最大的电子围栏序号
     * @param polygonPatchConfigVO 入参
     * @return Result<Integer>
     */
    @PostMapping("/selectMaxPolygonNo")
    public Result<Integer> selectMaxPolygonNo(@RequestBody PolygonPatchConfigVO polygonPatchConfigVO){
        return inspectionRouteService.selectMaxPolygonNo(polygonPatchConfigVO);
    }

    /**
     * 查询图层下的100条电子围栏信息
     */
    @PostMapping("/queryPolygonList")
    public Result<List<PolygonPatchConfigVO>> queryPolygonList(@RequestBody PolygonPatchConfigDTO dto){
        return inspectionRouteService.queryPolygonList(dto);
    }

    /**
     * 查询用户范围内的巡查路线总数
     * @return 用户范围内的巡查路线总数
     */
    @PostMapping("/getInspectionRouteNum")
    public Result<InspectionNumVO> getInspectionRouteNum(@RequestBody InspectionNumParam param) {
        return inspectionRouteService.getInspectionRouteNum(param);
    }

    /**
     * 查询用户范围内巡查路线图斑的总数
     * @return 用户范围内巡查路线图斑的总数
     */
    @PostMapping("/getInspectionPolygonNum")
    public Result<InspectionNumVO> getInspectionPolygonNum(@RequestBody InspectionNumParam param) {
        return inspectionRouteService.getInspectionPolygonNum(param);
    }

    @PostMapping("/queryRouteRecordInfo")
    public Result<InspectionRouteRecordVO> queryRouteRecordInfo(@RequestBody InspectionRouteRecordParam param) {
        return inspectionRouteService.queryRouteRecordInfo(param);
    }

    @Autowired
    private ITransferService iTransferService;

    @ApiOperation(value = "获取海图瓦片接口")
    @GetMapping("/chart/fishArea/getTile")
    public void getTile(@RequestParam("node") String node,
                        @RequestParam("userName")String userName,
                        @RequestParam("level")Integer level,
                        @RequestParam("col")Integer col,
                        @RequestParam("row")Integer row, HttpServletResponse response) {

        Result<byte[]> body = iTransferService.getTile(node, userName, level, col, row, null);
        ServletOutputStream outputStream = null;
        try {
            byte[] bytes = body.getData();
            response.setHeader("cache-control","max-age=864000");
            response.setContentType("image/png");
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("实时切图流关闭异常：", e);
                }
            }
        }
    }
}
