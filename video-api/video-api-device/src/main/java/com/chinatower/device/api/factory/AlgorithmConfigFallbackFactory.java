package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.IAlgorithmConfigService;
import com.chinatower.platform.entity.device.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @version 1.0
 * @ClassName AlgorithmConfigFallbackFactory
 * @Description 算法配置服务降级处理
 * @Author chenshaowu
 * @Date 2022/2/11
 **/
@Component
public class AlgorithmConfigFallbackFactory implements FallbackFactory<IAlgorithmConfigService> {

    @Override
    public IAlgorithmConfigService create(Throwable throwable) {
        return new IAlgorithmConfigService() {
            @Override
            public Result save(AlgorithmListVO algorithmListVO) {
                return Result.fail("算法配置新增失败:" + throwable.getMessage());
            }
            @Override
            public Result queryById(AlgorithmConfigDTO algorithmConfigDTO) {
                return Result.fail("算法配置查看失败:" + throwable.getMessage());
            }
            @Override
            public Result update(AlgorithmConfigDTO algorithmConfigDTO) {
                return Result.fail("算法配置编辑失败:" + throwable.getMessage());
            }
            @Override
            public Result enabledStatus(AlgorithmConfigDTO algorithmConfigDTO) {
                return Result.fail("算法配置启用失败" + throwable.getMessage());
            }
            @Override
            public Result disabledStatus(AlgorithmConfigDTO algorithmConfigDTO) {
                return Result.fail("算法配置停用失败" + throwable.getMessage());
            }
            @Override
            public Result delete(AlgorithmConfigDTO algorithmConfigDTO) {
                return Result.fail("算法配置删除失败" + throwable.getMessage());
            }
            @Override
            public TableDataInfo queryAlgorithm(AlgorithmConfigDTO algorithmConfigDTO) {
                TableDataInfo failResult = new TableDataInfo();
                failResult.setCode(ResultCode.FAILURE.getCode());
                return failResult;
            }
            @Override
            public Result synchAlgorithmConfigStatus(AlgorithmAreaVO algorithmAreaVO){
                return Result.fail("查询算法状态失败" + throwable.getMessage());
            }

            @Override
            public int getAlgorithmConfigCount(DelPresetReq presetDTO) {
                return 0;
            }

            @Override
            public String getDeviceCode(PresetDTO presetDTO) {
                return "获取设备编码失败";
            }

            @Override
            public Result<String> algorithmGiveMergeOrder(AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO) {
                return Result.fail("订单调用前端算法整合失败！");
            }
        };
    }
}
