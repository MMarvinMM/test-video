package com.chinatower.device.entity.vo;

import com.chinatower.video.api.entity.CommonResultEntiry;
import lombok.Data;

@Data
public class TjhTokenRes {
    private String url;

    private String token;

    @Data
    public static class TjhTokenResEntity extends CommonResultEntiry {
        private TjhTokenRes data;
    }
}
