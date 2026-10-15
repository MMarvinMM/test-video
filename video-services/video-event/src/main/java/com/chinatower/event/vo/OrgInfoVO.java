package com.chinatower.event.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @program: com.chinatower.product.cust.modules.receivealarmservice.entity
 * @description: TODO
 * @author: shenyq@strongdata.com.cn
 * @create: 2021/12/29 11:23
 */
@Data
@ToString
public class OrgInfoVO {

    private String orgId;

    private String orgName;

    private String tenantId;

    private String orgLevel;

    //组织类型：1组织 2部门
    private String orgType;

    private String deviceCode;

}
