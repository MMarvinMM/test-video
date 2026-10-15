package com.chinatower.video.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.chinatower.common.core.dto.BasePageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PresetImgJobDTO extends BasePageDTO implements Serializable {

	/**
     * 预置点id
     */
    private Long presetId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    private String deviceCode;

    private String channelCode;

    private String tenantId;

    private String industryCode;

    private List<Integer> presetIdList;

    private List<String> deviceCodeList;

    private List<String> channelCodeList;

    /**
     * 查询来源 0：单屏模式 1：视频抓图-摄像机 2：视频抓图-预置位
     */
    private String queryType;

    /**
     * 告警类型编码
     */
    private List<String> typeValueList;

    /**
     * 0正序  1倒序
     */
    private String sort;
}
