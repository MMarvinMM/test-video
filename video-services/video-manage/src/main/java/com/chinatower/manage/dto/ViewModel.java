package com.chinatower.manage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 行业大屏关联模型 ViewModel
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 16:29
 */
@Data
public class ViewModel implements Serializable {

    private static final long serialVersionUID = 2881663621257511782L;

    /**
     * banner信息
     */
    private IndustryViewBannerInfoDTO banner;

    /** 大屏关联菜单列表 */
    private List<MenuInfoDTO> menuList;
}
