package com.chinatower.platform.entity.request;


import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.chinatower.platform.model.dto.WarningOrderMsgDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value="极光推送入参", description="极光推送信息相关入参")
public class JgPushRequest extends CommonFeignEntity {
    /**
     * 极光别名 可以为单个  也可以为集合  集合每次最多5000
     * @required true
     */
    @ApiModelProperty(value="极光别名 可以为单个  也可以为集合  集合每次最多5000")
    @NotBlank(message = "极光别名不能为空")
    private String alias;
    /**
     * 通知内容标题
     * @required true
     */
    @ApiModelProperty(value="通知内容标题")
    private String notificationTitle;
    /**
     * 消息内容标题
     * @required true
     */
    @ApiModelProperty(value="消息内容标题")
    private String msgTitle;

    /**
     * 事件状态名称
     */
    private String orderStatusName;

    /**
     * 事件地点
     */
    private String address;

    /**
     * 消息内容
     * @required true
     */
    @ApiModelProperty(value="消息内容")
    private String msgContent;
    /**
     * 扩展字段
     * @required true
     */
    @ApiModelProperty(value="扩展字段")
    private String extrasparam;

    /**
     * 消息类型
     * 1- 告警、超期
     * 2- 通知公告
     * 3- 行业资讯
     * 4- 其他消息 (巡查等)
     * @required true
     */
    @ApiModelProperty(value="消息类型")
    private String type;

    /**
     * 推送类型
     * 1- 通知类
     * 0- 消息类
     * @required true
     */
    @ApiModelProperty(value="推送类型")
    private String  msgType;

    /**
     * 推送声音文件名称
     * @required true
     */
    @ApiModelProperty(value="推送声音文件名称")
    private String sound;

    /**
     * 消息id
     * @required false
     */
    private String msgId;

}
