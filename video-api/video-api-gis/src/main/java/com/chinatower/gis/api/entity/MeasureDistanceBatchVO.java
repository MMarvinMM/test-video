package com.chinatower.gis.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class MeasureDistanceBatchVO {

    private List<MeasureDistanceBatchChildVO> vos;

    private Integer sampleNum = 500;

}
