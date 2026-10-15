package com.chinatower.platform.auth.project.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.auth.api.AuthFeignClient;
import com.chinatower.auth.api.NodeServiceFeignClient;
import com.chinatower.auth.api.SystemApiFeignClient;
import com.chinatower.auth.api.entity.AuthReplaceTokenVO;
import com.chinatower.auth.api.entity.LoginFeignVo;
import com.chinatower.auth.api.entity.SwitchLoginFeignVo;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.constant.UserConstants;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.auth.common.constant.MessageConstant;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.common.constant.RedisConstants;
import com.chinatower.platform.auth.config.AuthProperties;
import com.chinatower.platform.auth.framework.manager.AsyncManager;
import com.chinatower.platform.auth.framework.manager.factory.AsyncFactory;
import com.chinatower.platform.auth.model.*;
import com.chinatower.platform.auth.project.entity.dto.CheckPasswordDTO;
import com.chinatower.platform.auth.project.entity.dto.ForgotPasswordDTO;
import com.chinatower.platform.auth.project.entity.dto.RolePermUrlDto;
import com.chinatower.platform.auth.project.mapper.SysUserMapper;
import com.chinatower.platform.auth.project.service.ISysUserService;
import com.chinatower.platform.auth.service.StoreTokenService;
import com.chinatower.platform.auth.service.TokenService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.constants.CommonConstants;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.CustomSmsTemplateConstants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.entity.request.SmsCodeRequest;
import com.chinatower.platform.enums.SmsResultCode;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.RemoteNodeUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.SmsUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.ReplaceTokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 用户 业务层处理
 *
 * @author bruce
 */
@Slf4j
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private AuthBaseMapper authBaseMapper;

    @Autowired
    private MsgFeignClient msgFeignClient;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    NodeServiceFeignClient nodeServiceFeignClient;

    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthProperties authProperties;

    @Autowired
    private StoreTokenService storeTokenService;

    public static final String VERTICAL_LINE_REGEX = "\\|";

    public static final String PLATFORM_CODE = "platform_code";

    public static final String MIDDLE_LINE = "-";

    public static final String CURRENT_FLAG = "currentFlag";

    public static final String APP_QR_LOGIN_TENANT_PRE = "APP:QR:LOGIN:";

    public static final String ONE = "1";

    public final String GENERAL_URL = "generalInternetUrl";

    public final String REDIRECT_URL = "redirectUrl";

    public final String CURRENT_VERSION = "currentVersion";

    public final String DATE_FORMAT = "yyyyMMddHHmmss";

    public final String ACCESS_TOKEN = "access_token";

    public final String APP_MANAGE = "app_manage:123456";

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public SysUser selectUserWithPwdByUserName(String userName, String tradeId) {
        SysUser sysUser = null;
        try {
            sysUser = userMapper.selectUserWithPwdByUserName(userName, tradeId);
            if (sysUser == null) {
                throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
            }
        } catch (Exception e) {
            log.error("登录失败，请检查用户名或密码是否正确！",e);
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if (sysUser.getRoles() != null) {
            List<Long> ids = sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
            sysUser.setRoleIds(ids.toArray(new Long[ids.size()]));
        }
        return sysUser;
    }

    /**
     * 通过用户id查询用户
     * 返回用户信息中包含密码
     *
     * @param userId  用户id
     * @param tradeId 租户id
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserWithPwdByUserId(String userId, String tradeId) {
        SysUser sysUser = null;
        try {
            sysUser = userMapper.selectUserWithPwdByUserId(userId, tradeId);
            if (sysUser == null) {
                throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if (sysUser.getRoles() != null) {
            List<Long> ids = sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
            sysUser.setRoleIds(ids.toArray(new Long[ids.size()]));
        }
        return sysUser;
    }

    @Override
    public Set<String> getMenuPermission(SysUser user) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdminNew()) {
            roles.add("/**");
//            roles.add("*:*:*");
        } else {
            Map<Long, Set<String>> map = selectRoleMenuPermsByUser(user);
            roles.addAll(map.values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
            for (int i = 0; i < user.getRoles().size(); i++) {
                SysRole sysRole = user.getRoles().get(i);
                sysRole.setPermissions(map.get(sysRole.getRoleId()));
            }
        }
        return roles;
    }


    @Override
    public Set<String> selectMenuPermsByUserId(SysUser user) {
        List<String> permUrls = userMapper.selectMenuPermsByUserId(user);
        Set<String> permsSet = new HashSet<>();
        for (String permUrl : permUrls) {
            if (StrUtil.isNotEmpty(permUrl)) {
                permsSet.addAll(Arrays.asList(permUrl.trim().split(",")));
            }
        }
        return permsSet;
    }

    private Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> permUrls = userMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String permUrl : permUrls) {
            if (StrUtil.isNotEmpty(permUrl)) {
                permsSet.addAll(Arrays.asList(permUrl.trim().split(",")));
            }
        }
        return permsSet;
    }

    private Map<Long,Set<String>> selectRoleMenuPermsByUser(SysUser user){
        List<RolePermUrlDto> list = userMapper.selectMenuPermsByUser(user);
        if(CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        Map<Long,Set<String>> result = new HashMap<>();
        for (RolePermUrlDto dto : list) {
            String permUrl = dto.getPermUrl();
            result.compute(dto.getRoleId(), (k, v) -> {
                if (v == null) {
                    v = new HashSet<>();
                }
                if (StrUtil.isNotEmpty(permUrl)) {
                    v.addAll(Arrays.asList(permUrl.trim().split(",")));
                }
                return v;
            });
        }
        return result;
    }

    @Override
    @DataScope(deptColumn = "a.user_id")
    public List<SysUser> getTenantUsers(SysUser sysUser) {
        return userMapper.getTenantUsers(sysUser);
    }

    //    @DataScope(deptColumn = "d.dept_id")
    @Override
    public List<Map> selectALLDeptListByDeptId(SysDept dept) {
        return userMapper.selectALLDeptListByDeptId(dept);
    }

    @Override
//	@DataScope(deptColumn = "d.dept_id")
    public List<Map> selectALLStreetListByDeptId(SysDept dept) {
        return userMapper.selectALLStreetListByDeptId(dept);
    }

    @Override
//	@DataScope(deptColumn = "d.dept_id")
    public List<Map> selectALLDeviceListByDeptId(SysDept dept) {
        return userMapper.selectALLDeviceListByDeptId(dept);
    }

    @Override
//	@DataScope(deptColumn = "d.dept_id")
    public List<Map> selectALLWeixingListByDeptId(SysDept dept) {
        return userMapper.selectALLWeixingListByDeptId(dept);
    }

    @Override
    public Result forgetUpdate(ForgotPasswordDTO dto) {
        log.info("忘记密码->入参：{}", JSON.toJSONString(dto));
        dto.setUsername(AESUtil.decryptBase64(dto.getUsername()));
        SysUser sysUser = selectUserByUserName(dto.getUsername());
        String phonenumber = sysUser.getPhonenumber();
        Result checkResult = msgFeignClient.checkPhoneCode(phonenumber, dto.getPhoneCode(), "", true);
        if (checkResult.getCode() == SmsResultCode.VERIFY_ERROR.getCode()) {
            return Result.fail("手机验证码错误！");
        }
        if (checkResult.getCode() == SmsResultCode.VERIFY_TIME_OUT.getCode()) {
            return Result.fail(ResultCode.VERIFY_CODE_ERROR.getCode(), "手机验证码失效");
        }
        if (checkResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return checkResult;
        }
        //验证新旧密码是否一致
        String s1 = RsaUtils.decryptByPrivateKey(dto.getNewPassword());
        String s2 = RsaUtils.decryptByPrivateKey(dto.getConfirmPassword());
        if (!s1.equalsIgnoreCase(s2)) {
            return Result.fail("两次密码输入不一致！");
        }

        String newPassword = RsaUtils.decryptByPrivateKey(dto.getNewPassword());
        // 判断不能设置初始密码
        if (UserConstants.PASSWORD.equals(newPassword)) {
            return Result.fail(MessageConstant.PASSWORD_NOT_STANDARD);
        }
        if (SecurityUtils.encryptPassword(newPassword).equalsIgnoreCase(sysUser.getPassword())) {
            return Result.fail("新密码不能与旧密码相同！");
        }
        Pattern pattern = Pattern.compile(OauthConstants.PASSWORD_REGULAR);
        if (!pattern.matcher(newPassword).find()) {
            return Result.fail("密码为8~16位非空字符,包含大小写字母、数字与特殊字符。");
        }

        userMapper.updatePassword(sysUser.getUserId(), SecurityUtils.encryptPassword(newPassword), sysUser.getUserName());
        return Result.success(ResultCode.SUCCESS);
    }

    @Override
    @DataScope(tenantColumn = "u.TENANT_ID")
    public boolean validateSwitchUser(SwitchUserDTO dto) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (SecurityUtils.isSwitch()) {
            user = userMapper.selectUserByUserId(user.getUserId());
            if (user.getUserType().equalsIgnoreCase("0")) {
                SysDept dept = authBaseMapper.getDeptById(user.getDeptId());
                if (!dept.getDeptLevel().equalsIgnoreCase("1")) {
                    SysDept deptById = authBaseMapper.getDeptById(user.getDeptId().substring(0, 6));
                    if(StringUtils.isEmpty(deptById.getDeptLevel())){
                        return false;
                    }
                    deptById.setDeptLevelInt(Integer.parseInt(deptById.getDeptLevel()));
                    List<SysDept> list = authBaseMapper.getDeptByCurrentDeptId(deptById);
                    List<String> tenantIds = authBaseMapper.getTenantByTowerDeptId(list);
                    StringBuffer sb = new StringBuffer(com.chinatower.platform.common.utils.StringUtils.format(" and {} in {} ", "u.tenant_id", joinParameter(tenantIds)));
                    dto.setDataScope(sb.toString());
                } else {
                    dto.setDataScope(null);
                }
            }
        }
        Long aLong = userMapper.validateSwitchUser(dto);
        return (aLong != null) && (aLong > 0);
    }

    private String joinParameter(List<String> deviceCodes) {
        if (deviceCodes == null || deviceCodes.isEmpty()) {
            return "('')";
        }
        StringBuffer deviceStrBuf = new StringBuffer();
        deviceStrBuf.append("(");
        for (String deviceCode : deviceCodes) {
            deviceStrBuf.append("'" + deviceCode + "',");
        }
        deviceStrBuf.deleteCharAt(deviceStrBuf.length() - 1);
        deviceStrBuf.append(")");
        return deviceStrBuf.toString();
    }

    @Override
    public SysUser selectUserByUserId(String userId) {
        return userMapper.selectUserByUserId(userId);
    }

    @Override
    public SysDept selectCurrentDept(SysUser sysUser) {
        return userMapper.selectCurrentDept(sysUser.getDeptId(), sysUser.getIndustryCode(), null);
    }

    /**
     * 获取登录账号用户权限
     *
     * @param user
     * @return
     */
    @Override
    public List<Map<String, Object>> getPermissions(SysUser user) {
        List<Map<String, Object>> list = userMapper.getPermissions(user.getUserId());
        return list;
    }

    @Override
    public List<TreeSelect> selectAreaTree(String deptId) {
        String key = RedisConstants.TENANT_TREE_SELECT_AREA_BY_DEPT + ":" + deptId;
        List<TreeSelect> treeSelects = RedisUtil.getList(key, TreeSelect.class, RedisDBType.PRIMARY);
        if (treeSelects != null) {
            return treeSelects;
        }
        List<SysAreaStreet> returnList = new ArrayList<>();
        List<SysAreaStreet> streeList = userMapper.selectStreeByDeptId(deptId);
        returnList.addAll(streeList);
        while (true) {
            if (streeList != null && streeList.size() > 0) {
                streeList = selectParent(streeList.stream().map(SysAreaStreet::getAreaCode).collect(Collectors.toList()));
                returnList.addAll(streeList);
            } else {
                break;
            }
        }
        //过滤重复的
        returnList = (List) returnList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SysAreaStreet::getAreaCode))), ArrayList::new));
        //等级五以上的去掉后六位
        for (int i = 0; i < returnList.size(); i++) {
            if ("5".equals(returnList.get(i).getAreaLevel())) {
                if (StringUtils.isNotBlank(returnList.get(i).getAreaParentCode()) && returnList.get(i).getAreaParentCode().length() >= 6) {
                    returnList.get(i).setAreaParentCode(returnList.get(i).getAreaParentCode().substring(0, 6));
                }
            } else if ("1".equals(returnList.get(i).getAreaLevel())) {
                if (StringUtils.isNotBlank(returnList.get(i).getAreaCode()) && returnList.get(i).getAreaCode().length() >= 6) {
                    returnList.get(i).setAreaCode(returnList.get(i).getAreaCode().substring(0, 6));
                }
            } else {
                if (StringUtils.isNotBlank(returnList.get(i).getAreaCode()) && returnList.get(i).getAreaCode().length() >= 6) {
                    returnList.get(i).setAreaCode(returnList.get(i).getAreaCode().substring(0, 6));
                }
                if (StringUtils.isNotBlank(returnList.get(i).getAreaParentCode()) && returnList.get(i).getAreaParentCode().length() >= 6) {
                    returnList.get(i).setAreaParentCode(returnList.get(i).getAreaParentCode().substring(0, 6));
                }

            }
        }
        //组织树形结构


        List<TreeSelect> tree = new ArrayList<>();
        for (SysAreaStreet stree : returnList) {
            tree.add(new TreeSelect(stree.getAreaCode(), stree.getAreaName(), stree.getAreaParentCode()));
        }
        List<TreeSelect> treeNodes = TreeUtils.listToTreeSelect(tree);
//        RedisUtil.setEx(treeNodes,1,RedisConstants.TENANT_TREE_SELECT_AREA_BY_DEPT,TreeSelect.class, RedisDBType.PRIMARY);
        RedisUtil.setEx(key, treeNodes, 30, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        return treeNodes;
    }

    @Override
    public List<SysDeptStreetRel> selectAreaList(String deptId) {

        List<SysDeptStreetRel> areaList = userMapper.selectAreaListByDeptId(deptId);
        return areaList;
    }


    public List<SysAreaStreet> selectParent(List<String> list) {
        return userMapper.selectParent(list);
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        //putIfAbsent方法添加键值对，如果map集合中没有该key对应的值，则直接添加，并返回null，如果已经存在对应的值，则依旧为原来的值。
        //如果返回null表示添加数据成功(不重复)，不重复(null==null :TRUE)
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 通过用户名判断当前用户状态
     *
     * @param username
     * @return 成功时包含手机号
     */
    public Result<String> getUserStateByUserName(String username) {
        /*必填参数*/
        if (StringUtils.isEmpty(username)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        /*通过用户名获取手机号*/
        SysUser userObj = selectUserByUserName(username);
        log.info("根据用户名获取用户信息:" + JSON.toJSONString(userObj));
        /*通过手机号判断用户状态*/
        if (userObj == null) {
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        } else {
            if (StringUtils.isBlank(userObj.getPhonenumber())) {
                return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
            }
            if (userObj.getStatus().equalsIgnoreCase("1")) {
                return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
            }
        }
        /*返回带手机号的成功消息*/
        return Result.data(userObj.getPhonenumber());
    }

    /**
     * 判断用户名和手机号是否匹配
     *
     * @return
     */
    public Result getStateByUserNameAndPhone(CheckPasswordDTO dto) {
        String username = dto.getUsername();
        String phone = dto.getPhone();

        /*必填参数*/
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(phone)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }

        /*解密传入的手机号*/
        String decryptPhone = RsaUtils.decryptByPrivateKey(phone);

        //增加用户名加密解密
        if(StringUtils.isNotBlank(dto.getDecryptFlag()) && ONE.equals(dto.getDecryptFlag())){
            username = AESUtil.decryptBase64(dto.getUsername());
        }

        /*判断当前用户状态*/
        Result userResultObj = getUserStateByUserName(username);
        /*判断手机号*/
        if (userResultObj.getCode() == ResultCode.SUCCESS.getCode()) {
            // data返回的是明文手机号
            if (decryptPhone.equals(String.valueOf(userResultObj.getData()))) {
                return Result.success(ResultCode.SUCCESS);
            } else {
                return Result.fail(MessageConstant.USERNAME_PHONE_NOT_MATCH);
            }
        } else {
            return Result.fail(MessageConstant.USERNAME_PHONE_NOT_MATCH);
        }
    }

    /**
     * 发送手机验证码
     *
     * @return
     */
    @Override
    public Result sendVerifyCode(CheckPasswordDTO dto) {

        String username = AESUtil.decryptBase64(dto.getUsername());
        String phone = dto.getPhone();
        /*解密传入的手机号*/
        String decryptPhone = RsaUtils.decryptByPrivateKey(phone);

        if (phone.equals(decryptPhone)) {
            //手机号未加密，安全保护
            log.info("手机号未加密");
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR, MessageConstant.SEND_MSG_FAIL);
        }

        /*判断当前用户状态*/
        Result<String> userResultObj = getUserStateByUserName(username);

        /*发送验证码*/
        if (userResultObj.getCode() == 200) {
            //校验入参手机号和数据库手机号是否一致
            if (!decryptPhone.equals(userResultObj.getData())) {
                log.info("手机号码与账号不匹配");
                return Result.fail(MessageConstant.SEND_MSG_FAIL);
            }

            /*将LoginController中的sendMsg2User直接复制过来使用，不关心处理逻辑*/
            return sendMsg2User(decryptPhone, CustomSmsTemplateConstants.UPDATE_PASSWORD_KEY);

        } else {
            userResultObj.setMsg(MessageConstant.SEND_MSG_FAIL);
            return userResultObj;
        }
    }

    /**
     * 校验手机验证码 -忘记密码
     *
     * @param username
     * @param verifyCode
     * @return
     */
    public Result checkVerifyCode(String username, String verifyCode) {
        log.info("校验验证码入参用户名：{}验证码：{}", username, verifyCode);
        //处理加密产生的空格问题
        username = URLDecoder.decode(username, StandardCharsets.UTF_8)
                .replace(" ", "+");
        /*判断当前用户状态*/
        username = AESUtil.decryptBase64(username);
        Result<String> userResultObj = getUserStateByUserName(username);

        if (userResultObj.getCode() == 200) {
            String phone = userResultObj.getData();
            Result result = msgFeignClient.checkPhoneCode(phone, verifyCode, CustomSmsTemplateConstants.UPDATE_PASSWORD_KEY, true);
            log.info("校验验证码返回:" + JSON.toJSONString(result));
            int checkResult = result.getCode();
            if (checkResult == SmsResultCode.VERIFY_ERROR.getCode()) {
                return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
            } else if (checkResult == SmsResultCode.VERIFY_TIME_OUT.getCode()) {
                return Result.fail(ResultCode.VERIFY_CODE_ERROR.getCode(), MessageConstant.USERNAME_PASSWORD_ERROR);
            } else if (checkResult == ResultCode.SUCCESS.getCode()) {
                //兼容
                String key = getCheckedVerifyKey(phone);
                RedisUtil.set(key, 1, 300, RedisDBType.PRIMARY);
                return Result.success(ResultCode.SUCCESS);
            }
            return result;
        } else {
            return userResultObj;
        }
    }

    private String getCheckedVerifyKey(String phone) {
        return "sms:resetPassword:checked:" + phone;
    }

    /**
     * 校验输入的新密码和确认密码
     *
     * @param dto
     * @return
     */
    public Result checkPasswordRepeat(ForgotPasswordDTO dto) {

        //增加用户名加密解密
        if(StringUtils.isNotBlank(dto.getDecryptFlag()) && ONE.equals(dto.getDecryptFlag())){
            dto.setUsername(AESUtil.decryptBase64(dto.getUsername()));
        }

        SysUser userObj = userMapper.selectUserWithPwdByUserName(dto.getUsername(), "");

        if (userObj == null) {
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        String phonenumber = userObj.getPhonenumber();

        if(StringUtils.isEmpty(phonenumber)){
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        //增加验证码成功判定
        String uniKey = CustomSmsTemplateConstants.UPDATE_PASSWORD_KEY + ":" + phonenumber;
        String smsCheckPossWordKey = "smsCheckPossWordKey:" + uniKey;

        if (!RedisUtil.hasKey(smsCheckPossWordKey, RedisDBType.PRIMARY)) {
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }


        if ("1".equals(userObj.getStatus())) {
            return Result.fail(MessageConstant.USER_FORBIDDEN);
        }


        /*解密后的密码*/
        String newPassword = RsaUtils.decryptByPrivateKey(dto.getNewPassword());
        String confirmPassword = RsaUtils.decryptByPrivateKey(dto.getConfirmPassword());

        if (!confirmPassword.equalsIgnoreCase(newPassword)) {
            return Result.fail(MessageConstant.PASSWORD_NOT_CONFIRMED);
        }

        Pattern pattern = Pattern.compile(OauthConstants.PASSWORD_REGULAR_NEW);
        if (!pattern.matcher(newPassword).find()) {
            return Result.fail(MessageConstant.PASSWORD_NOT_REGULAR);
        }

        // 判断不能设置初始密码
        if (UserConstants.PASSWORD.equals(newPassword)) {
            return Result.fail(MessageConstant.PASSWORD_NOT_STANDARD);
        }

        if (SecurityUtils.encryptPassword(newPassword).equals(userObj.getPassword())) {
            return Result.fail(MessageConstant.PASSWORD_REPEAT);// 新密码不可与旧密码一致
        } else {
            return Result.success(ResultCode.SUCCESS);
        }

    }

    public Result checkOnlyPasswordRepeat(ForgotPasswordDTO dto) {
        //增加用户名加密解密
        if(StringUtils.isNotBlank(dto.getDecryptFlag()) && ONE.equals(dto.getDecryptFlag())){
            dto.setUsername(AESUtil.decryptBase64(dto.getUsername()));
        }
        SysUser userObj = userMapper.selectUserWithPwdByUserName(dto.getUsername(), "");
        if (userObj == null) {
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        //增加验证码成功判定
        String phonenumber = userObj.getPhonenumber();
        if(StringUtils.isEmpty(phonenumber)){
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        String uniKey = CustomSmsTemplateConstants.UPDATE_PASSWORD_KEY + ":" + phonenumber;
        String smsCheckPossWordKey = "smsCheckPossWordKey:" + uniKey;
        if (!RedisUtil.hasKey(smsCheckPossWordKey, RedisDBType.PRIMARY)) {
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if ("1".equals(userObj.getStatus())) {
            return Result.fail(MessageConstant.USER_FORBIDDEN);
        }
        /*解密后的密码*/
        String newPassword = RsaUtils.decryptByPrivateKey(dto.getNewPassword());
        if (SecurityUtils.encryptPassword(newPassword).equals(userObj.getPassword())) {
            return Result.fail(MessageConstant.PASSWORD_REPEAT);// 新密码不可与旧密码一致
        } else {
            return Result.success(ResultCode.SUCCESS);
        }
    }

    /**
     * 在‘忘记密码’中更新用户密码
     *
     * @param dto
     * @return
     */
    public Result updateByForgerPassword(ForgotPasswordDTO dto) {
        SysUser sysUser = null;
        //增加用户名加密解密
        if(StringUtils.isNotBlank(dto.getDecryptFlag()) && ONE.equals(dto.getDecryptFlag())){
            dto.setUsername(AESUtil.decryptBase64(dto.getUsername()));
        }
        //校验验证码
        if (StringUtils.isNotBlank(dto.getPhoneCode())) {
            Result checkVerifyCode = checkVerifyCode(dto.getUsername(), dto.getPhoneCode());
            if (checkVerifyCode.getCode() != 200) {
                return checkVerifyCode;
            }
        } else {
            //兼容
            sysUser = selectUserByUserName(dto.getUsername());
            String key = getCheckedVerifyKey(sysUser.getPhonenumber());
            if (!RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                return Result.fail(MessageConstant.VERIFYCODE_ERROR);
            }
        }
        //检查输入密码
        Result resultObj = checkPasswordRepeat(dto);
        if (resultObj.getCode() != 200) {
            return resultObj;
        }
        /*更新密码*/
        if (sysUser == null) {
            sysUser = selectUserByUserName(dto.getUsername());
        }
        String newPassword = RsaUtils.decryptByPrivateKey(dto.getNewPassword());
        log.info("{}修改密码", dto.getUsername());
        userMapper.updatePassword(sysUser.getUserId(), SecurityUtils.encryptPassword(newPassword), sysUser.getUserName());
        String phone = sysUser.getPhonenumber();

        String verifyKey = CommonConstants.SMS_SUCCESS_KEY + phone;
        /*成功后删除验证码*/
        RedisUtil.delete(verifyKey, RedisDBType.PRIMARY);
        //退出用户
        systemFeignClient.forceLogout(sysUser.getUserId(), sysUser.getUserName());
        return Result.success(ResultCode.SUCCESS);
    }

    @Override
    public Result ssoLogin(String uuid,String appFlag) {
        SysAuthTokenVo authTokenVo = userMapper.selectAuthTokenInfo(uuid);
        if (authTokenVo == null || StringUtils.isBlank(authTokenVo.getUserId()) || authTokenVo.getCreateTime() == null) {
            return Result.fail("认证失败！");
        }

        // 判断授权是否过期
        int authDay = 0;
        AjaxResult ajaxResult = systemFeignClient.getConfValue("ssoAuthDay");
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            String ssoAuthDay = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
            authDay = Integer.valueOf(ssoAuthDay);
        }
        Date authEndDate = DateUtils.addDays(authTokenVo.getCreateTime(), authDay);
        if (authEndDate.before(DateUtils.getNowDate())) {
            return Result.fail("授权已过期！");
        }
        SysUser targetUser = this.selectUserByUserId(authTokenVo.getUserId());
        String userName = targetUser.getUserName();
        String password = SecurityUtils.decryptPassword(targetUser.getPassword());
        String appId = "";
        if(!StringUtils.isEmpty(appFlag) && Constants.ONE.equals(appFlag)){
            appId = APP_MANAGE;
        }else{
            appId = Constants.APP_ID;
        }
        Result ret = this.prepareToken(userName, password, appId);
        return ret;
    }

    @Override
    public List<SysIndustryViewRel> selectUserAppVerCodesByDeptId(String userDeptId) {
        return userMapper.selectUserAppVerCodesByDeptId(userDeptId,SecurityUtils.getLoginUser().getUser().getPlatformCode());
    }

    @Override
    public Result prepareToken(String username, String password, String appId) {
        Result ajax;
        Map<String, Object> tokenMap;
        String clientType="";
        try {
            clientType = appId.split(":")[0];
            tokenMap = tokenService.login(username, password, appId);
            if (null == tokenMap) {
                ajax = Result.fail(RedisConstants.OAUTH_FAIL_MSG);
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, OauthConstants.LOGIN_FAIL, RedisConstants.OAUTH_FAIL_MSG, clientType));
            } else {
                ajax = Result.data(tokenMap, "登录成功！");
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, OauthConstants.LOGIN_SUCCESS, "登录成功", clientType));
            }
        } catch (Exception e) {
            String logMsg = ResultCode.ERROR.getMsg();
            log.error("登录->登录异常：{}", e.getMessage(), e);
            if (e instanceof CustomException) {
                logMsg = e.getMessage();
                ajax = Result.fail(logMsg);
            } else {
                ajax = Result.fail(ResultCode.ERROR);
            }
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, OauthConstants.LOGIN_FAIL, logMsg, clientType));
        }
        return ajax;
    }

    @Override
    public Result sendMsg2User(String phone, String templateKey) {
        String dynamic = SmsUtils.generateDynamic();
        String aesPhone = SmsUtils.generateAesPhone(phone, dynamic);
        SmsCodeRequest request = new SmsCodeRequest();
        request.setMsgType("1");
        //request.setPhone(phone);
        request.setPhone(AESUtil.encryptBase64(phone));
        request.setAesPhone(aesPhone);
        request.setTemplateKey(templateKey);
        Result result = msgFeignClient.sendMessageCheck(request, dynamic);
        if (result.getCode() != ResultCode.SUCCESS.getCode()) {
            log.error("发送短信失败，{}",result);
        }
        return result;
    }

    @Override
    public Result<QrLoginDTO> getQrCode(){
        //二维码标识前缀
        String qrLoginPrefix = this.getConfigValue("qrLoginPrefix");
        //二维码过期时间
        String qrExpireTime = this.getConfigValue("qrExpireTime");
        //当前平台编码
        String platformCode = this.getConfigValue(PLATFORM_CODE);
        if(!StringUtils.isEmpty(qrLoginPrefix) && !StringUtils.isEmpty(qrExpireTime)){
            String code = qrLoginPrefix + Constants.UNDER_LINE + UUID.randomUUID() + Constants.UNDER_LINE + platformCode;

            QrLoginDTO insertVO = new QrLoginDTO();
            insertVO.setStatus(Constants.ZERO);
            insertVO.setCode(code);
            int flag = userMapper.insertQrLogin(insertVO);
            if(flag > Constants.ZERO_NUM){
                //设置缓存过期时间
                RedisUtil.set(code, code,Long.parseLong(qrExpireTime), TimeUnit.MINUTES,RedisDBType.PRIMARY);

                //清理一个月前未成功登录的数据
                log.info("删除条数=" + userMapper.deleteByDate());
            }
            return Result.data(insertVO);
        }else{
            return Result.fail("二维码标识前缀或过期时间未配置！");
        }
    }

    public String getConfigValue(String key){
        String configValue = "";
        AjaxResult ajaxResult = systemFeignClient.getConfValue(key);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            configValue = StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "");
        }
        return configValue;
    }

    @Override
    public Result<QrLoginDTO> checkQrCode(QrLoginReq qrLoginDTO){
        String code = qrLoginDTO.getCode();
        return Result.data(new QrLoginDTO(code,this.getQrStatus(qrLoginDTO.getCode())));
    }

    public String getQrStatus(String code){
        String status = Constants.FOUR;//二维码失效
        QrLoginDTO qrLoginInfo = userMapper.getQrLoginInfo(code);
        //如果redis已过期，则将此条二维码更新为失效状态
        if(RedisUtil.hasKey(code,RedisDBType.PRIMARY)){
            if(!ObjectUtils.isEmpty(qrLoginInfo)){
                status = qrLoginInfo.getStatus();
            }
        }else{
            if(!ObjectUtils.isEmpty(qrLoginInfo) && !Constants.FOUR.equals(qrLoginInfo.getStatus())){
                QrLoginDTO qrLoginDTO = new QrLoginDTO();
                qrLoginDTO.setStatus(status);
                qrLoginDTO.setCode(code);
                userMapper.updateQrLoginInfo(qrLoginDTO);
            }
        }
        return status;
    }

    @Override
    public Result<String> updateQrInfo(QrLoginDTO qrLoginDTO){
        log.info("更新二维码数据入参=" + JSONObject.toJSONString(qrLoginDTO));
        //userId解密
        String userId = RsaUtils.decryptByPrivateKey(qrLoginDTO.getUserId());
        String code = qrLoginDTO.getCode();
        String status = qrLoginDTO.getStatus();
        //如果入参为【扫码成功】或【确认登录】，变更状态前，先校验此二维码是否有效
        if(Constants.ONE.equals(status) || Constants.TWO.equals(status)){
            //认证二维码code是否有效
            QrLoginDTO qrLoginInfo = userMapper.getQrLoginInfo(code);
            Boolean aBoolean = RedisUtil.hasKey(code, RedisDBType.PRIMARY);
            if (
                    //查不到二维码数据
                    ObjectUtils.isEmpty(qrLoginInfo)
                    //二维码已失效
                    || !aBoolean
                    //二维码已被扫
                    || (Constants.ONE.equals(status) && !Constants.ZERO.equals(qrLoginInfo.getStatus()))
                    //二维码已经被扫，但不是入参用户扫的
                    || (Constants.TWO.equals(status) && !StringUtils.isEmpty(qrLoginInfo.getUserId()) && !(userId).equals(qrLoginInfo.getUserId()))

            ) {
                return Result.fail("二维码已失效，请重新扫描！");
            }
            if (StringUtils.isEmpty(qrLoginDTO.getAppLoginNode())) {
                return Result.fail("二维码入参错误，请重新扫描！");
            }
        }

        qrLoginDTO.setUserId(userId);
        HttpServletRequest request = RequestUtils.getRequest();
        String token = request.getHeader("Authorization");
        log.info("获取请求头Authorization:{}", token);
        qrLoginDTO.setToken(token);
        userMapper.updateQrLoginInfo(qrLoginDTO);
        Map<String, String> choose = new HashMap<>();
        choose.put("chooseAppVerCode", qrLoginDTO.getChooseAppVerCode());
        choose.put("chooseTradeId", qrLoginDTO.getChooseTradeId());
        log.info("增加选择行业缓存" + JSON.toJSONString(choose));
        RedisUtil.set(APP_QR_LOGIN_TENANT_PRE + userId, JSON.toJSONString(choose), RedisDBType.PRIMARY);
        return Result.data("更新二维码数据成功！");
    }

    @Override
    public Result doQrLogin(QrLoginReq qrLoginDTO){
        log.info("二维码登录入参=" + JSONObject.toJSONString(qrLoginDTO));
        String code = qrLoginDTO.getCode();
        //认证二维码code是否有效
        QrLoginDTO qrLoginInfo = userMapper.getQrLoginInfo(code);
        Boolean aBoolean = RedisUtil.hasKey(code, RedisDBType.PRIMARY);
        //二维码数据错误或redis数据已过期，则认证失败！
        if (ObjectUtils.isEmpty(qrLoginInfo) || !Constants.TWO.equals(qrLoginInfo.getStatus()) || !aBoolean) {
            log.error(Constants.AUTH_FAIL + "qrLoginInfo=" + JSONObject.toJSONString(qrLoginInfo));
            log.error(Constants.AUTH_FAIL + "haskey=" + aBoolean);
            return Result.fail(Constants.AUTH_FAIL);
        }
        String platformCode = "";
        try {
            AjaxResult ajaxResult = systemFeignClient.getConfValue(PLATFORM_CODE);
            if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    platformCode = sysConfig.getConfigValue();
                }
            }
        }catch (Exception e1){
            log.error("切换视角查询平台编码异常=",e1);
        }


        //增加行业版本
        String tenantVerCode = RedisUtil.get(APP_QR_LOGIN_TENANT_PRE + qrLoginInfo.getUserId(), RedisDBType.PRIMARY);
        JSONObject parse = (JSONObject) JSON.parse(tenantVerCode);
        RedisUtil.delete(APP_QR_LOGIN_TENANT_PRE + qrLoginInfo.getUserId(), RedisDBType.PRIMARY);

        /*
          平台编码一样  原逻辑返回
               不一样  调用下沉节点
         */
        if (platformCode.equals(qrLoginInfo.getAppLoginNode())) {
            Result result = prepareTokenByUserId(qrLoginInfo.getUserId());
            if (ObjectUtil.isNotEmpty(result.getData()) ) {
                Map<String, Object> data = (Map<String, Object>) result.getData();
                data.put(CURRENT_FLAG, "1");
                data.put(CURRENT_VERSION, parse);
            }
            return result;

        } else {
            String str = UUID.randomUUID().toString().replace("-", "");
            String time = DateUtil.format(new Date(), DATE_FORMAT);
            String userId = qrLoginInfo.getUserId();
            String encStr = AESUtil.encryptBase64(str + "-" + time + "-" + userId);
            //远程调用
            Result result = RemoteNodeUtils.create(qrLoginInfo.getAppLoginNode())
                    .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.GENERAL.getCode())
                    .userIdLogin(encStr, qrLoginInfo.getToken());
            if (ObjectUtil.isNotEmpty(result.getData()) ) {
                Map<String, Object> data = (Map<String, Object>) result.getData();
                data.put(CURRENT_VERSION, parse);
            }
            return result;

        }


    }

    @Override
    @Transactional
    public Result<String> doSwitchLogin(Map<String, String> param){
        try {
            String encryptStr = param.get("encryptStr");
            if(StringUtils.isEmpty(encryptStr)){
                return Result.fail(Constants.DO_SWITCH_LOGIN_ERROR + "请求参数错误！");
            }
            //解密入参
            String decryptStr = AESUtil.decrypt(encryptStr);
            ReplaceTokenVO replaceTokenVO = JSONObject.parseObject(decryptStr, ReplaceTokenVO.class);

            LoginUser towerUser = replaceTokenVO.getTowerUser();
            LoginUser switchdLoginUser = towerUser.getSwitchdLoginUser();
            //如果switchdLoginUser不是空，说明原客户切换过视角，
            //如果switchdLoginUser是空，说名原客户没切换过视角，取user里的铁塔账号和密码
            if(!ObjectUtils.isEmpty(switchdLoginUser)){
                towerUser = switchdLoginUser;
            }

            String userName = towerUser.getUser().getUserName();
            String password = SecurityUtils.decryptPassword(towerUser.getUser().getPassword());
            //将铁塔账号信息放到redis中
            //(CacheConstants.TOWER_USER + userName) 目的是不影响62环境，相同账号正常登录
            String towerUserCacheKey = CacheConstants.TOWER_USER + "web_manage:" + (CacheConstants.TOWER_USER + userName);
            RedisUtil.set(towerUserCacheKey, JSON.toJSONString(towerUser), 600 ,TimeUnit.SECONDS,RedisDBType.PRIMARY);

            //模拟登陆
            Map<String, Object> tokenMap = tokenService.getOauth2Token(CacheConstants.TOWER_USER + userName, password, Constants.APP_ID);
            if (null == tokenMap) {
                return Result.fail(RedisConstants.OAUTH_FAIL_MSG);
            }
            storeTokenService.cacheToken(tokenMap);

            ObjectMapper mapper = new ObjectMapper();
            SysUser sysUser = new SysUser();
            //加密
            String encUsername = AESUtil.encryptBase64(replaceTokenVO.getUserName());
            sysUser.setUserName(encUsername);
            sysUser.setChooseTradeId(replaceTokenVO.getChooseTradeId());
            sysUser.setIndustryName(replaceTokenVO.getIndustryName());
            sysUser.setChooseAppVerCode(replaceTokenVO.getChooseAppVerCode());
            String uuid = IdUtils.fastSimpleUUID();
            SwitchLoginVo switchLoginVo = new SwitchLoginVo();
            switchLoginVo.setUuid(uuid);
            switchLoginVo.setToken((String)tokenMap.get(ACCESS_TOKEN));
            switchLoginVo.setRefreshToken((String)tokenMap.get("refresh_token"));
            try {
                switchLoginVo.setJson(mapper.writeValueAsString(sysUser));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            switchLoginVo.setCreator(towerUser.getUser().getUserName());
            userMapper.insertSwitchLogin(switchLoginVo);
            return Result.data(uuid);
        } catch (Exception e) {
            log.error(Constants.DO_SWITCH_LOGIN_ERROR,e);
        }
        return Result.fail(Constants.DO_SWITCH_LOGIN_ERROR);
    }

    @Override
    public Result<SwitchLoginVo> checkSwitchId(String uuid){
        SwitchLoginVo switchLogin = userMapper.getSwitchLogin(uuid);
        if(ObjectUtils.isEmpty(switchLogin)){
            return Result.fail("切换视角uuid失效！");
        }else{
            //用过即销毁
            userMapper.updateSwitchLogin(uuid);
        }
        return Result.data(switchLogin);
    }

    @Override
    public String checkPlatformCode(String tenantId){
        return userMapper.checkPlatformCode(tenantId);
    }

    @Override
    public String tokenChange(Map<String, Object> map) {
        SwitchLoginVo switchLoginVo = new SwitchLoginVo();
        String uuid = IdUtils.fastSimpleUUID();
        switchLoginVo.setUuid(uuid);
        switchLoginVo.setToken((String)map.get(ACCESS_TOKEN));
        switchLoginVo.setRefreshToken((String)map.get("refresh_token"));
        switchLoginVo.setJson(JSON.toJSONString(map));
        String usernameEnc = (String)map.get("user_name");
        String username = AESUtil.decryptBase64(usernameEnc);
        String[] split = username.split(VERTICAL_LINE_REGEX);
        switchLoginVo.setCreator(split[1]);
        userMapper.insertSwitchLogin(switchLoginVo);
        return uuid;
    }

    @Override
    public Result userIdLogin(String token) {
        String decrypt = AESUtil.decryptBase64(token);
        log.info("解密后的信息: {}", decrypt);
        String[] split = decrypt.split(MIDDLE_LINE);
        if (2 >= split.length) {
            throw new com.chinatower.common.core.exception.CustomException("解析错误", 400);
        }
        String time = split[1];
        DateTime parse = DateUtil.parse(time, DATE_FORMAT);
        DateTime now = DateUtil.dateSecond();
        long between = DateUtil.between(parse, now, DateUnit.SECOND);
        if (60 < between) {
            throw new com.chinatower.common.core.exception.CustomException("加密过期", 400);
        }
        String userId = split[2];
        Result result = prepareTokenByUserId(userId);
        if (ObjectUtil.isNotEmpty(result.getData())) {
            Map<String, Object> data = (Map<String, Object>) result.getData();
            data.put(CURRENT_FLAG, "0");

            //生成短token 并且返回
            String uuid = this.tokenChange(data);
            data.put("uuid", uuid);

            try {
                //获取平台编码
                AjaxResult ajaxResult = systemFeignClient.getConfValue(PLATFORM_CODE);
                if (ResultCode.SUCCESS.getCode() == (int) ajaxResult.get(AjaxResult.CODE_TAG)) {
                    SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                    if (!ObjectUtils.isEmpty(sysConfig)) {
                        String platformCode = sysConfig.getConfigValue();
                        //获取跳转地址
                        String str = RedisUtil.get(Constants.COLONY_RELATION_ALL + platformCode, RedisDBType.PRIMARY);
                        if (StringUtils.isNotEmpty(str)) {
                            ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
                            for (ColonyApp app : cascadeConfigSyncVO.getApps()) {
                                if ("general".equals(app.getAppId())) {
                                    String extra = app.getExtra();
                                    if (StringUtil.isNotEmpty(extra)) {
                                        JSONObject json = JSON.parseObject(extra);
                                        if (json.containsKey(GENERAL_URL)) {
                                            String generalInternetUrl = (String) json.get(GENERAL_URL);
                                            if (com.chinatower.common.core.utils.StringUtils.isNotEmpty(generalInternetUrl)) {
                                                data.put(REDIRECT_URL, generalInternetUrl);
                                            }
                                        }
                                    } else {
                                        data.put(REDIRECT_URL, app.getAppAddr());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e1) {
                log.error("查询平台编码异常=", e1);
            }
        }
        return result;
    }

    private Result prepareTokenByUserId(String userId) {
        SysUser targetUser = this.selectUserByUserId(userId);
        if (ObjectUtils.isEmpty(targetUser)) {
            log.error(Constants.AUTH_FAIL + "二维码绑定的userId无效");
            return Result.fail(Constants.AUTH_FAIL);
        }
        String userName = targetUser.getUserName();
        String password = SecurityUtils.decryptPassword(targetUser.getPassword());
        return this.prepareToken(userName, password, Constants.APP_ID);
    }

    @Override
    public Result sdkToApiLogin(String userId) {
        SysUser targetUser = this.selectUserByUserId(userId);
        String userName = targetUser.getUserName();
        String password = SecurityUtils.decryptPassword(targetUser.getPassword());
        return this.prepareToken(userName, password, Constants.APP_ID);
    }

    @Override
    public Result doUserIdLogin(AuthReplaceTokenVO replaceTokenVO) {

        String tokenPre = "Bearer ";

        LoginUser loginUser = SecurityUtils.getLoginUser();
        replaceTokenVO.setTowerUser(loginUser);
        //来源属地编码
        String fromPlatformCode = loginUser.getUser().getPlatformCode();
        //目的地属地编码
        String toPlatformCode = replaceTokenVO.getPlatformCode();

        if(com.chinatower.common.core.utils.StringUtils.isEmpty(fromPlatformCode)){
            return Result.fail("平台编码未配置！");
        }

        //目的地公钥加密
        String encryptStr = AESUtil.encrypt(JSONObject.toJSONString(replaceTokenVO));
        // 定义请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("encryptStr", encryptStr);

        Result doSwitchLoginResult = RemoteNodeUtils.create(toPlatformCode)
                .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.CASCADE.getCode())
                .doSwitchLogin(param);

        log.info("doSwitchLogin-return" + JSONObject.toJSONString(doSwitchLoginResult));

        if (doSwitchLoginResult.getCode() == ResultCode.SUCCESS.getCode()) {

            if (ObjectUtil.isNotEmpty(doSwitchLoginResult.getData())) {
                String shortToken = doSwitchLoginResult.getData().toString();

                //短换长token
                SwitchLoginFeignVo switchLoginFeignVo = new SwitchLoginFeignVo();
                switchLoginFeignVo.setUuid(shortToken);

                log.info("checkSwitchId入参:{}", JSON.toJSONString(switchLoginFeignVo));
                Result<SwitchLoginFeignVo> switchLoginFeignVoResult = RemoteNodeUtils.create(replaceTokenVO.getPlatformCode())
                        .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.GENERAL.getCode())
                        .checkSwitchId(switchLoginFeignVo);

                log.info("switchLoginFeignVoResult:{}", JSON.toJSONString(switchLoginFeignVoResult));
                if (ObjectUtil.isNotEmpty(switchLoginFeignVoResult.getData())) {
                    SwitchLoginFeignVo tokenData = switchLoginFeignVoResult.getData();
                    String token = tokenData.getToken();
                    String bearToken = tokenPre + token;
                    Result userRes = RemoteNodeUtils.create(replaceTokenVO.getPlatformCode())
                            .getFeign(SystemApiFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.GENERAL.getCode())
                            .selectUserOnlyByUserName(replaceTokenVO.getUserName(), bearToken);
                    log.info("userRes:{}", JSON.toJSONString(userRes));
                    if (ObjectUtil.isEmpty(userRes.getData())) {
                        return Result.fail("用户不存在");
                    }
                    Object data1 = userRes.getData();
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(data1));
                    String userId = jsonObject.getString("userId");

                    //根据用户id登录属地节点
                    String str = UUID.randomUUID().toString().replace("-", "");
                    String time = DateUtil.format(new Date(), DATE_FORMAT);
                    String encStr = AESUtil.encryptBase64(str + "-" + time + "-" + userId);

                    //远程调用
                    Result result = RemoteNodeUtils.create(replaceTokenVO.getPlatformCode())
                            .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.GENERAL.getCode())
                            .userIdLogin(encStr, token);
                    if (ObjectUtil.isNotEmpty(result.getData()) ) {
                        Map<String, Object> data = (Map<String, Object>) result.getData();
                        Map<String, String> choose = new HashMap<>();
                        choose.put("chooseAppVerCode", replaceTokenVO.getChooseAppVerCode());
                        choose.put("chooseTradeId", replaceTokenVO.getChooseTradeId());
                        data.put(CURRENT_VERSION, choose);

                        Object data2 = result.getData();
                        JSONObject loginRes = JSON.parseObject(JSON.toJSONString(data2));

                        String accessToken = loginRes.getString(ACCESS_TOKEN);

                        //调用切换行业
                        if (!replaceTokenVO.getChooseTradeId().isEmpty()) {
                            LoginFeignVo loginFeignVo = new LoginFeignVo();
                            loginFeignVo.setTradeId(replaceTokenVO.getChooseTradeId());
                            loginFeignVo.setAppVerCode(replaceTokenVO.getChooseAppVerCode());
                            Result chooseTrade = RemoteNodeUtils.create(replaceTokenVO.getPlatformCode())
                                    .getFeign(AuthFeignClient.class, RemoteNodeUtils.RemoteUrlEenum.GENERAL.getCode())
                                    .chooseTrade(loginFeignVo, tokenPre + accessToken);
                            log.info("调用属地chooseTrade返回:{}", JSON.toJSONString(chooseTrade));
                        }
                    }
                    return result;
                }
            }



        }





        return null;
    }
}
