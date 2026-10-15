package com.chinatower.platform.model.vo;


import lombok.Data;

import java.math.BigDecimal;


/**
 * DeviceModel.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
public class DeviceModelVO implements java.io.Serializable {

  	private static final long serialVersionUID = 1L;
  
  	
  	//属性 begin
	/**
	 * 设备编码
	 */
	private String deviceCode;
  	
  	/**
  	 * 摄像机厂家编码.
  	 */
  	private String mfgrCode;
  	
  	/**
  	 * 摄像机型号编码.
  	 */
  	private String deviceModelCode;
  	
  	/**
  	 * 摄像机类型.
  	 */
  	private String deviceType;
  	
  	/**
  	 * 图像传感器尺寸，英寸.
  	 */
  	private String picSensorSize;
  	
  	/**
  	 * 图像传感器长度，毫米.
  	 */
  	private String picSensorLength;
  	
  	/**
  	 * 图像传感器宽度，毫米.
  	 */
  	private String picSensorWidth;
  	
  	/**
  	 * 最小焦距.
  	 */
  	private String minFocus;
  	
  	/**
  	 * 焦距范围.
  	 */
  	private String focusRange;
  	
  	/**
  	 * 水平视场角.
  	 */
  	private String horizViewRange;
  	
  	/**
  	 * 垂直视场角.
  	 */
  	private String vertiViewRange;
  	
  	/**
  	 * 水平角范围.
  	 */
  	private String horizRange;
  	
  	/**
  	 * 俯仰角范围.
  	 */
  	private String vertiRange;

	/**
	 * 水平向下为负 0否1是.
	 */
	private String vertiDownNeg;

	/**
	 * 实时P值，用来计算获取哪个p0,t0
	 */
	private String realPan;

	/**
	 * 水平参数p0.
	 */
	private BigDecimal p0;

	/**
	 * 垂直参数t0.
	 */
	private BigDecimal t0;

	/**
	 * 设备挂高，单位：米
	 */
	private String height;

	/**
	 * 设备型号ID.
	 */
	private Long modelId;

	/**
	 * 经度
	 */
	private String longitude;

	/**
	 * 纬度
	 */
	private String latitude;

	/**
	 * 观察距离.
	 */
	private int distance;
  	//属性 end
}
