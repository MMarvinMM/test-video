package com.chinatower.platform.project.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.api.entity.GridKeeperBaseVO;
import com.chinatower.bdm.api.entity.GridKeeperVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.project.custom.entity.DeptStreetVO;
import com.chinatower.platform.project.system.domain.AreaDetailInfo;
import com.chinatower.platform.project.system.domain.AreaStreetInfo;
import com.chinatower.platform.project.system.domain.dto.SysUserHisMongoDTO;
import com.chinatower.platform.project.system.domain.dto.UserQueryDTO;
import com.chinatower.system.api.model.AreaDataVO;
import com.chinatower.system.api.resp.SysUserTenantAreaResponse;
import com.chinatower.system.api.resp.SysUserTenantInfoResponse;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户表 数据层
 *
 * @author chinatower
 */
public interface SysUserMapper {
    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询用户列表,按照姓名首字母排序
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserListOrderByName(SysUser sysUser);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @DataScope(industryCode = "d.INDUSTRY_CODE",deptColumn = "d.DEPT_ID",acceptColumn = "ic.accept_node")
    SysUser selectUserByUserName(UserQueryDTO dto);


    /**
     * 通过用户名查询用户(不区分权限)
     *
     * @param dto 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserNameNoAuth(UserQueryDTO dto);

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    SysUser selectUserOnlyByUserName(String userName);

    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    @DS("db1")
    SysUser selectUserOnlyByUserId(String userId);

    /**
     * 根据用户ID批量查询用户信息
     * @param userIds userIds
     * @return
     */
    List<SysUser> selectUserOnlyByUserIds(String[] userIds);

    @DataScope(industryCode = "d.INDUSTRY_CODE",deptColumn = "d.DEPT_ID")
    SysUser selectUserWithPwdByUserName(UserQueryDTO userName);

    SysUser selectByUserName(UserQueryDTO userName);

    @DataScope(industryCode = "d.INDUSTRY_CODE",deptColumn = "d.DEPT_ID")
    SysUser selectDeptByDeptIds(UserQueryDTO userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(UserQueryDTO userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUser user);


    /**
     * 同步修改网格员信息
     *
     * @param gridKeeperBaseVO 网格员信息
     * @return 结果
     */
    int updateGridkeeper(GridKeeperBaseVO gridKeeperBaseVO);

    /**
     * 根据用户id修改用户状态
     * @param user 用户对象
     * @return 结果
     */
    int updateUserStatus(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     *
     * @param userId 用户编码
     * @param password 密码
     * @return 结果
     */
    int updateUserPassword(SysUser user);

    /**
     * 密码重置接口
     * @param user
     * @return
     */
    int resetUserPassword(SysUser user);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserById(String userId);

    /**
     * 批量删除用户信息
     *
     * @param sysUser 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(SysUser sysUser);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    int checkUserNameUnique(String userName);

    /**
     * 根据租户id查询用户表中是否已存在该租户管理员账号
     * @param tenantId 租户id
     * @return
     */
    int checkTenantAdminUnique(String tenantId);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    SysUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    SysUser checkEmailUnique(String email);

    /**
     * 根据部门和角色查询用户信息
     * @param deptId 部门id
     * @param roleKey 角色标识
     * @return 用户信息
     */
    List<SysUser> selectUserByDeptAndRole(@Param("deptId") String deptId, @Param("roleKey") String roleKey);

    /**
     * 根据用户id集合查询用户信息列表
     *
     * @param userIds 用户id集合
     * @return 用户信息列表
     */
    List<SysUser> selectUserByIds(String[] userIds);

    /**
     * 根据用户username集合查询用户信息列表
     *
     * @param array    用户id集合
     * @param platformCode
     * @return 用户信息列表
     */
    List<SysUser> getListByUserNames(@Param("array") String[] array, @Param("platformCode") String platformCode);

    List<String> selectUserNamesByIds(String[] longIds);

    @Select("select count(1) from SYS_USER")
    int selectCount();

    @Select(" select tt.* from (" +
            " SELECT t.USER_ID, t.AVATAR,rownum AS rowno FROM SYS_USER t WHERE ROWNUM <= #{pageNumber} * #{pageSize} " +
            " )  tt where  tt.rowno>(#{pageNumber}-1)*#{pageSize}")
    List<SysUser> selectPageInfo(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);

    int updateUserLoginFailNum(@Param("userName") String userName, @Param("num") int num);

    /**
     * 根据租户id查询用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/23
     * @version
     */
    List<SysUser> selectUserByTenantId(@Param("tenantId") String tenantId);

    /**
     *
     * @return 获得租户下所有用户信息（区域、角色、性别）
     */
    List<SysUserTenantInfoResponse> selectUserListByTenantId(@Param("tenantId") String tenantId,
                                                             @Param("industryCode")String industryCode);
    /**
     *
     * @param deptId
     * @return 查询部门区域
     */
    List<SysUserTenantAreaResponse> selectUserAreas(@Param("deptId")Long deptId,
                                                    @Param("tenantId") String tenantId,
                                                    @Param("industryCode")String industryCode);
    /**
     *
     * @param tenantId  总数量
     * @return 获得租户下所有用户信息（区域、角色、性别）
     */
    long selectUserListByTenantIdCount(@Param("tenantId") String tenantId);

    /**
     * 通过用户账号删除用户
     *
     * @param userName 用户账号
     * @return 结果
     */
    int deleteUserByName(String userName);


    /**
     * 根据账号修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserByUserName(SysUser user);

    /**
     * 根据用户角色和租户ID获取列表
     * @param roleId
     * @param tenantId
     * @return
     */
    List<SysUser> getUserInfos(@Param("roleId")Long roleId,@Param("tenantId") String tenantId);


    /**
     * @Author：liuxt
     * @Description：获取用户列表数据
     * @Date：2022/8/5 11:34
     * @param user
     * @return List<SysUser>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-单点登录管理_需求
     */
    List<SysUser> getUserInfosTable(SysUser user);

    /**
     * 查询登录账号权限范围内的行政区划树
     * @return
     */
    @DataScope(deptColumn = "a.DEPT_ID",tenantColumn = "a.TENANT_ID",appVerCode = "a.app_ver_code",industryCode = "a.industry_code")
    List<DeviceTreeVO> getRegionTree(SysUser user);


    List<DeviceTreeVO> getAllAreaList(@Param("tenantId") String tenantId);


    List<AreaDataVO> getAreaByTenantId(@Param("tenantId") String tenantId);


    /**
     * 获取登录账号数据权限
     * @return
     */
    List<DeviceTreeVO> getPermissions(@Param("userId") String userId,@Param("dept") List<String> dept);
    /**
     * 获取登录账号数据权限(返回liststring)
     * @return
     */
    List<String> getPermissionsForString(@Param("userId") String userId,@Param("dept") List<String> dept);


    /**
     * 查询登录账号权限范围内的行政区划树
     * @return
     */
    List<SysUser> getPhoneByRegion(@Param("appVerCode")String appVerCode,@Param("areaCode") String areaCode,@Param("tenantId") String tenantId,@Param("industryCode")String industryCode);


    @Select("select password from sys_user where user_name=#{userName} and del_flag='0'")
    String selectPwdByUserName(String userName);

    List<SysIndustryViewRel> selectUserAppVerCodesByUserId(@Param("userId") String userId ,@Param("platformCode") String platformCode);

    List<SysUser> selectUserAndRolesByIds(@Param("array")String[] array,@Param("platformCode")String platformCode);

    @MapKey("userName")
    Map<String, SysUser> getMapByUserNames(Collection<String> nameList);


    SysUser selectUptimeById(String userId);

    SysUser selectUserInfoByUserName(@Param("userName")String userName);

    int countAdminUserNameByTenantId(@Param("tenantId") String tenantId);

    String selectAdminUserNameByTenantId(@Param("tenantId") String tenantId);

    int countOrderInfoByTenantId(@Param("tenantId") String tenantId, @Param("userName") String userName);

    int deleteByUserName(@Param("tenantId") String tenantId, @Param("userName") String userName);

    /**
     * 通过用户id获取网格员信息
     * @param gridKeeperVO
     * @return
     */
    GridKeeperVO getKeeperInfoOnlyByUserId(GridKeeperVO gridKeeperVO);

    /**
     * 获取租户所有用户(租户+行业)
     * @param tenantId
     * @param industryCode
     * @return
     */
    List<SysUser> getTenantUsers(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 获取租户所有用户(租户+行业)，包括已经删除的用户
     * @param tenantId
     * @param industryCode
     * @return
     */
    List<SysUser> getTenantAllUsers(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);


    /**
     * 查询登录账号权限范围内的行政区划树
     * @return
     */
    @DataScope(deptColumn = "a.DEPT_ID",tenantColumn = "a.TENANT_ID",industryCode = "a.industry_code")
    List<DeptStreetVO> getRegionTreeNew(DeptStreetVO deptStreetVO);

    String queryNodeAlias(@Param("nodeCode") String nodeCode);


    @DataScope(deptColumn = "a.DEPT_ID",tenantColumn = "a.TENANT_ID",industryCode = "a.industry_code")
    List<AreaDetailInfo> getAreaInfoList(DeptStreetVO deptStreetVO);

    @DataScope(deptColumn = "a.DEPT_ID",tenantColumn = "a.TENANT_ID",industryCode = "a.industry_code")
    List<AreaDetailInfo> getFistAreaInfoList(DeptStreetVO deptStreetVO);

    @DataScope(deptColumn = "a.DEPT_ID",tenantColumn = "a.TENANT_ID",industryCode = "a.industry_code")
    List<DeptStreetVO> getAreaInfoByKeyWordsList(DeptStreetVO deptStreetVO);

    @DataScope(deptColumn = "a.DEPT_ID",tenantColumn = "a.TENANT_ID",industryCode = "a.industry_code")
    List<AreaStreetInfo> getAreaStreetList(DeptStreetVO deptStreetVO);

    /**
     * 获取租户所有用户
     */
    List<SysUser> getTenantUsersByTenantId(@Param("tenantId") String tenantId);

    List<SysUserHisMongoDTO> selectByUserIdList(@Param("userIdList")List<String> userIdList);

    int deletePhysicalByUserIdList(@Param("userIdList")List<String> userIdList);
}


