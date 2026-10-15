package com.chinatower.platform.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class RadarAlarmBlackWhiteExportVO implements Serializable {

    /**
     * 船名
     */
    @Excel(name = "船名")
    private String shipName;

    /**
     * mmsi
     */
    @Excel(name = "MMSI")
    private String mmsi;
}
