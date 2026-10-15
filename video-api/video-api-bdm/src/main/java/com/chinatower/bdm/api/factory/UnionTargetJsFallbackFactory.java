package com.chinatower.bdm.api.factory;


import com.chinatower.bdm.api.UnionTargetJsAPI;
import com.chinatower.bdm.api.entity.UnionTargetJsParam;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UnionTargetJsFallbackFactory implements FallbackFactory<UnionTargetJsAPI> {

    @Override
    public UnionTargetJsAPI create(Throwable throwable) {
        return new UnionTargetJsAPI() {
            @Override
            public Result targetFollow(UnionTargetJsParam unionTargetJsParam) {
                return Result.fail("应用平台通知雷达平台启动/关闭联动光电跟踪监测目标调用失败"+throwable.getMessage());
            }


        };
    }
}
