package com.chinatower.platform.project.common.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;

import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.utils.VerifyCodeUtils;
import com.chinatower.platform.framework.web.domain.AjaxResult;

import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.OperatorType;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * video-system
 * 验证码操作处理
 *
 * @author chinatower
 */
@RestController
public class CaptchaController {

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    @TowerLog(title = "验证码管理", operatorType = OperatorType.MANAGE)
    public AjaxResult getCode() throws IOException {
        // 生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = IdUtil.simpleUUID();
        // 唯一标识
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        RedisUtil.setEx(verifyKey, verifyCode, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
            AjaxResult ajax = AjaxResult.success();
            ajax.put("uuid", uuid);
            ajax.put("img", Base64.encode(stream.toByteArray()));
            ajax.put("time", new Date());
            return ajax;
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        } finally {
            stream.close();
        }
    }
}
