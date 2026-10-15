package com.chinatower.platform.project.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.sysbusiness.service.IDeptBusinessService;
import com.chinatower.platform.project.system.domain.dto.TreeselectBytenantIdDTO;
import com.chinatower.platform.project.system.domain.vo.SaveDeptParam;
import com.chinatower.system.api.model.TreeSelectBySystem;
import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.project.system.service.ISysTenantInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.request.dept.QuTenantDeptSubsetRequest;
import com.chinatower.system.api.request.dept.QuTenantDeptTopRequest;
import com.chinatower.system.api.resp.SysDeptInfoResponse;
import com.chinatower.system.api.resp.SysDeptResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * video-system
 * 组织信息
 *
 * @author iwhalecloud
 */
@Api(tags = "租户组织")
@RestController
public class SysTennatDeptController extends TowerBaseController {
    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysTenantInfoService sysTenantInfoService;

    @Resource
    IDeptBusinessService deptBusinessService;

    private static final String DEPT_LIST = "deptList";

    /**
     * 根据父级组织ID查询街道树
     */
    @ApiOperation(value = "获取组织下的街道信息树")
    @GetMapping("/base/system/tenantDept/getStreetSelect")
    public Result<List<TreeSelect>> getStreetTreeSelect(SysDept dept) {
        return Result.data(deptService.getStreetTreeselect(dept));
    }

    /**
     * 视联组织功能获取租户组织下拉树列表
     */
    @ApiOperation(value = "获取租户组织下拉树列表")
    @GetMapping("/base/system/tenantDept/getVideoTreeselect")
    public Result<List<TreeSelect>> getVideoTreeselect(@RequestParam(value = "status",required = false) String status) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        SysDept dept = new SysDept();
        dept.setTenantId(user.getTenantId());
        dept.setDeptId(user.getDeptId());
        if(StringUtils.isNotBlank(status)){
            dept.setStatus(status);
        }
        return Result.data(deptService.getVideoTreeselect(dept));
    }

    /**
     * 获取租户组织下拉树列表
     */
    @ApiOperation(value = "获取租户组织下拉树列表")
    @GetMapping("/base/system/tenantDept/getTenantTreeselect")
    public Result<List<TreeSelect>> getTennatTreeselect(SysDept dept) {
        return Result.data(deptService.getTennatTreeselect(dept));
    }

    /**
     * 获取租户组织下拉树列表
     */
    @ApiOperation(value = "获取租户组织下拉树列表")
    @GetMapping("/base/system/tenantDept/getTreeselectBytenantId")
    public Result<List<TreeSelectBySystem>> getTreeselectBytenantId(TreeselectBytenantIdDTO dto) {
        return Result.data(deptService.getTreeselectByTenantId(dto));
    }

    /**
     * 加载对应角色组织列表树
     */
    @ApiOperation(value = "加载对应角色租户组织")
    @GetMapping(value = "/base/system/tenantDept/roleTenantTreeselect/{roleId}")
    public AjaxResult roleTenantTreeselect(@PathVariable("roleId") String roleId) {
        return AjaxResult.success(deptService.selectTennatListByRoleId(roleId));
    }
   /**
     * 根据人员查租户的组织人员树
     */
    @ApiOperation(value = "获取租户组织下拉树列表")
    @PostMapping("/base/system/tenantDept/getTenantTree")
    public Result<List<TreeSelectBySystem>> getTenantTree(@RequestBody SysUser sysUser) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(StringUtils.isBlank(sysUser.getIndustryCode())){
            sysUser.setIndustryCode(user.getIndustryCode());
        }
        if(StringUtils.isBlank(sysUser.getAppVerCode())){
            sysUser.setAppVerCode(user.getAppVerCode());
        }
        if(StringUtils.isBlank(sysUser.getUserName())){
            sysUser.setUserName(user.getUserName());
        }
        return Result.data(deptService.getTennatTree(sysUser));
    }


    /**
     * 根据组织ID查询组织子列表(树形)
     */
    @ApiOperation(value = "根据组织ID查询组织子列表(树形)")
    @PostMapping(value = "/system/tenantDept/listSubsetDept")
    public Result<List<SysDept>> listSubsetDept(@RequestBody QuTenantDeptSubsetRequest quTenantDeptSubsetRequest) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(quTenantDeptSubsetRequest, dept);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        dept.setIndustryCode(user.getIndustryCode());
        dept.setTenantId(user.getTenantId());
        List<SysDept> depts = deptService.listSubsetDeptTree(dept);
        return Result.data(deptService.selectDeptTree(depts));
    }

    /**
     * 根据组织ID查询组织子列表(树形)
     */
    @ApiOperation(value = "根据组织ID查询组织子列表(树形)")
    @PostMapping(value = "/system/tenantDept/listSubsetDeptInfo")
    public Result<List<SysDeptInfoResponse>> listSubsetDeptInfo(@RequestBody QuTenantDeptSubsetRequest quTenantDeptSubsetRequest) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(quTenantDeptSubsetRequest, dept);
        List<SysDept> depts = deptService.listSubsetDeptTree(dept);
        List<SysDeptInfoResponse> responses = BeanUtil.copyToList(depts,SysDeptInfoResponse.class);
        return Result.data(responses);
    }


    /**
     * 获取租户组织列表(顶级结构)
     */
    @ApiOperation(value = "获取租户组织列表(顶级结构)")
    @GetMapping("/system/tenantDept/selectTenantTop")
    public Result<Map<String,Object>> selectTennatTop(SysDept dept) {
        return Result.data(deptService.selectTennatTop(dept));
    }

    /**
     * 获取租户组织(树形)
     */
    @ApiOperation(value = "获取租户组织(树形)")
    @GetMapping("/system/tenantDept/selectTenantTree")
    public AjaxResult selectTenantTree(SysDept dept) {
        List<SysDept> depts = deptService.selectTenantDeptList(dept);
        return AjaxResult.success(deptService.selectDeptTree(depts));
    }

    /**
     * 获取租户组织列表(顶级结构)
     */
    @ApiOperation(value = "获取租户组织列表(顶级结构)")
    @PostMapping(value = "/system/tenantDept/selectTenantTop")
    public Result<Map<String, Object>> selectTennatTop(@RequestBody QuTenantDeptTopRequest quTenantDeptTopRequest) {
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(quTenantDeptTopRequest, dept);
        Map<String, Object> map = deptService.selectTennatTop(dept);
        List<SysDept> returnDepts = (List<SysDept>)map.get(DEPT_LIST);
        List<SysDeptInfoResponse> responses = BeanUtil.copyToList(returnDepts, SysDeptInfoResponse.class);
        map.put(DEPT_LIST, responses);
        return Result.data(map);
    }





    /**
     * 根据组织编号获取详细信息
     */
    @ApiOperation(value = "根据组织编号获取详细信息")
    @GetMapping(value = "/system/tenantDept/detail/{deptId}")
    public AjaxResult getInfo(@PathVariable String deptId)
    {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }


    /**
     * 新增组织
     */
    @ApiOperation(value = "新增组织")
    @TowerLog(title = "组织管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/system/tenantDept/add")
    public Result add(@Validated @RequestBody SaveDeptParam dept)
    {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return Result.fail("新增组织'" + dept.getDeptName() + "'失败，组织名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return Result.data(deptService.insertDept(dept));
    }

    /**
     * 修改组织
     */
    @ApiOperation(value = "修改组织")
    @TowerLog(title = "组织管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/system/tenantDept/update")
    public Result edit(@Validated @RequestBody SaveDeptParam dept)
    {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return Result.fail("修改组织'" + dept.getDeptName() + "'失败，组织名称已存在");
        }
        else if (dept.getParentId().equals(dept.getDeptId()))
        {
            return Result.fail("修改组织'" + dept.getDeptName() + "'失败，上级组织不能是自己");
        }
        if("1".equals(dept.getStatus())){
            if (deptService.hasChildByDeptId(dept.getDeptId()))
            {
                return Result.fail("存在下级组织,不允许停用");
            }
            if (deptService.checkDeptExistUser(dept.getDeptId()))
            {
                return Result.fail("组织存在用户,不允许停用");
            }
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除组织
     */
    @ApiOperation(value = "删除组织")
    @TowerLog(title = "组织管理", businessType = BusinessType.DELETE)
    @GetMapping("/system/tenantDept/del/{deptId}")
    public Result remove(@PathVariable String deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return Result.fail("存在下级组织,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return Result.fail("组织存在用户,不允许删除");
        }
        return toAjax(deptService.deleteTenantDeptById(deptId));
    }


    /**
     * 根据租户id查询租户组织管理数据,返回树结构信息,视联调用
     * @author 李峥
     * @param quTenantDeptTopRequest 请求实体
     * @return 返回树结构信息
     */
    @ApiOperation(value = "根据租户id查询租户组织管理数据,返回树结构信息")
    @PostMapping(value = "/system/tenantDept/tenantTreeList2Video")
    public Result<List<SysDeptResponse>> tenantTreeList2Video(@RequestBody QuTenantDeptTopRequest quTenantDeptTopRequest)
    {
        return Result.data(deptService.tenantTreeList2Video(quTenantDeptTopRequest));
    }

    /**
     * 查询租户组织顶级结构信息
     * @author 李峥
     * @param quTenantDeptTopRequest 组织信息
     * @return 返回平级结构信息集合
     */
    @ApiOperation(value = "获取租户组织列表(顶级结构)")
    @PostMapping(value = "/system/tenantDept/selectTenantTop2Video")
    public Result<Map<String, Object>> selectTenantTop2Video(@RequestBody QuTenantDeptTopRequest quTenantDeptTopRequest) {
        String tenantId = quTenantDeptTopRequest.getTenantId();
        if (StringUtils.isEmpty(tenantId)){
            return Result.fail("租户id不能为空");
        }
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(quTenantDeptTopRequest, dept);
        Map<String, Object> map = deptService.selectTenantTop2Video(dept);
        List<SysDept> returnDepts = (List<SysDept>)map.get(DEPT_LIST);
        List<SysDeptInfoResponse> responses = BeanUtil.copyToList(returnDepts, SysDeptInfoResponse.class);
        map.put(DEPT_LIST, responses);
        return Result.data(map);
    }


    /**
     * 获取告警类型map
     */
    @PostMapping(value = "/system/tenantDept/getAlarmTypeMap")
    public Result<Map<String, Set<String>>> getAlarmTypeMap() {
        Map<String, Set<String>> map = deptBusinessService.getAlarmTypeMap();
        return Result.data(map);
    }


    /**
     * 给历史组织的大数据告警类型增加子级告警类型
     */
    @PostMapping("/system/tenantDept/addChildDeptAlarmType")
    public void syncDeptAlarmTypeRel() {
        deptBusinessService.addChildDeptAlarmType();
    }


}
