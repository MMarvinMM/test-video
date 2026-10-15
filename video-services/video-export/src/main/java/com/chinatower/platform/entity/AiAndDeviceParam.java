package com.chinatower.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/5 14:46
 */

@Data
public class AiAndDeviceParam implements Serializable {

    private String alarmAreaId;

    private transient List<CarDto> attrList;


    @Data
    public class CarDto{
        private String attrType;

        private String attrValue;
    }
}
