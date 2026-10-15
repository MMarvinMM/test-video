package com.chinatower.platform.auth.project.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.project.entity.FingerprintVO;
import com.chinatower.platform.auth.project.service.IFingerprintService;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * video-auth
 * 指纹登录控制器
 * @author system
 * @date 2026-05-11
 */
@Slf4j
@RestController
@RequestMapping("/fingerprint")
public class FingerprintController {

    @Autowired
    private IFingerprintService fingerprintService;

    /**
     * 指纹登录
     */
    @PostMapping("/fingerprintLogin")
    public Result fingerprintLogin(@RequestBody FingerprintVO vo) {
        log.info("指纹登录接口调用, username={}, deviceId={}, appId={}", vo.getUsername(), vo.getDeviceId(),vo.getAppId());
        return fingerprintService.fingerprintLogin(vo);
    }

    /**
     * 开启/关闭指纹登录
     */
    @PostMapping("/toggleFingerprint")
    public Result toggleFingerprint(@RequestBody FingerprintVO vo) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail("用户未登录");
        }
        String userId = loginUser.getUser().getUserId();
        vo.setUserId(userId);
        log.info("开关指纹登录接口调用, userId={}, deviceId={}, fingerprintEnabled={}", vo.getUserId(), vo.getDeviceId(), vo.getFingerprintEnabled());
        return fingerprintService.toggleFingerprint(vo);
    }

    /**
     * 指纹登录按钮是否置灰
     */
    @PostMapping("/isFingerprintButtonGray")
    public Result<FingerprintVO> isFingerprintButtonGray(@RequestBody FingerprintVO vo) {
        log.info("指纹登录按钮置灰查询接口调用, username={}, deviceId={}", vo.getUsername(), vo.getDeviceId());
        return fingerprintService.isFingerprintButtonGray(vo);
    }

    /**
     * 是否弹出密码确认弹框
     */
    @PostMapping("/isShowPasswordConfirmDialog")
    public Result<FingerprintVO> isShowPasswordConfirmDialog(@RequestBody FingerprintVO vo) {
        log.info("是否弹出密码确认弹框接口调用, username={}, deviceId={}", vo.getUsername(), vo.getDeviceId());
        return fingerprintService.isShowPasswordConfirmDialog(vo);
    }

    /**
     * 确认密码
     */
    @PostMapping("/confirmPassword")
    public Result<FingerprintVO> confirmPassword(@RequestBody FingerprintVO vo) {
        log.info("确认密码接口调用, username={}, deviceId={}", vo.getUsername(), vo.getDeviceId());
        return fingerprintService.confirmPassword(vo);
    }

}