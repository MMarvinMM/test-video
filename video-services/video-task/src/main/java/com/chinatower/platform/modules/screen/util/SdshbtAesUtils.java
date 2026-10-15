package com.chinatower.platform.modules.screen.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * sdshbt 登录 AES 加密，与前端 CryptoJS（keyone + iv=key）一致。
 * <p>
 * 逻辑与 {@code com.chinatower.platform.utils.AesUtils}（test(1).java）保持一致。
 * </p>
 */
@Component
public class SdshbtAesUtils {

    @Value("${sdshbt.security.algorithm}")
    private String algorithm;

    /**
     * AES 加密：明文为 content + token，密钥与 IV 均使用 secretKey。
     * <p>
     * IV 固定为 keyone 字节（与 sdshbt 前端 CryptoJS {@code iv: key} 一致），
     * 不可改为 {@link java.security.SecureRandom} 生成，否则服务端无法解密。
     * </p>
     *
     * @param content   用户名或密码
     * @param token     从 /token.do 获取的临时 token
     * @param secretKey 16 字节 AES 密钥（前端 keyone）
     * @return Base64 密文
     */
    public String encrypt(String content, String token, String secretKey) throws Exception {
        String plainText = content + token;
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        // sdshbt 第三方协议要求 IV=keyone，非通用场景下的固定 IV
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes); // NOSONAR

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec); // NOSONAR

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
