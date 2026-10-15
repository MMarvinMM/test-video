package com.chinatower.system.api.feign.factory;

import com.chinatower.common.core.domain.R;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.system.api.feign.client.ObsEncryptFeignClient;
import com.chinatower.system.api.feign.client.RemoteUserService;
import com.chinatower.system.api.model.LoginUser;
import com.chinatower.system.api.request.file.ObsEncryptRequest;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @className: ObsEncryptFallbackFactory
 * @description:
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Slf4j
@Component
public class ObsEncryptFallbackFactory implements FallbackFactory<ObsEncryptFeignClient> {


    @Override
    public ObsEncryptFeignClient create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new ObsEncryptFeignClient()
        {
            /**
             * 5.16	 获取加密后的OBS文件地址
             */
            @Override
            public ApiResult getObsEncryptUrls(ObsEncryptRequest obsEncryptRequest) {
                log.error("调用video-system获取加密后的OBS文件失败:{}", obsEncryptRequest.toString());
                return ApiResult.FAIL(ApiResult.RETURN_CODE_ERR_MSG,ApiResult.ERROR_CODE_1004,"内部服务调用异常");
            }
        };
    }
}
