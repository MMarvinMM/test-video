package com.chinatower.platform.model.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * DeviceRefPoint.
 * @author generator
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceRefPointVO implements java.io.Serializable {

  	private static final long serialVersionUID = 1L;
  
  	
  	//属性 begin
  	/**
  	 * 参考点ID.
  	 */
  	private Long pointId;
  	
  	/**
  	 * 参考点名称.
  	 */
  	private String pointName;
  	
  	/**
  	 * 设备编码.
  	 */
	@NotBlank
  	private String deviceCode;

	/**
	 * 通道编码.
	 */
	@NotBlank
	private String channelCode;

	/**
	 * 设备厂家code.
	 */
	@NotBlank
	private String modelId;
  	
  	/**
  	 * 经度.
  	 */
  	private String longitude;
  	
  	/**
  	 * 纬度.
  	 */
  	private String latitude;
  	
  	/**
  	 * 视频坐标.
  	 */
  	private String videoCoord;
  	
  	/**
  	 * 水平参数.
  	 */
  	private Float pan;
  	
  	/**
  	 * 垂直参数.
  	 */
  	private Float tilt;
  	
  	/**
  	 * 变倍参数.
  	 */
  	private Float zoom;
  	
  	/**
  	 * 真实水平角.
  	 */
	@NotNull
	private BigDecimal realHorAngle;
  	
  	/**
  	 * 真实俯仰角.
  	 */
	@NotNull
  	private BigDecimal realPitchAngle;
  	
  	/**
  	 * 水平角偏差.
  	 */
  	private BigDecimal horOffset;
  	
  	/**
  	 * 俯仰角偏差.
  	 */
  	private BigDecimal pitchOffset;
  	
  	/**
  	 * 是否为参考点 0否1是.
  	 */
  	private String pointFlag;
  	
  	/**
  	 * 创建时间.
  	 */
  	private java.util.Date createTime;
  	
  	/**
  	 * 更新时间.
  	 */
  	private java.util.Date updateTime;

  	//属性 end

}
