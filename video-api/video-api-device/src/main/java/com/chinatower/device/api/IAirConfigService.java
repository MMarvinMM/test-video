package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.AirConfigFallbackFactory;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.FlightRadiusVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 计划管理
 */
@FeignClient(contextId = "airConfigService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirConfigFallbackFactory.class)
public interface IAirConfigService {

    /**
     * 获取无人机密码配置
     *
     * @param airPwdReq
     * @return
     */
    @PostMapping("/airConfig/getUavPwdConfig")
    Result getUavPwdConfig(@RequestBody AirPwdReq airPwdReq);

    /**
     * 校验无人机密码
     *
     * @param airPwdReq
     * @return
     */
    @PostMapping("/airConfig/checkUavPwd")
    Result checkUavPwd(@RequestBody AirPwdReq airPwdReq);

    /**
     *
     * 通过无人机编码查询可飞行半径距离
     * @param uavCodeList
     * @return
     */
    @PostMapping("/airConfig/queryFlightRadiusByUavCode")
    Result<List<FlightRadiusVo>> queryFlightRadiusByUavCode(@RequestBody List<String> uavCodeList);
}
