package com.chinatower.platform.controller;


import com.chinatower.event.entity.dto.HandleAlarmImgDTO;
import com.chinatower.platform.service.EventHandleImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * video-export
 * 功能说明: event AI生成告警后原图/框图复合油管线图处理功能类
 *
 * @Author cui.sj
 * @Date 2023/10/16
 * @Version release_v1.6.20
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/order")
public class EventHandleImgController{

    @Resource
    private EventHandleImgService eventHandleImgService;

    /**
     *
     * 原图/框图复合油管线图处理
     * 功能描述 原图/框图复合油管线图处理
     *
     * @Author cui.sj
     * @Date 2023/10/16
     * @Version release_v1.6.20
     * @version:
     */
    @PostMapping("/handleReceiveAlarmImg")
    public void handleReceiveAlarmImg(@RequestBody HandleAlarmImgDTO handleAlarmImgDTO) {
        try {
            eventHandleImgService.handleReceiveAlarmImg(handleAlarmImgDTO);
        } catch (Exception e) {
            log.error("原图/框图复合油管线图处理异常：", e);
        }
    }
}
