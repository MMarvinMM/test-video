package com.chinatower.platform.entity.order;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName QueryOrderRelationshipVO
 * @Deacription 查询租户订购信息对象
 * @Author chenmf
 * @Date 2022/3/8 14:25
 * @Version 1.0
 **/
@Data
@ApiModel(value = "查询租户订购信息对象",description = "查询租户订购信息对象")
public class QueryOrderRelationshipVO extends BaseDataScopeEntity implements Serializable {

    /**
     * 客户编码
     * @required true
     */
    @NotNull(message = "客户编码不能为空")
    @ApiModelProperty(value = "客户编码")
    private String tenantId;

    @ApiModelProperty(value = "客户名称")
    private String tenantName;

    @ApiModelProperty(value = "订单编码")
    private String orderCode;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;
    private String[] orderStatusArr;

    @ApiModelProperty(value = "摄像机编号")
    private String deviceCode;

    @ApiModelProperty(value = "摄像机名称")
    private String deviceName;

    @ApiModelProperty(value = "算法名称")
    private String algorithmName;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "物联网设备编号")
    private String sensorDeviceCode;

    @ApiModelProperty(value = "物联网设备名称")
    private String sensorDeviceName;

    @ApiModelProperty(value = "雷达名称")
    private String radarName;

    @ApiModelProperty(value = "雷达编号")
    private String radarCode;

    @ApiModelProperty(value = "卫星名称")
    private String satelliteName;

    @ApiModelProperty(value = "无人机设备名称")
    private String uavName;

    @ApiModelProperty(value = "无人机设备编号")
    private String uavCode;

    @ApiModelProperty(value = "无人机机巢设备名称")
    private String uavNestName;

    @ApiModelProperty(value = "无人机机巢设备编号")
    private String uavNestCode;

    @ApiModelProperty(value = "喇叭设备名称")
    private String hornName;

    @ApiModelProperty(value = "喇叭设备编号")
    private String hornCode;

    @ApiModelProperty(value = "执法记录仪设备编号")
    private String recorderCode;

    @ApiModelProperty(value = "执法记录仪设备名称")
    private String recorderName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;

    /**
     * 排序方式
     * 排序字段
     * @return
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

    /**
     * 节点名编号 查询字段
     */
    private String locationCode;


    /**
     * 订单编码列表
     */
    private List<String> orderCodeList;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 设备类型 1-雷达,2传感器
     */
    private Integer categoryCode;
}
