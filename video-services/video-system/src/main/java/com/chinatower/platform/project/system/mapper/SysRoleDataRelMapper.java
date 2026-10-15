package com.chinatower.platform.project.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.project.system.domain.SysRoleDataRel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysRoleDataRelMapper extends BaseMapper<SysRoleDataRel> {

    @Delete(" delete from sys_role_data_rel where role_id = #{roleId} and industry_code = #{industryCode} and app_ver_code = #{appVerCode} ")
    void delete(@Param("roleId") Long roleId, @Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode);

}
