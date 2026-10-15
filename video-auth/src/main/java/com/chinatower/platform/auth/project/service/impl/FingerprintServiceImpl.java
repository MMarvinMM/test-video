package com.chinatower.platform.auth.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.auth.common.constant.MessageConstant;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.auth.project.entity.FingerprintVO;
import com.chinatower.platform.auth.project.mapper.FingerprintMapper;
import com.chinatower.platform.auth.project.service.IFingerprintService;
import com.chinatower.platform.auth.project.service.ISysUserService;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.common.utils.security.RsaUtils;
import com.chinatower.platform.constants.AuthConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 指纹登录Service实现类
 *
 * @author system
 * @date 2026-05-11
 */
@Slf4j
@Service
public class FingerprintServiceImpl implements IFingerprintService {

    @Autowired
    private FingerprintMapper fingerprintMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    private static final String FINGERPRINT_PWD_FAIL_KEY = "fingerprint:pwd:fail:";
    private static final int MAX_FAIL_COUNT = 5;

    @Override
    public Result fingerprintLogin(FingerprintVO vo) {
        // 共通校验
        Result<FingerprintVO> checkResult = checkCommon(vo);
        if (checkResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return checkResult;
        }
        FingerprintVO checkedData = checkResult.getData();
        String oriAppId = vo.getAppId();
        if (StrUtil.isBlank(oriAppId)) {
            return Result.fail("appId不能为空");
        }
        String appId = RsaUtils.decryptByPrivateKey(oriAppId);
        if(appId.equals(oriAppId) ){//如果解密后和原来一致，代表解密失败
            return Result.fail("解密失败");
        }
        // 查询指纹登录信息
        FingerprintVO fingerprintInfo = fingerprintMapper.selectFingerprintInfo(checkedData.getUserId(), checkedData.getDeviceId());
        Integer enabled = null;
        if (fingerprintInfo != null) {
            enabled = fingerprintInfo.getFingerprintEnabled();
        }
        if (enabled == null || enabled != 1) {
            return Result.fail("指纹登录未开启，请先开启指纹登录");
        }
        String password = checkedData.getPassword();
        log.info("开始生成token, username={}, appId={}", checkedData.getUsername(), appId);
        return userService.prepareToken(checkedData.getUsername(), password, appId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result toggleFingerprint(FingerprintVO vo) {
        String userId = vo.getUserId();
        String deviceId = vo.getDeviceId();
        Integer fingerprintEnabled = vo.getFingerprintEnabled();
        if (StrUtil.isBlank(userId)) {
            return Result.fail("用户ID不能为空");
        }
        if (StrUtil.isBlank(deviceId)) {
            return Result.fail("设备ID不能为空");
        }
        if (fingerprintEnabled == null) {
            return Result.fail("开关状态不能为空");
        }
        vo.setCreator(SecurityUtils.getOperator());
        // 一条SQL搞定，原子操作
        int rows = fingerprintMapper.upsertFingerprintEnabled(vo);
        if (rows > 0) {
            String message = fingerprintEnabled == 1 ? "指纹登录已启用" : "指纹登录已关闭";
            return Result.success(message);
        }
        return Result.fail("操作失败");
    }

    @Override
    public Result<FingerprintVO> isFingerprintButtonGray(FingerprintVO vo) {
        // 共通校验
        Result<FingerprintVO> checkResult = checkCommon(vo);
        if (checkResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return checkResult;
        }
        FingerprintVO checkedData = checkResult.getData();
        // 查询Redis中的密码输入失败次数
        String failKey = FINGERPRINT_PWD_FAIL_KEY + checkedData.getDeviceId() + ":" + checkedData.getUserId();
        Integer failCount = RedisUtil.get(failKey, Integer.class, RedisDBType.PRIMARY);
        if (failCount == null) {
            failCount = 0;
        }
        FingerprintVO result = new FingerprintVO();
        boolean ifGray = failCount >= MAX_FAIL_COUNT;
        result.setIfGray(ifGray);
        return Result.data(result);
    }

    @Override
    public Result<FingerprintVO> isShowPasswordConfirmDialog(FingerprintVO vo) {
        // 共通校验
        Result<FingerprintVO> checkResult = checkCommon(vo);
        if (checkResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return checkResult;
        }
        FingerprintVO checkedData = checkResult.getData();
        // 获取密码修改时间
        Date pwdUpdateTime = checkedData.getPwdUpdateTime();
        FingerprintVO result = new FingerprintVO();
        // 如果没有密码修改时间，默认不需要弹框
        if (pwdUpdateTime == null) {
            result.setIfShowDialog(false);
            return Result.data(result);
        }
        // 查询指纹登录信息（开关状态和开启时间）
        FingerprintVO fingerprintInfo = fingerprintMapper.selectFingerprintInfo(checkedData.getUserId(), checkedData.getDeviceId());
        Date fingerprintEnabledTime = null;
        if (fingerprintInfo != null) {
            fingerprintEnabledTime = fingerprintInfo.getEnabledTime();
        }
        boolean ifShowDialog = false;
        // 如果密码修改时间大于指纹开启时间，则弹出确认弹框。指纹从未开启过，但密码修改过，和app确认不存在这种情况。
        if (fingerprintEnabledTime != null) {
            ifShowDialog = pwdUpdateTime.after(fingerprintEnabledTime);
        }
        result.setIfShowDialog(ifShowDialog);
        return Result.data(result);
    }

    @Override
    public Result<FingerprintVO> confirmPassword(FingerprintVO vo) {
        // 共通校验
        Result<FingerprintVO> checkResult = checkCommon(vo);
        if (checkResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return checkResult;
        }
        FingerprintVO checkedData = checkResult.getData();
        String oriPassword = vo.getPassword();
        if (StrUtil.isBlank(oriPassword)) {
            return Result.fail("密码不能为空");
        }
        String password = RsaUtils.decryptByPrivateKey(oriPassword);
        if(password.equals(oriPassword)){//如果解密后和原来一致，代表解密失败
            return Result.fail("解密失败");
        }
        String failKey = FINGERPRINT_PWD_FAIL_KEY + checkedData.getDeviceId() + ":" + checkedData.getUserId();
        // 获取当前失败次数
        Integer failCount = RedisUtil.get(failKey, Integer.class, RedisDBType.PRIMARY);
        if (failCount == null) {
            failCount = 0;
        }
        FingerprintVO result = new FingerprintVO();
        // 检查是否已经达到最大失败次数
        if (failCount >= MAX_FAIL_COUNT) {
            result.setFlag(Constants.TWO);
            return Result.data(result);
        }
        // 校验密码
        boolean isPasswordCorrect = password.equals(checkedData.getPassword());
        if (isPasswordCorrect) {
            // 密码正确，清除失败次数记录
            RedisUtil.delete(failKey, RedisDBType.PRIMARY);
            // 更新指纹开启时间
            fingerprintMapper.updateEnabledTime(checkedData.getUserId(), checkedData.getDeviceId());
            result.setFlag(Constants.ONE);
            return Result.data(result);
        } else {
            // 密码错误，增加失败次数
            failCount++;
            int fingerPrintLockSeconds = 1800;
            AjaxResult ajaxResult = systemFeignClient.getConfValue(OauthConstants.FINGER_PRINT_LOCK_SECONDS);
            if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONBytes(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    fingerPrintLockSeconds = Integer.valueOf(sysConfig.getConfigValue());
                }
            }
            RedisUtil.set(failKey, failCount, fingerPrintLockSeconds, RedisDBType.PRIMARY);
            // 判断是否达到5次，决定返回flag
            if (failCount >= MAX_FAIL_COUNT) {
                result.setFlag(Constants.TWO);  // 达到5次，锁定
            } else {
                result.setFlag(Constants.THREE);  // 未达到5次
            }
            return Result.data(result);
        }
    }


    /**
     * 共通校验方法：解密并校验用户名和设备ID，返回包含解密后数据的FingerprintVO
     *
     * @param vo 请求参数
     * @return Result，如果成功则data为包含解密后deviceId的FingerprintVO，失败则返回错误Result
     */
    private Result<FingerprintVO> checkCommon(FingerprintVO vo) {
        String oriUsername = vo.getUsername();
        String oriDeviceId = vo.getDeviceId();
        if (StrUtil.isBlank(oriUsername)) {
            return Result.fail("用户名不能为空");
        }
        if (StrUtil.isBlank(oriDeviceId)) {
            return Result.fail("设备ID不能为空");
        }
        // 解密用户名和设备ID
        String username = RsaUtils.decryptByPrivateKey(oriUsername);
        String deviceId = RsaUtils.decryptByPrivateKey(oriDeviceId);
        if (username.equals(oriUsername) || deviceId.equals(oriDeviceId)) {
            return Result.fail("解密失败");
        }
        // 根据用户名获取用户信息
        SysUser targetUser = userService.selectUserByUserName(username);
        if (targetUser == null) {
            log.info("用户不存在: {}", username);
            return Result.fail(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if (Constants.ONE.equals(targetUser.getStatus())) {
            return Result.fail(MessageConstant.ACCOUNT_DISABLED);
        }
        // 构建返回的FingerprintVO，包含解密后的数据
        FingerprintVO result = new FingerprintVO();
        result.setDeviceId(deviceId);
        result.setUsername(username);
        result.setUserId(targetUser.getUserId());
        result.setPwdUpdateTime(targetUser.getPwdUpdateTime());
        result.setPassword(SecurityUtils.decryptPassword(targetUser.getPassword()));
        return Result.data(result);
    }


}