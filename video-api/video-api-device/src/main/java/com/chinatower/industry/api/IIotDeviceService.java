package com.chinatower.industry.api;

import com.chinatower.common.entity.Result;

import com.chinatower.device.api.IDeviceCommonService;
import com.chinatower.device.api.deprecated.IIotDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class IIotDeviceService {

    @Autowired
    private IIotDeviceServiceMigrate iIotDeviceServiceMigrate;

    @Autowired
    private IDeviceCommonService iDeviceCommonService;

    /**
     * 根据区域编码查询物联设备列表
     * @param iotDeviceVO
     * @return
     */
    @ApiOperation(value = "根据区域编码查询物联设备列表")
    @PostMapping("/iotdevice/queryIotDeviceByArea")
    public Result<List<IotDeviceVO>> queryIotDeviceByArea(@RequestBody IotDeviceVO iotDeviceVO){
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(iotDeviceVO,siteRequestDTO);
        siteRequestDTO.setQueryType(4);
        List<IndustryDeviceTree> industryDeviceTrees = iDeviceCommonService.getDeviceSites(siteRequestDTO).getData();
        List<IotDeviceVO> iotDeviceList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(industryDeviceTrees)) {
            industryDeviceTrees.stream().forEach(item -> {
                IotDeviceVO iotDevice = new IotDeviceVO();
                BeanUtils.copyProperties(item, iotDevice);
                if (Objects.nonNull(item.getDeviceStatus())) {
                    iotDevice.setDeviceStatus(Integer.valueOf(item.getDeviceStatus()));
                }
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<IotDeviceVO> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        IotDeviceVO childDeviceVO = new IotDeviceVO();
                        BeanUtils.copyProperties(child, childDeviceVO);
                        if (Objects.nonNull(child.getDeviceStatus())) {
                            childDeviceVO.setDeviceStatus(Integer.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childDeviceVO);
                    });
                    iotDevice.setList(childList);
                }
                iotDeviceList.add(iotDevice);
            });
        }
        return Result.data(iotDeviceList);
    }

    /**
     * 根据站址编码查询物联设备列表
     * @param iotDeviceVO
     * @return
     */
    @ApiOperation(value = "根据站址编码查询物联设备列表")
    @PostMapping("/iotdevice/queryIotDeviceBySite")
    public Result<List<IotDeviceVO>> queryIotDeviceBySite(@RequestBody IotDeviceVO iotDeviceVO){
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(iotDeviceVO,siteRequestDTO);
        siteRequestDTO.setQueryType(4);
        List<DeviceSiteVO> deviceSiteVOList = iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO).getData();
        List<IotDeviceVO> IotDeviceVOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
            deviceSiteVOList.stream().forEach(item -> {
                IotDeviceVO iotDevice = new IotDeviceVO();
                BeanUtils.copyProperties(item, iotDevice);
                if (Objects.nonNull(item.getDeviceStatus())) {
                    iotDevice.setDeviceStatus(Integer.valueOf(item.getDeviceStatus()));
                }
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<IotDeviceVO> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        IotDeviceVO childDeviceVO = new IotDeviceVO();
                        BeanUtils.copyProperties(child, childDeviceVO);
                        if (Objects.nonNull(child.getDeviceStatus())) {
                            childDeviceVO.setDeviceStatus(Integer.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childDeviceVO);
                    });
                    iotDevice.setList(childList);
                }
                IotDeviceVOList.add(iotDevice);
            });
        }
        return Result.data(IotDeviceVOList);
    }

    /**
     * 查询设备所属的行政区域树形
     * @param iotDeviceVO
     * @return
     */
    @ApiOperation(value = "查询设备所属的行政区域树形")
    @PostMapping("/iotdevice/getIotMonitorTree")
    public Result<List<IotDeviceVO>> getIotMonitorTree(@RequestBody IotDeviceVO iotDeviceVO){
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(iotDeviceVO,deviceReqDTO);
        deviceReqDTO.setQueryType(4);
        List<DeviceTreeVO> deviceTreeVOS = iDeviceCommonService.getDeviceAreaTree(deviceReqDTO).getData();
        List<IotDeviceVO> IotDeviceVOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(deviceTreeVOS)) {
            deviceTreeVOS.stream().forEach(province -> {
                IotDeviceVO pro = new IotDeviceVO();
                BeanUtils.copyProperties(province, pro);
                if (!CollectionUtils.isEmpty(province.getList())) {
                    List<IotDeviceVO> cityList = Lists.newArrayList();
                    province.getList().stream().forEach(city -> {
                        IotDeviceVO cit = new IotDeviceVO();
                        BeanUtils.copyProperties(city, cit);
                        if (!CollectionUtils.isEmpty(city.getList())){
                            List<IotDeviceVO> countyList = Lists.newArrayList();
                            city.getList().stream().forEach(county ->{
                                IotDeviceVO cou = new IotDeviceVO();
                                BeanUtils.copyProperties(county, cou);
                                if (!CollectionUtils.isEmpty(county.getList())){
                                    List<IotDeviceVO> townList = Lists.newArrayList();
                                    county.getList().stream().forEach(town ->{
                                        IotDeviceVO tow = new IotDeviceVO();
                                        BeanUtils.copyProperties(town, tow);
                                        if (!CollectionUtils.isEmpty(town.getList())){
                                            List<IotDeviceVO> deviceList = Lists.newArrayList();
                                            town.getList().stream().forEach(device ->{
                                                IotDeviceVO iotDevice = new IotDeviceVO();
                                                if (Objects.nonNull(device.getStatus())) {
                                                    iotDevice.setDeviceStatus(Integer.valueOf(device.getStatus()));
                                                }
                                            });
                                            tow.setList(deviceList);
                                        }
                                    });
                                    cou.setList(townList);
                                }
                            });
                            cit.setList(countyList);
                        }
                    });
                    pro.setList(cityList);
                }
                IotDeviceVOList.add(pro);
            });
        }
        return Result.data(IotDeviceVOList);
    }

}
