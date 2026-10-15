package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.service.AirConfigService;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.FlightRadiusVo;
import com.chinatower.device.entity.vo.UavDeviceQueryVO;
import com.chinatower.device.repository.UavDeviceService;
import com.chinatower.device.repository.UavNestDeviceService;
import com.chinatower.device.service.IDeviceCommonService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UavNestDevice2ServiceImpl extends IBaseDeviceService implements IDeviceCommonService {

    @Autowired
    private UavNestDeviceService uavNestDeviceService;

    /**
     * 无人机设备区域树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (currentUser == null) {
            throw new CustomException("用户未登录");
        }
        return uavNestDeviceService.getUavNestMonitorAreaTree(deviceReqDTO);
    }

    @Override
    public List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = uavNestDeviceService.getUavNestMonitorOrgTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setFirst(1);
        }
        return list;
    }

    @Override
    public List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO) {
        return uavNestDeviceService.queryUavNestSites(siteRequestDTO);
    }

    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
        return uavNestDeviceService.queryUavNestDevicesBySiteCode(siteRequestDTO);
    }

    @Override
    public List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
        super.setRadiusAndUserInfo(deviceDistanceDTO);
        return uavNestDeviceService.queryUavNestDeviceDistanceInfo(deviceDistanceDTO);
    }

    @Override
    public DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
        return uavNestDeviceService.qryByDeviceCode(deviceCommonInfoVO.getDeviceCode());
    }
}
