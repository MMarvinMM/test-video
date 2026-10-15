package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.exception.CommonErrorCode;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.HMAC_SHA1;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.domain.dto.QueryThirdMenuDTO;
import com.chinatower.platform.project.system.domain.dto.SysThirdMenuDTO;
import com.chinatower.platform.project.system.domain.vo.SysMenuTreeVO;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.platform.utils.OkhttpUtil;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.SysTreeSelect;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.domain.SysIndustryAuth;
import com.chinatower.platform.project.system.domain.SysMenu;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.MenuVo;
import com.chinatower.platform.project.system.domain.vo.MetaVo;
import com.chinatower.platform.project.system.mapper.SysIndustryAuthMapper;
import com.chinatower.platform.project.system.mapper.SysMenuMapper;
import com.chinatower.platform.project.system.mapper.SysRoleIndustryAuthMapper;
import com.chinatower.platform.project.system.service.ISysMenuService;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import springfox.documentation.spring.web.json.Json;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 菜单 业务层处理
 *
 * @author chinatower
 */
@Service
@Slf4j
public class SysMenuServiceImpl implements ISysMenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysRoleIndustryAuthMapper roleIndustryAuthMapper;

    @Autowired
    private SysIndustryAuthMapper industryAuthMapper;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private ISysConfigService iSysConfigService;

    public static final String PLATFORM_CODE = "platform_code";

    public static final String LAYOUT = "Layout";
    public static final String NO_REDIRECT = "noRedirect";
    public static final String IS_FRAME = "isFrame";

    public static final String GANG_LAYOUT = "/layout";

    /**
     * 查询菜单集合
     * @param menu
     * @return
     */
    @Override
    public List<SysMenu> selectForList(SysMenu menu) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String code = user.getPlatformCode();
        menu.setAcceptNode(code);
        List<SysMenu> sysMenus = menuMapper.selectMenuList(menu);
        if(CollUtil.isNotEmpty(sysMenus) && (StringUtils.isNotBlank(menu.getMenuName()) || StringUtils.isNotBlank(menu.getVisible()) || StringUtils.isNotBlank(menu.getStatus())
            || StringUtils.isNotBlank(menu.getMenuClassifyEq()) || StringUtils.isNotBlank(menu.getMenuApply()))){
            List<SysMenu> returnList = new ArrayList<>();
            List<SysMenu> sysMenuDistinct = recursionSelectMenu(sysMenus, returnList, code);
            decryption(sysMenuDistinct);
            List<SysMenu> sysMenuList1 = buildTree(sysMenuDistinct);
            return sysMenuList1;
        }
        decryption(sysMenus);
        return buildMenuTree(sysMenus);
    }


    private List<SysMenu> recursionSelectMenu(List<SysMenu> sysMenus, List<SysMenu> returnList, String acceptNode) {
        returnList.addAll(sysMenus);
        List<Long> menuIds = sysMenus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        SysMenu menuParam = new SysMenu();
        menuParam.setParentIds(menuIds);
        menuParam.setAcceptNode(acceptNode);
        menuParam.setNotInFlag(1);
        List<SysMenu> menus = menuMapper.selectMenuList(menuParam);
        if (CollUtil.isNotEmpty(menus)) {
            recursionSelectMenu(menus, returnList, acceptNode);
        }
        return returnList;
    }

    private List<SysMenu> buildTree(List<SysMenu> menus) {
        // 创建一个Map来存储每个节点的ID和对应的节点
        Map<Long, SysMenu> nodeMap = new HashMap<>();
        // 初始化Map
        for (SysMenu node : menus) {
            nodeMap.put(node.getMenuId(), node);
        }
        Iterator<SysMenu> iterator = menus.iterator();
        // 组装树结构
        while (iterator.hasNext()) {
            SysMenu node = iterator.next();
            if (nodeMap.containsKey(node.getParentId())) {
                // list中的元素与map中的元素为同一块内存地址，所以可以直接在迭代器中删除掉当前对象，通过map将children属性更新到内存对象中
                SysMenu parent = nodeMap.get(node.getParentId());
                parent.getChildren().add(node);
                iterator.remove();
            }
        }
        return menus; // 返回所有根节点
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String code = user.getPlatformCode();
        menu.setAcceptNode(code);
        List<SysMenu> sysMenus = menuMapper.selectMenuList(menu);
        decryption(sysMenus);
        return sysMenus;
    }

    private void decryption(List<SysMenu> sysMenus) {
        if(CollUtil.isNotEmpty(sysMenus)){
            Set<String> collect = sysMenus.stream().filter(e->StringUtils.isNotEmpty(e.getFileUrl())).map(e->AESUtil.decrypt(e.getFileUrl())).collect(Collectors.toSet());
            Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(collect));
            for (SysMenu sysMenu:sysMenus) {
                if((sysMenu.getMenuApply().equals(VideoSystemEnums.MenuApply.APP_MENU.getIndex()) || sysMenu.getMenuApply().equals(VideoSystemEnums.MenuApply.WX_MENU.getIndex())) && StrUtil.isNotEmpty(sysMenu.getFileUrl())){
//                    //图片解密
                    String url = AESUtil.decrypt(sysMenu.getFileUrl());
                    try{
//                        SysFileInfo fileInfo = new SysFileInfo();
//                        fileInfo.setFileUrl(url);
                        sysMenu.setFileUrl(urlMap.get(url));
                    }catch (Exception e){
                    }
                }
            }
        }
    }

    /**
     * 功能说明:根据当前登录用户登录后所选行业所属角色拥有的菜单权限查询菜单下拉树结构
     * @param:
     * @return:
     * @author: 王梦迪
     * @date: 2022/3/2 14:27
     * @requestID  需求单号及需求名称
     */
    @Override
    public List<SysMenu> treeselectByRoleScope(SysMenu menu) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if(SecurityUtils.isSwitch()){
            SysUser user1 = userService.selectUserOnlyByUserName(loginUser.getUsername());
            user.setUserId(user1.getUserId());
        }
        menu.setAcceptNode(user.getPlatformCode());
        // 用户信息
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(user.getUserId())) {
            menus = menuMapper.selectMenuList(menu);
        } else {
            user.setUserType(menu.getMenuClassify());

            menus = menuMapper.treeselectByRoleScope(user);
        }
        return menus;
    }

    @Override
    public List<SysMenu> selectCheckMenuListByRoleId(Long roleId) {
        return menuMapper.selectCheckMenuListByRoleId(roleId);
    }

    @Override
    public void disable(Long menuId,Integer status) {
        if(status == null){
            throw new RuntimeException("状态不允许为空");
        }
        //TODO 菜单停用时，需要将本级菜单及下属所有菜单、按钮都停用。
        List<SysMenu> sysMenus = menuMapper.selectChilds(menuId);
        if(null == sysMenus){
            return;
        }
        List<Long> list = new ArrayList<>();
        sysMenus.forEach(m->{
            list.add(m.getMenuId());
        });
        menuMapper.batchUpdateMenuVisible(list,status);
    }

    @Override
    public void changeStatus(Long menuId, Integer status) {
        if(status == null){
            throw new RuntimeException("状态不允许为空");
        }
        //TODO 菜单停用时，需要将本级菜单及下属所有菜单、按钮都停用。
        List<SysMenu> sysMenus = menuMapper.selectChilds(menuId);
        if(null == sysMenus){
            return;
        }
        List<Long> list = new ArrayList<>();
        sysMenus.forEach(m->{
            list.add(m.getMenuId());
        });
        menuMapper.batchUpdateMenuStatus(list,status);
    }

    private void getDescendantsRecursive(List<SysMenu> menuList, List<SysMenu> forDelete) {
        for (SysMenu sysMenu : menuList) {
            List<SysMenu> children = menuMapper.selectByParentId(sysMenu.getMenuId());
            if(children != null && !children.isEmpty()) {
                forDelete.addAll(children);
                getDescendantsRecursive(children, forDelete);
            }
        }
    }


    /**
     * 根据用户ID查询权限
     *
     * @param user 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(SysUser user) {
        //默认是pc菜单
        String userKey = SecurityUtils.getLoginUserId();
        List<String> clientType = Arrays.asList(new String[]{"0", "1"});
        if (StrUtil.isNotEmpty(userKey) && userKey.startsWith("app")) {
            clientType = Arrays.asList(new String[]{"2"});
        }
        user.setClientType(clientType);
        List<String> perms = menuMapper.selectAllMenuPermsByUser(user);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Stream.of(perm.trim().split(",")).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户名称查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(String userId,String tradeId,List<String> list) {
        return selectMenuTreeByUserIdInternal(userId,tradeId,list,false);
    }


    /**
     * 根据用户名称查询菜单（不区分显隐状态）
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserIdNoVisible(String userId,String tradeId,List<String> list) {
        return selectMenuTreeByUserIdInternal(userId,tradeId,list,true);
    }


    /**
     * 根据用户名称查询菜单内部方法
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserIdInternal(String userId,String tradeId,List<String> list,boolean noVisible) {
        StopWatch stopWatch = new StopWatch("getRouters selectMenuTreeByUserId cost");
        stopWatch.start("1");
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId)) {
            //如果是app或小程序的admin
            if (list != null && (list.contains("2") || list.contains("3"))) {
                if (noVisible) {
                    menus = menuMapper.selectMenuTreeAllByAppNoVisible(list);
                }else{
                    menus = menuMapper.selectMenuTreeAllByApp(list);
                }
                menuIconDecrypt(menus);
                return menus;
            }else{
                if (noVisible) {
                    menus =  menuMapper.selectMenuTreeAllByAdminNoVisible(list);
                } else {
                    menus =  menuMapper.selectMenuTreeAllByAdmin(list);
                }
            }
            stopWatch.stop();
            stopWatch.start("2");
        } else {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            stopWatch.stop();
            stopWatch.start("3");
            if (noVisible) {
                menus = menuMapper.selectMenuTreeByUserIdNoVisible(userId,loginUser.getUser().getUserType(),loginUser.getUser().getIndustryCode(),loginUser.getUser().getAppVerCode(),list);
            }else{
                menus = menuMapper.selectMenuTreeByUserId(userId,loginUser.getUser().getUserType(),loginUser.getUser().getIndustryCode(),loginUser.getUser().getAppVerCode(),list);

            }
            //菜单补全
            stopWatch.stop();
            stopWatch.start("4");
            List<SysMenu> compensates = compensate(userId,loginUser.getUser().getUserType(),loginUser.getUser().getIndustryCode(),loginUser.getUser().getAppVerCode(),list);
            if(compensates != null && compensates.size() > 0){
                menus.addAll(compensates);
            }
            stopWatch.stop();
            stopWatch.start("5");
            if (list != null && (list.contains("2") || list.contains("3"))) {
                List<String> types = Lists.newArrayList("M", "C");
                //app移除按钮和首页
                menus = menus.stream().filter(x -> types.contains(x.getMenuType()))
                        .filter(item -> !"/indexPermission".equals(item.getPath()))
                        .filter(item -> !"/appMenuList".equals(item.getPath()))
                        //小程序最高级菜单
                        .filter(item -> !"/wxMenuList".equals(item.getPath()))
                        .filter(item -> !"/appHomeBottom".equals(item.getPath()))
                        .collect(Collectors.toList());
                menuIconDecrypt(menus);
                stopWatch.stop();
                stopWatch.start("6");
                menus = menuSort(menus);
                return menuDistinct(menus);
            }
        }
        stopWatch.stop();
        stopWatch.start("7");
        menuIconDecrypt(menus);
        stopWatch.stop();
        stopWatch.start("8");
        menus = menuSort(menus);
        stopWatch.stop();
        stopWatch.start("9");
        menus = menuDistinct(menus);
        stopWatch.stop();
        stopWatch.start("10");
        //查询button的上级
        List<SysMenu> childPerms = getChildPerms(menus, 0);
        if (!CollectionUtils.isEmpty(childPerms)) {
            for (SysMenu childPerm : childPerms) {
                if (!CollectionUtils.isEmpty(childPerm.getChildren())) {
                    childPerm.setChildren(childPerm.getChildren().stream().filter(e -> Objects.nonNull(e.getOrderNum()))
                            .sorted(Comparator.comparing(SysMenu::getOrderNum, Comparator.comparingInt(Integer::parseInt))).collect(Collectors.toList()));
                }
            }
        }
        stopWatch.stop();
        log.info("getRouters selectMenuTreeByUserId result :"+stopWatch.prettyPrint());
        return childPerms;
    }


    @Override
    public List<SysMenu> compensate(String userId,String userType, String industryCode,String appVerCode, List<String> list){
        List<SysMenu> result = new ArrayList<>();
        List<SysMenu> leveC = new ArrayList<>();
        StopWatch stopWatch = new StopWatch("getRouters compensate cost");
        stopWatch.start("1");
        /**
         * 查询菜单和目录暂不卡行业，数据关联存在问题后续解决 20221112
         */
        List<SysMenu> levelF = menuMapper.selectParentMenuByMenuType(null,"F",userId,userType,industryCode,appVerCode,list);
        //防止除首页按钮外没有其他按钮权限
        stopWatch.stop();
        stopWatch.start("2");
        if (levelF.size()==1){
            leveC=menuMapper.selectParentMenuByMenuType(null,"C",userId,userType,industryCode,appVerCode,list);
        }else{
        	List<Long> parentIds = getMenuIds(levelF);
            stopWatch.stop();
            stopWatch.start("3");
        	if (!CollectionUtils.isEmpty(parentIds))
        	{
        		leveC = menuMapper.selectParentMenuByMenuType(parentIds, "C", null, userType, null, null, list);
        	}
            log.info("getRouters compensate cost param+"+JSON.toJSONString(list)+"---"+JSON.toJSONString(parentIds)+ userType);
            stopWatch.stop();
            stopWatch.start("4");
            leveC.addAll(menuMapper.selectParentMenuByMenuType(null,"C",userId,userType,industryCode,appVerCode,list));
            stopWatch.stop();
            stopWatch.start("5");
        }
//        List<SysMenu> leveC =  menuMapper.selectParentMenuByMenuType(getMenuIds(levelF),"C",null,userType,null,null,list);
        List<SysMenu> leveM =  menuMapper.selectParentMenuByMenuType(getMenuIds(leveC),"M",null,userType,null,null,list);
        stopWatch.stop();
        stopWatch.start("6");
        if(levelF != null){
            result.addAll(levelF);
        }
        if(leveC != null){
            result.addAll(leveC);
        }
        if(leveM != null){
            result.addAll(leveM);
        }
        stopWatch.stop();
        log.info("getRouters compensate result :"+stopWatch.prettyPrint());
        return result;
    }

    private List<Long> getMenuIds(List<SysMenu> list){
        if(list != null){
            List<Long> result = new ArrayList<>();
            list.forEach(s->{
                if(!result.contains(s.getParentId())){
                    result.add(s.getParentId());
                }
            });
            return result;
        }
        return null;
    }


    /**
     * 菜单去重
     */
    private List<SysMenu> menuDistinct(List<SysMenu> list) {
        Set<Long> menuIds = new HashSet<>();
        List<SysMenu> result = new ArrayList<>();
        for (SysMenu m : list) {
            if (menuIds.contains(m.getMenuId())) {
                continue;
            }
            menuIds.add(m.getMenuId());
            result.add(m);
        }
        return result;
    }

    /**
     * 菜单排序
     */
    private List<SysMenu> menuSort(List<SysMenu> menus) {
        menus = menus.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(SysMenu::getMenuId))), ArrayList::new));
        menus = menus.stream().peek(x->{
                    if (StringUtils.isBlank(x.getOrderNum())){
                        //为空排前面，和数据库保持一致
                        x.setOrderNum("1");
                    }
                }).sorted(Comparator.comparing(SysMenu::getOrderNum,Comparator.comparingInt(Integer::parseInt)))
                        .collect(Collectors.toList());
        return menus;
    }

    /**
     * 处理菜单图标
     */
    private void menuIconDecrypt(List<SysMenu> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Set<String> collect = list.stream().filter(e->StringUtils.isNotEmpty(e.getFileUrl())).map(SysMenu::getFileUrl).collect(Collectors.toSet());
        collect = collect.stream().map(e->AESUtil.decrypt(e)).collect(Collectors.toSet());
        try {
            Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(collect));
            list.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getFileUrl())) {
                    String url = AESUtil.decrypt(item.getFileUrl());
                    try {
//                SysFileInfo fileInfo = new SysFileInfo();
//                fileInfo.setFileUrl(url);
//                obsFileUrlEncryptDeal.dealObsFileUrl(fileInfo);
                        item.setFileUrl(urlMap.get(url));
                    } catch (Exception ignore) {
                    }
                }
            });
        }catch (Exception e) {
            log.error("文件服务异常", e);
        }
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<String> selectMenuListByRoleId(Long roleId,String industryCode,String appVerCode) {
        return menuMapper.selectMenuListByRoleId(roleId,industryCode,appVerCode);
    }

    /**
     * 根据租户信息查询菜单列表
     * @param roleDTO
     * @return
     */
    @Override
    public List<MenuVo> tanentMenuTreeselect(RoleDTO roleDTO) {
        return menuMapper.tanentMenuTreeselect(roleDTO);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<MenuVo> buildMenus(List<SysMenu> menus, boolean ifRoot) {
        List<MenuVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            MenuVo router = new MenuVo();
            //判断是否为一级菜单，给一级菜单添加Layout包裹
            if(ifRoot && "C".equals(menu.getMenuType())){
                MetaVo metaVo = new MetaVo();
                metaVo.put("onlyHideCurrent",true);
                router.setName(GANG_LAYOUT+menu.getPath());
                router.setPath(GANG_LAYOUT+menu.getPath());
                router.setComponent(LAYOUT);
                router.setRedirect(NO_REDIRECT);
                router.setMeta(metaVo);
                router.setType(Constants.DIRECTORY);
                router.setChildren(buildMenus(CollUtil.toList(menu),false));
            }else {
                router.setType(menu.getMenuType());
                router.setName(menu.getPath());
                router.setPath(menu.getPath());
                router.setComponent(StrUtil.isEmpty(menu.getComponent()) ? LAYOUT : menu.getComponent());
                MetaVo metaVo = new MetaVo(menu.getMenuName(), menu.getIcon());
                if(StrUtil.isNotBlank(menu.getRemark())){
                    try {
                        metaVo.putAll(JSON.parseObject(menu.getRemark()));
                    }catch (Exception e){

                    }
                }
                metaVo.put(IS_FRAME,"0".equals(menu.getIsFrame()));
                router.setMeta(metaVo);
                List<SysMenu> cMenus = menu.getChildren();
                if (!cMenus.isEmpty() && Constants.DIRECTORY.equals(menu.getMenuType())) {
                    router.setRedirect(NO_REDIRECT);
                    router.setChildren(buildMenus(cMenus,false));
                }
            }
            routers.add(router);
        }
        return routers;
    }


    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<MenuVo> buildMenusNoVisible(List<SysMenu> menus, boolean ifRoot) {
        List<MenuVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            MenuVo router = new MenuVo();
            //判断是否为一级菜单，给一级菜单添加Layout包裹
            if(ifRoot && "C".equals(menu.getMenuType())){
                MetaVo metaVo = new MetaVo();
                metaVo.put("onlyHideCurrent",true);
                router.setName(GANG_LAYOUT+menu.getPath());
                router.setPath(GANG_LAYOUT+menu.getPath());
                router.setComponent(LAYOUT);
                router.setRedirect(NO_REDIRECT);
                // 设置hidden字段（数据库0显示 , 1隐藏 ）
                metaVo.put("hidden",Constants.ONE.equals(menu.getVisible()));
                router.setMeta(metaVo);
                router.setType(Constants.DIRECTORY);
                router.setChildren(buildMenus(CollUtil.toList(menu),false));
            }else {
                router.setType(menu.getMenuType());
                router.setName(menu.getPath());
                router.setPath(menu.getPath());
                router.setComponent(StrUtil.isEmpty(menu.getComponent()) ? LAYOUT : menu.getComponent());
                MetaVo metaVo = new MetaVo(menu.getMenuName(), menu.getIcon());
                // 设置hidden字段（数据库0显示 , 1隐藏 ）
                metaVo.put("hidden",Constants.ONE.equals(menu.getVisible()));
                if(StrUtil.isNotBlank(menu.getRemark())){
                    try {
                        metaVo.putAll(JSON.parseObject(menu.getRemark()));
                    }catch (Exception e){

                    }
                }
                metaVo.put(IS_FRAME,"0".equals(menu.getIsFrame()));
                router.setMeta(metaVo);
                List<SysMenu> cMenus = menu.getChildren();
                if (!cMenus.isEmpty() && Constants.DIRECTORY.equals(menu.getMenuType())) {
                    router.setRedirect(NO_REDIRECT);
                    router.setChildren(buildMenusNoVisible(cMenus,false));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext();) {
            SysMenu t = iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == 0) {
                recursionFn(menus, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        for (SysMenu sysMenu : returnList) {
            if (StringUtils.isBlank(sysMenu.getOrderNum())) {
                sysMenu.setOrderNum("1");
            }
            if (!CollectionUtils.isEmpty(sysMenu.getChildren())) {
                sysMenu.setChildren(sysMenu.getChildren().stream().peek(e -> {
                            if (StringUtils.isBlank(e.getOrderNum())) {
                                e.setOrderNum("1");
                            }
                        }).sorted(Comparator.comparing(SysMenu::getOrderNum, Comparator.comparingInt(Integer::parseInt)))
                        .collect(Collectors.toList()));
            }
        }
        return returnList.stream()
                .sorted(Comparator.comparing(SysMenu::getOrderNum, Comparator.comparingInt(Integer::parseInt)))
                .collect(Collectors.toList());
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<SysTreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
//        menuTrees = menuTrees.stream().filter(e->!StrUtil.isBlank(e.getOrderNum())).sorted(Comparator.comparing(SysMenu::getOrderNum)).collect(Collectors.toList());
        return menuTrees.stream().map(SysTreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        SysConfig sysConfig = iSysConfigService.selectConfigByKey(PLATFORM_CODE);
    	SysMenu menu = menuMapper.selectMenuById(menuId,sysConfig.getConfigValue());
    	if( StringUtils.isNotBlank(menu.getFileUrl())) {
    		menu.setFileUrlDe(AESUtil.decryptHttp(menu.getFileUrl()));
    		try
    		{
    			menu.setFileUrlObs(fileUrlEncryptUtil.dealObsFileUrl(menu.getFileUrlDe()));
    		}
    		catch (Exception e) {
    			log.error(e.getMessage(), e);
			}
        }
        if(menu.getParentId() != 0) {
            SysMenu menuParent = menuMapper.selectMenuById(menu.getParentId(),sysConfig.getConfigValue());
            menu.setParentName(menuParent.getMenuName());
        }
        return menu;
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        return menuMapper.hasChildByMenuId(menuId) > 0;
    }

    /**
     * 查询菜单是否被角色使用
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return roleIndustryAuthMapper.checkMenuExistRole(menuId) > 0;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        SysConfig sysConfig = iSysConfigService.selectConfigByKey(PLATFORM_CODE);
        SysMenu parent = menuMapper.selectMenuById(menu.getParentId(),sysConfig.getConfigValue());
        if(parent != null && StringUtils.isBlank(menu.getMenuClassify())){
            menu.setMenuClassify(parent.getMenuClassify());
        }
        String[] cf = menu.getMenuClassify().split(",");
        this.checkParentClassify(null,menu.getParentId(),Arrays.asList(cf));
        checkParentType(parent,menu);
        menu.setCreateTime(new Date());
        menu.setStatus("0");
        try {
            if(StringUtils.isNotBlank(menu.getRemark())){
                JSONObject.parseObject(menu.getRemark());
            }
        }catch (Exception e){
            menu.setRemark(null);
        }
        if( StringUtils.isNotBlank(menu.getFileUrl()) )
        {
        	menu.setFileUrl(AESUtil.encryptHttp(menu.getFileUrl()));
        }
        int i = menuMapper.insertMenu(menu);
//        saveIndustryAuth(menu);
        return i;
    }
    public void checkParentType(SysMenu parent,SysMenu menu){
        if(parent == null){
            if (menu.getMenuType().equalsIgnoreCase("F")){
                throw new RuntimeException("主类目下不能增加按钮。");
            }
            return;
        }
        switch (parent.getMenuType().toUpperCase()){
            case "M":
                if(!menu.getMenuType().equalsIgnoreCase("C")){
                    throw new RuntimeException("目录下只能添加菜单类型。");
                }
                break;
            case "C":
                if(!menu.getMenuType().equalsIgnoreCase("F")){
                    throw new RuntimeException("菜单下只能添加按钮类型。");
                }
                break;
            case "F":
                throw new RuntimeException("按钮下不能再继续添加菜单。");
            default:
                throw new RuntimeException("菜单类型不正确。");
        }
    }

    public void saveIndustryAuth(SysMenu menu){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysIndustryAuth auth = new SysIndustryAuth();
        auth.setIndustryCode(user.getIndustryCode());
        auth.setAppVerCode(user.getAppVerCode());
        auth.setMenuId(menu.getMenuId());
        auth.setTenantId(user.getTenantId());
        industryAuthMapper.insert(auth);

    }

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateMenu(SysMenu menu) {
        String[] cf = menu.getMenuClassify().split(",");
        SysConfig sysConfig = iSysConfigService.selectConfigByKey(PLATFORM_CODE);
        SysMenu sysMenu = menuMapper.selectMenuById(menu.getMenuId(),sysConfig.getConfigValue());
        SysMenu parent = menuMapper.selectMenuById(sysMenu.getParentId(),sysConfig.getConfigValue());
        this.checkParentClassify(null,menu.getParentId(),Arrays.asList(cf));
        this.checkChildClassify(menu.getMenuId());
        checkParentType(parent,menu);
        menu.setUpdateTime(new Date());
        try {
            if(StringUtils.isNotBlank(menu.getRemark())){
                JSONObject.parseObject(menu.getRemark());
            }

        }catch (Exception e){
            menu.setRemark(null);
        }
        // 证明修改了权限 且修改的数据为菜单
        if(!sysMenu.getMenuClassify().equals(menu.getMenuClassify()) && menu.getMenuType().equalsIgnoreCase("C")) {
            SysMenu menuParam = new SysMenu();
            menuParam.setModifier(menu.getModifier());
            menuParam.setUpdateTime(new Date());
            menuParam.setMenuClassify(menu.getMenuClassify());
            menuParam.setParentId(menu.getMenuId());
            menuMapper.updateMenuByParentId(menuParam);
        }

        if( StringUtils.isNotBlank(menu.getFileUrl()) )
        {
        	menu.setFileUrl(AESUtil.encryptHttp(menu.getFileUrl()));
        }
        return menuMapper.updateMenu(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public int deleteMenuById(Long menuId) {
        Map<String, Object> params = new HashMap<>();
        params.put("menu_id",menuId);
//        industryAuthMapper.deleteByMap(params);
        List<Long> longs = menuMapper.selectAllMenuId(menuId);
        if(longs != null && longs.size() > 0){
            return menuMapper.deleteMenuById(longs);
        }
        return 0;
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu) {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }


    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();) {
            SysMenu t = iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<SysMenu> it = childList.iterator();
                while (it.hasNext()) {
                    SysMenu n = (SysMenu) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 校验下级菜单权限是否大于当前菜单
     * @param currentid
     */
    private void checkChildClassify(Long currentid){
        SysConfig sysConfig = iSysConfigService.selectConfigByKey(PLATFORM_CODE);
        SysMenu current = menuMapper.selectMenuById(currentid,sysConfig.getConfigValue());
        List<String> currentClassify = Arrays.asList(current.getMenuClassify().split(","));
        List<SysMenu> childMenuList = menuMapper.selectByParentId(currentid);

        if(CollectionUtils.isEmpty(childMenuList)){
            return;
        }

        Set<String> childClassify = childMenuList.stream().map(SysMenu::getMenuClassify).collect(Collectors.toSet());

        List<String> result = childClassify.stream().filter(e->!currentClassify.contains(e)).collect(Collectors.toList());
        if(result!=null&&result.size()>0){
            throw new RuntimeException("下级菜单权限冲突，为了避免影响使用，请修改下级菜单权限。");
        }

    }

    /**
     * 校验当前菜单权限是否大于上级菜单
     * @param parentId
     */
    private void checkParentClassify(Long currentId,Long parentId,List<String> currentClassify){

        if(parentId==null || parentId == 0){
            return;
        }

        if(currentId != null && currentId != 0){
            SysConfig sysConfig = iSysConfigService.selectConfigByKey(PLATFORM_CODE);
            SysMenu current = menuMapper.selectMenuById(currentId,sysConfig.getConfigValue());
            currentClassify = Arrays.asList(current.getMenuClassify().split(","));
        }

        SysConfig sysConfig = iSysConfigService.selectConfigByKey(PLATFORM_CODE);
        SysMenu parent = menuMapper.selectMenuById(parentId,sysConfig.getConfigValue());
        List<String> parentClassify = Arrays.asList(parent.getMenuClassify().split(","));

        List<String> result = currentClassify.stream().filter(e->!parentClassify.contains(e)).collect(Collectors.toList());

        if(result!=null&&result.size()>0){
            throw new RuntimeException("与上级菜单权限冲突，请重新选择。");
        }

    }


    /**
     * 按照菜单分类获取路由信息（app）
     * @param menu
     * @return
     */
    @Override
    public List<SysMenu> selectMenuClassifyInfo(SysMenu menu) {
        List<SysMenu> sysMenus = menuMapper.selectMenuClassifyInfo(menu);
        return sysMenus;
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param menu
     * @return 选中菜单列表
     */
    @Override
    public List<SysMenuTreeVO> selectMenuByUserRole(SysMenu menu) {
        List<SysMenuTreeVO> list = menuMapper.selectMenuByUserRole(menu);
        List<String> levelFParentIdList = list.stream().filter(a -> com.chinatower.platform.project.system.constants.Constants.F.equals(a.getMenuType())).map(SysMenuTreeVO::getPid).distinct().collect(Collectors.toList());
        List<String> levelCIdList = list.stream().filter(a -> com.chinatower.platform.project.system.constants.Constants.C.equals(a.getMenuType())).map(SysMenuTreeVO::getId).distinct().collect(Collectors.toList());
        levelCIdList.addAll(levelFParentIdList);
        SysMenu levelFMenuVO = new SysMenu();
        levelFMenuVO.setIdList(levelCIdList);
        List<SysMenuTreeVO> levelCMenuList = menuMapper.selectMenuByUserRole(levelFMenuVO);
        List<String> levelMIdList = levelCMenuList.stream().filter(a -> com.chinatower.platform.project.system.constants.Constants.C.equals(a.getMenuType())).map(SysMenuTreeVO::getPid).distinct().collect(Collectors.toList());
        SysMenu levelMMenuVO = new SysMenu();
        levelMMenuVO.setIdList(levelMIdList);
        List<SysMenuTreeVO> levelMMenuList = menuMapper.selectMenuByUserRole(levelMMenuVO);
        list.addAll(levelCMenuList);
        list.addAll(levelMMenuList);
        if (!CollectionUtils.isEmpty(list)) {
            list = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                    () -> new TreeSet<>(Comparator.comparing(a -> Long.parseLong(a.getId())))), ArrayList::new));
        }
        return list;
    }

    @Override
    public List<MenuVo> findThirdMenuByTenantIndustryAppVerCode(QueryThirdMenuDTO queryThirdMenuDTO) {
        List<SysThirdMenuDTO> thirdMenu = menuMapper.findThirdMenuByTenantIndustryAppVerCode(queryThirdMenuDTO);
        if (!thirdMenu.isEmpty()) {
            //构建树
            // 1. 创建节点映射表 (menuId -> node) 和子节点映射表 (parentId -> children)
            Map<String, MenuVo> nodeMap = new HashMap<>();
            Map<String, List<MenuVo>> childrenMap = new HashMap<>();

            // 初始化映射表
            for (SysThirdMenuDTO menu : thirdMenu) {
                MenuVo router = new MenuVo();
                //判断是否为一级菜单，给一级菜单添加Layout包裹
                if("C".equals(menu.getMenuType())){
                    MetaVo metaVo = new MetaVo();
                    metaVo.put("title", StringUtils.isEmpty(menu.getCustomName()) ? menu.getMenuName() : menu.getCustomName());
                    metaVo.put("icon", menu.getIcon());
                    metaVo.put("noCache", false);
                    metaVo.put(IS_FRAME, false);
                    metaVo.put("iframeUrl", menu.getIframeUrl());
                    metaVo.put("appid", System.currentTimeMillis());
                    metaVo.put("isKeepAlive", false);
                    metaVo.put("communication", true);
                    metaVo.put("ssoLoginFlag", menu.getSsoLoginFlag());
                    metaVo.put("menuId", menu.getMenuId());
                    router.setName(menu.getPath());
                    router.setPath(menu.getPath());
                    router.setComponent(menu.getComponent());
                    router.setMeta(metaVo);
                    router.setOrderNum(menu.getOrderNum());
                    router.setMenuId(menu.getMenuId());
                    router.setType(menu.getMenuType());
                }else {
                    router.setType(menu.getMenuType());
                    router.setName(menu.getPath());
                    router.setPath(menu.getPath());
                    router.setOrderNum(menu.getOrderNum());
                    router.setMenuId(menu.getMenuId());
                    router.setComponent(LAYOUT);
                    MetaVo metaVo = new MetaVo(menu.getMenuName(), menu.getIcon());
                    metaVo.put("noCache", false);
                    metaVo.put(IS_FRAME, false);
                    metaVo.put("title", StringUtils.isEmpty(menu.getCustomName()) ? menu.getMenuName() : menu.getCustomName());
                    metaVo.put("redirect", NO_REDIRECT);

                    if(StrUtil.isNotBlank(menu.getRemark())){
                        try {
                            metaVo.putAll(JSON.parseObject(menu.getRemark()));
                        }catch (Exception e){

                        }
                    }
                    metaVo.put(IS_FRAME,"0");
                    router.setMeta(metaVo);
                }
                nodeMap.put(menu.getMenuId(), router);

                // 初始化children列表
                router.setChildren(new ArrayList<>());

                // 准备子节点映射
                String parentId = menu.getParentId();
                if (parentId == null || parentId.isEmpty()) {
                    parentId = "0";
                }
                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(router);
            }

            // 2. 构建树结构 - 只保留根节点在最外层
            List<MenuVo> rootNodes = new ArrayList<>();
            for (SysThirdMenuDTO menu : thirdMenu) {
                String parentId = menu.getParentId();

                // 识别根节点
                if (parentId == null || parentId.isEmpty() || !nodeMap.containsKey(parentId)) {
                    rootNodes.add(nodeMap.get(menu.getMenuId()));
                }
            }

            // 3. 为每个节点设置子节点（从childrenMap中获取）
            for (MenuVo menu : rootNodes) {
                List<MenuVo> children = childrenMap.get(menu.getMenuId());
                if (children != null && !children.isEmpty()) {
                    // 排序子节点
                    children.sort(Comparator.comparingLong(MenuVo::getOrderNum));
                    menu.setChildren(children);
                }
            }
            return rootNodes;
        }
        return null;
    }

    @Override
    public Result<String> getThirdPartyUrlWithAuthorization(QueryThirdMenuDTO model) {
        //获取第三方菜单详情

        SysThirdMenuDTO configModel = menuMapper.findById(model.getMenuId());
        if (configModel == null) {
            return Result.fail("第三方菜单不存在");
        }
        //封装验签
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        SysUser sysUser = loginUser.getUser();
        //签名字符串 = URL的Path部分+ X-AppKey + X-MsgId + X-RequestTime + X-CustomerCode（如果有）+ X-UserCode（如果有）。
        Date now = new Date();
        String msgId = DateUtils.dateToString(now);
        String requestTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, now);

        String urlPath = getUrlPath(configModel.getIframeUrl());
        String signature = urlPath +
                configModel.getAppKey() +
                msgId +
                requestTime +
                sysUser.getTenantId() +
                sysUser.getUserId();
        String signatureStr = signature.toUpperCase();
        log.info("验签加密前====>{}", signatureStr);
        String mySignature = HMAC_SHA1.signByHmacSHA1(configModel.getSecretKey(), signatureStr);
        log.info("验签加密后====>{}", mySignature);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-MsgId", msgId);
        headerMap.put("X-RequestTime", requestTime);
        headerMap.put("X-AppKey", configModel.getAppKey());
        headerMap.put("X-CustomerCode", sysUser.getTenantId());
        headerMap.put("X-UserCode", sysUser.getUserId());
        headerMap.put("X-Signature", mySignature);

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("msgId", msgId);
        bodyMap.put("requestTime", requestTime);
        bodyMap.put("appKey", configModel.getAppKey());
        bodyMap.put("customerCode", sysUser.getTenantId());
        bodyMap.put("userCode", sysUser.getUserId());
        bodyMap.put("signature", mySignature);
        //todo 调用鉴权接口 获取跳转url
        String screenUrl = configModel.getIframeUrl();
        JSONObject resultObject = OkhttpUtil.doPost(screenUrl, JSON.toJSONString(bodyMap), headerMap);
        if (resultObject == null) {
            return Result.fail("调用第三方鉴权失败");
        }
        String returnCode = resultObject.getString("returnCode");
        if (CommonErrorCode.SUCCESSFUL_CODE.equals(returnCode)) {
            JSONObject dataObject = JSON.parseObject(JSON.toJSONString(resultObject.get("data")));
            if (dataObject == null) {
                return Result.fail("调用第三方鉴权返回信息为空");
            }
            return Result.data(dataObject.getString("url"));
        } else {
            return Result.fail(resultObject.getString("returnMsg"));
        }
    }

    private String getUrlPath(String screenUrl) {
        String urlPath = "";
        try {
            URL url = new URL(screenUrl);
            urlPath = url.getPath();
        } catch (Exception e) {
            log.error(" getUrlPath error, screenUrl:{}, e:", screenUrl, e);
        }
        return urlPath;
    }

    /**
     * 为菜单列表填充父节点名称
     * 只针对app和小程序请求场景，为每个菜单设置其父节点的名称
     *
     * @param menus 菜单列表
     */
    @Override
    public void fillParentNames(List<SysMenu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return;
        }

        // 收集所有需要查询的父节点ID
        Set<Long> parentIds = new HashSet<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId() != null && menu.getParentId() != 0) {
                parentIds.add(menu.getParentId());
            }
        }

        // 如果没有父节点需要查询，直接返回
        if (parentIds.isEmpty()) {
            return;
        }

        // 批量查询父节点信息（一次性查询所有父节点）
        List<SysMenu> parentMenus = menuMapper.selectMenusByIds(new ArrayList<>(parentIds));
        
        // 构建父节点ID到名称的映射
        Map<Long, String> parentNameMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(parentMenus)) {
            for (SysMenu parentMenu : parentMenus) {
                parentNameMap.put(parentMenu.getMenuId(), parentMenu.getMenuName());
            }
        }

        // 为每个菜单设置父节点名称
        for (SysMenu menu : menus) {
            if (menu.getParentId() != null && menu.getParentId() != 0) {
                String parentName = parentNameMap.get(menu.getParentId());
                if (StringUtils.isNotBlank(parentName)) {
                    menu.setParentName(parentName);
                }
            }
        }
    }

}
