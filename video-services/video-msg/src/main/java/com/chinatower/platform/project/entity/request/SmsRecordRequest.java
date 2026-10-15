package com.chinatower.platform.project.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author
 * 短信记录表
 */
@ApiModel(value="短信验证码入参", description="短信验证码信息相关入参")
@Data
public class SmsRecordRequest implements Serializable {

    /**
     * 手机号
     */
    @ApiModelProperty(value="用户手机号",required = true,example = "13837416790")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 短信内容
     */
    @ApiModelProperty(value="短信内容",required = false)
    private String smsTxt;

    /**
     * 模板key
     */
    @ApiModelProperty(value = "模板key")
    private String templateKey;
    /**
     * 短信内容
     */
    @ApiModelProperty(value="短信类型 1-验证码 2-短信",required = true)
    private String msgType;
    /**
     * 短信内容
     */
    @ApiModelProperty(value="验证码",required = false)
    @Size(max = 8)
    private String smsCode;

    /**
     *加密后的手机号
     */
    @ApiModelProperty(value="加密后的手机号",required = true)
    private String aesPhone;

}
