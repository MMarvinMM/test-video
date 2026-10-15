package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.IMemoryService;
import com.chinatower.video.api.entity.MemoryVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName MemoryFallbackFactory
 * @Description
 * @Author lyy
 * @Date 2025/07/15
 **/
@Component
public class MemoryFallbackFactory implements FallbackFactory<IMemoryService> {
    @Override
    public IMemoryService create(Throwable throwable) {
        return new IMemoryService() {
            @Override
            public Result uptUserMemoryInfo(MemoryVO memoryVO) {
                return Result.fail("更新用户记忆点缓存信息失败:" + throwable.getMessage());
            }

            @Override
            public Result getUserMemoryInfo(MemoryVO memoryVO) {
                return Result.fail("查看用户记忆点缓存信息失败:" + throwable.getMessage());
            }
        };
    }
}
