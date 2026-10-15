package com.chinatower.event.vo;

import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class CheckpointAlarmOriginalAreaVO implements Serializable {

    private static final long serialVersionUID = 7274002326948221261L;
    private String alarmAreaId;
    private String confidenceLevel;
    private String warningOrderId;
    /** 告警点列表 */
    private List<AlarmOriginalPointVO> alarmPointList;
}
