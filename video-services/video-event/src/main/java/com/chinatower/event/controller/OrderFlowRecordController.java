package com.chinatower.event.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.dto.OrderFlowRecordDTO;
import com.chinatower.event.dto.SelectLastProcess.SelectLastRrocessReq;
import com.chinatower.event.dto.current.CheckCurrentReq;
import com.chinatower.event.dto.current.CheckCurrentResp;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.repository.IOrderFlowRecordRepository;
import com.chinatower.event.vo.OrderFlowRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * video-event
 * <p>
 * 告警事件工单流程表 前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Slf4j
@RestController
@RequestMapping("/flow/order")
public class OrderFlowRecordController {
    @Autowired
    private IOrderFlowRecordRepository orderFlowRecordRepository;

    /**
     * 功能描述：查询回退环节
     *
     * @param vo
     * @return Result<List<FlowLinkConfig>>
     * @author chenminfneg
     * @date 2022/6/15 13:36
     * @version:
     */
    @PostMapping("/queryRollbackLink")
    public Result<List<FlowLinkAttrModel>> queryRollbackLink(@RequestBody @Validated ApiAlarmOrderRecordModel vo) {
        return Result.data(orderFlowRecordRepository.queryRollbackLink(vo));
    }

    /**
     * 功能描述：获取当前工单最后一个处理过环节的流程处理人
     *
     * @param dto
     * @return Result
     * @version:
     */
    @PostMapping("/queryLastProcess")
    public Result queryLastProcess(@RequestBody SelectLastRrocessReq dto) {
        log.info("queryLastProcess:获取最后一个环节处理人接口入参：{}", JSON.toJSONString(dto));
        return orderFlowRecordRepository.queryLastProcess(dto);
    }


    /**
     * 功能描述：通过主键获取处理环节信息
     *
     * @param dto
     * @return Result<OrderFlowRecordVO>
     * @author chenminfeng
     * @date 2022/6/15 13:37
     * @version:
     */
    @PostMapping("/getRecordById")
    @EventObsEncrypt
    public Result<OrderFlowRecordVO> getRecordById(@RequestBody OrderFlowRecordDTO dto) {
        return orderFlowRecordRepository.getRecordById(dto);
    }

    /**
     * 功能描述：根据工单号&linkId获取工单是否在当前环节
     *
     * @param req
     * @return Result<OrderFlowRecordVO>
     * @author chenminfeng
     * @date 2022/6/15 13:37
     * @version:C
     */
    @PostMapping("/getCurrentByOrderIdAndLinkId")
    public Result<CheckCurrentResp> getCurrentByOrderIdAndLinkId(@RequestBody CheckCurrentReq req) {
        return orderFlowRecordRepository.getCurrentByOrderIdAndLinkId(req);
    }
}
