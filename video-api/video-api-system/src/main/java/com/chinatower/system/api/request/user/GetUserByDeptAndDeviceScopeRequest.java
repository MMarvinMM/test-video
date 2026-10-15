package com.chinatower.system.api.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value = "getUserByDeptAndDeviceScopeRequest", description = "根据设备编号获取有权限的用户列表")
public class GetUserByDeptAndDeviceScopeRequest {
    /**
     * 租户ID
     */
    @NotNull(message = "客户编码不能为空")
    private String tenantId;
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 行业编码不能为空
     */
    @NotNull(message = "行业编码不能为空")
    private String industryCode;

    /**
     * 行业版本编码
     */
    @NotBlank(message = "行业版本编码不能为空")
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    /**
     * 用户Id
     */
    @NotNull(message = "用户Id不能为空")
    @ApiModelProperty(value = "用户Id")
    private List<String> userIdList;

    /**
     * 告警来源 1-算法AI告警；2-人工上报；3-一键告警；4、摄像机算法告警 5-车辆卡口； 6-人脸卡口；7-行业上报；8-物联设备告警；9-卫星告警；10-雷达告警
     */
    private String alarmSource;

    /**
     * 告警类型
     */
    private String typeValue;

    /**
     * 市编码
     */
    private String cityId;
    /**
     * 县编码
     */
    private String countyId;
    /**
     * 省编码
     */
    private String provinceId;
}
