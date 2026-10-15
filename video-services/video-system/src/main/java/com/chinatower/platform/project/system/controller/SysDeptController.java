package com.chinatower.platform.project.system.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.system.domain.vo.SaveDeptParam;
import com.chinatower.platform.project.system.domain.vo.SysDeptDetailVO;

import com.chinatower.platform.project.system.service.ISysDeptService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.SysDeptAreaVO;
import com.chinatower.system.api.model.SysDeptDeviceTreeVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import com.chinatower.system.api.request.tenant.CreateTenantRootDeptRequest;
import com.chinatower.system.api.resp.SysDeptResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * video-system
 * 组织信息
 *
 * @author iwhalecloud
 */
@Api(tags = "组织信息")
@RestController
public class SysDeptController extends TowerBaseController {
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取组织列表(树形)
     */
    @ApiOperation(value = "获取组织列表(树形)")
    @GetMapping("/system/dept/list")
    public AjaxResult list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.selectDeptTree(depts));
    }

    /**
     * 查询已选定设备
     */
    @GetMapping("/system/dept/deptDeviceDetail/{deptId}")
    public Result<SysDeptDetailVO> getDeptDeviceDetail(@PathVariable("deptId") String deptId) {
        if (StringUtils.isBlank(deptId)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        return Result.data(deptService.getDeptDevices(deptId,null));
    }

    /**
     * 批量查询组织设备权限
     */
    @PostMapping("/system/dept/deptDeviceDetailBatch")
    public Result<SysDeptDetailVO> deptDeviceDetailBatch(@RequestBody List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        return Result.data(deptService.deptDeviceDetailBatch(list));
    }

    /**
     * 获取管理区域列表(树形) 提供给app
     */
    @GetMapping("/system/dept/getDeptStreetTree/{deptId}")
    public Result<List<SysDeptDeviceTreeVO>> getDeptStreetTree(@PathVariable("deptId") String deptId) {
        if (StringUtils.isBlank(deptId)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR);
        }
        return Result.data(deptService.getDeptStreetTree(deptId));
    }

    /**
     * 查询设备范围
     */
    @GetMapping("/system/dept/deptDeviceDetailScop")
    public Result<SysDeptDetailVO> getDeptDeviceDetailScop() {
        return Result.data(deptService.getDeptDeviceScop());
    }

    /**
     * 获取组织列表(集合)
     */
    @ApiOperation(value = "获取组织列表(集合)")
    @GetMapping("/system/dept/arylist")
    public AjaxResult arylist(SysDept dept) {
        return AjaxResult.success(deptService.selectDeptList(dept));
    }

    /**
     * 根据组织编号获取详细信息
     */
    @ApiOperation(value = "根据组织编号获取详细信息")
    @GetMapping(value = "/system/dept/detail/{deptId}")
    public AjaxResult getInfo(@PathVariable String deptId) {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    /**
     * 获取组织下拉树列表
     */
    @ApiOperation(value = "获取组织下拉树列表")
    @GetMapping("/base/system/dept/treeselect")
    public AjaxResult treeselect() {
        try {
            HttpServletRequest request = getRequest();
            logger.error("================>>>>>>>>>>>>>>>> cookie-test-request: {}", request.toString());
            Cookie[] cookies = request.getCookies();
            if(ArrayUtil.isEmpty(cookies)){
                logger.error("==============================>>>>>>>>>>>>> 没有获取到cookie <<<<<<<<<<<<==============================");
            }
            for (Cookie cookie : cookies) {
                logger.error(StrUtil.format("==============================>>>>>>>>>>>>> 获取到cookie：{}，值为：{}", cookie.getName(), cookie.getValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("==============================>>>>>>>>>>>>> 获取cookie异常 <<<<<<<<<<<<==============================");
        }
        logger.error("==============================>>>>>>>>>>>>> 查看cookie日志结束 <<<<<<<<<<<<==============================");
        return AjaxResult.success(deptService.selectTreeSelect());
    }


    /**
     * 加载对应角色组织列表树
     */
    @ApiOperation(value = "加载对应角色组织列表树")
    @GetMapping(value = "/system/dept/roleDeptTreeselect/{roleId}")
    public AjaxResult roleDeptTreeselect(@PathVariable("roleId") String roleId) {
        return AjaxResult.success(deptService.selectDeptListByRoleId(roleId));
    }

    /**
     * 新增组织
     */
    @ApiOperation(value = "新增组织")
    @TowerLog(title = "组织管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/system/dept/add")
    public Result add(@Validated @RequestBody SaveDeptParam dept) {
        if (StringUtils.isBlank(dept.getDeptName())) {
            return Result.fail("组织名称不允许为空");
        }
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return Result.fail("新增组织'" + dept.getDeptName() + "'失败，组织名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return Result.data(deptService.insertDept(dept));
    }

    /**
     * 新增租户组织树根节点
     *
     * @param createTenantRootDeptRequest 创建租户组织树根节点入参
     * @return 结果
     */
    @ApiOperation(value = "新增租户组织树根节点")
    @TowerLog(title = "组织管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/system/dept/addTenantRootDept")
    public Result addTenantRootDept(@Validated @RequestBody CreateTenantRootDeptRequest createTenantRootDeptRequest) {
        return toAjax(deptService.insertTenantRootDept(createTenantRootDeptRequest));
    }

    /**
     * 修改组织信息
     */
    @ApiOperation(value = "修改组织")
    @TowerLog(title = "组织管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/system/dept/update")
    public Result edit(@Validated @RequestBody SaveDeptParam dept) {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))) {
            return Result.fail("修改组织'" + dept.getDeptName() + "'失败，组织名称已存在");
        } else if (dept.getParentId().equals(dept.getDeptId())) {
            return Result.fail("修改组织'" + dept.getDeptName() + "'失败，上级组织不能是自己");
        }

        if ("1".equals(dept.getStatus())) {
            if (deptService.hasChildByDeptId(dept.getDeptId())) {
                return Result.fail("存在下级组织,不允许停用");
            }
            if (deptService.checkDeptExistUser(dept.getDeptId())) {
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
    @GetMapping("/system/dept/del/{deptId}")
    public Result remove(@PathVariable String deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return Result.fail("存在下级组织,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return Result.fail("组织存在用户,不允许删除");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }

    /**
     * 获取组织列表(树形),定时任务调用
     *
     * @author 李峥
     * @date 2021-12-18
     */
    @ApiOperation(value = "获取组织列表(树形)")
    @PostMapping("/system/dept/treeList")
    public Result<List<SysDeptResponse>> treeList(SysDept dept) {
        return Result.data(deptService.treeList(dept));
    }

    /**
     * 查询组织管理数据,返回平级结构信息,定时任务调用
     *
     * @param dept 组织信息
     * @return 返回平级结构集合
     */
    @ApiOperation(value = "获取组织列表(集合)")
    @PostMapping("/system/dept/nonTreeList")
    public Result<List<SysDeptResponse>> nonTreeList(SysDept dept) {
        return Result.data(deptService.nonTreeList(dept));
    }

    /**
     * 获取组织列表(树形)
     *
     * @param dept
     * @return
     */
    @ApiOperation(value = "获取组织列表(树形)")
    @PostMapping("/system/dept/getDeptTree")
    public Result<List<SysDeptResponse>> getDeptTree(@RequestBody SysDept dept) {
        return Result.data(deptService.treeList(dept));
    }

    /**
     * 获取管理区域(树形)
     */
    @ApiOperation(value = "获取管理区域(树形)")
    @GetMapping("/system/dept/getUserDeptTreeselect")
    public Result<List<SysDeptAreaVO>> getUserDeptTreeselect(){
        return Result.data(deptService.getDeptAndAreaTree());
    }

    /**
     * 获取摄像机设备行政区域树
     */
    @ApiOperation(value = "获取摄像机设备行政区域树")
    @PostMapping("/system/dept/getCameraTree")
    public Result<List<SysDeptDeviceTreeVO>> getCameraTree() {
        return Result.data(deptService.getCameraTree());
    }

    /**
     * 获取雷达设备行政区域树
     */
    @ApiOperation(value = "获取雷达设备行政区域树")
    @PostMapping("/system/dept/getRadarTree")
    public Result<List<SysDeptDeviceTreeVO>> getRadarTree() {
        return Result.data(deptService.getRadarTree());
    }

    /**
     * 获取传感器(物联)设备行政区域树
     */
    @ApiOperation(value = "获取传感器(物联)设备行政区域树")
    @PostMapping("/system/dept/getSensorTree")
    public Result<List<SysDeptDeviceTreeVO>> getSensorTree() {
        return Result.data(deptService.getSensorTree());
    }

    /**
     * 获取卫星火点设备行政区域树
     */
    @ApiOperation(value = "获取卫星火点设备行政区域树")
    @PostMapping("/system/dept/getSatellitesTree")
    public Result<List<SysDeptDeviceTreeVO>> getSatellitesTree() {
        return Result.data(deptService.getSatellitesTree());
    }

    /**
     * 获取无人机设备行政区域树
     */
    @ApiOperation(value = "获取无人机设备行政区域树")
    @PostMapping("/system/dept/getUavTree")
    public Result<List<SysDeptDeviceTreeVO>> getUavTree() {
        return Result.data(deptService.getUavTree());
    }

    /**
     * 获取喇叭设备行政区域树
     */
    @ApiOperation(value = "获取喇叭设备行政区域树")
    @PostMapping("/system/dept/getHornTree")
    public Result<List<SysDeptDeviceTreeVO>> getHornTree() {
        return Result.data(deptService.getHornTree());
    }

    /**
     * 获取执法记录仪设备行政区域树
     */
    @ApiOperation(value = "获取执法记录仪设备行政区域树")
    @PostMapping("/system/dept/getLERTree")
    public Result<List<SysDeptDeviceTreeVO>> getLERTree() {
        return Result.data(deptService.getLERTree());
    }

    /**
     * 获取摄像机设备行政区域树(PC)
     */
    @ApiOperation(value = "获取摄像机设备行政区域树(PC)")
    @PostMapping("/system/dept/getCameraTreeForPC")
    public Result<List<SysDeptDeviceVO>> getCameraTreeForPC() {
        return Result.data(deptService.getCameraTreeForPC());
    }

    /**
     * 获取雷达设备行政区域树(PC)
     */
    @ApiOperation(value = "获取雷达设备行政区域树(PC)")
    @PostMapping("/system/dept/getRadarTreeForPC")
    public Result<List<SysDeptDeviceVO>> getRadarTreeForPC() {
        return Result.data(deptService.getRadarTreeForPC());
    }

    /**
     * 获取传感器(物联)设备行政区域树(PC)
     */
    @ApiOperation(value = "获取传感器(物联)设备行政区域树(PC)")
    @PostMapping("/system/dept/getSensorTreeForPC")
    public Result<List<SysDeptDeviceVO>> getSensorTreeForPC() {
        return Result.data(deptService.getSensorTreeForPC());
    }

    /**
     * 获取卫星火点设备行政区域树(PC)
     */
    @ApiOperation(value = "获取卫星火点设备行政区域树(PC)")
    @PostMapping("/system/dept/getSatellitesTreeForPC")
    public Result<List<SysDeptDeviceVO>> getSatellitesTreeForPC() {
        return Result.data(deptService.getSatellitesTreeForPC());
    }

    /**
     * 获取无人机设备行政区域树(PC)
     */
    @ApiOperation(value = "获取无人机设备行政区域树(PC)")
    @PostMapping("/system/dept/getUavTreeForPC")
    public Result<List<SysDeptDeviceVO>> getUavTreeForPC() {
        return Result.data(deptService.getUavTreeForPC());
    }


    /**
     * 获取无人机机场行政区域树(PC)
     */
    @ApiOperation(value = "获取无人机机场行政区域树(PC)")
    @PostMapping("/system/dept/getUavNestTreeForPC")
    public Result<List<SysDeptDeviceVO>> getUavNestTreeForPC() {
        return Result.data(deptService.getUavNestTreeForPC());
    }

    /**
     * 获取喇叭设备行政区域树(PC)
     */
    @ApiOperation(value = "获取喇叭设备行政区域树(PC)")
    @PostMapping("/system/dept/getHornTreeForPC")
    public Result<List<SysDeptDeviceVO>> getHornTreeForPC() {
        return Result.data(deptService.getHornTreeForPC());
    }

    /**
     * 获取执法记录仪设备行政区域树(PC)
     */
    @ApiOperation(value = "获取执法记录仪设备行政区域树(PC)")
    @PostMapping("/system/dept/getLERTreeForPC")
    public Result<List<SysDeptDeviceVO>> getLERTreeForPC() {
        return Result.data(deptService.getLERTreeForPC());
    }

    /**
     * 获取摄像机设备行政区域树(树形结构 只返回省市区街道)
     */
    @ApiOperation(value = "获取摄像机设备行政区域树(树形结构 只返回省市区街道)")
    @PostMapping("/system/dept/getCameraTreeNew")
    public Result<List<SysDeptDeviceTreeVO>> getCameraTreeNew() {
        return Result.data(deptService.getCameraTreeNew());
    }


    /**
     * 批量处理历史告警类型关系
     */
    @PostMapping("/system/dept/dealHisDeptAlarmTypeRel")
    public void syncDeptAlarmTypeRel(@RequestBody OrganAlarmTypeVO organAlarmTypeVO) {
         deptService.dealHisDeptAlarmTypeRel(organAlarmTypeVO);
    }


}
