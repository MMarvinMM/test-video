package com.chinatower.device.service.impl;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.service.IDeviceCommonService;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class RecorderDevice2ServiceImpl implements IDeviceCommonService {

    @Autowired
    private RecorderDeviceServiceImpl recorderDeviceService;

    /**
     * 执法记录仪设备区域树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list;
        if (deviceReqDTO.isNeedDevice()){
            list = recorderDeviceService.getRecorderMonitorAreaTree(deviceReqDTO);
            if (!CollectionUtils.isEmpty(list)) {
                list.get(0).setFirst(1);
            }
        }else {
            RecorderDeviceVO recorderDeviceVO = new RecorderDeviceVO();
            BeanUtils.copyProperties(deviceReqDTO,recorderDeviceVO);
            if (StringUtils.isNotEmpty(deviceReqDTO.getOrderStatus())) {
                recorderDeviceVO.setOrderStatus(Integer.valueOf(deviceReqDTO.getOrderStatus()));
            }
            list = recorderDeviceService.getAreaTree(recorderDeviceVO);
        }
        return list;
    }

    /**
     * 执法记录仪设备组织树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = recorderDeviceService.getRecorderMonitorOrgTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setFirst(1);
        }
        return list;
    }

    /**
     * 执法记录仪图层打点接口
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO) {
        return recorderDeviceService.getRecorderSites(siteRequestDTO);
    }

    /**
     * 根据站址查询执法记录仪设备列表
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
        return recorderDeviceService.getRecorderDevicesBySiteCode(siteRequestDTO);
    }

    /**
     * 查询执法记录仪设备信息
     * @param deviceDistanceDTO
     * @return
     */
    @Override
    public List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
        return recorderDeviceService.getRecorderDeviceDistanceInfo(deviceDistanceDTO);
    }

    @Override
    public DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
        RecorderDeviceVO recorderDeviceVO = new RecorderDeviceVO();
        recorderDeviceVO.setDeviceCode(deviceCommonInfoVO.getDeviceCode());
        RecorderDeviceDTO detail = recorderDeviceService.detail(recorderDeviceVO);
        DeviceCommonInfoVO deviceInfo = new DeviceCommonInfoVO();
        if (detail != null){
            BeanUtils.copyProperties(detail,deviceInfo);
            deviceInfo.setOrderStatus(String.valueOf(detail.getOrderStatus()));
        }
        return deviceInfo;
    }
}
