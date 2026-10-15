package com.chinatower.video.modules.query.entity;

import lombok.Data;

import java.util.List;

@Data
public class ArTagMoveVO {
    /**
     * 标签类别 101视频画面手动标签、102地图经纬度标签、103摄像机标签、104图层标签、105物联标签、106无人机标签、107雷达标签、108喇叭标签
     */
    private String tagClass;

    /**
     * 标签编码
     */
    private List<String> tagIdList;
}
