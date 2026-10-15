package com.chinatower.auth.api.factory;

import com.chinatower.auth.api.NodeServiceFeignClient;
import com.chinatower.auth.api.entity.AuthReplaceTokenVO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class NodeServiceFeignFallback implements FallbackFactory<NodeServiceFeignClient> {
    @Override
    public NodeServiceFeignClient create(Throwable throwable) {
        return new NodeServiceFeignClient() {
            @Override
            public Result<String> replaceToken(AuthReplaceTokenVO authReplaceTokenVO) {
                return null;
            }
        };
    }
}
