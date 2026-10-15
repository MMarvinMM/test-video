package com.chinatower.manage.api.dto;

import com.chinatower.platform.entity.admin.SysIndustryVerConfDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysIndustryVerConfEdition extends SysIndustryVerConfDTO implements Serializable {

    private Long editionId;
    /** bannerId */
    private Long bannerId;

    /**
     * banner标题
     */
    private String bannerTitle;

    /** 受理节点 */
    private String acceptNode;

    /**
     * 大屏banner风格 wiseblue-睿智蓝 aquamarine-碧山绿 terracotta-赤土黄
     */
    private String bannerType;
    /**
     * 属地版本号id
     */
    private Long localizedVersionId;

    private List<Long> industryScreenIdList;

    private String compoPath;

    private String compoName;
}
