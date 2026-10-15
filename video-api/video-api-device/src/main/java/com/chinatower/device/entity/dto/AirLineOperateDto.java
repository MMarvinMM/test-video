package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/1/15 11:12
 */
@Data
public class AirLineOperateDto {

    /**
     * 算法任务id集合
     */
    private List<String> taskIdList;

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 删除人
     */
    private String updateUser;

    private String algorithmType;
}
