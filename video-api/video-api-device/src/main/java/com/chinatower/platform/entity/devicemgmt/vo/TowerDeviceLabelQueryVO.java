package com.chinatower.platform.entity.devicemgmt.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TowerDeviceLabelQueryVO implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeEnd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeEnd;

    private Integer pageNum;

    private Integer pageSize;

    private Long labelTypeId;

    private String labelName;

    private String creator;

    private String modifier;

    private String sortName;

    private String sort;

    private String screenCodeFlag;

    private String labelIndustryCode;

    /**
     *  订购标识 1：已订购  0 未订购
     */
    private String ifOrderFlag;
}
