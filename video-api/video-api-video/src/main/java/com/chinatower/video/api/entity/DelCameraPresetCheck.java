package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelCameraPresetCheck {

	//查询该预置位是否绑定图斑与电子围栏  >0 绑定图斑与电子围栏  ==0 没绑定
	private int polyCount;
	
	//判断该预置位上是否配置了巡航计划  >0 配置了巡航计划  ==0 没配置
	private int patrolCount;
	
	// 查询是否配置了看守位  >0 配置了看守位 ==0 没配置
	private int guardCount;
	
	//判断该预置位上是否配置了算法  >0 配置了算法  ==0 没配置
	private int algorithmConfigCount;
	
	//判断该预置位上是否设置ar标签  >0 设置ar标签  ==0 未设置
	private int arCount;

}
