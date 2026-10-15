package com.chinatower.platform.service;

import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.platform.entity.VmDeviceSiteVO;
import com.chinatower.platform.entity.admin.SysUser;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 15:12
 */
public interface VmDeviceRepository {


    /**
     * 通过网格id获取网格员对象
     * @param gridId
     * @return
     */
    String getGridUserByGridId(String gridId);


    /**
     * 根据角色ID&行业查询角色用户
     * @param roleIdList
     * @param tenantId
     * @return
     */
    List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(List<Long> roleIdList, String tenantId, String industryCode);


    /**
     * 查询摄像机信息
     * @param deviceCodeList
     * @return
     */
    List<VmDeviceSiteVO> queryDeviceList(List<String> deviceCodeList);


    /**
     * 根据用户ID查询用户信息
     * @param userIdList
     * @return
     */
    List<SysUserVO> getUserInfoByUserIds(List<String> userIdList);

    String getWarningTypeName(String warningTypeId);

}
