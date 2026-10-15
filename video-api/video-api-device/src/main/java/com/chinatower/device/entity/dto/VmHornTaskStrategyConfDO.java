package com.chinatower.device.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * VmHornTaskStrategyConfDO.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
@TableName("vm_horn_task_strategy_conf")
public class VmHornTaskStrategyConfDO implements java.io.Serializable {
    /**表名常量*/
    public static final String TNAME = "vm_horn_task_strategy_conf";
  	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
  	//属性 begin
  	/*
  	 * 任务id.
  	 */
  	private Long taskId;
  	
  	/*
  	 * 启动策略 0-单次 1-循环.
  	 */
  	private Integer startupPolicy;
  	
  	/*
  	 * 有效日期.
  	 */
  	private String validDate;
  	
  	/*
  	 * 失效日期.
  	 */
  	private String invalidDate;
  	
  	/*
  	 * 循环日期 如 1,2,3,4,5,6,7.
  	 */
  	private String cycleDate;
  	
  	/*
  	 * 播放策略 0-播放次数 1-持续时长 2-播放时间段.
  	 */
  	private Integer playPolicy;
  	
  	/*
  	 * 开始时间.
  	 */
  	private String startTime;
  	
  	/*
  	 * 结束时间.
  	 */
  	private String endTime;
  	
  	/*
  	 * 播放次数.
  	 */
  	private Integer playTimes;
  	
  	/*
  	 * 播放时长.
  	 */
  	private Integer playDuration;
}
