package com.chinatower.platform.project.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.project.system.domain.SysRoleIndustryAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与行业关联表 数据层
 *
 * @author chinatower
 */
@Mapper
public interface SysRoleIndustryAuthMapper extends BaseMapper<SysRoleIndustryAuth>
{

    void batchInsert(List<SysRoleIndustryAuth> list);

    void deleteByRole(@Param("roleId") Long roleId);

    void deleteByRoleIdList(@Param("roleIdList") List<Long> roleIdList);

    int checkMenuExistRole(@Param("menuId") Long menuId);

    List<Long> selectAuthIdByMenuId(@Param("menuIds") Long[] menuIds,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode);

}
