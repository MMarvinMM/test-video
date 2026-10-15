package com.chinatower.platform.feign;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.dataScope.CustomDataScopeEntity;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class CustomDataFeignClientFallbackFactory implements FallbackFactory<CustomDataFeignClient> {
        @Override
        public CustomDataFeignClient create(Throwable throwable) {
            log.info("feign调用异常信息是:{}", throwable.getMessage());
            return new CustomDataFeignClient() {

                @Override
                public Result<CustomDataScopeEntity> customDataScope(CustomDataScopeEntity customDataScopeEntity) {
                    return Result.fail("调用失败");
                }
            };
        }
}
