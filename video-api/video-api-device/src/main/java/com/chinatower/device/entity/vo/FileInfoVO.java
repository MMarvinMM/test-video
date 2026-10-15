package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class FileInfoVO implements Serializable {

    private Integer sequence;

    private String path;

    private Integer businessProp;

}
