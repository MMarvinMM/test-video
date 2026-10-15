package com.chinatower.event.entity.vo;

import lombok.Data;

@Data
public class CombinationVo {

    //id
    private Integer id;

    //二次识别配置编码
    private Integer alarmSecondRecogId;

    //组合结果  告警来源值-告警类型编码   8-1979
    private String combinedResult;

}
