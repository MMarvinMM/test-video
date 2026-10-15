package com.chinatower.device.entity.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;


/**
 * 无人机设备安全策略
 */
@Data
public class VmUavSecureStrategyDTO extends BaseDataScopeEntity {

  	//属性 begin
	/*
  	 * 自增id.
  	 */
  	private Long id;

	/**
	 * 是否开启安全策略 0 开启  1 关闭
	 */
	private Integer open;
	
	/*
  	 * 设备编码.
  	 */
  	private String deviceCode;
	
	/*
  	 * 策略类型：windspeed-风速, rainfall-雨量, temperature-温度.
  	 */
  	private String strategyType;
	
	/*
  	 * 最小值.
  	 */
  	private String minValue;
	
	/*
  	 * 最大值.
  	 */
  	private String maxValue;
	
	/*
  	 * 最小值符号 < <=.
  	 */
  	private String minSymbol;
	
	/*
  	 * 最大值符号 < <=.
  	 */
  	private String maxSymbol;
	
	private List<String> deviceCodes;
  	
}
