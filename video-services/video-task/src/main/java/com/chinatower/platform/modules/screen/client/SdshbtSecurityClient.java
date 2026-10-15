package com.chinatower.platform.modules.screen.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chinatower.platform.modules.screen.config.SdshbtSecurityProperties;
import com.chinatower.platform.modules.screen.util.SdshbtAesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * sdshbt 安全接口客户端：获取临时 token、AES 加密登录
 */
@Component
@Slf4j
public class SdshbtSecurityClient {

    @Resource
    private SdshbtSecurityProperties properties;

    @Resource
    private SdshbtAesUtils sdshbtAesUtils;

    /**
     * 登录并获取 access token 与 refresh token
     */
    public SdshbtTokenResult login() {
        if (StrUtil.isBlank(properties.getUsername()) || StrUtil.isBlank(properties.getPassword())) {
            log.error("sdshbt 登录凭证未配置或解密失败，请检查 Nacos 中 sdshbt.security.username/password（Jasypt ENC 须与 bootstrap.yml 中 algorithm/password 一致）");
            return null;
        }
        if (StrUtil.isBlank(properties.getBaseUrl()) || StrUtil.isBlank(properties.getAesKey())) {
            log.error("sdshbt 接口地址或 AES 密钥未配置");
            return null;
        }
        if (properties.getAesKey().startsWith("ENC(")) {
            log.error("sdshbt AES 密钥仍为 ENC 密文，Jasypt 解密失败，请检查 jasypt.encryptor 配置或使用明文 aes-key");
            return null;
        }

        String cToken = getToken();
        if (StrUtil.isBlank(cToken)) {
            log.error("sdshbt 获取临时 token 失败");
            return null;
        }

        try {
            String aesKey = properties.getAesKey();
            String encryptedUsername = sdshbtAesUtils.encrypt(properties.getUsername(), cToken, aesKey);
            String encryptedPassword = sdshbtAesUtils.encrypt(properties.getPassword(), cToken, aesKey);
            JSONObject loginResp = doLogin(encryptedUsername, encryptedPassword, cToken);
            if (loginResp == null) {
                return null;
            }
            JSONObject body = loginResp.getJSONObject("body");
            if (body == null) {
                log.error("sdshbt 登录响应缺少 body: {}", loginResp);
                return null;
            }
            String token = body.getStr("token");
            String refreshToken = body.getStr("refreshToken");
            if (StrUtil.isBlank(token) || StrUtil.isBlank(refreshToken)) {
                log.error("sdshbt 登录响应缺少 token 或 refreshToken: {}", body);
                return null;
            }
            log.info("sdshbt 登录成功");
            return new SdshbtTokenResult(token, refreshToken);
        } catch (Exception e) {
            log.error("sdshbt 登录异常", e);
            return null;
        }
    }

    private String getToken() {
        String url = properties.getBaseUrl() + "/token.do";
        try (HttpResponse response = HttpRequest.get(url).timeout(properties.getConnectTimeout()).execute()) {
            if (!response.isOk()) {
                log.error("sdshbt token.do 响应异常, status={}, body={}", response.getStatus(), response.body());
                return null;
            }
            JSONObject json = JSONUtil.parseObj(response.body());
            return json.getStr("body");
        } catch (Exception e) {
            log.error("sdshbt token.do 请求异常", e);
            return null;
        }
    }

    private JSONObject doLogin(String encryptUser, String encryptPwd, String cToken) {
        String url = properties.getBaseUrl() + "/login-processing";
        try (HttpResponse response = HttpRequest.post(url)
                .form("j_username", encryptUser)
                .form("j_password", encryptPwd)
                .form("c_token", cToken)
                .form("isVerifyCode", false)
                .timeout(properties.getConnectTimeout())
                .execute()) {
            if (!response.isOk()) {
                log.error("sdshbt login-processing 响应异常, status={}, body={}", response.getStatus(), response.body());
                return null;
            }
            return JSONUtil.parseObj(response.body());
        } catch (Exception e) {
            log.error("sdshbt login-processing 请求异常", e);
            return null;
        }
    }
}
