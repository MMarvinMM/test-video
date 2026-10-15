package com.chinatower.event.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 10:49
 */

@Data
public class AlarmAreaResp {


    /**
     * area_id
     */
    private Integer areaId;

    /**
     * 点位json
     */
    private String points;


    /**
     * 根据点位及摄像机ptz所计算出的经纬度
     */
    private String positionInformation;
}
