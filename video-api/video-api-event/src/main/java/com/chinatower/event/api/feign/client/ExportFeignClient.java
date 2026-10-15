package com.chinatower.event.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.factory.ExportFeignFallbackFactory;
import com.chinatower.event.entity.dto.DownloadVideoByUrlReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * video-export 导出服务接口（事件侧调用）
 */
@FeignClient(contextId = "ExportFeignClient", value = ServiceNameConstants.EXPORT_SERVICE, fallbackFactory = ExportFeignFallbackFactory.class)
public interface ExportFeignClient {

    /**
     * 通过视频URL加水印并上传，返回上传后的URL
     */
    @PostMapping(value = "/alarm/order/downloadVideoByUrl", consumes = MediaType.APPLICATION_JSON_VALUE)
    Result<String> downloadVideoByUrl(@RequestBody DownloadVideoByUrlReq req);
}

