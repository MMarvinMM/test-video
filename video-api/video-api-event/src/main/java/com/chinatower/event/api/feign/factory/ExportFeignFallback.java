package com.chinatower.event.api.feign.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.client.ExportFeignClient;
import com.chinatower.event.entity.dto.DownloadVideoByUrlReq;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExportFeignFallback implements ExportFeignClient {

    @Setter
    private Throwable cause;

    @Override
    public Result<String> downloadVideoByUrl(DownloadVideoByUrlReq req) {
        log.error("调用video-export服务 downloadVideoByUrl 异常：服务降级:", cause);
        log.error("downloadVideoByUrl 请求参数:{}", JSONObject.toJSONString(req));
        return Result.fail("调用导出服务失败");
    }
}

