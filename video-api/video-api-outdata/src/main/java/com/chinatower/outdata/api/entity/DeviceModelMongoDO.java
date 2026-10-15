package com.chinatower.outdata.api.entity;


import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;


@Data
@Document(collection="vm_device_model_t")
public class DeviceModelMongoDO extends BaseMongoDTO implements Serializable {

  	private static final long serialVersionUID = 1L;

	/**
	 * 设备型号ID.
	 */
	private Integer modelId;
  	
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
	 * 备注
	 */
	private String remark;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 修改人
	 */
	private String modifier;
}
