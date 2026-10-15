package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.dto.StreetDeptDTO;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 功能描述: 自定义的 统计数据 DAO层
 *
 * @ClassName OrganDeviceRelMapper
 * @auther: chenmf
 * @date: 2022/6/16
 * @version: 1.0
 */
@Mapper
public interface OrganDeviceRelMapper extends BaseMapper<OrganDeviceRelDO> {


    /**
     * 查询组织下挂设备的区县关系
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    List<StreetDeptDTO> getDeptRel(@Param("tenantId") String tenantId,
                                   @Param("industryCode") String industryCode);

    List<StreetDeptDTO> getDeptRelBatch(@Param("tenantId") String tenantId,
                                        @Param("industryCodeList") List<String> industryCodeList);

    /**
     * 删除组织和行政区域关联关系
     *
     * @return int
     * @param: deptStreetRel
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    int deleteDeptStreetRel(SysDeptStreetRelVO deptStreetRel);

    int deleteDeptStreetRelBatch(@Param("tenantId") String tenantId, @Param("industryCodeList") List<String> industryCodeList);

    /**
     * 新增组织和行政区域关联关系
     *
     * @return int
     * @param: deptStreetRel
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    int insertBatchDeptStreetRelNew(List<SysDeptStreetRelVO> list);

    /**
     * 新增组织和行政区域权限关系
     *
     * @param list
     * @return
     */
    int insertBatchOrganStreetRel(List<OrganStreetRelVO> list);

    /**
     * 批量删除组织区域关系
     */
    void batchDelStreetRela(@Param("tenantId") String tenantId,
                            @Param("categoryCode") int categoryCode,
                            @Param("industryCode") String industryCode,
                            @Param("appVerCode") String appVerCode,
                            @Param("areaCodeList") List<String> areaCodeList);

    List<OrganStreetRelVO> selectOrganStreetInfoByCondition(@Param("tenantId") String tenantId,
                                                            @Param("categoryCode") int categoryCode,
                                                            @Param("industryCodeList") List<String> industryCodeList,
                                                            @Param("areaCodeList") List<String> areaCodeList);

    void batchDelStreetRelaByIds(@Param("ids") List<Long> ids);

    List<SysDept> selectTenantDeptId(@Param("tenantId")String customerCode,
                                              @Param("industryCode")String industryCode);

    List<SysDept> selectTenantRootDeptId(@Param("tenantId")String customerCode,
                                         @Param("industryCodeList")List<String> industryCodeList);

    /**
     * 根据条件查询已存在的设备编码列表
     * @param tenantId 租户id
     * @param industryCodeList 行业编码
     * @param deviceCodeList 设备编码列表
     * @param deviceTypeList 设备类型列表
     * @return 已存在的设备编码列表
     */
    List<DeviceIndustryDTO> selectExistDeviceCodeList(@Param("tenantId")String tenantId, @Param("industryCodeList")List<String> industryCodeList,
                                                      @Param("deviceCodeList")List<String> deviceCodeList, @Param("deviceTypeList")List<Integer> deviceTypeList);

    /**
     * 根据条件查询已存在的服务区域编码列表
     * @param tenantId 租户id
     * @param industryCode 行业编码
     * @param areaCodeList 服务区域
     * @param categoryCode 设备类型
     * @return 已存在的服务区域编码列表
     */
    List<String> selectExistServiceCountyCodeList(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode,
                                           @Param("areaCodeList")List<String> areaCodeList, @Param("categoryCode")int categoryCode);

    List<OrganDeviceRelDO> selectOrganDeviceInfoByCondition(@Param("tenantId")String tenantId,
                                                            @Param("deviceType")Integer deviceType,
                                                            @Param("industryCodeList")List<String> industryCodeList,
                                                            @Param("deviceCodeList")List<String> deviceCodeList);
}
