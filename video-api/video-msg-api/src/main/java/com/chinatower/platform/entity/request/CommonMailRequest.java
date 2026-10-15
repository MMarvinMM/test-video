package com.chinatower.platform.entity.request;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 邮件发送入参（内部 Feign 调用）
 */
@ApiModel(value = "邮件入参", description = "邮件发送相关入参")
@Data
public class CommonMailRequest extends CommonFeignEntity {

    /**
     * 收件箱，多个以英文逗号分隔
     */
    @ApiModelProperty(value = "收件箱", required = true)
    @NotBlank(message = "收件箱不能为空")
    private String receiveMail;

    /**
     * 抄送人，多个以英文逗号分隔
     */
    @ApiModelProperty(value = "抄送人")
    private String ccMail;

    /**
     * 邮件标题，不传则使用邮件类型名称
     */
    @ApiModelProperty(value = "邮件标题")
    private String mailTitle;

    /**
     * 邮件正文，与 mailTemplateCode 二选一
     */
    @ApiModelProperty(value = "邮件正文")
    private String mailTxt;

    /**
     * 邮件模板编码，与 mailTxt 二选一
     */
    @ApiModelProperty(value = "邮件模板编码")
    private String mailTemplateCode;

    /**
     * 模板参数 JSON 串，配合 mailTemplateCode 使用
     */
    @ApiModelProperty(value = "模板参数 JSON 串")
    private String paraMap;
}
