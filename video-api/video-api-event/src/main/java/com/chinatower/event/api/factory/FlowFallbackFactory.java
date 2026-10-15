package com.chinatower.event.api.factory;

import com.chinatower.common.core.dto.FlowReq;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.IFlowService;
import com.chinatower.event.api.model.ApiAlarmFlowConfigModel;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.api.model.ApiFlowLinkAttrModel;
import com.chinatower.event.api.model.ApiFlowModel;
import com.chinatower.event.entity.dto.CheckCurentReq;
import com.chinatower.event.entity.dto.CheckCurrentResp;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.entity.dto.VmAlarmFlowConfigModel;
import com.chinatower.system.api.resp.SysDictDataResponse;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 流程相关降级接口
 * @author chmod
 * @version 1.0
 * @date 2022/3/2 10:15
 */
@Component
public class FlowFallbackFactory implements FallbackFactory<IFlowService> {

    private static final Logger log = LoggerFactory.getLogger(FlowFallbackFactory.class);

    @Override
    public IFlowService create(Throwable throwable) {
        log.error("事件服务调用失败:{}", throwable.getMessage());
        return new IFlowService() {
            @Override
            public Result<Map<String, Long>> genDefaultFlowInfo(FlowReq flowReq) {
                return Result.fail("生成默认告警流程失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<ApiFlowLinkAttrModel>> queryNextLink(ApiFlowLinkAttrModel model) {
                return Result.fail("获取下一步环节失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<ApiFlowModel>> queryFlowLink(ApiAlarmFlowConfigModel model) {
                return Result.fail("获取流程环节失败:" + throwable.getMessage());
            }

            @Override
            public Result queryRollbackLink(ApiAlarmOrderRecordModel model) {
                return Result.fail("获取回退环节失败:" + throwable.getMessage());
            }

            @Override
            public Result<Object> addAlarmFlowTypeAndConfig(VmAlarmFlowConfigModel model) {
                return Result.fail("订购初始化流程配置失败:" + throwable.getMessage());
            }

            @Override
            public Result<CheckCurrentResp> getCurrentByOrderIdAndLinkId(CheckCurentReq req) {
                return Result.fail("1.4.21.根据工单号和环节id（linkId）获取工单是否在当前环节失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SysDictDataResponse>> findAlarmSourceAll() {
                return Result.fail("查询所有的告警来源失败:" + throwable.getMessage());
            }

            @Override
            public Result<Object> queryAlarmFlowConfigDetail(VmAlarmFlowConfigModel configModel) {
                return Result.fail("查询告警流程详情异常:" + throwable.getMessage());
            }
        };
    }
}
