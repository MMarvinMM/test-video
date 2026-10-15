package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 公交
 * @data 创建时间：2023/8/9 13:40
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegmentsBus {

    /**
     * 公交方案信息列表
     * @required false
     */
    @ApiModelProperty(value = "公交方案信息列表")
    private List<BusNavigationSegmentsBusBuslines> buslines;
}
