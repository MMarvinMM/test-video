package com.chinatower.system.api.model;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 大屏菜单配置表 screen_menu_conf
 *
 * @author liuxt
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenMenuConfVO extends BaseDataScopeEntity
{
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 分组id(前端生成)
     */
    private String groupId;


    /**
     * 大屏属性：1.菜单 2.分组
     */
    private String attr;

    /**
     * tab名称
     */
    private String tabName;


    /**
     * 大屏id
     */
    private String industryViewId;

    /**
     * 大屏类型（0-应用大屏 1-第三方大屏）
     */
    private Integer industryViewType;


    /**
     * 大屏类型 1-旧大屏 2-新大屏
     */
    private Integer viewType;

    /**
     * 大屏名称
     */
    private String industryViewName;


    /**
     * 大屏排序
     */
    private Integer sort;


    /**
     * 客户id
     */
    private String tenantId;


    /**
     * 行业编码
     */
    private String industryCode;



    /**
     * 行业版本编码
     */
    private String appVerCode;


    /**
     * 大屏列表
     */
    private List<ScreenMenuConfVO> industryViewList;




}
