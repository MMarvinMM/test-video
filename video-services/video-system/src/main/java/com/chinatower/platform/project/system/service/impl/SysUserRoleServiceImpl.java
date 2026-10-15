package com.chinatower.platform.project.system.service.impl;

import com.chinatower.platform.project.system.domain.SysUserRole;
import com.chinatower.platform.project.system.mapper.SysUserRoleMapper;
import com.chinatower.platform.project.system.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SysUserRoleServiceImpl implements ISysUserRoleService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public void save(SysUserRole userRole) {

        userRoleMapper.deleteUserRoleInfo(userRole);
        userRoleMapper.batchUserRole(Arrays.asList(userRole));

    }

}
