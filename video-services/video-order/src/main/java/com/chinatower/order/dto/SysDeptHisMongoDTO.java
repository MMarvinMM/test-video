package com.chinatower.order.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection="sys_dept_his")
public class SysDeptHisMongoDTO extends BaseMongoDTO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 部门编码
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 上级部门编码
     */
    private String parentId;

    /**
     * 上级部门名称
     */
    private String parentName;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 显示顺序
     */
    private Long orderNum;

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
     * 部门状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private Integer delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 部门等级
     */
    private String deptLevel;

    /**
     * 归属主体
     */
    private String bussnessUnit;

    /**
     * 主数据编码
     */
    private String mdmOrgCode;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 归属铁塔组织编码
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

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 安全等级：1.高风险 2.中高风险 3.中风险 4.中低风险 5.低风险
     */
    private String securityLevel;
}
