package com.chinatower.platform.project.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.entity.request.MsgRequestVO;
import com.chinatower.platform.entity.request.SmsCodeRequest;
import com.chinatower.platform.project.mapper.SysUserMapper;
import com.chinatower.platform.project.service.SmsService;
import com.chinatower.platform.project.vo.SmsRecordQuery;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * video-msg
 * @Description: 短信conttroller
 * @Author: 张帅
 * @Date: 2021/11/12 16:05
 **/
@Api(tags = "短信接口")
@Slf4j
@RestController
@RequestMapping("/")
public class SmsController extends TowerBaseController {


    /**
     * 注入用户注册业务层接口
     */
    @Autowired
    private SmsService smsService;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 发送短信验证码
     * @param @return
     * @return json
     * @throws
     * @Title: login
     * @Description: 暴露在公网发送短信验证码
     */
    @ApiOperation(value = "发送短信验证码", produces = "application/json")
    @PostMapping("base/sms/sendMessage")
    public Result sendMessage(@Validated @RequestBody SmsCodeRequest smsRecord, HttpServletRequest request) {
        log.info("SmsController-sendMessage入参:{}", JSON.toJSONString(smsRecord));
        String header = request.getHeader("dynamic");
        if ("".equals(header) || null == header) {
            return Result.fail(Constants.FAIL_MSG_REQUEST);
        }
        try {

            if (StringUtils.isEmpty(smsRecord.getUserName())) {
                return Result.fail(Constants.FAIL_MSG_REQUEST);
            }
            //验证码
            String phone = sysUserMapper.selectUserPhone(AESUtil.decryptBase64(smsRecord.getUserName()));
            if(StringUtils.isEmpty(phone)){
                log.info("为查询到用户手机号用户名:{}", smsRecord.getUserName());
                return Result.fail(Constants.FAIL_MSG_REQUEST);
            }
            smsRecord.setPhone(phone);
            if ("1".equals(smsRecord.getMsgType())) {
                String templateKey = smsRecord.getTemplateKey();
                log.info("解密前templateKey=" + templateKey);
                templateKey = AESUtil.decryptBase64(templateKey);
                log.info("解密后templateKey=" + templateKey);
                return smsService.sendShortMessage(smsRecord.getPhone(), header, smsRecord.getAesPhone(), templateKey);
            } else {//短信
                return Result.fail("外网只支持发送短信验证码");
            }
        } catch (Exception e) {
            log.error("暴露在公网发送短信验证码异常");
            log.error(e.getMessage(), e);
            return Result.fail("暴露在公网发送短信验证码发送失败，请联系管理员");
        }
    }

    /**
     * 发送短信验证码
     * @param @return
     * @return json
     * @throws
     * @Title: login
     * @Description: 暴露在公网发送短信验证码(个人中心修改手机号发送验证接口)
     */
    @ApiOperation(value = "发送短信验证码", produces = "application/json")
    @PostMapping("base/sms/sendMessageCheck")
    public Result sendMessageCheck(@Validated @RequestBody SmsCodeRequest smsRecord, HttpServletRequest request) {
        log.info("SmsController-sendMessageCheck入参:{}", JSON.toJSONString(smsRecord));
        String header = request.getHeader("dynamic");
        if ("".equals(header) || null == header) {
            return Result.fail(Constants.FAIL_MSG_REQUEST);
        }
        try {
            if (StringUtils.isEmpty(smsRecord.getPhone())) {
                return Result.fail(Constants.FAIL_MSG_REQUEST);
            }
            //验证码
            String phone = AESUtil.decryptBase64(smsRecord.getPhone());
            smsRecord.setPhone(phone);
            if ("1".equals(smsRecord.getMsgType())) {
                return smsService.sendShortMessage(smsRecord.getPhone(), header, smsRecord.getAesPhone(), smsRecord.getTemplateKey());
            } else {//短信
                return Result.fail("外网只支持发送短信验证码");
            }
        } catch (Exception e) {
            log.error("暴露在公网发送短信验证码异常");
            log.error(e.getMessage(), e);
            return Result.fail("暴露在公网发送短信验证码发送失败，请联系管理员");
        }
    }


    /**
     * 内部发送短信或短信验证码
     * @param @return
     * @return json
     * @throws
     * @Title: login
     * @Description: 内部发送短信或短信验证码
     */
    @ApiOperation(value = "发送短信", produces = "application/json")
    @PostMapping("/sms/inSideSendMessage")
    public Result inSideSendMessage(@Validated @RequestBody CommonSmsRequest smsRecord) {
        try {
            return smsService.inSideSendMessage(smsRecord);
        } catch (Exception e) {
            log.error("内网发送短信异常");
            log.error(e.getMessage(), e);
            return Result.fail("发送短信失败，请联系管理员");
        }
    }

    /**
     * 发送短信innovationSendMessage
     * @param @return
     * @return json
     * @throws
     * @Title: login
     * @Description: 内部发送短信或短信验证码
     */
    @ApiOperation(value = "发送短信", produces = "application/json")
    @PostMapping("/sms/innovationSendMessage")
    public Result innovationSendMessage(@Validated @RequestBody MsgRequestVO request) {
        return smsService.innovationSendMessage(request);
    }


    /**
     * 批量发送短信接口
     *
     * @param
     * @return json
     * @throws
     * @Description: 批量发送短信
     * @author yiyao
     */
    @ApiOperation(value = "批量发送短信", produces = "application/json")
    @PostMapping("/sms/batchSendMessage")
    public Result batchSendMessage(@Validated @RequestBody List<CommonSmsRequest> smsRecords) {
        try {
            return smsService.batchSendMessage(smsRecords);
        } catch (Exception e) {
            log.error("批量发送短信异常");
            log.error(e.getMessage(), e);
            return Result.fail("批量发送短信异常，请联系管理员");
        }
    }

    /**
     * 查询短信发送记录
     * @param query
     * @return
     */
    @ApiOperation(value = "查询短信发送记录")
    @GetMapping("/sms/sendRecord")
    public Result querySmsRecord(SmsRecordQuery query) {
        startPage();
        return Result.data(smsService.list(query));
    }

    /**
     * 校验短信验证码
     * @param phone
     * @param code
     * @param templateKey
     * @param canRepeatCheck
     * @return
     */
    @ApiOperation(value = "校验短信验证码", produces = "application/json")
    @PostMapping("base/sms/checkPhoneCode")
    public Result checkPhoneCode(String phone, String code, String templateKey, Boolean canRepeatCheck) {
        return smsService.checkPhoneCode(phone, code, templateKey, canRepeatCheck != null && canRepeatCheck);
    }


}
