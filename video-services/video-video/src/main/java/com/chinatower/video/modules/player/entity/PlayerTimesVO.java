package com.chinatower.video.modules.player.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlayerTimesVO {
    //录像开始时间，格式：HHmmss，范围[000000~235959]
    private String startTime;

    //录像结束时间，格式：HHmmss，范围[000000~235959]
    private String endTime;
}
