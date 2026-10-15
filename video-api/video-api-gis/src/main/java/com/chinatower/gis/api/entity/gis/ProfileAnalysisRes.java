package com.chinatower.gis.api.entity.gis;

import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2024/10/16 17:21
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ProfileAnalysisRes {

    /**
     * 坡面分析
     */
    private Double [][] samplePoint;
    /**
     * 关键点
     */
    private Double [][] keyPoint;
}
