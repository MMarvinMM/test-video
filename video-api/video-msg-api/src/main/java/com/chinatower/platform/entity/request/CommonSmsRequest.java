package com.chinatower.platform.entity.request;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: chinatowerH
 * @create: 2020/10/29
 **/
@ApiModel(value="短信入参", description="短信信息相关入参")
@Data
public class CommonSmsRequest extends CommonFeignEntity {
    /**
     * 手机号
     * @required true
     */
    @ApiModelProperty(value="用户手机号",required = true,example = "13837416790")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 短信内容
     * @required false
     */
    @ApiModelProperty(value="短信内容",required = false)
    private String smsTxt;
    /**
     * 短信内容
     * @required true
     */
    @ApiModelProperty(value="短信类型 1-验证码 2-短信",required = true)
    private String msgType;
    /**
     * 短信内容
     * @required false
     */
    @ApiModelProperty(value="验证码",required = false)
    private String smsCode;


    /**
     * 验证码模板
     * @required false
     */
    @ApiModelProperty(value="验证码模板, 占位符使用code",required = false)
    private String smsTemplate;


    /**
     * 验证码模板
     * @required false
     */
    @ApiModelProperty(value="mapjson串，结合架构室的短信网关",required = false)
    private String requestMap;

    /**
     * 模板编码
     * @required false
     */
    @ApiModelProperty(value="模板编码",required = false)
    private String templateCode;

}
