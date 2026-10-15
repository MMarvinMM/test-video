package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class HornTxtFileVO implements Serializable {

    private String txtId;

    private String fileStatus;

    private String fileUrl;

    private String md5;

    private String duration;
}
