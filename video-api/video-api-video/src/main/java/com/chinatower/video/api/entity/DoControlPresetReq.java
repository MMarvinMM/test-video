package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DoControlPresetReq extends VideoBaseControlReq{
    private int presetId;

    @Data
    public static class DoControlPresetReqA extends CommonResultEntiry {
        private DoControlPresetReq data;
    }
}
