package com.chinatower.device.controller;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.entity.vo.AllDeviceAttrVO;
import com.chinatower.device.repository.IDeviceInfoRepository;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * video-device
 */
@RestController
@Slf4j
@Api(value = "订单设备及服务信息模块", tags = "订单设备及服务信息模块")
public class ApiOrderDeviceController extends TowerBaseController {

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;
    /**
     * 根据客户id及行业编码查询对应订购的所有的摄像机、物联设备、雷达、卫星信息
     * @param entity
     * @return
     */
    @Deprecated
    @PostMapping("/industry/findAllDeviceByTenantId")
    public Result<AllDeviceAttrVO> findAllDeviceByTenantId(@RequestBody BaseIndustryEntity entity) {
        if (StringUtils.isEmpty(entity.getTenantId())) {
            return Result.fail("客户id不能为空");
        }
        if (StringUtils.isEmpty(entity.getIndustryCode())) {
            return Result.fail("行业编码不能为空");
        }
        if (StringUtils.isEmpty(entity.getAppVerCode())) {
            return Result.fail("行业版本编码不能为空");
        }
        AllDeviceAttrVO allDeviceAttrVO = new AllDeviceAttrVO();
        allDeviceAttrVO.setVideoDeviceList(deviceInfoRepository.getVideoDeviceList(entity));
        allDeviceAttrVO.setRadarDeviceList(deviceInfoRepository.getRadarDeviceList(entity));
        allDeviceAttrVO.setSensorDeviceList(deviceInfoRepository.getSensorDeviceList(entity));
        allDeviceAttrVO.setSatelliteServiceList(deviceInfoRepository.getSatelliteServiceList(entity));
        return Result.data(allDeviceAttrVO);
    }
}
