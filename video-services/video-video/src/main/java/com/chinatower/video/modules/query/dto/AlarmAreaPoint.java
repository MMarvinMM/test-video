package com.chinatower.video.modules.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmAreaPoint implements Serializable {

    private String percentX;

    private String percentY;

    private String suppressPointId;

    private String lon;

    private String lat;
}
