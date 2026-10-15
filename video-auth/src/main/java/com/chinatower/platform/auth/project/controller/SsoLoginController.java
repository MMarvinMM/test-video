package com.chinatower.platform.auth.project.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.project.entity.dto.SsoSecretSyncDTO;
import com.chinatower.platform.auth.project.entity.dto.SsoSecretSyncResultVO;
import com.chinatower.platform.auth.project.entity.dto.XiaoTaSsoLoginDTO;
import com.chinatower.platform.auth.project.service.ISsoLoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * video-auth
 * 功能说明: 单点登录相关接口
 */
@RestController
public class SsoLoginController {

    @Resource
    private ISsoLoginService xiaoTaSsoService;

    /**
     * 小塔单点登录
     * 流程：先调用4A平台统一凭证申请接口获取pwdaToken，再调用小塔SSO接口换取登录重定向地址
     *
     * @param vo 用户名 + redirectCode(可空)
     * @return 重定向地址
     */
    @PostMapping("/sso/xiaoTaLogin")
    public Result xiaoTaLogin(@RequestBody XiaoTaSsoLoginDTO vo) {
        return xiaoTaSsoService.loginXiaoTa(vo.getUsername(), vo.getRedirectCode());
    }

    /**
     * 4A系统密钥同步接口
     * 4A系统每月1日调用，推送新的DES密钥
     *
     * @param dto 入参
     * @return 同步结果 {rsp, errDesc}
     */
    @PostMapping("/sso/appIdAndSecretSyncToTongyong")
    public SsoSecretSyncResultVO appIdAndSecretSync(@RequestBody SsoSecretSyncDTO dto) {
        return xiaoTaSsoService.syncSecret(dto);
    }
}
