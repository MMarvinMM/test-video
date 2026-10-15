//package com.chinatower.platform.auth.provider.fallback;
//
//import com.chinatower.common.core.web.domain.AjaxResult;
//import com.chinatower.platform.auth.provider.WebServiceClient;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//
//@Slf4j
//@Component
//public class WebServiceFallback implements WebServiceClient {
//
//
//    @Setter
//    private Throwable cause;
//
//    @Override
//    public AjaxResult getConfValue(String dictType) {
//        log.error("获取字典值异常：服务降级:{}",cause);
//        log.error("服务名：chinatower-energy-exchange-web-admin，接口地址：/base/system/dict/data/dictType，参数：{}，调用失败！", dictType);
//        return AjaxResult.error();
//    }
//
//}
