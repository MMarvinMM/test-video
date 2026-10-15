package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.system.domain.dto.TreeselectBytenantIdDTO;
import com.chinatower.platform.project.system.domain.vo.SaveDeptParam;
import com.chinatower.platform.project.system.domain.vo.SysDeptDetailVO;

import com.chinatower.system.api.model.*;
import com.chinatower.system.api.request.dept.QuTenantDeptTopRequest;
import com.chinatower.system.api.request.tenant.CreateTenantRootDeptRequest;
import com.chinatower.system.api.resp.SysDeptResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 部门管理 服务层
 *
 * @author chinatower
 */
public interface ISysDeptService {




    /**
     * 查询租户部门顶级结构信息
     *
     * @param dept 部门信息
     * @return 返回平级结构信息集合
     */
    Map<String, Object> selectTennatTop(SysDept dept);



    /**
     * 查询租户部门顶级结构信息
     *
     * @param dept 部门信息
     * @return 返回平级结构信息集合
     */
    List<SysDept> listSubsetDeptTree(SysDept dept);


    List<TreeSelect> getTennatTreeselect(SysDept dept);

    /**
     * 根据租户ID查询租户下用户树
     * @param dept
     * @return
     */
    List<TreeSelectBySystem> getTreeselectByTenantId(TreeselectBytenantIdDTO dept);
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 查询租户组织数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDept> selectTenantDeptList(SysDept dept);

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    List<SysDept> selectDeptTree(List<SysDept> depts);


    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<Integer> selectDeptListByRoleId(String roleId);

    List<String> selectTennatListByRoleId(String roleId);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept selectDeptById(String deptId);

    SysDept selectDeptByIdTower(String deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean hasChildByDeptId(String deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(String deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    String checkDeptNameUnique(SysDept dept);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    SysDeptResponse insertDept(SaveDeptParam dept);

    /**
     * 新增租户组织树根节点
     *
     * @param createTenantRootDeptRequest 创建租户组织树根节点入参
     * @return 结果
     */
    int insertTenantRootDept(CreateTenantRootDeptRequest createTenantRootDeptRequest);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    int updateDept(SaveDeptParam dept);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int deleteDeptById(String deptId);

    /**
     * 查询所有的部门信息
     * @return 部门信息
     */
    List<SysDept> selectAll();

    /**
     * 查询部门的树结构信息
     * @return 部门的树结构信息
     */
    List<TreeSelect> selectTreeSelect();

    List<TreeSelect> userDeptTreeselect();

    Map<String,String> selectDeptWithNameRel();

    /**
     * 查询部门管理数据,返回树结构信息,定时任务调用
     * @author 李峥
     * @param dept 部门信息
     * @return 返回树结构信息
     */
    List<SysDeptResponse> treeList(SysDept dept);

    /**
     * 查询部门管理数据,返回平级结构信息,定时任务调用
     * @author 李峥
     * @param dept 部门信息
     * @return 返回平级结构信息
     */
    List<SysDeptResponse> nonTreeList(SysDept dept);



    int deleteTenantDeptById(String deptId);

    void updateByTenantId(SysDept dept);

    List<TreeSelect> getVideoTreeselect(SysDept dept);

    List<SysDept> getDeptListAll(SysDept dept);

    List<TreeSelect> getStreetTreeselect(SysDept dept);

    /**
     * 查询租户部门管理数据,返回树结构信息,视联调用
     * @author 李峥
     * @param quTenantDeptTopRequest 请求实体
     * @return 返回树结构信息
     */
    List<SysDeptResponse> tenantTreeList2Video(QuTenantDeptTopRequest quTenantDeptTopRequest);

    /**
     * 查询租户部门顶级结构信息
     * @author 李峥
     * @param dept 部门信息
     * @return 返回平级结构信息集合
     */
    Map<String, Object> selectTenantTop2Video(SysDept dept);


    /**
     * 根据人员查租户的组织人员树
     * @param sysUser
     * @return
     */
    List<TreeSelectBySystem> getTennatTree(SysUser sysUser);


    /**
     * 查询已选定设备
     * @param deptId
     * @return
     */
    SysDeptDetailVO getDeptDevices(String deptId,List<String> deptIdList);

    SysDeptDetailVO deptDeviceDetailBatch(List<String> list);

    /**
     * 获取管理区域列表(树形) 提供给app
     * @param deptId
     * @return
     */
    List<SysDeptDeviceTreeVO> getDeptStreetTree(String deptId);


    /**
     * 查询设备范围
     * @param deptId
     * @return
     */
    SysDeptDetailVO getDeptDeviceScop();

    /**
     * 根据用户权限获取用户部门信息
     * @param user
     * @return
     */
    Set<DeptVO> getDeptListByUserDataScope(SysUser user);

    /**
     * 查询管理区域树形结构
     * @return
     */
    List<SysDeptAreaVO> getDeptAndAreaTree();

    List<SysDeptDeviceTreeVO> getCameraTree();
    List<SysDeptDeviceTreeVO> getRadarTree();
    List<SysDeptDeviceTreeVO> getSensorTree();
    List<SysDeptDeviceTreeVO> getSatellitesTree();
    /**
     * 查询无人机设备行政区域树
     * @return
     */
    List<SysDeptDeviceTreeVO> getUavTree();
    /**
     * 查询喇叭设备行政区域树
     * @return
     */
    List<SysDeptDeviceTreeVO> getHornTree();
    /**
     * 查询执法记录仪设备行政区域树
     * @return
     */
    List<SysDeptDeviceTreeVO> getLERTree();

    List<SysDeptDeviceVO> getCameraTreeForPC();
    List<SysDeptDeviceVO> getRadarTreeForPC();
    List<SysDeptDeviceVO> getSensorTreeForPC();
    List<SysDeptDeviceVO> getSatellitesTreeForPC();
    /**
     * 查询无人机设备行政区域树
     * @return
     */
    List<SysDeptDeviceVO> getUavTreeForPC();

    List<SysDeptDeviceVO> getUavNestTreeForPC();
    /**
     * 查询喇叭设备行政区域树
     * @return
     */
    List<SysDeptDeviceVO> getHornTreeForPC();

    List<SysDeptDeviceVO> getLERTreeForPC();

    List<SysDeptDeviceTreeVO> getCameraTreeNew();

    void dealHisDeptAlarmTypeRel(OrganAlarmTypeVO organAlarmTypeVO);


    List<String> getDeptIdList(SysDept dept);
}
