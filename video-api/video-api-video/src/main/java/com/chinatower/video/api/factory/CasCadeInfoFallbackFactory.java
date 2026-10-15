package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.CasCadeVO;
import com.chinatower.video.api.entity.GroupByNodeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class CasCadeInfoFallbackFactory implements FallbackFactory<ICasCadeInfoService> {


    @Override
    public ICasCadeInfoService create(Throwable throwable) {

        return new ICasCadeInfoService() {

            @Override
            public Result<CasCadeVO> getCasCadeByDeviceCode(String deviceCode) {
                return Result.fail("根据设备编码查询from-to信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<CasCadeVO> getCasCadeByDeviceInfo(String deviceCode, Integer deviceType) {
                return Result.fail("根据设备信息查询from-to信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> getCurrentPlatformCode() {
                return Result.fail("根据当前平台节点编码信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<CasCadeVO>> getCasCadeByDeviceCodeBatch(List<String> list) {
                return Result.fail("根据设备编码批量查询from-to信息失败:" + throwable.getMessage());
            }
            @Override
            public Result<CasCadeVO> getCasCadeByTaskId(String taskId){
                return Result.fail("根据taskId查询from-to信息失败:" + throwable.getMessage());
            }
            @Override
            public Result<List<CasCadeVO>> getCasCadeByChannelCodeBatch(List<String> list) {
                return Result.fail("根据通道编码批量查询from-to信息失败:" + throwable.getMessage());
            }
            @Override
            public Result<Map<String,String>> getCasCadeMapByDeviceCodeBatch(List<String> list){
                return Result.fail("根据设备编码批量查询from-to信息map结构失败:" + throwable.getMessage());
            }
            @Override
            public Result<Map<String,String>> getCasCadeMapByChannelCodeBatch(List<String> list){
                return Result.fail("根据通道编码批量查询from-to信息map结构失败:" + throwable.getMessage());
            }
//            @Override
//            public Result<List<Map>> groupByNode(@RequestBody GroupByNodeVO groupByNodeVO){
//                return Result.fail("分组失败:" + throwable.getMessage());
//            }
        };
    }
}
