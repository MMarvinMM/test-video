package com.chinatower.platform.entity.request;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "短信入参", description = "短信校验码")
@Data
public class SmsCodeRequest extends CommonFeignEntity {

    /**
     * 手机号
     */
    @ApiModelProperty(value = "用户手机号", required = true, example = "13837416790")
    private String phone;

    /**
     * 加密后的手机号
     */
    @ApiModelProperty(value = "加密后的手机号", required = true)
    private String aesPhone;

    @ApiModelProperty(value = "消息类型,1-验证码", required = true)
    private String msgType = "1";

    /**
     * 加密后的用户名
     */
    private String userName;

    /**
     * 模板key
     */
    @ApiModelProperty(value = "模板key")
    private String templateKey;

    private String flag;
}
