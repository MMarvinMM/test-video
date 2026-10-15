package com.chinatower.platform.project.system.mapper;

import java.util.List;

import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.dto.QueryThirdMenuDTO;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.dto.SysThirdMenuDTO;
import com.chinatower.platform.project.system.domain.vo.MenuVo;
import com.chinatower.platform.project.system.domain.vo.SysMenuTreeVO;
import org.apache.ibatis.annotations.Param;
import com.chinatower.platform.project.system.domain.SysMenu;

/**
 * 菜单表 数据层
 *
 * @author iwhalecloud
 */
public interface SysMenuMapper
{
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    public List<String> selectMenuPerms();

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> selectMenuPermsByUserId(SysUser user);

    /**
     * 根据用户、行业、版本查询权限，包含父级权限
     *
     * @param user 用户
     * @return 权限列表
     */
    List<String> selectAllMenuPermsByUser(SysUser user);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(@Param("userId") String userId,@Param("userType")String userType,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode,List<String> list);

    /**
     *
     * @param menuType
     * @param userId
     * @param userType
     * @param industryCode
     * @param appVerCode
     * @param list
     * @return
     */
    public List<SysMenu> selectParentMenuByMenuType(@Param("parentIds") List<Long> parentIds,@Param("menuType")String menuType,@Param("userId") String userId,@Param("userType")String userType,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode,List<String> list);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<String> selectMenuListByRoleId(@Param("roleId") Long roleId,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(@Param("menuId") Long menuId,@Param("configValue") String configValue);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int hasChildByMenuId(Long menuId);

    /**
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 批量更新菜单可见性
     * @param menu
     * @return
     */
    public int batchUpdateMenuVisible(@Param("list") List<Long> menu,@Param("status") Integer status);

    /**
     * 批量更新菜单状态
     * @param menu
     * @param status
     * @return
     */
    public int batchUpdateMenuStatus(@Param("list") List<Long> menu,@Param("status") Integer status);

    /**
     * 查询当前菜单及下级菜单
     * @param menuId
     * @return
     */
    public List<SysMenu> selectChilds(@Param("menuId") Long menuId);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(@Param("list") List<Long> menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    public SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    List<SysMenu> treeselectByRoleScope(@Param("user") SysUser user);

    List<SysMenu> selectCheckMenuListByRoleId(Long roleId);

    List<SysMenu> selectMenuTreeAllByApp(@Param("list") List<String> list);

    List<SysMenu> selectMenuTreeAllByAdmin(@Param("list") List<String> list);

    List<SysMenu> selectByParentId(@Param("parentId") Long parentId);

    public List<MenuVo> tanentMenuTreeselect(RoleDTO roleDTO);

    List<Long> selectAllMenuId(@Param("menuId") Long menuId);

    void updateMenuByParentId(SysMenu menuParam);

    List<SysMenu> selectAllMenu();

    /**
     * 查询菜单分类表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuClassifyInfo(SysMenu menu);



    /**
     * 根据角色ID列表查询菜单树信息
     *
     * @param menu 角色ID列表
     * @return 选中菜单列表
     */
    public List<SysMenuTreeVO> selectMenuByUserRole(SysMenu menu);

    List<SysThirdMenuDTO> findThirdMenuByTenantIndustryAppVerCode(QueryThirdMenuDTO queryThirdMenuDTO);

    SysThirdMenuDTO findById(String menuId);

    /**
     * 根据用户ID查询菜单（不区分显隐状态）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserIdNoVisible(@Param("userId") String userId,@Param("userType")String userType,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode,List<String> list);

    /**
     * 查询所有菜单（不区分显隐状态）- APP端
     *
     * @param list 菜单适用范围
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAllByAppNoVisible(@Param("list") List<String> list);

    /**
     * 查询所有菜单（不区分显隐状态）- 管理端
     *
     * @param list 菜单适用范围
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAllByAdminNoVisible(@Param("list") List<String> list);

    /**
     * 批量查询菜单信息
     *
     * @param menuIds 菜单ID列表
     * @return 菜单列表
     */
    public List<SysMenu> selectMenusByIds(@Param("menuIds") List<Long> menuIds);
}
