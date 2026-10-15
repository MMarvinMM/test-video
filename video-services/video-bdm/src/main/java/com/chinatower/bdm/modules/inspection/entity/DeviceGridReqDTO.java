package com.chinatower.bdm.modules.inspection.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @Description 设备请求实体类
 * @Author zl
 * @Date 2022/09/10
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceGridReqDTO extends BaseDataScopeEntity implements Serializable {

    @ApiModelProperty(value = "租户编码")
    private String tenantId;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    @ApiModelProperty(value = "摄像头状态")
    private String status;

    @ApiModelProperty(value = "预置点id")
    private String provinceId;

    @ApiModelProperty(value = "省级编码")
    private String cityId;

    @ApiModelProperty(value = "市级编码")
    private String countyId;

    @ApiModelProperty(value = "区级编码")
    private Set<String> orgIdList;

    @ApiModelProperty(value = "部门编码")
    private List<String> deptIds;

    @ApiModelProperty(value = "用户id")
    private List<String> userIds;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "客户编码")
    private List<String> tenantIds;

    /** 客户编码 */
    private List<String> customerIds;

    @ApiModelProperty(value = "行业编码")
    private String appVerCode;

    @ApiModelProperty("厂家信息")
    private String modelCode;

    //0:收藏
    @ApiModelProperty("收藏标签")
    private String isMonitorFlag;

    /**
     * 不传 或者 false 都是查询有效订购设备，传入 true 查询所有订购设备
     */
    @ApiModelProperty("查询全部标识")
    private Boolean allOrderFlag;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("距离")
    private String distance;

    /**
     * 当 nearest 不为空时 取最接近的一条数据
     */
    @ApiModelProperty("距离最近的")
    private String nearest;

    @ApiModelProperty(value = "设备编码列表")
    private List<String> deviceCodes;

    @ApiModelProperty("开始日期")
    private String startDate;

    @ApiModelProperty("结束日期")
    private String endDate;

    @ApiModelProperty(value = "页数")
    private Integer pageNo = 0;

    @ApiModelProperty(value = "分页数")
    private Integer pageSize = 10;


    @ApiModelProperty("是否返回设备")
    private Boolean backDevFlag;
}
