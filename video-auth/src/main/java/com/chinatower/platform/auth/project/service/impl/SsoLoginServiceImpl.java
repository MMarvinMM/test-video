package com.chinatower.platform.auth.project.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.ip.IpUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.auth.config.SsoProperties;
import com.chinatower.platform.auth.project.util.SsoSecretCryptoUtil;
import com.chinatower.platform.auth.project.entity.SsoSecretSyncRecord;
import com.chinatower.platform.auth.project.entity.dto.SsoSecretSyncDTO;
import com.chinatower.platform.auth.project.entity.dto.SsoSecretSyncResultVO;
import com.chinatower.platform.auth.project.entity.dto.XiaoTaSsoResultVO;
import com.chinatower.platform.auth.project.mapper.SsoLoginMapper;
import com.chinatower.platform.auth.project.service.ISsoLoginService;
import com.chinatower.product.common.RequestExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

/**
 * 小塔单点登录服务实现
 * 流程：先调用4A平台统一凭证申请接口获取pwdaToken，再调用小塔SSO接口换取登录重定向地址
 */
@Slf4j
@Service
public class SsoLoginServiceImpl implements ISsoLoginService {

    private static final int HTTP_TIMEOUT_MILLIS = 30000;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private SsoLoginMapper ssoLoginMapper;

    private final String ERRDESC = "ERRDESC";

    @Override
    public Result<XiaoTaSsoResultVO> loginXiaoTa(String username, String redirectCode) {
        if (StrUtil.isBlank(username)) {
            return Result.fail("登录账号不能为空");
        }
        try {
            // 1. 调用4A统一凭证申请接口（DB优先，nacos回退）
            String fourASecret = resolveFourASecret();
            String appUuid = UUID.randomUUID().toString();
            JSONObject fourAResult = requestFourA(username, appUuid, fourASecret);
            String rsp = fourAResult.getString("RSP");
            if (!"0".equals(rsp)) {
                String errDesc = fourAResult.getString(ERRDESC);
                log.warn("小塔单点登录->4A统一凭证申请失败，RSP={}, ERRDESC={}", rsp, errDesc);
                return Result.fail(StrUtil.isNotBlank(errDesc) ? "4A统一凭证申请失败：" + errDesc : "4A统一凭证申请失败");
            }
            String pwdaToken = fourAResult.getString("TOKEN");
            String acctId = fourAResult.getString("ACCTID");

            // 2. 调用小塔单点登录接口
            JSONObject xiaoTaReq = new JSONObject();
            xiaoTaReq.put("pwdaToken", pwdaToken);
            xiaoTaReq.put("acctId", acctId);
            xiaoTaReq.put("fromSysCode", ssoProperties.getFromSysCode());
            if (StrUtil.isNotBlank(ssoProperties.getToSysCode())) {
                xiaoTaReq.put("toSysCode", ssoProperties.getToSysCode());
            }
            String useRedirectCode = StrUtil.isNotBlank(redirectCode) ? redirectCode : ssoProperties.getDefaultRedirectCode();
            if (StrUtil.isNotBlank(useRedirectCode)) {
                xiaoTaReq.put("redirectCode", useRedirectCode);
            }
            xiaoTaReq.put("timestamp", System.currentTimeMillis());

            log.info("小塔单点登录->小塔SSO请求明文参数：{}", xiaoTaReq.toJSONString());
            String xiaoTaArgs = SsoSecretCryptoUtil.encode(xiaoTaReq.toJSONString(), ssoProperties.getXiaota3DesKey());
            String xiaoTaRequestBody = JSON.toJSONString(Collections.singletonMap("args", xiaoTaArgs));
            log.info("小塔单点登录->小塔SSO请求，url={}，原始请求报文={}", ssoProperties.getXiaotaUrl(), xiaoTaRequestBody);
            String xiaoTaResponse = postJson(ssoProperties.getXiaotaUrl(), xiaoTaRequestBody);
            log.info("小塔单点登录->小塔SSO返回，url={}，原始响应报文={}", ssoProperties.getXiaotaUrl(), xiaoTaResponse);
            JSONObject xiaoTaResult = parseJsonObject(xiaoTaResponse);
            log.info("小塔单点登录->小塔SSO返回：code={}, msg={}",
                    xiaoTaResult.getString("code"), xiaoTaResult.getString("msg"));
            Integer code = xiaoTaResult.getInteger("code");
            String msg = xiaoTaResult.getString("msg");
            String data = xiaoTaResult.getString("data");
            JSONObject dataJson = StrUtil.isNotBlank(data) ? parseJsonObject(data) : null;
            log.info("小塔单点登录->小塔SSO返回明文数据：{}", dataJson == null ? null : dataJson.toJSONString());

            if (Integer.valueOf(200).equals(code)) {
                XiaoTaSsoResultVO vo = buildSsoResult(dataJson, msg);
                return Result.data(vo);
            }

            String errMsg = dataJson == null ? msg : dataJson.getString("errMsg");
            XiaoTaSsoResultVO vo = buildSsoResult(dataJson, errMsg);
            log.warn("小塔单点登录->小塔SSO失败，errCode={}, errMsg={}",
                    dataJson == null ? null : dataJson.getString("errCode"), errMsg);
            String failMsg = StrUtil.isNotBlank(errMsg) ? "小塔SSO登录失败：" + errMsg : "小塔SSO登录失败";
            return Result.data(ResultCode.FAILURE.getCode(), vo, failMsg);
        } catch (Exception e) {
            log.error("小塔单点登录异常", e);
            String errMsg = e.getMessage();
            return Result.fail(StrUtil.isNotBlank(errMsg) ? errMsg : "登录失败，请稍后再试");
        }
    }

    /**
     * 解析4A密钥：DB（sso_secret_sync 最新记录）优先，DB无记录回退 nacos 配置
     *
     * @return 当前生效密钥
     */
    private String resolveFourASecret() {
        try {
            SsoSecretSyncRecord latest = ssoLoginMapper.selectLatestSecretSync();
            if (latest != null && StrUtil.isNotBlank(latest.getAppSecret())) {
                return latest.getAppSecret();
            }
        } catch (Exception e) {
            log.warn("小塔单点登录->查询SSO密钥DB记录失败，回退nacos配置", e);
        }
        return ssoProperties.getFourASecret();
    }

    /**
     * SSO密钥同步：以当前生效密钥解密入参后，追加一条密钥下发记录（4A每月1日调用）
     * 当前生效密钥：sso_secret_sync 最新记录 appSecret，DB无记录时回退 nacos fourASecret
     *
     * @param dto 同步入参
     * @return 同步结果 {rsp, errDesc}
     */
    @Override
    public SsoSecretSyncResultVO syncSecret(SsoSecretSyncDTO dto) {
        log.info("SSO密钥同步->请求入参：{}", JSON.toJSONString(dto));
        SsoSecretSyncResultVO vo = new SsoSecretSyncResultVO();
        try {
            if (dto == null || StrUtil.isBlank(dto.getAppId())
                    || StrUtil.isBlank(dto.getAppSecret()) || StrUtil.isBlank(dto.getAppSecretOld())) {
                vo.setRsp("1");
                vo.setErrDesc("参数缺失");
                return vo;
            }
            String decryptKey = resolveFourASecret();
            if (StrUtil.isBlank(decryptKey)) {
                vo.setRsp("1");
                vo.setErrDesc("密钥解密失败");
                return vo;
            }
            String appSecret = SsoSecretCryptoUtil.decode(dto.getAppSecret());
            String appSecretOld = SsoSecretCryptoUtil.decode(dto.getAppSecretOld());
            if (StrUtil.isBlank(appSecret) || StrUtil.isBlank(appSecretOld)) {
                log.warn("SSO密钥同步->解密失败，appId={}", dto.getAppId());
                vo.setRsp("1");
                vo.setErrDesc("密钥解密失败");
                return vo;
            }
            SsoSecretSyncRecord record = new SsoSecretSyncRecord();
            record.setAppId(dto.getAppId().trim());
            record.setAppSecret(appSecret.trim());
            record.setAppSecretOld(appSecretOld.trim());
            log.info("SSO密钥同步->解密后入参：appId={}, appSecret={}, appSecretOld={}",
                    record.getAppId(), record.getAppSecret(), record.getAppSecretOld());
            if (StrUtil.isNotBlank(dto.getUpdateTime())) {
                try {
                    record.setUpdateTime(LocalDateTime.parse(dto.getUpdateTime(), DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    log.warn("SSO密钥同步->updateTime解析失败：{}", dto.getUpdateTime());
                }
            }
            int rows = ssoLoginMapper.insertSecretSyncRecord(record);
            if (rows <= 0) {
                log.warn("SSO密钥同步->影响行数为0，appId={}", record.getAppId());
                vo.setRsp("1");
                vo.setErrDesc("系统异常");
                return vo;
            }
            log.info("SSO密钥同步->成功，appId={}", record.getAppId());
            vo.setRsp("0");
            return vo;
        } catch (Exception e) {
            log.error("SSO密钥同步异常", e);
            vo.setRsp("1");
            vo.setErrDesc("系统异常");
            return vo;
        }
    }

    /**
     * 调用4A统一凭证申请接口（能开平台REST方式）
     *
     * @param username 登录账号
     * @param appUuid  UUID
     * @param secret   APPLYUUIDCRPT 单DES密钥
     * @return 4A响应JSON
     */
    private JSONObject requestFourA(String username, String appUuid, String secret) throws Exception {
        String applyUuidCrpt = SsoSecretCryptoUtil.encode(appUuid, secret);
        JSONObject fourAReq = new JSONObject();
        fourAReq.put("SYSCODE", ssoProperties.getSysCode());
        fourAReq.put("LOGINACCT", username);
        fourAReq.put("APPLYSYSCODE", ssoProperties.getApplySysCode());
        fourAReq.put("APPLYUUID", appUuid);
        fourAReq.put("APPLYUUIDCRPT", applyUuidCrpt);
        fourAReq.put("ACCESSIPADDR", IpUtils.getHostIp());

        log.info("小塔单点登录->4A平台请求明文参数：{}", fourAReq.toJSONString());
        // 4A 仍接收整体加密串：先用 fourA3DesKey 加密 fourAReq，再包一层 {"args": 加密串}
        String fourAArgs = SsoSecretCryptoUtil.encode(fourAReq.toJSONString(), ssoProperties.getFourA3DesKey());
        String content = JSON.toJSONString(Collections.singletonMap("args", fourAArgs));
        log.info("小塔单点登录->4A平台请求，url={}，原始请求报文={}", ssoProperties.getFourAUrl(), content);

        JSONObject platformResult = RequestExecute.executeJson(
                ssoProperties.getFourAUrl(),
                ssoProperties.getAppId(),
                ssoProperties.getNkMethod(),
                ssoProperties.getNkVersion(),
                ssoProperties.getNkSendCode(),
                ssoProperties.getNkServerCode(),
                content,
                ssoProperties.getNkSecretKey());
        log.info("小塔单点登录->4A平台返回原始结果：{}", platformResult.toJSONString());

        String resCode = platformResult.getString("res_code");
        if (!"00000".equals(resCode)) {
            String resMessage = platformResult.getString("res_message");
            log.warn("小塔单点登录->4A平台调用失败，res_code={}, res_message={}", resCode, resMessage);
            JSONObject failResult = new JSONObject();
            failResult.put("RSP", "1");
            failResult.put(ERRDESC, StrUtil.isNotBlank(resMessage) ? resMessage : "4A平台调用失败");
            return failResult;
        }

        // 业务数据在 result 字段，可能是JSON对象或JSON字符串
        JSONObject fourAResult = null;
        Object resultObj = platformResult.get("result");
        if (resultObj instanceof JSONObject) {
            fourAResult = (JSONObject) resultObj;
        } else if (resultObj instanceof String && StrUtil.isNotBlank((String) resultObj)) {
            fourAResult = parseJsonObject((String) resultObj);
        }
        if (fourAResult == null) {
            fourAResult = new JSONObject();
        }
        // 兼容 4A 将实际结果包裹在 data 字段（JSON字符串），需先解包后再读取 RSP/ERRDESC/TOKEN/ACCTID
        String data = fourAResult.getString("data");
        if (StrUtil.isNotBlank(data)) {
            try {
                JSONObject inner = JSON.parseObject(data);
                if (inner != null && inner.containsKey("RSP")) {
                    fourAResult = inner;
                }
            } catch (Exception e) {
                log.warn("小塔单点登录->4A响应data解析失败，使用外层字段", e);
            }
        }
        log.info("小塔单点登录->4A统一凭证申请返回：RSP={}, TOKEN={}, ACCTID={}, ERRDESC={}",
                fourAResult.getString("RSP"), fourAResult.getString("TOKEN"),
                fourAResult.getString("ACCTID"), fourAResult.getString(ERRDESC));
        return fourAResult;
    }

    /**
     * 发送HTTP POST JSON请求
     *
     * @param url  请求地址
     * @param body 请求体
     * @return 响应字符串
     */
    String postJson(String url, String body) {
        return HttpRequest.post(url)
                .contentType(ContentType.JSON.getValue())
                .body(body)
                .timeout(HTTP_TIMEOUT_MILLIS)
                .execute()
                .body();
    }

    /**
     * 解析响应为JSON对象；空响应或解析异常时返回空对象，避免后续取值NPE
     */
    private JSONObject parseJsonObject(String json) {
        try {
            JSONObject obj = JSON.parseObject(json);
            return obj == null ? new JSONObject() : obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 构造小塔SSO返回VO
     */
    private XiaoTaSsoResultVO buildSsoResult(JSONObject dataJson, String msg) {
        XiaoTaSsoResultVO vo = new XiaoTaSsoResultVO();
        vo.setRedirectUrl(dataJson == null ? null : dataJson.getString("redirectUrl"));
        vo.setMsg(msg);
        return vo;
    }
}
