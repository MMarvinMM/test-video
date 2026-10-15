package com.chinatower.device.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.device.entity.vo.TalkResultVO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author willem
 * @date 2024/10/17
 * 调用广播平台工具类
 */
@Slf4j
@Component
public class HornRequestUtils {

    @Value("${horn.appKey:0BC2F7A3369F4CA4830D0C128D0574CD}")
    private String HORN_APP_KEY;

    @Value("${horn.secretKey:5A1C830409DC4DC780B2C31EE0A546A0}")
    private String HORN_SECRET_KEY;

    @Resource
    private NetConnectUtils netConnectUtils;

    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private AIotPlatformRequestUtils aIotUtils;

    private static final String CONFIG_PLAT_CODE = "platform_code";

    private static final String TO_APP_HORN_SYSTEM = "smartbc";

    /**
     * 调用广播平台(body)
     *
     * @param inParam 请求参数
     * @param urlMappingEnum URL映射枚举
     * @param rType 返回类型
     * @return 返回TalkResultVO对象
     * @throws CustomException 自定义异常
     */
    public <R extends Serializable> TalkResultVO<R> callHornSystem(Object inParam, NetConnectUtils.RequestParamter.URLMappingEnum urlMappingEnum,
                                                                   Class<R> rType, List<String> deviceCodeList) {
        if (aIotUtils.isCallAIot(null)) {
            return aIotUtils.callAIotSystem(inParam, urlMappingEnum, rType, getCurrentLocationCode(), deviceCodeList);
        }
        // 调用广播平台
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(urlMappingEnum);
        body.setPath(path);
        body.setBody(inParam);
        try {
            log.info("通过(body方式)请求广播平台请求参数:{}", body);
            TalkResultVO response = netConnectUtils.post(body, TalkResultVO.class);
            log.info("通过(body方式)请求广播平台响应参数(body):{}", response);
            return dealHornResponse(response, rType);
        } catch (Exception e) {
            log.info("callHornSystem 调用广播平台异常：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
    }

    public <R extends Serializable> TalkResultVO<R> callHornSystemDiffNode(Object inParam, NetConnectUtils.RequestParamter.URLMappingEnum urlMappingEnum,
                                                                           Class<R> rType, String accessNode, List<String> deviceCodeList) {
        if (aIotUtils.isCallAIot(accessNode)) {
            return aIotUtils.callAIotSystem(inParam, urlMappingEnum, rType, accessNode, deviceCodeList);
        }
        // 调用广播平台
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(urlMappingEnum);
        body.setPath(path);
        body.setBody(inParam);
        try {
            log.info("通过(body方式)请求广播平台请求参数:{}", body);
            TalkResultVO response = netConnectUtils.post(body, TalkResultVO.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(getCurrentLocationCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(accessNode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(TO_APP_HORN_SYSTEM),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(body)));
            log.info("通过(body方式)请求广播平台响应参数(body):{}", response);
            return dealHornResponse(response, rType);
        } catch (Exception e) {
            log.info("callHornSystem 调用广播平台异常：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
    }

    public <R extends Serializable> TalkResultVO<R> callHornSystemForm(Object inParam, List<MultipartFile> multipartFiles,
                                                                       NetConnectUtils.RequestParamter.URLMappingEnum urlMappingEnum,
                                                                       Class<R> rType, List<String> deviceCodeList) {
        if (aIotUtils.isCallAIot(null)) {
            return aIotUtils.callAIotSystem(inParam, urlMappingEnum, rType, getCurrentLocationCode(), deviceCodeList);
        }
        // 调用广播平台
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(urlMappingEnum);
        body.setPath(path);
        body.setBody(inParam);
        try {
            log.info("通过(form-data方式)请求广播平台请求参数:{}", body);
            TalkResultVO response = netConnectUtils.postForm(body, multipartFiles, TalkResultVO.class);
            log.info("通过(form-data方式)请求广播平台响应参数:{}", response);
            return dealHornResponse(response, rType);
        } catch (Exception e) {
            log.info("callHornSystemForm 调用广播平台异常：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
    }

    private <R extends Serializable> TalkResultVO<R> dealHornResponse(TalkResultVO response, Class<R> rType) {
        if (!response.getReturnCode().equals(SysConstants.SUCCESS_CODE)) {
            // 友好提示 暂时这么处理 等确认错误码或者校验方案之后调整
            if (response.getErrorMsg().contains("将来或现在的时间") || response.getErrorMsg().contains("小于当前")) {
                response.setErrorMsg("广播任务已过期");
            }
            throw new CustomException(response.getErrorMsg(), HttpStatus.ERROR_4001);
        }
        if (!Objects.isNull(rType)) {
            //创建返回对象
            TalkResultVO<R> typedResponse = new TalkResultVO<>();
            typedResponse.setReturnCode(response.getReturnCode());
            typedResponse.setErrorMsg(response.getErrorMsg());
            typedResponse.setData(JSONObject.parseObject(JSONObject.toJSONString(response.getData()), rType));
            response = typedResponse;
        }
        return response;
    }

    public String getCurrentLocationCode() {
        AjaxResult ajaxResult = systemFeignClient.getConfValue(CONFIG_PLAT_CODE);
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (Objects.isNull(sysConfig)) {
                throw new CustomException("当前平台编码未配置，请配置后重试");
            }
            return sysConfig.getConfigValue();
        }
        else {
            throw new CustomException("获取平台编码配置失败，请稍后重试！");
        }
    }
}

