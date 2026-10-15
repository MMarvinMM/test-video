package com.chinatower.event.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程环节用户实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 16:19
 */
@Data
public class FlowLinkUser implements Serializable {
    private static final long serialVersionUID = 4182320090573659121L;

    /** 主键 */
    private Long linkUserId;
    /** 模板ID */
    private Long flowId;
    /** 环节属性ID */
    private Long flowLinkId;
    /** 用户类型，1用户，2角色，3领导 */
    private String userType;
    /** 用户ID */
    private String userId;
    /** 组织ID */
    private String orgId;

}
