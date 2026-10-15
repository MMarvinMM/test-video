package com.chinatower.platform.service;


import com.chinatower.event.entity.dto.HandleAlarmImgDTO;


/**
 * 功能说明: event服务导出接口类
 *
 * @author lyy
 * @date 2021-12-09 15:39
 * @requestID
 */
public interface EventHandleImgService {

    /**
     * 功能说明: event AI生成告警后原图/框图复合油管线图处理功能类
     *
     * @Author cui.sj
     * @Date 2023/10/16
     * @Version release_v1.6.20
     */
    void handleReceiveAlarmImg(HandleAlarmImgDTO handleAlarmImgDTO);
}
