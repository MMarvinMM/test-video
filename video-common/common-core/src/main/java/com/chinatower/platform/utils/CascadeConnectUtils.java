package com.chinatower.platform.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CommonErrorCode;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.model.dto.CascadeDto;
import com.chinatower.platform.util.CascadeSignatureUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.LinkedHashMap;

@Slf4j
@Component
public class CascadeConnectUtils {
    public Result sendToCascade(CascadeDto cascadeDto) {
        try {
            log.info("CascadeConnectUtils.sendToCascade入参=" + JSONObject.toJSON(cascadeDto));
            if(StringUtils.isEmpty(cascadeDto.getFromPlatformCode())){
                throw new CustomException("cascadeDto.fromPlatformCode必填！");
            }
            String str = RedisUtil.get(Constants.COLONY_RELATION_ALL + cascadeDto.getFromPlatformCode(), RedisDBType.PRIMARY);
            if(StringUtils.isEmpty(str)){
                throw new CustomException("平台缓存信息异常！");
            }
            ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);

            String s = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSSSSSSSSSS, new Date());

            String ipPort = cascadeConfigSyncVO.getGwAddr();
            String uri = cascadeDto.getUri();

            LinkedHashMap<String, String> header = new LinkedHashMap<>();
            // 请求时间戳
            header.put("X-Cascade-RequestTime", String.valueOf(System.currentTimeMillis()));
            // 请求唯一ID
            header.put("X-Cascade-RequestID", s);
            // Appkey
            header.put("X-Cascade-AppKey", cascadeConfigSyncVO.getAppKey());
            // 来源属地编码
            if (!ObjectUtils.isEmpty(cascadeDto.getFromPlatformCode())) {
                header.put("X-Cascade-From-Code", cascadeDto.getFromPlatformCode());
            }
            // 目的地属地编码
            if (!ObjectUtils.isEmpty(cascadeDto.getToPlatformCode())) {
                header.put("X-Cascade-To-Code", cascadeDto.getToPlatformCode());
            }
            // 属地应用
            if (!ObjectUtils.isEmpty(cascadeDto.getAppId())) {
                header.put("X-Cascade-To-App", cascadeDto.getAppId());
            }
            // 属地请求接口Path
            if (!ObjectUtils.isEmpty(cascadeDto.getPath())) {
                header.put("X-Cascade-To-Path", cascadeDto.getPath());
            }
            // 请求类型
            if (!ObjectUtils.isEmpty(cascadeDto.getMethod())) {
                header.put("X-Cascade-Method", cascadeDto.getMethod());
            }

            //级联网关提供的加签方法
            String xSignature = CascadeSignatureUtils.generateSignature(uri, header, cascadeConfigSyncVO.getSecretKey());
            // 接口签名
            header.put("X-Cascade-Signature", xSignature);
            log.info("========header=" + JSONObject.toJSONString(header));

            ipPort += uri;
            log.info("msgId:{},请求地址{},请求参数{}", new Object[]{s, ipPort, cascadeDto.getBody()});
            HttpResponse httpResponse = ((HttpRequest) HttpRequest.post(ipPort).addHeaders(header)).body(cascadeDto.getBody()).timeout(30000).execute();
            log.info("返回状态:{},返回信息{}", new Object[]{httpResponse.getStatus(), httpResponse.body()});
            if (httpResponse != null && httpResponse.isOk()) {
                if(!ObjectUtils.isEmpty(httpResponse.body())){
                    JSONObject resp = JSON.parseObject(httpResponse.body());
                    String returnCode = resp.getString("returnCode");
                    if (CommonErrorCode.SUCCESSFUL_CODE.equals(returnCode)) {
                        return Result.data(resp.get("data"));
                    } else {
                        return Result.fail(resp.getString("returnMsg"));
                    }
                }
            }
        } catch (Exception e) {
            log.error("CascadeConnectUtils.sendToCascade工具类异常=",e);
        }
        return Result.fail("请求失败！");
    }
}
