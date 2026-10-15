package com.chinatower.device.entity.dto;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * VmHornTaskDeviceRelDO.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
@TableName("vm_horn_task_device_rel")
public class VmHornTaskDeviceRelDO implements java.io.Serializable {
    /**表名常量*/
    public static final String TNAME = "vm_horn_task_device_rel";
  	private static final long serialVersionUID = 1L;
  
  	
  	//属性 begin
  	/*
  	 * 任务id.
  	 */
  	private Long taskId;
  	
  	/*
  	 * 设备编码.
  	 */
  	private String deviceCode;
}
