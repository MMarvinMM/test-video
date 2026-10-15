package com.chinatower.platform.controller;

import com.chinatower.platform.cascade.CascadeService;
import com.xxl.job.core.biz.model.ReturnT;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-out-data
 */
@RestController
@Slf4j
@RequestMapping("/cascade/")
public class CascadeController {

    @Autowired
    private CascadeService cascadeService;

    /**
     * 每30s向级联平台发一次心跳
     * @return
     */
    @ApiOperation(value = "每30s向级联平台发一次心跳")
    @PostMapping("/cascadeHeartBeat")
    public ReturnT<String> cascadeHeartBeat(){
        return cascadeService.cascadeHeartBeat(null);
    }
}

