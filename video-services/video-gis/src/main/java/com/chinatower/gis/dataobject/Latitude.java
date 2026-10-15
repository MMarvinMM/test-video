package com.chinatower.gis.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Latitude
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/18
 **/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("all")
public class Latitude implements Serializable {
    private String code;
    private String areaName;
    private String level;
    private String latitude;
    private String longitude;
    private String alarmNum;
    private String deviceNum;
    private String gridkeeperNum;
    private String tenantId;

    private String creat;

    private String parentCode;
    private List areaCode;
    private List provinceList;
}
