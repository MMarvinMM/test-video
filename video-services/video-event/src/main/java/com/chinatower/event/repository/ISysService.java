package com.chinatower.event.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.vo.UsersVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基础信息业务接口
 * @author chmod
 * @version 1.0
 * @date 2022/3/04 15:20
 */
public interface ISysService {
    /**
     * 获取租户根组织
     * @param tenantId
     * @return
     */
    SysDept getTenantRootDept(String tenantId);

    /**
     * 根据条件查询用户信息
     * @param tenantId
     * @param roleIdList
     * @param orgIdList
     * @return
     */
    List<SysInfo> queryUserInfoByRoleId(String tenantId, List<String> roleIdList, List<String> orgIdList);

    /**
     * 获取组织
     * @param deptIdList
     * @return
     */
    List<SysDept> queryDepts(List<String> deptIdList);

    /**
     * 查询用户信息
     * @param usersVO
     * @return
     */
    List<UsersVO> queryTreeChoose(UsersVO usersVO);

    /**
     * 查询租户信息
     * @param tenantId
     * @param tenantName
     * @return
     */
    List<SysTenantInfo> queryTenantInfoList(String tenantId, String tenantName);

    /**
     * 获取行业对应的版本信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/29
     * @version
     */
    List<SysTenantTrade> queryVerCodeByIndustry(String tenantId, String[] tradeArray, String appVerCode);

    /**
     * 查询租户下所有角色
     * @return
     */
    List<SysInfo> queryTenantRoles();

    /**
     * 根据多个areaCode获取区域信息
     * @return
     */
    List<SysArea> getSysAreaListByCodes(Set<String> areaCodes);


    /**
     * 查询用户名
     * @param userIdList
     * @return
     */
    List<SysUser> queryUsers(List<String> userIdList);

    @DS(EbikeDataBaseConstant.BASE)
    List<SysTenantInfo> queryTenantList(List<String> tenantIdList);

    /**
     * 根据行业编码查询
     * @param industryCode
     * @return
     */
    SysIndustryConf getByIndustryCode(String industryCode,String localNode);

    /**
     * 根据组织查询用户
     * @param deptId
     * @return
     */
    List<SysUser> selectUserByDeptId(String deptId);

    /**
     * 获取当前登录用户id(切换后取对应账号的id)
     * @return
     */
    String getLoginUserId();

    /**
     * 查询登录人执法编号信息
     * @return
     */
    Map getLoginUserEnforcementInfo();


    /**
     * 铁塔切租户视角时，获取当前登录铁塔用户的name(通过切换后取对应账号的id查name)
     * @return
     */
    String getLoginTowerUserName();
}
