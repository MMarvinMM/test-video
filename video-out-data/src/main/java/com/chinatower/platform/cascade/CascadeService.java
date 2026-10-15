package com.chinatower.platform.cascade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.CascadeDto;
import com.chinatower.platform.utils.CascadeConnectUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
@Slf4j
public class CascadeService {
    @Autowired
    private CascadeConnectUtils cascadeConnectUtils;
    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 每30s向级联平台发一次心跳
     */
    @XxlJob("cascadeHeartBeat")
    public ReturnT<String> cascadeHeartBeat(String param) {
        log.info("cascadeHeartBeat定时任务开始！");
        try {
            //查询本级平台编码
            String platformCode = null;
            AjaxResult ajaxResult = systemFeignClient.getConfValue("platform_code");
            if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)){
                    platformCode = sysConfig.getConfigValue();
                }
            }

            // 定义请求参数
            HashMap<String, Object> body = new HashMap<>();
            body.put("colonyCode", platformCode);
            body.put("colonyApp", "general");
            body.put("status", 1);
            CascadeDto cascadeDto = new CascadeDto();
            cascadeDto.setUri("/colony/heartbeat");
            cascadeDto.setFromPlatformCode(platformCode);
            cascadeDto.setBody(JSONObject.toJSONString(body));

            //给级联网关发送请求
            Result result = cascadeConnectUtils.sendToCascade(cascadeDto);
            if(result.getCode() != ResultCode.SUCCESS.getCode()){
                log.info("cascadeHeartBeat定时任务失败！");
                return ReturnT.FAIL;
            }
        }catch (Exception e){
            log.info("cascadeHeartBeat定时任务失败！", e);
            return ReturnT.FAIL;
        }
        log.info("cascadeHeartBeat定时任务结束！");
        return ReturnT.SUCCESS;
    }
}
