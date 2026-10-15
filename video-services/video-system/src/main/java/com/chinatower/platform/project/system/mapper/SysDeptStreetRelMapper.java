package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.project.system.domain.vo.SysDeptStreetVO;
import com.chinatower.system.api.model.*;
import com.chinatower.platform.project.system.domain.SysDeptStreetRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022-03-21
 */
public interface SysDeptStreetRelMapper
{

    /**
     * 组织和行政区域关联关系查询
     *
     * @param deptStreetRel
     * @return 【请填写功能名称】集合
     */
    public List<SysDeptStreetRelVO> selectDeptStreetRelList(SysDeptStreetRelVO deptStreetRel);


    /**

    /**
     * 删除【请填写功能名称】
     *
     * @param orgId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDeptStreetRelByOrgId(String orgId);

    /**
     * 删除组织和行政区域关联关系
     *
     * @param deptStreetRel
     * @return 结果
     */
    public int deleteDeptStreetRel(SysDeptStreetRelVO deptStreetRel);


    int insertBatchDeptStreetRelNew(List<SysDeptStreetRelVO> list);

    void insertBatchDeptStreetRelTenant(List<SysDeptStreetRel> list);

    void insertBatchDeptStreetRel(List<SysDeptStreetRel> list);

    List<SysAreaVO> getSysProvinceTown(@Param("regionCodeList") List<String> regionCodeList);

    List<SysArea> getSysStreet(@Param("regionCodeList") List<String> regionCodeList);

//    List<String> getStreetCodeByOrgId(String orgId);

    @DataScope(deptColumn = "r.DEPT_ID",tenantColumn = "r.TENANT_ID",appVerCode = "r.app_ver_code",industryCode = "r.industry_code")
    List<SysDeptStreetVO> selectStreet(SysDept dept);

    List<AreaDataVO> selectStreetByDeptIds(@Param("deptIds") List<String> deptId, @Param("industryCode") String industryCode, @Param("tenantId") String tenantId);

    @DataScope(appVerCode = "r.app_ver_code")
    List<SysDeptDeviceVO> selectDeptStreet(SysDept dept);

}