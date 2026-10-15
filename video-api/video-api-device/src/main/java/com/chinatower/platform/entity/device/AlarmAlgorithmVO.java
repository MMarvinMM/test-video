package com.chinatower.platform.entity.device;

import lombok.Data;

import java.util.List;

@Data
public class AlarmAlgorithmVO {

    //告警类型
    private List<String> alarmType;

    //算法编码
    private List<String> algorithmCode;
}
