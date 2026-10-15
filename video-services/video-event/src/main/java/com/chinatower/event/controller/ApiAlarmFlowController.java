package com.chinatower.event.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.model.ApiAlarmFlowConfigModel;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.api.model.ApiFlowLinkAttrModel;
import com.chinatower.event.api.model.ApiFlowLinkConfigModel;
import com.chinatower.event.api.model.ApiFlowModel;
import com.chinatower.event.entity.dto.AlarmFlowConfigModel;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.entity.dto.FlowModel;
import com.chinatower.event.repository.IAlarmFlowConfigService;
import com.chinatower.event.repository.IFlowLinkAttrService;
import com.chinatower.event.repository.IOrderFlowRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * video-event
 * 告警流程API控制器
 * @author chmod
 * @version 1.0
 * @date 2022/3/23 17:25
 */
@RestController
@Slf4j
public class ApiAlarmFlowController {
    @Autowired
    private IFlowLinkAttrService flowLinkAttrService;
    @Autowired
    private IAlarmFlowConfigService alarmFlowConfigService;
    @Autowired
    private IOrderFlowRecordRepository orderFlowRecordRepository;

    /**
     * 功能描述：查询下一步所有环节
     *
     * @param model
     * @return Result<List<ApiFlowLinkAttrModel>>
     * @author zhen.yan
     * @date 2022/6/15 11:28
     * @version:
     */
    @PostMapping("/api/flow/alarm/queryNextLink")
    public Result<List<ApiFlowLinkAttrModel>> queryNextLink(@RequestBody ApiFlowLinkAttrModel model) {
        log.info("获取下一环节接口入参：{}", JSON.toJSONString(model));
        FlowLinkAttrModel flowLinkAttrModel = new FlowLinkAttrModel();
        BeanUtils.copyProperties(model, flowLinkAttrModel);
        Result<List<ApiFlowLinkAttrModel>> result;
        //接口传入的flowId实际是alarmFlowId
        flowLinkAttrModel.setAlarmFlowId(flowLinkAttrModel.getFlowId());
        flowLinkAttrModel.setFlowId(null);
        Result<List<FlowLinkAttrModel>> queryResult = flowLinkAttrService.queryNextLinkNew(flowLinkAttrModel);
        if (ResultCode.SUCCESS.getCode() == queryResult.getCode()) {
            List<FlowLinkAttrModel> list = queryResult.getData();
            List<ApiFlowLinkAttrModel> resultList = new LinkedList<>();
            for (FlowLinkAttrModel attrModel : list) {
                ApiFlowLinkAttrModel item = new ApiFlowLinkAttrModel();
                BeanUtils.copyProperties(attrModel, item);
                resultList.add(item);
            }
            result = Result.success(queryResult.getMsg());
            result.setData(resultList);
        } else {
            result = Result.fail(queryResult.getCode(), queryResult.getMsg());
        }
        return result;
    }

    /**
     * 功能描述：根据流程ID查询流程环节
     *
     * @param model
     * @return Result<List<ApiFlowModel>>
     * @author zhen.yan
     * @date 2022/6/15 11:28
     * @version:
     */
    @PostMapping("/api/flow/alarm/queryFlowLink")
    public Result<List<ApiFlowModel>> queryFlowLink(@RequestBody ApiAlarmFlowConfigModel model) {
        log.info("获取流程环节接口：{}", JSON.toJSONString(model));
        AlarmFlowConfigModel alarmFlowConfigModel = new AlarmFlowConfigModel();
        BeanUtils.copyProperties(model, alarmFlowConfigModel);
        Result<List<ApiFlowModel>> result;
        Result<List<FlowModel>> queryResult = alarmFlowConfigService.queryFlowLink(alarmFlowConfigModel);
        if (ResultCode.SUCCESS.getCode() == queryResult.getCode()) {
            List<FlowModel> list = queryResult.getData();
            List<ApiFlowModel> resultList = new LinkedList<>();
            for (FlowModel flowModel : list) {
                ApiFlowModel item = new ApiFlowModel();
                BeanUtils.copyProperties(flowModel, item);
                resultList.add(item);
            }
            result = Result.success(queryResult.getMsg());
            result.setData(resultList);
        } else {
            result = Result.fail(queryResult.getCode(), queryResult.getMsg());
        }
        return result;
    }

    /**
     * 功能描述：行业调用查询回退环节
     *
     * @param model
     * @return Result<List<ApiFlowLinkConfigModel>>
     * @author zhen.yan
     * @date 2022/6/15 11:29
     * @version:
     */
    @PostMapping("/api/flow/alarm/queryRollbackLink")
    public Result<List<ApiFlowLinkConfigModel>> queryRollbackLink(@RequestBody @Validated ApiAlarmOrderRecordModel model) {
        List<FlowLinkAttrModel> queryList = orderFlowRecordRepository.queryRollbackLink(model);
        List<ApiFlowLinkConfigModel> resultList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(queryList)) {
            for (FlowLinkAttrModel flowLinkConfig : queryList) {
                ApiFlowLinkConfigModel item = new ApiFlowLinkConfigModel();
                BeanUtils.copyProperties(flowLinkConfig, item);
                resultList.add(item);
            }
        }

        return Result.data(resultList);
    }

}
