package com.chinatower.event.repository;


import com.chinatower.event.entity.dto.AddShareDTO;
import com.chinatower.event.entity.dto.AlarmShareDTO;

/**
 * 告警分享业务接口
 * @author yhb
 * @version 1.0
 * @date 2022/12/29 11:22
 */
public interface IAlarmShareService {

    /**
     * 告警分享新增
     *
     * @return
     * @author yhb
     * @date 2022/12/29
     * @version
     */
    AlarmShareDTO addAlarmShare(AddShareDTO addShareDTO);

}
