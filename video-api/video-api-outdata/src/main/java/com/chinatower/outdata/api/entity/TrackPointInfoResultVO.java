package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrackPointInfoResultVO implements Serializable {

    private String code;

    private String msg;

    private TrackAllInfo data;
}
