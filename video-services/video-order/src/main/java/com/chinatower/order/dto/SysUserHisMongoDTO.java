package com.chinatower.order.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection="sys_user_his")
public class SysUserHisMongoDTO extends BaseMongoDTO implements Serializable {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（0系统用户 2租户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 固定电话（冗余字段，同tel_phone）
     */
    private String telephone;

    /**
     * 用户性别（0男 1女）
     */
    private String sex;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登陆IP
     */
    private String loginIp;

    /**
     * 最后登陆时间
     */
    private Date loginDate;

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
     * 备注
     */
    private String remark;

    /**
     * 登录失败次数
     */
    private Integer loginFailNum;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 0:普通人员 1:部门负责人 2:公司领导
     */
    private String grade;

    /**
     * 工号
     */
    private String empNo;

    /**
     * 排序
     */
    private String sortNo;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 是否租户管理员(0否,1是)
     */
    private Integer isTenantAdmin;

    /**
     * 铁塔账号来源   0:4a 1:自建
     */
    private String towerUserSource;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;

    /**
     * 省份
     */
    private String provinceId;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 账号来源(0-4A,1-自建)
     */
    private String userSource;

    /**
     * 固定电话
     */
    private String telPhone;

    /**
     * 密码修改时间
     */
    private Date pwdUpdateTime;

    /**
     * 执法编号
     */
    private String enforcement;

    /**
     * 润之认证路由
     */
    private String adopt;

    /**
     * 润之登录路由
     */
    private String login;

    /**
     * 账号有效期状态(1 永久有效  2 自定义日期)
     */
    private String validityPeriodState;

    /**
     * 账号有效期
     */
    private Date accountValidityPeriod;

    /**
     * 0:不判断白名单，1：判断白名单
     */
    private Integer whiteIpFlag;

    /**
     * 部门ID标准化字段（数据库生成列，仅查询用）
     */
    private String deptIdNormalized;

}
