package com.chinatower.platform.entity.devicemgmt.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

@Data
public class TowerDeviceLabelTypeVO extends BaseIndustryEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5189198592354670389L;
	
	private Long id;
	
	@NotBlank(message = "标签类型名称不能为空")
    @Size(min = 0, max = 50, message = "最多支持输入50个字。")
    private String labelTypeName;
}
