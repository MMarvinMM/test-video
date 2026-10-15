package com.chinatower.video.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class QueryVideoData {
    private int totalPage;
    private int totalNum;
    private List<QueryVideoListData> videos;

    @Data
    public static class QueryVideoDataEntity extends CommonResultEntiry {
        private QueryVideoData data;
    }
}
