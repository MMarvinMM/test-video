package com.chinatower.device.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class HornTaskPlayParamDTO implements Serializable {

    private Long taskId;

    private String fileId;

    private String paramJson;

    private Date createTime;

    private Date updateTime;

    private String fileUrl;

    private String fileMd5;

    private String accessNode;

    private String fileDuration;
}
