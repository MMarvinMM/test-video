package com.chinatower.event.dto;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 16:53
 */
@Data
public class FlowLinkUserModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = -6998546946044752795L;
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
