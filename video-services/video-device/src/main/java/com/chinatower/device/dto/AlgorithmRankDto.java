package com.chinatower.device.dto;

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
public class AlgorithmRankDto implements Serializable {

    private String channelCode;

    private String channelName;

    private Integer algorithmCount;

    private Integer rank;
}
