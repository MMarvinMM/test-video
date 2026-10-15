package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionFjldParam implements Serializable {
    private String fileCode;
    private String tagPictureUrl;
    private String fileName;
}
