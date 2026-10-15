package com.chinatower.device.entity.dto;


import lombok.Data;

import java.util.List;


/**
 * 无人机设备安全策略信息.
 */
@Data
public class UavSecureStrategyInfoVO implements java.io.Serializable {

	/**
	 * 是否开启安全策略 1 开启  0 关闭
	 */
	private Integer open;
	/*
  	 * 设备编码.
  	 */
  	private List<String> deviceCodes;

	/*
	 * 安全策略信息.
	 */
	private List<VmUavSecureStrategyDTO> secureStrategyInfos;

	/*
	 * 失联告警推送信息
	 */
	private UavUnconnectedAlarmInfoVO unconnectedAlarmInfo;

	/*
	 * 无人机飞行控制密码配置信息
	 */
	private VmUavFlightControlPasswordConfDTO uavFlightControlPasswordConfInfo;
}
