package com.chinatower.platform.project.system.controller;


import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * video-system
 *
 */
@Api(tags = "行业应用获取通用用户信息")
@RestController
@RequestMapping("/system/security")
public class RemoteUserSecurityController {

    /**
     *  获取用户名称
     */
    @PostMapping("/getUsername")
    public SysUser getUsername() {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(SecurityUtils.getUsername());
        return sysUser;
    }

    /**
     *  获取登录用户信息
     */
    @PostMapping("/getLoginUser")
    public LoginUser getLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        loginUser.getUser().setCreateTime(null);
        loginUser.setSwitchdLoginUser(SecurityUtils.getActualLoginUser());
        return loginUser;
    }

    /**
     *  获取用户组织信息
     */
    @PostMapping("/getUserDeptSql")
    public String getUserDeptSql(@RequestBody SysUser user){
        return SecurityUtils.getUserDeptSql(user);
    }

    /**
     *  获取登录用户id
     */
    @PostMapping("/getLoginUserId")
    public String getLoginUserId() {
        return SecurityUtils.getLoginUserId();
    }

    /**
     *  加密密码
     */
    @PostMapping("/encryptPassword")
    public String encryptPassword(@RequestParam("password") String password) {
        return SecurityUtils.encryptPassword(password);
    }

    /**
     *  解密密码
     */
    @PostMapping("/decryptPassword")
    public String decryptPassword(@RequestParam("password") String password) {
        return SecurityUtils.decryptPassword(password);
    }

    /**
     *  匹配密码
     */
    @PostMapping("/matchesPassword")
    public boolean matchesPassword(@RequestParam("rawPassword") String rawPassword,@RequestParam("encodedPassword") String encodedPassword) {
        return SecurityUtils.matchesPassword(rawPassword,encodedPassword);
    }

    /**
     *  是否是管理员
     */
    @PostMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("userId") String userId) {
        return SecurityUtils.isAdmin(userId);
    }

    /**
     *  是否拥有某个角色
     */
    @PostMapping("/isHasRole")
    public boolean isHasRole(@RequestParam("roleKey") String roleKey) {
        return SecurityUtils.isHasRole(roleKey);
    }

    /**
     *  获取组织id
     */
    @PostMapping("/getDeptId")
    public String getDeptId() {
        return SecurityUtils.getDeptId();
    }


    /**
     *  获取铁塔用户下属部门id集合
     */
    @PostMapping("/getChildDeptIdList")
    public List<String> getChildDeptIdList() {
        return SecurityUtils.getChildDeptIdList();
    }

    /**
     *  获取租户用户下属部门id集合
     */
    @PostMapping("/getTenantChildDeptIdList")
    public List<String> getTenantChildDeptIdList() {
        return SecurityUtils.getTenantChildDeptIdList();
    }

}
