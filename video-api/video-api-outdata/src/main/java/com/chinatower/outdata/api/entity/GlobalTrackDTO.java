package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class GlobalTrackDTO {

    // 轨迹时长
    private int duration;

    // 目标id的集合
    private List<String> targetIds;
}
