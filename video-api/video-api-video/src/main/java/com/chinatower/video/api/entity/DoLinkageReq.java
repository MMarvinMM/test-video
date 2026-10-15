package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DoLinkageReq extends VideoBaseControlReq {
    private String pointX;
    private String pointY;
    private int mode;
    private String pointXEx;
    private String pointXEy;
}
