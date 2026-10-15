package com.chinatower.receive.vo.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: UsersVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/11 15:18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
@ToString
public class UsersVO implements Serializable {

    // 用户编码
    private String userId;

    // 登录名
    private String userAccount;

    // 姓名
    private String userName;

    // 组织机构编码
    private String orgId;

    private String userPassword;
    private String age;
    private String sex;
    private String email;
    private String mobilePhone;
    private String originalMobilePhone;//原手机号
    private String encryptionMobilePhone;//脱敏手机号
    private String phone;
    private String encryptionPhone;//脱敏固定电话
    private String source;
    private String tenantId;//租户编码
    private String manipulateLevel;//操作层级
    private String remark;//备注
    private String creator;//创建人
    private Date creatorDate;//创建时间
    private String modifier;//修改人
    private Date modifierDate;//修改时间
    private String deleteFlag;//0正常 1删除
    //    private Integer pageNum;
//    private Integer pageSize;
    private String orgName;
    private String operateFlag;//操作标识：1编辑2生失效
    private String platform;//平台 app端传app
    private String userAccountFlag;//精确查询登录账号
    private String userLevel;//用户层级
    private String provinceId;
    private String cityId;
    private String countyId;
    private String tenantName;
    private String roleId;
    private String isFirst;
    private String provinceName;
    private String cityName;

    private String gridId;
    private String gridName;

    //铁塔视角 增加租户id 字段（区别于tenantId）
    private String customerCode;
    //客户名称/ID 关键字
    private String tenantkeyword;
    private List<String> tenantList;//租户ID列表
    private String activeName;

    //private List<UserOrganDeviceVO> deviceInfo;//设备
    private List<String> roleList;//角色

    private String orderFlag;//订购标识 1运管推送 0非运管推送

    private String queryFlag;//查询标识 0 从缓存取id  2 铁塔用户管理查询标识

    private String towerUserSource;//铁塔用户来源：1：4A 2：自建

    private String sexName;//性别名称

    private String towerUserSourceName;//铁塔用户来源名称

    private String roleNameStr;//角色名称

    private String registrationId;//注册id

    private String pushFlag;//推送标识

    private String orgLevel;//推送标识

    private List<String> orgIdList;//组织ID列表

    private String parentOrgId;

    private String parentOrgLevel;

    private String areaOrgName;

    private String areaOrgId;

    private String oriOrgId;//原始组织id

    private List<String> deleteFlagList;

    private String keyStr;//秘钥

    private String startDate;//开始时间

    private String endDate;//结束时间

    private String showCreateDate;//创建时间

    private String mapModel;//最近一次地图模式 1：常规地图 2：卫星地图 3：深色地图

    private String treeChoose;//最近一次区域、组织树选择

    private List<String> userAccountList;
}
