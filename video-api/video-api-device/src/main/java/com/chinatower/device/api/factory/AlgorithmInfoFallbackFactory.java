package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IAlgorithmInfoServiceMigrate;
import com.chinatower.device.entity.vo.FunctionTypeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName AlgorithmInfoFallbackFactory
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/14
 **/
@Component
public class AlgorithmInfoFallbackFactory implements FallbackFactory<IAlgorithmInfoServiceMigrate> {
    @Override
    public IAlgorithmInfoServiceMigrate create(Throwable throwable) {
        return new IAlgorithmInfoServiceMigrate() {
            @Override
            public Result<Map> getFunction(String tenantId) {
                return Result.fail("获取功能信息推送失败:" + throwable.getMessage());
            }
            @Override
            public Result<FunctionTypeVO> getFunctionDetail(String id) {
                return Result.fail("获取功能详情推送失败:" + throwable.getMessage());
            }
        };
    }
}
