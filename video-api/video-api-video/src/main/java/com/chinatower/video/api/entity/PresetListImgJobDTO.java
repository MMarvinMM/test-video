package com.chinatower.video.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.chinatower.common.core.dto.BasePageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PresetListImgJobDTO extends BasePageDTO implements Serializable {

	/**
	 * 摄像机编码
	 * @required true
	 */
	@NotBlank(message = "摄像机编码不能为空")
	private String deviceCode;

    /**
     * 通道编码
     * @required true
     */
	@NotBlank(message = "通道编码不能为空")
    private String channelCode;
	
	/**
     * 预置点id列表
     */
    private List<Integer> presetIdList;

    /**
     * 抓图开始时间
     * @required true
     */
    @NotNull(message = "抓图开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;
    
    /**
     * 抓图结束时间
     * @required true
     */
    @NotNull(message = "抓图结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;
}
