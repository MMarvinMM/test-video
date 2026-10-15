package com.chinatower.platform.project.entity.request;


import com.alibaba.fastjson.JSON;
import com.chinatower.platform.entity.request.WxMsgData;
import com.chinatower.platform.entity.request.WxPushRequest;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value="微信公众号消息推送入参", description="微信公众号消息推送入参")
public class WxPushRequestVo extends WxPushRequest {
    /**
     * 微信用户ID
     */
    @ApiModelProperty(value="微信用户ID")
    private String unionId;
    /**
     * 微信公众号用户ID
     */
    @ApiModelProperty(value="微信公众号用户ID")
    private String openId;
    /**
     * 发送通知返回的信息ID
     */
    @ApiModelProperty(value="发送通知返回的信息ID")
    private String messageId;
    /**
     * 发送成功失败状态
     */
    @ApiModelProperty(value="发送成功失败状态")
    private String sendResult;
    /**
     * 发送失败原因
     */
    @ApiModelProperty(value="发送失败原因")
    private String failReason;

    public String getExtrasparamStr(){
        return super.getExtrasparam() == null? "": JSON.toJSONString(super.getExtrasparam());
    }
}
