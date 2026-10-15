package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
import com.chinatower.event.dataobject.FlowLinkUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/2/7 15:09
 */
@Data
public class FlowModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1205389362077121933L;

    private Long flowId;
    /**
     * 租户编码
     */
    @NotBlank(message = "租户编码不能为空")
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 流程模板名称
     */
    @NotBlank(message = "流程环节名称不能为空")
    private String flowName;
    /**
     * 流程模板前端展示json 详见antv-g6
     * id 元素标识 style元素样式 type元素类型 label元素文本标签
     * nodes 节点数据
     * edges 连线数据  自定义边 起始节点startPoint 结束节点endPoint
     */
    @NotBlank(message = "模板JSON不能为空")
    private String flowJson;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FlowLinkAttrModel> data;
    private String linkType;
    private String userType;
    private String roleId;
    private String msgNotifyType;
    private String leaderFlag;
    private String signFlag;
    private String rollbackFlag;
    private Long linkId;
    private Long flowLinkId;
    private Long alarmFlowId;
    private String preLink;
    private String nextLink;
    private String linkName;
    private Long timeLimit;
    private String orgId;
    private String userId;
    private String userName;
    private String nickName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FlowLinkUser> linkUserList;
    private String alarmCode;
    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;

    /** '事件来源' */
    private String warningSource;

    //环节Id
    private Long curLink;

    //转派开关
    private String turnFlag;

    //真实环节id
    private String realLinkType;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;

    /**
     * 是否过滤数据权限（0否,1是）
     */
    private String dataFilterPermiss;
    /**
     * 是否展示客户管理员（0否,1是）
     */
    private String customAdmin;

    /**
     * 流程环节属性id
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     */
    private String enableAcceptanceMode;

    /**
     * 受限时限
     */
    private String timeLimitAcceptance;
}
