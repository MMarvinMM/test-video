package com.chinatower.platform.project.system.service;

import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.custom.entity.DeptStreetVO;
import com.chinatower.platform.project.custom.entity.HeroVO;
import com.chinatower.platform.project.system.domain.AreaDetailInfo;
import com.chinatower.platform.project.system.domain.AreaInfoResultVO;
import com.chinatower.platform.project.system.domain.TenantIdByTowerAccount;
import com.chinatower.platform.project.system.domain.dto.SyncUserDTO;
import com.chinatower.platform.project.system.domain.vo.VerifyUpdateVO;
import com.chinatower.system.api.model.AreaDataVO;
import com.chinatower.system.api.model.SysUser4A;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.request.user.GetUserByDeptAndDeviceScopeRequest;
import com.chinatower.system.api.resp.SysUserTenantInfoResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author iwhalecloud
 */
public interface ISysUserService
{
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 通过用户名查询用户
     * 返回用户信息中不包含密码
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户名查询用户(不区分权限)
     * 返回用户信息中不包含密码
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserNameNoAuth(String userName);

    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    SysUser selectUserOnlyByUserName(String userName);


    /**
     * 通过用户名查询用户
     *返回用户信息中包含密码
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserWithPwdByUserName(String userName);
    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(String userId);
    SysUser selectUserByIdNew(String userId,String industryCode);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param user 用户名
     * @return 结果
     */
    String selectUserRoleGroup(SysUser user);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    String checkUserNameUnique(String userName);

    /**
     * 根据租户id查询用户表中是否已存在该租户管理员账号
     * @param tenantId
     * @return
     */
    String checkTenantAdminUnique(String tenantId);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    String checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    String checkEmailUnique(SysUser user);

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    void checkUserAllowed(SysUser user);

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
     * 修改用户信息,视联使用
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateVideoUser(SysUser user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    boolean updateUserAvatar(String userName, String avatar);

    /**
     * 修改用户密码
     * @param userId 用户编码
     * @param password 密码
     * @return 结果
     */
    int updateUserPassword(String userId, String password);

    /**
     * 用户密码重置
     * @param userId 用户编码
     * @return int
     */
    int resetPassword(String userId);

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
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(String[] userIds);

    /**
     * 导入用户数据
     *
     * @param params 导入参数
     * @return 结果
     */
    List<SysImportDetail> importUser(ImportParams<SysUser> params);

    /**
     * 导入用户数据
     *
     * @param params 导入参数
     * @return 结果
     */
    List<SysImportDetail> importUserData(ImportParams<SysUser> params);


    /**
     * 根据部门和角色查询用户信息
     * @param deptId 部门id
     * @param roleKey 角色标识
     * @return 用户信息
     */
    List<SysUser> selectUserByDeptAndRole(String deptId, String roleKey);

    /**
     * 根据用户id集合查询用户信息列表
     * @param userIds 用户id集合
     * @return 用户信息列表
     */
    List<SysUser> selectUserByIds(String[] userIds);


    /**
     * 根据用户userName集合查询用户信息列表
     * @param userNames 用户userName集合
     * @return 用户信息列表
     */
    List<SysUser> getListByUserNames(String[] userNames);


    String selectUserNamesByIds(String[] userIds);

    int updateUserLoginFailNum(String userName, int num);

    /**
     * 根据租户id查询用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/23
     * @version
     */
    List<SysUser> selectUserByTenantId(String tenantId);

    /**
     *
     * @return 获得租户下所有用户信息（区域、角色、性别）
     */
    List<SysUserTenantInfoResponse> selectUserListByTenantId(Integer pageNum,Integer pageSize);

    /**
     *
     * @return 得租户下所有用户信息（区域、角色、性别） 总数量
     */
    long selectUserListByTenantIdCount();

    /**
     * 新增用户(视联feign接口专用)
     * @param user 用户
     * @return
     */
    int insertVideoUser(SysUser user);

    /**
     * 校验验证码
     * @param phonenumber
     * @param code
     * @return
     */
    boolean checkPhoneCode(String phonenumber, String code);
    /**
     * 查询用户列表
     * @param user
     * @return
     */
    List<SysUser> getUserList(SysUser user);

    /**
     * @Author：liuxt
     * @Description：获取用户列表数据
     * @Date：2022/8/5 11:34
     * @param user
     * @return List<SysUser>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-单点登录管理_需求
     */
    List<SysUser> getUserListTable(SysUser user);

    /**
     * 获取登录账号数据权限(返回liststring)
     * @param user
     * @return
     */
    List<DeviceTreeVO> getPermissions(SysUser user);
    /**
     * 获取登录账号数据权限
     * @param user
     * @return
     */
    List<String> getPermissionsForString(SysUser user);
    /**
     * 查询登录账号权限范围内的行政区划树
     * @param user
     * @return
     */
    List<DeviceTreeVO> getRegionTree(SysUser user);

    /**
     * 判断当前用户是否可以注销
     * @param
     * @return
     */
    AjaxResult getLogoutFlag(SysUser user);

    /**
     * 查询登录账号全部行政区划树
     * @param user
     * @return
     */
    List<AreaDataVO> getAreaByTenantId(SysUser user);



    /**
     * 根据行业编码获取区县人员的手机号码
     * @param user
     * @return
     */
    List<SysUser> getPhoneByRegion(SysUser user);

    /**
     * 公网根据用户名查询手机号
     * @param userName
     * @param h
     * @return
     */
    AjaxResult selectUserByUserNamePub(AddUserRequest userName, String h);

    AjaxResult updatePhone(AddUserRequest addUserRequest);

    AjaxResult updatePassPublic(AddUserRequest addUserRequest);

    /**
     * 修改密码
     * @param key
     * @param password
     * @return
     */
    AjaxResult updatePass(String key,String password,String userName);

    List<SysUser> getUserByDeptAndDeviceScope(GetUserByDeptAndDeviceScopeRequest request);

    /**
     * 用户id查AppVerCodes
     * @param userId
     * @return
     */
    List<SysIndustryViewRel> selectUserAppVerCodesByUserId(String userId,String platformCode);

    /**
     * 获取权限范围内的部门用户树
     */
    List getDeptUserTree(SysUser sysUser,Integer flag);

    VerifyUpdateVO isNeedUpdate();

    /**
     * 同步应用平台客户账号信息
     * @param param 参数
     * @return 结果
     */
    ApiResult<String> synUserInfo(@RequestBody SyncUserDTO param);

    /**
     * 强制退出
     * @param userId 用户id
     * @param userName 用户账号
     */
    void forceLogOut(String userId,String userName);

    /**
     * 获取租户所有用户(租户+行业)
     * @return
     */
    List<SysUser> getTenantUsers(int deleteFlag);

    /**
     * 查询登录账号的管理区域
     * @param deptStreetVO
     * @return
     */
    List<DeptStreetVO> getRegionTreeNew(DeptStreetVO deptStreetVO);

    Integer checkLoginIp(SysUser user) throws Exception;


    /**
     * 推送4a用户（客户）
     *
     * @param user 用户信息
     * @return 结果
     */
    Result<String> userPush4A(SysUser4A user);

    Boolean checkSensitiveWords(String userName);

    List<SysUser> getUserListByDept(SysUser user);

    TenantIdByTowerAccount getTenantIdByTowerAccount();

    /**
     * 获取基本信息
     * @param heroVO
     * @return
     */
    HeroVO getHeroBaseInfo(HeroVO heroVO);

    AreaInfoResultVO queryAreaWideList(DeptStreetVO deptStreetVO);

    List<DeptStreetVO> queryAreaByKeyWordsList(DeptStreetVO deptStreetVO);

    Integer queryAreaStreetList(DeptStreetVO deptStreetVO);
}
