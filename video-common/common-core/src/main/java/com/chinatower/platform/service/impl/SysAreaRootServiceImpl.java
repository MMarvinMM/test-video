package com.chinatower.platform.service.impl;


import com.alibaba.fastjson.JSON;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.service.SysAreaRootService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * 提供获取根区域信息的接口方法。
 *
 * @author penghaibo
 */
@Component
@Slf4j
public class SysAreaRootServiceImpl implements SysAreaRootService {

    @Autowired(required = false)
    public AuthBaseMapper authBaseMapper;

    // 缓存键
    private static final String CACHE_KEY = "root_sys_area_info";
    private static final String CACHE_DEPT_KEY = "root_sys_dept_info";
    // 5分钟
    private static final long CACHE_TTL = 5;

    // 创建本地内存缓存
    private static final Cache<String, String> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(CACHE_TTL, TimeUnit.MINUTES)
            .expireAfterWrite(CACHE_TTL, TimeUnit.MINUTES)
            .recordStats()
            .build();

    /**
     * 获取根区域信息
     *
     * @return 根区域信息
     */
    @Override
    public SysArea getRootAreaInfo() {
        String cachedValue =  CACHE.getIfPresent(CACHE_KEY);
        if (StringUtils.isNotBlank(cachedValue)) {
            return JSON.parseObject(cachedValue, SysArea.class);
        }
        // 缓存不存在，查询数据库
        SysArea result = queryRootAreaInfo();
        log.info("查询数据库成功，结果为：{}", result);
        if (result != null) {
            // 缓存有效数据
            CACHE.put(CACHE_KEY, JSON.toJSONString(result));
        } else {
            // 缓存默认根节点信息
            result = new SysArea();
            result.setAreaCode(DeviceConstants.TOWER_ROOT_AREA);
            result.setAreaName(DeviceConstants.TOWER_ROOT_AREA_NAME);
            CACHE.put(CACHE_KEY, JSON.toJSONString(result));
        }
        return result;
    }

    /**
     * 查询根区域信息
     *
     * @return 返回查询到的根区域信息，如果查询失败则返回null
     */
    @Override
    public SysArea queryRootAreaInfo() {
        try {
            return authBaseMapper.getRootAreaInfo();
        } catch (Exception e) {
            log.error("查询数据库失败", e);
            return null;
        }
    }

    /**
     * 创建根节点树状结构视图对象
     *
     * @param target 目标对象
     */
    @Override
    public void createRootTreeVO(Object target) {
        if (target == null) {
            return;
        }
        try {
            Class<?> clazz = target.getClass();
            // 获取根区域信息
            SysArea sysArea = getRootAreaInfo();
            if (sysArea == null) {
                return;
            }
            // 设置code属性
            Method setCodeMethod = findSetMethod(clazz, "setCode");
            if (setCodeMethod != null) {
                setCodeMethod.invoke(target, sysArea.getAreaCode());
            }
            // 设置name属性
            Method setNameMethod = findSetMethod(clazz, "setName");
            if (setNameMethod != null) {
                setNameMethod.invoke(target, sysArea.getAreaName());
            }
        } catch (Exception e) {
            log.error("创建树状结构VO对象失败", e);
        }
    }

    /**
     * 查找指定名称和参数类型的setter方法
     *
     * @param clazz 要查找的类
     * @param methodName 方法名称
     * @return 返回找到的setter方法，如果未找到则返回null
     */
    private Method findSetMethod(Class<?> clazz, String methodName) {
        try {
            Method method = clazz.getMethod(methodName, String.class);
            if (method.getReturnType() == void.class) {
                return method;
            }
        } catch (NoSuchMethodException e) {
            log.error("未找到该方法", e);
        }
        return null;
    }

    /**
     * 获取根组织信息
     *
     * @return 根组织信息
     */
    @Override
    public SysDept getRootDeptInfo() {
        String cachedValue = CACHE.getIfPresent(CACHE_DEPT_KEY);
        if (StringUtils.isNotBlank(cachedValue)) {
            return JSON.parseObject(cachedValue, SysDept.class);
        }
        // 缓存不存在，查询数据库
        SysDept result = queryRootDeptInfo();
        log.info("查询数据库sys_dept表成功，结果为：{}", result);
        if (result != null) {
            // 缓存有效数据
            CACHE.put(CACHE_DEPT_KEY, JSON.toJSONString(result));
        } else {
            // 缓存默认根节点信息
            result = new SysDept();
            result.setDeptId(DeviceConstants.TOWER_ROOT_ORG);
            result.setDeptName(DeviceConstants.TOWER_ROOT_ORG_NAME);
            CACHE.put(CACHE_DEPT_KEY, JSON.toJSONString(result));
        }
        return result;
    }

    /**
     * 查询根组织信息
     *
     * @return 返回查询到的根组织信息，如果查询失败则返回null
     */
    @Override
    public SysDept queryRootDeptInfo() {
        try {
            return authBaseMapper.getRootDeptInfo();
        } catch (Exception e) {
            log.error("查询数据库失败", e);
            return null;
        }
    }

    /**
     * 创建根组织节点树状结构视图对象
     *
     * @param target 目标对象
     */
    @Override
    public void createRootDeptTreeVO(Object target) {
        if (target == null) {
            return;
        }
        try {
            Class<?> clazz = target.getClass();
            // 获取根区域信息
            SysDept sysDept = getRootDeptInfo();
            if (sysDept == null) {
                return;
            }
            // 设置code属性
            Method setCodeMethod = findSetMethod(clazz, "setCode");
            if (setCodeMethod != null) {
                setCodeMethod.invoke(target, sysDept.getDeptId());
            }
            // 设置name属性
            Method setNameMethod = findSetMethod(clazz, "setName");
            if (setNameMethod != null) {
                setNameMethod.invoke(target, sysDept.getDeptName());
            }
        } catch (Exception e) {
            log.error("创建组织树状结构VO对象失败", e);
        }
    }
}
