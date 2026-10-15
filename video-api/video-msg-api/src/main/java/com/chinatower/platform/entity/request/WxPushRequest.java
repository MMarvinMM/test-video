package com.chinatower.platform.entity.request;


import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value="微信公众号消息推送入参", description="微信公众号消息推送入参")
public class WxPushRequest extends CommonFeignEntity {
    /**
     * 主键编码
     * @required true
     */
    @ApiModelProperty(value="主键编码")
    private String id;
    /**
     * 消息内容标题
     * @required false
     */
    @ApiModelProperty(value="链接地址")
    private String linkUrl;
    /**
     * 业务参数
     * @required true
     */
    @ApiModelProperty(value="业务扩展参数")
    private WxMsgData extrasparam;

    /**
     * 接收通知的用户ID数组，若存在内容忽略userId字段
     * @required true
     */
    @ApiModelProperty(value="接收通知的用户ID数组，若存在内容忽略userId字段")
    private String[] userIds;
}
