package com.chinatower.platform.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
public class PTZPointDto implements Serializable {

    private String monitorPointId;

    private Float pan;

    private Float tilt;

    private Float zoom;
}
