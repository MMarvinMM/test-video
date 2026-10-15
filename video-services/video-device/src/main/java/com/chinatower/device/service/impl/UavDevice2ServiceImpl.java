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
import com.chinatower.device.service.IDeviceCommonService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AlarmOrderParmaDto;
import com.chinatower.event.entity.vo.OrderCompleteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UavDevice2ServiceImpl extends IBaseDeviceService implements IDeviceCommonService {

    @Autowired
    private UavDeviceService uavDeviceService;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Autowired
    private AirConfigService airConfigService;


    /**
     * 无人机设备区域树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list;
        if (deviceReqDTO.isNeedDevice()){
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (currentUser != null) {
                list = uavDeviceService.getUavMonitorAreaTree(deviceReqDTO);
            } else {
                list = uavDeviceService.getUavMonitorAreaTreeWithoutLogin(deviceReqDTO);
            }
            if (!CollectionUtils.isEmpty(list)) {
                list.get(0).setFirst(1);
            }
        }else {
            UavDeviceDTO uavDeviceDTO = new UavDeviceDTO();
            BeanUtils.copyProperties(deviceReqDTO,uavDeviceDTO);
            if (StringUtils.isNotEmpty(deviceReqDTO.getOrderStatus())) {
                uavDeviceDTO.setOrderStatus(Integer.valueOf(deviceReqDTO.getOrderStatus()));
            }
            list = uavDeviceService.getUavMonitorTree(uavDeviceDTO);
        }
        return list;
    }

    /**
     * 无人机设备组织树
     * @param deviceReqDTO
     * @return
     */
    @Override
    public List<DeviceTreeVO> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = uavDeviceService.getUavMonitorOrgTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setFirst(1);
        }
        return list;
    }

    /**
     * 无人机图层--根据站址打点
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<IndustryDeviceTree> getDeviceSites(SiteRequestDTO siteRequestDTO) {
        List<IndustryDeviceTree> industryDeviceTreeList = uavDeviceService.queryUavSites(siteRequestDTO);
        // 填充飞行范围
        fillFlightRadiusInfo(industryDeviceTreeList);
        setDeviceAlarmIsCompleted(industryDeviceTreeList);
        return industryDeviceTreeList;
    }

    private void fillFlightRadiusInfo(List<IndustryDeviceTree> industryDeviceTreeList) {
        if (CollectionUtils.isEmpty(industryDeviceTreeList)) {
            return;
        }
        List<String> deviceCodeList = Lists.newArrayList();
        industryDeviceTreeList.forEach(item -> {
            //获取所有设备编码
            List<String> list = item.getList().stream().map(IndustryDeviceTree::getDeviceCode).collect(Collectors.toList());
            deviceCodeList.addAll(list);
        });
        Result<List<FlightRadiusVo>> uavFlightRadiusInfo = airConfigService.queryFlightRadiusByUavCode(deviceCodeList);
        log.info("fillFlightRadiusInfo param:{}, result:{}", JSON.toJSON(deviceCodeList), JSON.toJSONString(uavFlightRadiusInfo));
        if (uavFlightRadiusInfo != null && CollectionUtils.isNotEmpty(uavFlightRadiusInfo.getData())) {
            Map<String, Double> uavFlightRadiusMap = uavFlightRadiusInfo.getData().stream()
                    .collect(Collectors.toMap(FlightRadiusVo::getUavCode, FlightRadiusVo::getFlightRadius, (a,b) -> a));
            industryDeviceTreeList.forEach(item -> {
                for (IndustryDeviceTree industryDeviceTree : item.getList()) {
                    industryDeviceTree.setFlightRadius(uavFlightRadiusMap.get(industryDeviceTree.getDeviceCode()));
                }
            });
        }
    }

    /**
     * 根据站址查询设备列表
     * @param siteRequestDTO
     * @return
     */
    @Override
    public List<DeviceSiteVO> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
        List<DeviceSiteVO> deviceSiteVOList = uavDeviceService.queryUavDevicesBySiteCode(siteRequestDTO);
        setDeviceAlarmIsCompleted(deviceSiteVOList, Constants.ZERO);
        return deviceSiteVOList;
    }

    /**
     * 查询无人机周围设备信息
     * @param deviceDistanceDTO
     * @return
     */
    @Override
    public List<DeviceDistanceVO> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
        super.setRadiusAndUserInfo(deviceDistanceDTO);
        return uavDeviceService.queryUavDeviceDistanceInfo(deviceDistanceDTO);
    }

    @Override
    public DeviceCommonInfoVO getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
        UavDeviceQueryVO uavDeviceQueryVO = uavDeviceService.qryByDeviceCode(deviceCommonInfoVO.getDeviceCode());
        DeviceCommonInfoVO deviceInfo = new DeviceCommonInfoVO();
        if (uavDeviceQueryVO != null) {
            BeanUtils.copyProperties(uavDeviceQueryVO, deviceInfo);
            if (StringUtils.isNotBlank(deviceInfo.getDeviceCode())) {
                SysUser sysUser = SecurityUtils.getLoginUser().getUser();
                List<String> deviceCodeList = Lists.newArrayList();
                deviceCodeList.add(deviceInfo.getDeviceCode());
                AlarmOrderParmaDto dto = new AlarmOrderParmaDto();
                dto.setDeviceCodes(deviceCodeList);
                dto.setTenantId(sysUser.getTenantId());
                dto.setIndustryCode(sysUser.getIndustryCode());
                log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 请求参数：{}", dto);
                Result<List<OrderCompleteVO>> result = iTradeWarningOrderService.getOrderNumByDeviceCode(dto);
                log.info("设备得告警事件是否完结/alarm/order/getOrderNumByDeviceCode 返回参数：{}", result);
                if (ResultCode.SUCCESS.getCode() == result.getCode()) {
                    String isCompleteFlag = Constants.ZERO;
                    if (!CollectionUtils.isEmpty(result.getData())) {
                        OrderCompleteVO orderCompleteVO = result.getData().stream().filter(vo -> Objects.equals(deviceInfo.getDeviceCode(), String.valueOf(vo.getDeviceCode()))).findFirst().orElse(null);
                        isCompleteFlag = Objects.nonNull(orderCompleteVO) ? String.valueOf(orderCompleteVO.getIsCompleteFlag()) : Constants.ZERO;
                    }
                    deviceInfo.setIsCompleteFlag(isCompleteFlag);
                }
            }
        }
        return deviceInfo;
    }
}
