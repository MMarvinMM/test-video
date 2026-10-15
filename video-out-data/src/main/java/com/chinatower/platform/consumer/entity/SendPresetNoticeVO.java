package com.chinatower.platform.consumer.entity;

import com.chinatower.outdata.api.entity.PresetArrivalNotice;
import lombok.Data;

@Data
public class SendPresetNoticeVO {
    private PresetArrivalNotice infoVO;
    private long count;
}
