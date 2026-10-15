package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

import java.util.List;

/**
 * 统计待研判数量发送短信实体类
 * @author zhaozh
 */
@Data
public class JudgeOrganVO {
    private String userId; //用户编码
    private List<String> orgIdList;//用户归属部门所有上级编码
    private String parentOrgIdStr; //上级组织拼接串
    private String parentOrgId; //上级组织
}
