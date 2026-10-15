package com.chinatower.event.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.dto.OrderLeaderAuditDTO;
import com.chinatower.event.repository.IOrderLeaderAuditRepository;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-event
 * <p>
 *  前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@RestController
@RequestMapping("/alarm/orderleaderaudit")
public class OrderLeaderAuditController extends TowerBaseController {

    @Autowired
    private IOrderLeaderAuditRepository orderLeaderAuditRepository;

    /**
     * 功能描述：转领导批示
     *
     * @param dto
     * @return Result
     * @author chenminfeng
     * @date 2022/6/15 13:37
     * @version:
     */
    @PostMapping("/addLeaderAudit")
    public Result addLeaderAudit(@RequestBody OrderLeaderAuditDTO dto) {
        return orderLeaderAuditRepository.addLeaderAudit(dto);
    }
}
