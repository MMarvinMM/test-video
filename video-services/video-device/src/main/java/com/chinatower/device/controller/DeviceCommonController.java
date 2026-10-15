package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.factory.DeviceServiceFactory;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-device
 * 设备类公共查询方法
 */
@RestController
@Api(value = "设备类公共查询方法")
@RequestMapping("/deviceCommon")
@Slf4j
public class DeviceCommonController {


    @Autowired
    private DeviceServiceFactory deviceServiceFactory;
    /**
     *
     * 区域设备树--给大屏展示用
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/getDeviceAreaTree")
    public Result<List<DeviceTreeVO>> getDeviceAreaTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = deviceServiceFactory.getBean(deviceReqDTO.getQueryType()).getDeviceAreaTree(deviceReqDTO);
        return Result.data(list);
    }

    /**
     * 组织设备树--给大屏展示用
     *
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/getDeviceOrgTree")
    public Result<List<DeviceTreeVO>> getDeviceOrgTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = deviceServiceFactory.getBean(deviceReqDTO.getQueryType()).getDeviceOrgTree(deviceReqDTO);
        return Result.data(list);
    }

    /**
     * 查询站址集合
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/getDeviceSites")
    Result<List<IndustryDeviceTree>> getDeviceSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(deviceServiceFactory.getBean(siteRequestDTO.getQueryType()).getDeviceSites(siteRequestDTO));
    }

    /**
     * 根据站址查询设备列表信息
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/getDeviceListBySiteCode")
    public Result<List<DeviceSiteVO>> getDeviceListBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO) {
        List<DeviceSiteVO> deviceSiteList = deviceServiceFactory.getBean(siteRequestDTO.getQueryType()).getDeviceListBySiteCode(siteRequestDTO);
        return Result.data(deviceSiteList);
    }

    /**
     * 查询设备距离信息
     * @param deviceDistanceDTO
     * @return
     */
    @PostMapping("/getDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> getDeviceDistanceInfo(@RequestBody DeviceDistanceDTO deviceDistanceDTO) {
        List<DeviceDistanceVO> deviceDistanceVOList = deviceServiceFactory.getBean(deviceDistanceDTO.getQueryType()).getDeviceDistanceInfo(deviceDistanceDTO);
        return Result.data(deviceDistanceVOList);
    }

    /**
     * 查询设备详情信息
     * @param deviceCommonInfoVO
     * @return
     */
    @PostMapping("/getDeviceInfo")
    public Result<DeviceCommonInfoVO> getDeviceInfo(@RequestBody DeviceCommonInfoVO deviceCommonInfoVO) {
        DeviceCommonInfoVO deviceInfo = deviceServiceFactory.getBean(deviceCommonInfoVO.getQueryType()).getDeviceInfo(deviceCommonInfoVO);
        return Result.data(deviceInfo);
    }


}
