package com.chinatower.platform.project.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysThirdMenuDTO implements Serializable {
    private String menuId;
    /**
     * 菜单名称
     */
    private String menuName;
    private String parentId;
    private Long orderNum;
    private String path;
    private String component;
    private String iframeUrl;
    /**
     * 菜单类型 菜单类型（M目录 C菜单 F按钮）
     */
    private String menuType;
    private String visible;
    private String perms;
    private String icon;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;
    private String ssoLoginFlag;
    private String menuDesc;
    private String status;
    private String appKey;
    private String secretKey;
    private Integer deleteFlag;

    private String customName;

}
