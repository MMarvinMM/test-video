package com.chinatower.event.entity.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AlarmVideoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileCode;

    private String fileName;

    private String startTime;

    private String endTime;

    private Integer recordType;

    private String fileImageUrl;

    private String fileVideoUrl;

    private String warningOrderId;

    private String tableName;
}
