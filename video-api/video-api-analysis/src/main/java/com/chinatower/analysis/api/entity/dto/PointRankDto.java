package com.chinatower.analysis.api.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointRankDto implements Serializable {

    private String deviceCode;

    private String deviceName;

    private Integer pointCount;

    private Integer availPointCount;

    private Integer rank;
}
