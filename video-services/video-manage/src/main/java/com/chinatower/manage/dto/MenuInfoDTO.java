package com.chinatower.manage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 大屏关联菜单列表
 * @author peng.haibo
 * @date 2022/8/10
 */
@Data
public class MenuInfoDTO implements Serializable {

    private String name;

    private String viewId;

    private String dirId;

    private Integer sort;

    private Integer displayType;
}
