package com.chinatower.manage.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SysIndustryConfEdition {


    private Long editionId;

    private Long industryId;
    /** 行业编码 */
    private String industryCode;
    /** 行业名称 */
    private String industryName;
    /** 行业描述 */
    private String industryDesc;
    /** 应用图标 */
    private String imgUrl;
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
    /** 1生效 0失效 */
    private String status;
    /** 删除标识 0-未删除 1-已删除 */
    private String deleteFlag;
    /** 接口配置 0-API 1-SDK 2-未配置 */
    private String apiIntfConfigType;
    /** appKey */
    private String appKey;
    /** secretKey */
    private String secretKey;
    /** 受理节点 */
    private String acceptNode;
    /** 受理节点 */
    private String acceptNodeName;

    /**
     * 行业名称
     */
    private String industryTypeName;

    private String industryTypeCode;
    /**
     * 属地版本号id
     */
    private Long localizedVersionId;

    private String isSelect;


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
     * 产品简介附件地址列表
     */
    private List<String> productBriefAttachUrlList;

    /**
     * 产品顺序
     */
    private Integer productSeq;

    /**
     * 对比明细页面 是否勾选新增（0：未勾选 1：被勾选）
     */
    private Integer selected;

}
