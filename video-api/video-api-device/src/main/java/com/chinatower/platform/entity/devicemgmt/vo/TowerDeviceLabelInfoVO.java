package com.chinatower.platform.entity.devicemgmt.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

@Data
public class TowerDeviceLabelInfoVO extends BaseIndustryEntity {
	
	private static final long serialVersionUID = -7169115395916369109L;
	
	private Long labelId;
    
	@NotNull(message = "标签类型不能为空")
	private Long labelTypeId;
    
	@NotBlank(message = "标签名称不能为空")
    @Size(min = 0, max = 50, message = "最多支持输入50个字。")
	private String labelName;

	//大屏统计显示1：是0：否
	private String screenStatisticsDisplay;

	//标签图标
	private String labelIcon;

	//标签打点图标
	private String labelPunchingIcon;

	//标签所属行业
	private String labelIndustryCode;
}
