package com.chinatower.platform.aspect;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.deal.DataTypeDeal;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.entity.dataScope.CustomDataScopeEntity;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.ScreenDataConfigCountVO;
import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import com.chinatower.platform.feign.CustomDataFeignClient;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 数据过滤处理
 *
 * @autohr iwhalecloud
 */
@Aspect
@Component
@Order(1)
@Slf4j
@SuppressWarnings("all")
public class DataScopeAspect {
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

    private static final String TOWER_AREA_PRO_CACHE_KEYS = "tower_areas_pro_";
    private static final String TOWER_AREA_CITY_CACHE_KEYS = "tower_areas_city_";
    private static final String TOWER_AREA_CODE_CACHE_KEYS = "tower_areas_code_";

    private static final String CUSTOM_DATA_SCOPE = "custom_data_cope";


    private static final String DEPT_COLUMN = "deptColumn";
    private static final String TENANT_COLUMN = "tenantColumn";
    private static final String DEVICE_COLUMN = "deviceColumn";
    private static final String USER_COLUMN = "userColumn";

    private static final String INDUSTRY_CODE_COLUMN = "industryCodeColumn";

    private static final String ACCEPT_COLUMN = "acceptColumn";

    private static final String PROCESS_ID_COLUMN = "processIdColumn";

    private static final String ALARM_SOURCE_COLUMN = "alarmSourceColumn";
    private static final String ALARM_SOURCE_VAL_COLUMN = "alarmSourceValColumn";
    private static final String GRID_COLUMN = "gridColumn";
    private static final String GRID_KEEPER_COLUMN = "gridKeeperColumn";

    private static final String RESOURCE_COLUMN = "resourceColumn";

    private static final String TYPE_VALUE_COLUMN = "typeValueColumn";

    private static final String AREA_PRO_COLUMN = "areaPro";
    private static final String AREA_CITY_COLUMN = "areaCity";
    private static final String AREA_CODE_COLUMN = "areaCode";

    private static final String IN = " IN ";






    private static final String REGEX_IN = "\\s+IN\\s*\\(([^)]+?)\\)";

    private static final String REGEX_EQUIS = "\\s*=\\s*'([^']+)'";



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
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point,DataScope dataScope) {
        handleDataScope(point, dataScope);
    }

    /**
     * 执行数据权限条件拼装
     * @author 李峥
     * @param joinPoint
     * @param dataScope
     */
    protected void handleDataScope(final JoinPoint joinPoint,DataScope dataScope) {
        BaseDataScopeEntity entity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        entity.setDataScope("");

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
                // 如果客户开启了大屏配置功能，而且是大屏调用接口，则用大屏配置的数据过滤
                String industryCode = currentUser.getIndustryCode();
                String appVerCode = currentUser.getAppVerCode();
                String tenantId = currentUser.getTenantId();
                BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
                String screenId = baseEntity.getScreenId();
                String screenType = baseEntity.getScreenType();
                boolean flag = true;
                Long screenDataConfigId = 1L;
                if(!StringUtils.isEmpty(screenId) && !StringUtils.isEmpty(screenType)){
                    /* 用户类型 0铁塔 2租户 确认切换视角后用户类型会变成2 */
                    String userType = currentUser.getUserType();
                    log.info("userType={},tenantId={}, industryCode={},appVerCode={}, screenId={}, screenType={}", userType,tenantId,industryCode,appVerCode,screenId,screenType);
                    if(Constants.TWO.equals(userType)){
                        ScreenDataConfigCountVO screenDataConfig = selectScreenDataConfigCount(dataScope.dbType(), tenantId, industryCode, appVerCode, screenId, screenType);
                        if(!ObjectUtils.isEmpty(screenDataConfig)){
                            screenDataConfigId = screenDataConfig.getId();
                            flag = false;
                        }
                    }
                }
                if(flag){
                    dataScopeFilter(joinPoint, currentUser, dataScope,loginUser.getSwitchdLoginUser());
                }else{
                    dataScopeFilterScreen(joinPoint, currentUser, dataScope,loginUser.getSwitchdLoginUser(), screenDataConfigId);
                }
            }else{
                BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
                baseEntity.setDataScope("");
                baseEntity.setCriteriaList(new ArrayList<>());
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


    private Set<String> getAreaCode(List<OrganStreetVO> orgnStreetVO,Integer type){
        Set<String> codes = new HashSet<>();
        for(OrganStreetVO vo:orgnStreetVO){
            if (vo == null) {
                continue;
            }
            switch (type){
                case 1:
                    if(StringUtils.isNotBlank(vo.getProvinceCode())){
                        codes.add(vo.getProvinceCode());
                    }
                    break;
                case 2:
                    if(StringUtils.isNotBlank(vo.getCityCode())){
                        codes.add(vo.getCityCode());
                    }
                    break;
                case 3:
                    if(StringUtils.isNotBlank(vo.getCountyCode())){
                        codes.add(vo.getCountyCode());
                    }
                    break;
                default:
                    break;
            }
        }
        return codes;
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
     * 大屏数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param dataScope 切面注解
     */
    public void dataScopeFilterScreen(JoinPoint joinPoint, SysUser user,DataScope dataScope,LoginUser switchUser, Long screenDataConfigId) {
        log.info("进入大屏配置数据切面方法===");
        List<Criteria> criList = new ArrayList<>();
        /* 部门字段名 默认dept_id */
        String deptColumn = dataScope.deptColumn();
        /* 创建用户字段名 默认create_by */
        String userColumn = dataScope.userColumn();
        /* 租户表的别名 */
        /* 租户字段名 默认tenant_id */
        String tenantColumn = dataScope.tenantColumn();
        /* 设备表的别名 */
        String deviceColumn = dataScope.deviceColumn();
        /* 网格表的别名 */
        String gridColumn = dataScope.gridColumn();
        /* 网格员表的别名 */
        String gridKeeperColumn = dataScope.gridKeeperColumn();
        /* 资源表的别名 */
        String resourceColumn = dataScope.resourceColumn();
        /* 告警类型表的别名 */
        String typeValueColumn = dataScope.typeValueColumn();

        HttpServletRequest request = RequestUtils.getRequest();
        //当前请求路径
        String requestURI = request.getRequestURI();
        BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        /* 用户类型 0铁塔 2租户 */
        String userType = user.getUserType();
        /* 用户部门ID */
        String deptId = user.getDeptId();
        SysDept dept = user.getDept();
        /* 部门父节点 */
        String parentId = dept.getParentId();
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
            String industryCode = user.getIndustryCode();
            // 如果租户管理员,则不过滤租户部门权限
            Criteria tenantCriteria = new Criteria();
            Criteria industryCriteria = new Criteria();
            if (StringUtils.isNotBlank(tenantColumn)) {
                stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                baseEntity.setDataScope(stringBuffer.toString());
                tenantCriteria = new Criteria("tenant_id").is(tenantId);
            }
            if(StringUtils.isNotBlank(dataScope.industryCode())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), user.getIndustryCode()));
                baseEntity.setDataScope(stringBuffer.toString());
                industryCriteria = new Criteria("industry_code").is(user.getIndustryCode());
            }

            if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                baseEntity.setDataScope(stringBuffer.toString());
            }

            // 如果非租户管理员,则滤部门权限;;;如果是客户管理员并配置全部数据权限，则与租户管理员相同权限
//            if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin) && !isMgr(user)) {
                Set<String> deptIdSet = getDeptListByUserDataScope(dataScope.dbType(), user);

                supportAttrs.add(DEPT_COLUMN);
                customDataScopeEntity.setDeptIdSet(deptIdSet);

                if(StringUtils.isNotBlank(deptColumn)){
                    String deptIdStr = joinInParameter(deptIdSet);
                    stringBuffer.append(StringUtils.format(" and {} IN {} ",  deptColumn,deptIdStr));
                    baseEntity.setDataScope(stringBuffer.toString());
                }
            // 设备情况下直接 租户+设备权限过滤
                if (StringUtils.isNotBlank(deviceColumn)) {
                    List<String> deviceCodes = new ArrayList<>();
                    List<String> cameraCodes = selectScreenDataConfigCameraByConfigId(dataScope.dbType(), screenDataConfigId);
                    List<String> iotCodes = selectScreenDataConfigIotByConfigId(dataScope.dbType(), screenDataConfigId);
                    List<String> radarCodes = selectScreenDataConfigRadarByConfigId(dataScope.dbType(), screenDataConfigId);
                    List<String> speakerCodes = selectScreenDataConfigSpeakerByConfigId(dataScope.dbType(), screenDataConfigId);
                    List<String> uavCodes = selectScreenDataConfigUavByConfigId(dataScope.dbType(), screenDataConfigId);
                    List<String> recorderCodes = selectScreenDataConfigRecorderByConfigId(dataScope.dbType(), screenDataConfigId);

                    deviceCodes.addAll(cameraCodes);
                    deviceCodes.addAll(iotCodes);
                    deviceCodes.addAll(radarCodes);
                    deviceCodes.addAll(speakerCodes);
                    deviceCodes.addAll(uavCodes);
                    deviceCodes.addAll(recorderCodes);

                    //增加无人机机巢权限（临时方案）
//                    List<String> uavNesCodes = selectScreenDataConfigUavNestByConfigId(dataScope.dbType(), screenDataConfigId, user);
                    if(!StringUtils.isEmpty(dataScope.alarmSourceVal()) &&
                            dataScope.alarmSourceVal().contains("15")){
                        List<String> uavNesCodes = getOrgDevicesByType(dataScope.dbType(),  deptIdSet, dataScope.alarmSourceVal(), user.getIndustryCode());
                        deviceCodes.addAll(uavNesCodes);
                    }


                    supportAttrs.add(DEPT_COLUMN);
                    customDataScopeEntity.setDeviceCodes(deviceCodes);

                    if (StringUtils.isNotBlank(dataScope.alarmSourceColumn()) && StringUtils.isNotBlank(dataScope.typeValueColumn())) {
                        supportAttrs.add(TYPE_VALUE_COLUMN);
                        customDataScopeEntity.setTypeValue(dataScope.typeValueColumn());
                    }

                } else {
                    if(StringUtils.isNotBlank(dataScope.alarmSourceColumn()) && StringUtils.isNotBlank(dataScope.typeValueColumn())){
                        supportAttrs.add(TYPE_VALUE_COLUMN);
                        customDataScopeEntity.setTypeValue(dataScope.typeValueColumn());

                    }
                }
                if(StringUtils.isNotBlank(gridColumn)){
                    List<String> gridCodes = selectScreenDataConfigGridByConfigId(dataScope.dbType(), screenDataConfigId);
                    supportAttrs.add(GRID_COLUMN);
                    customDataScopeEntity.setGridCodes(gridCodes);
                }
                if(StringUtils.isNotBlank(gridKeeperColumn)){
                    List<String> gridOperatorCodes = selectScreenDataConfigGridOperatorByConfigId(dataScope.dbType(), screenDataConfigId);
                    supportAttrs.add(GRID_KEEPER_COLUMN);
                    customDataScopeEntity.setGridKeeperCodes(gridOperatorCodes);
                    Set<String> gridOperatorCodeList = new HashSet<>(gridOperatorCodes);
                }
                if(StringUtils.isNotBlank(resourceColumn)){
                    List<String> resourceCodes = selectScreenDataConfigResourceByConfigId(dataScope.dbType(), screenDataConfigId);

                    if(!CollectionUtils.isEmpty(resourceCodes)){
                        supportAttrs.add(RESOURCE_COLUMN);
                        customDataScopeEntity.setResourceCodes(resourceCodes);
                    }
                }
            }

//        baseEntity.setCriteriaList(criList);
//        log.info("baseEntity.getDataScope() = {}", baseEntity.getDataScope());


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
                    dataScopeFilterScreenMontage(joinPoint, user, dataScope, switchUser, screenDataConfigId, data);
                } else {
                    dataScopeFilterScreenMontage(joinPoint, user, dataScope, switchUser, screenDataConfigId, customDataScopeEntity);
                }
            } catch (Exception e) {
                log.info("请求自定义权限接口失败:{}", e.getMessage());
                dataScopeFilterScreenMontage(joinPoint, user, dataScope, switchUser, screenDataConfigId, customDataScopeEntity);
            }
        } else {
            dataScopeFilterScreenMontage(joinPoint, user, dataScope, switchUser, screenDataConfigId, customDataScopeEntity);
        }

        //重新整合拼接sql

        log.info("baseEntity.getCriList() = {}", baseEntity.getCriteriaList());
        log.info("baseEntity.getDataScope() = {}", baseEntity.getDataScope());

    }


    /**
     *
     * @param joinPoint
     * @param user
     * @param dataScope
     * @param switchUser
     * @param screenDataConfigId
     * @param customDataScopeEntity
     */
    public void dataScopeFilterScreenMontage(JoinPoint joinPoint, SysUser user,DataScope dataScope,LoginUser switchUser,
                                             Long screenDataConfigId, CustomDataScopeEntity customDataScopeEntity) {
        log.info("进入大屏配置数据切面方法===");
        List<Criteria> criList = new ArrayList<>();
        /* 部门字段名 默认dept_id */
        String deptColumn = dataScope.deptColumn();
        /* 创建用户字段名 默认create_by */
        String userColumn = dataScope.userColumn();
        /* 租户表的别名 */
        /* 租户字段名 默认tenant_id */
        String tenantColumn = dataScope.tenantColumn();
        /* 设备表的别名 */
        String deviceColumn = dataScope.deviceColumn();
        /* 网格表的别名 */
        String gridColumn = dataScope.gridColumn();
        /* 网格员表的别名 */
        String gridKeeperColumn = dataScope.gridKeeperColumn();
        /* 资源表的别名 */
        String resourceColumn = dataScope.resourceColumn();
        /* 告警类型表的别名 */
        String typeValueColumn = dataScope.typeValueColumn();

        HttpServletRequest request = RequestUtils.getRequest();
        //当前请求路径
        String requestURI = request.getRequestURI();
        BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        /* 用户类型 0铁塔 2租户 */
        String userType = user.getUserType();
        /* 用户部门ID */
        String deptId = user.getDeptId();
        SysDept dept = user.getDept();
        /* 部门父节点 */
        String parentId = dept.getParentId();
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
            String industryCode = user.getIndustryCode();
            // 如果租户管理员,则不过滤租户部门权限
            Criteria tenantCriteria = new Criteria();
            Criteria industryCriteria = new Criteria();
            if (StringUtils.isNotBlank(tenantColumn)) {
                stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                baseEntity.setDataScope(stringBuffer.toString());
                tenantCriteria = new Criteria("tenant_id").is(tenantId);
            }
            if(StringUtils.isNotBlank(dataScope.industryCode())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), user.getIndustryCode()));
                baseEntity.setDataScope(stringBuffer.toString());
                industryCriteria = new Criteria("industry_code").is(user.getIndustryCode());
            }

            if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                baseEntity.setDataScope(stringBuffer.toString());
            }

            // 如果非租户管理员,则滤部门权限;;;如果是客户管理员并配置全部数据权限，则与租户管理员相同权限
//            if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin) && !isMgr(user)) {
            Set<String> deptIdSet = customDataScopeEntity.getDeptIdSet();
            if(StringUtils.isNotBlank(deptColumn)){
                String deptIdStr = joinInParameter(deptIdSet);
                stringBuffer.append(StringUtils.format(" and {} IN {} ",  deptColumn,deptIdStr));
                baseEntity.setDataScope(stringBuffer.toString());
            }
            // 设备情况下直接 租户+设备权限过滤
            if (StringUtils.isNotBlank(deviceColumn)) {
                List<String> deviceCodes = customDataScopeEntity.getDeviceCodes();
                if (StringUtils.isNotBlank(dataScope.alarmSourceColumn()) && StringUtils.isNotBlank(dataScope.typeValueColumn())) {
                    String typeValue = customDataScopeEntity.getTypeValue();
                    stringBuffer = this.montageAlarmTypeQueryCriteria(dataScope, screenDataConfigId,
                            tenantId, industryCode, stringBuffer, deviceColumn, deviceCodes, user,
                            deptIdSet, tenantCriteria, industryCriteria, criList, requestURI, typeValue);
                } else if(StringUtils.isNotBlank(dataScope.alarmSourceColumn())){
                    this.montageOnlyAlarmSource(stringBuffer, deviceColumn, deviceCodes, dataScope,
                            tenantId, industryCode, user, deptIdSet, tenantCriteria,
                            industryCriteria, criList, screenDataConfigId);
                } else{
                    stringBuffer.append(StringUtils.format(" and {} IN {} ",  deviceColumn, joinDeviceParameter(deviceCodes)));
                }
                if(StringUtils.isNotBlank(tenantColumn)){
                    stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                }
                baseEntity.setDataScope(stringBuffer.toString());
            } else {
                if(StringUtils.isNotBlank(tenantColumn)){
                    //租户数据权限的过滤
                    String tenantStr = StringUtils.format(" and {} = '{}'",  tenantColumn, tenantId);
                    baseEntity.setDataScope(stringBuffer.append(tenantStr).toString());
                }
                if(StringUtils.isNotBlank(dataScope.alarmSourceColumn()) && StringUtils.isNotBlank(dataScope.typeValueColumn())){
                    String typeValue = customDataScopeEntity.getTypeValue();
                    stringBuffer = this.montageAlarmTypeQueryCriteria(dataScope, screenDataConfigId, tenantId, industryCode, stringBuffer, deviceColumn, null, user,  deptIdSet, tenantCriteria, industryCriteria, criList, requestURI, typeValue);
                    baseEntity.setDataScope(stringBuffer.toString());
                }else if(StringUtils.isNotBlank(dataScope.typeValueColumn())){
                    montageOnlyTypeValue(stringBuffer, dataScope, tenantId, industryCode, screenDataConfigId);
                    baseEntity.setDataScope(stringBuffer.toString());
                }
            }
            if(StringUtils.isNotBlank(gridColumn)){
                List<String> gridCodes = customDataScopeEntity.getGridCodes();
                if(!CollectionUtils.isEmpty(gridCodes)){
                    Set<String> gridCodeList = new HashSet<>(gridCodes);
                    String gridIdStr = joinInParameter(gridCodeList);
                    stringBuffer.append(StringUtils.format(" and {} IN {} ",  gridColumn,gridIdStr));
                    baseEntity.setDataScope(stringBuffer.toString());
                }else{
//                    stringBuffer.append(" and 1 = 2 ");
                    // 修改后：使用 IS NULL 条件
                    stringBuffer.append(StringUtils.format(" and {} IS NULL ", gridColumn));
                    baseEntity.setDataScope(stringBuffer.toString());
                }
            }
            if(StringUtils.isNotBlank(gridKeeperColumn)){
                List<String> gridOperatorCodes = customDataScopeEntity.getGridKeeperCodes();
                Set<String> gridOperatorCodeList = new HashSet<>(gridOperatorCodes);
                stringBuffer.append(StringUtils.format(" and ( {} )",  joinInGridKeeperParameter(gridKeeperColumn, gridOperatorCodeList)));
                baseEntity.setDataScope(stringBuffer.toString());
            }
            if(StringUtils.isNotBlank(resourceColumn)){
                List<String> resourceCodes = customDataScopeEntity.getResourceCodes();
                if(!CollectionUtils.isEmpty(resourceCodes)){
                    Set<String> resourceCodeList = new HashSet<>(resourceCodes);
                    String resourceIdStr = joinInParameter(resourceCodeList);
                    stringBuffer.append(StringUtils.format(" and {} IN {} ",  resourceColumn,resourceIdStr));
                    baseEntity.setDataScope(stringBuffer.toString());
                }else{
//                    stringBuffer.append(" and 1 = 2 ");
                    // 修改后：使用 IS NULL 条件
                    stringBuffer.append(StringUtils.format(" and {} IS NULL ", resourceColumn));
                    baseEntity.setDataScope(stringBuffer.toString());
                }
            }
        }
//        else{
//                Criteria criteria10 = new Criteria();
//                criteria10=criteria10.andOperator(tenantCriteria,industryCriteria);
//                criList.add(criteria10);
//            }
//        }

        baseEntity.setCriteriaList(criList);
        log.info("baseEntity.getDataScope() = {}", baseEntity.getDataScope());
    }




    private void montageOnlyTypeValue(StringBuffer stringBuffer, DataScope dataScope, String tenantId, String industryCode, Long screenDataConfigId){
        List<String> intersection = new ArrayList<>();
        List<ScreenDataConfigParticipationVO> alarmTypeTenantConfigList = selectScreenDataConfigAlarmTypeByConfigId(dataScope.dbType(), screenDataConfigId);
        if(!CollectionUtils.isEmpty(alarmTypeTenantConfigList)){
            List<ScreenDataConfigParticipationVO> alarmTypeRealList = findRealAlarmType(dataScope.dbType(), tenantId, industryCode);
            List<String> configAlarmTypeValueList = alarmTypeTenantConfigList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            List<String> realAlarmTypeValueList = alarmTypeRealList.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toList());
            intersection = configAlarmTypeValueList.stream()
                    .filter(realAlarmTypeValueList::contains)
                    .collect(Collectors.toList());

        }
        stringBuffer.append(StringUtils.format(" and {} IN {} ",dataScope.typeValueColumn(), joinDeviceParameter(intersection)));
    }

    private void montageOnlyAlarmSource(StringBuffer stringBuffer, String deviceColumn, List<String> deviceCodes, DataScope dataScope, String tenantId,
                                        String industryCode, SysUser user, Set<String> deptIdSet, Criteria tenantCriteria, Criteria industryCriteria, List<Criteria> criList, Long screenDataConfigId){
        List<ScreenDataConfigParticipationVO> alarmTypeTenantConfigList = selectScreenDataConfigAlarmTypeByConfigId(dataScope.dbType(), screenDataConfigId);
        // 要找到真实的告警来源
        String alarmSourceVal = "";
        if(!CollectionUtils.isEmpty(alarmTypeTenantConfigList)){
            List<ScreenDataConfigParticipationVO> alarmTypeRealList = findRealAlarmType(dataScope.dbType(), tenantId, industryCode);
            List<ScreenDataConfigParticipationVO> intersection = alarmTypeTenantConfigList.stream()
                    .filter(alarmTypeRealList::contains)
                    .collect(Collectors.toList());
//            Map<String, List<ScreenDataConfigParticipationVO>> alarmTypeConfigListMap = alarmTypeTenantConfigList.stream().collect(Collectors.groupingBy(ScreenDataConfigParticipationVO::getSource));
//            List<ScreenDataConfigParticipationVO> screenDataConfigParticipationVOListTwo = alarmTypeConfigListMap.get(Constants.TWO);
//            List<ScreenDataConfigParticipationVO> screenDataConfigParticipationVOListThree = alarmTypeConfigListMap.get(Constants.THREE);
//            // 如果勾选的告警类型有人工上报那就找到真实的告警来源
//            if(!CollectionUtils.isEmpty(screenDataConfigParticipationVOListTwo)){
//                for (ScreenDataConfigParticipationVO voTwo:screenDataConfigParticipationVOListTwo) {
//                    for (ScreenDataConfigParticipationVO realVO:alarmTypeRealList) {
//                        if(voTwo.getCode().equals(realVO.getCode()) && !voTwo.getSource().equals(realVO.getSource())
//                                && !realVO.getSource().equals(Constants.THREE) && !realVO.getSource().equals(Constants.EIGHT)
//                                && !realVO.getSource().equals(Constants.ELEVEN) && !realVO.getSource().equals(Constants.TWELVE)){
//                            intersection.add(realVO);
//                            break;
//                        }
//                    }
//                }
//            }
//            // 如果勾选的告警类型有一键告警那就找到真实的告警来源
//            if(!CollectionUtils.isEmpty(screenDataConfigParticipationVOListThree)){
//                for (ScreenDataConfigParticipationVO voThree:screenDataConfigParticipationVOListThree) {
//                    for (ScreenDataConfigParticipationVO realVO:alarmTypeRealList) {
//                        if(voThree.getCode().equals(realVO.getCode()) && !voThree.getSource().equals(realVO.getSource())
//                                && !realVO.getSource().equals(Constants.TWO) && !realVO.getSource().equals(Constants.EIGHT)
//                                && !realVO.getSource().equals(Constants.ELEVEN) && !realVO.getSource().equals(Constants.TWELVE)){
//                            intersection.add(realVO);
//                            break;
//                        }
//                    }
//                }
//            }
            Set<String> alarmTypeSourceListNew = intersection.stream().map(ScreenDataConfigParticipationVO::getSource).collect(Collectors.toSet());
            alarmSourceVal = alarmTypeSourceListNew.stream().collect(Collectors.joining(","));
        }

        boolean status = false;
        stringBuffer.append(StringUtils.format(" and (({} IN {} and {} IN {})", deviceColumn, joinDeviceParameter(deviceCodes), dataScope.alarmSourceColumn(), joinAlarmSourceParameter(alarmSourceVal)));
        Criteria deviceCodeCriteria = new Criteria("device_code").in(deviceCodes);
        List<String> alarmSourceList = new ArrayList<>();
        if(!StringUtils.isEmpty(alarmSourceVal)){
            alarmSourceList = Arrays.asList(alarmSourceVal.split(","));
        }
        Criteria criteria2 = new Criteria("warning_source").in(alarmSourceList);
        Criteria combinedCriteria = new Criteria();
        combinedCriteria.andOperator(deviceCodeCriteria, criteria2);
        /* 告警来源 */
        // 2022.05.16 查询告警列表，卫星告警目前无法绑定租户部门和设备的关系，添加告警来源用于区分权限
        List<String> types = Arrays.asList(alarmSourceVal.split(","));
        if(StringUtils.isNotBlank(dataScope.processIdColumn())){
            stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(),dataScope.processIdColumn()));
        }
        Criteria criteria3 = new Criteria();
        //人工上报和第三方告警忽略权限
        if(types.contains("2") || types.contains("11")){
            //查询组织下的用户
            stringBuffer.append(StringUtils.format(" OR ({} in ('2','9','11'))",dataScope.alarmSourceColumn()));
            List<String> alarmSource = new ArrayList<>();
            alarmSource.add("2");
            alarmSource.add("11");
            alarmSource.add("9");
            criteria3 = new Criteria("warning_source").in(alarmSource);
        }
        Criteria criteria4 = new Criteria();
        Criteria criteria5 = new Criteria();
        Criteria criteria6 = new Criteria();
        Criteria criteria7 = new Criteria();
        Criteria criteria10 = new Criteria();
        Criteria criteria11 = new Criteria();
        Criteria criteria12 = new Criteria();
        Criteria criteria13 = new Criteria();
        // 雷达告警查询组织设备关联表关联雷达表拿到雷达设备的所有省市区信息进行权限控制
        status = false;
        if(types.contains("10")){
            List<OrganStreetVO> orgnStreetVO = getStreetByRadarVO(dataScope.dbType(), user.getIndustryCode(), new ArrayList<>(deptIdSet));

            if(StringUtils.isNotBlank(dataScope.areaPro())){
                String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                Set<String> proCodes = getAreaCode(orgnStreetVO,1);
                stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaPro(),joinInParameter(proCodes)));
                status = true;
                criteria4 = new Criteria(mongoPro).in(proCodes);
            }
            if(StringUtils.isNotBlank(dataScope.areaCity())){
                String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                Set<String> cityCodes = getAreaCode(orgnStreetVO,2);
                if(status){
                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                    criteria5 = new Criteria(mongoCity).in(cityCodes);
                }else{
                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                }
                status = true;

            }
            if(StringUtils.isNotBlank(dataScope.areaCode())){
                String mongoCounty = dataScope.areaCode().substring(dataScope.areaCode().indexOf(".")+1);
                Set<String> countyCodes = getAreaCode(orgnStreetVO,3);
                if(status){
                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                    criteria6 = new Criteria(mongoCounty).in(countyCodes);
                }else{
                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                }
                status = true;

            }
        }
        if(status){
            stringBuffer.append(StringUtils.format(") and {} in ('10'))",dataScope.alarmSourceColumn()));
            criteria7 = new Criteria("warning_source").in(Arrays.asList("10"));
        }
        Criteria combinedCriteria4 = new Criteria();
        combinedCriteria4.andOperator(criteria4, criteria5,criteria6,criteria7);

        Criteria orCondition = new Criteria().orOperator(
                combinedCriteria,
                criteria3,
                combinedCriteria4
        );

        stringBuffer.append(")");
        // 查询当前组织配置的告警类型
        Criteria alarmTypeAnd = new Criteria();
        if(!StringUtils.isEmpty(dataScope.typeValueColumn()) && !CollectionUtils.isEmpty(deptIdSet)){
            List<ScreenDataConfigParticipationVO> alarmTypeConfigList = selectDeptAlarmTypeConfigList(dataScope.dbType(), tenantId, user.getIndustryCode(), deptIdSet);
            if(!CollectionUtils.isEmpty(alarmTypeConfigList)){
                stringBuffer.append(StringUtils.format(" and ( ( {}, {} ) IN ( {} ) ", dataScope.alarmSourceColumn(), dataScope.typeValueColumn(), joinAlarmTypeParameter(alarmTypeConfigList)));
                if(StringUtils.isNotBlank(dataScope.processIdColumn())){
                    stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(),dataScope.processIdColumn()));
                }
                stringBuffer.append(" )");
                Criteria[] criteriaArr = new Criteria[alarmTypeConfigList.size()];
                for(int i=0; i<alarmTypeConfigList.size(); i++){
                    Criteria alarmType = new Criteria("warning_source").is(alarmTypeConfigList.get(i).getSource()).and("warning_type_id").is(alarmTypeConfigList.get(i).getCode());
                    criteriaArr[i] = alarmType;
                }
                alarmTypeAnd.orOperator(criteriaArr);
            }else{
//                stringBuffer.append(" and 1 = 2 ");
                stringBuffer.append(StringUtils.format(" and {} IS NULL ", dataScope.typeValueColumn()));
                alarmTypeAnd.orOperator(new Criteria("warning_source").is("0"));
            }
        }

        Criteria combinedCriteria3 = new Criteria();
        combinedCriteria3.andOperator(tenantCriteria, industryCriteria, orCondition, alarmTypeAnd);
        criList.add(combinedCriteria3);
    }

    private StringBuffer montageAlarmTypeQueryCriteria(DataScope dataScope, Long screenDataConfigId, String tenantId, String industryCode, StringBuffer stringBuffer, String deviceColumn,
                                                       List<String> deviceCodes, SysUser user, Set<String> deptIdSet, Criteria tenantCriteria, Criteria industryCriteria, List<Criteria> criList, String requestURI, String typeValue){
        List<ScreenDataConfigParticipationVO> alarmTypeConfigList = selectScreenDataConfigAlarmTypeByConfigId(dataScope.dbType(), screenDataConfigId);
        Set<String> alarmTypeSourceListNew = new HashSet<>();
        Set<String> alarmTypeValueListNew = new HashSet<>();
        List<ScreenDataConfigParticipationVO> intersection = new ArrayList<>();
        String alarmSourceVal = "";
        String alarmTypeVal = "";
        if(!CollectionUtils.isEmpty(alarmTypeConfigList)){
            List<ScreenDataConfigParticipationVO> alarmTypeRealList = findRealAlarmType(dataScope.dbType(), tenantId, industryCode);
            intersection = alarmTypeConfigList.stream()
                    .filter(alarmTypeRealList::contains)
                    .collect(Collectors.toList());
//            Map<String, List<ScreenDataConfigParticipationVO>> alarmTypeConfigListMap = alarmTypeConfigList.stream().collect(Collectors.groupingBy(ScreenDataConfigParticipationVO::getSource));
//            List<ScreenDataConfigParticipationVO> screenDataConfigParticipationVOListTwo = alarmTypeConfigListMap.get(Constants.TWO);
//            List<ScreenDataConfigParticipationVO> screenDataConfigParticipationVOListThree = alarmTypeConfigListMap.get(Constants.THREE);
//            // 如果勾选的告警类型有人工上报那就找到真实的告警来源
//            if(!CollectionUtils.isEmpty(screenDataConfigParticipationVOListTwo)){
//                for (ScreenDataConfigParticipationVO voTwo:screenDataConfigParticipationVOListTwo) {
//                    for (ScreenDataConfigParticipationVO realVO:alarmTypeRealList) {
//                        if(voTwo.getCode().equals(realVO.getCode()) && !voTwo.getSource().equals(realVO.getSource())
//                                && !realVO.getSource().equals(Constants.THREE) && !realVO.getSource().equals(Constants.EIGHT)
//                                && !realVO.getSource().equals(Constants.ELEVEN) && !realVO.getSource().equals(Constants.TWELVE)){
//                            intersection.add(realVO);
//                            break;
//                        }
//                    }
//                }
//            }
//            // 如果勾选的告警类型有一键告警那就找到真实的告警来源
//            if(!CollectionUtils.isEmpty(screenDataConfigParticipationVOListThree)){
//                for (ScreenDataConfigParticipationVO voThree:screenDataConfigParticipationVOListThree) {
//                    for (ScreenDataConfigParticipationVO realVO:alarmTypeRealList) {
//                        if(voThree.getCode().equals(realVO.getCode()) && !voThree.getSource().equals(realVO.getSource())
//                                && !realVO.getSource().equals(Constants.TWO) && !realVO.getSource().equals(Constants.EIGHT)
//                                && !realVO.getSource().equals(Constants.ELEVEN) && !realVO.getSource().equals(Constants.TWELVE)){
//                            intersection.add(realVO);
//                            break;
//                        }
//                    }
//                }
//            }
            intersection = intersection.stream().distinct().collect(Collectors.toList());
            alarmTypeSourceListNew = intersection.stream().map(ScreenDataConfigParticipationVO::getSource).collect(Collectors.toSet());
            alarmSourceVal = alarmTypeSourceListNew.stream().collect(Collectors.joining(","));
            alarmTypeValueListNew = intersection.stream().map(ScreenDataConfigParticipationVO::getCode).collect(Collectors.toSet());
            alarmTypeVal = alarmTypeValueListNew.stream().collect(Collectors.joining(","));
        }

        boolean status = false;
        Criteria deviceCodeCriteria = new Criteria();
        if(StringUtils.isNotEmpty(deviceColumn)){
            if(!CollectionUtils.isEmpty(deviceCodes)){
                deviceCodeCriteria = new Criteria("device_code").in(deviceCodes);
            }else{
                deviceCodeCriteria = new Criteria("device_code").in(new ArrayList<>());
            }
            stringBuffer.append(StringUtils.format(" and (({} IN {} and {} IN {})", deviceColumn, joinDeviceParameter(deviceCodes), dataScope.alarmSourceColumn(), joinAlarmSourceParameter(alarmSourceVal)));
        }else{
            // 如果没有设备别名 就不用设备限制
            stringBuffer.append(" and ( 1 = 1 ");
        }

        List<String> alarmSourceList = new ArrayList<>();
        if(!StringUtils.isEmpty(alarmSourceVal)){
            alarmSourceList = Arrays.asList(alarmSourceVal.split(","));
        }
        Criteria criteria2 = new Criteria("warning_source").in(alarmSourceList);
        Criteria combinedCriteria = new Criteria();
        combinedCriteria.andOperator(deviceCodeCriteria, criteria2);
        /* 告警来源 */
        // 2022.05.16 查询告警列表，卫星告警目前无法绑定租户部门和设备的关系，添加告警来源用于区分权限
        List<String> types = Arrays.asList(alarmSourceVal.split(","));
//        if(StringUtils.isNotBlank(dataScope.processIdColumn())){
//            stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(),dataScope.processIdColumn()));
//        }
        Criteria criteria3 = new Criteria();
        //人工上报和第三方告警和卫星告警忽略权限
        if(types.contains("2") || types.contains("11") || types.contains("9")){
            //查询组织下的用户
            stringBuffer.append(StringUtils.format(" OR ({} in ('2','9','11'))",dataScope.alarmSourceColumn()));
            List<String> alarmSource = new ArrayList<>();
            alarmSource.add("2");
            alarmSource.add("11");
            alarmSource.add("9");
            criteria3 = new Criteria("warning_source").in(alarmSource);
        }
        Criteria criteria4 = new Criteria();
        Criteria criteria5 = new Criteria();
        Criteria criteria6 = new Criteria();
        Criteria criteria7 = new Criteria();

        Criteria criteria10 = new Criteria();
        Criteria criteria11 = new Criteria();
        Criteria criteria12 = new Criteria();
        Criteria criteria13 = new Criteria();
        // 雷达告警查询组织设备关联表关联雷达表拿到雷达设备的所有省市区信息进行权限控制
        status = false;
        if(types.contains("10")){
            List<OrganStreetVO> orgnStreetVO = getStreetByRadarVO(dataScope.dbType(), user.getIndustryCode(), new ArrayList<>(deptIdSet));

            if(StringUtils.isNotBlank(dataScope.areaPro())){
                String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                Set<String> proCodes = getAreaCode(orgnStreetVO,1);
                stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaPro(),joinInParameter(proCodes)));
                status = true;
                criteria4 = new Criteria(mongoPro).in(proCodes);
            }
            if(StringUtils.isNotBlank(dataScope.areaCity())){
                String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                Set<String> cityCodes = getAreaCode(orgnStreetVO,2);
                if(status){
                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                    criteria5 = new Criteria(mongoCity).in(cityCodes);
                }else{
                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                }
                status = true;
            }
            if(StringUtils.isNotBlank(dataScope.areaCode())){
                String mongoCounty = dataScope.areaCode().substring(dataScope.areaCode().indexOf(".")+1);
                Set<String> countyCodes = getAreaCode(orgnStreetVO,3);
                if(status){
                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                    criteria6 = new Criteria(mongoCounty).in(countyCodes);
                }else{
                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                }
                status = true;
            }
        }
        if(status){
            stringBuffer.append(StringUtils.format(") and {} in ('10'))",dataScope.alarmSourceColumn()));
            criteria7 = new Criteria("warning_source").in(Arrays.asList("10"));
        }
        Criteria combinedCriteria4 = new Criteria();
        combinedCriteria4.andOperator(criteria4, criteria5,criteria6,criteria7);

        Criteria orCondition = new Criteria().orOperator(
                combinedCriteria,
                criteria3,
                combinedCriteria4
        );

        stringBuffer.append(")");

        // 告警类型拼接sql
        Criteria alarmTypeAnd = new Criteria();
        if(!CollectionUtils.isEmpty(intersection)){
            stringBuffer.append(StringUtils.format(" and ( {}, {} ) IN ( {} ) ", dataScope.alarmSourceColumn(), typeValue, joinAlarmTypeParameter(intersection)));
            Criteria[] criteriaArr = new Criteria[intersection.size()];
            for(int i=0; i<intersection.size(); i++){
                Criteria alarmType = new Criteria("warning_source").is(intersection.get(i).getSource()).and("warning_type_id").is(intersection.get(i).getCode());
                criteriaArr[i] = alarmType;
            }
            alarmTypeAnd.orOperator(criteriaArr);
        }else{
//            stringBuffer.append(" and 1 = 2 ");
            // 修改后：使用 IS NULL 条件
            if(!StringUtils.isEmpty(dataScope.alarmSourceColumn())){
                stringBuffer.append(StringUtils.format(" and  {} IS NULL  ", dataScope.alarmSourceColumn()));
            }
            alarmTypeAnd.orOperator(new Criteria("warning_source").is("0"));
        }

        Criteria combinedCriteria3 = new Criteria();
        combinedCriteria3.andOperator(tenantCriteria, industryCriteria,orCondition, alarmTypeAnd);
        criList.add(combinedCriteria3);
        return stringBuffer;
    }

    /**
     * @Description: 查询全量的告警类型
     * @Author: wangyt
     * @Date: 2025/1/13
     * @param tenantId
     * @param industryCode
     * @return java.util.List<com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO>
     **/
    private List<ScreenDataConfigParticipationVO> findRealAlarmType(String dbType, String tenantId, String industryCode){
        // 取出真实的告警类型
        ScreenDataConfigCountVO body = new ScreenDataConfigCountVO();
        body.setTenantId(tenantId);
        body.setIndustryCode(industryCode);
        // 这里的告警来源是人工上报和一键告警的类型少 需要加上
        List<ScreenDataConfigParticipationVO> alarmTypeRealList = findAlarmSourceSubType(dbType, body);
        // 人工上报2
        List<ScreenDataConfigParticipationVO> manualReportingList = new ArrayList<>();
        alarmTypeRealList.forEach(e -> {
            ScreenDataConfigParticipationVO manualReporting = new ScreenDataConfigParticipationVO();
            manualReporting.setCode(e.getCode());
            manualReporting.setSource(Constants.TWO);
            manualReportingList.add(manualReporting);
        });

        // 一键告警3
        List<ScreenDataConfigParticipationVO> oneClickAlarmList = new ArrayList<>();
        manualReportingList.forEach(e -> {
            ScreenDataConfigParticipationVO manualReporting = new ScreenDataConfigParticipationVO();
            manualReporting.setCode(e.getCode());
            manualReporting.setSource(Constants.THREE);
            oneClickAlarmList.add(manualReporting);
        });

        alarmTypeRealList.addAll(manualReportingList);
        alarmTypeRealList.addAll(oneClickAlarmList);
        // 去重
        alarmTypeRealList = alarmTypeRealList.stream().distinct().collect(Collectors.toList());
        return alarmTypeRealList;
    }

    public List<ScreenDataConfigParticipationVO> findAlarmSourceSubType(String dbType, ScreenDataConfigCountVO body) {
        List<String> alarmSources = findAlarmSource(dbType, body);
        //4、算法告警及非算法告警
        body.setAlarmSources(alarmSources);
        List<ScreenDataConfigParticipationVO> alarmTypeVOList = findAlarmTypeByOrder(dbType, body);
        List<ScreenDataConfigParticipationVO> sourceAlarmTypeList = findAlarmTypeBySource(dbType, body);
        if (!CollectionUtils.isEmpty(sourceAlarmTypeList)) {
            // 卫星告警只查询【卫星火点】告警
            if (alarmSources.contains(WarningSourceEnum.SATELLITE_ALARM.getTypeValue())
                    && BooleanUtils.isTrue(body.getOnlySatelliteFirePointFlag())) {
                // 过滤所有非卫星告警来源 以及 卫星告警来源中卫星火点类型
                sourceAlarmTypeList = sourceAlarmTypeList.stream()
                        .filter(x -> !Objects.equals(x.getSource(), WarningSourceEnum.SATELLITE_ALARM.getTypeValue())
                                || Objects.equals(x.getCode(), Constants.SATELLITE_FIRE_POINT_TYPE_VALUE)).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(sourceAlarmTypeList)) {
                alarmTypeVOList.addAll(sourceAlarmTypeList);
            }
        }

        // 如果包含第三方告警类型，则查询第三方告警类型信息并加入返回结果
        List<ScreenDataConfigParticipationVO> thirdPartyAlarmTypeList = getThirdPartyAlarmTypes("platform", body);
        if (!CollectionUtils.isEmpty(thirdPartyAlarmTypeList)) {
            alarmTypeVOList.addAll(thirdPartyAlarmTypeList);
        }

        return alarmTypeVOList;
    }

    public List<String> findAlarmSource(String dbType, ScreenDataConfigCountVO body) {
        List<String> alarmSources = new ArrayList<>();
        //人工上报和一件告警为固定告警来源
        alarmSources.add(WarningSourceEnum.ARTIFICIAL.getTypeValue());
        alarmSources.add(WarningSourceEnum.UAV_ALARM.getTypeValue());
        //1、是否有摄像机告警来源
        if (devByTenantIdCount(dbType, body) > 0) {
            alarmSources.add(WarningSourceEnum.CAMERA_ALARM.getTypeValue());
        }
        // 是否有无人机告警来源
        if (uavByTenantIdCount(dbType, body) > 0) {
            alarmSources.add(WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue());
        }
        //2、是否有物联设备、雷达告警来源
        body.setCategoryCode(String.valueOf(OrderConstants.ORDER_SENSOR_CODE));
        if (iotDevByTenantIdCount(dbType, body) > 0) {
            alarmSources.add(WarningSourceEnum.IOT_ALARM.getTypeValue());
        }
        body.setCategoryCode(String.valueOf(OrderConstants.ORDER_RADAR_CODE));
        if (iotDevByTenantIdCount(dbType, body) > 0) {
            alarmSources.add(WarningSourceEnum.RADAR_ALARM.getTypeValue());
        }

        //3、是否有卫星告警来源 跟志伟和学斌总确认，卫星告警类型查询不卡订购 0516
        alarmSources.add(WarningSourceEnum.SATELLITE_ALARM.getTypeValue());
        return alarmSources;
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param dataScope 切面注解
     */
    public void dataScopeFilter(JoinPoint joinPoint, SysUser user,DataScope dataScope,LoginUser switchUser) {
        List<Criteria> criList = new ArrayList<>();
        /* 部门字段名 默认dept_id */
        String deptColumn = dataScope.deptColumn();
        /* 创建用户字段名 默认create_by */
        String userColumn = dataScope.userColumn();
        /* 租户表的别名 */
        /* 租户字段名 默认tenant_id */
        String tenantColumn = dataScope.tenantColumn();
        /* 设备表的别名 */
        String deviceColumn = dataScope.deviceColumn();

        HttpServletRequest request = RequestUtils.getRequest();
        //当前请求路径
        String requestURI = request.getRequestURI();
        BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        /* 用户类型 0铁塔 2租户 */
        String userType = user.getUserType();
        /* 用户部门ID */
        String deptId = user.getDeptId();
        SysDept dept = user.getDept();
        /* 部门父节点 */
        String parentId = dept.getParentId();
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
            Criteria tenantCriteria = new Criteria();
            Criteria industryCriteria = new Criteria();
            if (StringUtils.isNotBlank(tenantColumn)) {
                supportAttrs.add(TENANT_COLUMN);
                customDataScopeEntity.setTenantId(tenantId);
            }
            if(StringUtils.isNotBlank(dataScope.industryCode())){
                supportAttrs.add(INDUSTRY_CODE_COLUMN);
                customDataScopeEntity.setIndustryCode(user.getIndustryCode());
            }

            if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                supportAttrs.add(ACCEPT_COLUMN);
                customDataScopeEntity.setAccept(platformCode);
            }

            // 如果非租户管理员,则滤部门权限;;;如果是客户管理员并配置全部数据权限，则与租户管理员相同权限
            if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin) && !isMgr(user)) {
                Set<String> deptIdSet = getDeptListByUserDataScope(dataScope.dbType(), user);
                supportAttrs.add(DEPT_COLUMN);
                customDataScopeEntity.setDeptIdSet(deptIdSet);

                if(StringUtils.isNotBlank(deptColumn)){
                    supportAttrs.add(DEPT_COLUMN);
                }
                // 设备情况下直接 租户+设备权限过滤
                if (StringUtils.isNotBlank(deviceColumn)) {
                    List<String> deviceCodes = getOrgDevicesByType(dataScope.dbType(),  deptIdSet, dataScope.alarmSourceVal(), user.getIndustryCode());
                    supportAttrs.add(DEVICE_COLUMN);
                    customDataScopeEntity.setDeviceCodes(deviceCodes);
                    if (StringUtils.isNotBlank(dataScope.alarmSourceColumn())) {
                        boolean status = false;

                        List<String> alarmSourceList = new ArrayList<>();
                        if(!StringUtils.isEmpty(dataScope.alarmSourceVal())){
                            alarmSourceList = Arrays.asList(dataScope.alarmSourceVal().split(","));
                            supportAttrs.add(ALARM_SOURCE_COLUMN);
                            customDataScopeEntity.setAlarmSourceList(alarmSourceList);
                        }
                        /* 告警来源 */
                        // 2022.05.16 查询告警列表，卫星告警目前无法绑定租户部门和设备的关系，添加告警来源用于区分权限
                        List<String> types = Arrays.asList(dataScope.alarmSourceVal().split(","));
                        supportAttrs.add(ALARM_SOURCE_VAL_COLUMN);
                        customDataScopeEntity.setAlarmSourceList(types);

                        if(StringUtils.isNotBlank(dataScope.processIdColumn())){
                            supportAttrs.add(PROCESS_ID_COLUMN);
                            customDataScopeEntity.setProcessId(user.getUserId());
                            //stringBuffer.append(StringUtils.format(" OR ({} like '%{}%')",dataScope.processIdColumn(),user.getUserId()));
//                            stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(),dataScope.processIdColumn()));

                        }
                        Criteria criteria3 = new Criteria();
                        //人工上报和第三方告警忽略权限
                        if(types.contains("2") || types.contains("11")){
                            //查询组织下的用户
//                            List<String> userIds = getUserIdsByDeptId(dataScope.dbType(),deptId);
//                            Set<String> userSets = new HashSet<>(userIds);
//                            stringBuffer.append(StringUtils.format(" OR ( {} IN {} and {} = 2)",userColumn,joinInParameter(userSets),dataScope.alarmSourceColumn()));
                            stringBuffer.append(StringUtils.format(" OR ({} in ('2','9','11'))",dataScope.alarmSourceColumn()));
                            List<String> alarmSource = new ArrayList<>();
                            alarmSource.add("2");
                            alarmSource.add("11");
                            alarmSource.add("9");
                            criteria3 = new Criteria("warning_source").in(alarmSource);
                        }
                        Criteria criteria4 = new Criteria();
                        Criteria criteria5 = new Criteria();
                        Criteria criteria6 = new Criteria();
                        Criteria criteria7 = new Criteria();
                        // 卫星告警查询组织区域关联表种的省市区信息进行权限控制
                        /*if(types.contains("9")){
                            List<OrganStreetVO> orgnStreetVO = getOrgnStreetVO(dataScope.dbType(), user.getIndustryCode(), new ArrayList<>(deptIdSet));

                            if(StringUtils.isNotBlank(dataScope.areaPro())){
                                String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                                Set<String> proCodes = getAreaCode(orgnStreetVO,1);
                                stringBuffer.append(StringUtils.format(" OR ((( {} IN {}",dataScope.areaPro(),joinInParameter(proCodes)));
                                status = true;
                                criteria4 = new Criteria(mongoPro).in(proCodes);
                            }
                            if(StringUtils.isNotBlank(dataScope.areaCity())){
                                String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                                Set<String> cityCodes = getAreaCode(orgnStreetVO,2);
                                if(status){
                                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                                    criteria5 = new Criteria(mongoCity).in(cityCodes);
                                }else{
                                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                                }
                                status = true;

                            }
                            if(StringUtils.isNotBlank(dataScope.areaCode())){
                                String mongoCounty = dataScope.areaCode().substring(dataScope.areaCode().indexOf(".")+1);
                                Set<String> countyCodes = getAreaCode(orgnStreetVO,3);
                                if(status){
                                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                                    criteria6 = new Criteria(mongoCounty).in(countyCodes);
                                }else{
                                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                                }
                                status = true;

                            }
                        }*/
                       /* if(status){
                            stringBuffer.append(StringUtils.format("  or dataTable.warning_type_id in ('4110101','4110102','4110103','4110104')"));
                            stringBuffer.append(StringUtils.format(")) and {} in ('9'))",dataScope.alarmSourceColumn()));
                            criteria7 = new Criteria("warning_source").in(Arrays.asList("9"));
                        }
                        Criteria combinedCriteria2 = new Criteria();
                        combinedCriteria2.andOperator(criteria4, criteria5,criteria6,criteria7);*/


                        Criteria criteria10 = new Criteria();
                        Criteria criteria11 = new Criteria();
                        Criteria criteria12 = new Criteria();
                        Criteria criteria13 = new Criteria();
                        // 雷达告警查询组织设备关联表关联雷达表拿到雷达设备的所有省市区信息进行权限控制
                        status = false;
                        if(types.contains("10")){
                            List<OrganStreetVO> orgnStreetVO = getStreetByRadarVO(dataScope.dbType(), user.getIndustryCode(), new ArrayList<>(deptIdSet));

                            if(StringUtils.isNotBlank(dataScope.areaPro())){
                                String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                                Set<String> proCodes = getAreaCode(orgnStreetVO,1);

                                supportAttrs.add(AREA_PRO_COLUMN);
                                customDataScopeEntity.setAreaPro(proCodes);

                            }
                            if(StringUtils.isNotBlank(dataScope.areaCity())){
                                String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                                Set<String> cityCodes = getAreaCode(orgnStreetVO,2);

                                supportAttrs.add(AREA_CITY_COLUMN);
                                customDataScopeEntity.setAreaCity(cityCodes);


                            }
                            if(StringUtils.isNotBlank(dataScope.areaCode())){
                                String mongoCounty = dataScope.areaCode().substring(dataScope.areaCode().indexOf(".")+1);
                                Set<String> countyCodes = getAreaCode(orgnStreetVO,3);

                                supportAttrs.add(AREA_CODE_COLUMN);
                                customDataScopeEntity.setAreaCode(countyCodes);


                            }
                        }

                        // 查询当前组织配置的告警类型
                        Criteria alarmTypeAnd = new Criteria();
                        if(!StringUtils.isEmpty(dataScope.typeValueColumn()) && !CollectionUtils.isEmpty(deptIdSet)){
                            List<ScreenDataConfigParticipationVO> alarmTypeConfigList = selectDeptAlarmTypeConfigList(dataScope.dbType(), tenantId, user.getIndustryCode(), deptIdSet);


                            supportAttrs.add(TYPE_VALUE_COLUMN);
                            customDataScopeEntity.setAlarmTypeConfigList(alarmTypeConfigList);

                        }

                    }
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
        log.info("获取自定义权限开关:{}", value);

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
                    dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, data, Constants.ONE);
                } else {
                    dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, customDataScopeEntity, Constants.TWO);
                }
            } catch (Exception e) {
                dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, customDataScopeEntity, Constants.TWO);
                log.info("请求自定义权限接口失败:{}", e.getMessage());
            }
        } else {
            dataScopeFilterMontage(joinPoint, user, dataScope, switchUser, customDataScopeEntity, Constants.TWO);
        }

        log.info("baseEntity.getDataScope() = {}", baseEntity.getDataScope());
    }


    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user      用户
     * @param dataScope 切面注解
     * @param localizationFlag 1：属地二开修改切面数据场景  2：通用场景
     */
    public void dataScopeFilterMontage(JoinPoint joinPoint, SysUser user,DataScope dataScope,LoginUser switchUser,
                                       CustomDataScopeEntity customDataScopeEntity, String localizationFlag) {
        List<Criteria> criList = new ArrayList<>();
        /* 部门字段名 默认dept_id */
        String deptColumn = dataScope.deptColumn();
        /* 创建用户字段名 默认create_by */
        String userColumn = dataScope.userColumn();
        /* 租户表的别名 */
        /* 租户字段名 默认tenant_id */
        String tenantColumn = dataScope.tenantColumn();
        /* 设备表的别名 */
        String deviceColumn = dataScope.deviceColumn();

        HttpServletRequest request = RequestUtils.getRequest();
        //当前请求路径
        String requestURI = request.getRequestURI();
        BaseDataScopeEntity baseEntity = (BaseDataScopeEntity) joinPoint.getArgs()[0];
        /* 用户类型 0铁塔 2租户 */
        String userType = user.getUserType();
        /* 用户部门ID */
        String deptId = user.getDeptId();
        SysDept dept = user.getDept();
        /* 部门父节点 */
        String parentId = dept.getParentId();
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
            Criteria tenantCriteria = new Criteria();
            Criteria industryCriteria = new Criteria();
            if (StringUtils.isNotBlank(tenantColumn)) {
                stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                baseEntity.setDataScope(stringBuffer.toString());
                tenantCriteria = new Criteria("tenant_id").is(tenantId);
            }
            if(StringUtils.isNotBlank(dataScope.industryCode())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), user.getIndustryCode()));
                baseEntity.setDataScope(stringBuffer.toString());
                industryCriteria = new Criteria("industry_code").is(user.getIndustryCode());
            }

            if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                baseEntity.setDataScope(stringBuffer.toString());
            }

            // 如果非租户管理员,则滤部门权限;;;如果是客户管理员并配置全部数据权限，则与租户管理员相同权限
            if (VideoSystemEnums.IsOrNotType.NOT.getIndex().equals(isTenantAdmin) && !isMgr(user)) {
                Set<String> deptIdSet = customDataScopeEntity.getDeptIdSet();
                if(StringUtils.isNotBlank(deptColumn)){
                    String deptIdStr = joinInParameter(deptIdSet);
                    stringBuffer.append(StringUtils.format(" and {} IN {} ",  deptColumn,deptIdStr));
                    baseEntity.setDataScope(stringBuffer.toString());
                }
                // 设备情况下直接 租户+设备权限过滤
                if (StringUtils.isNotBlank(deviceColumn)) {
                    List<String> deviceCodes = customDataScopeEntity.getDeviceCodes();
                    //拼接查询组织下的摄像机子查询sql
                    String deviceCodeSql = this.getDeviceCodeSql(deptIdSet, dataScope, user);
                    if (StringUtils.isNotBlank(dataScope.alarmSourceColumn())) {
                        boolean status = false;

                        // 通用场景，拼接sql语句，走子查询方式
                        if(Constants.TWO.equals(localizationFlag)){
                            stringBuffer.append(StringUtils.format(" and (({} IN {} and {} IN {})",deviceColumn,deviceCodeSql.toString(),dataScope.alarmSourceColumn(),joinAlarmSourceParameter(dataScope.alarmSourceVal())));
                        }else{
                            stringBuffer.append(StringUtils.format(" and (({} IN {} and {} IN {})",deviceColumn,joinDeviceParameter(deviceCodes),dataScope.alarmSourceColumn(), joinAlarmSourceParameter(dataScope.alarmSourceVal())));
                        }

                        Criteria deviceCodeCriteria = new Criteria("device_code").in(deviceCodes);
                        List<String> alarmSourceList = new ArrayList<>();
                        if(!StringUtils.isEmpty(dataScope.alarmSourceVal())){
                            alarmSourceList = customDataScopeEntity.getAlarmSourceList();
                        }
                        Criteria criteria2 = new Criteria("warning_source").in(alarmSourceList);
                        Criteria combinedCriteria = new Criteria();
                        combinedCriteria.andOperator(deviceCodeCriteria, criteria2);
                        /* 告警来源 */
                        // 2022.05.16 查询告警列表，卫星告警目前无法绑定租户部门和设备的关系，添加告警来源用于区分权限
                        List<String> types = customDataScopeEntity.getAlarmSourceList();
                        if(StringUtils.isNotBlank(dataScope.processIdColumn())){
                            //stringBuffer.append(StringUtils.format(" OR ({} like '%{}%')",dataScope.processIdColumn(),user.getUserId()));
                            stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(), dataScope.processIdColumn()));
                        }
                        Criteria criteria3 = new Criteria();
                        //人工上报和第三方告警忽略权限
                        if(types.contains("2") || types.contains("11")){
                            //查询组织下的用户
//                            List<String> userIds = getUserIdsByDeptId(dataScope.dbType(),deptId);
//                            Set<String> userSets = new HashSet<>(userIds);
//                            stringBuffer.append(StringUtils.format(" OR ( {} IN {} and {} = 2)",userColumn,joinInParameter(userSets),dataScope.alarmSourceColumn()));
                            stringBuffer.append(StringUtils.format(" OR ({} in ('2','9','11'))",dataScope.alarmSourceColumn()));
                            List<String> alarmSource = new ArrayList<>();
                            alarmSource.add("2");
                            alarmSource.add("11");
                            alarmSource.add("9");
                            criteria3 = new Criteria("warning_source").in(alarmSource);
                        }
                        Criteria criteria4 = new Criteria();
                        Criteria criteria5 = new Criteria();
                        Criteria criteria6 = new Criteria();
                        Criteria criteria7 = new Criteria();
                        // 卫星告警查询组织区域关联表种的省市区信息进行权限控制
                        /*if(types.contains("9")){
                            List<OrganStreetVO> orgnStreetVO = getOrgnStreetVO(dataScope.dbType(), user.getIndustryCode(), new ArrayList<>(deptIdSet));

                            if(StringUtils.isNotBlank(dataScope.areaPro())){
                                String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                                Set<String> proCodes = getAreaCode(orgnStreetVO,1);
                                stringBuffer.append(StringUtils.format(" OR ((( {} IN {}",dataScope.areaPro(),joinInParameter(proCodes)));
                                status = true;
                                criteria4 = new Criteria(mongoPro).in(proCodes);
                            }
                            if(StringUtils.isNotBlank(dataScope.areaCity())){
                                String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                                Set<String> cityCodes = getAreaCode(orgnStreetVO,2);
                                if(status){
                                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                                    criteria5 = new Criteria(mongoCity).in(cityCodes);
                                }else{
                                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                                }
                                status = true;

                            }
                            if(StringUtils.isNotBlank(dataScope.areaCode())){
                                String mongoCounty = dataScope.areaCode().substring(dataScope.areaCode().indexOf(".")+1);
                                Set<String> countyCodes = getAreaCode(orgnStreetVO,3);
                                if(status){
                                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                                    criteria6 = new Criteria(mongoCounty).in(countyCodes);
                                }else{
                                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                                }
                                status = true;

                            }
                        }*/
                       /* if(status){
                            stringBuffer.append(StringUtils.format("  or dataTable.warning_type_id in ('4110101','4110102','4110103','4110104')"));
                            stringBuffer.append(StringUtils.format(")) and {} in ('9'))",dataScope.alarmSourceColumn()));
                            criteria7 = new Criteria("warning_source").in(Arrays.asList("9"));
                        }
                        Criteria combinedCriteria2 = new Criteria();
                        combinedCriteria2.andOperator(criteria4, criteria5,criteria6,criteria7);*/


                        Criteria criteria10 = new Criteria();
                        Criteria criteria11 = new Criteria();
                        Criteria criteria12 = new Criteria();
                        Criteria criteria13 = new Criteria();
                        // 雷达告警查询组织设备关联表关联雷达表拿到雷达设备的所有省市区信息进行权限控制
                        status = false;
                        if(types.contains("10")){
                            List<OrganStreetVO> orgnStreetVO = getStreetByRadarVO(dataScope.dbType(), user.getIndustryCode(), new ArrayList<>(deptIdSet));

                            if(StringUtils.isNotBlank(dataScope.areaPro())){
                                String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);

                                Set<String> proCodes = customDataScopeEntity.getAreaPro();

                                stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaPro(),joinInParameter(proCodes)));
                                status = true;
                                criteria4 = new Criteria(mongoPro).in(proCodes);
                            }
                            if(StringUtils.isNotBlank(dataScope.areaCity())){
                                String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);

                                Set<String> cityCodes = customDataScopeEntity.getAreaCity();

                                if(status){
                                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                                    criteria5 = new Criteria(mongoCity).in(cityCodes);
                                }else{
                                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCity(),joinInParameter(cityCodes)));
                                }
                                status = true;

                            }
                            if(StringUtils.isNotBlank(dataScope.areaCode())){
                                String mongoCounty = dataScope.areaCode().substring(dataScope.areaCode().indexOf(".")+1);

                                Set<String> countyCodes = customDataScopeEntity.getAreaCode();

                                if(status){
                                    stringBuffer.append(StringUtils.format(" and {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                                    criteria6 = new Criteria(mongoCounty).in(countyCodes);
                                }else{
                                    stringBuffer.append(StringUtils.format(" OR (( {} IN {}",dataScope.areaCode(),joinInParameter(countyCodes)));
                                }
                                status = true;

                            }
                        }
                        if(status){
                            stringBuffer.append(StringUtils.format(") and {} in ('10'))",dataScope.alarmSourceColumn()));
                            criteria7 = new Criteria("warning_source").in(Arrays.asList("10"));
                        }
                        Criteria combinedCriteria4 = new Criteria();
                        combinedCriteria4.andOperator(criteria4, criteria5,criteria6,criteria7);

                        Criteria orCondition = new Criteria().orOperator(
                                combinedCriteria,
                                criteria3,
                                combinedCriteria4
                        );

                        stringBuffer.append(")");
                        // 查询当前组织配置的告警类型
                        Criteria alarmTypeAnd = new Criteria();
                        if(!StringUtils.isEmpty(dataScope.typeValueColumn()) && !CollectionUtils.isEmpty(deptIdSet)){
                            List<ScreenDataConfigParticipationVO> alarmTypeConfigList = customDataScopeEntity.getAlarmTypeConfigList();
                            if(!CollectionUtils.isEmpty(alarmTypeConfigList)){
                                stringBuffer.append(StringUtils.format(" and ( ( {}, {} ) IN ( {} ) ", dataScope.alarmSourceColumn(), dataScope.typeValueColumn(), joinAlarmTypeParameter(alarmTypeConfigList)));
                                if(StringUtils.isNotBlank(dataScope.processIdColumn())){
                                    stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(),dataScope.processIdColumn()));
                                }
                                stringBuffer.append(" )");
                                Criteria[] criteriaArr = new Criteria[alarmTypeConfigList.size()];
                                for(int i=0; i<alarmTypeConfigList.size(); i++){
                                    Criteria alarmType = new Criteria("warning_source").is(alarmTypeConfigList.get(i).getSource()).and("warning_type_id").is(alarmTypeConfigList.get(i).getCode());
                                    criteriaArr[i] = alarmType;
                                }
                                alarmTypeAnd.orOperator(criteriaArr);
                            }else{
                                if(StringUtils.isNotBlank(dataScope.processIdColumn())){
                                    stringBuffer.append(StringUtils.format(" and ( ( {}, {} ) IN ( {} ) ", dataScope.alarmSourceColumn(), dataScope.typeValueColumn(), joinAlarmTypeParameter(alarmTypeConfigList)));
                                    stringBuffer.append(StringUtils.format(" OR (FIND_IN_SET('{}',{}))",user.getUserId(),dataScope.processIdColumn()));
                                    stringBuffer.append(" )");
                                    alarmTypeAnd.orOperator(new Criteria("warning_source").is("0"));
                                }else{
//                                    stringBuffer.append(" and 1 = 2 ");
                                    // 修改后：使用 IS NULL 条件
                                    if(!StringUtils.isEmpty(dataScope.alarmSourceColumn())){
                                        stringBuffer.append(StringUtils.format(" and {} IS NULL ", dataScope.alarmSourceColumn()));
                                    }
                                    alarmTypeAnd.orOperator(new Criteria("warning_source").is("0"));
                                }
                            }
                        }

                        Criteria combinedCriteria3 = new Criteria();
                        combinedCriteria3.andOperator(tenantCriteria, industryCriteria, orCondition, alarmTypeAnd);
                        criList.add(combinedCriteria3);
                    }else{
                        // 通用场景，拼接sql语句，走子查询方式
                        if(Constants.TWO.equals(localizationFlag)){
                            stringBuffer.append(StringUtils.format(" and {} IN {} ",  deviceColumn, deviceCodeSql.toString()));
                        }else{
                            stringBuffer.append(StringUtils.format(" and {} IN {} ",  deviceColumn, joinDeviceParameter(deviceCodes)));
                        }
                    }
                    if(StringUtils.isNotBlank(tenantColumn)){
                        stringBuffer.append(StringUtils.format(" and {} = '{}'", tenantColumn, tenantId));
                    }
                    baseEntity.setDataScope(stringBuffer.toString());
                }
                else {
                    if(StringUtils.isNotBlank(tenantColumn)){
                        //租户数据权限的过滤
                        String tenantStr = StringUtils.format(" and {} = '{}'",  tenantColumn, tenantId);
                        baseEntity.setDataScope(stringBuffer.append(tenantStr).toString());
                    }
                }

            }else{
                Criteria criteria10 = new Criteria();
                criteria10=criteria10.andOperator(tenantCriteria,industryCriteria);
                criList.add(criteria10);
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
                    criList.add(Criteria.where("tenant_id").is(tenantId));
                }
                if(StringUtils.isNotBlank(industryCode)){
                    stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.industryCode(), industryCode));
                    criList.add(Criteria.where("industry_code").is(industryCode));
                }
                if(StringUtils.isNotBlank(dataScope.acceptColumn())){
                    stringBuffer.append(StringUtils.format(" and {} = '{}' ",  dataScope.acceptColumn(), platformCode));
                }
            }else{
                //未切换：判断是否有行政区域，有走行政区域逻辑，没有走租户逻辑
                if (StringUtils.isNotBlank(dataScope.areaPro()) || StringUtils.isNotBlank(dataScope.areaCity())) {
                    //返回当前铁塔用户对应的省市编码集合,铁塔管理区域基本不会变更，缓存处理增加过期失效
                    List<String> areaPro = RedisUtil.getList(TOWER_AREA_PRO_CACHE_KEYS + user.getUserId(), String.class, RedisDBType.PRIMARY);
                    List<String> areaCity = RedisUtil.getList(TOWER_AREA_CITY_CACHE_KEYS + user.getUserId(), String.class, RedisDBType.PRIMARY);
                    if (CollectionUtils.isEmpty(areaPro)) {
                        areaPro = getTowerByAreaLevel(dataScope.dbType(),user.getUserId(),2);
                        if (!CollectionUtils.isEmpty(areaPro)) {
                            this.setCacheList(TOWER_AREA_PRO_CACHE_KEYS + user.getUserId(), areaPro);
                        }
                    }

                    if (CollectionUtils.isEmpty(areaCity)) {
                        areaCity = getTowerByAreaLevel(dataScope.dbType(),user.getUserId(),3);
                        if (!CollectionUtils.isEmpty(areaCity)) {
                            this.setCacheList(TOWER_AREA_CITY_CACHE_KEYS + user.getUserId(), areaCity);
                        }
                    }

                    //如果两个同时存在，则合并拼接OR查询
                    Criteria criteria1 = new Criteria();
                    if(StringUtils.isNotBlank(dataScope.areaPro()) && !CollectionUtils.isEmpty(areaPro) && StringUtils.isNotBlank(dataScope.areaCity()) && !CollectionUtils.isEmpty(areaCity)){
                        stringBuffer.append( StringUtils.format(" and ( {} IN {} and {} IN {} ) ", dataScope.areaPro(), joinDeviceParameter(areaPro), dataScope.areaCity(), joinDeviceParameter(areaCity)));
                        String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                        String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                        Criteria criteria4 = new Criteria(mongoPro).in(areaPro);
                        Criteria criteria5 = new Criteria(mongoCity).in(areaCity);
                        criteria1=criteria1.andOperator(criteria4,criteria5);
                    }else if(StringUtils.isNotBlank(dataScope.areaPro()) && !CollectionUtils.isEmpty(areaPro)){
                        String mongoPro = dataScope.areaPro().substring(dataScope.areaPro().indexOf(".")+1);
                        stringBuffer.append( StringUtils.format(" and {} IN {} ", dataScope.areaPro(), joinDeviceParameter(areaPro)));
                        criteria1 = new Criteria(mongoPro).in(areaPro);
                    }else if(StringUtils.isNotBlank(dataScope.areaCity()) && !CollectionUtils.isEmpty(areaCity)){
                        String mongoCity = dataScope.areaCity().substring(dataScope.areaCity().indexOf(".")+1);
                        stringBuffer.append( StringUtils.format(" and {} IN {} ", dataScope.areaCity(), joinDeviceParameter(areaCity)));
                        criteria1 = new Criteria(mongoCity).in(areaCity);
                    }
                    criList.add(criteria1);
                    baseEntity.setDataScope(stringBuffer.toString());
                }
                else {
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
                            criList.add(new Criteria("tenant_id").in(tenantIds));
                        }
                        //根据组织过滤
                        if(StringUtils.isNotBlank(deptColumn)){
                            stringBuffer.append(StringUtils.format(" and {} in {} ",  deptColumn, joinDeptInParameter(list)));
                        }
                    }
                    baseEntity.setDataScope(stringBuffer.toString());
                }
            }
            baseEntity.setDataScope(stringBuffer.toString());

        }
        baseEntity.setCriteriaList(criList);
        log.info("baseEntity.criteriaList = {}", JSON.toJSONString(baseEntity.getCriteriaList()));
        log.info("baseEntity.getDataScope() = {}", baseEntity.getDataScope());
    }


    private static String joinInGridKeeperParameter(String gridKeeperColumn, Set<String> list){
        if (list == null || list.isEmpty()) {
            return " FIND_IN_SET ( '' , " + gridKeeperColumn + " ) ";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String GridKeeperId:list) {
            stringBuffer.append(" FIND_IN_SET ( ").append(GridKeeperId).append(" , ").append(gridKeeperColumn).append(" ) or ");
        }
        return stringBuffer.toString().substring(0, stringBuffer.length() - 3);
    }

    private static String joinInParameter(Set<String> list){
        if (list == null || list.isEmpty()) {
            return "('')";
        }
        StringBuffer deviceStrBuf = new StringBuffer();
        deviceStrBuf.append("(");
        for (String deptId : list) {
            deviceStrBuf.append("'" + deptId + "',");
        }
        deviceStrBuf.deleteCharAt(deviceStrBuf.length() - 1);
        deviceStrBuf.append(")");
        return deviceStrBuf.toString();
    }

    private static String joinDeptInParameter(List<SysDept> list){
        if (list == null || list.isEmpty()) {
            return "('')";
        }
        StringBuffer deviceStrBuf = new StringBuffer();
        deviceStrBuf.append("(");
        for (SysDept deviceCode : list) {
            deviceStrBuf.append("'" + deviceCode.getDeptId() + "',");
        }
        deviceStrBuf.deleteCharAt(deviceStrBuf.length() - 1);
        deviceStrBuf.append(")");
        return deviceStrBuf.toString();
    }

    private String joinAlarmTypeParameter(List<ScreenDataConfigParticipationVO> intersection){
        if (CollectionUtils.isEmpty(intersection)) {
            return "('','')";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (ScreenDataConfigParticipationVO vo:intersection) {
            stringBuffer.append("(").append("'").append(vo.getSource()).append("','").append(vo.getCode()).append("'),");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    /**
     * 获取该铁塔用户对应的部门权限
     * @authot 李峥
     * @return String
     */
    private static String getDeptInParameter() {
        List<String> childDeptIdList = SecurityUtils.getChildDeptIdList();
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

    private void setCacheList(String cacheKey, List<String> contentList) {
        RedisUtil.set(cacheKey, contentList, RedisDBType.PRIMARY);
        RedisUtil.expire(cacheKey, 60 * 5, TimeUnit.SECONDS, RedisDBType.PRIMARY);
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

    /**
     * 匹配url路径
     * @param urls
     * @param requestPath
     * @return
     */
    public static boolean matchUrl(Set<String> urls, String requestPath) {
        if (null == urls || urls.isEmpty()){
            return false;
        }
        if (urls.contains("/**")) {
            return true;
        }
        String urlStr = urls.toString();
        int count = urlStr.indexOf(requestPath);
        if (count > 0){
            return true;
        }
        return false;
    }

    /**
     * 获取铁塔人员对应的租户ids,铁塔人员可查看对应的租户信息
     * @param deptId
     * @return
     */
    private static String getTowerDeptTenantInParameter(String deptId) {
        List<String> list = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + deptId, String.class, RedisDBType.PRIMARY);
        StringBuffer stringBuffer = new StringBuffer();
        if (CollUtil.isNotEmpty(list)) {
            List<List<String>> partition = ListUtils.partition(list, 900);
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

    private ScreenDataConfigCountVO selectScreenDataConfigCount(String dbType, String tenantId, String industryCode, String appVerCode, String screenId, String screenType) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigCount(tenantId, industryCode, appVerCode, screenId, screenType);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigCount(dbType, tenantId, industryCode, appVerCode, screenId, screenType);
        }
    }

    private List<String> selectScreenDataConfigCameraByConfigId(String dbType, Long id) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigCameraByConfigId(id);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigCameraByConfigId(dbType, id);
        }
    }

    private List<String> selectScreenDataConfigIotByConfigId(String dbType, Long id) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigIotByConfigId(id);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigIotByConfigId(dbType, id);
        }
    }

    private List<String> selectScreenDataConfigRadarByConfigId(String dbType, Long id) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigRadarByConfigId(id);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigRadarByConfigId(dbType, id);
        }
    }

    private List<String> selectScreenDataConfigSpeakerByConfigId(String dbType, Long id) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigSpeakerByConfigId(id);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigSpeakerByConfigId(dbType, id);
        }
    }

    private List<String> selectScreenDataConfigUavByConfigId(String dbType, Long id) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigUavByConfigId(id);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigUavByConfigId(dbType, id);
        }
    }

    private List<String> selectScreenDataConfigUavNestByConfigId(String dbType, Long id, SysUser user) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigUavNestByConfigId(user);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigUavNestByConfigId(dbType, user);
        }
    }

    private List<String> selectScreenDataConfigRecorderByConfigId(String dbType, Long id) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigRecorderByConfigId(id);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigRecorderByConfigId(dbType, id);
        }
    }

    private List<ScreenDataConfigParticipationVO> selectScreenDataConfigAlarmTypeByConfigId(String dbType, Long screenId) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigAlarmTypeByConfigId(screenId);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigAlarmTypeByConfigId(dbType, screenId);
        }
    }

    private List<ScreenDataConfigParticipationVO> findAlarmTypeByOrder(String dbType, ScreenDataConfigCountVO body) {
        if ("".equals(dbType)) {
            return authBaseMapper.findAlarmTypeByOrder(body);
        }
        else
        {
            return dataTypeDeal.findAlarmTypeByOrder(dbType, body);
        }
    }

    private List<ScreenDataConfigParticipationVO> findAlarmTypeBySource(String dbType, ScreenDataConfigCountVO body) {
        if ("".equals(dbType)) {
            return authBaseMapper.findAlarmTypeBySource(body);
        }
        else
        {
            return dataTypeDeal.findAlarmTypeBySource(dbType, body);
        }
    }

    private List<ScreenDataConfigParticipationVO> getThirdPartyAlarmTypes(String dbType, ScreenDataConfigCountVO body) {
        if ("".equals(dbType)) {
            return authBaseMapper.getThirdPartyAlarmTypes(body);
        }
        else
        {
            return dataTypeDeal.getThirdPartyAlarmTypes(dbType, body);
        }
    }

    private List<String> selectScreenDataConfigGridByConfigId(String dbType, Long screenDataConfigId) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigGridByConfigId(screenDataConfigId);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigGridByConfigId(dbType, screenDataConfigId);
        }
    }

    private List<String> selectScreenDataConfigGridOperatorByConfigId(String dbType, Long screenDataConfigId) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigGridOperatorByConfigId(screenDataConfigId);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigGridOperatorByConfigId(dbType, screenDataConfigId);
        }
    }

    private List<String> selectScreenDataConfigResourceByConfigId(String dbType, Long screenDataConfigId) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectScreenDataConfigResourceByConfigId(screenDataConfigId);
        }
        else
        {
            return dataTypeDeal.selectScreenDataConfigResourceByConfigId(dbType, screenDataConfigId);
        }
    }

    private Integer devByTenantIdCount(String dbType, ScreenDataConfigCountVO body) {
        if ("".equals(dbType)) {
            return authBaseMapper.devByTenantIdCount(body);
        }
        else
        {
            return dataTypeDeal.devByTenantIdCount(dbType, body);
        }
    }

    private Integer uavByTenantIdCount(String dbType, ScreenDataConfigCountVO body) {
        if ("".equals(dbType)) {
            return authBaseMapper.uavByTenantIdCount(body);
        }
        else
        {
            return dataTypeDeal.uavByTenantIdCount(dbType, body);
        }
    }

    private Integer iotDevByTenantIdCount(String dbType, ScreenDataConfigCountVO body) {
        if ("".equals(dbType)) {
            return authBaseMapper.iotDevByTenantIdCount(body);
        }
        else
        {
            return dataTypeDeal.iotDevByTenantIdCount(dbType, body);
        }
    }

    private List<ScreenDataConfigParticipationVO> selectDeptAlarmTypeConfigList(String dbType, String tenantId, String industryCode, Set<String> deptIdSet) {
        if ("".equals(dbType)) {
            return authBaseMapper.selectDeptAlarmTypeConfigList(tenantId, industryCode, deptIdSet);
        }
        else
        {
            return dataTypeDeal.selectDeptAlarmTypeConfigList(dbType, tenantId, industryCode, deptIdSet);
        }
    }

    private List<String> getUserIdsByDeptId(String deptId,String dbType){
        if ("".equals(dbType)) {
            return authBaseMapper.getUserIdsByDeptId(deptId);
        }
        else
        {
            return dataTypeDeal.getUserIdsByDeptId(dbType,deptId);
        }
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

    public List<OrganStreetVO> getOrgnStreetVO(String dbType,String industryCode,List<String> deptIds){
        if ("".equals(dbType)) {
            return authBaseMapper.getOrgnStreetVO(industryCode,deptIds);
        }
        else
        {
            return dataTypeDeal.getOrgnStreetVO(dbType,industryCode,deptIds);
        }
    }


    public List<OrganStreetVO> getStreetByRadarVO(String dbType,String industryCode,List<String> deptIds){
        if ("".equals(dbType)) {
            return authBaseMapper.getStreetByRadarVO(industryCode,deptIds);
        }
        else
        {
            return dataTypeDeal.getStreetByRadarVO(dbType,industryCode,deptIds);
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

    public List<String> getOrgDevicesByType(String dbType, Set<String> deptIdStr,String alarmSource,String industryCode) {
        if ("".equals(dbType)) {
            return authBaseMapper.getOrgDevices(deptIdStr,alarmSource,industryCode);
        }
        else
        {
            return dataTypeDeal.getOrgDevicesByType(dbType, deptIdStr,alarmSource,industryCode);
        }
    }

    public List<String> getTowerUserAreasByType(String dbType, String userId) {
        if ("".equals(dbType)) {
            return authBaseMapper.getTowerUserAreas(userId);
        }
        else
        {
            return dataTypeDeal.getTowerUserAreasByType(dbType, userId);
        }
    }

    public List<String> getTowerByAreaLevel(String dbType, String userId,Integer areaLevel) {
        if ("".equals(dbType)) {
            return authBaseMapper.getTowerByAreaLevel(userId,areaLevel);
        }
        else
        {
            return dataTypeDeal.getTowerByAreaLevel(dbType, userId,areaLevel);
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

    public List<String> getTowerUserAreaPathByType(String dbType, List<String> areaProCity) {
        if ("".equals(dbType)) {
            return authBaseMapper.getTowerUserAreaPath(areaProCity);
        }
        else
        {
            return dataTypeDeal.getTowerUserAreaPathByType(dbType, areaProCity);
        }
    }

    public List<String> getTowerUserMgmtAreasByType(String dbType, List<String> areaProCity) {
        if ("".equals(dbType)) {
            return authBaseMapper.getTowerUserMgmtAreas(areaProCity);
        }
        else
        {
            return dataTypeDeal.getTowerUserMgmtAreasByType(dbType, areaProCity);
        }
    }

    /**
     * 告警来源处理
     * @param alarmSource
     * @return
     */
    private String joinAlarmSourceParameter(String alarmSource) {
        if (StringUtils.isEmpty(alarmSource)) {
            return "('')";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(");
        if (alarmSource.contains(",")) {
            String[] sources = alarmSource.split(",");
            for (String source : sources) {
                // 雷达告警设备在此处无需查到，走雷达设备对应省市区行政区域权限
                if(!source.equals(Constants.TEN_NUM_STRING)){
                    stringBuffer.append("'" + source + "',");
                }
            }
        }
        else {
            stringBuffer.append("'" + alarmSource + "',");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    /**
     * 拼接查询组织下的摄像机子查询sql
     * @param deptIdSet
     * @param dataScope
     * @param user
     * @return
     */
    private String getDeviceCodeSql(Set<String> deptIdSet,DataScope dataScope,SysUser user){
        StringBuilder sbSql = new StringBuilder();
        sbSql.append("(");
        sbSql.append("select distinct device_code from vm_organ_device_rel_t" +
                " WHERE delete_flag = '0' and industry_code = '"+user.getIndustryCode()+"'");
        if (deptIdSet != null && !deptIdSet.isEmpty()) {
            sbSql.append(" and dept_id in (")
                    .append(deptIdSet.stream()
                            .map(id -> "'" + id + "'")
                            .collect(Collectors.joining(",")))
                    .append(") ");
        } else {
            sbSql.append(" and dept_id in ('') ");
        }

        if (dataScope.alarmSourceVal() != null && !dataScope.alarmSourceVal().isEmpty()) {
            sbSql.append(" and FIND_IN_SET(device_type, '").append(dataScope.alarmSourceVal()).append("') ");
        }
        sbSql.append(")");
        log.info("sql语句："+sbSql.toString());
        return sbSql.toString();
    }

    /**
     * 拼接查询大屏数据权限下的摄像机子查询sql
     * @param deptIdSet
     * @param dataScope
     * @param user
     * @return
     */
    private String getScreenDeviceCodeSql(DataScope dataScope, Long screenDataConfigId){
        String union = " UNION ALL ";

        String device = "SELECT DISTINCT sdc.data_code FROM vm_screen_data_config sdc left join vm_order_devices od on sdc.data_code = od.device_code WHERE sdc.delete_flag = '0' AND sdc.data_type = '0' AND od.delete_flag = '0' AND od.ord_status in (0,1,2) AND sdc.screen_data_config_id = " + screenDataConfigId;

        String iot = "SELECT DISTINCT sdc.data_code FROM vm_screen_data_config sdc left join vm_order_sensor_devices od on sdc.data_code = od.device_code WHERE sdc.delete_flag = '0' AND sdc.data_type =  '1' AND od.delete_flag = '0' AND od.ord_status in (0,1,2) AND od.category_code = '2' AND sdc.screen_data_config_id = " + screenDataConfigId;

        String sensor = "SELECT DISTINCT sdc.data_code FROM vm_screen_data_config sdc left join vm_order_sensor_devices od on sdc.data_code = od.device_code WHERE sdc.delete_flag = '0' AND sdc.data_type = '2' AND od.delete_flag = '0' AND od.ord_status in (0,1,2) AND od.category_code = '1'  AND sdc.screen_data_config_id = " + screenDataConfigId;

        String horn = "SELECT DISTINCT sdc.data_code FROM vm_screen_data_config sdc left join vm_order_horn_devices od on sdc.data_code = od.device_code WHERE sdc.delete_flag = '0' AND sdc.data_type = '3' AND od.delete_flag = '0' AND od.ord_status in (0,1,2) AND sdc.screen_data_config_id = " + screenDataConfigId;

        String uav = "SELECT DISTINCT sdc.data_code FROM vm_screen_data_config sdc left join vm_order_uav_devices od on sdc.data_code = od.device_code WHERE sdc.delete_flag = '0' AND sdc.data_type = '4' AND od.delete_flag = '0' AND od.ord_status in (0,1,2) AND sdc.screen_data_config_id = " + screenDataConfigId;

        String recorder = "SELECT DISTINCT sdc.data_code FROM vm_screen_data_config sdc left join vm_order_recorder_devices od on sdc.data_code = od.device_code WHERE sdc.delete_flag = '0' AND sdc.data_type = '5' AND od.delete_flag = '0' AND od.ord_status in (0,1,2) AND sdc.screen_data_config_id = " + screenDataConfigId;

        StringBuilder sbSql = new StringBuilder();
        sbSql.append("(");

        if (dataScope.alarmSourceVal() != null && !dataScope.alarmSourceVal().isEmpty()) {
            String alarmSourceVal = dataScope.alarmSourceVal();
            String[] split = alarmSourceVal.split(",");
            // 转换为Set（会自动去重）
            Set<String> set = new HashSet<>(Arrays.asList(split));
            for (String s : set) {
                if("4".equals(s)){
                    sbSql.append(device);
                }
                if ("8".equals(s)) {
                    sbSql.append(iot);
                }
                if ("10".equals(s)) {
                    sbSql.append(sensor);
                }
                if ("12".equals(s)) {
                    sbSql.append(horn);
                }
                if ("11".equals(s)) {
                    sbSql.append(uav);
                }
                if ("13".equals(s)) {
                    sbSql.append(recorder);
                }
                sbSql.append(union);
            }
            sbSql = new StringBuilder(sbSql.substring(0, sbSql.length() - 11));
        }else {
            sbSql.append(device).append(union);
            sbSql.append(iot).append(union);
            sbSql.append(sensor).append(union);
            sbSql.append(horn).append(union);
            sbSql.append(uav).append(union);
            sbSql.append(recorder);
        }

        sbSql.append(")");
        log.info("screenSql语句："+sbSql.toString());
        return sbSql.toString();
    }
}
