package com.chinatower.device.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;


/**
 * VmHornTaskConfDO.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
@TableName("vm_uav_device_model_t")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UavModelDO implements java.io.Serializable {
    /**表名常量*/
  	private static final long serialVersionUID = 1L;

	/*
	 * 设备型号ID.
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
  	//属性 begin
  	/*
  	 * 型号编码.
  	 */
  	private String modelCode;
  	
  	/*
  	 * 型号名称
  	 */
  	private String modelName;
  	
  	/*
  	 * 厂家编码.
  	 */
  	private String manufacturerCode;
  	
  	/*
  	 * 负载枚举值
  	 */
  	private String domain;

	private String type;

	private String subType;

	/*
	 * 飞行器类别
	 */
	private String aircraftCategory;

	/*
	 * 飞行器类型
	 */
	private String aircraftType;

	/*
	 * 起飞全重（kg)
	 */
	private String weight;

	/*
	 * 支持相机
	 */
	private String supportCamera;

	/*
	 * 最大飞行时间（min）
	 */
	private String maxFlyTime;

	/*
	 * 最大水平速度（m/s）
	 */
	private String maxLevelSpeed;

	/*
	 * 搭配机巢
	 */
	private String matchNest;

  	/*
  	 * 创建人.
  	 */
  	private String creator;
	/*
	 * 创建时间.
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date createTime;
  	/*
  	 * 修改人.
  	 */
  	private String modifier;
	/*
	 * 修改时间.
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date updateTime;

	/*
	 * 删除态，0：未删除，1：已删除
	 */
	private Integer deleteFlag;

	/*
	 * 厂家名称.
	 */
	private String manufacturerName;


	/*
	 * 是否已经绑定，0：未绑定，1：已绑定
	 */
	private Integer isBind;

	/*
	 * 飞行器类别
	 */
	private String aircraftCategoryName;

	/*
	 * 飞行器类型
	 */
	private String aircraftTypeName;

}
