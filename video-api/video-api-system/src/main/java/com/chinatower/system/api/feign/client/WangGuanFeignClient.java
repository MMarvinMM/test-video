package com.chinatower.system.api.feign.client;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.factory.WangGuanrFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "WangGuanFeignClient", value = ServiceNameConstants.GATEWAY_ADMIN_OUT_SERVICE, fallbackFactory = WangGuanrFallbackFactory.class)
public interface WangGuanFeignClient {


    /**
     * 中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     * @apiNote 中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     * @apiVersion 6.96
     * @tag 六、基础功能能力
     * @requestDemo
     * {
     * 	"appKey": "111",
     * 	"APPSECRET": "222",
     * 	"clientId": "120112211",
     * 	"urlVoList": [
     *                {
     * 			"url": "www.baidu.com",
     * 			"eventId": "999"
     *        }
     * 	]
     * }
     * @responseDemo
     * {
     * 	"code": 200,
     * 	"msg": "推送成功",
     * 	"time": 1719825660457
     * }
     * @param jsonObject
     * @return Result
     */
    @PostMapping(value = "/appSignAuth")
    public Result appSignAuth(@RequestBody JSONObject jsonObject);




}
