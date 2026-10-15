package com.chinatower.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.service.IDeviceCommonService;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HornDevice2ServiceImpl extends IBaseDeviceService implements IDeviceCommonService {


    @Autowired
    private IHornDeviceRepository hornDeviceRepository;

    @Override
    public List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {

        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        BeanUtils.copyProperties(deviceReqDTO,hornDeviceDTO);
        super.fillUserInfo(hornDeviceDTO);
        // 默认查询已订购的
        if (StringUtils.isEmpty(hornDeviceDTO.getOrderStatus())) {
            hornDeviceDTO.setOrderStatus("1");
        }
        if (StringUtils.isNotBlank(deviceReqDTO.getDeviceType())) {
            hornDeviceDTO.setDeviceType(Integer.parseInt(deviceReqDTO.getDeviceType()));
        }
        if (CollectionUtil.isNotEmpty(hornDeviceDTO.getDeviceStatusList())) {
            // 1 离线，0在线 切换为 0 离线，1在线
            if (hornDeviceDTO.getDeviceStatusList().size() == 1) {
                if ("1".equals(hornDeviceDTO.getDeviceStatusList().get(0))) {
                    hornDeviceDTO.setDeviceStatusList(Arrays.asList("0"));
                } else {
                    hornDeviceDTO.setDeviceStatusList(new ArrayList<>(Arrays.asList("1")));
                }
            }
            if (hornDeviceDTO.getDeviceStatusList().contains("1")) {
                hornDeviceDTO.getDeviceStatusList().addAll(Arrays.asList("2","3"));
            }
        }
        List<DeviceTreeVO> treeList = hornDeviceRepository.getHornMonitorTree(hornDeviceDTO);
        return super.buildMonitorTree(treeList, hornDeviceDTO.isNeedDevice(), Boolean.TRUE);
    }

    @Override
    public List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {

        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        BeanUtils.copyProperties(deviceReqDTO,hornDeviceDTO);
        super.fillUserInfo(hornDeviceDTO);
        if (StringUtils.isEmpty(hornDeviceDTO.getOrderStatus())) {
            hornDeviceDTO.setOrderStatus("1");
        }
        if (CollectionUtil.isNotEmpty(hornDeviceDTO.getDeviceStatusList())) {
            // 1 离线，0在线 切换为 0 离线，1在线
            if (hornDeviceDTO.getDeviceStatusList().size() == 1) {
                if ("1".equals(hornDeviceDTO.getDeviceStatusList().get(0))) {
                    hornDeviceDTO.setDeviceStatusList(Arrays.asList("0"));
                } else {
                    hornDeviceDTO.setDeviceStatusList(new ArrayList<>(Arrays.asList("1")));
                }
            }
            if (hornDeviceDTO.getDeviceStatusList().contains("1")) {
                hornDeviceDTO.getDeviceStatusList().addAll(Arrays.asList("2","3"));
            }
        }
        List<DeviceTreeVO> treeList = hornDeviceRepository.getHornMonitorOrgTree(hornDeviceDTO);
        if (!treeList.isEmpty()) {
            treeList.get(0).setFirst(1);
        }
        return treeList;
    }

    @Override
    public List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO) {
        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        BeanUtils.copyProperties(siteRequestDTO,hornDeviceDTO);
        super.fillUserInfo(hornDeviceDTO);
        if (StringUtils.isNotEmpty(siteRequestDTO.getProvinceId())) {
            hornDeviceDTO.setProvinceCode(siteRequestDTO.getProvinceId());
        }
        if (StringUtils.isNotEmpty(siteRequestDTO.getCityId())) {
            hornDeviceDTO.setCityCode(siteRequestDTO.getCityId());
        }
        if (StringUtils.isNotEmpty(siteRequestDTO.getCountyId())) {
            hornDeviceDTO.setCountyCode(siteRequestDTO.getCountyId());
        }

        List<HornDeviceVo> hornDeviceVoList = hornDeviceRepository.querySiteDevTree(hornDeviceDTO);
        List<IndustryDeviceTree> resultList = new ArrayList<>();
        for (HornDeviceVo hornDeviceVo : hornDeviceVoList){
            IndustryDeviceTree industryDeviceTree = new IndustryDeviceTree();
            industryDeviceTree.setSiteCode(hornDeviceVo.getSiteCode());
            industryDeviceTree.setLatitude(hornDeviceVo.getLatitude());
            industryDeviceTree.setLongitude(hornDeviceVo.getLongitude());
            industryDeviceTree.setList(hornDeviceVo.getList());
            resultList.add(industryDeviceTree);
        }
        return resultList;
    }

    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        BeanUtils.copyProperties(siteRequestDTO,hornDeviceDTO);
        hornDeviceDTO.setProvinceCode(siteRequestDTO.getProvinceId());
        hornDeviceDTO.setCityCode(siteRequestDTO.getCityId());
        hornDeviceDTO.setCountyCode(siteRequestDTO.getCountyId());
        super.fillUserInfo(hornDeviceDTO);
        List<HornDeviceVo> hornDeviceVoList = hornDeviceRepository.queryHornForSite(hornDeviceDTO);
        List<DeviceSiteVO> resultList = new ArrayList<>();
        for (HornDeviceVo hornDeviceVo : hornDeviceVoList){
            DeviceSiteVO deviceSiteVO = new DeviceSiteVO();
            BeanUtils.copyProperties(hornDeviceVo,deviceSiteVO);
            resultList.add(deviceSiteVO);
        }
        return resultList;
    }

    /**
     * 查询周边大喇叭设备
     * @param deviceDistanceDTO
     * @return
     */
    @Override
    public List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
        super.setRadiusAndUserInfo(deviceDistanceDTO);
        return hornDeviceRepository.queryHornDeviceDistanceInfo(deviceDistanceDTO);
    }

    @Override
    public DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
        HornDeviceDTO hornDeviceDTO = new HornDeviceDTO();
        hornDeviceDTO.setDeviceCode(deviceCommonInfoVO.getDeviceCode());
        Result<HornDeviceVo> hornDeviceVoResult = hornDeviceRepository.queryHornDeviceDetail(hornDeviceDTO);
        HornDeviceVo hornDeviceVo = hornDeviceVoResult.getData();
        DeviceCommonInfoVO deviceInfo = new DeviceCommonInfoVO();
        if (hornDeviceVo != null) {
            BeanUtils.copyProperties(hornDeviceVo,deviceInfo);
            deviceInfo.setDeviceStatus(Integer.parseInt(hornDeviceVo.getDeviceStatus()));
        }
        return deviceInfo;
    }
}
