package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 喊话相关接口入参实体类
 */
@Data
public class HornRangeInfoDTO implements Serializable {

    private String devType;

    private Integer order;

    private List<HornRangeDevInfoDTO> rangeDevInfoList;
}
