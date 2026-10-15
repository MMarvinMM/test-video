package com.chinatower.device.mapper;


import com.chinatower.platform.entity.devicemgmt.vo.TenantDeviceLabelTypeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TenantLabelTypeConfigMapper {

    /**
     * 查询设备标签类型列表
     * @param param 请求参数
     * @return 结果
     */
    List<TenantDeviceLabelTypeVO> listDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 新增设备标签类型
     * @param param 设备标签类型信息
     * @return 结果
     */
    int addDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 更新设备标签类型
     * @param param 设备标签类型信息
     * @return 结果
     */
    int updateDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 删除设备标签类型
     * @param param 设备标签类型信息
     * @return 结果
     */
    int deleteDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 统计相同名称标签类型数量
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param labelTypeName 标签类型名称
     * @param id 标签类型id
     * @return 数量
     */
    int countByLabelTypeName(@Param("tenantId")String tenantId,
                             @Param("industryCode")String industryCode,
                             @Param("labelTypeName")String labelTypeName,
                             @Param("id")Long id);

    /**
     * 根据标签类型名称和客户信息获取标签类型编码
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param labelTypeName 标签类型名称
     * @return 标签类型编码
     */
    Long selectLabelTypeIdByName(@Param("tenantId")String tenantId,
                                 @Param("industryCode")String industryCode,
                                 @Param("labelTypeName")String labelTypeName);

    /**
     * 统计客户维度下标签类型数量
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @return 数量
     */
    int countByTenantInfo(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    /**
     * 根据标签类型编码统计标签关联数量
     * @param labelTypeId 标签类型编码
     * @return 标签数量
     */
    int countLabelByTypeId(@Param("labelTypeId")Long labelTypeId);

    /**
     * 根据标签类型id查询标签类型信息
     * @param id 标签类型id
     * @return 标签类型信息
     */
    TenantDeviceLabelTypeVO getLabelTypeInfoById(@Param("id")Long id);
}