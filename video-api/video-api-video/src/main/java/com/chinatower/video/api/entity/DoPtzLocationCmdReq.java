package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DoPtzLocationCmdReq extends VideoBaseControlReq{
    /**
     * ⽔平参数，范围[0, 3600]
     * @required true
     */
    private float pan;

    /**
     * 垂直参数，范围[-1800, 1800]
     * @required true
     */
    private float tilt;

    /**
     * 变倍参数，范围[0, 254]
     * @required true
     */
    private float zoom;
}
