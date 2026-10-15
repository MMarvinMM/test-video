package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.usertree.OrderInfoUserTreeReq;
import com.chinatower.event.vo.UsersVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基础库查询mapper
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:28
 */
@Mapper
public interface SysMapper {
    /**
     * 根据角色ID集合查询所属用户
     * @param tenantId
     * @param roleIdList
     * @param orgIdList
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysInfo> queryUserInfoByRoleId(@Param("tenantId") String tenantId, @Param("roleList") List<String> roleIdList, @Param("orgList") List<String> orgIdList);

    /**
     * 查询用户名
     * @param userIdList
     * @return
     */
    //@DS("db1")
    //List<SysUser> queryUsers(List<String> userIdList);

    /**
     * 根据租户ID查询租户集合
     * @param tenantIdList
     * @return
     */
    //@DS("db1")
    //List<SysTenantInfo> queryTenantList(List<String> tenantIdList);


    /**
     * 根据租户ID查询组织Id集合
     * @param tenantId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> queryDeptId(String tenantId);
    /**
     * 根据租户ID查询组织Id集合
     * @param model
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> queryDeptIdNew(FlowLinkAttrModel model);

    /**
     * 根据租户ID查询组织Id集合（不包括权限七日面）
     * @param model
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> queryOrderInfoDeptId(@Param("param") OrderInfoUserTreeReq model);


    /**
     * 根据租户ID查询有效根组织
     * @param tenantId
     * @return
     */
    SysDept getTenantRootDept(@Param("tenantId") String tenantId);

    /**
     * 获取组织
     * @param deptIdList
     * @return
     */
    List<SysDept> queryDepts(@Param("list") List<String> deptIdList);

    /**
     * 查询用户信息
     * @param usersVO
     * @return
     */
    List<UsersVO> queryUser(UsersVO usersVO);

    /**
     * 查询租户信息
     * @param tenantId
     * @param tenantName
     * @return
     */
    List<SysTenantInfo> queryTenantInfoList(@Param("tenantId") String tenantId, @Param("tenantName") String tenantName);

    /**
     * 获取行业对应的版本信息列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/29
     * @version
     */
    List<SysTenantTrade> queryVerCodeByIndustry(@Param("tenantId") String tenantId, @Param("tradeArray") String[] tradeArray, @Param("appVerCode") String appVerCode);

    /**
     * 查询租户下所有角色
     * @return
     */
    List<SysInfo> queryTenantRoles();

    /**
     * 查询租户下所有角色
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<UserInfoDto> queryUserListInfo(List<String> userIdList);

    /**
     * 查询租户下所有角色
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<UserInfoDto> queryUserListInfoByUserNameList(List<String> userNameList);

    /**
     * 查询部门父节点列表
     *
     * @return
     * @author chenmf
     * @date 2022/4/14
     * @version
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> getDeptParentId(List<String> rootDeptIdList);

    @DS(EbikeDataBaseConstant.BASE)
    String queryUserById(@Param("userId") String userId);

    @DS(EbikeDataBaseConstant.BASE)
    String selectTenantName(@Param("tenantId") String tenantId);

    List<SysArea> getSysAreaListByCodes(Set<String> areaCodes);

    /**
     * 根据行业编码查询
     * @param industryCode
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SysIndustryConf getByIndustryCode(@Param("industryCode") String industryCode,@Param("localNode") String localNode);

    /**
     * 根据组织查询用户
     * @param deptId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SysUser> selectUserByDeptId(String deptId);

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SysUser selectUserOnlyByUserName(String userName);

    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    SysUser selectUserOnlyByUserId(@Param("userId") String userId);

    /**
     * 根据用户id查询用户执法信息
     * @param userId
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    Map selectEnforcementInfoByUserId(String userId);
}
