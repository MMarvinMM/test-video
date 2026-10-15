package com.chinatower.platform.aspect;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.deal.DataTypeDeal;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.dataScope.CustomDataScopeEntity;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.feign.CustomDataFeignClient;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 数据过滤处理
 *
 * @author iwhalecloud
 */
@Aspect
@Component
@Order(2)
@Slf4j
@SuppressWarnings("all")
public class DataPermissionsAspect {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    private static final String CUSTOM_DATA_SCOPE = "custom_data_cope";

    private static final String LAYER_COLUMN = "layerColumn";



    @Autowired(required = false)
    private DataTypeDeal dataTypeDeal;

    @Autowired(required = false)
    private AuthBaseMapper authBaseMapper;

    @Resource
    private CustomDataFeignClient customDataFeignClient;

    /**
     * 前置通知
     * @author 李峥
     * @param point
     * @param dataScope
     */
    @Before("@annotation(dataPermissions)")
    public void doBefore(JoinPoint point, DataPermissions dataPermissions) {
        handleDataScope(point, dataPermissions);
    }

    /**
     * 执行数据权限条件拼装
     * @author 李峥
     * @param joinPoint
     * @param dataScope
     */
    protected void handleDataScope(final JoinPoint joinPoint,DataPermissions dataScope) {
        BaseDataScopeEntity entity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        entity.setDataPermissions("");

        if (dataScope == null) {
            return;
        }
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return;
        }
        SysUser currentUser = loginUser.getUser();
        if (currentUser != null) {
            // 如果是超级管理员，则不过滤数据
            if (!currentUser.isAdminNew()) {
                dataScopeFilter(joinPoint, currentUser, dataScope,loginUser.getSwitchdLoginUser());
            }else{
                BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
                baseEntity.setDataPermissions("");
            }
        }
    }

    public Set<String> getDeptListByUserDataScope(String dbType,SysUser sysUser) {
        Set<String>  deptList=new HashSet<>();
        deptList.add(sysUser.getDeptId());
        List<SysRole> roles = sysUser.getRoles();
        if(CollectionUtils.isEmpty(roles)){
            return deptList;
        }
        List<String> dataScopes = roles.stream().map(BaseDataScopeEntity::getDataScope).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if(dataScopes.contains(Constants.DATA_SCOPE_ALL)){
            List<String> treeselectByTenantId = getTenantAllDeptInParameter(sysUser,dbType);
            deptList.addAll(treeselectByTenantId);
        }else{
            for (String dataScope : dataScopes) {
                switch (dataScope) {
                    case Constants.DATA_SCOPE_CUSTOM:
                        List<String> sysDepts = getTenantCutomRoleDeptInParameter(sysUser,dbType);
                        deptList.addAll(sysDepts);
                        break;
                    case Constants.DATA_SCOPE_DEPT:
                        List<String> sysDepts0 = Collections.singletonList(sysUser.getDeptId());
                        deptList.addAll(sysDepts0);
                        break;
                    case Constants.DATA_SCOPE_DEPT_AND_CHILD:
                        List<String> sysDepts1 = getTenantChildDeptInParameter(sysUser,dbType);
                        deptList.addAll(sysDepts1);
                        break;
                    default:
                        break;
                }
            }
        }
        return deptList;
    }


    private boolean isMgr(SysUser user){
        for(SysRole role:user.getRoles()){
            if(role != null && StringUtils.isNotBlank(role.getDataScope())){
                if(role.getDataScope().equalsIgnoreCase(Constants.DATA_SCOPE_ALL)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param dataScope 切面注解
     */
    public void dataScopeFilter(JoinPoint joinPoint, SysUser user,DataPermissions dataScope,LoginUser switchUser) {
        /* 租户表的别名 */
        /* 租户字段名 默认tenant_id */
        String tenantColumn = dataScope.tenantColumn();
        /* 设备表的别名 */
        String layerColumn = dataScope.layerColumn();

        HttpServletRequest request = RequestUtils.getRequest();
        //当前请求路径
        String requestURI = request.getRequestURI();
        BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        /* 用户类型 0铁塔 2租户 */
        String userType = user.getUserType();
        SysDept dept = user.getDept();

        StringBuffer stringBuffer = new StringBuffer();

        //查询当前节点编码
        String platformCode = "";
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
        if(!ObjectUtils.isEmpty(config)) {
            platformCode = config.getConfigValue();
        }

        //保存支持自定义的传入的数据字段名，字段值
        CustomDataScopeEntity customDataScopeEntity = new CustomDataScopeEntity();
        Set<String> supportAttrs = new HashSet<>();

        //租户用户数据权限过滤
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(userType)){
            String isTenantAdmin = user.getIsTenantAdmin();
            String tenantId = user.getTenantId();
            // 如果租户管理员,则不过滤租户部门权限
            if (StringUtils.isNotBlank(tenantColumn)) {
                stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                baseEntity.setDataPermissions(stringBuffer.toString());
            }
            if(StringUtils.isNotBlank(dataScope.industryCode())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), user.getIndustryCode()));
                baseEntity.setDataPermissions(stringBuffer.toString());
            }

            if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                baseEntity.setDataPermissions(stringBuffer.toString());
            }

            // 如果非租户管理员,则滤部门权限;;;如果是客户管理员并配置全部数据权限，则与租户管理员相同权限
            if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin) && !isMgr(user)) {
                Set<String> deptIdSet = getDeptListByUserDataScope(dataScope.dbType(), user);

                // 设备情况下直接 租户+设备权限过滤
                if (StringUtils.isNotBlank(layerColumn)) {
                    List<String> layerIds = getOrgLayersByType(dataScope.dbType(),  deptIdSet, user.getIndustryCode());
                    supportAttrs.add(LAYER_COLUMN);
                    customDataScopeEntity.setLayerIds(layerIds);

//                    stringBuffer.append(StringUtils.format(" and {} IN {} ", layerColumn, joinDeviceParameter(layerIds)));

//                    baseEntity.setDataPermissions(stringBuffer.toString());
                }
            }
        }

        //判断开关 是否要替换自定义权限
        String value;
        try {
            value = authBaseMapper.getConfigByType(CUSTOM_DATA_SCOPE);
            log.info("获取自定义权限开关:{}", value);
        } catch (Exception e) {
            value = null;
            log.info("获取自定义权限开关报错：{}", e.getMessage());
        }

        if (null != value && "1".equals(value)) {
            //调用接口 替换自定义权限
            try {
                log.info("切面中开始服务间调用");

                customDataScopeEntity.setSupportAttrs(supportAttrs);

                log.info("请求自定义权限接口入参:{}", JSON.toJSONString(customDataScopeEntity));
                Result<CustomDataScopeEntity> sysNoticeStatus = customDataFeignClient.customDataScope(customDataScopeEntity);
                log.info("请求自定义权限接口返回:{}", JSON.toJSONString(sysNoticeStatus));
                log.info("替换值之前的sql:{}", JSON.toJSONString(baseEntity.getDataScope()));
                log.info("替换值之前的criteriaList:{}", JSON.toJSONString(baseEntity.getCriteriaList()));
                if (sysNoticeStatus.getCode() == 200) {
                    CustomDataScopeEntity data = sysNoticeStatus.getData();
                    dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, data);
                } else {
                    dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, customDataScopeEntity);
                }
            } catch (Exception e) {
                dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, customDataScopeEntity);
                log.info("请求自定义权限接口失败:{}", e.getMessage());
            }
        } else {
            dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, customDataScopeEntity);
        }


        log.info("baseEntity.getDataPermissions() = {}", baseEntity.getDataPermissions());
    }


    /**
     * 方法拼接sql
     * @param joinPoint
     * @param user
     * @param dataScope
     * @param switchUser
     */
    public void dataScopeFilterMontage(JoinPoint joinPoint, SysUser user,DataPermissions dataScope,LoginUser switchUser, CustomDataScopeEntity customDataScopeEntity) {
        /* 租户表的别名 */
        /* 租户字段名 默认tenant_id */
        String tenantColumn = dataScope.tenantColumn();
        /* 设备表的别名 */
        String layerColumn = dataScope.layerColumn();

        HttpServletRequest request = RequestUtils.getRequest();
        //当前请求路径
        String requestURI = request.getRequestURI();
        BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        /* 用户类型 0铁塔 2租户 */
        String userType = user.getUserType();
        SysDept dept = user.getDept();

        StringBuffer stringBuffer = new StringBuffer();

        //查询当前节点编码
        String platformCode = "";
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
        if(!ObjectUtils.isEmpty(config)) {
            platformCode = config.getConfigValue();
        }


        //租户用户数据权限过滤
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(userType)){
            String isTenantAdmin = user.getIsTenantAdmin();
            String tenantId = user.getTenantId();
            // 如果租户管理员,则不过滤租户部门权限
            if (StringUtils.isNotBlank(tenantColumn)) {
                stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                baseEntity.setDataPermissions(stringBuffer.toString());
            }
            if(StringUtils.isNotBlank(dataScope.industryCode())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), user.getIndustryCode()));
                baseEntity.setDataPermissions(stringBuffer.toString());
            }

            if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                baseEntity.setDataPermissions(stringBuffer.toString());
            }

            // 如果非租户管理员,则滤部门权限;;;如果是客户管理员并配置全部数据权限，则与租户管理员相同权限
            if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin) && !isMgr(user)) {
                Set<String> deptIdSet = getDeptListByUserDataScope(dataScope.dbType(), user);
                // 设备情况下直接 租户+设备权限过滤
                if (StringUtils.isNotBlank(layerColumn)) {
                    List<String> layerIds = customDataScopeEntity.getLayerIds();

                    stringBuffer.append(StringUtils.format(" and {} IN {} ", layerColumn, joinDeviceParameter(layerIds)));

                    baseEntity.setDataPermissions(stringBuffer.toString());
                }
            }
        } else {
            // 铁塔客户按管理区域过滤
            Boolean switchFlag = SecurityUtils.isSwitch(); //是否切换客户视角，后续完善后从redis取
            //客户视角切换：已切换-sql后面拼接租户ID,未切换-
            if(switchFlag){
                //已切换
                //获取当前租户Id
                String tenantId = switchUser.getUser().getTenantId();//从redis获取 判断时间是否过期
                String industryCode = switchUser.getUser().getIndustryCode();//从redis获取 判断时间是否过期
                if (!StringUtils.isEmpty(tenantId)) {
                    stringBuffer.append(StringUtils.format(" and {} = '{}' ",  tenantColumn, tenantId));
                }
                if(StringUtils.isNotBlank(industryCode)){
                    stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), industryCode));
                }
                if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                    stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                }
            }else{
                //根据当前用户组织id关联出订购信息，拼接租户Id
                String subDeptId = dept.getDeptId().substring(0,6);
                int currentDeptLevel = Integer.parseInt(dept.getDeptLevel());
                if (currentDeptLevel != 1) {
                    SysDept deptById = getDeptById(dataScope.dbType(), subDeptId);
                    deptById.setDeptLevelInt(currentDeptLevel);
                    if (currentDeptLevel >= 4) {
                        //兼容4级以下组织用户，理论上不应该有这种数据
                        deptById.setDeptId(dept.getDeptId());
                    }
                    List<SysDept> list = getDeptByCurrentDeptId(dataScope.dbType(),deptById);
                    //省分公司的话要加上省公司
                    if (currentDeptLevel == 2 && subDeptId.endsWith("001")) {
                        deptById.setDeptId(subDeptId.substring(0,5) + "0");
                        List<SysDept> listAdd = getDeptByCurrentDeptId(dataScope.dbType(),deptById);
                        list.addAll(listAdd);
                    }
                    List<String> tenantIds = getTenantByTowerDeptId(dataScope.dbType(),list);
                    if (StringUtils.isNotBlank(tenantColumn)) {
                        stringBuffer.append(StringUtils.format(" and {} in {} ", tenantColumn, joinDeviceParameter(tenantIds)));
                    }
                }
                baseEntity.setDataPermissions(stringBuffer.toString());
            }
            baseEntity.setDataPermissions(stringBuffer.toString());
        }
        log.info("baseEntity.getDataPermissions() = {}", baseEntity.getDataPermissions());
    }


    /**
     * 拼接设备编码
     * @param: deviceCodes
     * @return: java.lang.String
     * @author: zheng.yan
     * @date: 2022/3/3
     * @version:
     */
    private String joinDeviceParameter(List<String> deviceCodes) {
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


    /**
     * 获取该租户用户角色自定义数据权限
     * @author 李峥
     * @param role
     * @return
     */
    private List<String> getTenantCutomRoleDeptInParameter(SysUser user,String dbType) {
        //TODO 由于新增时不能及时刷新，暂去掉
//        String roleType = role.getRoleType();
//        Long roleId = role.getRoleId();
//        String key = CacheConstants.ROLE_SCOPE_TENANT_LIST_PREFIX + roleType + CommonConstants.COLON + roleId;
//        List<String> list = RedisUtil.getList(key, String.class, RedisDBType.PRIMARY);
//        if(CollUtil.isEmpty(list)){
//            list = selectTenantChildDept(dbType,user);
//            if(CollUtil.isNotEmpty(list)){
//                setCacheList(key,list);
//            }
//        }
        return selectCustomTanentDept(dbType,user);
    }

    /**
     * 获取该租户用户对应的部门权限
     * @author 李峥
     * @return
     */
    private List<String> getTenantChildDeptInParameter(SysUser user,String dbType) {
        //TODO 由于新增时不能及时刷新，暂去掉
//        List<String> childDeptIdList = SecurityUtils.getTenantChildDeptIdList();
//        String key = CacheConstants.DEPT_SCOPE_TENANT_LIST_PREFIX + user.getTenantId() + CommonConstants.COLON + user.getDeptId();
//        if (CollUtil.isEmpty(childDeptIdList)) {
//            childDeptIdList = selectTenantChildDept(dbType,user);
//            setCacheList(key,childDeptIdList);
//        }
        return selectTenantChildDept(dbType,user);
    }


    /**
     * 获取租户下所有组织
     * @param user
     * @param dbType
     * @return
     */
    private List<String> getTenantAllDeptInParameter(SysUser user,String dbType) {
        //TODO 由于新增时不能及时刷新，暂去掉
//        String key = CacheConstants.DEPT_SCOPE_TENANT_INDUSTRYCODE_LIST_PREFIX + user.getTenantId() + CommonConstants.COLON + user.getIndustryCode();
//        List<String> list = RedisUtil.getList(key, String.class, RedisDBType.PRIMARY);
//        if (CollUtil.isEmpty(list)) {
//            list = selectAllTanentDept(dbType,user);
//            setCacheList(key,list);
//        }
        return selectAllTanentDept(dbType,user);
    }


    public List<String> selectTenantChildDept(String dbType, SysUser user){
        if ("".equals(dbType)) {
            return authBaseMapper.selectTenantChildDept(user.getDeptId());
        }
        else
        {
            return dataTypeDeal.selectTenantChildDept(dbType,user);
        }
    }


    public List<String> selectAllTanentDept(String dbType, SysUser user){
        if ("".equals(dbType)) {
            return authBaseMapper.selectAllTanentDept(user);
        }
        else
        {
            return dataTypeDeal.selectAllTanentDept(dbType,user);
        }
    }


    public List<String> selectCustomTanentDept(String dbType, SysUser user){
        if ("".equals(dbType)) {
            return authBaseMapper.selectCustomTanentDept(user);
        }
        else
        {
            return dataTypeDeal.selectCustomTanentDept(dbType,user);
        }
    }

    public List<String> getOrgDevicesByType(String dbType, Set<String> deptIdStr,String layerType,String industryCode) {
        if ("".equals(dbType)) {
            return authBaseMapper.getOrgDevices(deptIdStr,layerType,industryCode);
        }
        else
        {
            return dataTypeDeal.getOrgDevicesByType(dbType, deptIdStr,layerType,industryCode);
        }
    }


    public List<String> getTenantByTowerDeptId(String dbType,List<SysDept> deptId){
        if ("".equals(dbType)) {
            return authBaseMapper.getTenantByTowerDeptId(deptId);
        }
        else
        {
            return dataTypeDeal.getTenantByTowerDeptId(dbType,deptId);
        }
    }

    public List<SysDept> getDeptByCurrentDeptId(String dbType,SysDept sysDept){
        if ("".equals(dbType)) {
            return authBaseMapper.getDeptByCurrentDeptId(sysDept);
        }
        else
        {
            return dataTypeDeal.getDeptByCurrentDeptId(dbType,sysDept);
        }
    }

    public SysDept getDeptById(String dbType, String deptId){
        if ("".equals(dbType)) {
            return authBaseMapper.getDeptById(deptId);
        }
        else
        {
            return dataTypeDeal.getDeptById(dbType,deptId);
        }
    }

    public List<String> getOrgLayersByType(String dbType, Set<String> deptIdStr,String industryCode) {
        if ("".equals(dbType)) {
            return authBaseMapper.getOrgLayers(deptIdStr,industryCode);
        }
        else
        {
            return dataTypeDeal.getOrgLayersByType(dbType, deptIdStr,industryCode);
        }
    }

}
