package com.chinatower.bdm.modules.inspectionRoute.service;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import com.chinatower.common.entity.Result;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 巡检路线配置 服务类
 */
public interface IVideoInspectionRouteService {

    /**
     * 巡查线路列表分页查询
     * @param param 请求参数
     * @return 巡查线路列表信息
     */
    Result<PageInfo<VideoInspectionVO>> list(VideoInspectionDTO param);

    /**
     * 分页查询巡检路线配置信息
     * @param inspectionDTO 入参
     * @return Result<PageInfo<HornDeviceVo>>
     */
    Result<PageInfo<VideoInspectionVO>> queryListPage(VideoInspectionDTO inspectionDTO);

    /**
     * 巡检线路详情查询
     * @param inspectionRouteId 巡检路线编码
     * @return 巡检线路详情信息
     */
    Result<VideoInspectionDetailVO> detail(Long inspectionRouteId);

    /**
     * 处理巡航线路流程
     * @param param 参数
     * @return
     */
    Result<VideoInspectionRouteProcessVO> doProcess(VideoInspectionRouteProcessParam param);


    /**
     * 巡检进度接口入口
     * @param param
     * @return
     */
    Result<VideoInspectionRouteProcessVO> doInspectionProgress(VideoInspectionRouteProcessParam param);

    /**
     * 查询巡检下地块信息
     * @param param
     * @return
     */
    Result<PageInfo<VideoInspectionRelVO>> queryRelList(VideoInspectionRelDTO param);

    /**
     * 新增巡检路线
     * @param dto 入参
     * @return Result<String>
     */
    Result<String> add(VideoInspectionRouteSaveVO dto);

    /**
     * 编辑巡检路线
     * @param dto 入参
     * @return Result<String>
     */
    Result<String> update(VideoInspectionRouteSaveVO dto);

    /**
     * 删除巡检路线配置
     * @param inspectionDTO 入参
     * @return Result<String>
     */
    Result<String> delete(VideoInspectionDTO inspectionDTO);

    /**
     * 自动生成巡检路线名称
     * @return Result<String> 巡检路线名称
     */
    Result<String> createInspectionName();

    /**
     * 查询线路记录信息
     * @param param 参数
     * @return 返回值
     */
    Result<VideoInspectionRouteRecordVO> queryRouteRecordInfo(VideoInspectionRouteRecordParam param);
}
