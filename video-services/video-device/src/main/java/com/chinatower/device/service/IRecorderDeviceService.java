package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 执法记录仪接口类
 */
public interface IRecorderDeviceService {

    /**
     * 分页查询执法记录仪列表
     * @param recorderDeviceVO
     * @return
     */
    Result<PageInfo<RecorderDeviceDTO>> getRecorderDeviceListPage(RecorderDeviceVO recorderDeviceVO);

    /**
     * 工作台设备区域树
     * @param recorderDeviceVO
     * @return
     */
    List<DeviceTreeVO> getAreaTree(RecorderDeviceVO recorderDeviceVO);

    /**
     * 根据条件查询所有执法记录仪列表
     * @param recorderDeviceVO
     * @return
     */
    List<RecorderDeviceDTO> getRecorderDeviceList(RecorderDeviceVO recorderDeviceVO);

    /**
     * 修改执法记录仪信息
     * @param recorderDeviceVO
     * @return
     */
    Result modify(RecorderDeviceVO recorderDeviceVO);

    /**
     * 查询执法记录仪详情
     * @param recorderDeviceVO
     * @return
     */
    RecorderDeviceDTO detail(RecorderDeviceVO recorderDeviceVO);

    /**
     * 查询执法记录仪设备组织树
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getRecorderMonitorOrgTree(DeviceReqDTO deviceReqDTO);

    /**
     * 询执法记录仪设备距离信息
     * @param param
     * @return
     */
    List<DeviceDistanceVO> getRecorderDeviceDistanceInfo(DeviceDistanceDTO param);

    /**
     * 根据站址查询执法记录仪设备列表
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> getRecorderDevicesBySiteCode(SiteRequestDTO siteRequestDTO);


    /**
     * 执法记录仪设备区域树--大屏使用
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getRecorderMonitorAreaTree(DeviceReqDTO deviceReqDTO);

    /**
     * 大屏--站址聚合点
     * @param siteRequestDTO
     * @return
     */
    List<IndustryDeviceTree> getRecorderSites(SiteRequestDTO siteRequestDTO);

    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq);

    /**
     * 查询执法记录仪列表基础信息
     * @param recorderDeviceVO
     * @return
     */
    List<RecorderDeviceDTO> queryRecorderListByCondition(RecorderDeviceVO recorderDeviceVO);
}
