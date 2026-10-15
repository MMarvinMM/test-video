package com.chinatower.platform.auth.project.service;


import com.chinatower.auth.api.entity.AuthReplaceTokenVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.auth.model.*;
import com.chinatower.platform.auth.project.entity.dto.CheckPasswordDTO;
import com.chinatower.platform.auth.project.entity.dto.ForgotPasswordDTO;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysDeptStreetRel;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.even.SysIndustryViewRel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户 业务层
 *
 * @author bruce
 */
public interface ISysUserService {

    /**
     * 通过用户名查询用户
     * 返回用户信息中不包含密码
     *
     * @param userName 用户名
     * @return 用户对象信息
     */

    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户名查询用户
     * 返回用户信息中包含密码
     *
     * @param userName 用户名
     * @param tradeId 租户id
     * @return 用户对象信息
     */
    SysUser selectUserWithPwdByUserName(String userName,String tradeId);
    /**
     * 通过用户id查询用户
     * 返回用户信息中包含密码
     *
     * @param userId 用户id
     * @param tradeId 租户id
     * @return 用户对象信息
     */
    SysUser selectUserWithPwdByUserId(String userId,String tradeId);

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(SysUser user);

    /**
     * 获取铁塔管理员下对应的租户管理员信息
     * @param sysUser
     * @return
     */
    List<SysUser> getTenantUsers(SysUser sysUser);

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    SysUser selectUserByUserId(String userId);

    /**
     * 查询当前用户选择的组织
     * @param sysUser
     * @return
     */
    SysDept selectCurrentDept(SysUser sysUser);

    /**
     * 获取登录账号数据权限
     * @param user
     * @return
     */
    List<Map<String,Object>> getPermissions(SysUser user);

    List<TreeSelect> selectAreaTree(String deptId);

    List<SysDeptStreetRel> selectAreaList(String deptId);

    List<Map> selectALLDeptListByDeptId(SysDept dept);

    List<Map> selectALLStreetListByDeptId(SysDept dept);

    List<Map> selectALLDeviceListByDeptId(SysDept dept);

    List<Map> selectALLWeixingListByDeptId(SysDept dept);

    Result forgetUpdate(ForgotPasswordDTO dto);

    boolean validateSwitchUser(SwitchUserDTO user);

    /**
     * 通过用户名判断当前用户状态
     *
     * @param username
     * @return
     */
    Result getUserStateByUserName(String username);

    /**
     * 判断用户名和手机号是否匹配
     *
     * @return
     */
    Result getStateByUserNameAndPhone(CheckPasswordDTO dto);

    /**
     * 发送手机验证码
     *
     * @return
     */
    Result sendVerifyCode(CheckPasswordDTO dto);

    /**
     * 校验手机验证码 - 忘记密码
     *
     * @param username
     * @param verifyCode
     * @return
     */
    Result checkVerifyCode(String username, String verifyCode);

    /**
     * 校验输入的新密码和确认密码
     *
     * @param dto
     * @return
     */
    Result checkPasswordRepeat(ForgotPasswordDTO dto);

    /**
     * 在‘忘记密码’中更新用户密码
     *
     * @param dto
     * @return
     */
    Result updateByForgerPassword(ForgotPasswordDTO dto);

    Result ssoLogin(String uuid,String appFlag);

    List<SysIndustryViewRel> selectUserAppVerCodesByDeptId(String userDeptId);

    Result prepareToken(String username, String password, String appId);

    Result sendMsg2User(String phone, String templateKey);

    Result<QrLoginDTO> getQrCode();

    Result<QrLoginDTO> checkQrCode(QrLoginReq qrLoginDTO);

    Result<String> updateQrInfo(QrLoginDTO qrLoginDTO);

    Result doQrLogin(QrLoginReq qrLoginDTO);

    Result<String> doSwitchLogin(Map<String, String> param);

    Result<SwitchLoginVo> checkSwitchId(String uuid);

    String checkPlatformCode(String userName);

    String tokenChange(Map<String, Object> map);

    Result userIdLogin(String token);

    Result checkOnlyPasswordRepeat(ForgotPasswordDTO dto);

    Result sdkToApiLogin(String userId);

    Result doUserIdLogin(AuthReplaceTokenVO replaceTokenVO);
}
