package com.chinatower.system.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.system.api.feign.factory.ObsEncryptFallbackFactory;
import com.chinatower.system.api.request.file.ObsEncryptRequest;
import com.chinatower.system.api.resp.ObsEncryptResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "ObsEncryptFeignClient", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = ObsEncryptFallbackFactory.class)
public interface ObsEncryptFeignClient {

    /**
     * 获取加密后的OBS文件地址
     * @apiNote 获取加密后的OBS文件地址
     * @apiVersion 6.1
     * @tag 六、基础功能能力
     * @param obsEncryptRequest
     * @requestDemo
     * ‘{
     *     "fileUrls":["https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/e48b8a231ce8a7af3cf180866b8dbe83/385fe1d409734a97a2a0dd67e6e6642f.zip"]
     * }’
     * @responseDemo
     *
     * returnCode 0000调用成功 8888调用失败
     * fileUrl 原始url
     * encryptUrl 加密后的url
     * ‘{
     * 	"data": [
     *                {
     * 			"encryptUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/e48b8a231ce8a7af3cf180866b8dbe83/385fe1d409734a97a2a0dd67e6e6642f.zip?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1693799786&Signature=P8ZoXNYaB%2BxtOC6p2G/nnMb5BGs%3D",
     * 			"fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/e48b8a231ce8a7af3cf180866b8dbe83/385fe1d409734a97a2a0dd67e6e6642f.zip"
     *        }
     * 	],
     * 	"returnCode": "0000",
     * 	"returnMsg": "请求成功"
     * }’
     * @return ApiResult<List<ObsEncryptResponse>>
     */
    @PostMapping(value = "/file/obsFiles/getObsEncryptUrls")
    public ApiResult<List<ObsEncryptResponse>> getObsEncryptUrls(@RequestBody ObsEncryptRequest obsEncryptRequest);

}
