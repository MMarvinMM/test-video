package com.chinatower.platform.project.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.GridKeeperVO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.constants.AuthConstants;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.framework.interceptor.annotation.RepeatRequestCheck;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.custom.entity.DeptStreetVO;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.system.domain.AreaDetailInfo;
import com.chinatower.platform.project.system.domain.AreaInfoResultVO;
import com.chinatower.platform.project.system.domain.SysPost;
import com.chinatower.platform.project.system.domain.WhiteIp;
import com.chinatower.platform.project.system.domain.dto.DeptUserTreeDto;
import com.chinatower.platform.project.system.domain.vo.VerifyUpdateVO;
import com.chinatower.platform.project.system.domain.vo.WhiteIpVO;
import com.chinatower.platform.project.system.mapper.SysUserMapper;
import com.chinatower.platform.project.system.service.*;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.RsaUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.AreaDataVO;
import com.chinatower.system.api.model.DeptUserGroupVO;
import com.chinatower.system.api.model.SysUser4A;
import com.chinatower.system.api.request.user.AddUserRequest;
import com.chinatower.system.api.request.user.GetUserByDeptAndDeviceScopeRequest;
import com.chinatower.system.api.request.user.VideoUpdateUserRequest;
import com.chinatower.system.api.resp.SysUserResponse;
import com.chinatower.system.api.resp.SysUserTenantInfoResponse;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.chinatower.platform.project.system.constants.Constants.ONE;

/**
 * video-system
 * 用户信息
 *
 * @author iwhalecloud
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/system/user")
public class SysUserController extends TowerBaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysImportService importService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IWhiteIpService whiteIpService;

    private static final String DEPT_ERROR = "请勿选择根组织！";

    private static final String NO_DELETE_INFO = "当前用户为网格员，请先从网格员管理中移除后再进行删除";

    private static final String NO_STOP_INFO = "当前用户为网格员，请先从网格员管理中移除后再进行停用";

    public static final int ZERO_NUM = 0;
    private static final String USER_TYPE_0 = "0";
    private static final String TOWER_INDUSTRY = "999999";

    /**
     * 获取用户列表
     */
    @ApiOperation(value = "获取租户用户列表")
    @GetMapping("/list")
    @TowerLog(title = "用户管理", businessType = BusinessType.SELECT)
    public TableDataInfo list(SysUser user) {
        user.setUserType("2");
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        user.setTenantId(currentUser.getTenantId());
        user.setIndustryCode(currentUser.getIndustryCode());
        user.setAppVerCode(currentUser.getAppVerCode());
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 获取用户列表
     */
    @ApiOperation(value = "获取铁塔用户列表")
    @GetMapping("/ttlist")
    @TowerLog(title = "用户管理", businessType = BusinessType.SELECT)
    public TableDataInfo ttlist(SysUser user) {
        startPage();
        user.setUserType("0");
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 用户列表导出
     */
    @ApiOperation(value = "用户列表导出")
    @TowerLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody SysUser user, HttpServletResponse response) throws Exception {
        List<SysUser> list = userService.selectUserList(user);
        byte[] data = new ExcelUtil<>(SysUser.class).exportBytes(list);
        FileUtils.download(response, data);
    }
    /**
     * 用户导入
     */
    @TowerLog(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public Result<String> importData(MultipartFile file, String importType, String fileUrl, boolean updateSupport) throws Exception {
        List<SysUser> list = new ExcelUtil<>(SysUser.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            throw new CustomException("导入数据不能为空！");
        }
        ImportParams<SysUser> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put("updateSupport", updateSupport);
        params.put("loginUser", SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> userService.importUser(p));
    }

    /**
     * 用户导入
     */
    @TowerLog(title = "用户导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importSysUser")
    public Result<String> importSysUser(MultipartFile file, String importType, String fileUrl) throws Exception {
        List<SysUser> list = new ExcelUtil<>(SysUser.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            throw new CustomException("导入数据不能为空！");
        }
        ImportParams<SysUser> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put("loginUser", SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> userService.importUserData(p));
    }

    /**
     * 用户解锁
     */
    @ApiOperation(value = "用户解锁")
    @TowerLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/unlock")
    public AjaxResult unlock(String userName) throws Exception {
        String key = Constants.LOGIN_FAIL_NUM_KEY + userName;
        Boolean aBoolean = RedisUtil.hasKey(key, RedisDBType.PRIMARY);
        if (aBoolean) {
            RedisUtil.delete(key, RedisDBType.PRIMARY);
        } else {
            return AjaxResult.error("用户帐号未被锁定！");
        }
        return AjaxResult.success();
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation(value = "根据用户编号获取详细信息")
    @GetMapping(value = {"base/detail/{userId}", "base/detail/{userId}/{industryCode}", "base/detail/{userId}/{industryCode}/{appVerCode}"})
    public AjaxResult getInfo(@PathVariable(value = "userId") String userId,
                              @PathVariable(value = "industryCode", required = false) String industryCode,
                              @PathVariable(value = "appVerCode", required = false) String appVerCode) {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId)) {

            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (user.getUserType().equalsIgnoreCase(UserTypeEnum.TANENT.getType())) {
                industryCode = user.getIndustryCode();
                appVerCode = user.getAppVerCode();
            }

            SysUser sysUser = userService.selectUserByIdNew(userId, industryCode);
            if (sysUser == null) {
                //安全限制，防止遍历账号
                return AjaxResult.error(AuthConstants.USERNAME_ERROR);
            }
            ajax.put(AjaxResult.DATA_TAG, sysUser);
//            ajax.put("postIds", postService.selectPostListByUserId(userId));

            user.setIndustryCode(industryCode);
            user.setAppVerCode(appVerCode);
            List<SysIndustryViewRel> list = userService.selectUserAppVerCodesByUserId(userId,user.getPlatformCode());
            ajax.put("tradeVersions", list);
            user.setUserId(userId);
            List<SysRole> sysRoles = new ArrayList<>();
            if (industryCode != null && industryCode.contains(",")) {
                List<String> industryCodes = Arrays.asList(industryCode.split(","));
                user.setIndustryCodes(industryCodes);
                sysRoles = roleService.selectRoleLists(user);
            } else {
                sysRoles = roleService.selectRoleListByUserId(user);
            }
            ajax.put("roleIds", sysRoles);
        }
        return ajax;
    }

    /**
     * 用户详情
     */
    @ApiOperation(value = "用户详情")
    @TowerLog(title = "用户详情", businessType = BusinessType.SELECT)
    @PostMapping("/ids/{userIds}")
    public AjaxResult getListByIds(@PathVariable String[] userIds) {
        return AjaxResult.success(userService.selectUserByIds(userIds));
    }

    /**
     * 根据部门和角色查询用户信息
     */
    @ApiOperation(value = "byDeptAndRole")
    @GetMapping("/list/byDeptAndRole")
    public AjaxResult getUserByDeptAndRole(@RequestParam("deptId") String deptId, @RequestParam("roleKey") String roleKey) {
        return AjaxResult.success(userService.selectUserByDeptAndRole(deptId, roleKey));
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户")
    @TowerLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    @RepeatRequestCheck(fields = {"userName","phonenumber"})
    public Result<Integer> add(@Validated @RequestBody AddUserRequest addUserRequest) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(addUserRequest, user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return Result.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        if(userService.checkSensitiveWords(user.getUserName())){
            return Result.fail(Constants.ACCOUNT_NOT_RULE);
        }
        if (VideoSystemEnums.IsOrNotType.IS.getIndex().equals(user.getIsTenantAdmin())
                && UserConstants.NOT_UNIQUE.equals(userService.checkTenantAdminUnique(user.getTenantId()))) {
            return Result.fail("该租户已存在租户管理员账号");
        }
        if (addUserRequest.getDeptId().equalsIgnoreCase("0")) {
            return Result.fail(DEPT_ERROR);
        }
        if ((user.getRoleIds() == null) || (user.getRoleIds().length == 0)) {
            throw new CustomException("角色不允许为空");
        }
        if (StringUtils.isNotBlank(user.getPhonenumber())) {
            user.setPhonenumber(RsaUtils.decryptByPrivateKey(user.getPhonenumber()));
        }
        if (StringUtils.isNotBlank(user.getTelPhone())) {
            user.setTelPhone(RsaUtils.decryptByPrivateKey(user.getTelPhone()));
        }
        return toAjax(userService.insertUser(user));
    }

    /**
     * 新增用户(视联feign接口专用)")
     */
    @ApiOperation(value = "新增用户(视联feign接口专用)")
    @TowerLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/addUser")
    public Result<Integer> addUser(@Validated @RequestBody AddUserRequest addUserRequest) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(addUserRequest, user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkTenantAdminUnique(user.getTenantId()))) {
            return Result.fail("该租户已存在租户管理员账号");
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return Result.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        if(userService.checkSensitiveWords(user.getUserName())){
            return Result.fail(Constants.ACCOUNT_NOT_RULE);
        }
        if (addUserRequest.getDeptId().equalsIgnoreCase("0")) {
            return Result.fail(DEPT_ERROR);
        }
        return toAjax(userService.insertVideoUser(user));
    }

    /**
     * 校验用户名是否存在
     *
     * @param userName 用户名
     * @return 1 存在 0 不存在
     */
    @ApiOperation(value = "校验用户名是否存在")
    @PostMapping("/checkUserNameIsExist")
    public AjaxResult checkUserNameIsExist(String userName) {
        return AjaxResult.success("操作成功", userService.checkUserNameUnique(userName));
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户")
    @TowerLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result<Integer> edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        if (user.getDeptId().equalsIgnoreCase("0")) {
            return Result.fail(DEPT_ERROR);
        }
        if ((user.getRoleIds() == null) || (user.getRoleIds().length == 0)) {
            throw new CustomException("角色不允许为空");
        }
        if (StringUtils.isNotBlank(user.getPhonenumber())) {
            user.setPhonenumber(RsaUtils.decryptByPrivateKey(user.getPhonenumber()));
        }
        if (StringUtils.isNotBlank(user.getTelPhone())) {
            user.setTelPhone(RsaUtils.decryptByPrivateKey(user.getTelPhone()));
        }
        return toAjax(userService.updateUser(user));
    }

    /**
     * 修改用户,视联使用
     */
    @ApiOperation(value = "修改用户,视联使用")
    @TowerLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateVideoUser")
    public Result<Integer> updateVideoUser(@Validated @RequestBody VideoUpdateUserRequest request) {
        logger.info("修改用户,视联使用->入参：{}", JSON.toJSONString(request));
        SysUser user = BeanUtil.copyProperties(request, SysUser.class);
        userService.checkUserAllowed(user);
        return toAjax(userService.updateVideoUser(user));
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户")
    @TowerLog(title = "用户管理", businessType = BusinessType.DELETE)
    @GetMapping("/del/{userIds}")
    public Result remove(@PathVariable String[] userIds) {
        if (!this.checkFlow(Arrays.asList(userIds))) {
            return Result.fail("当前用户下有未完成的待办事项，请处理完成后再进行删除。");
        }
        //add by liuxt 巡查任务需求 判断该用户是否为网格员
        String userId = Arrays.asList(userIds).get(ZERO_NUM).toString();
        GridKeeperVO gridKeeperVO = new GridKeeperVO();
        gridKeeperVO.setUserId(userId);
        GridKeeperVO returnGridKeeperVO = sysUserMapper.getKeeperInfoOnlyByUserId(gridKeeperVO);
        if (!ObjectUtils.isEmpty(returnGridKeeperVO)) {
            return Result.fail(NO_DELETE_INFO);
        }

        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码")
    @TowerLog(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public Result resetPwd(String userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        userService.checkUserAllowed(user);
        return toAjax(userService.resetPassword(userId));
    }

    /**
     * 状态修改
     */
    @ApiOperation(value = "状态修改")
    @TowerLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if ("1".equals(user.getStatus())) {
            if (!this.checkFlow(Arrays.asList(user.getUserId()))) {
                return Result.fail("当前用户下有未完成的待办事项，请处理完成后再进行停用。");
            }
            //add by liuxt 巡查任务需求 判断该用户是否为网格员
            String userId = user.getUserId();
            GridKeeperVO gridKeeperVO = new GridKeeperVO();
            gridKeeperVO.setUserId(userId);
            GridKeeperVO returnGridKeeperVO = sysUserMapper.getKeeperInfoOnlyByUserId(gridKeeperVO);
            if (!ObjectUtils.isEmpty(returnGridKeeperVO)) {
                return Result.fail(NO_STOP_INFO);
            }

        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户账号查询用户信息
     */
    @ApiOperation(value = "根据用户账号查询用户信息")
    @PostMapping("/selectUserByUserName")
    public Result<SysUser> selectUserByUserName(@RequestParam("userName") String userName) {
        return Result.data(userService.selectUserByUserName(userName));
    }

    /**
     * 通过用户名查询用户(不区分权限)
     */
    @ApiOperation(value = "根据用户账号查询用户信息")
    @GetMapping("/selectUserByUserName/{userName}")
    public Result<SysUser> selectUserOnlyByUserName(@PathVariable("userName") String userName) {
        return Result.data(userService.selectUserByUserNameNoAuth(userName));
    }

    /**
     * 查询所有岗位选项
     */
    @ApiOperation(value = "查询所有岗位选项")
    @GetMapping("/getPostOptions")
    public Result<List<SysPost>> getPostOptions() {
        return Result.data(postService.selectPostAll());
    }


    /**
     * 根据租户id查询用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/23
     * @version
     */
    @ApiOperation(value = "根据租户id查询用户信息")
    @GetMapping("/selectUserByTenantId/{tenantId}")
    public Result<List<SysUserResponse>> selectUserByTenantId(@PathVariable("tenantId") String tenantId) {
        List<SysUser> users = userService.selectUserByTenantId(tenantId);
//        //手机号脱敏处理（feign调用不加）
//        if (CollectionUtils.isNotEmpty(users)) {
//            users.forEach(x -> {
//                String phonenumber = x.getPhonenumber();
//                String encryptPhone = AESUtil.encrypt(phonenumber);
//                x.setPhonenumber(encryptPhone);
//                x.setAesPhoneNumber(encryptPhone);
//            });
//        }
        List<SysUserResponse> responses = BeanUtil.copyToList(users, SysUserResponse.class);
        return Result.data(responses);
    }

    /**
     * 根据租户id查询用户信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/23
     * @version
     */
    @ApiOperation(value = "根据租户id查询用户信息")
    @GetMapping("/selectUserListByTenantId")
    public Result<List<SysUserTenantInfoResponse>> selectUserListByTenantId(Integer pageNum, Integer pageSize) {
        List<SysUserTenantInfoResponse> users = userService.selectUserListByTenantId(pageNum, pageSize);
        return Result.data(users);
    }

    /**
     * 根据租户id查询用户信息总数量
     */
    @ApiOperation(value = "根据租户id查询用户信息总数量")
    @GetMapping("/selectUserListByTenantCount")
    public Result<Long> selectUserListByTenantCount() {
        Long usersCount = userService.selectUserListByTenantIdCount();
        return Result.data(usersCount);
    }

    /**
     * 查询用户列表
     */
    @ApiOperation(value = "查询用户列表")
    @PostMapping("/getUserList")
    public Result<List<SysUser>> getUserList(@RequestBody SysUser user) {
        List<SysUser> userList = userService.getUserList(user);
//        //手机号脱敏处理（feign调用不加）
//        if (CollectionUtils.isNotEmpty(userList)) {
//            userList.forEach(x -> {
//                String phonenumber = x.getPhonenumber();
//                String encryptPhone = AESUtil.encrypt(phonenumber);
//                x.setPhonenumber(encryptPhone);
//                x.setAesPhoneNumber(encryptPhone);
//            });
//        }
        return Result.data(userList);
    }

    /**
     * 查询用户列表
     */
    @ApiOperation(value = "查询用户列表")
    @PostMapping("/getUserListTable")
    public TableDataInfo getUserListTable(@RequestBody SysUser user) {
        startPage();
        List<SysUser> userList = userService.getUserListTable(user);
        return getDataTable(userList);
    }

    /**
     * 根据用户编码批量查询用户信息
     */
    @ApiOperation(value = "根据用户编码批量查询用户信息")
    @TowerLog(title = "根据用户编码批量查询用户信息", businessType = BusinessType.DELETE)
    @PostMapping("/getListByIds")
    public AjaxResult getListBatchIds(@RequestBody SysUser user) {
        return AjaxResult.success(userService.selectUserByIds(user.getUserIds()));
    }

    /**
     * 根据用户账号批量查询用户信息
     */
    @ApiOperation(value = "根据用户账号批量查询用户信息")
    @TowerLog(title = "根据用户账号批量查询用户信息")
    @PostMapping("/getListByUserNames")
    @Deprecated
    public AjaxResult getListByUserNames(@RequestBody SysUser user) {
        return AjaxResult.success(userService.getListByUserNames(user.getUserNames()));
    }

    /**
     * 获取登录账号数据权限
     */
    @ApiOperation(value = "获取登录账号数据权限")
    @PostMapping("/getPermissions")
    public Result<List<DeviceTreeVO>> getPermissions(@RequestBody SysUser user) {
        Result result = null;
        List<DeviceTreeVO> list = userService.getPermissions(user);
        result = Result.success("查询成功");
        result.setData(list);
        return result;
    }

    /**
     * 获取登录账号数据权限（返回LISTString）
     */
    @ApiOperation(value = "获取登录账号数据权限（返回LISTString）")
    @PostMapping("/getPermissionsForString")
    public Result getPermissionsForString(@RequestBody SysUser user) {
        List<String> list = userService.getPermissionsForString(user);
        return Result.data(list);
    }

    /**
     * 查询登录账号权限范围内的行政区划树
     */
    @ApiOperation(value = "查询登录账号权限范围内的行政区划树")
    @PostMapping("/getRegionTree")
    public Result<List<DeviceTreeVO>> getRegionTree(@RequestBody SysUser user) {
        Result result = null;
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        List<DeviceTreeVO> voList = userService.getRegionTree(sysUser);
        result = Result.success("查询成功");
        result.setData(voList);
        return result;
    }

    /**
     * 查询登录账号全部内的行政区划树
     */
    @ApiOperation(value = "查询登录账号全部内的行政区划树")
    @PostMapping("/getAreaByTenantId")
    public Result<List<AreaDataVO>> getAreaByTenantId(@RequestBody SysUser user) {
        Result result = null;
        List<AreaDataVO> voList = userService.getAreaByTenantId(user);
        result = Result.success("查询成功");
        result.setData(voList);
        return result;
    }

    /**
     * 根据行业编码获取区县人员的手机号码
     */
    @ApiOperation(value = "根据行业编码获取区县人员的手机号码")
    @PostMapping("/getPhoneByRegion")
    public Result<List<SysUser>> getPhoneByRegion(@RequestBody SysUser user) {
        Result result = null;
        List<SysUser> voList = userService.getPhoneByRegion(user);
        result = Result.success("查询成功");
        result.setData(voList);
        return result;
    }

    /**
     * 判断当前用户是否可以注销
     */
    @ApiOperation(value = "判断当前用户是否可以注销")
    @PostMapping("/getLogoutFlag")
    public AjaxResult getLogoutFlag() {
        AjaxResult result = null;
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        result = userService.getLogoutFlag(sysUser);

        return result;
    }

    private boolean checkFlow(List<String> userIds) {
        //TODO 需要流程那边提供接口校验该用户下是否有待办任务
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 根据设备编号获取有权限的用户列表
     */
    @ApiOperation(value = "根据设备编号获取有权限的用户列表")
    @PostMapping("/getUserByDeptAndDeviceScope")
    public Result<List<SysUser>> getUserByDeptAndDeviceScope(@RequestBody GetUserByDeptAndDeviceScopeRequest request) {
        List<SysUser> sysUsers = userService.getUserByDeptAndDeviceScope(request);
//        //手机号脱敏处理(feign调用不加)
//        if (CollectionUtils.isNotEmpty(sysUsers)) {
//            sysUsers.forEach(x -> {
//                String phonenumber = x.getPhonenumber();
//                String encryptPhone = AESUtil.encrypt(phonenumber);
//                x.setPhonenumber(encryptPhone);
//                x.setAesPhoneNumber(encryptPhone);
//            });
//        }
        return Result.data(sysUsers);
    }

    /**
     * 获取权限范围内的部门用户树
     */
    @ApiOperation(value = "获取权限范围内的部门用户树")
    @PostMapping("/getDeptUserTree")
    public Result<List<DeptUserTreeDto>> getDeptUserTree(@RequestBody(required = false) SysUser sysUser) {
        if (Objects.isNull(sysUser)) {
            SysUser sysUserNew = new SysUser();
            return Result.data(userService.getDeptUserTree(sysUserNew,1));
        }
        return Result.data(userService.getDeptUserTree(sysUser,1));
    }

    /**
     * 获取权限范围内的部门用户树(融合通信用)
     */
    @ApiOperation(value = "获取权限范围内的部门用户树")
    @PostMapping("/getDeptUserTreePeculiar")
    public Result<List<DeptUserGroupVO>> getDeptUserTreePeculiar(@RequestBody List<String> idList) {
        SysUser sysUserNew = new SysUser();
        sysUserNew.setUserIdList(idList);
        return Result.data(userService.getDeptUserTree(sysUserNew, 2));

    }


    /**
     * 获取权限范围内的部门用户树(融合通信用)
     */
    @ApiOperation(value = "获取权限范围内的部门用户树")
    @PostMapping("/getDeptUserTreeList")
    public Result<List<DeptUserGroupVO>> getDeptUserTreeList(@RequestBody SysUser sysUserNew) {
        sysUserNew.setStatus("0");
        return Result.data(userService.getDeptUserTree(sysUserNew, 3));

    }


    /**
     * 判断用户账号是否为锁定
     */
    @ApiOperation(value = "判断用户账号是否为锁定")
    @PostMapping("/checkUserLockStatus")
    public Result<Boolean> checkUserLockStatus(String userName) {
        //1、获取当前账号登录失败次数
        String loginFailNumStr = RedisUtil.get(AuthConstants.getLoginFailNumKey(userName), RedisDBType.PRIMARY);
        int loginFailNum = StrUtil.isNotBlank(loginFailNumStr) ? Integer.parseInt(loginFailNumStr) : 0;
        //2、判断登录失败次数，如果已经达到5次，则为锁定
        return Result.data(AuthConstants.isLoginCountLimit(loginFailNum));
    }


    /**
     * 判断用户是否需要更新密码
     */
    @ApiOperation(value = "判断用户是否需要更新密码")
    @PostMapping("/isNeedUpdate")
    public Result isNeedUpdate() {
        VerifyUpdateVO needUpdate = userService.isNeedUpdate();
        return Result.data(needUpdate);
    }

    /**
     * 强制退出
     *
     * @param userId   用户id
     * @param userName 用户账号
     */
    @ApiOperation(value = "强制退出")
    @PostMapping("/forceLogout")
    public Result forceLogOut(String userId, String userName) {
        userService.forceLogOut(userId, userName);
        return Result.success("success");
    }

    /**
     * 获取租户所有用户(租户+行业)
     */
    @GetMapping("/getTenantUsers")
    public Result<List<SysUser>> getTenantUsers() {
        List<SysUser> userList = userService.getTenantUsers(ZERO_NUM);
        return Result.data(userList);
    }

    /**
     * 获取租户所有用户(租户+行业),包括已经删除的用户
     */
    @GetMapping("/getTenantAllUsers")
    public Result<List<SysUser>> getTenantAllUsers() {
        List<SysUser> userList = userService.getTenantUsers(ONE);
        return Result.data(userList);
    }

    /**
     * 获取铁塔账号切换租户
     */
    @ApiOperation(value = " 获取铁塔账号切换租户")
    @PostMapping("/getTowerAccountSwitch")
    public Result<String> getTowerAccountSwitch(@RequestHeader("Authorization") String token) {
        String returnIndustryCode;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userType;
        LoginUser switchdLoginUser = loginUser.getSwitchdLoginUser();
        if (ObjectUtils.isEmpty(switchdLoginUser)) {
            userType = loginUser.getUser().getUserType();
        } else {
            userType = switchdLoginUser.getUser().getUserType();
        }
        if (!USER_TYPE_0.equals(userType)) {
            return Result.fail("当前账号非铁塔账号");
        }
        if (SecurityUtils.isSwitch()) {
            returnIndustryCode = loginUser.getUser().getIndustryCode();
        } else {
            returnIndustryCode = TOWER_INDUSTRY;
        }
        return Result.data(returnIndustryCode);
    }

    /**
     * 获取当前账号的行业编码
     */
    @ApiOperation(value = " 获取当前账号的行业编码")
    @PostMapping("/getAccountIndustry")
    public Result<String> getAccountIndustry() {
        String returnIndustryCode;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String industryCode = loginUser.getUser().getIndustryCode();
        String userType;
        LoginUser switchdLoginUser = loginUser.getSwitchdLoginUser();
        if (ObjectUtils.isEmpty(switchdLoginUser)) {
            userType = loginUser.getUser().getUserType();
        } else {
            userType = switchdLoginUser.getUser().getUserType();
        }
        if (!USER_TYPE_0.equals(userType)) {
            returnIndustryCode = industryCode;
        } else {
            returnIndustryCode = TOWER_INDUSTRY;
        }

        return Result.data(returnIndustryCode);
    }

    /**
     * 查询登录账号的管理区域
     */
    @ApiOperation(value = "查询登录账号的管理区域")
    @PostMapping("/getDeptStreetNew")
    public Result<List<DeptStreetVO>> getDeptStreetNew(@RequestBody DeptStreetVO deptStreetVO) {
        String[] areaLevelArr = {"2", "3", "4", "5"};
        List<String> areaLevelList = Arrays.asList(areaLevelArr);
        if (StringUtils.isEmpty(deptStreetVO.getAreaLevel()) || !areaLevelList.contains(deptStreetVO.getAreaLevel())) {
            return Result.fail("区域等级不能为空，并且值必须在2,3,4,5中");
        }
        if (StringUtils.isEmpty(deptStreetVO.getAreaParentCode())) {
            return Result.fail("父级编码不能为空");
        }
        List<DeptStreetVO> voList = userService.getRegionTreeNew(deptStreetVO);
        return Result.data(voList);
    }

    /**
     * 用户登录IP白名单弹窗设置用户是否校验白名单
     * @param user （userId, whiteIpFlag 0:不判断白名单，1：判断白名单）
     * @return
     */
    @ApiOperation(value = "用户登录IP白名单弹窗确定按钮")
    @PostMapping("/checkLoginIp")
    public Result<Integer> checkLoginIp(@RequestBody SysUser user) {
        try {
            userService.checkLoginIp(user);
            return Result.success(Constants.OPREATOR_SUCCESS);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户登录IP白名单弹窗确定按钮
     * @param whiteIpVO 包含三个key insertList, updateList, deleteList
     * @return
     */
    @ApiOperation(value = "用户登录IP白名单弹窗确定按钮")
    @PostMapping("/opreatorWhiteIps")
    public Result<Integer> opreatorWhiteIps(@RequestBody WhiteIpVO whiteIpVO) {
        logger.info("opreatorWhiteIp接口入参：{}", whiteIpVO.toString());
        try {
            whiteIpService.opreatorWhiteIps(whiteIpVO);
            return Result.success(Constants.OPREATOR_SUCCESS);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户登录IP白名单弹窗列表
     * @param whiteIp
     * @return
     */
    @ApiOperation(value = "用户登录IP白名单弹窗列表")
    @PostMapping("/pageWhiteIps")
    public Result<Page<WhiteIp>> pageWhiteIps(@RequestBody WhiteIp whiteIp) {
        logger.info("pageWhiteIps接口入参：{}", whiteIp.getUserId());
        Page<WhiteIp> page = whiteIpService.pageWhiteIps(whiteIp);
        return Result.data(page);
    }


    /**
     * 推送4a用户（客户）
     */
    @ApiOperation(value = "推送4a用户（客户）")
    @PostMapping(value = "/userPush4A")
    public Result<String> userPush4A(@RequestBody SysUser4A sysUser4A) {
        return userService.userPush4A(sysUser4A);
    }


    @ApiOperation(value = "批量根据组织id获取租户用户列表post方法")
    @PostMapping("/getUserListByDept")
    public TableDataInfo getUserListByDept(@RequestBody SysUser user) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        user.setTenantId(currentUser.getTenantId());
        user.setIndustryCode(currentUser.getIndustryCode());
        user.setAppVerCode(currentUser.getAppVerCode());
        List<SysUser> list = userService.getUserListByDept(user);
        return getDataTable(list);
    }

    @ApiOperation(value = "根据铁塔账号获取部门下的客户id（总部为空）")
    @GetMapping("/getTenantIdByTowerAccount")
    public Result getTenantIdByTowerAccount() {
        return Result.data(userService.getTenantIdByTowerAccount());
    }


    /**
     * 全域感知查询地区
     * @param deptStreetVO
     * @return
     */

    @PostMapping("/queryAreaWideList")
    public Result<AreaInfoResultVO> queryAreaWideList(@RequestBody DeptStreetVO deptStreetVO) {
        if (StringUtils.isEmpty(deptStreetVO.getAreaLevel())) {
            return Result.fail("区域信息不能为空");
        }
        AreaInfoResultVO areaInfoResultVO = userService.queryAreaWideList(deptStreetVO);
        return Result.data(areaInfoResultVO);
    }

    @PostMapping("/queryAreaByKeyWordList")
    public Result<List<DeptStreetVO>> queryAreaByKeyWordsList(@RequestBody DeptStreetVO deptStreetVO) {
        if (StringUtils.isEmpty(deptStreetVO.getKeyWords())) {
            return Result.fail("查询关键词不能为空");
        }
        List<DeptStreetVO> list = userService.queryAreaByKeyWordsList(deptStreetVO);
        return Result.data(list);
    }

    /**
     * 全域感知查询街道数据
     * @param deptStreetVO
     * @return
     */
    @PostMapping("/queryAreaStreetList")
    public Result<Integer> queryAreaStreetList(@RequestBody DeptStreetVO deptStreetVO) {
        Integer count = userService.queryAreaStreetList(deptStreetVO);
        return Result.data(count);
    }

}
