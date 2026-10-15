package com.chinatower.device.mapper;

import com.chinatower.platform.entity.devicemgmt.vo.TenantChannelRelVO;
import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceRelVO;

import java.util.List;

/**
 * @ClassName: TenantChannelRelMapper
 * @Description: 租户通道关系Dao
 * @Author: chmod
 * @Date: 2021-12-07 15:51
 **/
public interface TenantChannelRelMapper {

    /**
     * 根据条件查询
     * @param tenantChannelRelVO
     * @return
     */
//    List<TenantChannelRelVO> queryTenantChannelRelList(TenantChannelRelVO tenantChannelRelVO);

    /**
     * 批量添加
     * @param tenantChannelRelList
     * @return
     */
//    int addTenantChannelRel(List<TenantChannelRelVO> tenantChannelRelList);

    /**
     * 批量更新
     * @param tenantChannelRelList
     * @return
     */
//    int updateTenantChannelRel(List<TenantChannelRelVO> tenantChannelRelList);

    /**
     * 根據條件查询通道别名信息
     * @param tenantDeviceRelVO
     * @return
     */
    List<TenantDeviceRelVO> getTenantDeviceRelList(TenantDeviceRelVO tenantDeviceRelVO);

    /**
     * 新增租户通道别名信息
     * @param list
     * @return
     */
    int addTenantChannelCommonRel(List<TenantDeviceRelVO> list);

    /**
     * 功能描述: 更新添加租户设备通道别名信息
     * @param list
     * @return
     */
    int updateTenantChannelCommonRel(List<TenantDeviceRelVO> list);
}
