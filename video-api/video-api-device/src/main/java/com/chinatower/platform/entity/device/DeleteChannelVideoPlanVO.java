package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DeleteChannelVideoPlanVO
 * @Deacription 删除路学计划对象
 * @Author chenmf
 * @Date 2022/1/27 10:40
 * @Version 1.0
 **/
@Data
public class DeleteChannelVideoPlanVO implements Serializable {

    //录像计划id
    @NotNull(message = "录像计划id不能为空")
    private Long recordPlanId;

    //设备编号
    @NotNull(message = "设备编号不能为空")
    private String deviceCode;

    //通道编号
    @NotNull(message = "通道编号不能为空")
    private String channelCode;

    //客户唯一编码
    @NotNull(message = "客户编码不能为空")
    private String tenantId;

    //用户编码
    private String userCode;

    //订购关系编码
    private String orderCode;

    //行业编码
    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    //行业版本编码
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;
}
