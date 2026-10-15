package com.chinatower.platform.project.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.platform.entity.request.WxMsgData;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


/**
 * @ClassName WxPushRequestDTO
 * @Deacription
 * @Author dencycheng
 * @Date 2022/12/31 14:57
 * @Version 1.0
 **/
@Data
@ApiModel(value="微信公众号消息推送入参", description="微信公众号消息推送入参")
@Document(collection="bs_wx_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxPushRequestDTO extends BaseMongoDTO {

    /**
     * 主键编码
     */
    @ApiModelProperty(value="主键编码")
    private String id;
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

    /**
     * 消息内容标题
     */
    @ApiModelProperty(value="链接地址")
    private String linkUrl;
    /**
     * 业务参数
     */
    @ApiModelProperty(value="业务扩展参数")
    private WxMsgData extrasparam;

    /**
     * 0正常1删除
     */
    @ApiModelProperty(value="业务扩展参数")
    private String deleteFlag;

    /**
     * 创建人
     */
    @ApiModelProperty(value="创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 最后修改人
     */
    @ApiModelProperty(value="最后修改人")
    private String updateBy;

    /**
     * 最后修改时间
     */
    @ApiModelProperty(value="最后修改时间")
    private Date updateTime;

    /**
     * 请求json
     */
    public String requestJson;

    /**
     * 请求url
     */
    public String requestUrl;

    /**
     * 事件id
     */
    public String eventId;

    public WxPushRequestDTO() {
        this.collectionName = "bs_wx_record";
    }
}
