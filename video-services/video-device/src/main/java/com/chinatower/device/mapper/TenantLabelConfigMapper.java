package com.chinatower.device.mapper;

import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户标签配置DAO
 * @author chmod
 * @version 1.0
 * @date 2022/1/5 14:50
 */
public interface TenantLabelConfigMapper {
    /**
     * 添加
     * @param tenantLabelConfigVO
     * @return
     */
    int addTenantLabelConfig(TenantLabelConfigVO tenantLabelConfigVO);

    /**
     * 更新
     * @param tenantLabelConfigVO
     * @return
     */
    int updateTenantLabelConfig(TenantLabelConfigVO tenantLabelConfigVO);

    /**
     * 删除
     * @param labelId
     * @return
     */
    int deleteTenantLabelConfig(Long labelId);

    /**
     * 以租户维度统计标签数量
     * @param tenantLabelConfigVO
     * @return
     */
    int countLabel(TenantLabelConfigVO tenantLabelConfigVO);

    /**
     * 标签名是否唯一
     * @param tenantLabelConfigVO
     * @return
     */
    int countLabelName(TenantLabelConfigVO tenantLabelConfigVO);

    /**
     * 查询租户设备配置的标签
     * @param tenantDeviceLabelVO
     * @return
     */
    List<TenantDeviceLabelVO> queryLabelList(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 查询租户所属标签
     * @param tenantDeviceLabelVO
     * @return
     */
    List<TenantDeviceLabelVO> queryTenantLabelList(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 新增设备标签
     * @param param 设备标签信息
     * @return 结果
     */
    int addDeviceLabel(TenantDeviceLabelInfoVO param);

    /**
     * 更新设备标签
     * @param param 设备标签信息
     * @return 结果
     */
    int updateDeviceLabel(TenantDeviceLabelInfoVO param);

    /**
     * 删除设备标签
     * @param param 设备标签信息
     * @return 结果
     */
    int deleteDeviceLabel(TenantDeviceLabelInfoVO param);

    /**
     * 统计客户 标签类型维度下的标签数量
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param labelTypeId 标签类型编码
     * @return 标签数量
     */
    int countByTenantInfo(@Param("tenantId")String tenantId,
                          @Param("industryCode")String industryCode,
                          @Param("labelTypeId")Long labelTypeId);

    /**
     * 根据标签编码删除设备标签关系
     * @param labelId 标签编码
     * @return 影响条数
     */
    int deleteLabelRelByLabelId(@Param("labelId") Long labelId);

    /**
     * 根据标签名称统计客户维度下标签数量
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param labelName 标签名称
     * @param labelId 标签id
     * @return 数量
     */
    int countByLabelName(@Param("tenantId")String tenantId,
                         @Param("industryCode")String industryCode,
                         @Param("labelName")String labelName,
                         @Param("labelId")Long labelId);

    /**
     * 根据客户编码查询标签信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @return 标签信息
     */
    List<TenantDeviceLabelDetailVO> selectLabelInfoByTenantId(@Param("tenantId")String tenantId,
                                                             @Param("industryCode")String industryCode);

    /**
     * 根据条件查询设备标签信息
     *
     * @param param 设备标签请求参数
     * @return 设备标签信息列表
     */
    List<DeviceLabelInfoVO> selectDeviceLabelInfoByCondition(@Param("param") DeviceLabelReq param);

    /**
     * 根据客户编码查询标签信息【附带没有标签的标签类型】
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @return 标签信息
     */
    List<TenantDeviceLabelDetailVO> selectLabelInfoByTenantIdWithEmptyLabelType(@Param("tenantId")String tenantId,
                                                                                @Param("industryCode")String industryCode);

    /**
     * 根据id获取标签信息
     * @param labelId 标签id
     * @return 标签信息
     */
    TenantDeviceLabelInfoVO getLabelInfoById(@Param("labelId")Long labelId);

    /**
     * 分页查询设备标签信息
     * @param param 参数
     * @return 标签信息
     */
    List<TenantDeviceLabelDetailVO> queryDeviceLabelPage(@Param("param") TenantDeviceLabelQueryVO param);

    /**
     * 根据标签类型编码和客户信息获取标签数量
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param labelTypeId 标签类型编码
     * @return 标签数量
     */
    Integer getLabelCountByLabelTypeId(@Param("tenantId")String tenantId,
                                       @Param("industryCode")String industryCode,
                                       @Param("labelTypeId")Long labelTypeId);
}
