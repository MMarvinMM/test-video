package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdAlarmVo {

    private String warningSourceName;

    private AlarmTVo alarmTVo;

}
