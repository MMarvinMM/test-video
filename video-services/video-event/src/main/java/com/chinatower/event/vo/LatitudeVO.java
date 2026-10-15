package com.chinatower.event.vo;

import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: LatitudeVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/14 10:33
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class LatitudeVO{
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
