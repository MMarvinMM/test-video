package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName FindDeviceCodeVO
 * @Author chenmf
 * @Date 2022/3/5 10:19
 * @Version 1.0
 **/
@Data
@ApiModel(value = "findDeviceCodeVO",description = "查询用户订购行业版本下的设备编号实体")
public class FindDeviceCodeVO implements Serializable {

    /**
     * 行业版本编码
     * @required true
     */
    @NotBlank(message = "行业版本编码不能为空")
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    /**
     * 用户Id
     * @required true
     */
    @NotBlank(message = "用户Id不能为空")
    @ApiModelProperty(value = "用户Id")
    private String userId;
}
