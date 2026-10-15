package com.chinatower.order.mapper;

import com.chinatower.mapper.TowerBaseMapper;
import com.chinatower.order.dto.SysTenantInfoHisMongoDTO;
import com.chinatower.order.dto.TenantIdIndustryDTO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.order.CustomerAddVO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.platform.entity.order.TenantWeatherRankReq;
import com.chinatower.system.api.model.DeviceVO;
import com.chinatower.system.api.resp.SysIndustryViewRelResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户相关mapper
 *
 * @return
 * @author chenmf
 * @date 2022/6/13
 * @version
 */
@Mapper
public interface SysTenantInfoMapper extends TowerBaseMapper<SysTenantInfo> {

    /**
     * 功能描述
     *  @param: tenantId
     * @return java.util.List<com.chinatower.platform.entity.admin.SysTenantInfo>
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    List<SysTenantInfo> selectTenantInfoByTenantId(@Param("tenantId") String tenantId);

    List<SysIndustryViewRelResponse> selectAppVerCodeByTenId(@Param("tenantId") String tenantId);

    List<SysTenantTrade> selectTenantTradeByTenantId(@Param("tenantId") String tenantId);

    int checkTenantIdUnique(String tenantId);

    CustomerAddVO getCustomerAddInfo(String tenantId);

    int deleteByTenantId(String tenantId);

    String getMaxId(String prefix);

    int transferDeptInfo(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                         @Param("targetIndustryCode") String targetIndustryCode, @Param("targetAppVerCode") String targetAppVerCode);

    int countTargetGridInfo(@Param("tenantId") String tenantId, @Param("targetIndustryCode") String targetIndustryCode);

    int transferGridInfo(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                         @Param("targetIndustryCode") String targetIndustryCode);

    int transferRoleRelation(@Param("tenantId") String tenantId,
                             @Param("sourceIndustryCode") String sourceIndustryCode,
                             @Param("sourceAppVerCode") String sourceAppVerCode,
                             @Param("targetIndustryCode") String targetIndustryCode,
                             @Param("targetAppVerCode") String targetAppVerCode);

    int deleteSourceIndustryRoleRelation(@Param("tenantId") String tenantId,
                                         @Param("sourceIndustryCode") String sourceIndustryCode,
                                         @Param("sourceAppVerCode") String sourceAppVerCode);

    String selectOldResourceTypeCreator(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                                 @Param("sourceAppVerCode") String sourceAppVerCode);

    int deleteSourceResourceType(@Param("tenantId") String tenantId, @Param("sourceIndustryCode") String sourceIndustryCode,
                                 @Param("sourceAppVerCode") String sourceAppVerCode);

    int initTargetResourceType(@Param("creator") String creator, @Param("tenantId") String tenantId,
                               @Param("targetIndustryCode") String targetIndustryCode,
                               @Param("targetAppVerCode") String targetAppVerCode);

    int countTargetResourceType(@Param("tenantId") String tenantId,
                                @Param("industryCode") String industryCode,
                                @Param("appVerCode") String appVerCode);

    SysDept selectRootDeptIdByIndustryInfo(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    int deleteDeptByTenantAndIndustry(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    int deleteGridByTenantAndIndustry(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<OrganDeviceRelVO> selectOrganDeviceInfoByTenantAndIndustry(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<OrganStreetRelVO> selectOrganStreetInfoByTenantAndIndustry(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    int deleteOrganDeviceRel(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                             @Param("deviceCodeList") List<String> deviceCodeList);

    int deleteOrganStreetRel(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                             @Param("countyCodeList") List<String> countyCodeList);

    int transferOrderPackageInfo(@Param("tenantId") String tenantId,
                                 @Param("acceptNode") String acceptNode,
                                 @Param("sourceIndustryCode") String sourceIndustryCode,
                                 @Param("sourceAppVerCode") String sourceAppVerCode,
                                 @Param("targetIndustryCode") String targetIndustryCode,
                                 @Param("targetAppVerCode") String targetAppVerCode,
                                 @Param("targetIndustryName") String targetIndustryName);

    int transferOrderPackageHisInfo(@Param("tenantId") String tenantId,
                                 @Param("acceptNode") String acceptNode,
                                 @Param("sourceIndustryCode") String sourceIndustryCode,
                                 @Param("sourceAppVerCode") String sourceAppVerCode,
                                 @Param("targetIndustryCode") String targetIndustryCode,
                                 @Param("targetAppVerCode") String targetAppVerCode,
                                 @Param("targetIndustryName") String targetIndustryName);

    void callTransferIndustryBaseInfo(@Param("tenantId") String tenantId,
                                      @Param("sourceIndustryCode") String sourceIndustryCode,
                                      @Param("sourceAppVerCode") String sourceAppVerCode,
                                      @Param("targetIndustryCode") String targetIndustryCode,
                                      @Param("targetAppVerCode") String targetAppVerCode);

    void callTransferOrderIndustryBaseInfo(@Param("tenantId") String tenantId,
                                           @Param("acceptNode") String acceptNode,
                                           @Param("sourceIndustryCode") String sourceIndustryCode,
                                           @Param("sourceAppVerCode") String sourceAppVerCode,
                                           @Param("targetIndustryCode") String targetIndustryCode,
                                           @Param("targetAppVerCode") String targetAppVerCode);

    int transferUserDeptInfo(@Param("tenantId") String tenantId,
                             @Param("sourceRootDeptId") String sourceRootDeptId,
                             @Param("targetRootDeptId") String targetRootDeptId);

    String getIndustryNameByCode(@Param("industryCode") String industryCode);

    /**
     *
     * @param dto
     * @return 查询租户下区域
     */
    List<SysArea> getTenantSysArea(@Param("dto") TenantWeatherRankReq dto);
    List<SysDept> selectTreeselectByTenantId(TenantIdIndustryDTO dept);
    /**
     * 查询租户自定义的部门数据
     * @param user
     * @return
     */
    List<SysDept> selectCustomTanentDept(SysUser user);
    /**
     * 查询租户下级部门
     * @param deptId
     * @return
     */
    List<SysDept> selectTenantChildDept(@Param("deptId") String deptId);
    /**
     *
     * @param areaCode
     * @return 查询区域
     */
    List<SysArea> getSysArea(@Param("areaCode") String areaCode);


    @DataScope(deptColumn = "r.DEPT_ID",tenantColumn = "r.TENANT_ID",industryCode = "r.industry_code")
    List<SysArea> selectStreet(SysDept dept);

    int insertSysTenant(@Param("param") SysTenantInfo param);

    List<SysTenantInfoHisMongoDTO> selectByIdList(@Param("idList")List<Long> idList);

    int deletePhysicalByIdList(@Param("idList")List<Long> idList);
}
