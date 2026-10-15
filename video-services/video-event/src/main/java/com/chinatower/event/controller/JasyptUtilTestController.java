package com.chinatower.event.controller;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.entity.dto.usertree.OrderInfoUserTreeReq;
import com.chinatower.event.entity.vo.SysInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-event
 */
@RestController
@Slf4j
public class JasyptUtilTestController {


    @Autowired
    private StringEncryptor stringEncryptor;


    /**
     * 功能描述：根据流程ID，租户ID获取用户树(app专用)
     *
     * @version:
     */
    @PostMapping("/test/jasyptUtilTest")
    public void jasyptUtilTest(String original, String ciphertext) {
        //加密字段
        //针对3456进行加密
        if (StrUtil.isNotBlank(original)) {
            String encrypt = stringEncryptor.encrypt(original);
            log.info("加密前:{},加密后:{}", original, encrypt);
        }
        if (StrUtil.isNotBlank(ciphertext)) {
            String decrypt = stringEncryptor.decrypt(ciphertext);
            log.info("解密前:{},解密后:{}", ciphertext, decrypt);
        }
    }

}
