package com.chinatower.platform.project.system.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.UserConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.platform.api.dto.SysOperLogDTO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.SysFileTypeUtil;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.constants.AuthConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.SmsResultCode;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.project.system.constants.CacheConstants;
import com.chinatower.platform.project.system.domain.dto.UserProfileUpdateDTO;
import com.chinatower.platform.project.system.domain.dto.UserQueryDTO;
import com.chinatower.platform.project.system.mapper.SysUserMapper;
import com.chinatower.platform.project.system.service.ISysUserService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.WxFeignClient;
import com.chinatower.system.api.model.WxLogoutVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * video-system
 * 个人信息 业务处理
 *
 * @author iwhalecloud
 */
@Api(tags = "个人信息业务处理")
@RestController
@RequestMapping("/base/system/user/profile")
@Slf4j
public class SysProfileController extends TowerBaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private LogFeign logFeign;

    @Resource
    private WxFeignClient wxFeignClient;

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private MsgFeignClient msgFeignClient;


    private static final String MSG_EXCEPTION = "上传图片异常，请联系管理员";

    private static final String IMG_NULL = "上传图片不能为空";

    /**
     * 个人信息
     */
    @ApiOperation(value = "个人信息")
    @GetMapping
    public AjaxResult profile() {
        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        String username = loginUser.getUserName();
        SysUser user = userService.selectUserByUserName(username);
        if (Objects.isNull(user)) {
            log.error("个人信息->个人信息查询用户为空，username->{}", username);
            //兼容处理，保证返回基本用户信息，会导致返回数据的roleGroup有问题
            user = userService.selectUserOnlyByUserName(username);
        }
        try {
            //保证查询行业正确
            user.setIndustryCode(loginUser.getIndustryCode());
            user.setAppVerCode(loginUser.getAppVerCode());
            if(!StringUtils.isEmpty(user.getAvatar())){
                Result<String> obsFileUrlEncrypt = fileFeignClient.getObsFileUrlEncrypt(user.getAvatar());
                if (obsFileUrlEncrypt != null) {
                    user.setAvatar(obsFileUrlEncrypt.getData());
                }
            }
        } catch (Exception e) {
            log.error("处理用户头像加密失败，user->{}",JSON.toJSONString(user), e);
        }
//        user.setAvatar(obsFileUrlEncryptUtil.dealObsFileUrl(user.getAvatar()));
        AjaxResult ajax = AjaxResult.success(user);
        long s = System.currentTimeMillis();
        ajax.put("roleGroup", userService.selectUserRoleGroup(user));
        log.info("个人信息->userService.selectUserRoleGroup耗时:{}", System.currentTimeMillis() - s);
        ajax.put("postGroup", userService.selectUserPostGroup(username));
        return ajax;
    }

    /**
     * 验证码校验
     */
    @ApiOperation(value = "验证码校验")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "手机号码", name = "phonenumber", dataType = "string", required = true),
            @ApiImplicitParam(value = "验证码", name = "code", dataType = "string", required = true)
    })
    @GetMapping(value = "/checkPhoneCode")
    public Result checkPhoneCode(@RequestParam("phonenumber") @Valid @NotBlank(message = "手机号码不能为空") String phonenumber,
                                 @RequestParam("code") @Valid @NotBlank(message = "验证码不能为空") String code) {

        phonenumber = AESUtil.decryptBase64(phonenumber);
        Result result = msgFeignClient.checkPhoneCode(phonenumber, code, "", true);
        if (result.getCode() == ResultCode.SUCCESS.getCode()) {
            String key = CacheConstants.getCodeKey(phonenumber);
            RedisUtil.setEx(key, phonenumber, 5, TimeUnit.MINUTES, RedisDBType.PRIMARY);
            return Result.data(key);
        }
        if (result.getCode() == SmsResultCode.VERIFY_ERROR.getCode()) {
            return Result.fail("输入的验证码不正确");
        }
        return result;
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户")
    @TowerLog(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result updateProfile(@RequestBody UserProfileUpdateDTO dto) {

        SysUser user1 = SecurityUtils.getLoginUser().getUser();
        if(!user1.getUserId().equals(dto.getUserId())){//确保/admin/base/system/user/profile接口，后续若有改动，不能影响其返回userId
            return toAjax(false);
        }
        SysUser updateUser = new SysUser();
        updateUser.setUserId(user1.getUserId());
        String telPhone = dto.getTelPhone();
        String email = dto.getEmail();
        String sex = dto.getSex();
        String phoneEditCode = dto.getPhoneEditCode();
        if (Boolean.TRUE.equals(StringUtils.isNotBlank(phoneEditCode) && RedisUtil.hasKey(phoneEditCode, RedisDBType.PRIMARY)) && RedisUtil.get(phoneEditCode, RedisDBType.PRIMARY).equals(dto.getPhonenumber())) {
            String phoneNumber = dto.getPhonenumber();
            //如果修改了手机号，需要校验新号码与旧号码是否一致，一致返回“新号码与旧号码一致，请修改！”
            UserQueryDTO userQueryDTO = new UserQueryDTO();
            userQueryDTO.setUserId(user1.getUserId());
            String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
            userQueryDTO.setPlatformCode(platformCode);
            SysUser user = userMapper.selectUserById(userQueryDTO);

            if(!ObjectUtils.isEmpty(user)){
                if(StringUtils.isNotEmpty(phoneNumber) && StringUtils.isNotEmpty(user.getPhonenumber()) && phoneNumber.equals(user.getPhonenumber())){
                    return Result.fail("新号码与旧号码一致，请修改！");
                }
            }
            updateUser.setPhonenumber(phoneNumber);
            RedisUtil.delete(phoneEditCode, RedisDBType.PRIMARY);
        }
        updateUser.setTelPhone(telPhone);
        updateUser.setEmail(email);
        updateUser.setSex(sex);
        return toAjax(userService.updateUserProfile(updateUser));
    }

    /**
     * 修改密码
     */
    @ApiOperation(value = "修改密码")
    @TowerLog(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody HashMap<String, String> paramMap) throws Exception {
        String oldPassword = paramMap.get("oldPassword");
        String newPassword = paramMap.get("newPassword");
        if(StrUtil.isBlank(oldPassword)){
            return Result.fail("修改密码失败，旧密码不能为空");
        }
        if(StrUtil.isBlank(newPassword)){
            return Result.fail("修改密码失败，新密码不能为空");
        }
        oldPassword = RsaUtils.decryptByPrivateKey(oldPassword);
        newPassword = RsaUtils.decryptByPrivateKey(newPassword);
        String userName = SecurityUtils.getUsername();
        SysUser user = userService.selectUserOnlyByUserName(userName);
        String password = user.getPassword();
        String loginFailNumKey = AuthConstants.getLoginFailNumKey(SecurityUtils.getUsername());
        String loginFailNumStr = RedisUtil.get(loginFailNumKey, RedisDBType.PRIMARY);
        int loginFailNum = StrUtil.isNotBlank(loginFailNumStr) ? Integer.parseInt(loginFailNumStr) : 0;
        //2、判断登录失败次数，如果已经达到5次，则禁止登录直接返回错误
        if (AuthConstants.isLoginCountLimit(loginFailNum)){
            return Result.fail(AuthConstants.LOGIN_COUNT_MOREERROR_MSG);
        }
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            loginFailNum = loginFailNum + 1;
            RedisUtil.set(loginFailNumKey, loginFailNum + "", AuthConstants.LOGIN_COUNT_DURATION_SECONDS, RedisDBType.PRIMARY);
            String msg = AuthConstants.isLoginCountLimit(loginFailNum)? AuthConstants.LOGIN_COUNT_ERROR_MSG : AuthConstants.getLoginCountMsg(loginFailNum);
            throw new CustomException(msg);
        }
        // 判断不能设置初始密码
        if(UserConstants.PASSWORD.equals(newPassword)){
            return Result.fail("密码不规范");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return Result.fail("新密码不能与旧密码相同");
        }
        return toAjax(userService.updateUserPassword(user.getUserId(), SecurityUtils.encryptPassword(newPassword)));
    }

    /**
     * 首次重置密码
     */
    @ApiOperation(value = "首次重置密码")
    @TowerLog(title = "首次重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/updatePwdFirst")
    public Result updatePwdFirst(@RequestBody HashMap<String, String> paramMap) throws Exception {
        String newPassword = paramMap.get("newPassword");

        if(StrUtil.isBlank(newPassword)){
            return Result.fail("修改密码失败，新密码不能为空");
        }
        String decNewPassword = RsaUtils.decryptByPrivateKey(newPassword);
        if(newPassword.equals(decNewPassword)){//如果密码解密后和原密码一致，代表加密失败，则返回空字符串
            return Result.fail("新密码加密失败");
        }
        newPassword = decNewPassword;
        String userName = SecurityUtils.getUsername();
        SysUser user = userService.selectUserOnlyByUserName(userName);
        WxLogoutVo wxLogoutVo = new WxLogoutVo();
        wxLogoutVo.setUserName(userName);
        wxLogoutVo.setUserId(user.getUserId());
        String password = user.getPassword();
        // 判断不能设置初始密码
        if(UserConstants.PASSWORD.equals(newPassword)){
            return Result.fail("密码不规范");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return Result.fail("新密码不能与旧密码相同");
        }
        int i = userService.updateUserPassword(user.getUserId(), SecurityUtils.encryptPassword(newPassword));
        return toAjax(i);
    }

    /**
     * 获取最后更新时间
     */
    @ApiOperation(value = "获取最后更新时间")
    @PostMapping("/getLastupdateTime")
    public Result getLastupdateTime() throws Exception {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysOperLogDTO dto = new SysOperLogDTO();
        dto.setPageNum(1);
        dto.setPageSize(1);
        dto.setOperName(user.getUserName());
        dto.setTitle("重置密码");
        TableDataInfo list = logFeign.list(dto);
        if(list == null || list.getRows() == null || list.getRows().size() == 0){
            return Result.data(null);
        }
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(list.getRows().get(0)));
        return Result.data(jsonObject.getString("operTime"));
    }

    @Resource
    FileFeignClient fileFeignClient;


    /**
     * 头像上传
     */
    @ApiOperation(value = "头像上传")
    @TowerLog(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String type = SysFileTypeUtil.getType(file.getInputStream());
            String contentType = file.getContentType();
            if((StringUtils.isNotEmpty(contentType) && !contentType.contains("image")) || ObjectUtils.isEmpty(type)){
                return AjaxResult.error("上传头像只能是图片格式!");
            }
            long size = file.getSize();
            if(size > 2 * 1024 * 1024){
                return AjaxResult.error("上传头像图片大小不能超过 2MB!");
            }
            return uploadAvatar(file);
        }else{
            return AjaxResult.error(IMG_NULL);
        }
    }


    /**
     * 头像上传
     */
    @ApiOperation(value = "头像上传")
    @TowerLog(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatarForLanXin")
    public AjaxResult avatarForLanXin(MultipartFile file) throws IOException {
        log.info("avatarForLanXin开始"+file);
        if (!file.isEmpty()) {
            return uploadAvatar(file);
        }else{
            return AjaxResult.error(IMG_NULL);
        }
    }



    /**
     * 头像上传共通方法
     */
    public AjaxResult uploadAvatar(MultipartFile file) throws IOException {
        log.info("头像上传开始");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String avatar= null;
        String url= null; //加密后的URL
        try {
            log.info("调用uploadFile开始");
//                avatar = obsClientUtil.uploadFile(file);
            com.chinatower.common.core.web.domain.AjaxResult ajaxResult = fileFeignClient.uploadFile(file);
            if (ajaxResult != null) {
                log.info("ajaxResult"+ajaxResult);
                if (ajaxResult.ok()) {
                    com.chinatower.file.vo.SysFileInfo sysFileInfo = ajaxResult.parseData(com.chinatower.file.vo.SysFileInfo.class);
                    avatar = sysFileInfo.getOriginUrl();//未加密存数据库
                    url = sysFileInfo.getFileUrl();//加密返回前台
                }else{
                    return AjaxResult.error(String.valueOf(ajaxResult.get(AjaxResult.MSG_TAG)));
                }
            }
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return AjaxResult.error(e.getMessage());
        }
        if (userService.updateUserAvatar(loginUser.getUsername(), avatar)) {
            AjaxResult ajax = AjaxResult.success();
            // 处理Obs文件URL加密返回
//                avatar = obsFileUrlEncryptDeal.dealObsFileUrl(avatar);
            ajax.put("imgUrl", url);
            loginUser.getUser().setAvatar(url);
//                tokenService.setLoginUser(loginUser);
            return ajax;
        }

        return AjaxResult.error(MSG_EXCEPTION);
    }
}
