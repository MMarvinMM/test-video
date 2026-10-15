package com.chinatower.order.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.dto.StreetDeptDTO;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;

import java.util.List;


/**
 * <p>
 * 设备组织关系服务类
 * </p>
 *
 * @author chenmf
 * @since 2022-01-19
 */
public interface IOrganDeviceRelRepository extends IService<OrganDeviceRelDO> {


    /**
     * 批量保存设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    void batchSaveDeviceRela(List<OrganDeviceRelDO> organDeviceRelList);

    /**
     * 批量删除设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    void batchDelDeviceRela(DelDeviceRelaDTO delDeviceRelaDTO);

    /**
     * 根据条件查询设备组织关系
     * @param param 参数
     * @return 设备组织关系信息
     */
    List<OrganDeviceRelDO> selectOrganDeviceInfoByCondition(DelDeviceRelaDTO param);

    /**
     * 查询组织下挂设备的区县关系
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    List<StreetDeptDTO> getDeptRel(String tenantId, String industryCode);

    List<StreetDeptDTO> getDeptRelBatch(String tenantId, List<String> industryCodeList);

    /**
     * 删除组织行政关系数据
     * @param: sysDeptStreetRelVO
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void deleteDeptStreetRel(SysDeptStreetRelVO sysDeptStreetRelVO);

    void deleteDeptStreetRelBatch(String tenantId, List<String> industryCodeList);

    /**
     * 新增组织行政关系数据
     * @param: list
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void insertDeptStreetRel(List<SysDeptStreetRelVO> list);

    /**
     * 批量保存组织区域关系
     * @param: orgList
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    void batchSaveStreetRela(List<OrganStreetRelVO> orgList);


    /**
     * 批量删除组织区域关系
     */
    void batchDelStreetRela(String tenantId, int categoryCode , String industryCode,
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
