package com.chinatower.device.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.text.UUID;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.device.entity.vo.AIotPlatformCommonReq;
import com.chinatower.device.entity.vo.AIotPlatformCommonRsp;
import com.chinatower.device.entity.vo.AIotPlatformDataRsp;
import com.chinatower.device.entity.vo.TalkResultVO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.enums.AIotPlatformCommonEnum;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.content.SysConstants;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 调用AIot能力平台
 */
@Slf4j
@Component
public class AIotPlatformRequestUtils {

    @Value("${unified.gateway.userCode:typt-fangy}")
    private String userCode;

    @Resource
    private NetConnectUtils netConnectUtils;

    @Resource
    private SystemFeignClient systemFeignClient;

    private static final String CONFIG_PLAT_CODE = "platform_code";

    private static final String CALL_NEW_VERSION = "3.4";

    private static final String CALL_NEW_VERSION_LOCATION = "4.0";

    private static final String AIOT_PLATFORM_CODE = "aiot";


    public <R extends Serializable> TalkResultVO<R> callAIotSystem(Object inParam, NetConnectUtils.RequestParamter.URLMappingEnum urlMappingEnum, Class<R> rType, String accessNode, List<String> deviceCodeList) {
        // 调用广播平台
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        String mapping = urlMappingEnum.mapping;
        AIotPlatformCommonEnum.Command command = AIotPlatformCommonEnum.Command.getCommand(mapping);
        String CurrentLocationCode = getCurrentLocationCode();
        if (Objects.isNull(command)) {
            throw new CustomException("未匹配到AIot能力配置", HttpStatus.NOT_IMPLEMENTED);
        }
        if (StringUtils.equals(Constants.ZERO, command.getFnType())) {
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.AIOT_PLATFORM_COMMAND);
            body.setBody(assemblePlatformCommandReq(command.getFnId(), inParam, CurrentLocationCode, accessNode));

        }
        else if (StringUtils.equals(Constants.ONE, command.getFnType())) {
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.AIOT_DEVICE_COMMAND);
            body.setBody(assembleDevCommandReq(command.getFnId(), inParam, CurrentLocationCode, accessNode, deviceCodeList));
        }
        else {
            throw new CustomException("能力不支持", HttpStatus.NOT_IMPLEMENTED);
        }
        body.setPath(path);
        String serialNo = UUID.randomUUID().toString();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        try {
            log.info("请求AIot平台参数:{}", body);
            AIotPlatformCommonRsp response = netConnectUtils.post(body, AIotPlatformCommonRsp.class,
                    NetConnectUtils.HeaderKeyEnum.SerialNo.of(serialNo),
                    NetConnectUtils.HeaderKeyEnum.TimeStamp.of(timeStamp),
                    NetConnectUtils.HeaderKeyEnum.ReqType.of(command.getReqType()),
                    NetConnectUtils.HeaderKeyEnum.PlateformCode.of(command.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_UserCode.of(userCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(getCurrentLocationCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(accessNode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(command.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(body)));
            log.info("AIot平台响应参数:{}", response);
            return dealAIotResponse(response, rType);
        } catch (Exception e) {
            log.info("callAIotSystem 调用AIot平台异常：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }
    }

    private <R extends Serializable> TalkResultVO<R> dealAIotResponse(AIotPlatformCommonRsp response, Class<R> rType) {
        //这一层是控制中心的返回结果
        if (!response.getCode().equals(AIotPlatformCommonEnum.CODE_SUCCESS_CODE)) {
            throw new CustomException(response.getMsg(), HttpStatus.ERROR_4001);
        }
        if (CollectionUtils.isEmpty(response.getData())) {
            throw new CustomException("返回结果为空", HttpStatus.ERROR_4001);
        }
        //拿到能力层返回
        AIotPlatformDataRsp data = response.getData().get(0);
        if (!StringUtils.equals(AIotPlatformCommonEnum.RESULT_SUCCESS_CODE, data.getResult())) {
            // 友好提示 暂时这么处理 等确认错误码或者校验方案之后调整
            if (data.getMsg().contains("将来或现在的时间") || data.getMsg().contains("小于当前")) {
                data.setMsg("广播任务已过期");
            }
            throw new CustomException(data.getMsg(), HttpStatus.ERROR_4001);
        }
        TalkResultVO<R> typedResponse = new TalkResultVO<>();
        //特殊处理成老的返回码 防止之前代码判断0000
        typedResponse.setReturnCode(dealReturnCode(data.getResult()));
        typedResponse.setErrorMsg(response.getMsg());
        if (!Objects.isNull(rType)) {
            //创建返回对象
            typedResponse.setData(JSON.parseObject(data.getInfo(), rType));
        }
        return typedResponse;
    }

    private String dealReturnCode(String result) {
        if (StringUtils.equals(AIotPlatformCommonEnum.RESULT_SUCCESS_CODE, result)) {
            return SysConstants.SUCCESS_CODE;
        }
        return result;
    }

    private String getCurrentLocationCode() {
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

    /**
     * 判断是否调用AIoT接口
     *
     * @return 如果调用AIoT接口，则返回true；否则返回false
     */
    public Boolean isCallAIot(String platformCode) {
        try {
            String locationCode = StringUtils.isEmpty(platformCode) ? getCurrentLocationCode() : platformCode;
            log.info("当前平台编码:{}", locationCode);
            String str = RedisUtil.get(Constants.COLONY_RELATION_ALL + locationCode, RedisDBType.PRIMARY);
            if (StringUtils.isEmpty(str)) {
                return false;
            }
            ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
            for (ColonyApp app : cascadeConfigSyncVO.getApps()) {
                if (AIOT_PLATFORM_CODE.equals(app.getAppId())) {
                    String platformNodeVersion = app.getPlatformNodeVersion();
                    if (StringUtil.isNotEmpty(platformNodeVersion)) {
                        return compareVersion(platformNodeVersion, locationCode);
                    }
                }
            }
        } catch (Exception e) {
            log.error("AIotPlatformRequestUtils isCallAIot error:", e);
        }
        return false;
    }


    private static Boolean compareVersion(String v1, String locationCode) {
        if (StringUtils.isEmpty(v1)) {
            return false;
        }
        String[] parts1 = v1.split("\\.");
        log.info("当前版本号:{}", v1);
        String[] parts2 = CALL_NEW_VERSION.split("\\.");
        if (!Constants.CENTER_LOCATION.equals(locationCode)) {
            parts2 = CALL_NEW_VERSION_LOCATION.split("\\.");
        }

        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            int result = Integer.compare(num1, num2);
            if (result != 0) {
                return result >= 0;
            }
        }
        return true;
    }

    public List<AIotPlatformCommonReq> assemblePlatformCommandReq(String fnId, Object fnValue, String acceptNode, String accessNode) {
        AIotPlatformCommonReq req = new AIotPlatformCommonReq();
        req.setFnId(fnId);
        req.setFnValue(fnValue);
        req.setAcceptNode(acceptNode);
        req.setAccessNode(accessNode);
        return Collections.singletonList(req);
    }

    public List<AIotPlatformCommonReq> assembleDevCommandReq(String fnId, Object fnValue, String acceptNode, String accessNode, List<String> deviceCodes) {
        AIotPlatformCommonReq req = new AIotPlatformCommonReq();
        req.setFnId(fnId);
        req.setFnValue(fnValue);
        req.setAcceptNode(acceptNode);
        req.setAccessNode(accessNode);
        req.setDevCodeList(deviceCodes);
        req.setType("0");
        return Collections.singletonList(req);
    }
}
