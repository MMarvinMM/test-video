package com.chinatower.event.repository;

import java.util.List;
import java.util.Map;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.MessagePushDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.ReceiveSatelliteAlarmVo;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: IReceiveRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/14 16:14
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface IReceiveRepository {




    /**
     * 将最新的告警时间放入Redis中
     * @param list
     */
    void setAlarmTimeToRedis(List<ReceiveAlarmVO> list);

    /**
     * 消息推送
     * @param dto
     */
    void messagePush(MessagePushDTO dto);
    /**
     * 批量消息推送
     * @param alarms
     */
    public void batchPushMessage(List<ReceiveAlarmVO> alarms);
    /**
     * 接收卫星火情告警
     * @param receiveSatelliteAlarmVos
     * @return
     */
    Result receiveSatelliteAlarm(List<ReceiveSatelliteAlarmVo> receiveSatelliteAlarmVos);

    /**
     * 功能说明： 鉴权第三方系统接口
     * @param data
     * @return
     */
    Result oauthToken(Map data);
}
