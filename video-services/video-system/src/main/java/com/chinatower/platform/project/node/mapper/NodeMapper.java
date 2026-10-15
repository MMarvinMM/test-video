package com.chinatower.platform.project.node.mapper;

import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.node.entity.PlatFormVO;
import com.chinatower.platform.project.node.entity.SwitchUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NodeMapper
{
    List<PlatFormVO> selectNodeByTenantId(PlatFormVO platFormVO);
    SysUser selectUserByUserName(String userId);
    Long validateSwitchUser(SwitchUserVO dto);

    SysDept getDeptById(@Param("deptId") String deptId);

    SysUser selectUserByUserId(String userId);

    List<SysDept> getDeptByCurrentDeptId(SysDept sysDept);

    List<String> getTenantByTowerDeptId(@Param("deptIds") List<SysDept> deptId);

}
