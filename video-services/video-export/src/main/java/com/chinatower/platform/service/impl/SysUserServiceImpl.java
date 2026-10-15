package com.chinatower.platform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.chinatower.common.utils.SqlUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.SysUserMapper;
import com.chinatower.platform.service.SysUserService;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;
import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserMapper userMapper;



    @Override
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "u.tenant_id", acceptColumn = "ic.accept_node", industryCode = "d.INDUSTRY_CODE")
    public List<SysUser> selectUserList(SysUser user) {

        //排序列字段入参合法性检查
        getAllFields(SysUser.class,user);

        Integer pageNum = 1;
        Integer pageSize = 20000;
        // String orderBy = SqlUtil.escapeOrderBySql(user.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, user.getOrderBy());
        List<SysUser> list = userMapper.selectUserList(user);
        SysConfig sysConfig = this.selectConfigByKey("sys_config_phoneNumber");
        if (list.size()>5000){
            throw new CustomException("导出数据最大支持5000条，请按照查询条件导出。");
        }
        list.forEach(item -> {
            String phoneNumber = item.getPhonenumber();
            item.setAesPhoneNumber(AESUtil.encrypt(phoneNumber));
            //如果参数配置值为1则脱敏
            if (Objects.nonNull(sysConfig)) {
                if (DeviceConstants.ONE.equals(sysConfig.getConfigValue())) {
                    item.setPhonenumber(phoneNumber.replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2"));
                }
            }
            List<String> collect = item.getRoles().stream().map(SysRole::getRoleName).collect(toList());
            item.setRoleNames(String.join(",", collect));
            item.setDeptName(item.getDept().getDeptName());
        });
        return list;
    }

    @Override
    public SysConfig selectConfigByKey(String configKey) {
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

}
