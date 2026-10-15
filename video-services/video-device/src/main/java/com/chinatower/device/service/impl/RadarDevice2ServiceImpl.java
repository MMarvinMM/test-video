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
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 雷达接口实现类
 */
@Service
public class RadarDevice2ServiceImpl extends IBaseDeviceService implements IDeviceCommonService {

    @Autowired
    private IIotDeviceRepository iotDeviceRepository;

    /**
     * 大屏-雷达设备区域树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {
        super.fillUserInfo(deviceReqDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(deviceReqDTO, iotDeviceVO);
        List<DeviceTreeVO> treeList = iotDeviceRepository.getRadarMonitorTree(iotDeviceVO);
        //回填设备告警是否完结
        setDeviceTreeAlarmIsCompleted(treeList);
        return super.buildMonitorTree(treeList, deviceReqDTO.isNeedDevice(), Boolean.FALSE);
    }

    /**
     * 大屏-雷达设备组织树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {
        super.fillUserInfo(deviceReqDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(deviceReqDTO, iotDeviceVO);
        List<DeviceTreeVO> treeList = iotDeviceRepository.getRadarMonitorOrgTree(iotDeviceVO);
        if (!treeList.isEmpty()) {
            treeList.get(0).setFirst(1);
        }
        return treeList;
    }

    /**
     * 大屏-雷达图层打点
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO) {
        super.fillUserInfo(siteRequestDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(siteRequestDTO, iotDeviceVO);
        List<IotDeviceVO> iotDeviceVOList = iotDeviceRepository.queryRadarDeviceByArea(iotDeviceVO);
        List<IndustryDeviceTree> industryDeviceTreeList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(iotDeviceVOList)) {
            iotDeviceVOList.stream().forEach(item -> {
                IndustryDeviceTree industryDeviceTree = new IndustryDeviceTree();
                BeanUtils.copyProperties(item, industryDeviceTree);
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<IndustryDeviceTree> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        IndustryDeviceTree childDeviceVO = new IndustryDeviceTree();
                        BeanUtils.copyProperties(child, childDeviceVO);
                        if (Objects.nonNull(child.getDeviceStatus())) {
                            childDeviceVO.setDeviceStatus(String.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childDeviceVO);
                    });
                    industryDeviceTree.setList(childList);
                }
                industryDeviceTreeList.add(industryDeviceTree);
            });
        }
        //设置告警事件是否完结
        setDeviceAlarmIsCompleted(industryDeviceTreeList);
        return industryDeviceTreeList;
    }

    /**
     * 大屏-根据站址查询雷达设备列表
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
        super.fillUserInfo(siteRequestDTO);
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(siteRequestDTO, iotDeviceVO);
        List<IotDeviceVO> iotDeviceVOList = iotDeviceRepository.queryRadarDeviceBySite(iotDeviceVO);
        List<DeviceSiteVO> deviceSiteVOList =  super.transferVO(iotDeviceVOList);
        setDeviceAlarmIsCompleted(deviceSiteVOList, Constants.ONE);
        return deviceSiteVOList;
    }

    /**
     * 查询周围雷达设备
     * @param deviceDistanceDTO
     * @return
     */
    @Override
    public List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
        super.setRadiusAndUserInfo(deviceDistanceDTO);
        return iotDeviceRepository.queryRadarDevDistanceInfo(deviceDistanceDTO);
    }

    /**
     * 查询雷达设备信息--APP使用
     * @param deviceCommonInfoVO
     * @return
     */
    @Override
    public DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
        IotDeviceVO iotDeviceVO = new IotDeviceVO();
        BeanUtils.copyProperties(deviceCommonInfoVO, iotDeviceVO);
        List<IotDeviceVO> radarDevice = iotDeviceRepository.queryRadarDeviceInfo(iotDeviceVO);
        DeviceCommonInfoVO deviceInfo = new DeviceCommonInfoVO();
        if (!CollectionUtils.isEmpty(radarDevice)){
            IotDeviceVO radar = radarDevice.get(0);
            BeanUtils.copyProperties(radar,deviceInfo);
        }
        return deviceInfo;
    }
}
