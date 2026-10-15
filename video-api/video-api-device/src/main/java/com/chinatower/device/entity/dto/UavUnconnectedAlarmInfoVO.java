package com.chinatower.device.entity.dto;


import lombok.Data;

import java.util.List;


/**
 * 无人机设备安全策略信息.
 */
@Data
public class UavUnconnectedAlarmInfoVO implements java.io.Serializable {

	/**
	 * 是否开启失联告警 1 开启  0 关闭
	 */
	private Integer unconnectedOpen;

	/**
	 * 是否开启延时告警 1 开启  0 关闭
	 */
	private Integer delayOpen;

	/*
	 * 延迟时间.
	 */
	private VmUavUnconnectedAlarmDTO timeInfo;

}
