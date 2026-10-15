package com.chinatower.message.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
@ApiModel(value = "极简模式抓图消息请求-对象", description = "极简模式抓图消息请求-对象")
@AllArgsConstructor
public class SampleMsgReq {
    @ApiModelProperty(value = "用户ID")
    private String userId;
    @ApiModelProperty(value = "uuid")
    private String msgId;
    @ApiModelProperty(value = "通道编码")
    private String channelCode;
    @ApiModelProperty(value = "图片")
    private String image;
    @ApiModelProperty(value = "消息关键字")
    private String key;
}
