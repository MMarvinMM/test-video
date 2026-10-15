package com.chinatower.manage.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_localization_industry_conf")
public class LocalizationSysIndustryConf implements Serializable {
    /** 主键 */
    @TableId(value = "industry_id", type = IdType.AUTO)
    private Long industryId;
    /** 行业编码 */
    private String industryCode;
    /** 行业名称 */
    private String industryName;
    /**
     * 所属行业类型id
     */
    private String industryTypeCode;
    /** 行业描述 */
    private String industryDesc;
    /** 受理节点 */
    private String acceptNode;
    /** 0-未同步 1-同步成功 2-同步失败 */
    private String syncStatus;
    /** 同步时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date syncTime;
    /** 1生效 0失效 */
    private String status;
    /** 应用图标 */
    private String imgUrl;

    /**
     * 移动端应用图标地址
     */
    private String mobileAppIconUrl;

    /**
     * 登录展示图地址
     */
    private String loginDisplayImgUrl;

    /**
     * 产品简介信息
     */
    private String productBriefInfo;
    /**
     * 产品顺序
     */
    private Integer productSeq;

    /** 删除标识 0-未删除 1-已删除 */
    private String deleteFlag;
    /** 创建人 */
    private String creator;
    /** 修改人 */
    private String modifier;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

}
