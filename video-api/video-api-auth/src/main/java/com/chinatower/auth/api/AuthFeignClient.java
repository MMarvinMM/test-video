package com.chinatower.auth.api;

import com.chinatower.auth.api.entity.LoginFeignVo;
import com.chinatower.auth.api.entity.SwitchLoginFeignVo;
import com.chinatower.auth.api.entity.WxColonyCodeVo;
import com.chinatower.auth.api.entity.WxUnionIdVo;
import com.chinatower.auth.api.factory.AuthFallbackFactory;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(contextId = "authApi", value = "auth", fallback = AuthFallbackFactory.class)
public interface AuthFeignClient {

    @PostMapping(value = "/userIdLogin")
    Result userIdLogin(@RequestParam("token") String token, @RequestHeader("Authorization") String Authorization);


    @PostMapping("/wx/colonyBind")
    Result<String> colonyBind(@RequestBody WxColonyCodeVo vo);


    @PostMapping("/wxLogin")
    Result wxLogin(@RequestBody WxUnionIdVo vo);

    @PostMapping("/doSwitchLogin")
    Result<String> doSwitchLogin(@RequestBody Map<String, String> param);

    @PostMapping("/checkSwitchId")
    Result<SwitchLoginFeignVo> checkSwitchId(@RequestBody SwitchLoginFeignVo switchLoginVO);

    @PostMapping("/chooseTrade")
    Result chooseTrade(@RequestBody LoginFeignVo vo, @RequestHeader("authorization") String authorization);

}
