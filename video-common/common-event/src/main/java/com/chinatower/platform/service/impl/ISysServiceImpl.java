package com.chinatower.platform.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.ISysUserMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/6 16:54
 */

@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class ISysServiceImpl implements SysService {

    @Autowired
    private ISysUserMapper sysMapper;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String getLoginUserId() {
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
