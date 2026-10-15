package com.chinatower.platform.project.service;


import com.chinatower.platform.entity.request.CommonSmsRequest;

import java.util.Map;

/**
 * @Description: 短信发送service
 * @Author: 张帅
 * @Date: 2021/11/12 16:05
 **/
public interface CommonSmsService {

    /**
     * 发送短信通用方法
     * */
    Map<String, String> sendMessageToPhone(CommonSmsRequest commonSmsRequest, Boolean isSendVerificationCode);
}
