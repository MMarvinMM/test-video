package com.chinatower.platform.project.numtwin.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * AesCryptoUtils 单元测试，覆盖基本加解密与随机数生成逻辑。
 */
public class AesCryptoUtilsTest {

    @Test
    public void shouldEncryptAndDecrypt() throws Exception {
        // 使用自定义密钥与向量，验证加解密闭环
        String key = "123456789012345678901234";
        String iv = "1234567890abcdef";
        String plain = "hello-world";

        String encrypted = AesCryptoUtils.encodeAes(plain, key, iv);
        assertNotNull("加密结果不应为空", encrypted);

        String decrypted = AesCryptoUtils.decodeAesString(encrypted, key, iv);
        assertEquals("解密结果应还原明文", plain, decrypted);
    }

    @Test
    public void generateCryptoJSKey_shouldReturnNumericWithLength() {
        int length = 8;
        String generated = AesCryptoUtils.generateCryptoJSKey(length);

        assertNotNull("生成的随机串不应为空", generated);
        assertEquals("长度应符合入参", length, generated.length());
        assertTrue("应全部为数字", generated.matches("\\d+"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeAesString_invalidInput_shouldThrow() throws Exception {
        // 非法的 Base64 字符串应触发异常
        String key = "123456789012345678901234";
        String iv = "1234567890abcdef";
        AesCryptoUtils.decodeAesString("not-base64", key, iv);
    }
}
