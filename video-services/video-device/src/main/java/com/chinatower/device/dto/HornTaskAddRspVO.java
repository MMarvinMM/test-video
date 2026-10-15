package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HornTaskAddRspVO implements Serializable {

    private Long taskId;

    private String pushFailReason;

    private String hornTaskId;

    private String accessNode;

}
