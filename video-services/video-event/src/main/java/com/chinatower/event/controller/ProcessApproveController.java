package com.chinatower.event.controller;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.repository.IProcessApproveRepository;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-event
 * <p>
 * 事件审批表 前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-15
 */
@Slf4j
@RestController
@RequestMapping("/alarm/orderApprove")
public class ProcessApproveController extends TowerBaseController {

    @Autowired
    private IProcessApproveRepository processApproveRepository;

    /**
     * 功能描述：告警事件核实
     *
     * @param dto
     * @return Result
     * @author chenminfeng
     * @date 2022/6/15 13:38
     * @version:
     */
    @PostMapping("/submitOrderApproveInfo")
    public Result submitOrderApproveInfo(@RequestBody SendAlarmOrderDTO dto) {
        log.info("ProcessApproveController-submitOrderApproveInfo入参:{}", JSON.toJSONString(dto));
        return processApproveRepository.submitOrderApproveInfo(dto);
    }

    /**
     * 功能描述：会签完成流转环节处理
     *
     * @param dto
     * @return Result
     * @author chenminfeng
     * @date 2022/6/15 13:38
     * @version:
     */
    @PostMapping("/signSubmitOrderApproveInfo")
    public Result signSubmitOrderApproveInfo(@RequestBody SendAlarmOrderDTO dto) {
        return processApproveRepository.signSubmitOrderApproveInfo(dto);
    }
}
