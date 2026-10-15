package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SysDeptDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 父部门ID
     */
    private String parentId;
    /**
     * 父部门名称
     */
    private String parentName;
    /**
     * 祖级列表
     */
    private String ancestors;
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
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 部门级别
     */
    private String deptLevel;


    /**
     * 归属主体
     */
    private String bussnessUnit;
    /** 主数据编码 */
    private String mdmOrgCode;
    /**
     * 租户IP
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
     * 备注
     */
    private String remark;
    /** 行业应用版本编码 */
    private String appVerCode;
    /** 行业编码 */
    private String industryCode;
}
