package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.ToString;

/**
 * @desription: 公共VO
 * @data 创建时间：2023/3/27 9:11
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ToString
public class GridKeeperBaseVO extends BaseDataScopeEntity {
    /**
     * 网格员ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long keeperId;

    /**
     * 网格员类型
     */
    private String gridType;


    /**
     * aes加密手机号
     */
    private String aesPhoneNumber;


    /**
     * aes加密身份证号
     */
    private String aesKeeperIdCardNumber;



    /**
     * 网格员编号
     */
    private String keeperCode;

    /**
     * 人员id
     */
    private String staffId;

    /**
     * 人员岗位信息id
     */
    private String staffOrgId;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 选用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String entryTime;

    /**
     * 微信号
     */
    private String wechatNumber;

    /**
     * 政治面貌
     */
    private String party;

    /**
     * 学历
     */
    private String education;

    /**
     * 是否特殊网格员
     */
    private String isSpecial;

    /**
     * 特殊网格员分类
     */
    private String specialType;

    /**
     * 专业类别
     */
    private String majorType;

    /**
     * 人员身份
     */
    private String keeperAidentity;

    /**
     * 职业格言
     */
    private String keeperMotto;

    /**
     * 个人简介
     */
    private String personalProfile;

    /**
     * 是否网格长
     */
    private String isGridManager;

    /**
     * 展示顺序
     */
    private Integer keeperSort;

    /**
     * 星级标识
     */
    private String starClass;

    /**
     * 乐观锁
     */
    private Integer versions;

    /**
     * 身份证号
     */
    private String keeperIdCardNumber;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String keeperBirthDate;

    /**
     * 现居住地
     */
    private String keeperAddress;

    /**
     * 联系方式
     */
    private String keeperPhoneNumber;

    /**
     * 邮箱
     */
    private String keeperEmail;

    /**
     * 性别: 0-男；1-女
     */
    private String keeperGender;

    /**
     * 网格员姓名
     */
    private String keeperName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 打点时间
     */
    private String takePointTime;

    /**
     * 图片地址
     */
    private String fjUrl;

    /**
     * 1 在线 0 离线
     */
    private String isLine;

    /**
     * 实时定位省份编码
     */
    private String provinceId;

    /**
     * 实时定位地市编码
     */
    private String cityId;

    /**
     * 实时定位区县编码
     */
    private String countyId;

    /**
     * 是否开启定位：0-是；1-否
     */
    private String locationFlag;
    /**
     * 网格员类型：1-网格长；2-网格员
     */
    private String keeperType;
    /**
     * 工作职责
     */
    private String operatingDuty;

    /**
     * 组织名称
     */
    private String staffOrgName;

    /**
     * 网格编码集合
     */
    private String[] gridIds;

    private String[] gridNames;
    /**
     * 0-启用 1-停用
     */
    private String status;

    /**
     * 网格员位置距离中心点距离
     */
    private String distance;

    /**
     * 网格名称字符串
     */
    private String gridNameStr;

    private String tenantName;
}
