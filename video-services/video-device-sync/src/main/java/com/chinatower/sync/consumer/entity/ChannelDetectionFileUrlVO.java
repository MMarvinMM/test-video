package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelDetectionFileUrlVO implements Serializable {

    private String path;

    private String previewAddress;
}
