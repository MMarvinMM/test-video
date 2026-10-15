package com.chinatower.device.repository;

import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;

import java.util.List;

public interface UavNestDeviceService {


    /**
     * 查询无人机机巢设备距离信息
     *
     * @param param 参数
     * @return 无人机设备距离信息
     */
    List<DeviceDistanceVO> queryUavNestDeviceDistanceInfo(DeviceDistanceDTO param);

    /**
     * 无人机机巢区域树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavNestMonitorAreaTree(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavNestMonitorAreaTreeWithoutFormat(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavNestMonitorOrgTree(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavNestMonitorOrgTreeWithoutFormat(DeviceReqDTO deviceReqDTO);

    /**
     * 根据站址编码查询无人机列表信息
     *
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> queryUavNestDevicesBySiteCode(SiteRequestDTO siteRequestDTO);

    List<IndustryDeviceTree> queryUavNestSites(SiteRequestDTO siteRequestDTO);

    DeviceCommonInfoVO qryByDeviceCode(String deviceCode);
}
