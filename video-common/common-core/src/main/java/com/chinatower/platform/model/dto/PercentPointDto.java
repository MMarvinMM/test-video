package com.chinatower.platform.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
public class PercentPointDto implements Serializable {

    private String monitorPointId;

    private Float percentX;

    private Float percentY;
}
