package com.chinatower.event.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "weChatFeignClient", url = "https://api.weixin.qq.com", path = "/cgi-bin")
public interface WeChatFeignClient {
    @GetMapping("/token")
    String getAccessToken(@RequestParam Map<String, Object> map);
    @GetMapping("/ticket/getticket")
    String getJsapiTicket(@RequestParam Map<String, Object> map);
}
