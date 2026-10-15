package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.GroupByNodeVO;
import com.chinatower.video.api.factory.CasCadeInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@FeignClient(contextId = "casCadeInfoService", value = ServiceNameConstants.VIDEO_SERVICE, fallbackFactory = CasCadeInfoFallbackFactory.class)
public interface ICasCadeInfoService {
    @GetMapping(value = "/getCasCadeByDeviceCode")
    Result<CasCadeVO> getCasCadeByDeviceCode(@RequestParam("deviceCode") String deviceCode);

    @GetMapping(value = "/getCasCadeByDeviceInfo")
    Result<CasCadeVO> getCasCadeByDeviceInfo(@RequestParam("deviceCode") String deviceCode,
                                             @RequestParam("deviceType") Integer deviceType);

    @GetMapping(value = "/getCurrentPlatformCode")
    Result<String> getCurrentPlatformCode();

    @PostMapping(value = "/getCasCadeByDeviceCodeBatch")
    Result<List<CasCadeVO>> getCasCadeByDeviceCodeBatch(@RequestBody List<String> list);

    @GetMapping(value = "/getCasCadeByTaskId")
    Result<CasCadeVO> getCasCadeByTaskId(@RequestParam("taskId") String taskId);

    @PostMapping(value = "/getCasCadeByChannelCodeBatch")
    Result<List<CasCadeVO>> getCasCadeByChannelCodeBatch(@RequestBody List<String> list);

    @PostMapping(value = "/getCasCadeMapByDeviceCodeBatch")
    Result<Map<String,String>> getCasCadeMapByDeviceCodeBatch(@RequestBody List<String> list);

    @PostMapping(value = "/getCasCadeMapByChannelCodeBatch")
    Result<Map<String,String>> getCasCadeMapByChannelCodeBatch(@RequestBody List<String> list);

//    @PostMapping(value = "/groupByNode")
//    Result<List<Map>> groupByNode(@RequestBody GroupByNodeVO groupByNodeVO);
}
