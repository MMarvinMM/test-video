package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.mapper.AlarmFlowConfigMapper;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.ISysService;
import com.chinatower.event.vo.UsersVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基础信息业务接口实现
 * @author chmod
 * @version 1.0
 * @date 2022/3/04 15:22
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class SysServiceImpl implements ISysService {
    @Autowired
    private SysMapper sysMapper;
    @Autowired
    private AlarmFlowConfigMapper alarmFlowConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public SysDept getTenantRootDept(String tenantId) {
        SysDept sysDept = null;
        if (StringUtils.isNotEmpty(tenantId)) {
            sysDept = sysMapper.getTenantRootDept(tenantId);
        }
        return sysDept;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<SysInfo> queryUserInfoByRoleId(String tenantId, List<String> roleIdList, List<String> orgIdList) {
        return sysMapper.queryUserInfoByRoleId(tenantId, roleIdList, orgIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<SysDept> queryDepts(List<String> deptIdList) {
        return sysMapper.queryDepts(deptIdList);
    }

    /**
     * 查询用户信息
     * @param usersVO
     * @return
     */
    @Override
    public List<UsersVO> queryTreeChoose(UsersVO usersVO) {
        UsersVO useVO = new UsersVO();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            useVO.setUserId(getLoginUserId());
        }
        List<UsersVO> returnUsersVO = sysMapper.queryUser(useVO);
        return returnUsersVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<SysTenantInfo> queryTenantInfoList(String tenantId, String tenantName) {
        return sysMapper.queryTenantInfoList(tenantId, tenantName);
    }

    /**
     * 获取行业对应的版本信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/29
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysTenantTrade> queryVerCodeByIndustry(String tenantId, String[] tradeArray, String appVerCode) {
        if (null != tradeArray && tradeArray.length > 0) {
            return sysMapper.queryVerCodeByIndustry(tenantId, tradeArray, appVerCode);
        }
        else {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public List<SysInfo> queryTenantRoles() {
        return sysMapper.queryTenantRoles();
    }

    @Override
    public List<SysArea> getSysAreaListByCodes(Set<String> areaCodes) {
        return sysMapper.getSysAreaListByCodes(areaCodes);
    }

    /**
     * 查询用户名
     *
     * @param userIdList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public List<SysUser> queryUsers(List<String> userIdList) {
        return alarmFlowConfigMapper.queryUsers(userIdList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public List<SysTenantInfo> queryTenantList(List<String> tenantIdList) {
        return alarmFlowConfigMapper.queryTenantList(tenantIdList);
    }


    /**
     * 查询行业信息
     * @param industryCode
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public SysIndustryConf getByIndustryCode(String industryCode,String localNode) {
        SysIndustryConf result = null;
        try {
            result = sysMapper.getByIndustryCode(industryCode,localNode);
        } catch (Exception e) {
            log.error("查询行业配置异常", e);
        }
        return result;
    }

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


    /**
     * 根据用户id查询用户执法信息
     *
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DS(EbikeDataBaseConstant.BASE)
    public Map getLoginUserEnforcementInfo() {
        return sysMapper.selectEnforcementInfoByUserId(getLoginUserId());
    }

    /**
     * 获取当前登录用户id(切换后取对应账号的id)
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String getLoginTowerUserName(){
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getUser().getUserName();
        //是否切换客户视角，后续完善后从redis取
        Boolean switchFlag = SecurityUtils.isSwitch();
        if(switchFlag){
            SysUser user = sysMapper.selectUserOnlyByUserName(loginUser.getUser().getUserId());
            userName = user.getUserName();
        }
        return userName;
    }
}
