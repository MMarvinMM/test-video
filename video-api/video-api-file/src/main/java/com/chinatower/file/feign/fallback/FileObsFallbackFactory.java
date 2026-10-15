package com.chinatower.file.feign.fallback;

import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.file.feign.client.FileObsFeignClient;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.request.ToEncryptUrl;
import com.chinatower.file.response.FileObsEncryptResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @className: ObsEncryptFallbackFactory
 * @description:
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Slf4j
@Component
public class FileObsFallbackFactory implements FallbackFactory<FileObsFeignClient> {


    @Override
    public FileObsFeignClient create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new FileObsFeignClient()
        {
            /**
             * 5.16	 获取加密后的OBS文件地址
             */
            @Override
            public ApiResult getObsEncryptUrls(FileObsEncryptRequest fileObsEncryptRequest) {
                log.error("调用video-file获取加密后的OBS文件失败:{}", fileObsEncryptRequest.toString());
                return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG,ApiResult.ERROR_CODE_1004,"内部服务调用异常");
            }

            @Override
            public Map<String, String> encryptBatch(List<String> urlList) {
                return null;
            }

            @Override
            public Object genericEncryptBatch(Object obj) {
                return null;
            }

            @Override
            public Boolean isObs(String url) {
                return false;
            }

            @Override
            public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrlsSDK(FileObsEncryptRequest fileObsEncryptRequest) {
                return null;
            }
        };
    }
}
