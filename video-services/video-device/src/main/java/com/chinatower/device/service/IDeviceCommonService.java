package com.chinatower.device.service;

import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;

import java.util.List;

public interface IDeviceCommonService {

    /**
     * 大屏设备区域树
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO);

    /**
     * 无人机设备组织树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO);

    /**
     * 大屏设备图层站址打点接口
     * @param siteRequestDTO
     * @return
     */
    List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO);

    /**
     * 根据站址编码查询设备列表信息
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO);

    /**
     * 查询设备距离信息
     * @param deviceDistanceDTO
     * @return
     */
    List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO);

    /**
     * 查询设备详情信息
     * @param deviceCommonInfoVO
     * @return
     */
    DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO);
}
