package com.chinatower.platform.project.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.manage.api.IIndustryService;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.SaveRoleParam;
import com.chinatower.platform.project.system.domain.vo.SysRoleQuery;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;

import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.project.system.service.ISysRoleService;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import com.chinatower.system.api.model.SysRoleScreenVO;
import com.chinatower.system.api.request.role.QueryRoleRequest;
import com.chinatower.system.api.resp.SysRoleDeptResponse;
import com.chinatower.system.api.resp.SysRoleResponse;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * video-system
 * 角色信息
 *
 * @author iwhalecloud
 */
@Api(tags = "角色信息")
@RestController
@Slf4j
@RequestMapping("/system/role")
public class SysRoleController extends TowerBaseController
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private IIndustryService iIndustryService;


    //排序列字段入参合法性
    public static void getAllFields(Class<?> clazz,QueryRoleRequest queryRoleRequest) {
        List<Field> fieldList = Lists.newArrayList();
        while (clazz != null){
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        List<String> stringList = new ArrayList<>();
        for (Field Field : fieldList) {
            stringList.add(Field.getName());
        }

        String orderByColumn = queryRoleRequest.getOrderByColumn();
        if(StringUtils.isNotEmpty(orderByColumn)){
            if(!stringList.contains(orderByColumn)){
                queryRoleRequest.setOrderByColumn(null);
            }
        }
    }

    /**
     * 角色信息列表
     */
    @ApiOperation(value = "角色信息列表")
    @PostMapping("/list")
    @TowerLog(title = "角色列表",businessType = BusinessType.SELECT)
    public Result<Map<String, Object>> list(@RequestBody QueryRoleRequest queryRoleRequest) {
        getAllFields(SysRole.class,queryRoleRequest);

        PageHelper.startPage(queryRoleRequest.getPageNum(), queryRoleRequest.getPageSize(),queryRoleRequest.getDefaultOrderBy());

        SysRoleQuery role = new SysRoleQuery();
        BeanUtils.copyProperties(queryRoleRequest, role);
        List<SysRole> list = roleService.selectRoleList(role);
        return result(list);
    }

    /**
     * 铁塔角色信息列表
     */
    @ApiOperation(value = "铁塔角色信息列表")
    @PostMapping("/list/tower")
//    @TowerLog(title = "角色列表",businessType = BusinessType.SELECT)
    public Result<Map<String, Object>> listTower(@RequestBody QueryRoleRequest queryRoleRequest) {

        getAllFields(SysRole.class,queryRoleRequest);

        PageHelper.startPage(queryRoleRequest.getPageNum(), queryRoleRequest.getPageSize(),queryRoleRequest.getDefaultOrderBy());
        SysRoleQuery role = new SysRoleQuery();
        BeanUtils.copyProperties(queryRoleRequest, role);
        List<SysRole> list = roleService.selectRoleListTower(role);
        return result(list);
    }

    /**
     * 角色信息列表
     */
    @ApiOperation(value = "角色信息列表")
    @PostMapping("/list/tenant")
    public TableDataInfo listTenant(@RequestBody QueryRoleRequest queryRoleRequest) {

        getAllFields(SysRole.class,queryRoleRequest);

        PageHelper.startPage(queryRoleRequest.getPageNum(), queryRoleRequest.getPageSize(), queryRoleRequest.getDefaultOrderBy());
        List<SysRole> list = roleService.selectRoleListTenant(queryRoleRequest);
        return getDataTable(list);
    }

    /**
     * 查询角色列表
     */
    @ApiOperation(value = "角色信息列表")
    @PostMapping("/selectRoleList")
    public Result<List<SysRoleResponse>> selectRoleList(@RequestBody QueryRoleRequest queryRoleRequest)
    {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(queryRoleRequest, role);
        List<SysRole> list = roleService.selectRolePublicList(role);
        List<SysRoleResponse> sysRoleResponses = BeanUtil.copyToList(list, SysRoleResponse.class);
        return Result.data(sysRoleResponses);
    }

    /**
     * 角色信息导出
     */
    @ApiOperation(value = "角色信息导出")
    @TowerLog(title = "角色管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody  SysRoleQuery role, HttpServletResponse response) {
        List<SysRole> list = roleService.selectRoleList(role);
        byte[] data =new ExcelUtil<>(SysRole.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @ApiOperation(value = "根据角色编号获取详细信息")
    @PostMapping(value = "/detail")
    public AjaxResult getInfo(@RequestBody RoleDTO roleId)
    {
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @ApiOperation(value = "新增角色")
    @TowerLog(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody SaveRoleParam role)
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        role.setIndustryCode(currentUser.getIndustryCode());
        role.setAppVerCode(currentUser.getAppVerCode());
        role.setTenantId(currentUser.getTenantId());
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return Result.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
       /* else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
        {
            return Result.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }*/
        role.setCreateBy(SecurityUtils.getUsername());
        role.setRoleType(SecurityUtils.getLoginUser().getUser().getUserType());

        roleService.insertRole(role);
        //add by liuxt 20231108 保存角色大屏关系
        if(!CollectionUtils.isEmpty(role.getSysRoleScreenVOList())){
            role.getSysRoleScreenVOList().stream().forEach(item ->{
                item.setRoleId(role.getRoleId());
                item.setIndustryCode(role.getIndustryCode());
                item.setAppVerCode(role.getAppVerCode());
                item.setTenantId(role.getTenantId());
            });
            roleService.saveRoleScreenRef(role.getSysRoleScreenVOList(),role.getRoleId(),Constants.ONE);
        }
        return Result.success("新增成功");

    }

    /**
     * 修改保存角色
     */
    @ApiOperation(value = "修改保存角色")
    @TowerLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody SaveRoleParam role)
    {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return Result.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
//        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
//        {
//            return Result.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
//        }
        //add by liuxt 20231108 保存角色大屏关系
        if(!CollectionUtils.isEmpty(role.getSysRoleScreenVOList())){
            role.getSysRoleScreenVOList().stream().forEach(item ->{
                item.setRoleId(role.getRoleId());
                item.setIndustryCode(role.getIndustryCode());
                item.setAppVerCode(role.getAppVerCode());
                item.setTenantId(role.getTenantId());
            });
            roleService.saveRoleScreenRef(role.getSysRoleScreenVOList(),role.getRoleId(),Constants.TWO);
        }
        role.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        role.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @ApiOperation(value = "修改保存数据权限")
    @TowerLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/dataScope")
    public Result dataScope(@RequestBody SaveRoleParam role)
    {
        roleService.checkRoleAllowed(role);
        roleService.authDataScope(role);
        return Result.success("修改成功");
    }

    /**
     * 更新角色菜单按钮权限和数据查询权限
     */
    @ApiOperation(value = "更新角色菜单按钮权限和数据查询权限")
    @TowerLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/permission")
    public Result permission(@RequestBody SaveRoleParam role) {
        roleService.checkRoleAllowed(role);
        roleService.updateRolePermission(role);
        return Result.success("更新成功");
    }

    /**
     * 状态修改
     */
    @ApiOperation(value = "状态修改")
    @TowerLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);

        if("1".equals(role.getStatus())){
            try {
                this.checkFlow(Arrays.asList(role.getRoleId()));
            }catch (Exception e){
                return Result.fail(e.getMessage());
            }

        }

        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @ApiOperation(value = "删除角色")
    @TowerLog(title = "角色管理", businessType = BusinessType.DELETE)
    @GetMapping("/del/{roleIds}")
    @Transactional(rollbackFor = Exception.class)
    public Result remove(@PathVariable Long[] roleIds)
    {
        try {
            this.checkFlow(Arrays.asList(roleIds));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

        try {
            //add by liuxt 20231108 逻辑删除角色大屏关系
            List<Long> roleIdList = Arrays.asList(roleIds);
            List<SysRoleScreenVO> sysRoleScreenVOList = new ArrayList<>();
            for (int i = 0; i < roleIdList.size(); i++) {
                SysRoleScreenVO sysRoleScreenVO = new SysRoleScreenVO();
                sysRoleScreenVO.setRoleId(roleIdList.get(i));
                sysRoleScreenVOList.add(sysRoleScreenVO);
            }
            roleService.logicDeleteRoleScreenRef(sysRoleScreenVOList);
            return toAjax(roleService.deleteRoleByIds(roleIds));
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("删除角色失败，{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取角色选择框列表
     */
    @ApiOperation(value = "获取角色选择框列表")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return AjaxResult.success(roleService.selectRoleAll());
    }

    /**
     * 查询某种类型角色对应的部门,刷新数据到redis定时任务调用
     * @author 李峥
     * @return List<SysRoleDeptResponse>
     */
    @ApiOperation(value = "查询某种类型角色对应的部门")
    @TowerLog(title = "查询某种类型角色对应的部门", businessType = BusinessType.OTHER)
    @GetMapping("/selectAllRoleDept")
    public Result<List<SysRoleDeptResponse>> selectAllRoleDept(String roleType)
    {
        return Result.data(roleService.selectAllRoleDept(roleType));
    }

    /**
     * 根据租户ID获取角色信息
     */
    @ApiOperation(value = "根据租户ID获取角色信息")
    @GetMapping(value = { "getRoleByTenant", "getRoleByTenant/{tenantId}" })
    public Result<List<SysRole>> getRoleByTenant(@PathVariable(value = "tenantId", required = false) String tenantId) {
        // tenantId不为空时查询该租户下的角色和审批流角色，为空时查询铁塔的角色和审批流角色
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(!user.getUserType().equalsIgnoreCase("0")){
            tenantId = user.getTenantId();
        }
        return Result.data(roleService.selectRoleByTenantId(tenantId));
    }

    private void checkFlow(List<Long> roleIds){

//        boolean result = false;
//        //TODO 需要流程那边提供接口校验该角色下是否在流程中
//
//        if(result){
//            throw new RuntimeException("当前角色已在xxx流程中，请先修改流程后再进行操作");
//        }
    }


    /**
     * 仅依据租户id筛选角色
     * @param queryRoleRequest
     * @return
     */
    @ApiOperation(value = "角色信息列表-告警流程配置")
    @PostMapping("/flowRoleList")
    public Result flowRoleList(@RequestBody QueryRoleRequest queryRoleRequest) {
        SysRoleQuery role = new SysRoleQuery();
        BeanUtils.copyProperties(queryRoleRequest, role);
        List<SysRole> list = roleService.flowRoleList(role);
        return Result.data(list);
    }



    /**
     * 查询角色大屏关系
     * @param sysRoleScreenVO
     * @return
     */
    @ApiOperation(value = "查询角色大屏关系")
    @PostMapping("/queryRoleScreenRef")
    public Result<List<SysRoleScreenVO>> queryRoleScreenRef(@RequestBody SysRoleScreenVO sysRoleScreenVO)
    {
        List<SysRoleScreenVO> list = roleService.getRoleScreenRef(sysRoleScreenVO);
        return Result.data(list);
    }



    /**
     * 删除角色大屏关系(暴露给浩鲸调用)
     */
    @ApiOperation(value = "删除角色大屏关系")
    @PostMapping("/delRoleScreenRef")
    public Result<Integer> delRoleScreenRef(@RequestBody List<SysRoleScreenVO> sysRoleScreenVOList)
    {
        log.info("删除角色大屏关系请求报文:"+ JSONObject.toJSONString(sysRoleScreenVOList));
        try {
            roleService.logicDeleteRoleScreenRef(sysRoleScreenVOList);
            return Result.success("删除成功");
        } catch (Exception e) {
            logger.error("删除角色大屏关系，{}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }


    /**
     * 获取当前用户大屏列表
     * @param sysRoleScreenVO
     * @return
     */
    @ApiOperation(value = "getCurrentRoleScreen")
    @PostMapping("/getCurrentRoleScreen")
    public Result<SysRoleScreenVO> getCurrentRoleScreen(@RequestBody SysRoleScreenVO sysRoleScreenVO)
    {
        log.info("获取当前用户大屏列表接口入参:"+JSONObject.toJSONString(sysRoleScreenVO));
        SysRoleScreenVO returnVO = roleService.getCurrentRoleScreen(sysRoleScreenVO);
        return Result.data(returnVO);
    }


    /**
     * 获取全部大屏配置信息【包含行业大屏和第三方大屏】铁塔客户共用此接口
     * @param sysRoleScreenVO
     * @return
     */
    @ApiOperation(value = "queryIndustryConfigInfoNew")
    @PostMapping("/queryIndustryConfigInfoNew")
    public Result<List<SysIndustryConfigInfoModel>> queryIndustryConfigInfoNew(@RequestBody SysRoleScreenVO sysRoleScreenVO) {
        List<SysIndustryConfigInfoModel> sysIndustryConfigInfoModelDataList = new ArrayList<>();
        SysIndustryConfigInfoModel param = new SysIndustryConfigInfoModel();
        param.setIndustryCode(sysRoleScreenVO.getIndustryCode());
        param.setAppVerCode(sysRoleScreenVO.getAppVerCode());
        param.setTenantId(sysRoleScreenVO.getTenantId());
        log.info("获取全部大屏配置信息接口入参:" + JSONObject.toJSONString(param));
        Result<List<SysIndustryConfigInfoModel>> list = iIndustryService.queryIndustryConfigInfo(param);
        if (list != null && !CollectionUtils.isEmpty(list.getData())) {
            sysIndustryConfigInfoModelDataList = list.getData();
            log.info("queryIndustryConfigInfo 接口, param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(sysIndustryConfigInfoModelDataList));
        }
        return Result.data(sysIndustryConfigInfoModelDataList);
    }



}
