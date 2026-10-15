package com.chinatower.emergency.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 应急组织表
 * vm_emergency_dept
 * @author Liqi
 */
@Data
public class EmergencyDeptDTO implements Serializable {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 主键ID
     */
    private String oldId;

    /**
     * 组织名称
     */
    private String emergencyDeptName;

    /**
     * 上级部门编码
     */
    private String parentId;

    /**
     * 组织等级
     */
    private Integer deptLevel;

    /**
     * 祖籍列表
     */
    private String ancestors;

    /**
     * 应急事件编码
     */
    private String eventCode;

    /**
     * 指挥官标识（1为指挥官）
     */
    private Integer commanderFlag;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer deleteFlag;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String modifier;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业编码
     */
    @TableField(exist = false)
    private List<String>  deptNameList;


    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 接口标识flag（返回用户信息：1，不返回用户信息：2）（返回指挥官信息：1）
     */
    private Integer flag;


    /**
     * 接口标识flag（不返回空组织信息：1，返回空组织信息：2）
     */
    private Integer deptFlag;


    /**
     * 融合通信标识flag（不返回未配置融合通信号码用户：1  不返回融合通信号码：3）
     */
    private String correspondenceFlag;


    @TableField(exist = false)
    private List<String> idList;

    @TableField(exist = false)
    private String topDeptId;

    @TableField(exist = false)
    private List<EmergencyUserDTO> userList;

    private static final long serialVersionUID = 1L;
}