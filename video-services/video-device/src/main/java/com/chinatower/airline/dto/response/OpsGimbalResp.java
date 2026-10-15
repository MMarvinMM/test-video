package com.chinatower.airline.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 进入云台控制返回值
 *
 * @author ljy
 * @date 2026/1/4
 */
@Data
public class OpsGimbalResp {
    
    @ApiModelProperty(value = "获取锁标志", notes = "是否成功获取锁，1-获取锁；0-没获取到锁",required = true)
    private int lock;
    @ApiModelProperty(value = "请求令牌",notes = "获取云台控制权后，基础平台返回的令牌token",required = true)
    private String token;
    
    @ApiModelProperty(value="等待时长",notes = "请求令牌不成功时为必填，单位为秒")
    private int waitDuration;
    @ApiModelProperty(value="可用时间",notes = "返回锁定成功后可使用的时长，单位：秒（成功时）网关自身进行逻辑控制")
    private int duration;
    @ApiModelProperty(value="排队位次",notes = "排队位次，例如 3 代表着排在第3位，前面有2人在排队等候,网关自身进行逻辑控制")
    private int queue;
    @ApiModelProperty(value="排队时间",notes = "以当前最长时间来预估排队时间，单位：秒 网关自身进行逻辑控制")
    private int wait;
    @ApiModelProperty(value="申请锁租户",notes = "申请锁的当前租户")
    private String currentExecuteCustomerCode;
    @ApiModelProperty(value="申请锁用户",notes = "申请锁的当前用户")
    private String currentExecuteUserCode;
    @ApiModelProperty(value="申请锁用户级别",notes = "当前用户的优先级，数字越大，优先级越高，优先级高的会抢占锁（1-9）")
    private String level;
    @ApiModelProperty(value="申请锁用户组织",notes = "申请锁用户的归属组织")
    private String deptCode;
    
}
