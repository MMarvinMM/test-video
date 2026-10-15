package com.chinatower.platform.project.pushsys.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.pushsys.common.Constants;
import com.chinatower.platform.project.pushsys.mapper.ManagementAreaMapper;
import com.chinatower.platform.project.pushsys.mapper.MessageMapper;
import com.chinatower.platform.project.pushsys.service.IPushSysService;
import com.chinatower.platform.project.system.constants.MongoConstants;
import com.chinatower.platform.project.system.dao.SysUserHisMongoDAO;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.domain.SysUserRole;
import com.chinatower.platform.project.system.domain.dto.SysUserHisMongoDTO;
import com.chinatower.platform.project.system.domain.dto.UserQueryDTO;
import com.chinatower.platform.project.system.mapper.SysDeptMapper;
import com.chinatower.platform.project.system.mapper.SysUserMapper;
import com.chinatower.platform.project.system.mapper.SysUserRoleMapper;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.*;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;


/**
 * 推送变更服务实现类
 *
 * @author zl
 * @since 2022-02-14 12:03:37
 */
@Service("pushSysService")
@Slf4j
@SuppressWarnings("all")
public class PushSysServiceImpl implements IPushSysService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private MessageMapper messageDAO;

    @Autowired
    private ManagementAreaMapper managementAreaDao;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysDeptMapper deptMapper;


    @Autowired
    private SysUserRoleMapper userRoleMapper;


    @Autowired
    private ISysUserService sysUserService;

    @Resource
    private SysUserHisMongoDAO sysUserHisMongoDAO;


    /**
     * 5.4.8.铁塔系统用户数据变更通知推送
     *
     * @return
     */
    public Result userPush(Map map) {
        log.info("*************5.4.8.铁塔系统用户数据变更通知推送入参***************："+ JSON.toJSONString(map));
        try{
            List dataList = (List) map.get(Constants.DATA);
            for (int i = Constants.ZERO; i < dataList.size(); i++) {
                UserPushVO userPushVO = JSON.parseObject(JSON.toJSONString(dataList.get(i)), UserPushVO.class);
                if (StringUtils.isEmpty(userPushVO.getUserCode())) {
                    return Result.fail(Constants.USERCODE_EMPTY_MSG);
                }
                if (StringUtils.isEmpty(userPushVO.getFlag())) {
                    return Result.fail(Constants.FLAG_EMPTY_MSG);
                }
                //1:新增；2:修改；3：删除；
                String flag = userPushVO.getFlag();
                if (!(Constants.num_str_3).equals(flag)) {
                    String platformCode = "";
                    SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
                    if (ObjectUtil.isNotNull(sysConfig)) {
                        platformCode = sysConfig.getConfigValue();
                    }

                    SysUser user=new SysUser();
                    user.setPageNum(1);
                    user.setPageSize(10);
                    user.setExactUserName(userPushVO.getUserCode());
                    //判断该人员是否存在
                    user.setPlatformCode(platformCode);
                    List<SysUser> usersVOList = sysUserMapper.selectUserList(user);
                    user.setUserName(userPushVO.getUserCode());
                    user.setNickName(userPushVO.getUserName());
                    if(!StringUtils.isEmpty(userPushVO.getOrgCode())){
                        user.setDeptId(userPushVO.getOrgCode().substring(Constants.ZERO,Constants.SIX));
                    }
                    //add by liuxt 20231113
                    wrapTowerUserDept(user);
                    user.setPhonenumber(userPushVO.getMobile());
                    if(CollUtil.isNotEmpty(usersVOList)){
                        //判断 如果存在的历史账号有客户账号 则不更新（岳总要求）
                        boolean customerFlag = false;
                        for (SysUser sysUser:usersVOList) {
                            if(Constants.num_str_2.equals(sysUser.getUserType())){
                                customerFlag = true;
                                break;
                            }
                        }
                        if(!customerFlag){
                            user.setUpdateBy(Constants.SYSTEM_STR);
                            user.setUpdateTime(new Date());
                            sysUserMapper.updateUserByUserName(user);
                        }
                    }else{
                        if(sysUserService.checkSensitiveWords(user.getUserName())){
                            return Result.fail(com.chinatower.platform.project.system.constants.Constants.ACCOUNT_NOT_RULE);
                        }
                        //创建用户
                        user.setPassword(SecurityUtils.encryptPassword(configService.selectConfigValueByKey(Constants.INIT_PASSWORD)));
                        user.setCreateTime(new Date());
                        user.setCreateBy(Constants.SYSTEM_STR);
                        user.setSource(Constants.num_str_0);
                        user.setTowerUserSource(Constants.num_str_0);
                        user.setStatus(Constants.num_str_1);
                        user.setUserType(Constants.num_str_0);
                        user.setUserId(IdUtils.fastSimpleUUID());
                        sysUserMapper.insertUser(user);
                        //此处需要给账号添加默认角色(客户经理)
                        List<SysUserRole> userRoleList = new ArrayList<>();
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setRoleId(Constants.CUSTOMER_MANAGER_NUM);
                        sysUserRole.setUserId(user.getUserId());
                        userRoleList.add(sysUserRole);
                        userRoleMapper.batchUserRole(userRoleList);
                    }
                } else {
//                    sysUserMapper.deleteUserByName(userPushVO.getUserCode());
                    SysUser deleteUser = sysUserMapper.selectUserOnlyByUserName(userPushVO.getUserCode());
                    if (Objects.nonNull(deleteUser)) {
                        saveSysUserToMongoHis(Collections.singletonList(deleteUser.getUserId()));
                    }
                }
            }
        }catch(Exception e){
            return Result.fail(e.getMessage());
        }

        return Result.success(Constants.PUSH_SUCCESS_MSG);
    }


    private void wrapTowerUserDept(SysUser user) {
        SysDept sysDept = deptMapper.selectDeptOnlyById(user.getDeptId().substring(0, 6));
        if (null == sysDept) {
            throw new RuntimeException("上级部门不允许为空!");
        }

        switch (sysDept.getDeptLevel()) {
            case "1":
                user.setProvinceCode(Constants.STR_100000);
                user.setProvinceId(Constants.STR_100000);
                user.setCityCode(Constants.STR_100001);
                user.setCityId(Constants.STR_100001);
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
     * 5.4.3.客户账号变更推送
     *
     * @param map
     * @return
     */

    public Result customer(Map map) {
        log.info("*************5.4.3.客户账号变更推送入参***************："+ JSON.toJSONString(map));
        try {
            Map data = (Map) map.get(Constants.DATA);
            if (StringUtil.isEmpty((String)data.get(Constants.CUSTOMERCODE))) {
                return Result.fail(Constants.MESS_1001,Constants.STR_MESG_1);
            }
            if (StringUtil.isEmpty((String)data.get(Constants.CUSTOMERNAME))) {
                return Result.fail(Constants.MESS_1001,Constants.STR_MESG_2);
            }
            if (StringUtil.isEmpty((String)data.get(Constants.ADMIN))) {
                return Result.fail(Constants.MESS_1001,Constants.STR_MESG_3);
            }
            if (StringUtil.isEmpty((String)data.get(Constants.PASSWORD))) {
                return Result.fail(Constants.MESS_1001,Constants.STR_MESG_4);
            }
            CustomerVO customerVO = new CustomerVO();
            BeanUtils.populate(customerVO,data);
            int result = messageDAO.updateCustomer(customerVO);
            if(result <= Constants.ZERO){
                log.info(Constants.LOG_FAIL_1);
                return Result.fail(Constants.MESS_1004,Constants.UPDT_CUSTOM_FAIL);
            }
            // 更新租户表
            TenantVO tenantVO = new TenantVO();
            tenantVO.setTenantId(customerVO.getCustomerCode());
            tenantVO.setTenantName(customerVO.getCustomerName());
//            tenantVO.setTenantManager(customerVO.getAdmin());
            messageDAO.updateTenant(tenantVO);
            //根据租户id更新原有租户管理员
            messageDAO.updateUser(tenantVO);
            // 判断用户新增还是删除
            CustomerVO vo = messageDAO.getCustomer(customerVO.getCustomerCode());
            SysUser userVO = new SysUser();
            userVO.setUserName(customerVO.getAdmin());
            userVO.setTenantId(customerVO.getCustomerCode());
            userVO.setIsTenantAdmin(Constants.YES_FLAG);
            // 密码做加密处理
            userVO.setPassword(SecurityUtils.encryptPassword(configService.selectConfigValueByKey(Base64.decodeBase64(customerVO.getPassword()).toString())));
            int resultCount = Constants.ZERO;
            if(vo!=null){
                UserQueryDTO dto = new UserQueryDTO();
                dto.setUserId(vo.getUserId());
                String platformCode = "";
                SysConfig sysConfig = RedisUtil.get(SYS_CONFIG_KEY + "platform_code", SysConfig.class, RedisDBType.PRIMARY);
                if (ObjectUtil.isNotNull(sysConfig)) {
                    platformCode = sysConfig.getConfigValue();
                }
                dto.setPlatformCode(platformCode);
                SysUser sysUserVO=sysUserMapper.selectUserById(dto);
                if(sysUserVO == null){
                    // 新增
                    userVO.setSource(Constants.YES_FLAG);
                    userVO.setUserId(IdUtils.fastSimpleUUID());
                    resultCount = sysUserMapper.insertUser(userVO);
                    if(resultCount == Constants.ZERO){
                        // log.info("==========业务处理异常:用户信息保存失败!");
                        throw new Exception(Constants.THROW_MSG_1);
                    }
                    //此处需要存入账号对应角色 待处理

                } else{
                    userVO.setUserId(sysUserVO.getUserId());
                    // 更新
                    resultCount= sysUserMapper.updateUser(userVO);
                    if(resultCount == Constants.ZERO){
                        // log.info("==========业务处理异常:更新用户信息失败!");
                        throw new Exception(Constants.THROW_MSG_2);
                    }
                }
            }



        } catch (Exception e) {
            //log.error("5.4.3.客户账号变更推送操作失败："+e);
            return Result.fail(Constants.MESS_1004,e.getLocalizedMessage());
        }
        // log.info("*************5.4.3.客户账号变更推送成功.*************");
        return Result.success(Constants.PUSH_SUCCESS_MSG);
    }


    /**
     * 5.4.6.	管理区域变更通知推送
     * @param map
     * @author: zhaol
     * @date: 2022/2/02
     * @return
     */
    public Result updateManagementArea( Map map) {
        log.info("管理区域变更通知推送："+ JSON.toJSONString(map));
        try {
            List<Map> list = (List<Map>) map.get(Constants.DATA);
            JSONArray data= JSONArray.parseArray(JSON.toJSONString(list));
            if(!data.isEmpty() && data.size()>0){

                //查询管理区域是否存在
                Set<ManagementAreaVO>  managementAreaSet = new HashSet<ManagementAreaVO>();
                Set<String>  managementAreaCodeSet = new HashSet<String>();
                for (int count = Constants.ZERO;count < data.size();count++) {
                    JSONObject managementAreaObj = data.getJSONObject(count);
                    ManagementAreaVO managementAreaVO = JSON.toJavaObject(managementAreaObj, ManagementAreaVO.class);

                    if (managementAreaVO != null) {
                        managementAreaSet.add(managementAreaVO);//管理区域
                        managementAreaCodeSet.add(managementAreaVO.getAreaCode());
                    }
                }

                ManagementAreaVO newManagementAreaVO = new ManagementAreaVO();
                newManagementAreaVO.setManagementAreaCodeSet(managementAreaCodeSet);
                List<ManagementAreaVO> managementAreaVOList = managementAreaDao.getManagementArea(newManagementAreaVO);

                List<ManagementAreaVO> updateList = managementAreaSet.stream().filter(
                        item -> managementAreaVOList.stream().map(managementArea -> managementArea.getAreaCode()).collect(Collectors.toList()).
                                contains(item.getAreaCode())).collect(Collectors.toList());

                List<ManagementAreaVO> addList = managementAreaSet.stream().filter(
                        item -> !managementAreaVOList.stream().map(dev -> dev.getAreaCode()).collect(Collectors.toList()).
                                contains(item.getAreaCode())).collect(Collectors.toList());

                if (updateList != null && updateList.size() > 0) {
                    managementAreaDao.uptManagementAreaInfoBatch(updateList);//更新
                }
                if (addList != null && addList.size() > 0) {
                    managementAreaDao.addManagementAreaInfoBatch(addList); //新增
                }


            }

        } catch (Exception e) {
            // log.error("5.4.6.管理区域变更通知推送操作失败："+e);
            return Result.fail(Constants.MESS_1004,Constants.THROW_MSG_3+e.getLocalizedMessage());
        }
        return Result.success(Constants.PUSH_SUCCESS_MSG);
    }


    /**
     * 5.4.4.组织机构变更通知推送
     */

    public Result organInfoPush( Map map) {

        try {
            List dataList = (List) map.get(Constants.DATA);
            for (int i = Constants.ZERO; i < dataList.size(); i++) {
                OrganInfoPushVO organInfoPushVO = JSON.parseObject(JSON.toJSONString(dataList.get(i)), OrganInfoPushVO.class);
                if (StringUtils.isEmpty(organInfoPushVO.getOrgCode())) {
                    return  Result.fail(Constants.CHECK_ORG_CODE);
                }
                SysDept dept=new SysDept();
                dept.setDeptId(organInfoPushVO.getOrgCode());
                dept.setDeptName(organInfoPushVO.getOrgName());
                dept.setParentId(organInfoPushVO.getOrgParentCode());
                String orgPath=organInfoPushVO.getOrgPath();
                if(StringUtils.isNotEmpty(orgPath)){
                    orgPath=orgPath.replaceAll("/",",").substring(1);
                    dept.setAncestors(orgPath);
                }
                dept.setBussnessUnit("01");
                dept.setDeptLevel(organInfoPushVO.getOrgLevel());
                String deleteFlag = Constants.EMPTY_STR;
                if ((Constants.STR_CODE_00).equals(organInfoPushVO.getOrgStatus())) {
                    deleteFlag = Constants.num_str_1;
                } else if ((Constants.STR_CODE_01).equals(organInfoPushVO.getOrgStatus())) {
                    deleteFlag = Constants.num_str_0;
                }
                dept.setStatus(deleteFlag);
                sysDeptMapper.updateDept(dept);
            }
        } catch (Exception e) {
            StackTraceElement stackTraceElement = e.getStackTrace()[Constants.ZERO];
            String error = "--Method:" + stackTraceElement.getMethodName() +
                    "--Line:" + stackTraceElement.getLineNumber() +
                    "--error:" + e.toString();
            log.info("5.4.4.组织机构变更通知推送失败:" + error);
            return  Result.fail(error);
        }

        return  Result.success(Constants.PUSH_SUCCESS_MSG);
    }


    private int saveSysUserToMongoHis(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return userIdList.size();
        }
        List<SysUserHisMongoDTO> infos = sysUserMapper.selectByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(infos)) {
            return infos.size();
        }
        sysUserHisMongoDAO.insertAll(infos, MongoConstants.MONGO_TABLE_NAME_SYS_USER_HIS, MongoDBType.PRIMARY);
        return sysUserMapper.deletePhysicalByUserIdList(userIdList);
    }
}
