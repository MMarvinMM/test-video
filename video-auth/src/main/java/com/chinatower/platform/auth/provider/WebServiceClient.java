//package com.chinatower.platform.auth.provider;
//
//import com.chinatower.common.core.web.domain.AjaxResult;
//import com.chinatower.platform.auth.provider.factory.RemoteMerchantServiceFallbackFactory;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//
//@FeignClient(value = "video-system",fallbackFactory = RemoteMerchantServiceFallbackFactory.class)
//public interface WebServiceClient {
//
//    /**
//     * 获取公共参数
//     * @param configKey
//     * @return
//     */
//    @RequestMapping(value = "/system/config/base/detail/{configKey}",method = RequestMethod.GET)
//    AjaxResult getConfValue(@PathVariable(value="configKey") String configKey);
//
//
//
//
//}
