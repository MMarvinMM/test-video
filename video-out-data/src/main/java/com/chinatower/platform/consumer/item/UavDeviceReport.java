package com.chinatower.platform.consumer.item;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 无人机设备实体类
 */
@Data
public class UavDeviceReport extends BaseDataScopeEntity implements Serializable {

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "摄像机编码不能为空!")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    @NotBlank(message = "摄像机名称不能为空!")
    private String deviceName;

    /**
     * 资源状态(0在线，1离线,2 从未连接)
     */
    @ApiModelProperty(value = "资源状态(0在线，1离线,2 从未连接)")
    private Integer resourceStatus;

    /**
     * 省份编码
     */
    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    /**
     * 地市编码
     */
    @ApiModelProperty(value = "地市编码")
    private String cityCode;

    /**
     * 区县编码
     */
    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    /**
     * 乡镇编码
     */
    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    /**
     * 设备地址
     */
    @ApiModelProperty(value = "设备地址")
    private String address;

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
     * 所属管理区域
     */
    @ApiModelProperty(value = "所属管理区域")
    private String dept;

    /**
     * 行政区域
     */
    @ApiModelProperty(value = "行政区域")
    private String adminArea;

    /**
     * 设备厂家编码
     */
    @ApiModelProperty(value = "设备厂家编码")
    private String manufacturer;

    /**
     * 设备型号编码
     */
    @ApiModelProperty(value = "设备型号编码")
    private String deviceModel;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    @ApiModelProperty(value = "应用修改标识（1:已修改 0未修改）")
    private String appModFlag;

    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    @ApiModelProperty(value = "删除标识 0-未删除 1-已删除")
    private String delFlag;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String modifier;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * 站址编码
     */
    @ApiModelProperty(value = "站址编码")
    private String siteId;

    /**
     * 设备挂高
     */
    @ApiModelProperty(value = "设备挂高")
    private Double hangingHeight;

    /**
     * 属地化同步新增-接入节点
     */
    @ApiModelProperty(value = "属地化同步新增-接入节点")
    private String locationCode;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
