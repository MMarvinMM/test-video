package com.chinatower.platform.project.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.request.CommonMailRequest;
import com.chinatower.platform.project.service.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件接口
 */
@Api(tags = "邮件接口")
@Slf4j
@RestController
@RequestMapping("/")
public class MailController extends TowerBaseController {

    @Autowired
    private MailService mailService;

    /**
     * 内部发送邮件
     */
    @ApiOperation(value = "发送邮件", produces = "application/json")
    @PostMapping("/mail/inSideSendMessage")
    public Result inSideSendMessage(@Validated @RequestBody CommonMailRequest request) {
        try {
            return mailService.sendMail(request);
        } catch (Exception e) {
            log.error("内网发送邮件异常", e);
            return Result.fail("发送邮件失败，请联系管理员");
        }
    }
}
