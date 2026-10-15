package com.chinatower.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.order.mapper.SysTenantDeptMapper;
import com.chinatower.order.service.IAsyncOrderDealService;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.DeptConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AsyncOrderDealService
 * @Deacription 异步处理服务
 * @Author chenmf
 * @Date 2022/6/13 16:00
 * @Version 1.0
 **/
@Slf4j
@Service
public class AsyncOrderDealService implements IAsyncOrderDealService {

    @Autowired
    private SysTenantDeptMapper sysTenantDeptMapper;

    public static final String ONE_STR = "1";

    /**
     * 插入租户时,异步更新该租户部门id对应的租户以及父部门id集合
     * @param: sysTenantInfo
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    @Async("asyncOrderExecutor")
    public void asyncInsertSysTen2Redis(SysTenantInfo sysTenantInfo) {
        try {
            String deptId = sysTenantInfo.getDeptId();
            String tenantId = sysTenantInfo.getTenantId();
            SysDept sysDept = this.selectDeptById(deptId);
            //得到所有父节点，父节点中加入租户id
            String ancestors = sysDept.getAncestors();
            if (StringUtils.isNotEmpty(ancestors)){
                List<String> ancestorsList = Arrays.asList(ancestors.split(","));
                if (CollUtil.isNotEmpty(ancestorsList)){
                    List<String> tenantRedisKey = ancestorsList.stream().map(x -> (CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + x)).collect(Collectors.toList());
                    Map<String, Object> tenantMap = RedisUtil.multiGetToMap(tenantRedisKey, RedisDBType.PRIMARY);
                    Map<String, Object> redisMap = Maps.newHashMap();
                    for (String anDeptId : ancestorsList) {
                        if (!DeptConstants.ROOT_DEPT.equals(anDeptId)){
//                            List<String> tenantIdList = RedisUtil.getList(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, String.class, RedisDBType.PRIMARY);
                            Object redisData = tenantMap.get(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId);
                            List<String> tenantIdList =  JSON.parseArray(JSON.toJSONString(redisData), String.class);
                            Set<String> tenantSet = new HashSet<>(tenantIdList);
                            tenantSet.add(tenantId);
                            redisMap.put(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + anDeptId, tenantSet);
                        }
                    }
                    RedisUtil.multiSet(redisMap, RedisDBType.PRIMARY);

                }
            }
        } catch (Exception e) {
            log.error("插入机构时,异步更新父节点集合失败:{}" , e.getMessage());
        }
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    private SysDept selectDeptById(String deptId) {
        SysDeptStreetRelVO vo = new SysDeptStreetRelVO();
        vo.setDeptId(deptId);
        List<String> applyScope = sysTenantDeptMapper.selectDeptStreetCodeByDeptId(deptId);
        SysDept sysDept = sysTenantDeptMapper.selectDeptById(deptId);
        sysDept.setApplyScope(applyScope);
        return sysDept;
    }
}