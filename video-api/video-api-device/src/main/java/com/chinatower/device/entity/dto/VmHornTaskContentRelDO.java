package com.chinatower.device.entity.dto;


import lombok.Data;


/**
 * VmHornTaskContentRelDO.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
public class VmHornTaskContentRelDO implements java.io.Serializable {

  	//属性 begin
  	/*
  	 * 任务id.
  	 */
  	private Long taskId;
  	
  	/*
  	 * 内容id.
  	 */
  	private Long contentId;
  	
  	/*
  	 * 语速.
  	 */
  	private Integer speechRate;
  	
  	/*
  	 * 音量增益.
  	 */
  	private Integer volume;
  	
  	/*
  	 * 发音人 中英-女:x2_yezi 中英-男:x2_qige.
  	 */
  	private String xunfeiVcn;
  	
  	/*
  	 * 对应广播平台文本id.
  	 */
  	private String txtId;

	/*
	 * 播放顺序.
	 */
	private Integer playOrder;

	/*
	 * 内容标题.
	 */
	private String contentTitle;

	/*
	 * 内容信息
	 */
	private String contentValue;

	/*
	 * 文件时长.
	 */
	private String fileDuration;

	/*
	 * 内容信息
	 */
	private String resourceId;
}
