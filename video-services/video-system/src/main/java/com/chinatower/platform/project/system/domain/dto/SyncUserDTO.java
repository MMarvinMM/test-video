package com.chinatower.platform.project.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门用户树实体
 *
 * @author qq on 2023/2/1
 */
@Data
public class SyncUserDTO implements Serializable {

    /**
     * 1	用户登录账号	userName	Y	varchar(64)	登录账号，唯一校验，规则校验
     * 2	用户姓名	nickName	Y	varchar(64)	用户姓名
     * 3	手机号	phoneNumber	Y	varchar(16)	手机号，规则校验
     * 4	客户编码	tenantId	Y	varchar(16)	客户编码
     * 5	客户名称	tenantName	Y	varchar(64)	客户名称
     * 6	省编码	provinceId	Y	varchar(16)	行政区域编码
     * 7	地市编码	cityId	Y	varchar(16)	行政区域编码
     * 8	所属铁塔组织编码	deptId	Y	varchar(16)	铁塔所属管理区域对应组织
     * 9	邮箱	email	N	varchar(64)	邮箱
     * 10	性别	sex	N	varchar(2)	性别:0男 1女
     * 11	操作类型	optType	Y	varchar(2)	0同步账号 1删除账号
     */

    /**
     * 用户登录账号，唯一校验，规则校验
     */
    private String userName;

    /**
     * 用户姓名
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 省编码
     */
    private String provinceId;

    /**
     * 地市编码
     */
    private String cityId;

    /**
     * 所属铁塔组织编码
     */
    private String deptId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别 0男 1女
     */
    private String sex;

    /**
     * 操作类型 0同步账号 1删除账号
     */
    private String optType;

}
