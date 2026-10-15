package com.chinatower.bdm.api.factory;



import com.chinatower.bdm.api.ResourceTypeAPI;
import com.chinatower.bdm.api.entity.ResourceTreeSelect;
import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResourceTypeFallbackFactory implements FallbackFactory<ResourceTypeAPI> {
    @Override
    public ResourceTypeAPI create(Throwable throwable) {
        return new ResourceTypeAPI() {
            @Override
            public Result<List<ResourceType>> selectResourceType(ResourceType resourceType) {
                return Result.fail("资源类型获取调用失败"+throwable.getMessage());
            }

            @Override
            public Result<List<ResourceTreeSelect>> selectResourceTypeTree(ResourceType resourceType) {
                return Result.fail("资源类型获取调用失败"+throwable.getMessage());
            }

            @Override
            public Result initResourceType(ResourceTypeInt resourceTypeInt) {
                return Result.fail("资类型数据初始化调用失败"+throwable.getMessage());
            }
        };
    }
}
