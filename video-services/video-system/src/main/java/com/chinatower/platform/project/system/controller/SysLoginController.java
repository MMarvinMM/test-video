package com.chinatower.platform.project.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.dto.IndustryReqModel;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.manage.api.IIndustryService;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.security.DESUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceOptAuthVO;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.framework.security.service.SysPermissionService;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.HeroVO;
import com.chinatower.platform.project.system.domain.SysFileInfo;
import com.chinatower.platform.project.system.domain.SysLoginConfig;
import com.chinatower.platform.project.system.domain.SysMenu;
import com.chinatower.platform.project.system.domain.dto.QueryThirdMenuDTO;
import com.chinatower.platform.project.system.domain.vo.MenuVo;
import com.chinatower.platform.project.system.domain.vo.SysInfoSetVO;
import com.chinatower.platform.project.system.service.*;
import com.chinatower.platform.utils.AESUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.request.user.AddUserRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


/**
 * video-system
 * 登录验证
 *
 * @author iwhalecloud
 */
@Api(tags = " 登录验证")
@Slf4j
@RestController
public class SysLoginController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysDeptService deptService;


    @Autowired
    private ISysLoginConfigService sysLoginConfigService;

    @Autowired
    private ISysPersonalityService personalityService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IIndustryService iIndustryService;

    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    private static final String S1_S2= "$1****$2";

    private static final String LOG_BY_USERNAME = "selectUserOnlyByUserName:";

    private static final String LOG_BY_USERNAME_ERROR = "根据登录信息username从数据库查询用户信息失败，username->{}";

    private static final String PHONE_DESENSITIZE = "(\\d{3})\\d*(\\d{4})";

    private static final String INDUSTRY_TWO = "2";

    private static final String VIDEO_CONTROL_ROLE  = "video:videoControlRole";

    private static final String VIDEO_PRESET_ROLE  = "video:videoPreset";

    private static final String VIDEO_CRUISE_PLAN_ROLE  = "video:videoCruisePlan";

    private static final String VIDEO_GUARD_ROLE = "video:videoGuard";

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/base/getInfo")
    public AjaxResult getInfo() {
        log.info("进入getInfo()接口");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
        log.info(LOG_BY_USERNAME+ JSONObject.toJSONString(user1));
        if (Objects.isNull(user1)) {
            log.error(LOG_BY_USERNAME_ERROR, user.getUserName());
            user1 = new SysUser(user.getUserId());
        } else {
            //替换最新redis中的数据
            user.setSex(user1.getSex());
            user.setEmail(user1.getEmail());
        }
        if(SecurityUtils.isSwitch()){
            user.setUserId(user1.getUserId());
        }

        AjaxResult ajax = AjaxResult.success();
        String password = DESUtil.decrypt(user.getPassword());
        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,20}$";
        if (ReUtil.isMatch(regex, password)) {
            user.setPassword(null);
        } else {
            user.setPassword("TO_MODIFY");
        }
        //手机号加密
        String phonenumber = user.getPhonenumber();
        String encryptPhone = AESUtil.encrypt(phonenumber);
        if (StringUtils.isNotEmpty(phonenumber)){
            user.setPhonenumber(phonenumber.replaceAll(PHONE_DESENSITIZE, S1_S2));//fixme 后续版本处理
        }
        user.setAesPhoneNumber(encryptPhone);

        String telPhone = user.getTelPhone();
        if (StringUtils.isNotEmpty(telPhone)){
            user.setTelPhone(telPhone.replaceAll(PHONE_DESENSITIZE, S1_S2));//fixme 后续版本处理
        }
        user.setAesTelPhone(AESUtil.encrypt(telPhone));
        log.info("开始dealObsFileUrl");
        long s8 = System.currentTimeMillis();
        try {
            user.setAvatar(fileUrlEncryptUtil.dealObsFileUrl(user1.getAvatar()));
        } catch (Exception e) {
            log.error("获取用户头像失败", e);
        }
        log.info("结束dealObsFileUrl耗时:{}ms", System.currentTimeMillis() - s8);
        ajax.put("user", user);
        // 角色集合
        long s1 = System.currentTimeMillis();
        ajax.put("roles", permissionService.getRolePermission(user));
        log.info("获取用户信息->permissionService.getRolePermission耗时:{}ms", System.currentTimeMillis() - s1);

        log.info("开始selectUserAppVerCodesByUserId");
        long s6 = System.currentTimeMillis();
        List<SysIndustryViewRel> list = sysUserService.selectUserAppVerCodesByUserId(user.getUserId(),user.getPlatformCode());
        log.info("结束selectUserAppVerCodesByUserId耗时:{}ms", System.currentTimeMillis() - s6);

        long s7 = System.currentTimeMillis();
        if (list != null && list.size() > 0) {
            Set<String> collect = list.stream().filter(e -> StringUtils.isNotEmpty(e.getImgUrl())).map(SysIndustryViewRel::getImgUrl).collect(Collectors.toSet());
            try {
                Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(collect));
                for (SysIndustryViewRel sysIndustryViewRel : list) {
                    if (StringUtils.isNotEmpty(sysIndustryViewRel.getImgUrl())) {
                        sysIndustryViewRel.setImgUrl(urlMap.get(sysIndustryViewRel.getImgUrl()));
                    }
                }
            } catch (Throwable e) {
                log.error("文件服务错误", e);
            }
        }
        log.info("批量加密dealObsFileUrlBatch耗时:{}ms", System.currentTimeMillis() - s7);
        ajax.put("tradeVersions", list == null ? Collections.emptyList() : list);

        // 权限集合
        long s2 = System.currentTimeMillis();
        Set<String> permissions = permissionService.getMenuPermission(user);
        log.info("获取用户信息->permissionService.getMenuPermission耗时:{}ms", System.currentTimeMillis() - s2);
        if (StrUtil.isNotEmpty(loginUser.getUser().getAppVerCode())) {
            IndustryReqModel industryReqModel = new IndustryReqModel();
            industryReqModel.setVersion(user.getIndustryCode());
            industryReqModel.setIndustryCode(user.getAppVerCode());
            List<String> permissionsList = null;
            log.info("开始getAuthUrlList");
            long s5 = System.currentTimeMillis();
            try {
                permissionsList = iIndustryService.getAuthUrlList(industryReqModel).getData();
            } catch (Exception e) {
                log.error("根据行业编码、版本号获取鉴权url获取失败",e);
            }
            log.info("结束getAuthUrlList耗时:{}ms", System.currentTimeMillis() - s5);
            if (permissionsList != null) {
                permissions.addAll(permissionsList);
            }
        }

        //兼容行业低版本没升级重新构造权限
        Set<String> permissionsNew = getOptPermissions(user, permissions);

        ajax.put("permissions", permissionsNew);
        ajax.put("deptInfo", user.getDept());

        log.info("开始userDeptTreeselect");
        long s4 = System.currentTimeMillis();
        List<TreeSelect> treeSelects = deptService.userDeptTreeselect();
        log.info("结束userDeptTreeselect耗时:{}ms", System.currentTimeMillis() - s4);
        ajax.put("userDeptTreeList", treeSelects);

        log.info("开始putPersonalityConfig");
        long s3 = System.currentTimeMillis();
        putPersonalityConfig(ajax, user);
        log.info("结束putPersonalityConfig耗时:{}ms", System.currentTimeMillis() - s3);

        return ajax;
    }

    private void putPersonalityConfig(AjaxResult result, SysUser user) {
        log.info("开始putPersonalityConfig");
        Map<String, String> map = new HashMap<>();
        SysInfoSetVO vo = new SysInfoSetVO();
        vo.setCreator(SecurityUtils.getUsername());
        try {
            log.info("开始getThemeInfoByLoginUser"+JSONObject.toJSONString(vo));
            List<SysInfoSetVO> list1 = personalityService.getThemeInfoByLoginUser(vo);
            log.info("结束getThemeInfoByLoginUser"+JSONObject.toJSONString(list1));
            list1.forEach(info -> map.put(info.getSetName(), info.getSetValue()));
            SysInfoSetVO setVO = new SysInfoSetVO();
            setVO.setIsDefault(1);
            log.info("开始getInfoSetList"+JSONObject.toJSONString(setVO));
            List<SysInfoSetVO> list2 = personalityService.getInfoSetList(setVO);
            log.info("结束getInfoSetList"+JSONObject.toJSONString(list2));
            list2.forEach(info -> map.put(info.getSetName(), info.getSetValue()));
            SysFileInfo fileInfo = new SysFileInfo();
            log.info("开始dealObsFileUrl");
            fileInfo.setFileUrl(fileUrlEncryptUtil.dealObsFileUrl(MapUtil.getStr(map, "logo")));
            log.info("结束dealObsFileUrl");
            result.put("systemLogo",fileInfo.getFileUrl());
        } catch (Exception e) {
            log.error("文件错误", e);
        }
        log.info("开始selectConfigValueByKey(\"sys.default.title\")");
        String defaultTitle = configService.selectConfigValueByKey("sys.default.title");
        log.info("开始selectConfigValueByKey(\"sys.default.theme\")");
        String defaultThemeStyle = configService.selectConfigValueByKey("sys.default.theme");
        log.info("开始selectConfigValueByKey(\"sys.default.color\")");
        String defaultThemeColor = configService.selectConfigValueByKey("sys.default.color");
        log.info("开始selectConfigValueByKey(\"sys.default.size\")");
        String defaultLayoutSize = configService.selectConfigValueByKey("sys.default.size");
        log.info("开始selectConfigValueByKey(\"sys.index.greetings\"");
        String greetings = configService.selectConfigValueByKey("sys.index.greetings");
        String systemTitle = MapUtil.getStr(map, "platformName", defaultTitle);
        result.put("systemTitle", systemTitle);
        result.put("themeStyle", MapUtil.getStr(map, "themeStyle", defaultThemeStyle));
        result.put("themeColor", MapUtil.getStr(map, "themeColor", defaultThemeColor));
        result.put("layoutSize", MapUtil.getStr(map, "layoutSize", defaultLayoutSize));
        greetings = greetings.replace("${用户账号}", user.getUserName());
        greetings = greetings.replace("${用户昵称}", user.getNickName());
        greetings = greetings.replace("${系统名称}", systemTitle);
        greetings = greetings.replace("${注册天数}",
                "<span class='days-span'>" + (DateUtil.betweenDay(user.getCreateTime(), new Date(), true)+1) + "</span>");
        result.put("greetings", greetings);
    }

    /**
     * 获取路由信息
     *
     * @param appVersion app版本：1-新app，2-旧app，默认2
     * @return 路由信息
     */
    @ApiOperation(value = "获取路由信息")
    @GetMapping("/base/getRouters")
    public AjaxResult getRouters(@RequestParam(value = "appVersion", defaultValue = "2") Integer appVersion) {
        return getRoutersInternal(false, appVersion);
    }


    /**
     * 获取路由信息（不区分显隐状态）
     *
     * @param appVersion app版本：1-新app，2-旧app，默认2
     * @return 路由信息
     */
    @ApiOperation(value = "获取路由信息（不区分显隐状态）")
    @GetMapping("/base/getRoutersNoVisible")
    public AjaxResult getRoutersNoVisible(@RequestParam(value = "appVersion", defaultValue = "2") Integer appVersion) {
        return getRoutersInternal(true, appVersion);
    }


    /**
     * 获取路由信息内部方法
     *
     * @param noVisible 是否不区分显隐状态（true：不区分显隐，false：只查显示的）
     * @param appVersion app版本：1-新app，2-旧app，默认2
     * @return 路由信息
     */
    public AjaxResult getRoutersInternal(boolean noVisible, Integer appVersion) {
        StopWatch stopWatch = new StopWatch("getRouters result cost");
        stopWatch.start("1");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        stopWatch.stop();
        stopWatch.start("2");
        SysUser user = loginUser.getUser();
        if(SecurityUtils.isSwitch()){
            SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
            user.setUserId(user1.getUserId());
        }
        stopWatch.stop();
        stopWatch.start("3");
        String userKey = SecurityUtils.getLoginUserId();
        List<String> list;
        //默认是pc菜单
        list = Arrays.asList("0", "1");
        if (StrUtil.isNotEmpty(userKey) && userKey.startsWith("app")) {
            list = Collections.singletonList("2");
        }else if(StrUtil.isNotEmpty(userKey) && userKey.startsWith("wx")){
            list = Collections.singletonList("3");
        }
        // 查询菜单（根据是否区分显隐状态调用不同的方法）
        List<SysMenu> menus;
        if (noVisible) {
            menus = menuService.selectMenuTreeByUserIdNoVisible(user.getUserId(), "", list);
        } else {
            menus = menuService.selectMenuTreeByUserId(user.getUserId(), "", list);
        }

        if (StrUtil.isNotEmpty(userKey) && (userKey.startsWith("app") || userKey.startsWith("wx"))) {
            // 为app和小程序请求填充父节点菜单名称
            menuService.fillParentNames(menus);
            if (appVersion != null && appVersion == 2) {
                menus = filterHarmonyBottomMenu(menus);
            }
            return AjaxResult.success(menus);
        }
        stopWatch.stop();
        stopWatch.start("4");
        List<MenuVo> menuVos;
        if (noVisible) {
            menuVos = menuService.buildMenusNoVisible(menus, true);
        }else{
            menuVos = menuService.buildMenus(menus, true);
        }
        stopWatch.stop();
        stopWatch.start("5");
        //如果是客户账号，查询客户行业下配置的目录，菜单
        if (INDUSTRY_TWO.equals(user.getUserType())) {
            QueryThirdMenuDTO queryThirdMenuDTO = new QueryThirdMenuDTO();
            queryThirdMenuDTO.setTenantId(user.getTenantId());
            queryThirdMenuDTO.setIndustryCode(user.getIndustryCode());
            queryThirdMenuDTO.setAppVerCode(user.getAppVerCode());
            List<MenuVo> thirdMenu = menuService.findThirdMenuByTenantIndustryAppVerCode(queryThirdMenuDTO);
            if (null != thirdMenu && !thirdMenu.isEmpty()) {
                menuVos.addAll(thirdMenu);
            }
        }
        stopWatch.stop();
        log.info("getRouters result :"+stopWatch.prettyPrint());
        return AjaxResult.success(menuVos);
    }


    /**
     * 查询【登录配置】列表
     */
    @ApiOperation(value = "查询【登录配置】列表")
    @GetMapping("/loginPageInfo")
    public AjaxResult getLoginInfo(SysLoginConfig sysLoginConfig) {
        sysLoginConfig.setStatus("0");//登录页查询启用页面
        List<SysLoginConfig> list = sysLoginConfigService.selectSysLoginConfigList(sysLoginConfig);
        return AjaxResult.success(list);
    }

    /**
     * 公网根据用户名获取手机号
     */
    @ApiOperation(value = "公网根据用户名获取手机号")
    @PostMapping("/getPhoneByAccount")
    public AjaxResult getPhoneByAccount(@RequestBody AddUserRequest addUserRequest, HttpServletRequest request) {
        String header = request.getHeader("dynamic");
        if (StrUtil.isEmpty(header)) {
            log.info("短信发送少了参数,入参{}", addUserRequest);
            return AjaxResult.error("非法请求!");
        }
        if (ObjectUtil.isNotNull(addUserRequest) && StrUtil.isNotBlank(addUserRequest.getUserName())) {
            return sysUserService.selectUserByUserNamePub(addUserRequest, header);
        } else {
            return AjaxResult.error("用户账户不能为空");
        }
    }

    /**
     * 有token修改手机号 验证旧手机验证码成功后修改成手机号
     */
    @ApiOperation(value = "验证旧手机验证码成功后修改成手机号")
    @PostMapping("/updatePhone")
    @TowerLog(title = "修改手机号",businessType = BusinessType.UPDATE)
    public AjaxResult updatePhone(@RequestBody AddUserRequest addUserRequest) {
        return sysUserService.updatePhone(addUserRequest);
    }

    /**
     * 公网 验证旧手机验证码成功后修改密码
     */
    @ApiOperation(value = "公网 验证旧手机验证码成功后修改密码")
    @PostMapping("/updatePassPublic")
    @TowerLog(title = "密码修改",businessType = BusinessType.UPDATE)
    public AjaxResult updatePassPublic(@RequestBody AddUserRequest addUserRequest) {
        return sysUserService.updatePassPublic(addUserRequest);
    }

    /**
     * 无验证码修改密码
     */
    @ApiOperation(value = "无验证码修改密码")
    @PostMapping("/updatePassApp")
    @TowerLog(title = "密码修改",businessType = BusinessType.UPDATE)
    public AjaxResult updatePassApp(@RequestParam("key") String key, @RequestParam("password") String password,@RequestParam("userName") String userName){
        return sysUserService.updatePass(key,password,userName);
    }

    /**
     * 有token 注销接口
     */
    @ApiOperation(value = " 注销接口")
    @PostMapping("/disabledUser")
    @TowerLog(title = "注销登录",businessType = BusinessType.UPDATE)
    public AjaxResult disabledUser() {
        try{
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            SysUser newSysUser = new SysUser();
            newSysUser.setUserId(sysUser.getUserId());
            newSysUser.setStatus("1");
            sysUserService.updateUserStatus(newSysUser);
            String pcKey = CacheConstants.USER_INDUSTRY_CODE + "web_manage" + ":" + sysUser.getUserId();
            String appKey = CacheConstants.USER_INDUSTRY_CODE + "app_manage" + ":" + sysUser.getUserId();
            RedisUtil.delete(pcKey, RedisDBType.PRIMARY);
            RedisUtil.delete(appKey, RedisDBType.PRIMARY);
        }catch(Exception e){
            log.error("注销异常"+e.getMessage());
            log.error(e.getMessage(),e);
        }

        return AjaxResult.success("注销成功");
    }

    /**
     * 获取用户信息(新)
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/base/getInfoNew")
    public AjaxResult getInfoNew() {
        log.info("进入getInfoNew()接口");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
        log.info(LOG_BY_USERNAME+ JSONObject.toJSONString(user1));
        if (Objects.isNull(user1)) {
            log.error(LOG_BY_USERNAME_ERROR, user.getUserName());
            user1 = new SysUser(user.getUserId());
        } else {
            //替换最新redis中的数据
            user.setSex(user1.getSex());
            user.setEmail(user1.getEmail());
        }
        if(SecurityUtils.isSwitch()){
            user.setUserId(user1.getUserId());
        }

        AjaxResult ajax = AjaxResult.success();
        String password = DESUtil.decrypt(user.getPassword());
        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,20}$";
        if (ReUtil.isMatch(regex, password)) {
            user.setPassword(null);
        } else {
            user.setPassword("TO_MODIFY");
        }
        //手机号加密
        String phonenumber = user.getPhonenumber();
        String encryptPhone = AESUtil.encrypt(phonenumber);
        if (StringUtils.isNotEmpty(phonenumber)){
            user.setPhonenumber(phonenumber.replaceAll(PHONE_DESENSITIZE, S1_S2));//fixme 后续版本处理
        }
        user.setAesPhoneNumber(encryptPhone);

        String telPhone = user.getTelPhone();
        if (StringUtils.isNotEmpty(telPhone)){
            user.setTelPhone(telPhone.replaceAll(PHONE_DESENSITIZE, S1_S2));//fixme 后续版本处理
        }
        user.setAesTelPhone(AESUtil.encrypt(telPhone));
        log.info("开始dealObsFileUrl获取头像");
        long s8 = System.currentTimeMillis();
        try {
            user.setAvatar(fileUrlEncryptUtil.dealObsFileUrl(user1.getAvatar()));
        } catch (Exception e) {
            log.error("获取用户头像失败", e);
        }
        log.info("结束dealObsFileUrl耗时:{}ms", System.currentTimeMillis() - s8);
        ajax.put("user", user);
        // 角色集合
        long s1 = System.currentTimeMillis();
        ajax.put("roles", permissionService.getRolePermission(user));
        log.info("获取用户信息->permissionService.getRolePermission耗时:{}ms", System.currentTimeMillis() - s1);

        log.info("开始selectUserAppVerCodesByUserId");
        long s6 = System.currentTimeMillis();
        List<SysIndustryViewRel> list = sysUserService.selectUserAppVerCodesByUserId(user.getUserId(),user.getPlatformCode());
        log.info("结束selectUserAppVerCodesByUserId耗时:{}ms", System.currentTimeMillis() - s6);

        long s7 = System.currentTimeMillis();
        if (list != null && list.size() > 0) {
            Set<String> collect = list.stream().filter(e -> StringUtils.isNotEmpty(e.getImgUrl())).map(SysIndustryViewRel::getImgUrl).collect(Collectors.toSet());
            try {
                Map<String, String> urlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(collect));
                for (SysIndustryViewRel sysIndustryViewRel : list) {
                    if (StringUtils.isNotEmpty(sysIndustryViewRel.getImgUrl())) {
                        sysIndustryViewRel.setImgUrl(urlMap.get(sysIndustryViewRel.getImgUrl()));
                    }
                }
            } catch (Throwable e) {
                log.error("文件服务错误", e);
            }
        }
        log.info("批量加密dealObsFileUrlBatch耗时:{}ms", System.currentTimeMillis() - s7);
        ajax.put("tradeVersions", list == null ? Collections.emptyList() : list);

        // 权限集合
        long s2 = System.currentTimeMillis();

        ajax.put("deptInfo", user.getDept());

        log.info("开始userDeptTreeselect");
        long s4 = System.currentTimeMillis();
        List<TreeSelect> treeSelects = deptService.userDeptTreeselect();
        log.info("结束userDeptTreeselect耗时:{}ms", System.currentTimeMillis() - s4);
        ajax.put("userDeptTreeList", treeSelects);

        return ajax;
    }

    /**
     * 獲取個性化信息
     *
     * @return 用户信息
     */
    @ApiOperation(value = "獲取個性化信息")
    @GetMapping("/base/getPersonalityConfig")
    public AjaxResult getPersonalityConfig() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
        log.info(LOG_BY_USERNAME+ JSONObject.toJSONString(user1));
        if (Objects.isNull(user1)) {
            log.error(LOG_BY_USERNAME_ERROR, user.getUserName());
            user1 = new SysUser(user.getUserId());
        } else {
            //替换最新redis中的数据
            user.setSex(user1.getSex());
            user.setEmail(user1.getEmail());
        }
        if(SecurityUtils.isSwitch()){
            user.setUserId(user1.getUserId());
        }
        AjaxResult ajax = AjaxResult.success();
        putPersonalityConfig(ajax, user);
        return ajax;
    }


    /**
     * 获取權限信息
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取權限信息")
    @GetMapping("/base/getPermissions")
    public AjaxResult getPermissions() {
        AjaxResult ajax = AjaxResult.success();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
        log.info(LOG_BY_USERNAME+ JSONObject.toJSONString(user1));
        if (Objects.isNull(user1)) {
            log.error(LOG_BY_USERNAME_ERROR, user.getUserName());
            user1 = new SysUser(user.getUserId());
        } else {
            //替换最新redis中的数据
            user.setSex(user1.getSex());
            user.setEmail(user1.getEmail());
        }
        if(SecurityUtils.isSwitch()){
            user.setUserId(user1.getUserId());
        }
        Set<String> permissions = permissionService.getMenuPermission(user);
        log.info("获取用户信息->permissionService.getMenuPermission耗时:{}ms", System.currentTimeMillis());
        if (StrUtil.isNotEmpty(loginUser.getUser().getAppVerCode())) {
            IndustryReqModel industryReqModel = new IndustryReqModel();
            industryReqModel.setVersion(user.getIndustryCode());
            industryReqModel.setIndustryCode(user.getAppVerCode());
            List<String> permissionsList = null;
            log.info("开始getAuthUrlList");
            long s5 = System.currentTimeMillis();
            try {
                permissionsList = iIndustryService.getAuthUrlList(industryReqModel).getData();
            } catch (Exception e) {
                log.error("根据行业编码、版本号获取鉴权url获取失败",e);
            }
            log.info("结束getAuthUrlList耗时:{}ms", System.currentTimeMillis() - s5);
            if (permissionsList != null) {
                permissions.addAll(permissionsList);
            }
        }
        //兼容行业低版本没升级重新构造权限
        Set<String> permissionsNew = getOptPermissions(user, permissions);
        ajax.put("permissions", permissionsNew);
        return ajax;
    }


    /**
     * 按照菜单分类获取路由信息（app）
     *
     */
    @PostMapping("/base/getRoutersByClassification")
    public Result<Map<String, List<SysMenu>>> getRoutersByClassification() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if(SecurityUtils.isSwitch()){
            SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
            user.setUserId(user1.getUserId());
        }
        String userKey = SecurityUtils.getLoginUserId();
        List<String> list = Arrays.asList(new String[]{Constants.TWO});
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId(), "", list);
        List<Long> memuIdList  = menus.stream().map(o -> o.getMenuId()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(memuIdList)){
            SysMenu menu = new  SysMenu();
            menu.setMenuIdList(memuIdList);
            List<SysMenu> menuClassifyList = menuService.selectMenuClassifyInfo(menu);
            if(!CollectionUtils.isEmpty(menuClassifyList)){
                for (int i = 0; i < menuClassifyList.size(); i++) {
                    SysMenu menuClassify = menuClassifyList.get(i);
                    for (int j = 0; j < menus.size(); j++) {
                        if(menuClassify.getMenuId().equals(menus.get(j).getMenuId())){
                            menuClassify.setMenuName(menus.get(j).getMenuName());
                            menuClassify.setFileUrl(menus.get(j).getFileUrl());
                            menuClassify.setParentId(menus.get(j).getParentId());
                            menuClassify.setPath(menus.get(j).getPath());
                            menuClassify.setIsFrame(menus.get(j).getIsFrame());
                            menuClassify.setMenuType(menus.get(j).getMenuType());
                            menuClassify.setVisible(menus.get(j).getVisible());
                            menuClassify.setStatus(menus.get(j).getStatus());
                            menuClassify.setMenuClassify(menus.get(j).getMenuClassify());
                            menuClassify.setIcon(menus.get(j).getIcon());
                        }
                    }
                }
            }
            Map<String, List<SysMenu>> groupedByClassifyName = menuClassifyList.stream()
                    .collect(Collectors.groupingBy(SysMenu::getClassifyName,
                            LinkedHashMap::new,Collectors.toCollection(ArrayList::new)));
            return Result.data(groupedByClassifyName);
        }else{
            return Result.data(null);
        }
    }

    /**
     * 获取第三方菜单免登录地址
     *
     * @param model menuId
     * @return 返回带有授权的第三方URL
     */
    @PostMapping("/menu/getThirdPartyUrlWithAuthorization")
    public Result<String> getThirdPartyUrlWithAuthorization(@RequestBody QueryThirdMenuDTO model) {
        return menuService.getThirdPartyUrlWithAuthorization(model);
    }

    /**
     * 获取用户信息(新)
     *
     * @return 用户信息
     */
    @ApiOperation(value = "获取用户信息")
    @PostMapping("/base/getInfoNewMCP")
    public Result<SysUser> getInfoNewMCP() {
        log.info("进入getInfoNewMCP()接口");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        SysUser user1 = sysUserService.selectUserOnlyByUserName(user.getUserName());
        log.info(LOG_BY_USERNAME+ JSONObject.toJSONString(user1));
        if (Objects.isNull(user1)) {
            log.error(LOG_BY_USERNAME_ERROR, user.getUserName());
            user1 = new SysUser(user.getUserId());
        } else {
            //替换最新redis中的数据
            user.setSex(user1.getSex());
            user.setEmail(user1.getEmail());
        }
        if(SecurityUtils.isSwitch()){
            user.setUserId(user1.getUserId());
        }

        //手机号加密
        String phonenumber = user.getPhonenumber();
        String encryptPhone = AESUtil.encrypt(phonenumber);
        if (StringUtils.isNotEmpty(phonenumber)){
            user.setPhonenumber(phonenumber.replaceAll(PHONE_DESENSITIZE, S1_S2));//fixme 后续版本处理
        }
        user.setAesPhoneNumber(encryptPhone);

        String telPhone = user.getTelPhone();
        if (StringUtils.isNotEmpty(telPhone)){
            user.setTelPhone(telPhone.replaceAll(PHONE_DESENSITIZE, S1_S2));//fixme 后续版本处理
        }
        user.setAesTelPhone(AESUtil.encrypt(telPhone));
        return Result.data(user);
    }

    /**
     * 获取基本信息
     */
    @PostMapping(value = "/base/getHeroBaseInfo")
    public Result<HeroVO> heroBaseInfo(@RequestBody HeroVO heroVO)
    {
        return Result.data(sysUserService.getHeroBaseInfo(heroVO));
    }

    private Set<String> getOptPermissions(SysUser user,Set<String> permissions){
        try {
            DeviceOptAuthVO deviceOptAuthVO = permissionService.checkDeviceOptAuth(user);
            if(null != deviceOptAuthVO){
                // 判断摄像头权限有权限
                if(deviceOptAuthVO.getCameraStatus().equals(Constants.ZERO)){
                    if(!permissions.contains(VIDEO_CONTROL_ROLE)){
                        permissions.add(VIDEO_CONTROL_ROLE);
                    }
                }else{
                    if(permissions.contains(VIDEO_CONTROL_ROLE)){
                        permissions.remove(VIDEO_CONTROL_ROLE);
                    }
                }
                //预置位权限
                if(deviceOptAuthVO.getPresetStatus().equals(Constants.ZERO)){
                    if(!permissions.contains(VIDEO_PRESET_ROLE)){
                        permissions.add(VIDEO_PRESET_ROLE);
                    }
                }else{
                    if(permissions.contains(VIDEO_PRESET_ROLE)){
                        permissions.remove(VIDEO_PRESET_ROLE);
                    }
                }
                //巡航计划权限
                if(deviceOptAuthVO.getPatrolStatus().equals(Constants.ZERO)){
                    if(!permissions.contains(VIDEO_CRUISE_PLAN_ROLE)){
                        permissions.add(VIDEO_CRUISE_PLAN_ROLE);
                    }
                }else{
                    if(permissions.contains(VIDEO_CRUISE_PLAN_ROLE)){
                        permissions.remove(VIDEO_CRUISE_PLAN_ROLE);
                    }
                }
                //看守卫权限
                if(deviceOptAuthVO.getGuardStatus().equals(Constants.ZERO)){
                    if(!permissions.contains(VIDEO_GUARD_ROLE)){
                        permissions.add(VIDEO_GUARD_ROLE);
                    }
                }else{
                    if(permissions.contains(VIDEO_GUARD_ROLE)){
                        permissions.remove(VIDEO_GUARD_ROLE);
                    }
                }
            }
        }catch (Exception e){
            log.info("查询权限报错"+e);
        }
        return permissions;
    }

    /**
     * 过滤【APP鸿蒙首页底部菜单】及其子菜单
     * 只针对新app（版本1）使用
     *
     * @param menus 原始菜单列表
     * @return 过滤后的菜单列表
     */
    private List<SysMenu> filterHarmonyBottomMenu(List<SysMenu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return menus;
        }
        
        // 过滤掉【APP鸿蒙首页底部菜单】目录及其所有子菜单
        return menus.stream()
                .filter(menu -> !"APP鸿蒙首页底部菜单".equals(menu.getMenuName()))
                .filter(menu -> !"APP鸿蒙首页底部菜单".equals(menu.getParentName()))
                .collect(Collectors.toList());
    }

}
