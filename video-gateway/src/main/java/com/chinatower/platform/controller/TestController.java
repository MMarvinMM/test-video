package com.chinatower.platform.controller;

import com.chinatower.platform.api.dto.InterfaceOpeLog;
import com.chinatower.platform.entity.admin.SysInterfaceInfo;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class TestController {



    @GetMapping("/test/get")
    public  void  test(@RequestParam String name){
        log.info("name:{}",name);
    }


    @PostMapping("/test/post1")
    public  void  testPost(@RequestBody InterfaceOpeLog logVO){
        log.info( "log:{}",logVO);
    }

    @PostMapping("/test/post2")
    public  void  testPost2(@RequestBody InterfaceOpeLog logVO){
        log.info("tower_interface_log:"+logVO.getOperUrl());
        SysInterfaceInfo sysInterfaceInfo = RedisUtil.get("tower_interface_log:"+logVO.getOperUrl() , SysInterfaceInfo.class, RedisDBType.PRIMARY);
        log.info( "log:{}",sysInterfaceInfo);
    }
}
