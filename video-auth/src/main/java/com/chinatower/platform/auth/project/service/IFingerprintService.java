package com.chinatower.platform.auth.project.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.project.entity.FingerprintVO;

/**
 * 指纹登录Service接口
 *
 * @author system
 * @date 2026-05-11
 */
public interface IFingerprintService {

    /**
     * 指纹登录
     */
    Result fingerprintLogin(FingerprintVO vo);

    /**
     * 开启/关闭指纹登录
     */
    Result toggleFingerprint(FingerprintVO vo);

    /**
     * 判断指纹登录按钮是否置灰
     */
    Result<FingerprintVO> isFingerprintButtonGray(FingerprintVO vo);

    /**
     * 判断是否弹出密码确认弹框
     */
    Result<FingerprintVO> isShowPasswordConfirmDialog(FingerprintVO vo);

    /**
     * 确认密码
     */
    Result<FingerprintVO> confirmPassword(FingerprintVO vo);


}