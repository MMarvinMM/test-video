package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

@Data
public class TowerDeviceLabelRelVO implements Serializable {
	
    private List<Long> labelIdList;
    
    @NotBlank(message = "摄像机编码不能为空")
    private String deviceCode;
}
