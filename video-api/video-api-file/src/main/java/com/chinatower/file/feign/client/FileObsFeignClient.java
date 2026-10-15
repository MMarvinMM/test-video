package com.chinatower.file.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.file.feign.fallback.FileObsFallbackFactory;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @className: ObsEncryptFeignClient
 * @description: 5.16. 获取加密后的OBS文件地址
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */

@FeignClient(contextId = "FileObsFeignClient", value = ServiceNameConstants.VIDEO_FILE_SERVICE, fallbackFactory = FileObsFallbackFactory.class)
public interface FileObsFeignClient {

    /**
     * 获取加密后的OBS文件地址
     * @apiNote 获取加密后的OBS文件地址
     * @apiVersion 9.10
     * @tag 九、文件能力
     * @return
     */
    @PostMapping(value = "/file/obsFiles/getObsEncryptUrls")
    public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrls(@RequestBody FileObsEncryptRequest fileObsEncryptRequest);
    /**
     * 批量获取加密后的OBS文件地址
     * @apiNote 批量获取加密后的OBS文件地址
     * @apiVersion 9.11
     * @tag 九、文件能力
     * @return
     */
    @PostMapping("/file/obsFiles/encryptBatch")
    public Map<String, String> encryptBatch(@RequestBody List<String> urlList);

    @PostMapping("/file/obsFiles/genericEncryptBatch")
    public Object genericEncryptBatch(@RequestBody Object obj);


    /**
     * 判断当前url是否为obs地址
     * @param url
     * @return
     */
    @PostMapping("/file/obsFiles/isObs")
    public Boolean isObs(@RequestBody String url);


    /**
     * SDK OBS加密url获取
     * @param fileObsEncryptRequest
     * @return
     */
    @PostMapping(value = "/file/obsFiles/getObsEncryptUrlsSDK")
    public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrlsSDK(@RequestBody FileObsEncryptRequest fileObsEncryptRequest);
}
