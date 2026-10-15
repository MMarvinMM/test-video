package com.chinatower.event.entity.vo;

import com.chinatower.platform.entity.admin.SysUser;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: SysUserVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/8 19:57
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class SysUserVO extends SysUser {
    /**
     * 是否具有子节点
     */
    private Boolean hasChildren;
    /**
     * 租户IP
     * @required true
     */
    private String tenantId;

    /**
     * 租户组织归属铁塔组织ID
     */
    private String tenantTowerDeptId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 父部门ID
     */
    private String parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 省分公司编码
     */
    private String province;

    /**
     * 市分公司编码
     */
    private String city;

    /**
     * 父部门名称
     */
    private String parentName;

    private String deptLevel;

    private String bussnessUnit;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;
}
