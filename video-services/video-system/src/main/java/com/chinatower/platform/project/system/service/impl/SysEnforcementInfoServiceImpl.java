package com.chinatower.platform.project.system.service.impl;

import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.mapper.*;
import com.chinatower.platform.project.system.service.*;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;




/**
 * 用户执法信息 业务层处理
 *
 * @author zhang.xionghui
 */
@Slf4j
@Service
public class SysEnforcementInfoServiceImpl implements ISysEnforcementInfoService {

    @Autowired
    private SysEnforcementInfoMapper enforcementInfoMapper;
    @Autowired
    private SysDictDataMapper dictDataMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 根据条件分页查询用户执法信息列表
     *
     * @param enforcementInfo 用户执法信息
     * @return 用户执法信息集合信息
     */
    @Override
    @DataScope(deptColumn = "d.dept_id",tenantColumn ="u.tenant_id",industryCode = "d.INDUSTRY_CODE")
    public List<SysEnforcementInfo> selectEnforcementInfoList(SysEnforcementInfo enforcementInfo) {
        List<SysEnforcementInfo> list = enforcementInfoMapper.selectEnforcementInfoList(enforcementInfo);
        return list;
    }


    /**
     * 通过ID查询用户执法信息
     *
     * @param sysEnforcementInfo 用户执法信息
     * @return 用户执法信息
     */
    @Override
    @DataScope(deptColumn = "d.dept_id",tenantColumn ="u.tenant_id",industryCode = "d.INDUSTRY_CODE")
    public SysEnforcementInfo selectEnforcementInfoById(SysEnforcementInfo sysEnforcementInfo) {
        SysEnforcementInfo enforcementInfo = enforcementInfoMapper.selectEnforcementInfoById(sysEnforcementInfo);
        return enforcementInfo;
    }


    /**
     * 新增或更新用户执法信息
     *
     * @param enforcementInfo 用户执法信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateEnforcementUser(SysEnforcementInfo enforcementInfo) {
        int rows = 0;
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userName = null;
        String tenantId = null;
        if(currentUser != null && !StringUtils.isEmpty(currentUser.getUser().getTenantId())){
            SysUser user =sysUserMapper.selectUserOnlyByUserId(currentUser.getUser().getUserId());
            userName = user.getUserName();
            tenantId = currentUser.getUser().getTenantId();
        }

        String adoptStr = null;
        String loginStr = null;
        List<SysDictData> adoptList = dictDataMapper.selectDictDataByType(Constants.ENFORCEMENT_ADOPT);
        for(SysDictData sysDictData :adoptList){
            if(!StringUtils.isEmpty(sysDictData.getDictLabel()) && sysDictData.getDictLabel().equals(tenantId)){
                adoptStr = sysDictData.getDictValue();
            }
        }
        List<SysDictData> loginList = dictDataMapper.selectDictDataByType(Constants.ENFORCEMENT_LOGIN);
        for(SysDictData sysDictData :loginList){
            if(!StringUtils.isEmpty(sysDictData.getDictLabel()) && sysDictData.getDictLabel().equals(tenantId)){
                loginStr = sysDictData.getDictValue();
            }
        }
        //根据用户id查执法信息
        SysEnforcementInfo sysEnforcementInfo = enforcementInfoMapper.selectById(enforcementInfo.getUserId());
        // 存在则更新执法信息
        enforcementInfo.setAdopt(adoptStr);
        enforcementInfo.setLogin(loginStr);
        if (sysEnforcementInfo != null){
            enforcementInfo.setUpdateBy(userName);
            rows = enforcementInfoMapper.updateEnforcementInfo(enforcementInfo);
        }else{
            // 否则新增执法信息
            enforcementInfo.setCreateBy(userName);
            rows = enforcementInfoMapper.insertEnforcementInfo(enforcementInfo);
        }
        return rows;
    }


}
