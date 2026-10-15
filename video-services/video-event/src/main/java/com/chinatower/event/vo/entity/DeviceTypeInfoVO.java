package com.chinatower.event.vo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceTypeInfoVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String deviceTypeName;

        private int  deviceTypeNum;

        private String categoryCode;
}
