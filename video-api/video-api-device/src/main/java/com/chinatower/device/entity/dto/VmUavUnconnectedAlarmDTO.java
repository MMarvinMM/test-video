package com.chinatower.device.entity.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;


/**
 * 无人机失联告警推送
 */
@Data
public class VmUavUnconnectedAlarmDTO extends BaseDataScopeEntity {

  	//属性 begin
	/*
  	 * 自增id.
  	 */
  	private Long id;

	/**
	 * 是否开启失联告警 1 开启  0 关闭
	 */
	private Integer unconnectedOpen;

	/**
	 * 是否开启延时告警 1 开启  0 关闭
	 */
	private Integer delayOpen;
	
	/*
  	 * 设备编码.
  	 */
  	private String deviceCode;
	
	/*
  	 * 失联告警短信接收客户userId.
  	 */
  	private String alarmUserId;
	
	/*
  	 * 告警延迟时间，时.
  	 */
  	private String alarmDelayHour;
	
	/*
  	 * 告警延迟时间，分.
  	 */
  	private String alarmDelayMinute;
	
	/*
	 * 设备编码.
	 */
	private List<String> deviceCodes;

}
