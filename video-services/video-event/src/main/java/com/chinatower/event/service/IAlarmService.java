package com.chinatower.event.service;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.ReceiveAlarmVOReq;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;

import java.util.List;

/**
 * @Description 告警相关服务接口
 * @Author guyuefei
 * @Date 2022/7/11
 * @Version release_v1.5
 **/
public interface IAlarmService {

    /**
     * 告警接收
     * @param receiveAlarmVOReq
     * @return
     */
    Result receiveAlarm(ReceiveAlarmVOReq receiveAlarmVOReq);

    /**
     * 运营研判同步
     * @param req
     * @return
     */
    Result syncAlarm(ReceiveAlarmVO req);

    /**
     * 卫片告警上报
     * @param list
     * @return
     */
    Result receiveRegionAlarm(List<ReceiveRegionAlarmDto> list);

}
