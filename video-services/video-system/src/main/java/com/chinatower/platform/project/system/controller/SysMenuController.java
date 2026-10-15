package com.chinatower.platform.project.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.PageDomain;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.page.TableSupport;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.ObsEncrypt;
import com.chinatower.platform.common.constant.UserConstants;

import com.chinatower.platform.common.utils.PageByList;
import com.chinatower.platform.common.utils.SysTreeSelect;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.domain.SysMenu;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.SysMenuTreeVO;
import com.chinatower.platform.project.system.service.ISysMenuService;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.resp.SysRoleResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * video-system
 * 菜单信息
 *
 * @author iwhalecloud
 */
@Api(tags = "菜单信息")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends TowerBaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysUserService userService;

    /**
     * 获取菜单列表
     */
    @ApiOperation(value = "获取菜单列表")
    @GetMapping("/list")
    public TableDataInfo list(SysMenu menu)
    {
        int pageNum = menu.getPageNum();
        int pageSize = menu.getPageSize();
        menu.setPageSize(null);
        menu.setPageNum(null);
        List<SysMenu> menus = menuService.selectForList(menu);
        if(StringUtils.isNotBlank(menu.getSortRule()) &&  "asc".equals(menu.getSortRule())) {
            // 根据sysmenu的createTime字段进行升序排序
            menus.sort(Comparator.comparing(SysMenu::getCreateTime));
        }
        if(StringUtils.isNotBlank(menu.getSortRule()) &&  "desc".equals(menu.getSortRule())) {
            // 根据sysmenu的createTime字段进降序排序
            menus.sort(Comparator.comparing(SysMenu::getCreateTime).reversed());
        }
        TableDataInfo rspData =new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(PageByList.startPage(menus, pageNum, pageSize));
        rspData.setTotal(menus.size());
        return rspData;
    }

    /**
     * 获取租户的菜单列表
     */
    @ApiOperation(value = "获取租户的菜单列表")
    @GetMapping("/list/tenant")
    public TableDataInfo tenantList(SysMenu menu)
    {
        menu.setMenuClassify("1");
        return this.list(menu);
    }



    /**
     * 通用行业初始化菜单
     */
    @ApiOperation(value = "获取菜单列表")
    @GetMapping("/menuList")
    public Result menuList(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        List<SysMenu> sysMenus = menuService.buildMenuTree(menus);
        return Result.data(sysMenus);
    }



    /**
     * 根据菜单编号获取详细信息
     */
    @ApiOperation(value = "根据菜单编号获取详细信息")
    @GetMapping(value = "/detail/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @ApiOperation(value = "根据当前登录用户登录后所选行业所属角色拥有的菜单权限查询菜单下拉树结构")
    @GetMapping("/treeselectByRoleScope")
    public AjaxResult treeselectByRoleScope(SysMenu menu) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (SecurityUtils.isSwitch()) {
            SysUser user1 = userService.selectUserOnlyByUserName(user.getUserName());
            user.setUserId(user1.getUserId());
        }
        List<SysMenu> menus = menuService.treeselectByRoleScope(menu);

        List<SysMenu> compensates = menuService.compensate(user.getUserId(), user.getUserType(),
                user.getIndustryCode(), user.getAppVerCode(), Arrays.asList("0", "1"));

        if (CollUtil.isNotEmpty(compensates)) {
            menus.addAll(compensates);
        }
        if (CollUtil.isNotEmpty(menus)) {
            menus = menus.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                    () -> new TreeSet<>(Comparator.comparing(SysMenu::getMenuId))), ArrayList::new));
        }

        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 该角色已有的权限集合
     */
    @ApiOperation(value = "该角色已有的权限集合")
    @GetMapping(value = "/roleCheckMenuTreeselect/{roleId}")
    public AjaxResult roleCheckMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysMenu> menus = menuService.selectCheckMenuListByRoleId(roleId);
        if(ObjectUtil.isNotEmpty(menus)&&menus.size()>0){
            return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
        }else{
            return AjaxResult.success(new ArrayList<>());
        }

    }

    /**
     * 获取菜单下拉树列表
     */
    @ApiOperation(value = "获取菜单下拉树列表")
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu dept)
    {
        List<SysMenu> menus = menuService.selectMenuList(dept);
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation(value = "加载对应角色菜单列表树")
    @GetMapping(value = "/roleMenuTreeselect/{roleId}/{industryCode}/{appVerCode}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId,@PathVariable("industryCode")String industryCode,@PathVariable("appVerCode")String appVerCode)
    {
        return AjaxResult.success(menuService.selectMenuListByRoleId(roleId,industryCode,appVerCode));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation(value = "加载对应租户菜单列表树")
    @GetMapping(value = "/tenantMenuTreeselect/")
    public Result tenantMenuTreeselect(@RequestBody RoleDTO roleId)
    {
        return Result.data(menuService.tanentMenuTreeselect(roleId));
    }

    /**
     * 新增菜单
     */
    @ApiOperation(value = "新增菜单")
    @TowerLog(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody SysMenu menu)
    {
        /*if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return Result.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }*/
        menu.setCreator(SecurityUtils.getUsername());
        try {
            menuService.insertMenu(menu);
            return Result.success("新增成功！");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 修改菜单
     */
    @ApiOperation(value = "修改菜单")
    @TowerLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody SysMenu menu)
    {
    /*    if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return Result.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }*/
        menu.setModifier(SecurityUtils.getUsername());
        SysMenu sysMenu = menuService.selectMenuById(menu.getMenuId());
        if(menu.getStatus().equalsIgnoreCase("1") && sysMenu.getStatus().equalsIgnoreCase("0")){
            if (menuService.hasChildByMenuId(menu.getMenuId()))
            {
                return Result.fail("存在子菜单,不允许停用");
            }
            if (menuService.checkMenuExistRole(menu.getMenuId()))
            {
                return Result.fail("菜单已分配,不允许停用");
            }
        }
        try{
           menuService.updateMenu(menu);
           return Result.success("修改成功");
        } catch (RuntimeException e) {
           return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @ApiOperation(value = "删除菜单")
    @TowerLog(title = "菜单管理", businessType = BusinessType.DELETE)
    @GetMapping("/del/{menuId}")
    public Result remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return Result.fail("存在子菜单，不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return Result.fail("菜单已分配，不允许删除");
        }
        int deleteCount = menuService.deleteMenuById(menuId);
        if(deleteCount > 0) {
            return Result.success("删除成功");
        } else {
            return Result.success("删除失败");
        }
    }

    /**
     * 隐藏菜单
     */
    @ApiOperation(value = "隐藏菜单")
    @TowerLog(title = "隐藏菜单", businessType = BusinessType.UPDATE)
    @PostMapping("/disable/{menuId}")
    public Result disable(@PathVariable("menuId") Long menuId,@RequestParam("status") Integer status)
    {
        menuService.disable(menuId,status);
        return Result.success("停用成功");
    }

    /**
     * 停用菜单
     */
    @ApiOperation(value = "停用菜单")
    @TowerLog(title = "停用菜单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus/{menuId}")
    public Result changeStatus(@PathVariable("menuId") Long menuId,@RequestParam("status") Integer status)
    {
        SysMenu sysMenu = menuService.selectMenuById(menuId);
        if(String.valueOf(status).equalsIgnoreCase("1") && sysMenu.getStatus().equalsIgnoreCase("0")){
            if (menuService.hasChildByMenuId(menuId))
            {
                return Result.fail("存在子菜单,不允许停用");
            }
            if (menuService.checkMenuExistRole(menuId))
            {
                return Result.fail("菜单已分配,不允许停用");
            }
        }
        menuService.disable(menuId,status);
        return Result.success("停用成功");
    }


    /**
     * 加载角色id列表对应的角色菜单树
     */
    @PostMapping("/treeselectByUserRole")
    public AjaxResult treeselectByUserRole(@RequestBody SysMenu menu) {
        List<SysMenuTreeVO> menus = menuService.selectMenuByUserRole(menu);
        List<SysTreeSelect> tree = new ArrayList<>();
        if(!CollectionUtils.isEmpty(menus)){
            List<SysMenu> menuList = new ArrayList<>();
            for (int i = 0; i < menus.size(); i++) {
                SysMenu sysMenu = new SysMenu();
                sysMenu.setMenuId(Long.valueOf(menus.get(i).getId()));
                sysMenu.setParentId(Long.valueOf(menus.get(i).getPid()));
                sysMenu.setMenuName(menus.get(i).getLabel());
                sysMenu.setMenuApply(menus.get(i).getApplyType());
                sysMenu.setOrderNum(menus.get(i).getOrderNum());
                menuList.add(sysMenu);
            }
            tree = menuService.buildMenuTreeSelect(menuList);
        }
//        JSONArray treeAry = TreeUtils.listToTree(menus, "id", "pid");
        return AjaxResult.success(tree);
    }


}
