package com.chinatower.platform.project.mapper;

import org.apache.ibatis.annotations.Param;

public interface SysUserMapper {

    /**
     * 根据用户名查询用户手机号
     * @param userName
     * @return
     */
    String selectUserPhone(@Param("userName") String userName);
}
