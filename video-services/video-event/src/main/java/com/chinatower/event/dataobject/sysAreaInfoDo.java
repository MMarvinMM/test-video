package com.chinatower.event.dataobject;

import lombok.Data;

import java.io.Serializable;

@Data
public class sysAreaInfoDo implements Serializable {


    private String areaName;

    private String areaParentCode;

}
