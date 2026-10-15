package com.chinatower.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GridVO;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.order.dto.SysAreaDeptManage;
import com.chinatower.order.dto.SysUserRole;
import com.chinatower.order.dto.UserRoleDTO;
import com.chinatower.order.mapper.*;
import com.chinatower.order.service.IAsyncOrderDealService;
import com.chinatower.order.service.IOrderSystemService;
import com.chinatower.order.util.NonceUtil;
import com.chinatower.order.util.OrgUtil;
import com.chinatower.order.util.PasswordUtil;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.tenant.AddTenantInfoRequest;
import com.chinatower.system.api.request.tenant.CreateTenantRootDeptRequest;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.resp.SysAreaDeptManageResp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * @ClassName ISystemService
 * @Deacription 公共组件服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
@Slf4j
@Service
public class OrderSystemService implements IOrderSystemService {

    private static final String BEIJING_PREFIX = "110";
    private static final String TIANJIN_PREFIX = "120";
    private static final String SHANGHAI_PREFIX = "310";
    private static final String CHONGQING_PREFIX = "500";

    @Autowired
    private SysTenantInfoMapper sysTenantInfoMapper;

    @Autowired
    private SysAreaDeptManageMapper sysAreaDeptManageMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private OrderCustomerMapper orderCustomerMapper;

    @Autowired
    private IAsyncOrderDealService asyncOrderDealService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private PasswordUtil passwordUtil;

    private static final String DEPT_TREE_ROOT_ID = "000000";

    public static final String DEPENDENCE_MANAGE = "dependence_manage";

    private static final String SYS_USER_INITPASSWORD= "sys.user.initPassword";

    @Autowired
    private SysTenantDeptMapper sysTenantDeptMapper;

    @Autowired
    private GridAPI gridAPI;

    @Autowired
    private MsgFeignClient msgFeignClient;

    @Value("${sms.loginUrl}")
    private String url;

    @Value("${sms.centerUrl:https://sl.chinatowercom.cn}")
    private String centerUrl;

    @Value("${sms.template.addUser}")
    private String templateCode;


    /**
     * 新增租户管理
     *
     * @return void
     * @param: addTenantInfoRequest
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void addTenantInfo(AddTenantInfoRequest addTenantInfoRequest, String userName, Boolean handleOrderUserInfoOnly) {
        SysTenantInfo sysTenantInfo = new ObjectMapper().convertValue(addTenantInfoRequest, new TypeReference<SysTenantInfo>() {
        });
        this.insertSysTenantInfo(sysTenantInfo, userName, handleOrderUserInfoOnly);
    }


    /**
     * 新增用户信息
     * * @param: addUserRequest
     *
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public String addUser(AddUserRequest addUserRequest, boolean isAcceptNode) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(addUserRequest, user);

        if (UserConstants.NOT_UNIQUE.equals(this.checkTenantAdminUnique(user.getTenantId()))) {
            log.info("该租户{}已存在租户管理员账号", user.getTenantId());
            throw new CustomException("该租户" + user.getTenantId() + "已存在租户管理员账号");
        }
        if (UserConstants.NOT_UNIQUE.equals(this.checkUserNameUnique(user.getUserName()))) {
            log.info("新增用户{}失败，登录账号已存在", user.getUserName());
            throw new CustomException("新增用户" + user.getUserName() + "失败，登录账号已存在");
        }
        return this.insertUser(user, isAcceptNode);
    }

    /**
     * 查询userId
     *
     * @author liu.chao
     * @date 2022/8/10
     */
    @Override
    public String findUserIdByUserName(String userName) {
        return userMapper.findUserIdByUserName(userName);
    }

    @Override
    public int updateDeptByUserId(String userId, String deptId, boolean isAcceptNode, String currentAcceptNode) {
        int result;
        // 如果当前节点是受理节点，查询订购的用户是否是第一次下订单，如果是第一次下订单，则更改密码发送密码短信
        if (isAcceptNode) {
            SysUser user = userMapper.selectByUserId(userId);
            int orderCount = orderCustomerMapper.countByUserName(user.getUserName(), currentAcceptNode);
            // 之前没有订单则更改密码并发送密码短信
            if (orderCount == 0) {
                String password = passwordUtil.generatePassword();
                String userPwd = SecurityUtils.encryptPassword(password);
                result = userMapper.updateDeptByUserId(userId, deptId, userPwd);
                sendMessage4User(password, user.getUserName(), user.getPhonenumber());
            } else {
                result = userMapper.updateDeptByUserId(userId, deptId, null);
            }
        } else {
            result = userMapper.updateDeptByUserId(userId, deptId, null);
        }
        return result;
    }

    /**
     * 根据租户id查询用户表中是否已存在该租户管理员账号
     *
     * @param tenantId
     * @return
     */
    private String checkTenantAdminUnique(String tenantId) {
        int count = userMapper.checkTenantAdminUnique(tenantId);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    private String checkUserNameUnique(String userName) {
        int count = userMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    private String insertUser(SysUser user, boolean isAcceptNode) {
        String password = passwordUtil.generatePassword();
        user.setPassword(SecurityUtils.encryptPassword(password));
        if (StrUtil.isBlank(user.getCreateBy())) {
            user.setCreateBy(user.getUserName());
        }
        user.setCreateTime(new Date());
        // 生成userId
        user.setUserId(IdUtils.fastSimpleUUID());

        // 新增用户信息
        userMapper.insertUser(user);
        // 只处理订单和客户信息的情况下，不发送短信通知
        if (isAcceptNode) {
            sendMessage4User(password, user.getUserName(), user.getPhonenumber());
        }
        return user.getUserId();
    }

    private void sendMessage4User(String password, String userName, String phoneNumber) {
        try{
            Map<String,Object> map = new HashMap<>();
            map.put("password",password);
            map.put("url", url);
            // 如果当前节点是下沉节点，则发送短信替换为中心的登录地址
            AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if (sysConfig.getConfigValue().equals(com.chinatower.platform.constants.Constants.TWO)){
                    map.put("url", centerUrl);
                }
            }
            String nodeCode = getPlatformNode();
            // 通过节点编码获取节点别名
            String nodeAlias = userMapper.queryNodeAlias(nodeCode);
            if(StringUtils.isEmpty(nodeAlias)){
                //节点别名为空的话，按照原有逻辑发送节点编号
                map.put("nodeCode",nodeCode);
            }else{
                map.put("nodeCode",nodeAlias);
            }
            map.put("userName", userName);
            //requestVO.setParaMap(JSON.toJSONString(map));
            // 内网推送
            CommonSmsRequest commonSmsRequest = new CommonSmsRequest();
            commonSmsRequest.setMsgType(Constants.MSG_TYPE_SMS);
            //commonSmsRequest.setPhone(sysUser.getPhonenumber());
            commonSmsRequest.setPhone(phoneNumber);
            commonSmsRequest.setRequestMap(JSON.toJSONString(map));
            commonSmsRequest.setTemplateCode(templateCode);
            //msgFeignClient.innovationSendMessage(requestVO);
            Result result = msgFeignClient.sendMessage(commonSmsRequest);
            log.info("OrderSystemService sendMessage4User param:{}, result:{}",
                    JSON.toJSONString(commonSmsRequest), JSON.toJSONString(result));
        } catch (Exception e){
            log.info("新增用户发送通知短信异常:"+e);
        }
    }

    /**
     * 获取参数配置
     * @param configKey
     * @return
     */
    private SysConfig selectConfigByKey(String configKey) {
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
        if(ObjectUtil.isNull(config)){
            config = new SysConfig();
            config.setConfigKey(configKey);
            config = userMapper.selectConfig(config);
            if(ObjectUtil.isNotNull(config)){
                RedisUtil.set(SYS_CONFIG_KEY+configKey,config, RedisDBType.PRIMARY);
            }
        }
        return config;
    }

    private String selectConfigValueByKey(String configKey) {
        SysConfig config = selectConfigByKey(configKey);
        return ObjectUtil.isNull(config) ? "" : config.getConfigValue();
    }

    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    /**
     * 新增用户角色信息
     */
    @Override
    public void insertUserRole(String userId, String industryCode, String appVerCode) {
        List<Long> roleIds = userMapper.getRoleByIndustryCode(industryCode, appVerCode);
        if (!CollectionUtils.isEmpty(roleIds)) {
            //查询已有角色
            log.info("查询已有角色{}", JSON.toJSONString(roleIds));
            List<Long> havRoleIds = userMapper.getRoleByUserId(userId);
            if (!CollectionUtils.isEmpty(havRoleIds)) {
                roleIds = roleIds.stream().filter(roleId -> !havRoleIds.contains(roleId)).collect(Collectors.toList());
            }
            // 新增用户与角色管理
            log.info("新增用户与角色管理{}", JSON.toJSONString(roleIds));
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (!list.isEmpty()) {
                userMapper.batchUserRole(list);
            }
        }
    }

    @Override
    public void insertUserRoleBatch(String userId, Map<String,String> industryMap) {
        if (industryMap == null || industryMap.size() == 0) {
            return;
        }
        List<String> industryCodeList = industryMap.keySet().stream().distinct().collect(Collectors.toList());
        List<String> appVerCodeList = industryMap.values().stream().distinct().collect(Collectors.toList());
        List<UserRoleDTO> userRoleList = userMapper.getRoleByIndustryCodeBatch(industryCodeList, appVerCodeList);

        List<Long> roleIds = userRoleList.stream().filter(x -> industryMap.containsKey(x.getIndustryCode())
                && StringUtils.equals(x.getAppVerCode(), industryMap.get(x.getIndustryCode())))
                .map(UserRoleDTO::getRoleId).distinct().collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(roleIds)) {
            //查询已有角色
            log.info("查询已有角色{}", JSON.toJSONString(roleIds));
            List<Long> havRoleIds = userMapper.getRoleByUserId(userId);
            if (!CollectionUtils.isEmpty(havRoleIds)) {
                roleIds = roleIds.stream().filter(roleId -> !havRoleIds.contains(roleId)).collect(Collectors.toList());
            }
            // 新增用户与角色管理
            log.info("新增用户与角色管理{}", JSON.toJSONString(roleIds));
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (!list.isEmpty()) {
                userMapper.batchUserRole(list);
            }
        }
    }

    @Override
    public int countAdminUserNameByTenantId(String tenantId) {
        return userMapper.countAdminUserNameByTenantId(tenantId);
    }

    /**
     * 新增租户管理
     *
     * @param sysTenantInfo 租户管理
     * @return 结果
     */
    private void insertSysTenantInfo(SysTenantInfo sysTenantInfo, String userName, Boolean handleOrderUserInfoOnly) {
        if (StrUtil.isEmpty(sysTenantInfo.getDeptId())) {
            sysTenantInfo.setDeptId(this.getDeptId(sysTenantInfo));
        }
        List<SysTenantInfo> sysTenantInfos = sysTenantInfoMapper.selectTenantInfoByTenantId(sysTenantInfo.getTenantId());
        if (CollUtil.isEmpty(sysTenantInfos)) {
            sysTenantInfo.setCreateBy(userName);
            sysTenantInfo.setCreateTime(new Date());
            sysTenantInfoMapper.insert(sysTenantInfo);
            //新增部门,修改父节点的下属节点
            asyncOrderDealService.asyncInsertSysTen2Redis(sysTenantInfo);
        }
        // 如果只需处理订单和客户信息，则不初始化部门及网格信息
        if (BooleanUtils.isNotTrue(handleOrderUserInfoOnly)) {
            // 更新租户组织树根节点
            CreateTenantRootDeptRequest request = new CreateTenantRootDeptRequest();
            request.setTenantId(sysTenantInfo.getTenantId());
            request.setTenantName(sysTenantInfo.getTenantName());
            request.setTenantTowerDeptId(sysTenantInfo.getDeptId());
            this.insertTenantRootDept(request, userName, sysTenantInfo.getTenantTrades());
        }
    }

    /**
     * 新增租户组织树根节点
     *
     * @return void
     * @param: request
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    private void insertTenantRootDept(CreateTenantRootDeptRequest request, String userName, List<SysTenantTrade> tradeList) {
        // key -> industryCode value -> tradeInfo
        Map<String, SysTenantTrade> tradeMap = tradeList.stream().collect(Collectors.toMap(SysTenantTrade::getIndustryCode, p -> p, (a, b) -> a));
        List<SysDept> sysDepts = sysTenantDeptMapper.countByDeptIdBatch(request.getTenantId(), Lists.newArrayList(tradeMap.keySet()));
        // 行业去重后的订购行业信息
        List<SysTenantTrade> distinctTradeList = Lists.newArrayList(tradeMap.values());

        Map<String, List<SysDept>> stringListMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(sysDepts)){
            stringListMap = sysDepts.stream().collect(Collectors.groupingBy(SysDept::getIndustryCode));
        }
        Map<String, List<SysDept>> finalStringListMap = stringListMap;
        List<SysDept> insertDeptList = new ArrayList<>();
        List<SysDept> updateDeptList = new ArrayList<>();
        int size = distinctTradeList.size();
        List<String> deptIds = OrgUtil.getOrgCodeByParentOrgIdAndTenantIdV2(DEPT_TREE_ROOT_ID, request.getTenantId(), size);
        log.info("insertTenantRootDept size:{}, deptIds:{}", size, deptIds);
        String deptId = "";
        int index = 0;
        for (SysTenantTrade trade : distinctTradeList) {
            SysDept dept = new SysDept();
            dept.setParentId(DEPT_TREE_ROOT_ID);
            dept.setParentName(request.getTenantName());
            dept.setAncestors(DEPT_TREE_ROOT_ID);
            dept.setDeptLevel("1");
            dept.setTenantId(request.getTenantId());
            dept.setTenantName(request.getTenantName());
            dept.setTenantTowerDeptId(request.getTenantTowerDeptId());
            dept.setCreateTime(new Date());
            dept.setCreateBy(userName);
            deptId = deptIds.get(index);
            dept.setDeptId(deptId);
            // 客户名称-行业应用-应用版本
            dept.setDeptName(String.format("%s-%s-%s", request.getTenantName(), trade.getIndustryName(), trade.getAppVerCode()));
            dept.setIndustryCode(trade.getIndustryCode());
            dept.setAppVerCode(trade.getAppVerCode());
            log.info("============countByDeptId");
            if (CollectionUtils.isEmpty(finalStringListMap.get(dept.getIndustryCode()))){
                insertDeptList.add(dept);
                // 有插入的新组织，序号增1，否则序号不变
                index += 1;
            }else {
                updateDeptList.add(dept);
            }
//            if (sysTenantDeptMapper.countByDeptId(dept) == 0) {
//                log.info("============insertDept===={}", dept.getDeptId());
//                sysTenantDeptMapper.insertDept(dept);
//            } else {
//                // 更新客户组织表的行业版本号
//                sysTenantDeptMapper.updateAppVerCode(dept);
//            }
            log.info("============insertTenantRootDept==={}", JSON.toJSONString(dept));
        }
        if (!CollectionUtils.isEmpty(insertDeptList)){
            sysTenantDeptMapper.insertDeptBatch(insertDeptList);
        }
        if (!CollectionUtils.isEmpty(updateDeptList)){
            // 更新客户组织表的行业版本号
            sysTenantDeptMapper.updateAppVerCodeBatch(updateDeptList);
        }
        //更新最高级网格
        addFirstGrid(request, request.getTenantId(), deptId, userName, tradeList);
    }

    private void addFirstGrid(CreateTenantRootDeptRequest request, String tenantId, String deptId, String createBy, List<SysTenantTrade> tradeList) {
        List<GridVO> gridVOList = new ArrayList<>();
        Date now = new Date();
        List<String> industryCodeList = tradeList.stream().map(SysTenantTrade::getIndustryCode).collect(Collectors.toList());
        List<GridVO> gridList = sysTenantDeptMapper.findFirstGridBatch(tenantId, industryCodeList);
        List<String> firstGridIndustryCodeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(gridList)){
            firstGridIndustryCodeList = gridList.stream().map(BaseIndustryEntity::getIndustryCode).collect(Collectors.toList());
        }
        List<String> finalFirstGridIndustryCodeList = firstGridIndustryCodeList;
        tradeList.forEach(trade -> {
            GridVO gridVO = new GridVO();
            gridVO.setTenantId(tenantId);
            gridVO.setIndustryCode(trade.getIndustryCode());
            gridVO.setIndustryName(trade.getIndustryName());
            gridVO.setAppVerCode(trade.getAppVerCode());
            gridVO.setGridEmCenter(deptId);
            gridVO.setCreator(createBy);
            gridVO.setCreateTime(now);
            gridVO.setGridCode(NonceUtil.getNonceStr());
            gridVO.setGridId(Long.parseLong(gridVO.getGridCode()));
            gridVO.setParentId(0L);
            gridVO.setParentCode("0");
            gridVO.setDeleteFlag(0);
            //租户名-行业编码名-行业版本编码
            gridVO.setGridName(request.getTenantName() + "-" + gridVO.getIndustryName() + "-" + gridVO.getAppVerCode());
            gridVO.setGridLevelCode("101" + "," + gridVO.getGridCode());
            gridVO.setGridLevel("1");
            //校验顶级网格是否存在
            if (!finalFirstGridIndustryCodeList.contains(trade.getIndustryCode())) {
                gridVOList.add(gridVO);
            }

        });
        int result = 0;
        if (!CollectionUtils.isEmpty(gridVOList)) {
            result = sysTenantDeptMapper.insertGridInfoList(gridVOList);
        }
        log.info("OrderSystemService addFirstGrid param:{}, result:{}", JSON.toJSONString(gridVOList), JSON.toJSONString(result));

    }


    /**
     * 获取deptId,根据行政区域编码转换对应为部门编码
     *
     * @param sysTenantInfo
     * @return
     */
    private String getDeptId(SysTenantInfo sysTenantInfo) {
        if (StrUtil.isNotEmpty(sysTenantInfo.getCountyId())) {
            return this.getDeptCodeByAreaCode(sysTenantInfo.getCountyId(), VideoSystemEnums.AreaLevel.COUNTY.getIndex());
        }
        if (StrUtil.isNotEmpty(sysTenantInfo.getCityId())) {
            return this.getDeptCodeByAreaCode(sysTenantInfo.getCityId(), VideoSystemEnums.AreaLevel.CITY.getIndex());
        }
        if (StrUtil.isNotEmpty(sysTenantInfo.getProvinceId())) {
            return this.getDeptCodeByAreaCode(sysTenantInfo.getProvinceId(), VideoSystemEnums.AreaLevel.PROVINCE.getIndex());
        }
        //省,市,县都不传情况,返回空
        return StrUtil.isNotEmpty(sysTenantInfo.getCityId()) ? sysTenantInfo.getCityId() : sysTenantInfo.getProvinceId();
    }

    /**
     * 根据行政区域编码获取对应部门编码
     *
     * @param areaCode 区域代码
     * @param level    级别(省,市,区)
     * @return String
     * @author zheng.li
     */
    private String getDeptCodeByAreaCode(String areaCode, String level) {
        boolean isDirectCity = isDirectCity(areaCode);
        String result = null;
        //省分级别
        if (VideoSystemEnums.AreaLevel.PROVINCE.getIndex().equals(level)) {
            return getProvinceDeptCode(areaCode);
        }
        //地市级别
        if (VideoSystemEnums.AreaLevel.CITY.getIndex().equals(level)) {
            List<SysAreaDeptManageResp> sysCityDeptManageRespList = null;
            if (isDirectCity) {
                //直辖市处理逻辑,直辖市行政区域编码对应关联表中的省行政区域编码
                String code = VideoSystemEnums.DirectCityArea2Dept.getName(areaCode);
                return getProvinceDeptCode(code);
            } else {
                //非直辖市处理逻辑
                sysCityDeptManageRespList = selectCityDeptByCityAreaCode(areaCode);
                if (CollUtil.isNotEmpty(sysCityDeptManageRespList)) {
                    SysAreaDeptManageResp sysAreaDeptManageResp = sysCityDeptManageRespList.get(0);
                    result = sysAreaDeptManageResp.getCityDeptCode();
                }
            }
            return result;
        }
        //区县级别
        if (VideoSystemEnums.AreaLevel.COUNTY.getIndex().equals(level)) {
            if (isDirectCity) {
                //直辖市处理逻辑,直辖市区县编码对应地市部门编码
                List<SysAreaDeptManageResp> sysCityDeptManageRespList = this.selectCityDeptByCountyAreaCode(areaCode);
                if (CollUtil.isNotEmpty(sysCityDeptManageRespList)) {
                    SysAreaDeptManageResp sysAreaDeptManageResp = sysCityDeptManageRespList.get(0);
                    return sysAreaDeptManageResp.getCityDeptCode();
                }
            } else {
                //非直辖市处理逻辑,根据区县编码获取对应区县部门管理区域编码
                List<SysAreaDeptManageResp> sysCountyDeptManageRespList = selectCountyDeptByCountyAreaCode(areaCode);
                if (CollUtil.isNotEmpty(sysCountyDeptManageRespList)) {
                    SysAreaDeptManageResp sysAreaDeptManageResp = sysCountyDeptManageRespList.get(0);
                    return sysAreaDeptManageResp.getManageCode();
                }
            }
        }
        return null;
    }

    /**
     * 根据区县编码查询地市部门编码
     *
     * @param countyAreaCode
     * @return
     */
    public List<SysAreaDeptManageResp> selectCityDeptByCountyAreaCode(String countyAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectCityDeptByCountyAreaCode(countyAreaCode);
        return BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
    }

    /**
     * 根据省编码查询省部门编码
     *
     * @param areaCode
     * @return
     */
    private String getProvinceDeptCode(String areaCode) {
        List<SysAreaDeptManageResp> sysAreaDeptManageRespList = selectProvinceDeptByProvinceAreaCode(areaCode);
        if (CollUtil.isNotEmpty(sysAreaDeptManageRespList)) {
            SysAreaDeptManageResp sysAreaDeptManageResp = sysAreaDeptManageRespList.get(0);
            return sysAreaDeptManageResp.getProvinceDeptCode();
        }
        return null;
    }


    /**
     * 根据区县行政区域编码查询管理区域编码
     *
     * @param countyAreaCode
     * @return List<SysAreaDeptManage>
     * @author zheng.li
     */
    private List<SysAreaDeptManageResp> selectCountyDeptByCountyAreaCode(String countyAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectCountryDeptByCountyAreaCode(countyAreaCode);
        return BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
    }

    /**
     * 根据地市行政区域编码查询市部门编码
     *
     * @param cityAreaCode
     * @return List<SysAreaDeptManage>
     * @author zheng.li
     */
    private List<SysAreaDeptManageResp> selectCityDeptByCityAreaCode(String cityAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectCityDeptByCityAreaCode(cityAreaCode);
        return BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
    }


    /**
     * 根据省份行政区域编码查询管理区域编码
     *
     * @param provinceAreaCode
     * @return List<SysAreaDeptManage>
     * @author zheng.li
     */
    private List<SysAreaDeptManageResp> selectProvinceDeptByProvinceAreaCode(String provinceAreaCode) {
        List<SysAreaDeptManage> sysAreaDeptManageList = sysAreaDeptManageMapper.selectProvinceDeptByProvinceAreaCode(provinceAreaCode);
        return BeanUtil.copyToList(sysAreaDeptManageList, SysAreaDeptManageResp.class);
    }

    /**
     * 是否直辖市判断
     *
     * @return boolean
     * @param: code
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    private boolean isDirectCity(String code) {
        return (StringUtils.isNotEmpty(code) && (code.startsWith(BEIJING_PREFIX) || code.startsWith(TIANJIN_PREFIX)
                || code.startsWith(SHANGHAI_PREFIX) || code.startsWith(CHONGQING_PREFIX)));
    }
}
