package com.chinatower.device.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * VmHornTaskConfDO.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
@TableName("vm_horn_task_conf")
public class VmHornTaskConfDO implements java.io.Serializable {
    /**表名常量*/
    public static final String TNAME = "vm_horn_task_conf";
  	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
  	//属性 begin
  	/*
  	 * 任务名称.
  	 */
  	private String taskName;
  	
  	/*
  	 * 任务类型 0-音频 1-文本.
  	 */
  	private Integer taskType;
  	
  	/*
  	 * 任务音量.
  	 */
  	private String taskVolumn;
  	
  	/*
  	 * 任务优先级.
  	 */
  	private Integer taskPriority;
  	
  	/*
  	 * 操作类型 0-未操作 1-开始 2-结束.
  	 */
  	private Integer operatorType;
  	
  	/*
  	 * 启动状态 0-未启动 1-启动中.
  	 */
  	private Integer startupStatus;
  	
  	/*
  	 * 执行状态 0-未执行 1-执行中.
  	 */
  	private Integer executStatus;
  	
  	/*
  	 * 客户编码.
  	 */
  	private String tenantId;
  	
  	/*
  	 * 行业编码.
  	 */
  	private String industryCode;

	/*
	 * 应用版本.
	 */
	private String appVerCode;
  	
  	/*
  	 * 创建人.
  	 */
  	private String creator;
  	
  	/*
  	 * 修改人.
  	 */
  	private String modifier;
  	
  	/*
  	 * 操作人.
  	 */
  	private String operator;
  	
  	/*
  	 * 删除标识 0-未删除 1-已删除.
  	 */
  	private Integer deleteFlag;
  	
  	/*
  	 * 创建时间.
  	 */
  	private Date createTime;
  	
  	/*
  	 * 修改时间.
  	 */
  	private Date updateTime;
  	
  	/*
  	 * 操作时间.
  	 */
  	private Date operateTime;
  	
  	/*
  	 * 对应广播平台文本id.
  	 */
  	private String hornTaskId;

}
