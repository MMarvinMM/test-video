package com.chinatower.emergency.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 应急用户表
 * vm_emergency_user
 * @author Liqi
 */
@Data
public class EmergencyUserDTO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户IDList
     */
    private List<String> userIdList;

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
     * 部门ID
     */
    private String emergencyDeptId;

    /**
     * 部门ID
     */
    @TableField(exist = false)
    private List<String> emergencyDeptIdList;

    /**
     * 租户id
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
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;


    /**
     * 移动端号码
     */
    @TableField(exist = false)
    private String mobileTerminalNum;

    /**
     * 调度台号码
     */
    @TableField(exist = false)
    private String dispatchDeskNum;

    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String nickName;

    private static final long serialVersionUID = 1L;
}