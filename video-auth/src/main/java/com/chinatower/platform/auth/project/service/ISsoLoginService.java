package com.chinatower.platform.auth.project.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.project.entity.dto.SsoSecretSyncDTO;
import com.chinatower.platform.auth.project.entity.dto.SsoSecretSyncResultVO;
import com.chinatower.platform.auth.project.entity.dto.XiaoTaSsoResultVO;

/**
 * 单点登录服务
 */
public interface ISsoLoginService {

    /**
     * 小塔单点登录
     * 流程：先调用4A平台统一凭证申请接口获取pwdaToken，再调用小塔SSO接口换取登录重定向地址
     *
     * @param username     登录账号
     * @param redirectCode 跳转页面编码，可空
     * @return 重定向地址
     */
    Result<XiaoTaSsoResultVO> loginXiaoTa(String username, String redirectCode);

    /**
     * SSO密钥同步（4A系统每月1日调用，推送新的DES密钥）
     *
     * @param dto 同步入参
     * @return 同步结果 {rsp, errDesc}
     */
    SsoSecretSyncResultVO syncSecret(SsoSecretSyncDTO dto);
}
