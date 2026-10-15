package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionNumParam implements Serializable {

    /**
     * 创建时间开始
     */
    private String createTimeBegin;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;
}
