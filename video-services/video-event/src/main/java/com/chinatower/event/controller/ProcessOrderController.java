package com.chinatower.event.controller;


import com.alibaba.fastjson.JSON;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.common.core.dto.ProcessOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.repository.IProcessOrderRepository;

/**
 * video-event
 * <p>
 *  前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Slf4j
@RestController
@RequestMapping("/alarm/processorder")
public class ProcessOrderController extends TowerBaseController {


    @Autowired
    private IProcessOrderRepository processOrderRepository;


    /**
     * 功能描述：告警事件处理（处置）
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:39
     * @version:
     */
    @PostMapping("/submitProcessOrder")
    public Result submitProcessOrder(@RequestBody SendAlarmOrderDTO dto) {
        log.info("ProcessOrderController-submitProcessOrder入参:{}", JSON.toJSONString(dto));
        return processOrderRepository.submitProcessOrder(dto);
    }

    /**
     * 功能描述：获取处置单详情
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:39
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/getProcessDetail")
    public Result getProcessDetail(@RequestBody ProcessOrderDTO dto) {
        return processOrderRepository.getProcessDetail(dto);
    }

    /**
     * 功能描述：流程回退
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:39
     * @version:
     */
    @PostMapping("/rollBack")
    public Result rollBack(@RequestBody SendAlarmOrderDTO dto) {
        return processOrderRepository.rollBack(dto);
    }
}
