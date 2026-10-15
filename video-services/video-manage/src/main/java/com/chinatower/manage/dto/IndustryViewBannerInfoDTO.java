package com.chinatower.manage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 大屏关联banner信息
 * @author peng.haibo
 * @date 2022/8/10
 */
@Data
public class IndustryViewBannerInfoDTO implements Serializable {

    private String compoId;

    private String compoName;

    private String compoPath;
}
