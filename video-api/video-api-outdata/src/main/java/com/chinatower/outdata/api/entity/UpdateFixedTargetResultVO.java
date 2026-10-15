package com.chinatower.outdata.api.entity;

import lombok.Data;

@Data
public class UpdateFixedTargetResultVO {

    private String code;

    private String msg;

    private UpdateFixedTargetIdList data;
}
