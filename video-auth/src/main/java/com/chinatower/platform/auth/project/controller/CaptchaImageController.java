package com.chinatower.platform.auth.project.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.auth.common.constant.OauthConstants;
import com.chinatower.platform.common.utils.VerifyCodeUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * video-auth
 * @Description: 验证码
 * @Author: 张帅
 * @Date: 2021/11/24 17:30
 **/
@RestController
@Slf4j
public class CaptchaImageController {

    @RequestMapping(value = "/captchaImage",method = {RequestMethod.GET})
    public Result captchaImage(){
        Result result = Result.success("操作成功！");
        // 生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = IdUtil.simpleUUID();
        //保存验证码信息
        String verifyKey = OauthConstants.CAPTCHA_CODE_KEY + uuid;
        RedisUtil.set(verifyKey, verifyCode, OauthConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        // 生成图片
        int w = 130, h = 38;
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            VerifyCodeUtils.outputImage(w, h, os, verifyCode);
            Map<String, Object> codeMap = new HashMap<>();
            codeMap.put("img", Base64.encode(os.toByteArray()));
            codeMap.put("uuid", uuid);
            result.setData(codeMap);
        } catch (IOException e) {
            log.error("生成验证码失败："+e.getMessage(),e);
            e.printStackTrace();
        }
        return  result;
    }
}
