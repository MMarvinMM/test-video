package com.chinatower.device.entity.dto;


import lombok.Data;

import java.util.Date;


/**
 * VmHornTaskContentRelDO.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
public class VmHornTaskDiffNodeRelDO implements java.io.Serializable {

	//属性 begin
	/*
	 * 任务id.
	 */
	private String taskId;

	/*
	 * 执行状态 0-未执行(广播平台 1等待播发、4播发失败、5播发取消) 1-执行中(广播平台 2播发中 3播发成功).
	 */
	private Integer executStatus;

	/*
	 * 广播平台任务id
	 */
	private String hornTaskId;

	/*
	 * 接入节点
	 */
	private String accessNode;

	/*
	 * 失败原因
	 */
	private String pushFailReason;

	/*
	 * 创建时间.
	 */
	private Date createTime;

	/*
	 * 修改时间.
	 */
	private Date updateTime;
}
  	

