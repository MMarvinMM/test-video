package com.chinatower.event.controller;

import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.dto.BatchDelWarningOrderDTO;
import com.chinatower.event.dto.BatchEndWarningOrderDTO;
import com.chinatower.event.dto.BatchJudgeWarningOrderDTO;
import com.chinatower.event.repository.IWarningOrderBatchRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * video-event
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderBatchController.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/29 16:06
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/alarm/orderBatch")
public class WarningOrderBatchController extends TowerBaseController {

    @Autowired
    private IWarningOrderBatchRepository warningOrderBatchRepository;


    /**
     * 功能描述：批量删除告警数据
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:44
     * @version:
     */
    @TowerLog(title = "批量删除", businessType = BusinessType.DELETE)
    @PostMapping("/batchDelWarningOrder")
    public Result batchDelWarningOrder(@RequestBody BatchDelWarningOrderDTO dto) {
        return warningOrderBatchRepository.batchDelWarningOrder(dto);
    }

    /**
     * 功能描述：批量关闭告警
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:44
     * @version:
     */
    @TowerLog(title = "批量关闭", businessType = BusinessType.UPDATE)
    @PostMapping("/batchEndWarningOrder")
    public Result batchEndWarningOrder(@Validated @RequestBody BatchEndWarningOrderDTO dto) {
        return warningOrderBatchRepository.batchEndWarningOrder(dto);
    }

    /**
     * 功能描述：批量研判
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:44
     * @version:
     */
    @TowerLog(title = "批量研判", businessType = BusinessType.UPDATE)
    @PostMapping("/batchJudgeWarningOrder")
    public Result batchJudgeWarningOrder(@Validated @RequestBody BatchJudgeWarningOrderDTO dto) {
        return warningOrderBatchRepository.batchJudgeWarningOrder(dto);
    }
}
