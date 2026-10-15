package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAlgorithmInfoSyncService;
import com.chinatower.device.entity.dto.AlgoCommonReq;
import com.chinatower.device.entity.dto.AlgoInfoReq;
import com.chinatower.device.entity.dto.AlgoTypeReq;
import com.chinatower.device.entity.dto.FuncTypeReq;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName AlgorithmInfoFallbackFactory
 * @Description
 * @Author chenshaowu
 * @Date 2022/3/14
 **/
@Component
public class AlgorithmInfoSyncFallbackFactory implements FallbackFactory<IAlgorithmInfoSyncService> {
    @Override
    public IAlgorithmInfoSyncService create(Throwable throwable) {
        return new IAlgorithmInfoSyncService() {
            @Override
            public Result<String> pushAlgorithmInfo(@RequestBody AlgoCommonReq<AlgoInfoReq> req) {
                return Result.fail("算法基本信息推送失败:" + throwable.getMessage());
            }
            @Override
            public Result<String> pushAlgorithmType(@RequestBody AlgoCommonReq<AlgoTypeReq> req) {
                return Result.fail("算法类型推送失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> pushFunctionType(@RequestBody AlgoCommonReq<FuncTypeReq> req) {
                return Result.fail("功能类型推送失败:" + throwable.getMessage());
            }
        };
    }
}
