package com.chinatower.platform.project.system.service;

import java.util.List;
import java.util.Set;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.utils.SysTreeSelect;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.SysMenu;
import com.chinatower.platform.project.system.domain.dto.QueryThirdMenuDTO;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.MenuVo;
import com.chinatower.platform.project.system.domain.vo.SysMenuTreeVO;

/**
 * 菜单 业务层
 *
 * @author iwhalecloud
 */
public interface ISysMenuService
{
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(SysUser user);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(String userId,String tradeId,List<String> list);

    /**
     * 菜单补全
     * @param userId 用户id
     * @param userType 用户类型
     * @param industryCode 行业编码
     * @param appVerCode 版本
     * @param list 菜单适用范围集合
     * @return
     */
    List<SysMenu> compensate(String userId,String userType, String industryCode,String appVerCode, List<String> list);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<String> selectMenuListByRoleId(Long roleId,String industryCode,String appVerCode);

    /**
     * 根据角色，租户查询
     * @param roleDTO
     * @return
     */
    public List<MenuVo> tanentMenuTreeselect(RoleDTO roleDTO);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<MenuVo> buildMenus(List<SysMenu> menus, boolean ifRoot);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<MenuVo> buildMenusNoVisible(List<SysMenu> menus, boolean ifRoot);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<SysTreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public String checkMenuNameUnique(SysMenu menu);

    List<SysMenu> treeselectByRoleScope(SysMenu menu);

    List<SysMenu> selectCheckMenuListByRoleId(Long roleId);

    void disable(Long menuId,Integer status);

    void changeStatus(Long menuId,Integer status);

    List<SysMenu> selectForList(SysMenu menu);

    /**
     * 查询菜单分类表
     *
     */
    public List<SysMenu> selectMenuClassifyInfo(SysMenu menu);


    /**
     * 根据角色ID列表查询菜单树信息
     *
     * @param menu
     * @return 选中菜单列表
     */
    public List<SysMenuTreeVO> selectMenuByUserRole(SysMenu menu);

    List<MenuVo> findThirdMenuByTenantIndustryAppVerCode(QueryThirdMenuDTO queryThirdMenuDTO);

    Result<String> getThirdPartyUrlWithAuthorization(QueryThirdMenuDTO model);

    /**
     * 根据用户ID查询菜单树信息（不区分显隐状态）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserIdNoVisible(String userId,String tradeId,List<String> list);

    /**
     * 为菜单列表填充父节点名称
     *
     * @param menus 菜单列表
     */
    public void fillParentNames(List<SysMenu> menus);
}
