package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckCurrentResp  implements Serializable {

    /**
     * 是否当前环节 1:是 0:否
     * @required true
     */
    private String ifCurrentLink;
}
