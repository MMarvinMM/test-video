package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class EcmwfInfoParam implements Serializable {
    private static final long serialVersionUID = 1L;



    private String data;

    EcmwfHeader header;

}
