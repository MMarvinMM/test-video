package com.chinatower.platform.project.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.common.utils.spring.SpringUtils;
import com.chinatower.platform.constants.CacheConstants;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.framework.manager.AsyncManager;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.project.system.domain.SysRoleDataRel;
import com.chinatower.platform.project.system.domain.SysRoleIndustryAuth;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.SaveRoleParam;
import com.chinatower.platform.project.system.domain.vo.SysRoleQuery;
import com.chinatower.platform.project.system.mapper.*;
import com.chinatower.platform.project.system.service.ISysTenantInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import com.chinatower.system.api.model.SysRoleScreenVO;
import com.chinatower.system.api.request.role.QueryRoleRequest;
import com.chinatower.system.api.resp.SysRoleDeptResponse;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.project.system.domain.SysRoleDept;
//import com.chinatower.platform.project.system.domain.SysRoleMenu;
import com.chinatower.platform.project.system.mapper.SysRoleDeptMapper;
import com.chinatower.platform.project.system.mapper.SysRoleMapper;
//import com.chinatower.platform.project.system.mapper.SysRoleMenuMapper;
import com.chinatower.platform.project.system.mapper.SysUserRoleMapper;
import com.chinatower.platform.project.system.service.ISysRoleService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * 角色 业务层处理
 *
 * @author chinatower
 */
@Slf4j
@Service
public class SysRoleServiceImpl implements ISysRoleService {
    @Autowired
    private SysRoleMapper roleMapper;

    /*@Autowired
    private SysRoleMenuMapper roleMenuMapper;*/

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleIndustryAuthMapper roleIndustryAuthMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    @Autowired
    private ISysTenantInfoService sysTenantInfoService;

    @Autowired
    private SysRoleDataRelMapper roleDataRelMapper;

    @Autowired
    private ScreenMenuConfMapper screenMenuConfMapper;


    private static String ROLE_KEY_NUM = "roleKeyNum";
    private static String TOWER_ROLE= "tower_role_";
    private static String PROCESS_ROLE= "process_role_";
    private static String TRADE_ROLE= "trade_role_";
    private static String TENANT_ROLE= "tenant_role_";

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SysRole> selectRoleList(SysRoleQuery role) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        return roleMapper.selectRoleList(role,user);
    }

    @Override
    public List<SysRole> selectRoleListTower(SysRoleQuery role) {
        return roleMapper.selectRoleListTower(role);
    }

    @Override
    public List<SysRole> selectRoleListTenant(QueryRoleRequest role) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        role.setTenantId(user.getTenantId());
        role.setIndustryCode(user.getIndustryCode());
        role.setAppVerCode(user.getAppVerCode());
        return roleMapper.selectRoleListTenant(role);
    }

    /**
     * 仅依据租户id筛选角色（告警流程配置使用）
     * 原接口铁塔视角传租户id查询到的role与租户视角不同
     */
    @Override
    public List<SysRole> flowRoleList(SysRoleQuery role) {
        return roleMapper.selectRoleList(role,new SysUser());
    }

    @Override
    public List<SysRole> selectRolePublicList(SysRole role) {
        List<SysRole> list = roleMapper.selectRolePublicList(role);
        if(StringUtils.isNotBlank(role.getRoleType()) && role.getRoleType().contains(VideoSystemEnums.RoleType.PUBLIC_ROLE.getIndex()) && StringUtils.isNotBlank(role.getIndustryCode())){
            List<SysRole> sysRoles = roleMapper.selectRoleIndustryList(role);
            if(sysRoles != null && sysRoles.size() > 0){
                if(null != list && list.size() > 0){
                    list.addAll(sysRoles);
                }else{
                    list = sysRoles;
                }
            }
        }
        return list;
    }

    /**
     * 查询租户的角色
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<SysRole> selectRoleByTenantId(String tenantId) {
        List<SysRole> list = null;
        if(StrUtil.isEmpty(tenantId)) {
            list = roleMapper.selectTowerAndPublicRole();
        } else {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            log.info("selectRoleByTenantId入参=userId:{},userName:{},userType:{},tenantId:{}",user.getUserId(),user.getUserName(),user.getUserType(),tenantId);
            if(VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(user.getUserType())) {
                // 	租户管理员账号创建租户普通用户时，选择角色时，
                // 展示本租户下的带租户id的角色和该租户管理员已有的审批流角色和该租户管理员已有的公共的行业角色 -- 5期需求
                list = roleMapper.selectOwnOrTenantRole(user.getUserId(), tenantId,user.getIndustryCode(),user.getAppVerCode());
            }
            else {
                // 	铁塔账号创建行业用户时，角色可以选择审批流角色和对应行业的版本角色 -- 4期需求
                List<SysTenantTrade> tenantTrades = roleMapper.selectTenantTradeByTenantId(tenantId,user.getPlatformCode());
                list = roleMapper.selectPublicOrTradeRole(tenantTrades);
            }

        }
        return list;
    }

    /**
     * 根据用户ID查询权限
     *
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(SysUser user) {
        Set<String> permsSet = new HashSet<>();
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(user);
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll()
    {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRoleQuery());
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param user 用户ID
     * @return 选中角色ID列表
     */
    @Override
    @DataScope(tenantColumn = "srdr.TENANT_ID",industryCode = "srdr.industry_code",appVerCode = "srdr.app_ver_code")
    public List<SysRole> selectRoleListByUserId(SysUser user)
    {
        return roleMapper.selectRoleListByUserId(user);
    }

    /**
     * 根据标识查询用户下的角色
     *
     * @param user 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<SysRole> selectRoleListByCondition(SysUser user)
    {
        return roleMapper.selectRoleListByCondition(user);
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param user 用户ID
     * @return 选中角色ID列表
     */
    @Override
    @DataScope(tenantColumn = "srdr.TENANT_ID",appVerCode = "srdr.app_ver_code")
    public List<SysRole> selectRoleLists(SysUser user)
    {
        return roleMapper.selectRoleLists(user);
    }


    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    @DataScope(industryCode = "ia.industry_code",appVerCode = "ia.app_ver_code")
    public SysRole selectRoleById(RoleDTO roleId)
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        roleId.setAppVerCode(user.getAppVerCode());
        roleId.setIndustryCode(user.getIndustryCode());
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 通过角色KEY查询角色
     *
     * @param roleKey 角色key
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleByKey(String roleKey) {
        return roleMapper.selectRoleByKey(roleKey);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        role.setIndustryCode(currentUser.getIndustryCode());
        role.setAppVerCode(currentUser.getAppVerCode());
        role.setTenantId(currentUser.getTenantId());
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        List<SysRole> list = roleMapper.checkRoleNameUnique(role);
        if((list == null) || (list.size() == 0)){
            return UserConstants.UNIQUE;
        }
        if(list.size() > 1){
            return UserConstants.NOT_UNIQUE;
        }
        SysRole info = list.get(0);
        if (StringUtils.isNotNull(info) && (info.getRoleId().longValue() != roleId.longValue())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SysRole role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && (info.getRoleId().longValue() != roleId.longValue())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new CustomException("不允许操作超级管理员角色");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 
     * 方法用途: 通过角色ID查询角色使用数量(去除已经删除的用户id)<br>
     * 实现步骤: <br>
     * @param roleId
     * @return
     */
    public int countUserRoleByRoleIdExcludeDeledUser(Long roleId) {
    	return userRoleMapper.countUserRoleByRoleIdExcludeDeledUser(roleId);
    }


    /**
     *
     * 方法用途: 通过角色ID查询角色使用数量(去除已经删除的用户id)<br>
     * 实现步骤: <br>
     * @param roleIdList
     * @return
     */
    public int countUserRoleByRoleIdListExcludeDeledUser(List<Long> roleIdList) {
        return userRoleMapper.countUserRoleByRoleIdListExcludeDeledUser(roleIdList);
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRole(SaveRoleParam role) {
        Long roleKeyNum=0L;
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String userType = user.getUserType();
        if(userType.equals(VideoSystemEnums.UserType.TOWER_USER.getIndex())){//铁塔用户
            String roleType = role.getRoleType();
            switch (roleType) {//0=铁塔角色,1=公共角色,2=租户角色
                case "0":
                    getRoleKeyNum(userType, roleType, TOWER_ROLE);
                    roleKeyNum = RedisUtil.hIncrBy(ROLE_KEY_NUM, TOWER_ROLE, 1,RedisDBType.PRIMARY);
                    role.setRoleSort(roleKeyNum.toString());
                    role.setRoleKey(TOWER_ROLE+roleKeyNum);
                    break;
                case "1":
                    getRoleKeyNum(userType, roleType, PROCESS_ROLE);
                    roleKeyNum = RedisUtil.hIncrBy(ROLE_KEY_NUM, PROCESS_ROLE, 1,RedisDBType.PRIMARY);
                    role.setRoleSort(roleKeyNum.toString());
                    role.setRoleKey(PROCESS_ROLE+roleKeyNum);
                    break;
                case "2":
                    getRoleKeyNum(userType, roleType, TRADE_ROLE);
                    roleKeyNum = RedisUtil.hIncrBy(ROLE_KEY_NUM, TRADE_ROLE, 1,RedisDBType.PRIMARY);
                    role.setRoleSort(roleKeyNum.toString());
                    role.setRoleKey(TRADE_ROLE+roleKeyNum);
                    break;
                default:
                    break;
            }
        }else{
            String tenantId = user.getTenantId();
            if(StrUtil.isNotEmpty(tenantId)){
                genRoleKey(role,userType,tenantId);

            }
        }
        role.setStatus("0");
        role.setCreateTime(new Date());
        roleMapper.insertRole(role);

        if(StringUtils.isNotBlank(user.getAppVerCode()) && StringUtils.isNotBlank(user.getIndustryCode())){
            SysRoleDataRel roleDataRel = new SysRoleDataRel();
            roleDataRel.setRoleId(role.getRoleId());
            roleDataRel.setDataScope(role.getDataScope());
            roleDataRel.setTenantId(user.getTenantId());
            roleDataRel.setIndustryCode(user.getIndustryCode());
            roleDataRel.setAppVerCode(user.getAppVerCode());
            roleDataRelMapper.insert(roleDataRel);
        }

        //7-29表结构修改后新增
        this.saveRoleIndustryAuth(role);
        //TODO 7-29表结构修改后应该没用了
        this.updateRolePermission(role);
        //return insertRoleMenu(role);
    }

    private void genRoleKey(SysRole role,String userType,String tenantId){
        getRoleKeyNum(userType, "", tenantId);
        Long roleKeyNum = RedisUtil.hIncrBy(ROLE_KEY_NUM, tenantId, 1,RedisDBType.PRIMARY);
        role.setRoleSort(roleKeyNum.toString());
        role.setRoleKey(tenantId+"_"+roleKeyNum);
        String s = checkRoleKeyUnique(role);
        if(s.equalsIgnoreCase(UserConstants.NOT_UNIQUE)){
            genRoleKey(role,userType,tenantId);
        }
    }

    private void getRoleKeyNum(String userType, String roleType, String key) {
        //key:tower_role_:铁塔;process_role_:审批流;trade_role_:行业  租户id:租户
        //判断redis中是否存在roleKeyNum:key值 ,不存在则从数据表中取最新一条数据开始计数
        String tower_role = RedisUtil.hGet(ROLE_KEY_NUM, key, RedisDBType.PRIMARY);
        if(StrUtil.isEmpty(tower_role)){
            SysRole sysRole=roleMapper.getLatelyRole(userType,roleType,key);
            if(sysRole!=null&&StrUtil.isNotEmpty(sysRole.getRoleSort())){
                RedisUtil.hPut(ROLE_KEY_NUM, key,Integer.parseInt(sysRole.getRoleSort()),RedisDBType.PRIMARY);
            }
        }
    }


    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SaveRoleParam role) {
        role.setUpdateTime(new Date());
        this.updateRolePermission(role);
        return roleMapper.updateRole(role);
    }

    /**
     * 保存角色与行业角色权限关联
     * @param role
     */
    private void saveRoleIndustryAuth(SaveRoleParam role){
        roleIndustryAuthMapper.deleteByRole(role.getRoleId());
        if(!CollectionUtils.isEmpty(role.getRoleIndustryAuths())){
            List<SysRoleIndustryAuth> list = new ArrayList<>();
            role.getRoleIndustryAuths().forEach(e->{
                e.setRoleId(role.getRoleId());
                e.setTenantId(role.getTenantId());
                list.add(e);
            });
            roleIndustryAuthMapper.batchInsert(list);
        }else if(role.getMenuIds() != null && role.getMenuIds().length > 0){
            Long[] menuIds = role.getMenuIds();
            List<Long> longs = roleIndustryAuthMapper.selectAuthIdByMenuId(menuIds,role.getIndustryCode(),role.getAppVerCode());
            List<SysRoleIndustryAuth> list = new ArrayList<>();
            longs.forEach(authId ->{
                SysRoleIndustryAuth industryAuth = new SysRoleIndustryAuth();
                industryAuth.setAuthId(String.valueOf(authId));
                industryAuth.setRoleId(role.getRoleId());
                industryAuth.setTenantId(role.getTenantId());
                list.add(industryAuth);
            });
            if(CollUtil.isNotEmpty(list)){
                roleIndustryAuthMapper.batchInsert(list);
            }
        }else{
            //查询出当前用户的行业大屏权限
        }
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     * @auther: liuxt
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        if (role.getStatus().equalsIgnoreCase("1") && countUserRoleByRoleIdExcludeDeledUser(role.getRoleId()) > 0) {
            throw new CustomException(String.format("%1$s已分配,不能停用", role.getRoleName()));
        }
        role.setUpdateTime(new Date());
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void authDataScope(SaveRoleParam role) {
        role.setUpdateTime(new Date());
        // 修改角色信息
        roleMapper.updateRole(role);
        this.saveRoleDataAuth(role);
    }

    /**
     * 修改角色的数据和菜单权限
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRolePermission(SaveRoleParam role) {

        this.saveRoleDataAuth(role);

        this.saveRoleIndustryAuth(role);

        //TODO 7-26 表结构修改了，不知道下面逻辑是否全部删除
        // 删除角色与菜单关联关系
        /*roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        // 插入角色与菜单关联关系
        insertRoleMenu(role);
        role.setUpdateTime(new Date());
        // 修改角色信息
        int i = roleMapper.updateRole(role);
        */
        //新增角色,修改角色关联部门数据
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncUpdateSysRole(role);
            }
        });
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    /*public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (!list.isEmpty()) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }*/

    private void saveRoleDataAuth(SaveRoleParam role){
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        //删除角色数据权限
        roleDataRelMapper.delete(role.getRoleId(),sysUser.getIndustryCode(),sysUser.getAppVerCode());
        //新增角色数据权限
        if(StringUtils.isNotBlank(sysUser.getIndustryCode()) && StringUtils.isNotBlank(sysUser.getAppVerCode())){
            SysRoleDataRel sysRoleDataRel = new SysRoleDataRel();
            sysRoleDataRel.setRoleId(role.getRoleId());
            sysRoleDataRel.setTenantId(role.getTenantId());
            sysRoleDataRel.setDataScope(role.getDataScope());
            sysRoleDataRel.setIndustryCode(sysUser.getIndustryCode());
            sysRoleDataRel.setAppVerCode(sysUser.getAppVerCode());

            roleDataRelMapper.insert(sysRoleDataRel);
        }
        // 删除角色与部门关联关系
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        // 插入角色与部门关联关系
        insertRoleDept(role.getRoleId(),role.getDeptIds());
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param roleId 角色id
     * @param deptIds 部门id
     */
    public int insertRoleDept(Long roleId, String [] deptIds) {
        int rows = 1;
        if(deptIds == null || deptIds.length == 0){
            return 0;
        }
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (String deptId : deptIds) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(roleId);
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (!list.isEmpty()) {
            rows = roleDeptMapper.batchRoleDept(list);
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int deleteRoleById(Long roleId)
    {
        int i = roleMapper.deleteRoleById(roleId);
        //删除角色时,删除角色部门关联
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncDelSysRole(new Long[]{roleId});
            }
        });
        return i;
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
        }
        List<Long> roleIdList = Arrays.asList(roleIds);
        if (countUserRoleByRoleIdListExcludeDeledUser(roleIdList) > 0) {
            throw new CustomException("当前角色已被分配，请先取消角色后再进行操作");
        }
        roleIndustryAuthMapper.deleteByRoleIdList(roleIdList);
        int i = roleMapper.deleteRoleByIds(roleIds);
        //删除角色时,删除角色部门关联
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                asyncDelSysRole(roleIds);
            }
        });
        return i;
    }

    /**
     * 查询某种类型角色对应的部门
     * @author 李峥
     * @return List<SysRoleDeptResponse>
     */
    @Override
    public List<SysRoleDeptResponse> selectAllRoleDept(String roleType) {
        return roleDeptMapper.selectAllRoleDept(roleType);
    }



    /**
     * 更新redis中角色部门关联数据
     * @author 李峥
     * @param sysRole
     */
    private void asyncUpdateSysRole(SysRole sysRole){
        try {
            Long roleId = sysRole.getRoleId();
            if (null == roleId) return;
            if (null != sysRole.getDeptIds() && sysRole.getDeptIds().length > 0){
                String[] deptIds = sysRole.getDeptIds();
                List<String> longs = Arrays.asList(deptIds);
                RedisUtil.set(CacheConstants.ROLE_SCOPE_LIST_PREFIX + roleId, longs, RedisDBType.PRIMARY);
            }
            if (null == sysRole.getDeptIds() || sysRole.getDeptIds().length == 0){
                RedisUtil.delete(CacheConstants.ROLE_SCOPE_LIST_PREFIX + roleId, RedisDBType.PRIMARY);
            }
        } catch (Exception e) {
            log.error("更新redis中角色部门关联数据失败:{}", e.getMessage());
        }
    }

    /**
     * 删除redis中角色部门关联数据
     * @param roleIds
     */
    private void asyncDelSysRole(Long[] roleIds) {
        try {
            if (null == roleIds || roleIds.length == 0) return;
            for (Long roleId : roleIds
            ) {
                RedisUtil.delete(CacheConstants.ROLE_SCOPE_LIST_PREFIX + roleId, RedisDBType.PRIMARY);
            }
        } catch (Exception e) {
            log.error("删除redis中角色部门关联数据:{}", e.getMessage());
        }
    }



    @Override
    public List<SysRoleScreenVO> getRoleScreenRef(SysRoleScreenVO sysRoleScreenVO) {
        List<SysRoleScreenVO> list = roleMapper.getRoleScreenRef(sysRoleScreenVO);
        return list;
    }


    @Override
    public SysRoleScreenVO getCurrentRoleScreen(SysRoleScreenVO sysRoleScreenVO) {
        SysRoleScreenVO returnVO = new SysRoleScreenVO();
        SysUser user = new SysUser();
        user.setTenantId(sysRoleScreenVO.getTenantId());
        user.setIndustryCode(sysRoleScreenVO.getIndustryCode());
        user.setAppVerCode(sysRoleScreenVO.getAppVerCode());
        sysRoleScreenVO.setUserId(roleMapper.getUserByLoginName(sysRoleScreenVO.getUserName()).getUserId());
        user.setUserId(sysRoleScreenVO.getUserId());
        List<SysRole> sysRoles = new ArrayList<>();
        List<Long> roleIdList = new ArrayList<>();
        //判断用户的所有角色是否有默认角色
        user.setRoleFlag(Constants.ONE);
        sysRoles = selectRoleListByCondition(user);
        ScreenMenuConfVO screenMenuConfVO = new ScreenMenuConfVO();
        screenMenuConfVO.setTenantId(user.getTenantId());
        screenMenuConfVO.setIndustryCode(user.getIndustryCode());
        screenMenuConfVO.setAppVerCode(user.getAppVerCode());
        //判断是否配置了个性化
        List<ScreenMenuConfVO> personalList = screenMenuConfMapper.getScreenMenuConf(screenMenuConfVO);
        if(!CollectionUtils.isEmpty(personalList)){
            returnVO.setPersonalFlag(Constants.ONE);
        }else{
            returnVO.setPersonalFlag(Constants.ZERO);
        }
        if(CollectionUtils.isEmpty(sysRoles)){//如果有默认角色，浩鲸返回全部大屏，修改逻辑返回全量个性化配置 如果没有默认角色 进入该分支
            //取出用户的所有自定义角色
            user.setRoleFlag(Constants.TWO);
            sysRoles = selectRoleListByCondition(user);
            if(!CollectionUtils.isEmpty(sysRoles)){
                for (int i = 0; i < sysRoles.size(); i++) {
                    roleIdList.add(sysRoles.get(i).getRoleId());
                }
            }
            sysRoleScreenVO.setRoleIdList(roleIdList);
            //取出在关系表配置的角色idList
            List<String> addRoleIdList = roleMapper.getCurrentRoleScreenRoleId(sysRoleScreenVO);
            if(roleIdList.size() == addRoleIdList.size()){//如果入参的角色idList比关系表配置的角色idList size大，代表有存量角色，浩鲸返回全部大屏，修改逻辑返回全量个性化配置 如果没有存量角色 进入该分支
                //取出新增角色（有角色大屏配置关系）
                List<ScreenMenuConfVO> roleScreenList = roleMapper.getCurrentRoleScreen(sysRoleScreenVO);
                returnVO.setScreenMenuConfVOList(roleScreenList);
                returnVO.setRoleScreenFlag(Constants.ONE);
                return returnVO;
            }
        }
        returnVO.setScreenMenuConfVOList(personalList);
        return returnVO;
    }

    /**
     * 功能说明: 保存角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    @Override
    public int saveRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList,Long roleId,String flag) {
        int num = 0;
        if(!Constants.ONE.equals(flag)){
            SysRoleScreenVO sysRoleScreenVO = new SysRoleScreenVO();
            sysRoleScreenVO.setRoleId(roleId);
            roleMapper.deleteRoleScreenRef(sysRoleScreenVO);
        }
        if(!CollectionUtils.isEmpty(sysRoleScreenVOList)){
            sysRoleScreenVOList.stream().forEach(item -> item.setCreator(SecurityUtils.getOperator()));
            num = roleMapper.insertRoleScreenRef(sysRoleScreenVOList);
        }
        return num;
    }


    /**
     * 功能说明: 逻辑删除角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    @Override
    public int logicDeleteRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList) {
        sysRoleScreenVOList.stream().forEach(item -> item.setUpdateBy(SecurityUtils.getOperator()));
        int num =roleMapper.logicDeleteRoleScreenRef(sysRoleScreenVOList);
        return num;
    }




}
