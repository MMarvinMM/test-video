package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * 无人机飞行控制密码配置
 * @author penghaibo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VmUavFlightControlPasswordConfDTO extends BaseDataScopeEntity {

	// 属性 begin
	/*
	 * 自增id.
	 */
	private Long id;

	/**
	 * 设备编码.
	 */
	private String deviceCode;

	/**
	 * 飞行控制密码状态 0-关闭，1-开启
	 */
	private Integer passwordStatus;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 错误次数限制
	 */
	private String errorTimesLimit;

	/**
	 * 锁定时间
	 */
	private Integer lockTime;

	/**
	 * 锁定时间单位 1-分钟 2-秒 3-小时
	 */
	private String lockTimeUnit;

	/**
	 * 设备码列表
	 */
	private List<String> deviceCodes;
}