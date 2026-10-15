package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.GridKeeperBaseVO;
import com.chinatower.common.core.comparator.PinYinComparator;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.AppOrderListByUserDTO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.constant.ImportConstants;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.IdUtils;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.common.utils.security.MD5Util;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.constants.*;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.DeptStreetVO;
import com.chinatower.platform.project.custom.entity.HeroVO;
import com.chinatower.platform.project.custom.entity.TenantMangeResultVO;
import com.chinatower.platform.project.custom.mapper.CustomManageMapper;
import com.chinatower.platform.project.system.constants.MongoConstants;
import com.chinatower.platform.project.system.dao.SysUserHisMongoDAO;
import com.chinatower.platform.project.system.domain.*;
import com.chinatower.platform.project.system.domain.dto.DeptUserTreeDto;
import com.chinatower.platform.project.system.domain.dto.SyncUserDTO;
import com.chinatower.platform.project.system.domain.dto.SysUserHisMongoDTO;
import com.chinatower.platform.project.system.domain.dto.UserQueryDTO;
import com.chinatower.platform.project.system.domain.vo.VerifyUpdateVO;
import com.chinatower.platform.project.system.mapper.*;
import com.chinatower.platform.project.system.service.*;
import com.chinatower.platform.project.system.utils.PasswordUtil;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.feign.client.WxFeignClient;
import com.chinatower.system.api.model.*;
import com.chinatower.system.api.request.role.QueryRoleRequest;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.request.user.GetUserByDeptAndDeviceScopeRequest;
import com.chinatower.system.api.resp.SysUserTenantInfoResponse;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 用户 业务层处理
 *
 * @author iwhalecloud
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private ISysDictDataService dictDataService;

    private final SysUserMapper userMapper;

    private final SysRoleMapper roleMapper;

    private final SysPostMapper postMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysUserPostMapper userPostMapper;

    private final ISysConfigService configService;

    private final SysDeptMapper deptMapper;

    private final MsgFeignClient msgFeignClient;

    private final ITradeWarningOrderService iTradeWarningOrderService;

    private final ISysTenantInfoService sysTenantInfoService;

    private final ISysDeptService deptService;

    private final SysDeptDeviceMapper deptDeviceMapper;

    private final ISysRoleService sysRoleService;

    private final SystemFeignClient systemFeignClient;

    private final WxFeignClient wxFeignClient;

    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    @Autowired
    private SystemRiskManagementMapper systemRiskManagementMapper;


    @Resource
    CustomManageMapper customManageMapper;

    @Resource
    private SysUserHisMongoDAO sysUserHisMongoDAO;


    private final FileUrlEncryptUtil fileUrlEncryptUtil;

    private final PasswordUtil passwordUtil;

    private RedisTemplate<String, Object> redisTemplate;

    private static final String ROOT_SUFFIX = "-01";

    private static final String REPLACE_STR = "{}-{}";

    private static final int USER_LIST_NUMBER = 1000;

    public static final String DEPENDENCE_MANAGE = "dependence_manage";
    /**
     * app 刷新token失效时间，默认30天
     */
    @Value("${mall.system.app.refreshTokenValidity:2592000}")
    private int appRefreshTokenValiditySeconds;

    @Value("${sms.loginUrl}")
    private String url;

    @Value("${sms.centerUrl:https://sl.chinatowercom.cn}")
    private String centerUrl;

    @Value("${sms.template.addUser}")
    private String templateCodeAddUser;

    @Value("${sms.template.updatePassword}")
    private String templateCodeUpdatePassword;

    /**
     * web 刷新token失效时间，默认25小时
     */
    @Value("${mall.system.web.refreshTokenValidity:90000}")
    private int webRefreshTokenValiditySeconds;

    private static final String S1_S2= "$1****$2";
    private static final String STR_100000= "100000";
    private static final String STR_100001= "100001";
    private static final String SYS_USER_INITPASSWORD= "sys.user.initPassword";
    private static final String IMPORT_ERROR= "导入失败，第";
    private static final String DEPT_ID= "dept_id";
    private static final String SYSTEM_YG= "system-yg";
    private static final String PATTERN_USER_NAME_ERROR= "导入用户登录名必须为4-16位(字母/数字/下划线/减号)";

    private static final String MAP_URL_KEY = "url";
    private static final int ZERO = 0;
    private static final String ZERO_STRING = "0";
    private static final int ONE = 1;
    private static final String ONE_STRING = "1";
    private static final String TWO_STRING = "2";
    private static final String THREE_STRING = "3";
    private static final int FOUR = 4;
    private static final String XINGHAO = "****";
    private static final String HENGGANG = "-";

    private final List<String> DEPT_IDS = Arrays.asList("100000", "100001");


    @Value("${hero.pInfo}")
    private String pInfo;

    @Value("${hero.rInfo}")
    private String rInfo;

    @Value("${hero.iInfo}")
    private String iInfo;

    @Value("${hero.aInfo}")
    private String aInfo;

    @Value("${hero.cInfo}")
    private String cInfo;


    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "u.tenant_id", industryCode = "d.INDUSTRY_CODE",acceptColumn = "ic.accept_node")
    public List<SysUser> selectUserList(SysUser user) {
        //排序列字段入参合法性检查
        getAllFields(SysUser.class,user);

        Integer pageNum = user.getPageNum();
        Integer pageSize = user.getPageSize();
        String requestSource = user.getRequestSource();
        String searchKeyword = user.getSearchKeyword();
        if (com.chinatower.common.utils.StringUtils.isNotNull(pageNum) && com.chinatower.common.utils.StringUtils.isNotNull(pageSize)) {
            //String orderBy = SqlUtil.escapeOrderBySql(user.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, user.getOrderBy());
        }
        List<SysUser> list = userMapper.selectUserList(user);
        if ("APP".equals(requestSource)) {
            if (StringUtils.isNotEmpty(user.getSearchKeyword())) {
                int size = list.size();
                int nickNameListSize = list.stream().filter(e -> StringUtils.isNotEmpty(e.getNickName())).map(SysUser::getNickName).collect(Collectors.toList()).size();
                int userNameListSize = list.stream().filter(e -> StringUtils.isNotEmpty(e.getUserName())).map(SysUser::getUserName).collect(Collectors.toList()).size();
                int phonenumberListSize = list.stream().filter(e -> StringUtils.isNotEmpty(e.getPhonenumber())).map(SysUser::getPhonenumber).collect(Collectors.toList()).size();
                if(nickNameListSize == size && userNameListSize == size && phonenumberListSize == size){
                    if (CollectionUtils.isNotEmpty(list)) {
                        Collections.sort(list, new Comparator<SysUser>() {
                            @Override
                            public int compare(SysUser o1, SysUser o2) {
                                return maxNum(o2, searchKeyword).compareTo(maxNum(o1, searchKeyword));
                            }
                        });
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        Set<String> avatarList = list.stream().filter(e -> StringUtils.isNotEmpty(e.getAvatar())).map(SysUser::getAvatar).collect(Collectors.toSet());
        Map<String, String> avatarMap = new HashMap<>();
        if(!avatarList.isEmpty()){
            avatarMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(avatarList));
        }
        List<String> collect = new ArrayList<>();
        for (SysUser sysUser : list) {
            if (StringUtils.isNotEmpty(sysUser.getDept().getAncestors())) {
                List<String> splitList = Arrays.asList(sysUser.getDept().getAncestors().split(","));
                collect.addAll(splitList);
            }
        }
        List<SysDept> sysDepts = deptMapper.selectDeptsByIds(collect);
        Map<String, String> deptMap = sysDepts.stream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
        List<UserRoleVO> userRoleList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            SysUser userItem = list.get(i);
            String phoneNumber = userItem.getPhonenumber();
            if (StringUtils.isNotEmpty(phoneNumber)) {
                userItem.setAesPhoneNumber(AESUtil.encrypt(phoneNumber));
                userItem.setPhonenumber(phoneNumber.replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
            }
            if (!StringUtils.isBlank(userItem.getDept().getAncestors())) {
                List<String> ancestorsDepts = Arrays.asList(userItem.getDept().getAncestors().split(","));
                List<String> parentNameList = new ArrayList<>();
                for (String s : ancestorsDepts) {
                    String parentDept = deptMap.get(s);
                    if (StringUtils.isNotEmpty(parentDept)) {
                        parentNameList.add(parentDept);
                    }
                }
                userItem.setParentNames(parentNameList);
            }
            if (StringUtils.isNotEmpty(userItem.getAvatar())) {
                userItem.setAvatar(avatarMap.get(userItem.getAvatar()));
            }
            UserRoleVO uerRoleVO = new UserRoleVO();
            uerRoleVO.setUserId(userItem.getUserId());
            uerRoleVO.setIndustryCode(userItem.getIndustryCode());
            uerRoleVO.setAppVerCode(userItem.getAppVerCode());
            userRoleList.add(uerRoleVO);
        }
        UserRoleVO userRoleVO = new UserRoleVO();
        userRoleVO.setUserList(userRoleList);
        List<SysRole>  roleList = roleMapper.selectRoleListByUserInfo(userRoleVO);
        if(!CollectionUtils.isEmpty(roleList)){
            for (int i = 0; i < list.size(); i++) {
                List<SysRole> roles = new ArrayList<>();
                for (int j = 0; j < roleList.size(); j++) {
                    if(list.get(i).getUserId().equals(roleList.get(j).getUserId())){
                        roles.add(roleList.get(j));
                    }
                }
                list.get(i).setRoles(roles);
            }
        }
        return list;
    }


    //排序列字段入参合法性检查
    public static void getAllFields(Class<?> clazz,SysUser user) {
        List<Field> fieldList = Lists.newArrayList();
        while (clazz != null){
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        List<String> stringList = new ArrayList<>();
        for (Field Field : fieldList) {
            stringList.add(Field.getName());
        }
        String orderByColumn = user.getOrderByColumn();
        if(StringUtils.isNotEmpty(orderByColumn)){
            if(!stringList.contains(orderByColumn)){
                user.setOrderByColumn(null);
            }
        }
    }


    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserName(userName);
        SysUser user = userMapper.selectUserByUserName(dto);
        if (user != null) {
            String phoneNumber = user.getPhonenumber();
            if (StringUtils.isNotEmpty(phoneNumber)) {
                user.setPhonenumber(phoneNumber.replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
                user.setAesPhoneNumber(AESUtil.encrypt(phoneNumber));
            }
            String telPhone = user.getTelPhone();
            if (StringUtils.isNotEmpty(telPhone)) {
                String frontStr = telPhone.substring(ZERO, telPhone.indexOf(HENGGANG)+ ONE);
                String behindStr = telPhone.substring(telPhone.length() - FOUR);
                String tempTelPhone = frontStr + XINGHAO + behindStr;
                user.setTelPhone(tempTelPhone);//fixme 后续版本处理
                user.setAesTelPhone(AESUtil.encrypt(telPhone));
            }
        }
        return user;
    }


    /**
     * 通过用户名查询用户(不区分权限)
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserNameNoAuth(String userName) {
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserName(userName);
        SysUser user = userMapper.selectUserByUserNameNoAuth(dto);
        if (user != null) {
            String phoneNumber = user.getPhonenumber();
            if (StringUtils.isNotEmpty(phoneNumber)) {
                user.setPhonenumber(phoneNumber.replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
                user.setAesPhoneNumber(AESUtil.encrypt(phoneNumber));
            }
            String telPhone = user.getTelPhone();
            if (StringUtils.isNotEmpty(telPhone)) {
                user.setTelPhone(telPhone.replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));//fixme 后续版本处理
                user.setAesTelPhone(AESUtil.encrypt(telPhone));
            }
        }
        return user;
    }


    @Override
    public SysUser selectUserOnlyByUserName(String userName) {
        SysUser user = userMapper.selectUserOnlyByUserName(userName);
        if (user != null && StringUtils.isNotEmpty(user.getPhonenumber())) {
            user.setPhonenumber(user.getPhonenumber().replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
        }
        return user;
    }

    @Override
    public SysUser selectUserWithPwdByUserName(String userName) {
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserName(userName);
//        SysUser user1 = userMapper.selectUserWithPwdByUserName(dto);
//        log.info("user1:{}", user1);
        SysUser user = userMapper.selectByUserName(dto);
        log.info("user:{}", user);
        String deptId = user.getDeptId();
        List<String> deptIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(deptId) && deptId.contains(",")) {
            deptIds.addAll(Arrays.asList(deptId.split(",")));
        } else {
            deptIds.add(deptId);
        }
        dto.setDeptIds(deptIds);
        SysUser userDept = userMapper.selectDeptByDeptIds(dto);
        log.info("userDept:{}", userDept);
        if (Objects.nonNull(userDept)) {
            BeanUtils.copyProperties(userDept, user, getNullPropertyNames(userDept));
        }
        log.info("add properties laster user:{}", user);
        if ((null != user) && StringUtils.isNotBlank(user.getPhonenumber())) {
            user.setPhonenumber(user.getPhonenumber().replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
        }
        return user;
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(String userId) {
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserId(userId);
        return queryUser(dto);
    }

    @Override
    public SysUser selectUserByIdNew(String userId, String industryCode) {
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserId(userId);
        dto.setIndustryCode(industryCode);
        return queryUser(dto);
    }

    private SysUser queryUser(UserQueryDTO dto) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        dto.setPlatformCode(platformCode);
        SysUser user = userMapper.selectUserById(dto);
        if (null != user) {
            String phoneNumber = user.getPhonenumber();
            if (StringUtils.isNotBlank(phoneNumber)) {
                user.setPhonenumber(phoneNumber.replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
                user.setAesPhoneNumber(AESUtil.encrypt(phoneNumber));
            }
            SysDept dept = user.getDept();
            if (Objects.nonNull(dept) && !StringUtils.isBlank(dept.getAncestors())) {
                List<String> list = Lists.newArrayList(dept.getAncestors().split(","));
                list.remove(dept.getDeptId());
                if(CollectionUtils.isNotEmpty(list)){
                    user.setParentNames(deptMapper.selectDeptByIds(list));
                }
            }
            try {
                user.setAvatar(fileUrlEncryptUtil.dealObsFileUrl(user.getAvatar()));
            } catch (Exception e) {
                log.error("文件服务错误", e);
            }
        }

        return user;
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(SysUser userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuilder idsStr = new StringBuilder();
        for (SysRole role : list) {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        StringBuilder idsStr = new StringBuilder();
        for (SysPost post : list) {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString())) {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = userMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 根据租户id查询用户表中是否已存在该租户管理员账号
     *
     * @param tenantId
     * @return
     */
    @Override
    public String checkTenantAdminUnique(String tenantId) {
        int count = userMapper.checkTenantAdminUnique(tenantId);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        String userId = StringUtils.isNull(user.getUserId()) ? "-1" : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && !info.getUserId().equalsIgnoreCase(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        String userId = StringUtils.isNull(user.getUserId()) ? "-1" : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && !info.getUserId().equalsIgnoreCase(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new CustomException("不允许操作超级管理员用户");
        }
    }

    /**
     * 新增用户(视联feign接口专用)
     *
     * @param user 用户
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertVideoUser(SysUser user) {
        // 创建用户与角色关系时，
        // 1.根据租户id查询出对应行业应用版本，
        // 2.查询出该行业应用版本对应的角色，添加到Long[] roleIds该字段中
        String tenantId = user.getTenantId();

        List<SysTenantTrade> tenantTrades = sysTenantInfoService.selectTenantTradeByTenantId(tenantId);
        if (CollUtil.isNotEmpty(tenantTrades)) {
            List<SysRole> tradeRoles = roleMapper.selectRoleByTrades(tenantTrades);
            if (CollUtil.isNotEmpty(tradeRoles)) {
                List<Long> roleIds = tradeRoles.stream().map(sysRole -> sysRole.getRoleId()).collect(Collectors.toList());
                if (user.getRoleIds() != null) {
                    roleIds.addAll(Arrays.asList(user.getRoleIds()));
                }
                user.setRoleIds(roleIds.toArray(new Long[]{}));
            }
        }
        //赋值为该租户部门根节点
        user.setDeptId(tenantId + ROOT_SUFFIX);
        return this.insertUser(user);
    }

    /**
     * 根据用户角色和租户ID获取列表
     *
     * @param sysUser
     * @return
     */
    @Override
    public List<SysUser> getUserList(SysUser sysUser) {
        List<SysUser> userList = userMapper.getUserInfos(sysUser.getRoleId(), sysUser.getTenantId());
        return userList;
    }

    /**
     * @param sysUser
     * @return List<SysUser>
     * @Author：liuxt
     * @Description：获取用户列表数据
     * @Date：2022/8/5 11:34
     * @requestID 中国铁塔_铁塔视联平台__运营管理-单点登录管理_需求
     */
    @Override
    public List<SysUser> getUserListTable(SysUser sysUser) {
        List<SysUser> userList = userMapper.getUserInfosTable(sysUser);
        return userList;
    }


    private void wrapTowerUserDept(SysUser user) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!UserTypeEnum.TOWER.getType().equalsIgnoreCase(currentUser.getUserType())) {
            return;
        }
        if (!user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
            return;
        }
        SysDept sysDept = deptMapper.selectDeptOnlyById(user.getDeptId().substring(0, 6));
        if (null == sysDept) {
            throw new RuntimeException("上级部门不允许为空!");
        }
        String deptLevel = sysDept.getDeptLevel();
        deptLevel = deptLevel.replaceFirst("^0+(?!$)", "");
        switch (deptLevel) {
            case "1":
                user.setProvinceCode(STR_100000);
                user.setProvinceId(STR_100000);
                user.setCityCode(STR_100001);
                user.setCityId(STR_100001);
                break;
            case "2":
                user.setProvinceCode(sysDept.getDeptId());
                user.setProvinceId(sysDept.getDeptId());
                user.setCityCode(null);
                user.setCityId(null);
                break;
            case "3":
                user.setProvinceCode(sysDept.getParentId());
                user.setProvinceId(sysDept.getParentId());
                user.setCityId(sysDept.getDeptId());
                user.setCityCode(sysDept.getDeptId());
                break;
            default:
                break;
        }

    }

    private void importTowerUserDept(SysUser currentUser, SysUser user) {
        if (!UserTypeEnum.TOWER.getType().equalsIgnoreCase(currentUser.getUserType())) {
            return;
        }
        if (!user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
            return;
        }
        SysDept sysDept = deptMapper.selectDeptOnlyById(user.getDeptId().substring(0, 6));
        if (null == sysDept) {
            throw new RuntimeException("上级部门不允许为空!");
        }

        switch (sysDept.getDeptLevel()) {
            case "1":
                user.setProvinceCode(STR_100000);
                user.setProvinceId(STR_100000);
                user.setCityCode(STR_100001);
                user.setCityId(STR_100001);
                break;
            case "2":
                user.setProvinceCode(sysDept.getDeptId());
                user.setProvinceId(sysDept.getDeptId());
                user.setCityCode(null);
                user.setCityId(null);
                break;
            case "3":
                user.setProvinceCode(sysDept.getParentId());
                user.setProvinceId(sysDept.getParentId());
                user.setCityId(sysDept.getDeptId());
                user.setCityCode(sysDept.getDeptId());
                break;
            default:
                break;
        }

    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        String password = passwordUtil.generatePassword();
        if (StrUtil.isBlank(user.getPassword())) {
            //user.setPassword(SecurityUtils.encryptPassword(configService.selectConfigValueByKey(SYS_USER_INITPASSWORD)));
            user.setPassword(SecurityUtils.encryptPassword(password));
        }
        if (StrUtil.isBlank(user.getCreateBy())) {
            user.setCreateBy(SecurityUtils.getOperator());
        }
        if (StrUtil.isBlank(user.getStatus())) {
            user.setStatus(ZERO_STRING);
        }
        user.setCreateTime(new Date());
        if (StringUtils.isEmpty(user.getUserId())) {
            user.setUserId(IdUtils.fastSimpleUUID());
        }
        //获取省部门，市部门
        wrapTowerUserDept(user);
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        // 记录赋予角色权限
        recordingTowerUserOperationalRoles(user.getRoleIds(),user,user,1);
        try{
            /*MsgRequestVO requestVO = new MsgRequestVO();
            requestVO.setPhoneNumber(user.getPhonenumber());
            requestVO.setSmsTemplateCode(templateCodeAddUser);*/

            Map<String,Object> map = new HashMap<>();
            map.put("password",password);

            //根据属地部署方式确定新增用户发送短信中的系统地址
            setAddUserMsgUrl(map,user);
            String nodeCode = getPlatformNode();
            map.put("nodeCode",nodeCode);
            map.put("userName",user.getUserName());
            /*requestVO.setParaMap(JSON.toJSONString(map));
            msgFeignClient.innovationSendMessage(requestVO);*/
            CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
            commonSmsRequest.setMsgType(Constants.TWO);
            //commonSmsRequest.setPhone(sysUser.getPhonenumber());
            commonSmsRequest.setPhone(user.getPhonenumber());
            commonSmsRequest.setRequestMap(JSON.toJSONString(map));
            commonSmsRequest.setTemplateCode(templateCodeAddUser);
            log.info("发送短信入参:{}", JSON.toJSONString(commonSmsRequest));
            msgFeignClient.sendMessage(commonSmsRequest);
        }catch (Exception e){
            log.info("新增用户发送通知短信异常:"+e);
        }
        return rows;
    }

    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(com.chinatower.event.constants.Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        com.chinatower.common.core.web.domain.AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(com.chinatower.event.constants.Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(com.chinatower.event.constants.Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(com.chinatower.event.constants.Constants.PARAM_CONFIG_VALUE);
        }
        String nodeAlias = userMapper.queryNodeAlias(sdhNode);
        if(StringUtils.isEmpty(nodeAlias)){
            //节点别名为空的话，按照原来逻辑发送节点编码
            return sdhNode;
        }else {
            return nodeAlias;
        }
    }

    /**
     * 根据不同场景确定新增用户发送短信中的系统地址
     * @param map
     */
    private void setAddUserMsgUrl(Map<String, Object> map,SysUser user) {
        if(!StringUtils.isEmpty(user.getTenantId())){
            // 调用浩鲸sql查询登录页面开关状态及返回地址
            TenantMangeResultVO tenantInfo = customManageMapper.selectSysTenantInfoByTenantId(user.getTenantId());
            String loginType = tenantInfo.getLoginType();
            String loginUrl = tenantInfo.getLoginUrl();
            if (Constants.ONE.equals(tenantInfo.getLoginType()) && !StringUtils.isEmpty(loginUrl)) {
                map.put(MAP_URL_KEY, loginUrl);
            }else{
                normalSetUrl(map);
            }
        }else{
            normalSetUrl(map);
        }
    }


    /**
     * 根据属地部署方式确定新增用户发送短信中的系统地址
     * @param map
     */
    private void normalSetUrl(Map<String, Object> map) {
        map.put(MAP_URL_KEY, url);
        //根据属地部署方式确定新增用户发送短信中的系统地址
        com.chinatower.platform.project.system.domain.SysConfig dependenceManage = configService.selectConfigByKey(DEPENDENCE_MANAGE);
        log.info("查询属地化管理返回：{}", JSON.toJSONString(dependenceManage));
        if (null != dependenceManage) {
            String configValue = dependenceManage.getConfigValue();
            if ("2".equals(configValue)) {
                map.put(MAP_URL_KEY, centerUrl);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int insertSysUser(SysUser user) {
        String password = passwordUtil.generatePassword();
        /*if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword(SecurityUtils.encryptPassword(configService.selectConfigValueByKey(SYS_USER_INITPASSWORD)));
        }*/
        user.setPassword(SecurityUtils.encryptPassword(password));
        if (StrUtil.isBlank(user.getCreateBy())) {
            user.setCreateBy(SecurityUtils.getOperator());
        }
        if (StrUtil.isBlank(user.getStatus())) {
            user.setStatus(ZERO_STRING);
        }
        user.setCreateTime(new Date());
        if (StringUtils.isEmpty(user.getUserId())) {
            user.setUserId(IdUtils.fastSimpleUUID());
        }
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        try{
            /* MsgRequestVO requestVO = new MsgRequestVO();
            requestVO.setPhoneNumber(user.getPhonenumber());
            requestVO.setSmsTemplateCode(templateCodeAddUser);*/
            Map<String,Object> map = new HashMap<>();
            map.put("password",password);
            //根据属地部署方式确定新增用户发送短信中的系统地址
            setAddUserMsgUrl(map,user);
            map.put("nodeCode",getPlatformNode());
            map.put("userName",user.getUserName());
            /*requestVO.setParaMap(JSON.toJSONString(map));
            msgFeignClient.innovationSendMessage(requestVO);*/
            CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
            commonSmsRequest.setMsgType(Constants.TWO);
            commonSmsRequest.setPhone(user.getPhonenumber());
            commonSmsRequest.setRequestMap(JSON.toJSONString(map));
            commonSmsRequest.setTemplateCode(templateCodeAddUser);
            msgFeignClient.sendMessage(commonSmsRequest);
        }catch (Exception e){
            log.info("新增用户发送通知短信异常:"+e);
        }
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        String userId = user.getUserId();
        SysUser user1 = userMapper.selectUserOnlyByUserName(user.getUserName());
        if (user1 != null && user1.getUserName().equalsIgnoreCase(user.getUserName()) && !user1.getUserId().equalsIgnoreCase(userId)) {
            throw new CustomException("用户名称已存在！");
        }

        UserQueryDTO userQueryDTO = new UserQueryDTO();
        userQueryDTO.setUserId(userId);
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        userQueryDTO.setPlatformCode(platformCode);
        user1 = userMapper.selectUserById(userQueryDTO);
        if(StringUtils.isNotEmpty(user1.getPhonenumber()) && user1.getPhonenumber().equals(user.getPhonenumber())){
            //新手机号与原有手机号一致,不走修改手机号重置密码逻辑
            user.setPhonenumber(null);
        }
        //记录铁塔用户操作角色

        recordingTowerUserOperationalRoles(user.getRoleIds(),user,user1,2);

        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        user.setUpdateTime(new Date());
        if (StrUtil.isEmpty(user.getTenantId())) {
            user.setTenantId("null");
        } else if (!user.getTenantId().equalsIgnoreCase(user1.getTenantId())) {
            throw new CustomException("参数错误！");
        }
        if (!"1".equals(user1.getIsTenantAdmin())) {
            SysDept dept = deptMapper.selectDeptOnlyById(user.getDeptId());
            if (Objects.isNull(dept)) {
                throw new CustomException("用户组织不存在！");
            }
            StringBuffer deptId = new StringBuffer();
            if (!user1.getDeptId().equalsIgnoreCase(user.getDeptId())) {
                boolean status = false;
                String[] split = user1.getDeptId().split(",");
                for (String k : split) {
                    SysDept dept1 = deptMapper.selectDeptOnlyById(k);
                    if (dept1.getIndustryCode().equalsIgnoreCase(dept.getIndustryCode())) {
                        if (status) {
                            deptId.append("," + user.getDeptId());
                        } else {
                            deptId.append(user.getDeptId());
                        }
                    } else {
                        if (status) {
                            deptId.append("," + k);
                        } else {
                            deptId.append(k);
                        }
                    }
                    status = true;
                }
                user.setDeptId(deptId.toString());
            }
        } else {
            //租户管理员不更新
            user.setDeptId(null);
            user.setIsTenantAdmin(null);
        }
        String password ="";
        //判断手机号是否脱敏,如脱敏不入库
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && user.getPhonenumber().contains("*")) {
            user.setPhonenumber(null);
        } else if (!StringUtils.isEmpty(user.getPhonenumber())) {
            //需要手改手机号时要重置密码
            password = passwordUtil.generatePassword();
            user.setPassword(SecurityUtils.encryptPassword(password));
        }
        //获取省部门，市部门
        wrapTowerUserDept(user);
        //同步更新网格员表
        GridKeeperBaseVO gridKeeperBaseVO = new GridKeeperBaseVO();
        gridKeeperBaseVO.setKeeperName(user.getNickName());
        gridKeeperBaseVO.setKeeperPhoneNumber(user.getPhonenumber());
        gridKeeperBaseVO.setKeeperEmail(user.getEmail());
        gridKeeperBaseVO.setKeeperGender(user.getSex());
        gridKeeperBaseVO.setStaffId(userId);
        userMapper.updateGridkeeper(gridKeeperBaseVO);
        int result = userMapper.updateUser(user);
        if(result>0){
            try{
                if(!StringUtils.isEmpty(user.getPhonenumber())){
                    /*MsgRequestVO requestVO = new MsgRequestVO();
                    requestVO.setPhoneNumber(user.getPhonenumber());
                    requestVO.setSmsTemplateCode(templateCodeUpdatePassword);*/
                    Map<String,Object> map = new HashMap<>();
                    map.put("password",password);
                    map.put("nodeCode",getPlatformNode());
                    /*requestVO.setParaMap(JSON.toJSONString(map));
                    msgFeignClient.innovationSendMessage(requestVO);*/
                    CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
                    commonSmsRequest.setMsgType(Constants.TWO);
                    commonSmsRequest.setPhone(user.getPhonenumber());
                    commonSmsRequest.setRequestMap(JSON.toJSONString(map));
                    commonSmsRequest.setTemplateCode(templateCodeUpdatePassword);
                    msgFeignClient.sendMessage(commonSmsRequest);
                }
            }catch (Exception e){
                log.info("新增用户发送通知短信异常:"+e);
            }
        }
        return result;
    }

    private void recordingTowerUserOperationalRoles(Long[] roleIds,SysUser user, SysUser user1, int flag) {
        try {
            if(user1.getUserType().equals(ZERO_STRING)){
                // 获取当前登录用户信息
                SysUser currentUser = SecurityUtils.getActualLoginUser().getUser();
                List<SystemRiskManagement> systemRiskManagements = new ArrayList<>();

                List<String> newRoleIds = Arrays.stream(roleIds)
                        .map(String::valueOf)
                        .collect(Collectors.toList());

                //flag 1: 新增用户 2: 修改用户
                if (flag == 1) {
                    newRoleIds = filterRoleIdsByConfig(newRoleIds);
                    processRoleChanges(newRoleIds, "用户配置了高风险角色权限", currentUser, user1,user, systemRiskManagements,ONE_STRING);
                } else {
                    // 获取用户当前角色列表
                    List<String> oldRoleIds = userRoleMapper.selectUserRoleByUserId(user1.getUserId());

                    // 计算新增角色
                    List<String> addRoleIds = new ArrayList<>(newRoleIds);
                    addRoleIds.removeAll(oldRoleIds);

                    // 计算取消角色
                    List<String> cancelRoleIds = new ArrayList<>(oldRoleIds);
                    cancelRoleIds.removeAll(newRoleIds);

                    addRoleIds = filterRoleIdsByConfig(addRoleIds);
                    cancelRoleIds = filterRoleIdsByConfig(cancelRoleIds);

                    // 处理新增角色
                    if (!addRoleIds.isEmpty()) {
                        processRoleChanges(addRoleIds, "用户配置了高风险角色权限", currentUser, user1,user, systemRiskManagements,ONE_STRING);
                    }

                    // 处理取消角色
                    if (!cancelRoleIds.isEmpty()) {
                        processRoleChanges(cancelRoleIds, "用户取消了高风险角色权限", currentUser, user1,user, systemRiskManagements,ZERO_STRING);
                    }
                }

                // 数据入库
                if(systemRiskManagements.size()>0){
                    systemRiskManagementMapper.batchInsert(systemRiskManagements);
                }
            }
        }catch (Exception e){
            log.error("记录用户操作角色异常:"+e);
        }

    }

    private List<String> filterRoleIdsByConfig(List<String> roleIds) {
        // 判断是否存在角色配置
        List<SysDictData> riskManagementRoles = sysDictDataMapper.selectDictDataByType("risk_management_roles");
        if (CollectionUtils.isNotEmpty(riskManagementRoles) && riskManagementRoles.size() > 0) {
            List<String> collect = riskManagementRoles.stream()
                    .map(SysDictData::getDictValue)
                    .collect(Collectors.toList());
            // 只留配置的角色
            if (!roleIds.isEmpty()) {
                roleIds = roleIds.stream().filter(collect::contains).collect(Collectors.toList());
            }
        }
        return roleIds;
    }

    private void processRoleChanges(List<String> roleIds, String judgeMethod, SysUser currentUser, SysUser user, SysUser intoUser,List<SystemRiskManagement> systemRiskManagements,String isRisk) {
        if (CollectionUtils.isNotEmpty(roleIds) && roleIds.size() > 0) {
            List<SysRole> sysRoles = roleMapper.selectRoleByIds(roleIds);
            Map<Long, String> roleIdToNameMap = sysRoles.stream()
                    .collect(Collectors.toMap(SysRole::getRoleId, SysRole::getRoleName));
            SysDept sysDept = deptMapper.selectDeptOnlyById(intoUser.getDeptId());
            SysDept sysDeptParent = deptMapper.selectDeptOnlyById(sysDept.getParentId());
            String provinceId = null;
            String cityCode = null;
            String deptLevel = sysDept.getDeptLevel();
            deptLevel = deptLevel.replaceFirst("^0+(?!$)", "");
            String parentDeptLevel = ZERO_STRING;
            if (sysDeptParent != null) {
                parentDeptLevel = sysDeptParent.getDeptLevel();
                parentDeptLevel = parentDeptLevel.replaceFirst("^0+(?!$)", "");
            }
            if (deptLevel.equals(ONE_STRING)) {
                provinceId = sysDept.getDeptId();
            } else if ((deptLevel.equals(TWO_STRING) && parentDeptLevel.equals(TWO_STRING))) {
                provinceId = sysDept.getParentId();
                cityCode = sysDept.getDeptId();
            } else if ((deptLevel.equals(TWO_STRING) && parentDeptLevel.equals(ONE_STRING))) {
                provinceId = sysDept.getDeptId();
            } else {
                provinceId = sysDept.getAncestors().substring(14, 20);
                List<SysDept> sysDepts = deptMapper.selectTowerDeptByLevel(provinceId, 2);
                Optional<SysDept> first = sysDepts.stream().filter(e -> e.getDeptLevel().equals(TWO_STRING)).findFirst();
                cityCode = first.get().getDeptId();
            }
            for (String roleId : roleIds) {
                SystemRiskManagement systemRiskManagement = new SystemRiskManagement();
                systemRiskManagement.setJudgeMethod(judgeMethod);
                systemRiskManagement.setIsRisk(isRisk);
                systemRiskManagement.setUserCode(currentUser.getUserName());
                systemRiskManagement.setUserName(currentUser.getNickName());
                systemRiskManagement.setBusinessKey(user.getUserId() + HENGGANG + roleId);
                systemRiskManagement.setBusinessName("用户【" + user.getNickName() + "】-角色【" + roleIdToNameMap.get(Long.valueOf(roleId)) + "】");
                systemRiskManagement.setProvinceCode(provinceId);
                systemRiskManagement.setCityCode(cityCode);
                systemRiskManagement.setToRoleName(roleIdToNameMap.get(Long.valueOf(roleId)));
                systemRiskManagement.setToUserName(user.getNickName());
                systemRiskManagements.add(systemRiskManagement);
            }
        }
    }

    /**
     * 修改用户信息,视联使用
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateVideoUser(SysUser user) {
        user.setUpdateBy(SecurityUtils.getUsername());
        user.setUpdateTime(new Date());
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        String userId = user.getUserId();
        SysUser sysUser = userMapper.selectUserOnlyByUserId(userId);
        if (Objects.isNull(sysUser)) {
            throw new CustomException(AuthConstants.USERNAME_ERROR);
        }
        SysUser db = new SysUser(userId);
        String password ="";
        if(Constants.ZERO.equals(user.getStatus())){
            //账号停用后启用需要重置密码并通知客户
            password = passwordUtil.generatePassword();
            db.setPassword(SecurityUtils.encryptPassword(password));
            //add by liuxt 铁塔用户管理优化需求
            //查询用户表account_validity_period字段和validityPeriodState字段
            String validityPeriodState = sysUser.getValidityPeriodState();
            String accountValidityPeriod = sysUser.getAccountValidityPeriod();
            if(Constants.TWO.equals(validityPeriodState) && StringUtils.isNotEmpty(accountValidityPeriod)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date currentDate = new Date();
                    Date validDate = sdf.parse(accountValidityPeriod);
                    if(currentDate.after(validDate)){//当前时间大于有效时间 将validityPeriodState字段置为1永久有效, account_validity_period字段置为null。
                        db.setValidityPeriodState(Constants.ONE);
                        db.setAccountValidityPeriod(null);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        db.setStatus(user.getStatus());
        db.setUpdateTime(new Date());
        db.setUpdateBy(user.getUpdateBy());
        int i = userMapper.updateUserStatus(db);
        String userName = sysUser.getUserName();
        if ("1".equals(user.getStatus())) {
            this.forceLogOut(userId, userName);
        }
        if(Constants.ZERO.equals(user.getStatus())){
            //账号停用后启用需要重置密码并通知客户
            try{
                /*MsgRequestVO requestVO = new MsgRequestVO();
                requestVO.setPhoneNumber(sysUser.getPhonenumber());
                requestVO.setSmsTemplateCode(templateCodeUpdatePassword);*/
                Map<String,Object> map = new HashMap<>();
                map.put("password",password);
                map.put("nodeCode",getPlatformNode());
                /*requestVO.setParaMap(JSON.toJSONString(map));
                msgFeignClient.innovationSendMessage(requestVO);*/
                CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
                commonSmsRequest.setMsgType(Constants.TWO);
                commonSmsRequest.setPhone(sysUser.getPhonenumber());
                commonSmsRequest.setRequestMap(JSON.toJSONString(map));
                commonSmsRequest.setTemplateCode(templateCodeUpdatePassword);
                msgFeignClient.sendMessage(commonSmsRequest);
            }catch (Exception e){
                log.info("新增用户发送通知短信异常:"+e);
            }
        }
        return i;
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        user.setUpdateTime(new Date());
        user.setAvatar(StringUtils.substringBefore(user.getAvatar(), "?"));
        String password = "";
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && user.getPhonenumber().contains("****")) {
            user.setPhonenumber(null);
        }else if (!StringUtils.isEmpty(user.getPhonenumber())) {
            //需要手改手机号时要重置密码
            password = passwordUtil.generatePassword();
            user.setPassword(SecurityUtils.encryptPassword(password));
        }
        //同步更新网格员表
        GridKeeperBaseVO gridKeeperBaseVO = new GridKeeperBaseVO();
        gridKeeperBaseVO.setKeeperPhoneNumber(user.getPhonenumber());
        gridKeeperBaseVO.setKeeperEmail(user.getEmail());
        gridKeeperBaseVO.setKeeperGender(user.getSex());
        gridKeeperBaseVO.setStaffId(user.getUserId());
        userMapper.updateGridkeeper(gridKeeperBaseVO);
        if(!StringUtils.isEmpty(user.getPhonenumber())){
            Map<String,Object> map = new HashMap<>();
            map.put("password",password);
            map.put("nodeCode",getPlatformNode());
            CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
            commonSmsRequest.setMsgType(Constants.TWO);
            commonSmsRequest.setPhone(user.getPhonenumber());
            commonSmsRequest.setRequestMap(JSON.toJSONString(map));
            commonSmsRequest.setTemplateCode(templateCodeUpdatePassword);
            msgFeignClient.sendMessage(commonSmsRequest);
        }
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户ID
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 校验验证码
     *
     * @param phonenumber
     * @param code
     * @return
     */
    @Override
    public boolean checkPhoneCode(String phonenumber, String code) {
        String s = RedisUtil.get(CommonConstants.SMS_FAIL_NUM_KEY + phonenumber, RedisDBType.PRIMARY);
        int loginFailNum = StrUtil.isNotBlank(s) ? Integer.parseInt(s) : 0;
        String verifyKey = CommonConstants.SMS_SUCCESS_KEY + phonenumber;

        if (loginFailNum >= CommonConstants.CAPTCHA_FAIL_MAX) {
            RedisUtil.delete(verifyKey, RedisDBType.PRIMARY);
            return false;
        }

        String cacheCode = "";
        try {
            cacheCode = RedisUtil.get(verifyKey, RedisDBType.PRIMARY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean status = code.equals(cacheCode);
        if (!status) {
            RedisUtil.set(CommonConstants.SMS_FAIL_NUM_KEY + phonenumber, (loginFailNum + 1) + "", 1800, RedisDBType.PRIMARY);
        }
        return status;
    }

    /**
     * 修改用户密码
     *
     * @param userId   用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int updateUserPassword(String userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(password);
        String username = SecurityUtils.getUsername();
        user.setUpdateBy(username);
        user.setPwdUpdateTime(new Date());
        int i = userMapper.updateUserPassword(user);
        if (i == 1) {
            this.forceLogOut(userId, username);
        }
        return i;
    }

    /**
     * 用户密码重置
     *
     * @param userId 用户编码
     * @return int
     */
    @Override
    public int resetPassword(String userId) {
        log.info("resetPassword-req:{}",userId);
        int count = 0;
        try {
            /*String passwordDecrypt = configService.selectConfigValueByKey(SYS_USER_INITPASSWORD);*/
            String password = passwordUtil.generatePassword();
            SysUser user = new SysUser();
            user.setUserId(userId);
            user.setPassword(SecurityUtils.encryptPassword(password));
            user.setUpdateBy(SecurityUtils.getUsername());
            user.setPwdUpdateTime(new Date());
            count = userMapper.resetUserPassword(user);
            UserQueryDTO dto = new UserQueryDTO();
            dto.setUserId(userId);
            SysUser sysUser = userMapper.selectUserOnlyByUserId(userId);
            this.forceLogOut(userId, sysUser.getUserName());
            try{
                /*MsgRequestVO requestVO = new MsgRequestVO();
                requestVO.setPhoneNumber(sysUser.getPhonenumber());
                requestVO.setSmsTemplateCode(templateCodeUpdatePassword);*/
                Map<String,Object> map = new HashMap<>();
                map.put("password",password);
                map.put("nodeCode",getPlatformNode());
                /*requestVO.setParaMap(JSON.toJSONString(map));
                msgFeignClient.innovationSendMessage(requestVO);*/
                CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
                commonSmsRequest.setMsgType(Constants.TWO);
                commonSmsRequest.setPhone(sysUser.getPhonenumber());
                commonSmsRequest.setRequestMap(JSON.toJSONString(map));
                commonSmsRequest.setTemplateCode(templateCodeUpdatePassword);
                msgFeignClient.sendMessage(commonSmsRequest);
            }catch (Exception e){
                log.error("密码重置发送短信通知异常:", e);
                throw new CustomException("密码重置发送短信通知异常！");
            }
            //this.sendMsg2User(sysUser.getPhonenumber(), passwordDecrypt);
        } catch (Exception e) {
            log.error("发送重置密码的短信失败，userId=" + userId + "，错误信息：", e);
            throw new CustomException("密码重置失败！");
        }
        return count;
    }

    /**
     * 向用户发送短信
     *
     * @param phone           手机号
     * @param passwordDecrypt 密码
     */
 /*   private void sendMsg2User(String phone, String passwordDecrypt) {
        CommonSmsRequest request = new CommonSmsRequest();
        request.setPhone(phone);
        request.setMsgType("2");
        request.setTemplateCode(CustomSmsTemplateConstants.RESET_PASSWORD_NOTIFY);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("code", passwordDecrypt);
        request.setRequestMap(JSON.toJSONString(requestMap));
        Result result = msgFeignClient.sendMessage(request);
        if (result.getCode() != ResultCode.SUCCESS.getCode()) {
            log.error("发送重置密码短信失败，" + result.getMsg());
        }
    }*/

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();

        if (StringUtils.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            Set<Long> roleIdSet = new HashSet<>();

            for (Long roleId : roles) {
                roleIdSet.add(roleId);
            }
            roleIdSet.forEach(r -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(String.valueOf(r));
                list.add(ur);
            });
            if (!list.isEmpty()) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int deleteUserById(String userId) {

        this.checkUserSource(userId);
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (!list.isEmpty()) {
                userPostMapper.batchUserPost(list);
            }
        }
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    public int deleteUserByIds(String[] userIds) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        for (String userId : userIds) {
            this.checkUserSource(userId);
            checkUserAllowed(new SysUser(userId));
        }
        List<SysUser> sysUsers = userMapper.selectUserOnlyByUserIds(userIds);
        Map<String, SysUser> sysUserMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserId, x -> x));
        SysUser sysUserVO = new SysUser();
        sysUserVO.setUserIds(userIds);
        sysUserVO.setUserName(user.getUserName());
//        int result = userMapper.deleteUserByIds(sysUserVO);
        int result = saveSysUserToMongoHis(Arrays.asList(userIds));
        for (int i = 0; i < userIds.length; i++) {
            SysUser sysUser = sysUserMap.get(userIds[i]);
            String userName = sysUser.getUserName();
            this.forceLogOut(userIds[i], userName);
        }
        return result;
    }

    private void checkUserSource(String userId) {
        SysUser user = this.selectUserById(userId);
        if (user != null && "4A".equals(user.getTowerUserSource())) {
            throw new RuntimeException("来源为4A对用户不可删除。");
        }
    }

    /**
     * 导入用户数据
     *
     * @param params 导入参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysImportDetail> importUser(ImportParams<SysUser> params) {
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        boolean updateSupport = MapUtil.getBool(params.getExtraParams(), "updateSupport");
        SysUser loginUser = MapUtil.get(params.getExtraParams(), "loginUser", SysUser.class);
        String userDept = loginUser.getDeptId();
        for (SysUser user : params.getList()) {
            SysImportDetail detail = new SysImportDetail(params.getImportId(), rowNum);
            try {
                String deptId = user.getDeptId();
                if (StrUtil.isNotBlank(deptId)) {
                    if (!userDept.equals(deptId) && deptMapper.checkIsRelation(deptId, userDept) == 0) {
                        detail.setFailReason(user.getUserName() + " 的部门编码不在当前用户权限内");
                        continue;
                    }
                } else {
                    detail.setFailReason(user.getUserName() + " 部门编码不能为空");
                    continue;
                }
                // 验证是否存在这个用户
                UserQueryDTO dto = new UserQueryDTO();
                dto.setUserName(user.getUserName());
                SysUser u = userMapper.selectUserByUserName(dto);
                if (StringUtils.isNull(u)) {
                    user.setCreateBy(loginUser.getUserName());
                    this.insertUser(user);
                    detail.setHandleResult(Constants.S);
                } else if (updateSupport) {
                    user.setUpdateBy(loginUser.getUserName());
                    this.updateUser(user);
                    detail.setHandleResult(Constants.S);
                } else {
                    detail.setFailReason(user.getUserName() + ImportConstants.STR_E);
                }
            } catch (Exception e) {
                detail.setFailReason("处理异常：" + ExceptionUtil.stacktraceToString(e, 1000));
                log.error(e.getMessage(), e);
            } finally {
                rowNum++;
                details.add(detail);
            }
        }
        return details;
    }


    /**
     * 导入用户数据
     *
     * @param params 导入参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysImportDetail> importUserData(ImportParams<SysUser> params) {
        List<SysImportDetail> details = new ArrayList<>();
        int rowNum = params.getStartRowNum();
        SysUser loginUser = MapUtil.get(params.getExtraParams(), "loginUser", SysUser.class);
        String userDept = loginUser.getDeptId();
        QueryRoleRequest role = new QueryRoleRequest();
//        List<SysRole> sysRoles = sysRoleService.selectRoleListTenant(role);
        role.setTenantId(loginUser.getTenantId());
        role.setIndustryCode(loginUser.getIndustryCode());
        role.setAppVerCode(loginUser.getAppVerCode());
        List<SysRole> sysRoles = roleMapper.selectRoleListTenant(role);
        List<String> collect = sysRoles.stream().filter(e -> Objects.nonNull(e.getRoleName())).map(SysRole::getRoleName).collect(Collectors.toList());
        List<SysUser> sysUserList = params.getList();
        for (int i = 0; i < sysUserList.size(); i++) {
            SysImportDetail detail = new SysImportDetail(params.getImportId(), rowNum);
            SysUser user = sysUserList.get(i);
            try {
                String deptId = user.getDeptId();
//                SysDept sysDept = deptService.selectDeptById(deptId);
//                SysDeptStreetRelVO vo = new SysDeptStreetRelVO();
//                vo.setDeptId(deptId);
                SysDept sysDept = deptMapper.selectDeptById(deptId, loginUser.getIndustryCode(), loginUser.getAppVerCode());
                if (StrUtil.isNotBlank(deptId)) {
                    if (Objects.isNull(sysDept)) {
                        detail.setFailReason(IMPORT_ERROR + (i + 2) + "行组织编码错误");
                        continue;
                    }
                    if (!userDept.contains(deptId) && deptMapper.checkIsRelation(deptId, userDept) == 0) {
                        detail.setFailReason("第" + (i + 2) + "行组织编码不在当前用户权限内");
                        continue;
                    }
                    if ("1".equals(sysDept.getStatus())) {
                        detail.setFailReason("第" + (i + 2) + "行的组织不能为停用状态");
                        continue;
                    }
                } else {
                    detail.setFailReason("第" + (i + 2) + "行的组织编码不能为空");
                    continue;
                }
                if (StrUtil.isBlank(user.getNickName())) {
                    detail.setFailReason("导入用户姓名不能为空");
                    continue;
                }
                if (StrUtil.isBlank(user.getUserName())) {
                    detail.setFailReason("导入用户登录名不能为空");
                    continue;
                } else {
                	if (!checkTextPattern(USER_NAME_CHECK_PATTER, user.getUserName())) {
                		detail.setFailReason(PATTERN_USER_NAME_ERROR);
                        continue;
                    }
                    if (checkSensitiveWords(user.getUserName())) {
                        detail.setFailReason(com.chinatower.platform.project.system.constants.Constants.ACCOUNT_NOT_RULE);
                        continue;
                    }
                }
                if (StrUtil.isBlank(user.getPhonenumber())) {
                    detail.setFailReason("导入用户手机号不能为空");
                    continue;
                } else {
                    if (!checkPhoneNumber(user.getPhonenumber())) {
                        detail.setFailReason("第" + (i + 2) + "行手机号格式错误");
                        continue;
                    }
                }
                List<SysRole> roles = user.getRoles();
                String roleNames = user.getRoleNames();
                if (StrUtil.isBlank(roleNames)) {
                    detail.setFailReason("导入用户角色不能为空");
                    continue;
                }
                List<String> result = Arrays.asList(roleNames.split(","));
                //验证角色名称是否正确
                List<String> roleNameList = result.stream().filter(e -> Objects.nonNull(e)).collect(Collectors.toList());
                if (!collect.containsAll(roleNameList)) {
                    detail.setFailReason(IMPORT_ERROR + (i + 2) + "行角色不存在");
                    continue;
                }
                Long[] roleIds = sysRoles.stream().filter(e -> roleNameList.contains(e.getRoleName())).map(SysRole::getRoleId).toArray(Long[]::new);
                user.setRoleIds(roleIds);
                // 验证是否存在这个用户
//                UserQueryDTO dto = new UserQueryDTO();
//                dto.setUserName(user.getUserName());
                SysUser u = userMapper.selectUserOnlyByUserName(user.getUserName());
                if (StringUtils.isNull(u)) {
                    user.setCreateBy(loginUser.getUserName());
                    user.setTenantId(loginUser.getTenantId());
                    user.setUserType("2");
                    //获取省部门，市部门
                    importTowerUserDept(loginUser, user);
                    this.insertSysUser(user);
                    detail.setHandleResult(Constants.S);
                } else {
                    detail.setFailReason(IMPORT_ERROR + (i + 2) + "行登录名已存在");
                }
            } catch (Exception e) {
                detail.setFailReason("处理异常：" + ExceptionUtil.stacktraceToString(e, 1000));
                log.error(e.getMessage(), e);
            } finally {
                rowNum++;
                details.add(detail);
            }
        }
        return details;
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
//        Pattern pattern = Pattern.compile("^1[1-9]{1}[0-9]{9}$");
        Pattern pattern = Pattern.compile("^\\d{11,13}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.find();
    }


    /**
     * 根据部门和角色查询用户信息
     *
     * @param deptId  部门id
     * @param roleKey 角色标识
     * @return 用户信息
     */
    @Override
    public List<SysUser> selectUserByDeptAndRole(String deptId, String roleKey) {
        return userMapper.selectUserByDeptAndRole(deptId, roleKey);
    }

    /**
     * 根据用户id集合查询用户信息列表
     *
     * @param userIds 用户id集合
     * @return 用户信息列表
     */
    @Override
    public List<SysUser> selectUserByIds(String[] userIds) {
        List<SysUser> result = new ArrayList<>();
        List<String> userIdList = Arrays.stream(userIds).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());

        int userSize = userIdList.size();
        int userBatch = (int) Math.ceil(userSize * 1.0 / USER_LIST_NUMBER);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String platformCode = user.getPlatformCode();
        for (int i = 0; i < userBatch; i++) {
            int indexStart = USER_LIST_NUMBER * i;
            int indexEnd = i == userBatch - 1 ? userSize : USER_LIST_NUMBER * (i + 1);
            List<String> ids = userIdList.subList(indexStart, indexEnd);
            String[] selectIds = ids.toArray(new String[0]);
            result.addAll(userMapper.selectUserAndRolesByIds(selectIds,platformCode));
        }
        return result;
//        //手机号脱敏处理(feign调用不加)
//        if (CollectionUtils.isNotEmpty(sysUsers)) {
//            sysUsers.forEach(x -> {
//                String phonenumber = x.getPhonenumber();
//                String encryptPhone = AESUtil.encrypt(phonenumber);
//                x.setPhonenumber(encryptPhone);
//                x.setAesPhoneNumber(encryptPhone);
//            });
//        }
    }

    /**
     * 根据用户userName集合查询用户信息列表
     *
     * @param userNames 用户userName集合
     * @return 用户信息列表
     */
    @Override
    public List<SysUser> getListByUserNames(String[] userNames) {
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        List<SysUser> sysUsers = userMapper.getListByUserNames(userNames,platformCode);
//        //手机号脱敏处理(feign调用不加)
//        if (CollectionUtils.isNotEmpty(sysUsers)) {
//            sysUsers.forEach(x -> {
//                String phonenumber = x.getPhonenumber();
//                String encryptPhone = AESUtil.encrypt(phonenumber);
//                x.setPhonenumber(encryptPhone);
//                x.setAesPhoneNumber(encryptPhone);
//            });
//        }
        return sysUsers;
    }

    @Override
    public String selectUserNamesByIds(String[] userIds) {
        if (userIds.length == 0) {
            return "";
        }
        String[] longIds = new String[userIds.length];
        for (int i = 0; i < userIds.length; i++) {
            try {
                longIds[i] = userIds[i];
            } catch (Exception e) {
                log.error("用户编码[{}]类型转换失败", userIds[i]);
            }
        }
        List<String> nameList = userMapper.selectUserNamesByIds(longIds);
        return IterUtil.join(nameList, "、");
    }

    @Override
    public int updateUserLoginFailNum(String userName, int num) {
        return userMapper.updateUserLoginFailNum(userName, num);
    }

    /**
     * 根据租户id查询用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/23
     * @version
     */
    @Override
    public List<SysUser> selectUserByTenantId(String tenantId) {
        return userMapper.selectUserByTenantId(tenantId);
    }

    /**
     * @return 获得租户下所有用户信息（区域、角色、性别）
     */
    @Override
    public List<SysUserTenantInfoResponse> selectUserListByTenantId(Integer pageNum, Integer pageSize) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        PageHelper.startPage(pageNum, pageSize, false);
        return userMapper.selectUserListByTenantId(tenantId, industryCode);
    }

    @Override
    public long selectUserListByTenantIdCount() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return userMapper.selectUserListByTenantIdCount(loginUser.getUser().getTenantId());
    }

    /**
     * 获取登录账号用户权限(返回liststring)
     *
     * @param user
     * @return
     */
    @Override
    public List<DeviceTreeVO> getPermissions(SysUser user) {
        //查询用户的租户id
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserId(user.getUserId());
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        dto.setPlatformCode(platformCode);
        SysUser sysUser = userMapper.selectUserById(dto);
        sysUser.setRoles(roleMapper.selectRoleListByUserId(sysUser));
        List<String> dept = getUserDeptSql(sysUser);// 拼接部门权限sql

        List<DeviceTreeVO> list = userMapper.getPermissions(user.getUserId(), dept);
        return list;
    }

    /**
     * 获取登录账号用户权限
     *
     * @param user
     * @return
     */
    @Override
    public List<String> getPermissionsForString(SysUser user) {
        //查询用户的租户id
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserId(user.getUserId());
        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        dto.setPlatformCode(platformCode);
        SysUser sysUser = userMapper.selectUserById(dto);
        sysUser.setRoles(roleMapper.selectRoleListByUserId(sysUser));
        List<String> dept = getUserDeptSql(sysUser);// 拼接部门权限sql

        List<String> list = userMapper.getPermissionsForString(user.getUserId(), dept);


        return list;
    }

    /**
     * 查询登录账号权限范围内的行政区划树
     *
     * @param sysUser
     * @return
     */
    @Override
    public List<DeviceTreeVO> getRegionTree(SysUser sysUser) {
        return userMapper.getRegionTree(sysUser);


    }

    /**
     * 查询登录账号全部的行政区划树
     *
     * @param sysUser
     * @return
     */
    @Override
    public List<AreaDataVO> getAreaByTenantId(SysUser sysUser) {
        List<AreaDataVO> list = new ArrayList<>();

        list = userMapper.getAreaByTenantId(sysUser.getTenantId());
        return list;
    }

    /**
     * 根据行业编码获取区县人员的手机号码
     *
     * @param user
     * @return
     */
    @Override
    public List<SysUser> getPhoneByRegion(SysUser user) {
        List<SysUser> sysUsers = userMapper.getPhoneByRegion(user.getAppVerCode(), user.getAreaCode(), user.getTenantId(),user.getIndustryCode());
//        //手机号脱敏处理(feign调用不加)
//        if (CollectionUtils.isNotEmpty(sysUsers)) {
//            sysUsers.forEach(x -> {
//                String phonenumber = x.getPhonenumber();
//                String encryptPhone = AESUtil.encrypt(phonenumber);
//                x.setPhonenumber(encryptPhone);
//                x.setAesPhoneNumber(encryptPhone);
//            });
//        }
        return sysUsers;
    }

    /**
     * 拼接部门权限sql
     *
     * @param user
     */
    public List<String> getUserDeptSql(SysUser user) {
        List<String> list = new ArrayList<>();
//        StringBuffer deptSql = new StringBuffer();

        List<SysRole> roles = user.getRoles();
        for (SysRole role : roles) {
            String roleDataScope = StrUtil.isNotBlank(role.getDataScope()) ? role.getDataScope() : "3";
            switch (roleDataScope) {
                case "2":
                    list.addAll(getRoleDeptInParameter(role.getRoleId()));
//                    //不为空时,设置该用户对应角色的部门及以下部门数据权限
//                    if (StringUtils.isNotEmpty(roleDeptInParameter)) {
//                        deptSql.append(StringUtils.format(" or {}.{} IN '{}' ", "u", DEPT_ID, roleDeptInParameter));
//                    }
                    break;
                case "3":
                    list.add(user.getDeptId());
//                    deptSql.append(StringUtils.format(" or {}.{} = '{}' ", "u", DEPT_ID, user.getDeptId()));
                    break;
                case "4":
                    //如果不是根节点,加deptId数据过滤
                    if (!DeptConstants.ROOT_DEPT.equals(user.getDept().getParentId())) {
                        List<String> deptInParameter = getTenantDeptInParameter();
                        //不为空时,设置该用户对应的部门及以下部门数据权限
                        if (CollectionUtils.isNotEmpty(deptInParameter)) {
                            list.addAll(deptInParameter);
//                            deptSql.append(StringUtils.format(" or {}.{} IN '{}'", "u", DEPT_ID, deptInParameter));
                        }
                        //为空时,设置该用户对应的部门数据权限
                        if (CollectionUtils.isEmpty(deptInParameter)) {
                            list.add(user.getDeptId());
//                            deptSql.append(StringUtils.format(" or {}.{} = '{}' ", "u", DEPT_ID, user.getDeptId()));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return list;

    }

    /**
     * 获取该角色对应的部门权限
     *
     * @return
     * @authot 李峥
     */
    private static List<String> getRoleDeptInParameter(Long roleId) {
        List<String> list = RedisUtil.getList(CacheConstants.ROLE_SCOPE_LIST_PREFIX + roleId, String.class, RedisDBType.PRIMARY);
//        StringBuffer stringBuffer = new StringBuffer();
//        if (CollUtil.isNotEmpty(list)) {
//            List<List<String>> partition = ListUtils.partition(list, 900);
//            if (CollUtil.isNotEmpty(partition)) {
//                for (int i = 0; i < partition.size(); i++) {
//                    if (i != partition.size() - 1) {
//                        joinRoleDeptParameter(partition, stringBuffer, i);
//                        stringBuffer.append(") or ");
//                    }
//                    if (i == partition.size() - 1) {
//                        joinRoleDeptParameter(partition, stringBuffer, i);
//                        stringBuffer.append(")");
//                    }
//                }
//            }
//        }
//        return stringBuffer.toString();
        return list;
    }

    private static void joinRoleDeptParameter(List<List<String>> partition, StringBuffer stringBuffer, int i) {
        List<String> strings = partition.get(i);
        stringBuffer.append("(");
        for (String id : strings) {
            stringBuffer.append(id + ",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    /**
     * 获取该租户用户对应的部门权限
     *
     * @return
     * @author 李峥
     */
    private static List<String> getTenantDeptInParameter() {
        List<String> childDeptIdList = SecurityUtils.getTenantChildDeptIdList();
//        StringBuffer stringBuffer = new StringBuffer();
//        if (CollUtil.isNotEmpty(childDeptIdList)) {
//            List<List<String>> partition = ListUtils.partition(childDeptIdList, 900);
//            if (CollUtil.isNotEmpty(partition)) {
//                for (int i = 0; i < partition.size(); i++) {
//                    if (i != partition.size() - 1) {
//                        joinDeptParameter(partition, stringBuffer, i);
//                        stringBuffer.append(") or ");
//                    }
//                    if (i == partition.size() - 1) {
//                        joinDeptParameter(partition, stringBuffer, i);
//                        stringBuffer.append(")");
//                    }
//                }
//            }
//        }
//        return stringBuffer.toString();
        return childDeptIdList;
    }

    private static void joinDeptParameter(List<List<String>> partition, StringBuffer stringBuffer, int i) {
        List<String> strings = partition.get(i);
        stringBuffer.append("(");
        for (String id : strings) {
            stringBuffer.append("'" + id + "',");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    @Override
    public AjaxResult selectUserByUserNamePub(AddUserRequest addUserRequest, String header) {
        boolean flag = this.decrypt(header, addUserRequest.getUserName(), addUserRequest.getAesAccount());
        if (!flag) {
            log.info("{} 非法请求!", addUserRequest.getUserName());
            return AjaxResult.error("非法请求");
        }
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserName(addUserRequest.getUserName());
        SysUser sysUser = userMapper.selectUserByUserName(dto);
        if ((sysUser == null) || StringUtils.isBlank(sysUser.getPhonenumber())) {
            return AjaxResult.error("用户名或密码错误！");
        }
        String aesPheone = AESUtil.encrypt(sysUser.getPhonenumber());
        Map<String, String> params = new HashMap<>();
        params.put("aes", aesPheone);
        params.put("phonenumber", sysUser.getPhonenumber().replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
        return AjaxResult.success(params);
    }


    @Override
    public AjaxResult updatePhone(AddUserRequest addUserRequest) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (SecurityUtils.isSwitch()) {
            //铁塔视角切换查询
            currentUser = userMapper.selectUserOnlyByUserName(currentUser.getUserName());
        }
        if (Objects.isNull(currentUser)) {
            log.error("更新用户手机号->用户不存在:{}", JSON.toJSONString(addUserRequest));
            return AjaxResult.error("修改失败");
        }
        //2.校验手机号验证码(兼容pc，此处为旧手机验证码)
        if (StringUtils.isEmpty(addUserRequest.getNewCode())) {
            Result checkResult = msgFeignClient.checkPhoneCode(currentUser.getPhonenumber(), addUserRequest.getCode(), CustomSmsTemplateConstants.UPDATE_PHONE_KEY, true);
            if (checkResult.getCode() == ResultCode.SUCCESS.getCode()) {
                updateUserPhone(currentUser.getUserId(), addUserRequest.getNewPhone());
                return AjaxResult.success("修改成功");
            } else {
                return AjaxResult.error(checkResult.getMsg());
            }
        } else {
            addUserRequest.setNewPhone(com.chinatower.platform.common.utils.security.AESUtil.decryptBase64(addUserRequest.getNewPhone()));
            //app校验新验证码
            Result checkResult = msgFeignClient.checkPhoneCode(addUserRequest.getNewPhone(), addUserRequest.getNewCode(), CustomSmsTemplateConstants.UPDATE_PHONE_KEY, true);
            if (checkResult.getCode() == ResultCode.SUCCESS.getCode()) {
                updateUserPhone(currentUser.getUserId(), addUserRequest.getNewPhone());
                return AjaxResult.success("修改成功");
            } else {
                return AjaxResult.error(checkResult.getMsg());
            }
        }
    }

    /**
     * 更新用户手机号
     *
     * @param newPhone 用户手机号
     */
    private void updateUserPhone(String userId, String newPhone) {
        SysUser newSysUser = new SysUser();
        newSysUser.setUserId(userId);
        newSysUser.setPhonenumber(newPhone);
        String password = passwordUtil.generatePassword();
        newSysUser.setPassword(SecurityUtils.encryptPassword(password));
        Map<String,Object> map = new HashMap<>();
        map.put("password",password);
        map.put("nodeCode",getPlatformNode());
            /*requestVO.setParaMap(JSON.toJSONString(map));
            msgFeignClient.innovationSendMessage(requestVO);*/
        CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
        commonSmsRequest.setMsgType(Constants.TWO);
        commonSmsRequest.setPhone(newSysUser.getPhonenumber());
        commonSmsRequest.setRequestMap(JSON.toJSONString(map));
        commonSmsRequest.setTemplateCode(templateCodeUpdatePassword);
        msgFeignClient.sendMessage(commonSmsRequest);
        int i = userMapper.updateUser(newSysUser);
        //更新缓存
        if (i == 1) {
            updateCacheUser(userId, x -> x.getUser().setPhonenumber(newPhone));
        }
    }

    /**
     * 更新用户缓存
     *
     * @param userId      用户id
     * @param cacheModify 缓存修改器
     */
    private void updateCacheUser(String userId, Consumer<LoginUser> cacheModify) {
        log.info("更新用户缓存->更新用户{}的用户缓存", userId);
        ClientType[] values = ClientType.values();
        for (ClientType clientType : values) {
            String cacheUserKey = clientType.getCacheUserKey(userId);
            Map<Object, Object> map = RedisUtil.hGetAll(cacheUserKey, RedisDBType.PRIMARY);
            if (Objects.nonNull(map)) {
                Map<String, Object> mulitMap = new HashMap<>();
                map.forEach((industryCode, cacheStr) -> {
                    LoginUser loginUser = JSON.parseObject(Objects.toString(cacheStr), LoginUser.class);
                    cacheModify.accept(loginUser);
                    mulitMap.put(Objects.toString(industryCode), JSON.toJSONString(loginUser));
//                    RedisUtil.hset(cacheUserKey, Objects.toString(industryCode), JSON.toJSONString(loginUser), RedisDBType.PRIMARY);
                });
                RedisUtil.hPutAll(cacheUserKey, mulitMap, RedisDBType.PRIMARY);
            }
        }
    }

    @Override
    public AjaxResult updatePassPublic(AddUserRequest addUserRequest) {
        //1根据账户获取手机号
        if (StrUtil.isEmpty(addUserRequest.getUserName())) {
            return AjaxResult.error("非法请求，账户不能为空");
        }
        UserQueryDTO dto = new UserQueryDTO();
        dto.setUserName(addUserRequest.getUserName());
        SysUser sysUser = userMapper.selectUserByUserName(dto);
        if (sysUser == null) {
            return AjaxResult.error("用户名或密码错误");
        }
        String userId = sysUser.getUserId();
        String userName = sysUser.getUserName();
        String oldPassWord = userMapper.selectPwdByUserName(addUserRequest.getUserName());

        //2.校验验证码
        String verifyKey = CommonConstants.SMS_SUCCESS_KEY + sysUser.getPhonenumber();
        String cacheCaptcha = RedisUtil.get(verifyKey, RedisDBType.PRIMARY);
        if (addUserRequest.getCode().equals(cacheCaptcha)) {
            //3.修改密码
            SysUser newSysUser = new SysUser();
            newSysUser.setUserId(sysUser.getUserId());
            if (StrUtil.isEmpty(addUserRequest.getNewPassWord())) {
                return AjaxResult.error("新秘密不能为空");
            }
            String newPassWord = RsaUtils.decryptByPrivateKey(addUserRequest.getNewPassWord());
            if (newPassWord.indexOf(" ") != -1) {
                return AjaxResult.error("密码不能带空格");
            }
            if (newPassWord.replace(" ", "").length() < 8) {
                return AjaxResult.error("密码必须设置不小于八位");
            }
            if (SecurityUtils.encryptPassword(newPassWord).equals(oldPassWord)) {
                return AjaxResult.error("新密码与旧密码不能相同");
            }
            newSysUser.setPassword(SecurityUtils.encryptPassword(newPassWord));
            int i = userMapper.updateUserPassword(newSysUser);
            this.forceLogOut(userId, userName);
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("验证码已失效");
        }
    }

    @Override
    public AjaxResult updatePass(String key, String password, String userName) {
        String val = RedisUtil.get(key, RedisDBType.PRIMARY);
        if (StringUtils.isBlank(val) || !val.equalsIgnoreCase("1")) {
            return AjaxResult.error("请先校验验证码");
        }
        String aesPhone = key.split(":")[2];
        String phone = AESUtil.decrypt(aesPhone);
        SysUser user = userMapper.selectUserOnlyByUserName(userName);
        if (user == null || !phone.equalsIgnoreCase(user.getPhonenumber())) {
            return AjaxResult.error("非法请求");
        }
        String userId = user.getUserId();
        String newPassWord = RsaUtils.decryptByPrivateKey(password);
        if (newPassWord.indexOf(" ") != -1) {
            return AjaxResult.error("密码不能带空格");
        }
        if (newPassWord.replace(" ", "").length() < 8) {
            return AjaxResult.error("密码必须设置不小于八位");
        }
        SysUser newSysUser = new SysUser();
        newSysUser.setUserId(user.getUserId());
        newSysUser.setPassword(SecurityUtils.encryptPassword(newPassWord));
        userMapper.updateUserPassword(newSysUser);
        RedisUtil.delete(key, RedisDBType.PRIMARY);
        this.forceLogOut(userId, userName);
        return AjaxResult.success("修改成功");
    }

    /**
     * 手机号解密加验证
     * 参数1为动态密钥，参数二为手机号,参数三为传输过来的加密的手机号
     */
    private boolean decrypt(String header, String userName, String AesUserName) {
        String keyPrefix = MD5Util.MD5Encode(userName, "utf-8").substring(10, 20);
        String key = keyPrefix + header;
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]{6}");
        Matcher matcher = pattern.matcher(header);
        if (!matcher.matches()) {
            return false;
        }
        String aesPhoneTemp = AESUtil.Base64encrypt(userName, key);
        if (!org.apache.commons.lang3.StringUtils.equals(AesUserName, aesPhoneTemp)) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前用户是否可以注销
     *
     * @param sysUser
     * @return
     */
    @Override
    public AjaxResult getLogoutFlag(SysUser sysUser) {
        String isTenantAdmin = sysUser.getIsTenantAdmin();
        String userId = sysUser.getUserId();
        AppOrderListByUserDTO dto = new AppOrderListByUserDTO();
        Result result = iTradeWarningOrderService.getTodoNum(dto);
        int code = result.getCode();
        if (code != 200) {
            return AjaxResult.error("查询报警信息报错", false);
        } else {
            Map<String, Object> resMap = (Map) result.getData();
            int eventNum = (int) resMap.get("eventNum");
            int leanderNum = (int) resMap.get("leanderNum");

            int flag;
            if (isTenantAdmin.equals("1")) {
                flag = 2;
                return AjaxResult.success("租户管理员不允许注销", flag);
            }
            if (sysUser.isAdminNew()) {
                flag = 1;
                return AjaxResult.success("超级管理员不允许注销", flag);
            }
            if (eventNum != 0 || leanderNum != 0) {
                flag = 3;
                return AjaxResult.success("有代办不允许注销", flag);
            } else {
                flag = 0;
                return AjaxResult.success("允许注销", flag);
            }
        }

    }

    //    @Override
    public List<SysUser> getUserByDeptAndDeviceScope1(GetUserByDeptAndDeviceScopeRequest request) {
        List<SysUser> list = Lists.newArrayList();
        List<String> userIdList = request.getUserIdList();
        log.debug("GetUserByDeptAndDeviceScopeRequest={}", JSON.toJSONString(request));
        for (String userId : userIdList) {
            UserQueryDTO dto = new UserQueryDTO();
            dto.setUserId(userId);
            String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
            dto.setPlatformCode(platformCode);
            SysUser sysUser = userMapper.selectUserById(dto);
            if (StringUtils.isBlank(sysUser.getAppVerCode())) {
                sysUser.setAppVerCode(request.getAppVerCode());
            }
            if (StringUtils.isBlank(sysUser.getIndustryCode())) {
                sysUser.setIndustryCode(request.getIndustryCode());
            }
            List<SysRole> sysRoles = roleMapper.selectRoleListByUserId(sysUser);
            sysUser.setRoles(sysRoles);
            Set<String> deptIdSet = getDeptListByUserDataScope(sysUser);
            if (CollectionUtils.isEmpty(deptIdSet)) {
                continue;
            }
            List<SysDeptDeviceVO> sysDeptDeviceVOList = getSysDeptDeviceVO(deptIdSet, request);
            if (CollectionUtils.isEmpty(sysDeptDeviceVOList)) {
                continue;
            }
            Optional<SysDeptDeviceVO> optional;
            if ("9".equals(request.getAlarmSource())) {
                optional = sysDeptDeviceVOList.stream().filter(k -> {
                    log.debug(REPLACE_STR, k.getProvinceId(), request.getProvinceId());
                    return StringUtils.equals(k.getProvinceId(), request.getProvinceId());
                }).filter(k -> {
                    log.debug(REPLACE_STR, k.getCityId(), request.getCityId());
                    return StringUtils.equals(k.getCityId(), request.getCityId());
                }).filter(k -> {
                    log.debug(REPLACE_STR, k.getCountyId(), request.getCountyId());
                    return StringUtils.equals(k.getCountyId(), request.getCountyId());
                }).findFirst();
            } else {
                optional = sysDeptDeviceVOList.stream().filter(k -> request.getDeviceCode().equals(k.getDeviceCode())).findFirst();
            }
            if (optional.isPresent()) {
                SysUser user = new SysUser();
                user.setUserId(sysUser.getUserId());
                user.setPhonenumber(sysUser.getPhonenumber());
                user.setDeptId(sysUser.getDeptId());
                list.add(user);
            }
        }
        return list;
    }

    @Override
    public List<SysUser> getUserByDeptAndDeviceScope(GetUserByDeptAndDeviceScopeRequest request) {
        log.info("GetUserByDeptAndDeviceScopeRequest={}", JSON.toJSONString(request));
        List<SysUser> list = Lists.newArrayList();
        List<String> userIdList = request.getUserIdList();
        String industryCode = request.getIndustryCode();
        //分批查用户、角色
        List<SysUser> userList = Lists.newArrayList();
        List<SysRole> rolesList = Lists.newArrayList();
        int userSize = userIdList.size();
        int userBatch = (int) Math.ceil(userSize * 1.0 / USER_LIST_NUMBER);
        for (int i = 0; i < userBatch; i++) {
            int indexStart = USER_LIST_NUMBER * i;
            int indexEnd = i == userBatch - 1 ? userSize : USER_LIST_NUMBER * (i + 1);
            List<String> ids = userIdList.subList(indexStart, indexEnd);
            String[] userIds = ids.toArray(new String[0]);
            //用户
            long begin1 = System.currentTimeMillis();
            List<SysUser> sysUsers = userMapper.selectUserOnlyByUserIds(userIds);
//            List<SysUser> sysUsers = userMapper.selectUserByIds(userIds);
            long end1 = System.currentTimeMillis();
            log.info("根据设备编号获取有权限的用户列表->查询用户{}-{}耗时{}ms", indexStart, indexEnd, (end1 - begin1));
            if (CollectionUtils.isNotEmpty(sysUsers)) {
                userList.addAll(sysUsers);
            }
            //角色
            long begin2 = System.currentTimeMillis();
            List<SysRole> sysRoles = roleMapper.selectRoleListByUserIds(ids, industryCode);
            long end2 = System.currentTimeMillis();
            log.info("根据设备编号获取有权限的用户列表->查询角色{}-{}耗时{}ms", indexStart, indexEnd, (end2 - begin2));
            if (CollectionUtils.isNotEmpty(sysRoles)) {
                rolesList.addAll(sysRoles);
            }
        }

        if(!("2").equals(request.getAlarmSource())&& !("11").equals(request.getAlarmSource())){
            //获取所有部门
            List<SysDept> allDeptList = getAllDepts(request);
            List<String> allDeptIds = allDeptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());

            //查询租户在指定行业的该告警类型的所有设备
            List<SysDeptDeviceVO> allDeviceList = getSysDeptDeviceVOAll(request);
            Map<String, List<SysDeptDeviceVO>> deptDeviceMap = allDeviceList.stream().collect(Collectors.groupingBy(SysDeptDeviceVO::getDeptId));

            Map<String, SysUser> userMap = userList.stream().collect(Collectors.toMap(SysUser::getUserId, x -> x));
            Map<String, Set<String>> roleScopeMap = rolesList.stream().collect(Collectors.groupingBy(SysRole::getUserId, Collectors.mapping(BaseDataScopeEntity::getDataScope, Collectors.toSet())));

            String alarmSource = request.getAlarmSource();
            String provinceId = request.getProvinceId();
            String cityId = request.getCityId();
            String countyId = request.getCountyId();
            String deviceCode = request.getDeviceCode();

            long begin3 = System.currentTimeMillis();
            for (String userId : userIdList) {
                SysUser sysUser = userMap.get(userId);
                if (Objects.isNull(sysUser)) {
                    continue;
                }
                Set<String> userDataScopes = roleScopeMap.getOrDefault(userId, Sets.newHashSet());
                Set<String> userDeptIds = Sets.newHashSet();
                String userDeptIdCsv = sysUser.getDeptId();
                if (StringUtils.isNotBlank(userDeptIdCsv)) {
                    String[] split = userDeptIdCsv.split(",");
                    for (String userDeptId : split) {
                        if (StringUtils.isNotBlank(userDeptId)) {
                            userDeptIds.add(userDeptId);
                        }
                        if (userDataScopes.contains(com.chinatower.platform.constants.Constants.DATA_SCOPE_ALL)) {
                            userDeptIds.addAll(allDeptIds);
                        } else if (userDataScopes.contains(com.chinatower.platform.constants.Constants.DATA_SCOPE_CUSTOM) || userDataScopes.contains(com.chinatower.platform.constants.Constants.DATA_SCOPE_DEPT_AND_CHILD)) {
                            userDeptIds.addAll(allDeptIds.stream().filter(x -> StringUtils.isNotBlank(x) && StringUtils.isNotBlank(userDeptId) && x.startsWith(userDeptId)).collect(Collectors.toSet()));
                        }
                    }
                }

                for (String deptId : userDeptIds) {
                    List<SysDeptDeviceVO> sysDeptDeviceVOList = deptDeviceMap.get(deptId);
                    if (CollectionUtils.isEmpty(sysDeptDeviceVOList)) {
                        continue;
                    }
                    Optional<SysDeptDeviceVO> optional;
                    if ("9".equals(alarmSource) || "10".equals(alarmSource)) {
                        optional = sysDeptDeviceVOList.stream().filter(k -> {
                            log.debug(REPLACE_STR, k.getProvinceId(), provinceId);
                            return StringUtils.equals(k.getProvinceId(), provinceId);
                        }).filter(k -> {
                            log.debug(REPLACE_STR, k.getCityId(), cityId);
                            return StringUtils.equals(k.getCityId(), cityId);
                        }).filter(k -> {
                            log.debug(REPLACE_STR, k.getCountyId(), countyId);
                            return StringUtils.equals(k.getCountyId(), countyId);
                        }).findFirst();
                    } else {
                        optional = sysDeptDeviceVOList.stream().filter(k -> deviceCode.equals(k.getDeviceCode())).findFirst();
                    }
                    if (optional.isPresent()) {
                        SysUser user = new SysUser();
                        user.setUserId(sysUser.getUserId());
                        user.setPhonenumber(sysUser.getPhonenumber());
                        user.setDeptId(userDeptIdCsv);
                        list.add(user);
                        break;
                    }
                }
            }
            long end3 = System.currentTimeMillis();
            log.info("根据设备编号获取有权限的用户列表->循环处理{}条数据耗时{}ms", userSize, (end3 - begin3));
        }else{
            if(!CollectionUtils.isEmpty(userList)){
                for (int i = 0; i < userList.size(); i++) {
                    SysUser user = new SysUser();
                    user.setUserId(userList.get(i).getUserId());
                    user.setPhonenumber(userList.get(i).getPhonenumber());
                    user.setDeptId(userList.get(i).getDeptId());
                    list.add(user);
                }
            }
        }
        //根据告警来源,告警类型,客户id,行业编码,组织id列表批量查询vm_organ_alarm_type_rel_t表，判断每个用户是否能查到告警类型，如果能查到，保留；查不到，删除
        if(!CollectionUtils.isEmpty(list) && !StringUtils.isEmpty(request.getTypeValue())){
            Set<String> deptIdSet = new HashSet<String>();
            for (int i = 0; i < list.size(); i++) {
                List<String> deptIdList = Arrays.asList(list.get(i).getDeptId().split(com.chinatower.platform.constants.Constants.COMMA));
                for (int j = 0; j < deptIdList.size(); j++) {
                    deptIdSet.add(deptIdList.get(j));
                }
            }
            List<String> deptIdList = deptMapper.getOrganListByAlarmType(request.getTenantId(),request.getIndustryCode(),
                    request.getAlarmSource(),request.getTypeValue(),deptIdSet);
            Iterator<SysUser> iterator = list.iterator();
            while (iterator.hasNext()) {
                SysUser tempSysUser = iterator.next();
                //当前遍历的用户的组织id列表
                List<String> curDeptIdList = Arrays.asList(tempSysUser.getDeptId().split(com.chinatower.platform.constants.Constants.COMMA));
                boolean flag = false;
                for (String deptId : deptIdList) {
                    if (curDeptIdList.contains(deptId)) {
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    iterator.remove();
                }
            }
        }

        return list;
    }


    private Set<String> getDeptListByUserDataScope(SysUser sysUser) {
        Set<String> deptList = new HashSet<>();
        deptList.add(sysUser.getDeptId());
        for (SysRole role : sysUser.getRoles()) {
            String roleDataScope = StrUtil.isNotBlank(role.getDataScope()) ? role.getDataScope() : com.chinatower.platform.constants.Constants.DATA_SCOPE_DEPT;
            switch (roleDataScope) {
                case com.chinatower.platform.constants.Constants.DATA_SCOPE_CUSTOM:
                    List<SysDept> sysDepts = selectTenantChildDept(sysUser);
                    deptList.addAll(sysDepts.stream().map(SysDept::getDeptId).collect(Collectors.toSet()));
                    break;
                case com.chinatower.platform.constants.Constants.DATA_SCOPE_DEPT_AND_CHILD:
                    List<SysDept> sysDepts1 = selectTenantChildDept(sysUser);
                    deptList.addAll(sysDepts1.stream().map(SysDept::getDeptId).collect(Collectors.toSet()));
                    break;
                case com.chinatower.platform.constants.Constants.DATA_SCOPE_ALL:
                    List<SysDept> sysDepts2 = selectAllTanentDept(sysUser);
                    deptList.addAll(sysDepts2.stream().map(SysDept::getDeptId).collect(Collectors.toSet()));
                    break;
                default:
                    break;
            }
        }
        return deptList;
    }

    private List<SysDept> getAllDepts(GetUserByDeptAndDeviceScopeRequest request) {
        SysUser sysUser = new SysUser();
        sysUser.setTenantId(request.getTenantId());
        sysUser.setIndustryCode(request.getIndustryCode());
        sysUser.setAppVerCode(request.getAppVerCode());
        return deptMapper.selectAllTanentDept(sysUser);
    }

    public List<SysDept> selectTenantChildDept(SysUser user) {
        return deptMapper.selectTenantChildDept(user.getDeptId());
    }

    private List<SysDept> selectAllTanentDept(SysUser user) {
        return deptMapper.selectAllTanentDept(user);
    }

    private List<SysDeptDeviceVO> getSysDeptDeviceVO(Set<String> deptIdSet, GetUserByDeptAndDeviceScopeRequest request) {
        String alarmSource = request.getAlarmSource();
        String tenantId = request.getTenantId();
        String industryCode = request.getIndustryCode();
        List<String> deptIds = new ArrayList<>(deptIdSet);

        if (StringUtils.equalsAny(alarmSource, "1", "3", "4", "10")) {
            return deptDeviceMapper.getCamerasTemp(tenantId, industryCode, deptIds);
        }

        if ("8".equals(alarmSource)) {
            return deptDeviceMapper.getSensorsTemp(tenantId, industryCode, deptIds);
        }

        if ("9".equals(alarmSource)) {
            return deptDeviceMapper.getSatellitesTemp(tenantId, industryCode, deptIds);
        }

        //if ("10".equals(alarmSource)) {
        //    return deptDeviceMapper.getRaders(dept);
        //}
        return Lists.newArrayList();
    }

    private List<SysDeptDeviceVO> getSysDeptDeviceVOAll(GetUserByDeptAndDeviceScopeRequest request) {
        String alarmSource = request.getAlarmSource();
        String tenantId = request.getTenantId();
        String industryCode = request.getIndustryCode();

        if (StringUtils.equalsAny(alarmSource, "1", "3", "4", "12")) {
            return deptDeviceMapper.getCamerasTempAll(tenantId, industryCode);
        }

        if ("8".equals(alarmSource)) {
            return deptDeviceMapper.getSensorsTempAll(tenantId, industryCode);
        }

        if ("9".equals(alarmSource)) {
            return deptDeviceMapper.getSatellitesTempAll(tenantId, industryCode);
        }

        if ("10".equals(alarmSource)) {
            return deptDeviceMapper.getStreetByRadarVO(tenantId, industryCode);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<SysIndustryViewRel> selectUserAppVerCodesByUserId(String userId,String platformCode) {
        return userMapper.selectUserAppVerCodesByUserId(userId,platformCode);
    }

    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "u.tenant_id", industryCode = "d.INDUSTRY_CODE",acceptColumn = "ic.accept_node")
    public List getDeptUserTree(SysUser sysUser,Integer flag) {
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        sysUser.setDept(loginUser.getDept());
        String searchKeyword = sysUser.getSearchKeyword();
        String industryCode = loginUser.getIndustryCode();
        String tenantId = loginUser.getTenantId();
        SysDept dept = new SysDept();
        dept.setTenantId(tenantId);
        dept.setDeptId(loginUser.getDeptId());
        List<SysDept> deptList = deptService.getDeptListAll(dept);
        deptList.sort(Comparator.comparing(SysDept::getDeptName, new PinYinComparator()));
        SysUser user = new SysUser();
        user.setUserType(UserTypeEnum.TANENT.getType());
        user.setTenantId(loginUser.getTenantId());
        user.setIndustryCode(loginUser.getIndustryCode());
        user.setAppVerCode(loginUser.getAppVerCode());
        user.setDataScope(sysUser.getDataScope());
        user.setStatus(sysUser.getStatus());
        user.setBeginTime(sysUser.getBeginTime());
        user.setEndTime(sysUser.getEndTime());
        user.setSearchKeyword(searchKeyword);
        List<SysUser> userList = userMapper.selectUserListOrderByName(user);
        //截取数字和特殊字符开头的
        List<SysUser> startWithNum = userList.stream().filter(e -> e.getNickName().matches("^[\\p{Punct}\\p{Digit}].*")).collect(Collectors.toList());
        userList = userList.stream().filter(e -> !e.getNickName().matches("^[\\p{Punct}\\p{Digit}].*")).collect(Collectors.toList());
        //排序
        userList.sort(Comparator.comparing(SysUser::getNickName, new PinYinComparator()).thenComparing(SysUser::getCreateTime, Comparator.reverseOrder()));
//            userList.sort(Comparator.comparing(SysUser::getCreateTime).reversed());
        if (CollectionUtils.isNotEmpty(startWithNum)) {
            startWithNum.sort(Comparator.comparing(SysUser::getCreateTime).reversed());
            userList.addAll(startWithNum);
        }
        List<String> userIds = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
        List<SysRole> roleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            roleList = roleMapper.selectRoleListByUserIds(userIds, industryCode);
        }
        if(flag == 1){
            return buildDeptUserTree(deptList, userList, roleList);
        }else{
            return buildDeptUserTree(deptList, userList, sysUser,flag);
        }
    }

    @Override
    public VerifyUpdateVO isNeedUpdate() {
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        String userId = loginUser.getUserId();
        SysUser sysUser = userMapper.selectUptimeById(userId);
        VerifyUpdateVO verifyUpdateVO = new VerifyUpdateVO();
        if (Objects.isNull(sysUser)) {
            log.info("该用户信息未找到:" + userId);
            return verifyUpdateVO;
        }
        Date pwdUpdateTime = sysUser.getPwdUpdateTime();
        // 处理 pwdUpdateTime 为 null 的情况（新用户或从未修改过密码的用户）
        if (pwdUpdateTime == null) {
            verifyUpdateVO.setNeedUpdate(Constants.ONE);
            verifyUpdateVO.setExpirationTime(null);
            verifyUpdateVO.setRemainingDays(null);
            return verifyUpdateVO;
        }
        // 从配置中获取密码有效期天数，默认180天（6个月）
        int passwordExpireDays = getPasswordExpireDays();
        // 计算密码过期时间 = 上次修改时间 + 配置天数
        Date expirationTime = stepDays(pwdUpdateTime, passwordExpireDays);
        Date date = new Date();
        int days = (int) ((expirationTime.getTime() - date.getTime()) / (1000 * 3600 * 24));
        if (days <= 15) {
            verifyUpdateVO.setNeedUpdate("1");
        } else {
            verifyUpdateVO.setNeedUpdate(ZERO_STRING);
        }
        verifyUpdateVO.setExpirationTime(expirationTime);
        verifyUpdateVO.setRemainingDays(days);
        return verifyUpdateVO;
    }

    /**
     * 从配置中获取密码有效期天数
     */
    private int getPasswordExpireDays() {
        try {
            // 配置key，可以根据实际需要定义
            String configKey = "expireDays";
            com.chinatower.platform.project.system.domain.SysConfig sysConfig = configService.selectConfigByKey(configKey);
            if (sysConfig != null && StringUtils.isNotBlank(sysConfig.getConfigValue())) {
                return Integer.parseInt(sysConfig.getConfigValue());
            }
        } catch (Exception e) {
            log.error("获取密码有效期配置失败，使用默认值180天", e);
        }
        // 默认180天
        return 180;
    }

    /**
     * 日期增加天数
     * @param sourceDate 源日期
     * @param days 增加的天数
     * @return 增加后的日期
     */
    public static Date stepDays(Date sourceDate, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    @Override
    @Transactional
    public ApiResult<String> synUserInfo(SyncUserDTO param) {
        log.info("SysUserServiceImpl synUserInfo param:{}", JSON.toJSONString(param));

        if (param == null) {
            log.error("SysUserServiceImpl checkSyncParam 参数不能为空！");
            throw new CustomException("请求参数不能为空！");
        }

        if (StringUtils.isEmpty(param.getTenantId())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数tenantId不能为空！");
            throw new CustomException("客户编码不能为空！");
        }

        if (StringUtils.isEmpty(param.getUserName())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数userName不能为空！");
            throw new CustomException("客户登录账号不能为空！");
        }

        // optType 0同步账号 1删除账号
        if (Objects.equals(param.getOptType(), ZERO_STRING)) {
            // 校验参数
            checkSyncParam(param);
            // 处理客户信息新增、更新逻辑
            handleModifyTenantInfoLogic(param);
            // 处理用户信息新增、更新逻辑
            handleModifyUserInfoLogic(param);

        } else if (Objects.equals(param.getOptType(), "1")) {
            String tenantId = param.getTenantId();
            String userName = param.getUserName();
            // 校验客户是否有有效订购
            int count = userMapper.countOrderInfoByTenantId(tenantId, userName);
            if (count > 0) {
                throw new CustomException("该客户下有订购的应用版本，不可删除！");
            }
            // 删除用户信息
//            userMapper.deleteByUserName(tenantId, userName);
            SysUser deleteUser = userMapper.selectUserOnlyByUserName(userName);
            if (Objects.nonNull(deleteUser)) {
                saveSysUserToMongoHis(Collections.singletonList(deleteUser.getUserId()));
            }
        }
        return ApiResult.SUCCESS("执行成功");
    }

    @Override
    public void forceLogOut(String userId, String userName) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("强制退出用户->强制退出用户{}（{}）", userName, userId);
                //小程序退出通知
                WxLogoutVo wxLogoutVo = new WxLogoutVo();
                wxLogoutVo.setUserId(userId);
                wxLogoutVo.setUserName(userName);
                wxFeignClient.invalidUser(wxLogoutVo);

                logoutOne(userId);
            } catch (Exception e) {
                log.error("强制退出用户->用户{}（{}）退出异常：{},", userName, userId, e.getMessage(), e);
            }
        });
    }

    @Override
    public List<SysUser> getTenantUsers(int deleteFlag) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //铁塔不返回数据
        if(UserTypeEnum.TOWER.getType().equalsIgnoreCase(user.getUserType())){
            return new ArrayList<>();
        }
        String tenantId = user.getTenantId();
        String industryCode = user.getIndustryCode();

        List<SysUser> list = new ArrayList<>();
        if (deleteFlag == ZERO) {
            list = userMapper.getTenantUsers(tenantId, industryCode);
        } else {
            list = userMapper.getTenantAllUsers(tenantId, industryCode);
        }

        for (int i = 0; i < list.size(); i++) {
            SysUser userItem = list.get(i);
            String phoneNumber = userItem.getPhonenumber();
            if (StringUtils.isNotEmpty(phoneNumber)) {
                userItem.setAesPhoneNumber(AESUtil.encrypt(phoneNumber));
                userItem.setPhonenumber(phoneNumber.replaceAll("(\\d{3})\\d*(\\d{4})", S1_S2));
            }
        }
        return list;
    }

    private void logoutOne(String userId){
        for (ClientType clientType : ClientType.values()) {
            String cacheAppKey = clientType.getCacheUserKey(userId);
            RedisUtil.delete(cacheAppKey, RedisDBType.PRIMARY);
            String userTokensKey = clientType.getCacheTokensKey(userId);
            Set<ZSetOperations.TypedTuple<String>> typedTuples = RedisUtil.zRangeByScoreWithScores(userTokensKey, Long.valueOf(System.currentTimeMillis()).doubleValue(), Double.MAX_VALUE, RedisDBType.PRIMARY);
            if (typedTuples != null) {
                long tokenSeconds = ClientType.WEB_MANAGE == clientType ? webRefreshTokenValiditySeconds : appRefreshTokenValiditySeconds;
                long defaultExpireMs = tokenSeconds * 1000;
                for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
                    String tokenOne = typedTuple.getValue();
                    Double expire = typedTuple.getScore();
                    //默认值，保证不报错
                    long time = defaultExpireMs;
                    if (expire != null) {
                        time = Math.max(expire.longValue() - System.currentTimeMillis(), 0);
                    }
                    String tokenMd5 = MD5Util.hash(tokenOne);
                    String tokenBlackListKey = clientType.getCacheTokenBlackListKey(tokenMd5);
                    RedisUtil.set(tokenBlackListKey, tokenOne, time, TimeUnit.MILLISECONDS, RedisDBType.PRIMARY);
                }
                //加黑名单后删除用户token缓存，防止值过大
                RedisUtil.delete(userTokensKey, RedisDBType.PRIMARY);
            }
        }
    }


    private static final Pattern PHONE_CHECK_PATTER = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");

    private static final Pattern USER_NAME_CHECK_PATTER = Pattern.compile("^[\\w-]{4,16}$");

    public static boolean checkTextPattern(Pattern pattern, String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private void checkSyncParam(SyncUserDTO param) {
        if (StringUtils.isEmpty(param.getTenantName())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数tenantName不能为空！");
            throw new CustomException("客户名称不能为空！");
        }
        if (StringUtils.isEmpty(param.getProvinceId())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数provinceId不能为空！");
            throw new CustomException("省份不能为空！");
        }
        if (StringUtils.isEmpty(param.getCityId())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数cityId不能为空！");
            throw new CustomException("地市不能为空！");
        }
        if (StringUtils.isEmpty(param.getDeptId())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数deptId不能为空！");
            throw new CustomException("铁塔组织编码不能为空！");
        }
        if (StringUtils.isEmpty(param.getPhoneNumber())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数phoneNumber不能为空！");
            throw new CustomException("联系电话不能为空！");
        }

        if (StringUtils.isEmpty(param.getNickName())) {
            log.error("SysUserServiceImpl checkSyncParam 必填参数nickName不能为空！");
            throw new CustomException("用户姓名不能为空！");
        }

        if (!checkTextPattern(USER_NAME_CHECK_PATTER, param.getUserName())) {
            log.error("SysUserServiceImpl checkSyncParam 用户账号格式不正确, userName:{}", param.getUserName());
            throw new CustomException("客户登录账号格式不正确，账号必须为4-16位(字母/数字/下划线/减号)！");
        }

        if (!checkTextPattern(PHONE_CHECK_PATTER, param.getPhoneNumber())) {
            log.error("SysUserServiceImpl checkSyncParam 手机号格式不正确, phoneNumber:{}", param.getPhoneNumber());
            throw new CustomException("联系电话格式不正确！");
        }

        if (StringUtils.isEmpty(param.getTenantName())) {
            log.error("SysUserServiceImpl checkSyncParam 客户名称不能为空");
            throw new CustomException("客户名称不能为空！");
        }
    }

    private void handleModifyTenantInfoLogic(SyncUserDTO param) {
        SysTenantInfo info = sysTenantInfoService.selectByTenantId(param.getTenantId());
        if (info != null) {
            info.setCityId(param.getCityId());
            info.setProvinceId(param.getProvinceId());
            info.setUpdateBy(SYSTEM_YG);
            info.setUpdateTime(new Date());
            sysTenantInfoService.updateSysTenantById(info);
        } else {
            // 初始化客户表
            info = new SysTenantInfo();
            info.setCityId(param.getCityId());
            info.setProvinceId(param.getProvinceId());
            info.setDeptId(param.getDeptId());
            info.setTradeInfo(null);
            info.setTenantName(param.getTenantName());
            info.setDelFlag(OrderConstants.DELETE_FLAG_NORMAL);
            info.setTenantTrades(Lists.newArrayList());
            info.setTenantId(param.getTenantId());
            info.setCreateBy(SYSTEM_YG);
            info.setCreateTime(new Date());
            sysTenantInfoService.insertSysTenantInfo4Sync(info);
        }
    }

    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    private void handleModifyUserInfoLogic(SyncUserDTO param) {
        SysUser user = userMapper.selectUserInfoByUserName(param.getUserName());

        // 只处理新增，不处理更新
        if (user == null) {
            // tenantId 下没有管理员账号才能新增，有则报错
            if (userMapper.countAdminUserNameByTenantId(param.getTenantId()) > 0) {
                throw new CustomException("该客户下已有管理员账号，同步失败");
            }

            // 初始化用户表
            user = new SysUser();
            user.setTenantId(param.getTenantId());
            user.setUserName(param.getUserName());
            user.setDeptId(param.getDeptId());
            user.setIsTenantAdmin(OrderConstants.IS_TENANT_ADMIN);
            user.setStatus(SysConstants.USET_STATUS_0);
            user.setUserType(SysConstants.USET_TYPE_2);
            user.setNickName(param.getNickName());
            user.setSex(param.getSex());
            user.setPhonenumber(param.getPhoneNumber());
            user.setCreateBy(SYSTEM_YG);
            user.setCreateTime(new Date());
            // 生成userId
            user.setUserId(IdUtils.fastSimpleUUID());
            String password = passwordUtil.generatePassword();
            //user.setPassword(SecurityUtils.encryptPassword(configService.selectConfigValueByKey(SYS_USER_INITPASSWORD)));
            user.setPassword(SecurityUtils.encryptPassword(password));
            userMapper.insertUser(user);
        } else {
            if (!StringUtils.equals(user.getTenantId(), param.getTenantId())
                    || !StringUtils.equals(user.getIsTenantAdmin(), "1")) {
                throw new CustomException("该客户账号在通用平台已存在，同步账号失败！");
            }

            String userName = userMapper.selectAdminUserNameByTenantId(user.getTenantId());
            if (StringUtils.isNotEmpty(userName) && !StringUtils.equals(userName, param.getUserName())) {
                throw new CustomException("该客户下已有管理员账号，同步失败");
            }
        }
    }


    private List<DeptUserGroupVO> buildDeptUserTree(List<SysDept> deptList, List<SysUser> userList,SysUser sysUser,Integer flag) {
        //判断是否需要筛选用户
        if (CollectionUtils.isNotEmpty(sysUser.getUserIdList()) && sysUser.getUserIdList().size() > 0) {
            userList = userList.stream().filter(e ->
                    sysUser.getUserIdList().contains(e.getUserId())
            ).collect(Collectors.toList());
        }
        //排除无用户部门
        Set<String> deptIdSet = userList.stream().map(SysUser::getDeptId).collect(Collectors.toSet());
        List<String> ancestorList = deptList.stream().filter(e -> deptIdSet.contains(e.getDeptId())).collect(Collectors.toList()).stream().map(SysDept::getAncestors).collect(Collectors.toList());
        List<String> deptIdList = new ArrayList();
        for(String ancestor : ancestorList){
            if (ancestor.contains(",")){
                String[] split = ancestor.split(",");
                Collections.addAll(deptIdList, split);
            }else{
                deptIdList.add(ancestor);
            }
        }
        deptIdList.addAll(new ArrayList<String>(deptIdSet));
        deptList = deptList.stream().filter(e ->deptIdList.contains(e.getDeptId())).collect(Collectors.toList());
        List<DeptUserGroupVO> deptUserGroupVOS = new ArrayList<>();
        deptList.forEach(e->{
            DeptUserGroupVO deptUserGroupVO = new DeptUserGroupVO();
            deptUserGroupVO.setName(e.getDeptName());
            deptUserGroupVO.setParentId(e.getParentId());
            deptUserGroupVO.setFlag(1);
            deptUserGroupVO.setId(e.getDeptId());
            deptUserGroupVOS.add(deptUserGroupVO);
        });
        //判断是否需要区分控制台或移动端
        if(flag == 3){
            userList.forEach(e->{
                DeptUserGroupVO deptUserGroupVO = getEmergencyGroupVO(e);
                deptUserGroupVOS.add(deptUserGroupVO);
            });
        }else{
            userList.forEach(e->{
                if (StringUtils.isNotBlank(e.getMobileTerminalNum()) && StringUtils.isNotBlank(e.getDispatchDeskNum())) {
                    DeptUserGroupVO dispatchDeskGroup = getEmergencyGroupVO(e);
                    //调度台
                    dispatchDeskGroup.setNumberMarking("1");
                    dispatchDeskGroup.setCorrespondenceNumber(e.getDispatchDeskNum());
                    deptUserGroupVOS.add(dispatchDeskGroup);
                    DeptUserGroupVO mobileGroup = getEmergencyGroupVO(e);
                    //移动端
                    mobileGroup.setCorrespondenceNumber(e.getMobileTerminalNum());
                    mobileGroup.setNumberMarking("2");
                    deptUserGroupVOS.add(mobileGroup);
                } else {
                    DeptUserGroupVO emergencyGroupVO = getEmergencyGroupVO(e);
                    if (StringUtils.isNotBlank(e.getMobileTerminalNum())) {
                        emergencyGroupVO.setCorrespondenceNumber(e.getMobileTerminalNum());
                        emergencyGroupVO.setNumberMarking("2");
                    } else if (StringUtils.isNotBlank(e.getDispatchDeskNum())) {
                        emergencyGroupVO.setCorrespondenceNumber(e.getDispatchDeskNum());
                        emergencyGroupVO.setNumberMarking("1");
                    }
                    deptUserGroupVOS.add(emergencyGroupVO);
                }
            });
        }
        Comparator<DeptUserGroupVO> comparator = Comparator.comparing(DeptUserGroupVO::getFlag);
        List<DeptUserGroupVO> collect = deptUserGroupVOS.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        JSONArray jsonArray = TreeUtils.listToTree(collect, "id", "parentId", "child");
        List<DeptUserGroupVO> deptUserGroupVOList = JSON.parseArray(jsonArray.toJSONString(), DeptUserGroupVO.class);
        return deptUserGroupVOList;
    }

    private  DeptUserGroupVO getEmergencyGroupVO(SysUser e) {
        DeptUserGroupVO deptUserGroupVO = new DeptUserGroupVO();
        deptUserGroupVO.setName(e.getNickName());
        deptUserGroupVO.setUserName(e.getUserName());
        deptUserGroupVO.setParentId(e.getDeptId());
        deptUserGroupVO.setFlag(2);
        deptUserGroupVO.setId(e.getUserId());
        deptUserGroupVO.setPhone(com.chinatower.platform.common.utils.security.AESUtil.encrypt(e.getPhonenumber()));
        deptUserGroupVO.setAvatar(fileUrlEncryptUtil.dealObsFileUrl(e.getAvatar()));
        return deptUserGroupVO;
    }

    private List<DeptUserTreeDto> buildDeptUserTree(List<SysDept> deptList, List<SysUser> userList, List<SysRole> roleList) {
        Map<String, DeptUserTreeDto> deptMap = deptList.stream().map(DeptUserTreeDto::new).collect(Collectors.toMap(DeptUserTreeDto::getDeptId, x -> x, (t1, t2) -> t1, LinkedHashMap::new));
        Map<String, List<String>> userRoleMap = roleList.stream().collect(Collectors.groupingBy(BaseIndustryEntity::getUserId, Collectors.mapping(SysRole::getRoleName, Collectors.toList())));
        Set<String> collect = userList.stream().map(SysUser::getAvatar).collect(Collectors.toSet());
        Map<String, String> avatarMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(collect));
        for (SysUser user : userList) {
            user.setAvatar(avatarMap.get(user.getAvatar()));
            String userDeptId = user.getDeptId();
            DeptUserTreeDto.User userDto = new DeptUserTreeDto.User(user, userRoleMap.get(user.getUserId()));
            for (String deptId : userDeptId.split(",")) {
                if (deptMap.containsKey(deptId)) {
                    deptMap.get(deptId).getUsers().add(userDto);
                }
            }
        }
        List<DeptUserTreeDto> list = new ArrayList<>(deptMap.values());
        JSONArray jsonArray = TreeUtils.listToTree(list, "deptId", "parentId", "children");
        return JSON.parseArray(jsonArray.toJSONString(), DeptUserTreeDto.class);
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        emptyNames.add("roles");
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static BigDecimal maxNum(SysUser sysUser, String searchKey) {
        //字段原长度
        int nickLength = sysUser.getNickName().length();
        int nameLength = sysUser.getUserName().length();
        int phoneLength = sysUser.getPhonenumber().length();
        //关键字长度
        int keyLength = searchKey.length();
        //替换关键字之后的长度
        int nickReplace = sysUser.getNickName().replace(searchKey, "").length();
        int nameReplace = sysUser.getUserName().replace(searchKey, "").length();
        int phoneReplace = sysUser.getPhonenumber().replace(searchKey, "").length();
        //原长度减去替换后的长度
        int nickEnd = nickLength - nickReplace;
        int nameEnd = nameLength - nameReplace;
        int phoneEnd = phoneLength - phoneReplace;
        BigDecimal nickRatio = BigDecimal.valueOf(nickEnd).divide(BigDecimal.valueOf(nickLength), 2, RoundingMode.HALF_UP);
        BigDecimal nameRatio = BigDecimal.valueOf(nameEnd).divide(BigDecimal.valueOf(nameLength), 2, RoundingMode.HALF_UP);
        BigDecimal phoneRatio = BigDecimal.valueOf(phoneEnd).divide(BigDecimal.valueOf(phoneLength), 2, RoundingMode.HALF_UP);
//        nickRatio = (nickRatio.compareTo(BigDecimal.ONE)) > 0 ? nickRatio : BigDecimal.ZERO;
//        nameRatio = (nameRatio.compareTo(BigDecimal.ONE)) > 0 ? nameRatio : BigDecimal.ZERO;
//        phoneRatio = (phoneRatio.compareTo(BigDecimal.ONE)) > 0 ? phoneRatio : BigDecimal.ZERO;
        BigDecimal maxRatio = nickRatio.max(nameRatio).max(phoneRatio);
        return maxRatio;
    }


    /**
     * 查询登录账号的管理区域
     *
     * @param deptStreetVO
     * @return
     */
    @Override
    public List<DeptStreetVO> getRegionTreeNew(DeptStreetVO deptStreetVO) {
        return userMapper.getRegionTreeNew(deptStreetVO);
    }

    @Override
    public Integer checkLoginIp(SysUser user) throws Exception {
        try {
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            user.setUpdateBy(sysUser.getUserName());
            user.setUpdateTime(new Date());
            return userMapper.updateUser(user);
        } catch (Exception e) {
            log.error("用户登录IP白名单弹窗设置用户是否校验白名单",e);
            throw new Exception(com.chinatower.event.constants.Constants.DEFAULT_ERROR_MESSAGE);
        }
    }

    /**
     * 推送4a用户（客户）
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> userPush4A(SysUser4A sysUser4A) {
        log.info("*************推送4a用户（客户）入参***************："+ JSON.toJSONString(sysUser4A));
        try{
            if (StringUtils.isEmpty(sysUser4A.getUserName())) {//用户账号
                return Result.fail(SysConstants.USERNAME_EMPTY_MSG);
            }
            if (StringUtils.isEmpty(sysUser4A.getFlag())) {
                return Result.fail(SysConstants.FLAG_EMPTY_MSG);
            }
            //1:新增；2:修改；3：删除；
            String flag = sysUser4A.getFlag();
            if (!(SysConstants.num_str_3).equals(flag)) {
                if (sysUser4A.getDeptId().equalsIgnoreCase(SysConstants.num_str_0)) {
                    return Result.fail(SysConstants.DEPT_ERROR);
                }
                if ((sysUser4A.getRoleIds() == null) || (sysUser4A.getRoleIds().length == 0)) {
                    return Result.fail(SysConstants.ROLE_EMPTY_MSG);
                }
                String platformCode = "";
                SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
                if (ObjectUtil.isNotNull(sysConfig)) {
                    platformCode = sysConfig.getConfigValue();
                }
                SysUser user=new SysUser();
                user.setPageNum(SysConstants.ONE);
                user.setPageSize(SysConstants.TEN);
                //判断该人员是否存在
                UserQueryDTO dto = new UserQueryDTO();
                dto.setUserName(sysUser4A.getUserName());
                SysUser sysUserReturn = userMapper.selectUserByUserNameNoAuth(dto);
                user.setUserName(sysUser4A.getUserName());
                user.setNickName(sysUser4A.getNickName());
                user.setDeptId(sysUser4A.getDeptId());
                user.setEmail(sysUser4A.getEmail());
                user.setSex(sysUser4A.getSex());
                user.setPhonenumber(sysUser4A.getPhonenumber());
                user.setTelPhone(sysUser4A.getTelPhone());
                user.setRoleIds(sysUser4A.getRoleIds());
                user.setPassword(SecurityUtils.encryptPassword(sysUser4A.getPassword()));
                if(ObjectUtils.isNotEmpty(sysUserReturn)){
                    //判断 如果存在的历史账号是铁塔账号 则不更新
                    if(SysConstants.USET_TYPE_2.equals(sysUserReturn.getUserType())){
                        user.setStatus(sysUser4A.getStatus());
                        user.setUpdateBy(SysConstants.SYSTEM_YQ_STR);
                        user.setUpdateTime(new Date());
                        userMapper.updateUserByUserName(user);
                        // 删除用户与角色关联
                        userRoleMapper.deleteUserRoleByUserId(sysUserReturn.getUserId());
                        user.setUserId(sysUserReturn.getUserId());
                        // 新增用户与角色关系
                        insertUserRole(user);
                    }
                }else{
                    //创建用户
                    if(checkSensitiveWords(user.getUserName())){
                        return Result.fail(com.chinatower.platform.project.system.constants.Constants.ACCOUNT_NOT_RULE);
                    }
                    user.setCreateTime(new Date());
                    user.setCreateBy(SysConstants.SYSTEM_YQ_STR);
//                    user.setSource(SysConstants.num_str_1);
                    user.setUserSource(SysConstants.num_str_0);
                    user.setTowerUserSource(null);
                    user.setStatus(SysConstants.num_str_0);
                    user.setUserType(SysConstants.num_str_2);
                    user.setPwdUpdateTimeFlag(SysConstants.num_str_1);//密码修改时间标识传"1"代表将此字段置为未来时间，首次登录后无需改密
                    SimpleDateFormat format = new SimpleDateFormat(SysConstants.YMDHMS);
                    Date date = format.parse(SysConstants.TWO_ZERO_NINE_NINE);
                    user.setPwdUpdateTime(date);
                    user.setTenantId(sysUser4A.getTenantId());
                    user.setUserId(IdUtils.fastSimpleUUID());
                    //默认插入的不是客户管理员
                    userMapper.insertUser(user);
                    // 新增用户与角色关系
                    insertUserRole(user);
                }
            } else {
//                userMapper.deleteUserByName(sysUser4A.getUserName());
                SysUser deleteUser = userMapper.selectUserOnlyByUserName(sysUser4A.getUserName());
                if (Objects.nonNull(deleteUser)) {
                    saveSysUserToMongoHis(Collections.singletonList(deleteUser.getUserId()));
                }
            }
        }catch(Exception e){
            return Result.fail(e.getMessage());
        }
        return Result.success(SysConstants.PUSH_SUCCESS_MSG);
    }

    /**
     * 校验用户名称是否包含敏感词汇
     */
    @Override
    public Boolean checkSensitiveWords(String userName) {
        Boolean flag = false;
        if(!StringUtils.isEmpty(userName)){
            List<SysDictData> list = dictDataService.selectDictDataByType(com.chinatower.platform.project.system.constants.Constants.ACCOUNT_SENSITIVE_WORDS);
            if(!org.springframework.util.CollectionUtils.isEmpty(list)){
                for (int i = 0; i < list.size(); i++) {
                    if(userName.toLowerCase().contains(list.get(i).getDictValue().toLowerCase())){
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "u.tenant_id", industryCode = "d.INDUSTRY_CODE",acceptColumn = "ic.accept_node")
    public List<SysUser> getUserListByDept(SysUser user) {
        Integer pageNum = user.getPageNum();
        Integer pageSize = user.getPageSize();
        if (com.chinatower.common.utils.StringUtils.isNotNull(pageNum) && com.chinatower.common.utils.StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize, user.getOrderBy());
        }
        List<SysUser> list = userMapper.selectUserList(user);
        return list;
    }

    @Override
    public TenantIdByTowerAccount getTenantIdByTowerAccount() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        TenantIdByTowerAccount tenantIdByTowerAccount = new TenantIdByTowerAccount();
        // 根据当前人直属单位进行组织信息查询
        if (user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType())) {
            String deptId = user.getDeptId().substring(0, 6);
            SysDept sysDept = deptMapper.selectDeptOnlyById(deptId);
            if (ObjectUtils.isNotEmpty(sysDept)) {
                if (DEPT_IDS.contains(sysDept.getDeptId())) {
                    //返回总部铁塔标识
                    tenantIdByTowerAccount.setHeadquarters(true);
                } else {
                    //查询该组织下的客户
                    tenantIdByTowerAccount.setHeadquarters(false);
                    sysDept.setDeptLevelInt(Integer.parseInt(sysDept.getDeptLevel()));
                    List<String> deptList = deptMapper.getDeptByCurrentDeptId(sysDept);
                    if (!deptList.isEmpty()) {
                        List<String> tenantIds = deptMapper.getTenantByTowerDeptId(deptList);
                        if (!tenantIds.isEmpty()) {
                            tenantIdByTowerAccount.setTenantIds(tenantIds);

                        }
                    }
                }
                return tenantIdByTowerAccount;
            }
        }
        return null;
    }


    @Override
    public HeroVO getHeroBaseInfo(HeroVO heroVO) {
        HeroVO returnHeroVO = new HeroVO();
        returnHeroVO.setPInfo(pInfo);
        returnHeroVO.setRInfo(rInfo);
        returnHeroVO.setIInfo(iInfo);
        returnHeroVO.setAInfo(aInfo);
        returnHeroVO.setCInfo(cInfo);
        return returnHeroVO;
    }

    @Override
    public AreaInfoResultVO queryAreaWideList(DeptStreetVO deptStreetVO) {
        try {
            List<AreaDetailInfo> areaDetailList = userMapper.getFistAreaInfoList(deptStreetVO);
            List<AreaDetailInfo> fistData = new ArrayList<>();
            List<AreaDetailInfo> flatList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(areaDetailList)){
                //整合首字母信息
                for(AreaDetailInfo areaDetailInfo : areaDetailList){
                    if(null != areaDetailInfo){
                        String areaCode = areaDetailInfo.getAreaCode();
                        String areaName = areaDetailInfo.getAreaName();
                        if(StringUtils.isNotBlank(areaCode) && StringUtils.isNotBlank(areaName)){
                            fistData.add(new AreaDetailInfo(areaCode, areaName));
                        }
                    }

                }
                // 省份信息单独处理
                if ("2".equals(deptStreetVO.getAreaLevel())) {
                    List<AreaDetailInfo> areaProviceDetailList = userMapper.getAreaInfoList(deptStreetVO);
                    // 省份特殊处理：包含省份及其下属城市
                    for (AreaDetailInfo areaDetailInfo : areaProviceDetailList) {
                        if(null != areaDetailInfo){
                            String cityCode  = areaDetailInfo.getAreaCode();
                            String cityName = areaDetailInfo.getAreaName();
                            String provinceName = areaDetailInfo.getProvince();
                            String provinceCode = areaDetailInfo.getProvinceCode();
                            if(StringUtils.isNotBlank(cityCode) && StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(provinceName) && StringUtils.isNotBlank(provinceCode)){
                                flatList.add(new AreaDetailInfo(cityCode, cityName, provinceName, provinceCode));
                            }
                        }
                    }
                } else {
                    // 其他类型直接返回平铺列表
                    flatList = fistData;
                }
                // 对分组数据按首字母排序
                List<AreaDetailInfo> groupedData = fistData.stream()
                        .sorted((g1, g2) -> {
                            int letterCompare = g1.getFirstLetter().compareToIgnoreCase(g2.getFirstLetter());
                            if (letterCompare != 0) {
                                return letterCompare;
                            }
                            return compareChinese(g1.getAreaName(), g2.getAreaName());
                        })
                        .collect(Collectors.toList());

                return new AreaInfoResultVO(groupedData, flatList);
            }
        }catch (Exception e) {
            log.error("查询区域信息列表出错", e);
        }
        return new AreaInfoResultVO(new ArrayList<>(),new ArrayList<>());
    }

    @Override
    public List<DeptStreetVO> queryAreaByKeyWordsList(DeptStreetVO deptStreetVO) {
        List<DeptStreetVO> areaProviceDetailList = userMapper.getAreaInfoByKeyWordsList(deptStreetVO);
        return areaProviceDetailList;
    }

    @Override
    public Integer queryAreaStreetList(DeptStreetVO deptStreetVO) {
        Integer count = 0;
        List<AreaStreetInfo> areaDetailList = userMapper.getAreaStreetList(deptStreetVO);
        if(areaDetailList != null && areaDetailList.stream().anyMatch(Objects::nonNull)){
            count = 1;
            return count;
        }
        return count;
    }

    /**
     * 中文字符串比较方法
     */
    private static int compareChinese(String s1, String s2) {
        Collator collator = Collator.getInstance(Locale.CHINESE);
        return collator.compare(s1, s2);
    }

    private int saveSysUserToMongoHis(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return userIdList.size();
        }
        List<SysUserHisMongoDTO> infos = userMapper.selectByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(infos)) {
            return infos.size();
        }
        sysUserHisMongoDAO.insertAll(infos, MongoConstants.MONGO_TABLE_NAME_SYS_USER_HIS, MongoDBType.PRIMARY);
        return userMapper.deletePhysicalByUserIdList(userIdList);
    }
}
