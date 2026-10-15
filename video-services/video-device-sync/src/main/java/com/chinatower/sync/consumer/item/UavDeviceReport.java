package com.chinatower.sync.consumer.item;

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

    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "摄像机编码不能为空!")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    @NotBlank(message = "摄像机名称不能为空!")
    private String deviceName;

    @ApiModelProperty(value = "资源状态(0在线，1离线,2 从未连接)")
    private Integer resourceStatus;

    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    @ApiModelProperty(value = "地市编码")
    private String cityCode;

    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    @ApiModelProperty(value = "设备地址")
    private String address;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "所属管理区域")
    private String dept;

    @ApiModelProperty(value = "行政区域")
    private String adminArea;

    @ApiModelProperty(value = "设备厂家编码")
    private String manufacturer;

    @ApiModelProperty(value = "设备型号编码")
    private String deviceModel;

    @ApiModelProperty(value = "应用修改标识（1:已修改 0未修改）")
    private String appModFlag;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "删除标识 0-未删除 1-已删除")
    private String delFlag;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "站址编码")
    private String siteId;

    @ApiModelProperty(value = "设备挂高")
    private Double hangingHeight;

    @ApiModelProperty(value = "属地化同步新增-接入节点")
    private String locationCode;

    @ApiModelProperty(value = "绑定机巢设备编码")
    private String bindNestDeviceCode;

    @ApiModelProperty(value = "相机型号")
    private String cameraType;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;

    @ApiModelProperty(value = "无人机SN编码")
    private String uavCode;

    @ApiModelProperty(value = "起飞权重（kg）")
    private String takeoffWeight;

    @ApiModelProperty(value = "遥感器序列号")
    private String rcNumber;

    @ApiModelProperty(value = "民航局实名登记码")
    private String registerCode;

    @ApiModelProperty(value = "无人机挂载型号")
    private String uavMountModel;

    @ApiModelProperty(value = "接入类型：0-私协接入，1-国标接入，2-主动注册，字典：access_type")
    private String accessType;

    private String accessProtocols;
}
