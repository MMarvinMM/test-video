package com.chinatower.event.dto;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import lombok.Data;

import java.util.List;

@Data
public class PushThirdDto {

    private String originalJson;
    private List<ReceiveAlarmVO> alarmList;
}
