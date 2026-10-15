package com.chinatower.video.api;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.GuardDTO;
import com.chinatower.video.api.entity.VideoRequestDTO;
import com.chinatower.video.api.factory.GuardFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "guardService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = GuardFallbackFactory.class)
public interface IGuardService {


    /**
     * 设置看守卫
     * @apiNote 设置看守卫
     * @apiVersion 5.29
     * @tag 五、视频能力
     * @requestDemo
     * {
     * 	"deviceCode": "43062400831327000001",
     * 	"channelCode": "43062400001130000002",
     * 	"presetId": 55297,
     * 	"guardName": "预置位1",
     * 	"staySeconds": 300,
     * 	"guardStatus": "1",
     * 	"tenantId": "122035807"
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *{
     * 	"code": 200,
     * 	"msg": "设置看守卫成功!",
     * 	"time": 1694757996723
     * }
     * @param guardDTO
     * @return
     */
    @ApiOperation(value = "设置看守卫", notes = "设置看守卫")
    @PostMapping("/video/patrol/sdk/doSetGuard")
    Result doSetGuard(@RequestBody GuardDTO guardDTO);

    /**
     * 5.3.故障告警消息报文
     *
     * @param: DeviceErrorVO
     * @return: com.chinatower.common.entity.Result
     * @auther: sutn
     * @date: 2022/3/15
     * @version: 1.0
     */
    @PostMapping("/video/patrol/sdk/getAlarmInfo")
    Result<JSONObject> getAlarmInfo(@RequestBody VideoRequestDTO videoRequestDTO);
}
