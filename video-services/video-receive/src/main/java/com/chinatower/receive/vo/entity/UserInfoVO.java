package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: UserInfoVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/25 17:31
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class UserInfoVO implements Serializable {

    /**
     * 用户唯一id
     */
    private String userId;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 人员层级 0总部|1省公司|2地市公司
     */
    private String userLevel;

    /**
     * 所属组织机构
     */
    private String orgId;

    /**
     * 年龄
     */
    private String age;

    /**
     * 性别：男1女2
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 移动电话
     */
    private String mobilePhone;

    /**
     * 固定电话
     */
    private String phone;

    /**
     * 来源：0铁塔 1租户
     */
    private String source;

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 省份
     */
    private String provinceId;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 区县
     */
    private String countyId;

    /**
     * 摄像头操作优先级   目前暂定  1   2   3 个等级
     */
    private String manipulateLevel;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 性别名称
     */
    private String sexName;

    //角色标识：8领导
    private String roleFlag;

    //角色ID：5调度人员6处置人员
    private String roleId;

    /**
     * 删除状态：0正常 1删除
     */
    private String deleteFlag;
    private int pageNum;
    private int pageSize;

    private List<String> orgIdList;

    private static final long serialVersionUID = 1L;
}
