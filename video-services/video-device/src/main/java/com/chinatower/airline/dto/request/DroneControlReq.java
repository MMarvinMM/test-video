package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 虚拟摇杆控制参数
 *
 * @author ljy
 * @date 2026/1/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("虚拟摇杆控制参数类")
public class DroneControlReq extends BaseAirDeviceWithNodeInfoReq{

    @ApiModelProperty(value = "前进后退方向的速度", notes = "负值表示向后移动，最小值：-17，最大值：17，单位为米每秒（一般1/8/15三挡，默认8）")
    private Double pitch;
    @ApiModelProperty(value = "左右方向的速度", notes = "负值表示向左移动最小值：-17，最大值：17，单位为米每秒（一般1/8/15三挡，默认8）")
    private Double roll;
    @ApiModelProperty(value="机身角速度", notes = "负值表示逆时针转动，最小值：-90，最大值：90，单位为弧度每秒")
    private Double yaw;
    @ApiModelProperty(value="上下方向的速度", notes = "负值表示向下移动，最小值：-4，最大值：5，单位为米每秒(默认+2/-2)")
    private Double throttle;

}
