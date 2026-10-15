package com.chinatower.event.api.model;

import com.chinatower.event.dataobject.FlowLinkUser;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/2/7 15:09
 */
@Data
public class ApiFlowModel implements Serializable {
    private static final long serialVersionUID = -808120948718987718L;
    private Long flowId;
    private String flowName;
    private String tenantId;
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
    private String warningSource;
    private String alarmCode;


    //转派开关（转派标识(1:：是，0：否)）
    private String turnFlag;

    //真实环节id
    private String realLinkType;

    /**
     * 租户名称
     */
    private String tenantName;

    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;

    //环节Id
    private Long curLink;

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
