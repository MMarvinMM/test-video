package com.chinatower.product.api.modules.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-service-api
 */
@RestController
public class SdkToApiController {

    /**
     * sdkToApi暴露接口（废弃接口-无入参无返回）
     * 复杂
     * 1.加载所有sdk jar包中的所有类，方法（url+请求方式<POST/GET/DELETE等>）缓存至内存
     * com.tieta.example.test.sdk.ScanAllAnnotation
     * 2.拦截所有请求，通过请求前缀识别是否是api请求调用，所有api请求执行转发请求操作。
     * 2.1 根据url+请求方式 -> 类及方法，通过发射调用接口，返回结果
     */
    @PostMapping("/api/sdkToApi")
    public String reflect() {
        return null;
    }
}
