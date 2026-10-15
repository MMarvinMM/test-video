package com.chinatower.receive.model;

import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 接收告警（AI/摄像机/物联/雷达/卫星/卡口/故障）
 */
@Data
@ToString
public class ReceiveAlarmVOReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private ReceiveAlarmVO data;
    //物联设备批量上报节点信息
    private List<ReceiveIotAlarmVO> receiveIotAlarmVOList;

    //无人机告警接收入参
    private ReceiveUavAlarmVO receiveUavAlarmVO;
}
