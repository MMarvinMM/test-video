package com.chinatower.device.mapper;

import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceRelVO;

import java.util.List;

/**
 *
 * 功能描述: 租户视角--摄像机管理Mapper
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: release_v1.5
 * @requestID: 资源管理_摄像机管理
 */
public interface TenantDeviceRelMapper {

    /**
     * 功能描述: 根据条件查询设备信息
     * @param: tenantDeviceRelVO
     * @return: List<TenantDeviceRelVO>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    List<TenantDeviceRelVO> queryTenantDeviceRelList(TenantDeviceRelVO tenantDeviceRelVO);

    /**
     * 功能描述: 批量添加租户设备通道别名信息
     * @param: list
     * @return: int
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    int addTenantDeviceRel(List<TenantDeviceRelVO> list);

    /**
     * 功能描述: 更新添加租户设备通道别名信息
     * @param: list
     * @return: int
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    int updateTenantDeviceRel(List<TenantDeviceRelVO> list);

    /**
     * 功能描述: 根据租户ID获取设备名称集合
     * @param: deviceInfoVO
     * @return: List<String>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    List<String> getDeviceNameList(DeviceInfoVO deviceInfoVO);
}
