package com.chinatower.device.repository;

import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;

import java.util.List;

public interface UavNestCombineService {

    /**
     * 无人机区域树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorOrgTreeWithNest(DeviceReqDTO deviceReqDTO);


    /**
     * 无人机区域树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorAreaTreeWithNest(DeviceReqDTO deviceReqDTO);


    /**
     * 大屏设备图层站址打点接口
     * @param siteRequestDTO
     * @return
     */
    List<IndustryDeviceTree> getDeviceSitesWithNest(SiteRequestDTO siteRequestDTO);

    /**
     * 根据站址编码查询设备列表信息
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> getDeviceListBySiteCodeWithNest(SiteRequestDTO siteRequestDTO);

}
