package com.chinatower.airline.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.airline.anno.EnumTypeValid;
import com.chinatower.airline.enums.OptTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 无人机获取/取消云台控制权参数
 *
 * @author ljy
 * @date 2025/12/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("无人机获取/取消云台控制权参数")
public class OpsGimbalReq extends BaseAirDeviceWithNodeInfoReq{

    @ApiModelProperty(value = "操作类型",example = "0:取消云台控制权 1:获取云台控制权")
    @NotNull(message = "操作类型不能为空")
    @EnumTypeValid(message = "操作类型支持 0|1 (取消|获取)", enumClass = OptTypeEnum.class)
    private Integer optType;
    
    @ApiModelProperty(value = "待释放控制权对应的令牌",notes = "当释放控制权时为必填")
    @JSONField(name = "X-Token")
    @JsonProperty(value = "xToken")
    private String xToken;
    @ApiModelProperty(value = "请求时长",notes = "当获取控制权时为必填，单位秒")
    @JSONField(name = "X-Duration")
    @JsonProperty(value = "xDuration")
    private String xDuration;
    @ApiModelProperty(value = "申请锁的当前用户",notes = "申请锁的当前用户")
    @JSONField(name = "X-UserCode")
    @JsonProperty(value = "xUserCode")
    private String xUserCode;
    @ApiModelProperty(value = "申请锁用户级别",notes = "当前用户的优先级，数字越大，优先级越高，优先级高的会抢占锁（1-9）")
    @JSONField(name = "X-level")
    @JsonProperty(value = "xLevel")
    private String xLevel;
    @ApiModelProperty(value = "申请锁用户组织",notes="申请锁用户的归属组织")
    @JSONField(name = "X-deptCode")
    @JsonProperty(value = "xDeptCode")
    private String xDeptCode;
    @ApiModelProperty(value = "申请锁租户编码",notes = "申请锁用户的租户编码")
    @JSONField(name = "X-CustomerCode")
    @JsonProperty(value = "xCustomerCode")
    private String xCustomerCode;
    @ApiModelProperty(value = "有效时长")
    private Integer duration;

}
