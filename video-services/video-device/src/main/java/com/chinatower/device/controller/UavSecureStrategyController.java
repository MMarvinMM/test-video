package com.chinatower.device.controller;

import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.UavSecureStrategyInfoVO;

import com.chinatower.device.repository.UavSecureStrategyService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * video-device
 * UavSecureStrategyController
 * 无人机安全策略控制层
 **/
@RestController
@Api(value = "无人机安全策略控制层")
@RequestMapping("/uav/strategy")
@Slf4j
public class UavSecureStrategyController extends BaseController {

    @Autowired
    private UavSecureStrategyService uavSecureStrategyService;

    /**
     * 查询无人机安全策略详情
     *
     * @param param 包含查询条件的无人机设备DTO对象
     * @return 包含查询结果的无人机详情VO对象
     */
    @PostMapping("/queryUavSecureStrategyDetail")
    public Result<UavSecureStrategyInfoVO> queryUavSecureStrategyDetail(@RequestBody UavDeviceDTO param) {
        return Result.data(uavSecureStrategyService.queryUavSecureStrategyDetail(param));
    }

    /**
     * 批量更新安全策略
     * @param param
     * @return
     */
    @PostMapping("/batchModifyUavSecureStrategy")
    public Result<String> batchModifyUavSecureStrategy(@RequestBody UavSecureStrategyInfoVO param) {
        return uavSecureStrategyService.batchModifyUavSecureStrategy(param);
    }
}
