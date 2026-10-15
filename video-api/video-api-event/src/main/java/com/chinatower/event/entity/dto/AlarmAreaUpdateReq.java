package com.chinatower.event.entity.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 9:35
 */

@Data
public class AlarmAreaUpdateReq {

    /**
     * 修改的位置信息
     */
    private String positionInformation;

    /**
     * 要修改的区域id
     */
    private Integer areaId;

}
