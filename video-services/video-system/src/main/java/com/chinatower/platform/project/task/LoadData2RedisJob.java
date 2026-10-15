package com.chinatower.platform.project.task;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.CommonConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.service.ISysRoleService;
import com.chinatower.platform.project.system.service.ISysTenantInfoService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.resp.SysDeptResponse;
import com.chinatower.system.api.resp.SysRoleDeptResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoadData2RedisJob {
    //部门service
    private final ISysDeptService sysDeptService;
    //角色service
    private final ISysRoleService sysRoleService;
    //租户service
    private final ISysTenantInfoService sysTenantInfoService;
    //部门状态
    private static final String DEPT_STATUS = "0";
    //根节点
    private static final String ROOT_NODE = "000000";
    //递归
    private static final String RECURSIVE = "1";
    //非递归,不获取子集合数据
    private static final String NOT_RECURSIVE = "0";

    /**
     * 功能说明 : 定时更新铁塔部门表到redis中
     *
     * @return ReturnT<String>
     * @author 李峥
     * @date 2022-01-26
     * @requestID 定时更新铁塔部门表到redis中
     */
    @XxlJob("loadTowerSysDept2RedisHandler")
    public ReturnT<String> loadTowerSysDept2RedisHandler(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTowerSysDept2RedisHandler开始执行,开始时间为:{}", startTime);
        SysDept sysDept = new SysDept();
        sysDept.setStatus(DEPT_STATUS);
        try {
            List<SysDeptResponse> treeListRespData = sysDeptService.treeList(sysDept);
            List<SysDeptResponse> nonTreeListRespData = sysDeptService.nonTreeList(sysDept);
            if (null != treeListRespData && !treeListRespData.isEmpty()) {
                //模拟带根节点集合
                SysDeptResponse rootSysDept = new SysDeptResponse();
                rootSysDept.setDeptId(ROOT_NODE);
                rootSysDept.setChildren(treeListRespData);
                List<SysDeptResponse> rootTreeListRespData = new ArrayList<>();
                rootTreeListRespData.add(rootSysDept);
                log.info("铁塔组织部门表,树形结构:{}", treeListRespData);
                log.info("铁塔组织部门表,非树形结构:{}", nonTreeListRespData);
                List<String> deptList = null;
                for (SysDeptResponse sysDeptResponse : nonTreeListRespData
                ) {
                    deptList = new ArrayList<>();
                    String deptId = sysDeptResponse.getDeptId();
                    deptList.add(deptId);
                    listToSingleKey(rootTreeListRespData, deptList, deptId, NOT_RECURSIVE);
                    RedisUtil.set(CacheConstants.DEPT_SCOPE_LIST_PREFIX + deptId, deptList, RedisDBType.PRIMARY);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTowerSysDept2RedisHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("loadTowerSysDept2RedisHandler执行结束");
    }

    /**
     * 功能说明 : 定时更新租户部门表到redis中
     *
     * @return ReturnT<String>
     * @author 李峥
     * @date 2022-01-26
     * @requestID 定时更新租户部门表到redis中
     */
    @XxlJob("loadTenantSysDept2RedisHandler")
    public ReturnT<String> loadTenantSysDept2RedisHandler(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTenantSysDept2RedisHandler开始执行,开始时间为:{}", startTime);
        try {
            List<SysTenantInfoResponse> sysTenantInfos = sysTenantInfoService.selectAllSysTenant();
            if (CollUtil.isNotEmpty(sysTenantInfos)){
                SysDept sysDept = new SysDept();
                sysDept.setStatus(DEPT_STATUS);
                for (SysTenantInfoResponse sysTenantInfo: sysTenantInfos
                     ) {
                    String tenantId = sysTenantInfo.getTenantId();
                    if (StringUtils.isNotEmpty(tenantId)){
                        sysDept.setTenantId(tenantId);
                        List<SysDeptResponse> treeListRespData = sysDeptService.treeList(sysDept);
                        List<SysDeptResponse> nonTreeListRespData = sysDeptService.nonTreeList(sysDept);
                        if (null != treeListRespData && !treeListRespData.isEmpty()) {
                            //模拟带根节点集合
                            SysDeptResponse rootSysDept = new SysDeptResponse();
                            rootSysDept.setDeptId(ROOT_NODE);
                            rootSysDept.setChildren(treeListRespData);
                            List<SysDeptResponse> rootTreeListRespData = new ArrayList<>();
                            rootTreeListRespData.add(rootSysDept);
                            log.info("租户组织部门表,树形结构:{}", treeListRespData);
                            log.info("租户组织部门表,非树形结构:{}", nonTreeListRespData);
                            List<String> deptList = null;
                            for (SysDeptResponse sysDeptResponse : nonTreeListRespData
                            ) {
                                deptList = new ArrayList<>();
                                String deptId = sysDeptResponse.getDeptId();
                                deptList.add(deptId);
                                listToSingleKey(rootTreeListRespData, deptList, deptId, NOT_RECURSIVE);
                                RedisUtil.set(CacheConstants.DEPT_SCOPE_TENANT_LIST_PREFIX + tenantId + CommonConstants.COLON + deptId, deptList, RedisDBType.PRIMARY);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTowerSysDept2RedisHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("loadTowerSysDept2RedisHandler执行结束");
    }

    /**
     * 集合拆分成单个key,key是省市区编码
     *
     * @param treeListRespData
     * @param deptList
     * @param deptId
     * @param isRecursive
     */
    public void listToSingleKey(List<SysDeptResponse> treeListRespData, List<String> deptList, String deptId, String isRecursive) {
        if (null != treeListRespData && !treeListRespData.isEmpty()) {
            for (SysDeptResponse sysDeptResponse : treeListRespData
            ) {
                List<SysDeptResponse> children = sysDeptResponse.getChildren();
                if (null == children || children.isEmpty()) {
                    continue;
                }
                Integer offset = null;
                for (int i = 0; i < children.size(); i++) {
                    SysDeptResponse deptResponse = children.get(i);
                    String deptResponseDeptId = deptResponse.getDeptId();
                    if (deptId.equals(deptResponseDeptId)) {
                        offset = i;
                    }
                }
                //子节点未找到对应部门id
                if (null == offset && NOT_RECURSIVE.equals(isRecursive)) {
                    listToSingleKey(children, deptList, deptId, NOT_RECURSIVE);
                }
                if (null != offset) {
                    SysDeptResponse deptResponse = children.get(offset);
                    children = deptResponse.getChildren();
                    List<String> list = children.stream().map(SysDeptResponse::getDeptId).collect(Collectors.toList());
                    deptList.addAll(list);
                    listToSingleKey(children, deptList, deptId, RECURSIVE);
                }
                if (RECURSIVE.equals(isRecursive)) {
                    List<String> list = children.stream().map(SysDeptResponse::getDeptId).collect(Collectors.toList());
                    deptList.addAll(list);
                    listToSingleKey(children, deptList, deptId, RECURSIVE);
                }
            }
        }
    }

    /**
     * 功能说明 : 定时更新角色对应的部门表到redis中
     *
     * @return ReturnT<String>
     * @author 李峥
     * @date 2022-01-26
     * @requestID 定时更新角色对应的部门表到redis中
     */
    @XxlJob("loadRoleDept2RedisHandler")
    public ReturnT<String> loadRoleDept2RedisHandler(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("loadRoleDept2RedisHandler开始执行,开始时间为:{}", startTime);
        try {
            List<SysRoleDeptResponse> sysRoleDeptResponseList = sysRoleService.selectAllRoleDept(null);
            if (null != sysRoleDeptResponseList && !sysRoleDeptResponseList.isEmpty()) {
                log.info("角色对应部门关联表数据:{}", sysRoleDeptResponseList);
                if (CollUtil.isNotEmpty(sysRoleDeptResponseList)) {
                    Map<Long, List<String>> listMap = sysRoleDeptResponseList.stream().
                            collect(Collectors.groupingBy(SysRoleDeptResponse::getRoleId,
                                    Collectors.mapping(SysRoleDeptResponse::getDeptId, Collectors.toList())));
                    for (Map.Entry<Long,List<String>> entry: listMap.entrySet()
                    ) {
                        Long key = entry.getKey();
                        List<String> value = entry.getValue();
                        RedisUtil.set(CacheConstants.ROLE_SCOPE_LIST_PREFIX + key, value, RedisDBType.PRIMARY);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("loadRoleDept2RedisHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("loadRoleDept2RedisHandler执行结束");
    }


    /**
     * 功能说明 : 定时更新租户角色部门表到redis中,过期方法
     *
     * @return ReturnT<String>
     * @author 李峥
     * @date 2022-01-26
     * @requestID 定时更新租户角色部门表到redis中
     */
    @Deprecated
    @XxlJob("loadTenantRoleDept2RedisHandler")
    public ReturnT<String> loadTenantRoleDept2RedisHandler(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTenantRoleDept2RedisHandler开始执行,开始时间为:{}", startTime);
        try {
            List<SysRoleDeptResponse> sysRoleDeptResponseList = sysRoleService.selectAllRoleDept(VideoSystemEnums.RoleType.TENANT_USER.getIndex());
            if (null != sysRoleDeptResponseList && !sysRoleDeptResponseList.isEmpty()) {
                log.info("租户角色部门关联表数据:{}", sysRoleDeptResponseList);
                if (CollUtil.isNotEmpty(sysRoleDeptResponseList)) {
                    Map<Long, List<String>> listMap = sysRoleDeptResponseList.stream().
                            collect(Collectors.groupingBy(SysRoleDeptResponse::getRoleId,
                                    Collectors.mapping(SysRoleDeptResponse::getDeptId, Collectors.toList())));
                    for (Map.Entry<Long,List<String>> entry: listMap.entrySet()
                    ) {
                        Long key = entry.getKey();
                        List<String> value = entry.getValue();
                        RedisUtil.set(CacheConstants.ROLE_SCOPE_TENANT_LIST_PREFIX + VideoSystemEnums.RoleType.TENANT_USER.getIndex() + CommonConstants.COLON + key, value, RedisDBType.PRIMARY);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTenantRoleDept2RedisHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("loadTenantRoleDept2RedisHandler执行结束");
    }

    /**
     * 功能说明 : 定时更新铁塔部门表对应租户信息到redis中,铁塔视角使用
     *
     * @return ReturnT<String>
     * @author 李峥
     * @date 2022-01-26
     * @requestID 定时更新铁塔部门表对应租户信息到redis中
     */
    @XxlJob("loadTowerDeptTenant2RedisHandler")
    public ReturnT<String> loadTowerDeptTenant2RedisHandler(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTowerDeptTenant2RedisHandler开始执行,开始时间为:{}", startTime);
        SysDept sysDept = new SysDept();
        sysDept.setStatus(DEPT_STATUS);
        try {
            List<SysDeptResponse> treeListRespData = sysDeptService.treeList(sysDept);
            List<SysDeptResponse> nonTreeListRespData = sysDeptService.nonTreeList(sysDept);
            if (null != treeListRespData && !treeListRespData.isEmpty()) {
                //模拟带根节点集合
                SysDeptResponse rootSysDept = new SysDeptResponse();
                rootSysDept.setDeptId(ROOT_NODE);
                rootSysDept.setChildren(treeListRespData);
                List<SysDeptResponse> rootTreeListRespData = new ArrayList<>();
                rootTreeListRespData.add(rootSysDept);
                log.info("铁塔组织部门表,树形结构:{}", rootTreeListRespData);
                log.info("铁塔组织部门表,非树形结构:{}", nonTreeListRespData);
                List<String> deptList = null;
                for (SysDeptResponse sysDeptResponse : nonTreeListRespData
                ) {
                    deptList = new ArrayList<>();
                    String deptId = sysDeptResponse.getDeptId();
                    deptList.add(deptId);
                    listToSingleKey(rootTreeListRespData, deptList, deptId, NOT_RECURSIVE);
                    List<SysTenantInfoResponse> sysTenantInfoResponses = sysTenantInfoService.selectTenantByDeptIds(deptList);
                    List<String> tenantIdList = sysTenantInfoResponses.stream().collect(Collectors.mapping(SysTenantInfoResponse::getTenantId, Collectors.toList()));
                    RedisUtil.set(CacheConstants.TOWER_DEPT_TENANT_LIST_PREFIX + deptId, tenantIdList, RedisDBType.PRIMARY);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("loadTowerDeptTenant2RedisHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("loadTowerDeptTenant2RedisHandler执行结束");
    }

}
