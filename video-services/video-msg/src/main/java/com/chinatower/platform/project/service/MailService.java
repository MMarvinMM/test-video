package com.chinatower.platform.project.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.request.CommonMailRequest;

/**
 * 邮件服务
 */
public interface MailService {

    /**
     * 内部发送邮件（调用平台创新中心邮件组件）
     *
     * @param request 邮件请求
     * @return 发送结果
     */
    Result sendMail(CommonMailRequest request);
}
