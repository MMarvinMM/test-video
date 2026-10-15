package com.chinatower.platform.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.ServletUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.constant.HttpStatus;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.HttpServletUtils;
import com.chinatower.platform.common.utils.security.DESUtil;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.CommonConstants;
import com.chinatower.platform.constants.DeptConstants;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全服务工具类
 *
 * @author chinatower
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class SecurityUtils {
    private static final String GET_USER_ERR_MSG = "获取用户账户异常";
    public static final String SWITCH_USER_KEY = "switch_user:";

    @Autowired(required = false)
    private AuthBaseMapper authBaseMapper;
    private SecurityUtils() {
    }

    /**
     * 获取实际登录人
     */
    public static LoginUser getActualLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (SecurityUtils.isSwitch()) {
            return loginUser.getSwitchdLoginUser();
        }
        return loginUser;
    }

    /**
     * 获取操作人姓名
     */
    public static String getOperatorNickname() {
        return SecurityUtils.getActualLoginUser().getUser().getNickName();
    }

    /**
     * 获取操作人
     */
    public static String getOperator() {
        return SecurityUtils.getActualLoginUser().getUsername();
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            return "admin";
        }
    }

    /**
     * 是否已切换
     * @return
     */
    public static boolean isSwitch(){
        LoginUser loginUser = getLoginUser();
        boolean flag = false;
        if(loginUser.getSwitchdLoginUser() != null){
            Long exTime = RedisUtil.get(SWITCH_USER_KEY +getLoginUserId(),Long.class, RedisDBType.PRIMARY);
            long currentTime = System.currentTimeMillis();
            if (exTime != null) {
                if (-1 == exTime || (exTime - currentTime > 0)) {
                    flag = true;
                }
            }
        }
        return  flag;
    }

    /**
     * 是否已切换-透传loginUser避免在此获取当前登录人
     * @return
     */
    public static boolean isSwitch(LoginUser loginUser){
        boolean flag = false;
        if(loginUser.getSwitchdLoginUser() != null){
            Long exTime = RedisUtil.get(SWITCH_USER_KEY +getLoginUserId(),Long.class, RedisDBType.PRIMARY);
            long currentTime = System.currentTimeMillis();
            if((exTime != null) && (exTime -currentTime > 0)){
                flag = true;
            }
        }
        return  flag;
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        LoginUser user = null;
        try {
            String loginUserId = getLoginUserId();
            log.info("AuthorizationManager ==> getLoginUser:loginUserId ==" + loginUserId);
            if(StringUtils.isBlank(loginUserId)){
                return user;
            }
            String cacheKey = com.chinatower.common.core.constant.CacheConstants.USER_INDUSTRY_CODE + loginUserId;
            String industryCode = HttpServletUtils.getRequestHeader(FeignConstants.HEADER_INDUSTRY_CODE);
            log.info("AuthorizationManager ==> getLoginUser: 1 get header industryCode :cache key:{},industryCode:{}", cacheKey,industryCode);
            if (StringUtils.isBlank(industryCode)) {
                // 如果请求头中不存在，则取最后一次选择的行业code
                String industryCodeKey = com.chinatower.common.core.constant.CacheConstants.USER_LAST_INDUSTRY_CODE + loginUserId;
                industryCode = RedisUtil.get(industryCodeKey, RedisDBType.PRIMARY);
                log.info("AuthorizationManager ==> getLoginUser: 2 get cache industryCode :cache key:{},industryCode:{}", cacheKey,industryCode);
            }
            // 再次判断行业code是否存在
            if (StringUtils.isBlank(industryCode)) {
                Map<Object, Object> objectObjectMap = RedisUtil.hGetAll(cacheKey, RedisDBType.PRIMARY);
                //map无序，只能随便获取了
                industryCode = new ArrayList<>(objectObjectMap.keySet()).get(0).toString();
                log.info("AuthorizationManager ==> getLoginUser: 3 get random industryCode :cache key:{},industryCode:{}", cacheKey,industryCode);
            }

            log.info("AuthorizationManager ==> getLoginUser: 4 final industryCode :cache key:{},industryCode:{}", cacheKey,industryCode);
            String userCacheStr = RedisUtil.hGet(cacheKey, industryCode, RedisDBType.PRIMARY);
            if (StrUtil.isNotBlank(userCacheStr)) {
                user = JSON.parseObject(userCacheStr, LoginUser.class);
            }

            return user;
        } catch (Exception e) {
            log.error("获取用户失败", e);
            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * 获取用户且包含设备信息
     **/
//    public LoginUser getLoginUserAndDevList() {
//        try {
//            LoginUser loginUser = RedisUtil.get(Constants.LOGIN_TOKEN_KEY + getLoginUserId(), LoginUser.class, RedisDBType.PRIMARY);
//            List<String> deviceCodes = new ArrayList<String>();
//            SysUser sysUser = new SysUser();
//            sysUser.setUserId(loginUser.getUser().getUserId());
//            deviceCodes = getOrgDevicesByUserId(sysUser);
//            loginUser.getUser().setDeviceCodeList(deviceCodes);
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
//        }
//    }

//    private List<String> getOrgDevicesByUserId(SysUser user ) {
//        //查询用户的租户id
//        SysUser sysUser = authBaseMapper.selectUserById(user.getUserId());
//        sysUser.setRoles(authBaseMapper.selectRoleListByUserId(user.getUserId()));
//        String dept = getUserDeptSql(sysUser);// 拼接部门权限sql
//
//        List<String> list = authBaseMapper.getPermissionsForString(user.getUserId(),dept);
//
//
//        return list;
//
//    }
    /**
     * 拼接部门权限sql
     * @param user
     */
    public static String getUserDeptSql(SysUser user){

        StringBuffer deptSql = new StringBuffer();

        List<SysRole> roles = user.getRoles();
        for(SysRole role : roles){
            String roleDataScope = StrUtil.isNotBlank(role.getDataScope()) ? role.getDataScope() : "3";
            switch (roleDataScope) {
                case "2":
                    String roleDeptInParameter = getRoleDeptInParameter(role.getRoleId());
                    //不为空时,设置该用户对应角色的部门及以下部门数据权限
                    if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(roleDeptInParameter)) {
                        deptSql.append(com.chinatower.platform.common.utils.StringUtils.format(" or {}.{} IN {} ", "u", "dept_id", roleDeptInParameter));
                    }
                    break;
                case "3":
                    deptSql.append(com.chinatower.platform.common.utils.StringUtils.format(" or {}.{} = '{}' ", "u", "dept_id", user.getDeptId()));
                    break;
                case "4":
                    //如果不是根节点,加deptId数据过滤
                    if (!DeptConstants.ROOT_DEPT.equals(user.getDept().getParentId())) {
                        String deptInParameter = getTenantDeptInParameter();
                        //不为空时,设置该用户对应的部门及以下部门数据权限
                        if (com.chinatower.platform.common.utils.StringUtils.isNotEmpty(deptInParameter)) {
                            deptSql.append(com.chinatower.platform.common.utils.StringUtils.format(" or {}.{} IN {}", "u", "dept_id", deptInParameter));
                        }
                        //为空时,设置该用户对应的部门数据权限
                        if (com.chinatower.platform.common.utils.StringUtils.isEmpty(deptInParameter)) {
                            deptSql.append(com.chinatower.platform.common.utils.StringUtils.format(" or {}.{} = '{}' ", "u", "dept_id", user.getDeptId()));
                        }
                    }
                    break;
            }
        }
        return deptSql.toString();

    }
    private static String getTenantDeptInParameter() {
        List<String> childDeptIdList = SecurityUtils.getTenantChildDeptIdList();
        StringBuffer stringBuffer = new StringBuffer();
        if (CollUtil.isNotEmpty(childDeptIdList)) {
            List<List<String>> partition = ListUtils.partition(childDeptIdList, 900);
            if (CollUtil.isNotEmpty(partition)) {
                for (int i = 0; i < partition.size(); i++) {
                    if (i != partition.size() - 1) {
                        joinDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(") or ");
                    }
                    if (i == partition.size() - 1) {
                        joinDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(")");
                    }
                }
            }
        }
        return stringBuffer.toString();
    }

    private static void joinDeptParameter(List<List<String>> partition, StringBuffer stringBuffer, int i) {
        List<String> strings = partition.get(i);
        stringBuffer.append("(");
        for (String id : strings
        ) {
            stringBuffer.append("'" + id + "',");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    /**
     * 获取该角色对应的部门权限
     * @authot 李峥
     * @return
     */
    private static String getRoleDeptInParameter(Long roleId) {
        List<Long> list = RedisUtil.getList(CacheConstants.ROLE_SCOPE_LIST_PREFIX + roleId, Long.class, RedisDBType.PRIMARY);
        StringBuffer stringBuffer = new StringBuffer();
        if (CollUtil.isNotEmpty(list)) {
            List<List<Long>> partition = ListUtils.partition(list, 900);
            if (CollUtil.isNotEmpty(partition)) {
                for (int i = 0; i < partition.size(); i++) {
                    if (i != partition.size() - 1) {
                        joinRoleDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(") or ");
                    }
                    if (i == partition.size() - 1) {
                        joinRoleDeptParameter(partition, stringBuffer, i);
                        stringBuffer.append(")");
                    }
                }
            }
        }
        return stringBuffer.toString();
    }
    private static void joinRoleDeptParameter(List<List<Long>> partition, StringBuffer stringBuffer, int i) {
        List<Long> strings = partition.get(i);
        stringBuffer.append("(");
        for (Long id : strings
        ) {
            stringBuffer.append(id + ",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    /**
     * 获取用户redis的key
     * 注意：切换视角获取的是登录账号的
     **/
    public static String getLoginUserId() {
        try {
            return HttpServletUtils.getRequestHeader(FeignConstants.HEADER_USER_KEY);
        } catch (Exception e) {
        	log.error(GET_USER_ERR_MSG, e);
            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        return DESUtil.encrypt(password);
    }

    /**
     * 解密
     *
     * @param password 密码密文
     * @return 解密
     */
    public static String decryptPassword(String password) {
        return DESUtil.decrypt(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return StringUtils.equalsIgnoreCase(DESUtil.encrypt(rawPassword), encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(String userId) {
        return userId != null && userId.equalsIgnoreCase("1");
    }

    /**
     * 是否拥有某个角色
     *
     * @param roleKey 用户角色标识
     * @return 结果
     */
    public static boolean isHasRole(String roleKey) {
        try {
            List<SysRole> roles = getLoginUser().getUser().getRoles();
            for (SysRole role : roles) {
                if (roleKey.equals(role.getRoleKey())) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
        }
        return false;
    }

    /**
     * 获取用户部门id
     **/
    public static String getDeptId() {
        try {
            LoginUser loginUser = getLoginUser();
            if (null != loginUser) {
                SysUser sysUser = loginUser.getUser();
                if (null != sysUser) {
                    return sysUser.getDeptId();
                }
            }
            return null;
        } catch (Exception e) {
            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取铁塔用户下属部门id集合
     **/
    public static List<String> getChildDeptIdList() {
        try {
            LoginUser loginUser = getLoginUser();
            if (null != loginUser) {
                SysUser sysUser = loginUser.getUser();
                if (null != sysUser) {
                    String deptId = sysUser.getDeptId();
                    return RedisUtil.getList(CacheConstants.DEPT_SCOPE_LIST_PREFIX + deptId, String.class, RedisDBType.PRIMARY);
                }
            }
            return null;
        } catch (Exception e) {
            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取租户用户下属部门id集合
     **/
    public static List<String> getTenantChildDeptIdList() {
        try {
            LoginUser loginUser = getLoginUser();
            if (null != loginUser) {
                SysUser sysUser = loginUser.getUser();
                if (null != sysUser) {
                    String tenantId = sysUser.getTenantId();
                    String deptId = sysUser.getDeptId();
                    return RedisUtil.getList(CacheConstants.DEPT_SCOPE_TENANT_LIST_PREFIX + tenantId + CommonConstants.COLON + deptId, String.class, RedisDBType.PRIMARY);
                }
            }
            return null;
        } catch (Exception e) {
            throw new CustomException(GET_USER_ERR_MSG, HttpStatus.UNAUTHORIZED);
        }
    }
}
