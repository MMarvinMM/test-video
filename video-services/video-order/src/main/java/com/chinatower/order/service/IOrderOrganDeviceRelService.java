package com.chinatower.order.service;

import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IOrderOrganDeviceRelService
 * @Deacription 设备组织服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
public interface IOrderOrganDeviceRelService {

    /**
     * 更新组织行政区域表（先删后增）
     *
     * @return void
     * @param: deviceOrgRelByTenantIdDTO
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void updateDeptStreetRel(String tenantId, String industryCode, String appVerCode);

    /**
     * 批量更新组织行政区域表（先删后增）
     * @param tenantId 客户编码
     * @param industryMap 行业信息
     */
    void updateDeptStreetRelBatch(String tenantId, Map<String, String> industryMap);

    /**
     * 批量保存设备组织关系
     *
     * @return void
     * @param: orgList
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchSaveDeviceRela(List<OrganDeviceRelVO> orgList);

    /**
     * 批量删除组织设备关系表
     *
     * @return void
     * @param: delDeviceRelaDTO
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchDelDeviceRela(DelDeviceRelaDTO delDeviceRelaDTO);

    void batchDelDeviceRelaByIds(List<Long> ids);

    List<OrganDeviceRelDO> selectOrganDeviceInfoByCondition(DelDeviceRelaDTO param);

    /**
     * 批量保存组织区域关系
     *
     * @return void
     * @param: orgList
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchSaveStreetRela(List<OrganStreetRelVO> orgList);

    /**
     * 批量删除组织区域关系
     */
    void batchDelStreetRela(String tenantId, int categoryCode, String industryCode,
                            String appVerCode, List<String> areaCodeList);

    List<OrganStreetRelVO> selectOrganStreetInfoByCondition(String tenantId, int categoryCode,
                                                            List<String> industryCodeList,
                                                            List<String> areaCodeList);

    void batchDelStreetRelaByIds(List<Long> ids);

    List<SysDept> selectTenantDeptId(String customerCode, String industryCode);

    List<SysDept> selectTenantRootDeptId(String customerCode, List<String> industryCodeList);

    /**
     * 根据条件查询已存在的设备编码列表
     * @param tenantId 租户id
     * @param industryCodeList 行业编码列表
     * @param deviceCodeList 设备编码列表
     * @param deviceTypeList 设备类型列表
     * @return 已存在的设备编码列表
     */
    List<DeviceIndustryDTO> selectExistDeviceCodeList(String tenantId, List<String> industryCodeList, List<String> deviceCodeList, List<Integer> deviceTypeList);

    /**
     * 根据条件查询已存在的服务区域编码列表
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param areaCodeList 服务区域
     * @param categoryCode 设备类型
     * @return 已存在的服务区域编码列表
     */
    List<String> selectExistServiceCountyCodeList(String tenantId, String industryCode, List<String> areaCodeList, int categoryCode);
}
