package com.chinatower.platform.auth.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.platform.api.entity.SysLogininfoEntity;
import com.chinatower.platform.auth.project.mapper.SysLogininforMapper;
import com.chinatower.platform.auth.project.mapper.SysUserMapper;
import com.chinatower.platform.auth.project.service.ISysLogininforService;
import com.chinatower.platform.entity.admin.SysLogininfor;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author bruce
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService
{

    @Resource
    private LogFeign logFeign;

    @Resource
    private SysUserMapper userMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor)
    {
        SysUser user = userMapper.selectUserOnlyByUserName(logininfor.getUserName());
        if (user != null){
            logininfor.setPhonenumber(user.getPhonenumber());
            logininfor.setUserType(user.getUserType());
            logininfor.setDeptId(user.getDeptId());
            logininfor.setTenantId(user.getTenantId());
            logininfor.setNickName(user.getNickName());
        }
        logininfor.setLoginTime(new Date());
        logininfor.setMsg(StrUtil.sub(logininfor.getMsg(),0,200));
        SysLogininfoEntity sysLogininfoEntity = new SysLogininfoEntity();
        BeanUtils.copyProperties(logininfor,sysLogininfoEntity);
        //add by liuxt 20231115
        String tenantName = userMapper.getTenantName(logininfor.getTenantId());
        sysLogininfoEntity.setTenantName(tenantName);
        if(Constants.ZERO.equals(logininfor.getUserType())){
            sysLogininfoEntity.setDeptId(user.getDeptId());
        }else{
            sysLogininfoEntity.setDeptId(null);
        }
        logFeign.recordLoginInfo(sysLogininfoEntity);
    }

}
