package com.chinatower.bdm.modules.inspectionRoute.service;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.common.entity.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 巡检路线配置 服务类
 */
public interface IInspectionRouteService {

    /**
     * 分页查询巡检路线配置信息
     * @param inspectionDTO 入参
     * @return Result<PageInfo<HornDeviceVo>>
     */
    Result<PageInfo<InspectionVO>> queryListPage(InspectionDTO inspectionDTO);

    /**
     * 巡查线路列表分页查询
     * @param param 请求参数
     * @return 巡查线路列表信息
     */
    Result<PageInfo<InspectionRoutePage4SdkVO>> queryInspectionRoutePage4Sdk(InspectionRoutePage4SdkParam param);

    /**
     * 处理巡航线路流程
     * @param param 参数
     * @return
     */
    Result<InspectionRouteProcessVO> doProcess(InspectionRouteProcessParam param);

    /**
     * 查询巡检路线关联电子围栏信息
     * @param inspectionDTO 入参
     * @return Result<PageInfo<InspectionRelVO>>
     */
    Result<List<InspectionRelVO>> queryRelList(InspectionDTO inspectionDTO);

    /**
     * 查询客户下的所有图层
     * @param layerQueryDTO 图层查询条件
     * @return Result<List<TenantLayerListDTO>>
     */
    Result<List<LayerTreeVO>> queryLayerList(LayerQueryDTO layerQueryDTO);

    /**
     * 巡检进度接口入口
     * @param param
     * @return
     */
    Result<InspectionRouteProcessVO> doInspectionProgress(InspectionRouteProcessParam param);


    /**
     * 根据图层、图斑编码查询对应的电子围栏信息
     * @param polygonPatchConfigDTO 入参
     * @return Result<PolygonPathVO> 电子围栏信息
     */
    Result<List<PolygonPatchConfigVO>> queryPolygonPathInfo(PolygonPatchConfigDTO polygonPatchConfigDTO);

    /**
     * 新增巡检路线
     * @param dto 入参
     * @return Result<String>
     */
    Result<String> insertInspectionRoute(InspectionRouteFenceVO dto);

    /**
     * 查询巡检下地块信息
     * @param param
     * @return
     */
    Result<PageInfo<InspectionRouteRelLayerSdkVO>> queryInspectionLayerPage(InspectionRouteRelLayerPageSdkParam param);

    /**
     * 删除巡检路线配置
     * @param inspectionDTO 入参
     * @return Result<String>
     */
    Result<String> deleteInspectionRoute(InspectionDTO inspectionDTO);

    /**
     * 自动生成巡检路线名称
     * @return Result<String> 巡检路线名称
     */
    Result<String> createInspectionName();

    /**
     * 编辑巡检路线
     * @param dto 入参
     * @return Result<String>
     */
    Result<String> updateInspectionRoute(InspectionRouteFenceVO dto);

    /**
     *  删除电子围栏校验
     * @param polygonId 电子围栏ID
     * @return Result<String>
     */
    Result<String> deletePolygonChecked(String polygonId);

    /**
     * 查询客户、行业、摄像机通道、预置位下最大的电子围栏序号
     * @param polygonPatchConfigVO 入参
     * @return Result<Integer>
     */
    Result<Integer> selectMaxPolygonNo(PolygonPatchConfigVO polygonPatchConfigVO);


    Result<List<PolygonPatchConfigVO>> queryPolygonList(PolygonPatchConfigDTO dto);

    /**
     * 查询用户范围内的巡查路线总数
     * @return 用户范围内的巡查路线总数
     */
    Result<InspectionNumVO> getInspectionRouteNum(InspectionNumParam param);

    /**
     * 查询用户范围内巡查路线图斑的总数
     * @return 用户范围内巡查路线图斑的总数
     */
    Result<InspectionNumVO> getInspectionPolygonNum(InspectionNumParam param);

    /**
     * 查询线路记录信息
     * @param param 参数
     * @return 返回值
     */
    Result<InspectionRouteRecordVO> queryRouteRecordInfo(InspectionRouteRecordParam param);
}
