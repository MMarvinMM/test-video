package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.service.IOrganDeviceRelInfoService;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.device.DeviceOrgRelByTenantIdDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备组织关系服务
 *
 * @return
 * @author chenmf
 * @date 2022/2/19
 * @version
 */
@RestController
@Slf4j
@Api(value = "设备组织关系服务模块",tags = "设备组织关系服务模块")
public class OrganDeviceRelController {
    
    @Autowired
    private IOrganDeviceRelInfoService organDeviceRelInfoService;
    
    /**
     * 批量删除设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @PostMapping("/video/device/batchDelDeviceRela")
    public Result<String>  batchDelDeviceRela(@RequestBody DelDeviceRelaDTO delDeviceRelaDTO) {
        organDeviceRelInfoService.batchDelDeviceRela(delDeviceRelaDTO);
        return Result.success("批量删除设备组织关系成功!");
    }
    
    /**
     * 批量保存设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @PostMapping("/video/device/batchSaveDeviceRela")
    public Result<String>  batchSaveDeviceRela(@RequestBody List<OrganDeviceRelVO> organDeviceRelList) {
        organDeviceRelInfoService.batchSaveDeviceRela(organDeviceRelList);
        return Result.success("批量保存设备组织关系成功!");
    }
    
    /**
     * 查询组织下没有设备的列表
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    @PostMapping("/video/device/getDeptNoDeviceCode")
    Result<List<String>> getDeptNoDeviceCode(@RequestBody DelDeviceRelaDTO delDeviceRelaDTO) {
        
        List<String> deptList = organDeviceRelInfoService.getDeptNoDeviceCode(delDeviceRelaDTO);
        return Result.data(deptList);
    }
    
    /**
     * 更新组织行政区域表（先删后增）
     *
     * @return
     * @author chenmf
     * @date 2022/5/29
     * @version
     */
    @PostMapping("/video/device/updateDeptStreetRel")
    Result<List<String>> updateDeptStreetRel(@RequestBody DeviceOrgRelByTenantIdDTO deviceOrgRelByTenantIdDTO) {
        organDeviceRelInfoService.updateDeptStreetRel(deviceOrgRelByTenantIdDTO.getTenantId());
        return Result.success("批量保存设备组织关系成功!");
    }
}
