package com.chinatower.platform.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.mapper.ISysMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.repository.ISysService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础信息业务接口实现
 * @author chmod
 * @version 1.0
 * @date 2022/3/04 15:22
 */
@Slf4j
@Service
public class ISysServiceImpl implements ISysService {
    @Autowired
    private ISysMapper sysMapper;

    /**
     * 根据组织查询用户
     * @param deptId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> selectUserByDeptId(String deptId) {
        List<SysUser> result = new ArrayList<>();
        try {
            result = sysMapper.selectUserByDeptId(deptId);
        } catch (Exception e) {
            log.error("根据组织查询用户异常", e);
        }
        return result;
    }

    /**
     * 获取当前登录用户id(切换后取对应账号的id)
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String getLoginUserId(){
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userId = loginUser.getUser().getUserId();
        //是否切换客户视角，后续完善后从redis取
        Boolean switchFlag = SecurityUtils.isSwitch();
        if(switchFlag){
            SysUser user = sysMapper.selectUserOnlyByUserName(loginUser.getUser().getUserName());
            userId = user.getUserId();
        }
        return userId;
    }

}
