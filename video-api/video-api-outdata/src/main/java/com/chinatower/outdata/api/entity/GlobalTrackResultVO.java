package com.chinatower.outdata.api.entity;

import lombok.Data;

@Data
public class GlobalTrackResultVO {

    private String code;

    private String msg;

    private GlobalTrackTrackMap data;
}
