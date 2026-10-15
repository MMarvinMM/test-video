package com.chinatower.device.service.impl;

import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.service.IDeviceCommonService;
import com.chinatower.event.constants.Constants;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class IotDevice2ServiceImpl extends IBaseDeviceService implements IDeviceCommonService {

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    /**
     * 查询物联设备区域树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {
        super.fillUserInfo(deviceReqDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(deviceReqDTO, iotDeviceVO);
        log.info("deviceReqDTO--:{}, iotDeviceVO--:{}", deviceReqDTO, iotDeviceVO);
        List<DeviceTreeVO> treeList = iotDeviceRepository.getIotMonitorTree(iotDeviceVO);
        //回填设备告警是否完结
        setDeviceTreeAlarmIsCompleted(treeList);
        return super.buildMonitorTree(treeList, deviceReqDTO.isNeedDevice(), Boolean.FALSE);
    }

    /**
     * 物联设备组织树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {
        super.fillUserInfo(deviceReqDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(deviceReqDTO, iotDeviceVO);
        List<DeviceTreeVO> treeList = iotDeviceRepository.getIotMonitorOrgTree(iotDeviceVO);
        if (!treeList.isEmpty()) {
            treeList.get(0).setFirst(1);
        }
        return treeList;
    }

    /**
     * 物联设备图层打点接口
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO) {
        super.fillUserInfo(siteRequestDTO);
        List<IndustryDeviceTree> industryDeviceTreeList = iotDeviceRepository.queryIotDeviceSites(siteRequestDTO);
        //设置告警事件是否完结
        setDeviceAlarmIsCompleted(industryDeviceTreeList);
        return industryDeviceTreeList;
    }

    /**
     * 根据站址编码查询物联设备编码
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
        super.fillUserInfo(siteRequestDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(siteRequestDTO, iotDeviceVO);
        List<IotDeviceVO> iotDeviceVOList = iotDeviceRepository.queryIotDeviceBySite(iotDeviceVO);
        List<DeviceSiteVO> deviceSiteVOList = super.transferVO(iotDeviceVOList);
        setDeviceAlarmIsCompleted(deviceSiteVOList, Constants.ONE);
        return deviceSiteVOList;
    }

    /**
     * 查询周边物联设备列表
     * @param deviceDistanceDTO
     * @return
     */
    @Override
    public List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
        super.setRadiusAndUserInfo(deviceDistanceDTO);
        return iotDeviceRepository.queryIotDevDistanceInfo(deviceDistanceDTO);
    }

    /**
     * 查询物联设备详情信息--APP使用
     * @param deviceCommonInfoVO
     * @return
     */
    @Override
    public DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(deviceCommonInfoVO, iotDeviceVO);
        List<IotDeviceVO> iotDevice = iotDeviceRepository.queryIotDeviceInfo(iotDeviceVO);
        DeviceCommonInfoVO deviceInfo = new DeviceCommonInfoVO();
        if (!CollectionUtils.isEmpty(iotDevice)){
            //APP只会查出来一个物联设备
            IotDeviceVO deviceVO = iotDevice.get(0);
            BeanUtils.copyProperties(deviceVO,deviceInfo);
        }
        return deviceInfo;
    }
}
