package com.chinatower.industry.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 功能描述: 摄像机查询实体类
 *
 * @auther: hued
 * @date: 2022年8月11日10:04:35
 * @version: release_v1.5
 * @requestID:
 */
@Data
public class DeviceIndReqDTO extends BaseDataScopeEntity {
    /**
     * 设备编码
     */
    @ApiModelProperty(value = "摄像机编码", required = true)
    @NotEmpty(message = "摄像机编码不能为空")
    private String deviceCode;
    /**
     * 站址编码
     */
    @ApiModelProperty(value = "站址编码")
    private String siteCode;
    /**
     * 站址名称
     */
    @ApiModelProperty(value = "站址名称")
    private String siteName;
    /**
     * 通道编码
     */
    @ApiModelProperty(value = "通道编码")
    private String channelCode;
    /**
     * 站址编码列表
     */
    @ApiModelProperty(value = "站址编码列表")
    private List<String> siteCodes;
    /**
     * 摄像机编码列表
     */
    @ApiModelProperty(value = "摄像机编码列表")
    private List<String> deviceCodes;
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitude;
    /**
     * 范围
     */
    @ApiModelProperty(value = "范围")
    private String distance;

    private Integer categoryCode;

    /**
     * 标签集合
     */
    private List<Long> labelIdList;
    /**
     * 设备厂家编码集合
     */
    private List<String> modelCodeList;
    /**
     * 设备类型集合
     */
    private List<Integer> categoryCodeList;
    /**
     * 算法编码集合
     */
    private List<String> algorithmCodeList;
    /**
     * 部门编码集合
     */
    private List<String> deptIdList;
    /**
     * 设备状态集合
     */
    private List<Integer> deviceStatusList;

    /**
     *  BySite:1 BySiteNew:2
     */
    @TableField(exist = false)
    private int queryType;

    @TableField(exist = false)
    private String monitorAngle;

    @TableField(exist = false)
    private String monitorDirection;

    @TableField(exist = false)
    private String deviceSubType;

}
