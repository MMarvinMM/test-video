package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.List;

import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReceiveAlarmVOReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private ReceiveAlarmVO data;
    //物联设备批量上报节点信息
    private List<ReceiveIotAlarmVO> receiveIotAlarmVOList;
}
