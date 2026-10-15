package com.chinatower.manage.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SysIndustryScreenEditionVO {

    private Long editionId;

    private Long id;

    /**
     * 大屏名称
     */
    private String screenName;

    /**
     * 大屏状态--0-未使用  1-使用中
     */
    private String screenStatus;

    /**
     * 大屏状态名称
     */
    private String screenStatusName;

    /**
     * 组件类型
     */
    private Integer screenCompoType;

    /**
     * 大屏图片地址
     */
    private String imgUrl;

    /**
     * 大屏链接
     */
    private String screenUrl;

    /**
     * 大屏描述
     */
    private String screenDesc;

    /**
     * 大屏目录ID
     */
    private Long pageId;

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    private String industryName;

    private String industryCode;

    private String isSelect;

    private Long localizedVersionId;

    private String pageName;

    /**
     * 修改后的端口
     */
    private String updatePort;

    /**
     * 对比明细页面 是否勾选新增（0：未勾选 1：被勾选）
     */
    private Integer selected;

    /**
     * 对比项（1：新增 2:差异 3:相同）
     */
    private String comparisonResult;

    /**
     * 老大屏地址
     */
    private String oldScreenUrl;

}
