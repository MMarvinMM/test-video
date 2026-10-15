package com.chinatower.event.api.feign.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.feign.client.AlarmFlowDeployFeign;
import com.chinatower.event.api.model.AlarmByTaskReq;
import com.chinatower.event.api.model.IntelligentJudgeTaskRequest;
import com.chinatower.event.entity.dto.FlowTemplateIndustryDTO;
import com.chinatower.event.entity.vo.SecondRecogBackVO;
import com.chinatower.event.entity.vo.SecondRecogSendVO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AlarmFlowDeployFeignFallback implements AlarmFlowDeployFeign {

    @Setter
    private Throwable cause;

    @Override
    public Result<Object> addFlowLinkConfigIndustry(FlowTemplateIndustryDTO templateIndustryDTO) {
        log.error("新增行业、流程、环节绑定属性异常：服务降级:", cause);
        log.error("调用video-event服务新增行业、流程、环节绑定属性失败:{}", templateIndustryDTO.toString());
        return Result.fail("新增行业、流程、环节绑定属性失败");
    }

    @Override
    public Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustry(Map<String, String> params) {
        log.error("查询行业、流程、环节绑定属性异常：服务降级:", cause);
        log.error("调用video-event服务查询行业、流程、环节绑定属性失败:{}", params.toString());
        return Result.fail("查询行业、流程、环节绑定属性失败");
    }

    @Override
    public Result<Object> queryIotDeviceAndFactorInfo(Map<String, Object> params) {
        log.error("查询物联设备以及物联因子信息异常：服务降级:", cause);
        log.error("调用video-event服务查询物联设备以及物联因子失败:{}", params.toString());
        return Result.fail("查询物联设备以及物联因子失败");
    }
    @Override
    public Result<String> synAnalysisTask(List<SecondRecogSendVO> list) {
        log.info("synAnalysisTask二次识别下发失败：" + JSONObject.toJSONString(list));
        return Result.fail("二次识别下发失败");
    }

    @Override
    public Result<List<SecondRecogBackVO>> alarmByTaskBatchId(String taskBatchId) {
        log.info("alarmByTaskBatchId二次识别下发失败：" + JSONObject.toJSONString(taskBatchId));
        return null;
    }

    @Override
    public Result<List<SecondRecogBackVO>> alarmByTaskBatchIdSelf(AlarmByTaskReq alarmByTaskReq) {
        log.info("alarmByTaskBatchIdSelf二次识别下发失败：" + JSONObject.toJSONString(alarmByTaskReq));
        return null;
    }

    @Override
    public Result<String> secondRecogBack(List<SecondRecogBackVO> list) {
        log.info("二次识别结果更新失败：" + JSONObject.toJSONString(list));
        return Result.fail("二次识别结果更新失败");
    }

    @Override
    public Result<FlowTemplateIndustryDTO> queryFlowLinkConfigIndustryLocalVersion(Map<String, String> params) {
        return Result.fail("查询行业、流程、环节绑定属性失败");
    }

    @Override
    public Result<JSONObject> synIntelligentJudgeTask(IntelligentJudgeTaskRequest taskRequest) {
        return Result.fail("智能研判任务识别失败");
    }

    @Override
    public Result<Object> addFlowLinkConfigIndustryLocalVersion(FlowTemplateIndustryDTO templateIndustryDTO) {
        return Result.fail("新增行业、流程、环节绑定属性失败");
    }

    ;

}
