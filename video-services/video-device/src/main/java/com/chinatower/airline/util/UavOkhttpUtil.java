package com.chinatower.airline.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 网关通用调用工具
 */
@Slf4j
@Component
public class UavOkhttpUtil {
    @Resource
    private NetConnectUtils connectUtils;

    @Value("${algorithm.appCode}")
    private String algAppCode;

    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 飞控运管调用方式
     *
     * @param
     * @param
     * @return
     */
    public ResultEntity doPostUavGateWay(NetConnectUtils.RequestParamter requestParamter, String toPlatformCode, String platformCode) {
        ResultEntity cr = new ResultEntity();
        try {
            log.info("开始调用新大陆网关:接入节点{}接收节点{}", toPlatformCode, platformCode);
            cr = connectUtils.post(requestParamter, ResultEntity.class, NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("uav"), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(requestParamter)));
            log.info("调用新大陆网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关:{}", e);
        }
        if (ObjectUtil.isNotNull(cr) && !"0000".equals(cr.getReturnCode())) {
            throw new CustomException("调用飞控平台返回错误:" + cr.getErrorMsg());
        }
        return cr;
    }

    /**
     * 算法仓运管调用方式
     *
     * @param
     * @param
     * @return
     */
    public ResultEntity doPostAlgGateWay(NetConnectUtils.RequestParamter requestParamter, String toPlatformCode, String platformCode) {
        ResultEntity cr = new ResultEntity();
        try {
            cr = connectUtils.post(requestParamter, ResultEntity.class, NetConnectUtils.HeaderKeyEnum.X_AppCode.of(algAppCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("algorithm"), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(requestParamter)));
            log.info("调用网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关异常:{}", e);
        }
        return cr;
    }

    /**
     * 飞控运管调用方式
     *
     * @param
     * @param
     * @return
     */
    public ResultEntity doPostUavGateWayNoException(NetConnectUtils.RequestParamter requestParamter, String toPlatformCode, String platformCode) {
        ResultEntity cr = new ResultEntity();
        try {
            log.info("开始调用新大陆网关:接入节点{}接收节点{}", toPlatformCode, platformCode);
            cr = connectUtils.post(requestParamter, ResultEntity.class, NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toPlatformCode), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of("uav"), NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(requestParamter)));
            log.info("调用新大陆网关结果：" + JSONObject.toJSONString(cr));
        } catch (Exception e) {
            log.error("调用网关:{}", e);
        }
        return cr;
    }

    /**
     * 获取当前节点
     * @return
     */
    public String getSelfPlatFormCode(){
        String configValue ="";
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))){
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")),SysConfig.class);
            if(sysConfigInfo != null){
                configValue = sysConfigInfo.getConfigValue();
            }else{
                log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
            }
        }else {
            log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
        }
        return configValue;
    }
}
