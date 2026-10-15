package com.chinatower.emergency.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HrtcRecordVo implements Serializable {

    private Integer id;

    private String hrtcId;

    private Integer type;

    private String captaincy;

    private String muteId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    private String player;

    private String invited;

    private String callId;

    private String fileId;
}
