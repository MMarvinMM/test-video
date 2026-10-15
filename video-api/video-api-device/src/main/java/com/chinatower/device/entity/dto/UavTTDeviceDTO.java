package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 无人机设备实体类
 */
@Data
public class UavTTDeviceDTO extends UavDeviceDTO {

    @ApiModelProperty(value = "省公司编码")
    private String provinceDeptCode;

    @ApiModelProperty(value = "市公司编码")
    private String cityDeptCode;

    private List<Long> deptProvinceCity;

    private Long chooseOrgId;

    @ApiModelProperty(value = "机场SN编码")
    private String airportCode;

    @ApiModelProperty(value = "挂高(米)")
    private Double hangingHeight;

    @ApiModelProperty(value = "挂高类型")
    private String hangingType;

    @ApiModelProperty(value = "设备序列号")
    private String deviceSerialId;

    @ApiModelProperty(value = "安装日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date installDate;

    @ApiModelProperty(value = "适用性(共享-1、私有-0)")
    private Integer adaptability;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "接入协议")
    private String accessProtocols;

    @ApiModelProperty(value = "接入节点（中心、下沉点）")
    private String accessNodeCode;

    @ApiModelProperty(value = "接入节点名称（中心、下沉点）")
    private String accessName;

    @ApiModelProperty(value = "接入类型")
    private Long collectType;

    @ApiModelProperty(value = "接入方式")
    private Long collectMethod;

    private String siteCode;

    private String siteName;

    /*网域类型*/
    private Long netType;

    /*是否级联*/
    private String isCascade;

    @ApiModelProperty(value = "属地化同步新增-接入节点")
    private String locationCode;

    @ApiModelProperty(value = "操作类型 1：同步")
    private Integer optType;

    @ApiModelProperty(value = "组织ID")
    private String deptId;

    /**
     * 属地化同步类型
     */
    private String opType;

    private String sort;

    private String sortName;
}
