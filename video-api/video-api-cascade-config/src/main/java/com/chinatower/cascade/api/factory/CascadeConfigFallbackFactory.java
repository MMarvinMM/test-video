package com.chinatower.cascade.api.factory;


import com.chinatower.cascade.api.CascadeConfigAPI;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
public class CascadeConfigFallbackFactory implements FallbackFactory<CascadeConfigAPI> {

    @Override
    public CascadeConfigAPI create(Throwable throwable) {
        return new CascadeConfigAPI() {
            @Override
            public Result<List<PlatformDataConfigVO>> getInfo(@RequestParam("list") List<String> list) {
                return Result.fail("平台配置信息详情失败"+throwable.getMessage());
            }
        };
    }
}
