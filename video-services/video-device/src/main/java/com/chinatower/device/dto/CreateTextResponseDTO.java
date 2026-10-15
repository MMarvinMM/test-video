package com.chinatower.device.dto;

import lombok.Data;

/**
 * @author willem
 * @date 2024/10/22
 * @description
 */
@Data
public class CreateTextResponseDTO {
    private String auditStatus;
    private String auditTime;
    private String auditUser;
    private String createTime;
    private String createUser;
    private String fileStatus;
    private String md5;
    private String regionCode;
    private Integer speechRate;
    private Integer tonePitch;
    private String txtContent;
    private String txtId;
    private String txtName;
    private Integer volume;
    private String xunfeiEnt;
    private String xunfeiVcn;
}
